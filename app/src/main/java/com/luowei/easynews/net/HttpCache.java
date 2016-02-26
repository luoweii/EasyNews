package com.luowei.easynews.net;

import android.os.StatFs;

import com.jakewharton.disklrucache.DiskLruCache;
import com.luowei.easynews.App;
import com.luowei.easynews.utils.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpCache {
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();

    private final int DISK_CACHE_INDEX = 0;
    private int diskCacheSize = 1024 * 1024 * 50;  // 50M
    private String diskCachePath;

    public HttpCache() {
        diskCachePath = App.context.getExternalCacheDir() + "/net";
    }

    public void initDiskCache() {
        // Set up disk cache
        synchronized (mDiskCacheLock) {
            if (diskCachePath != null && mDiskLruCache == null) {
                File diskCacheDir = new File(diskCachePath);
                if (diskCacheDir.exists() || diskCacheDir.mkdirs()) {
                    long availableSpace = getAvailableSpace(diskCacheDir);
                    long diskCacheSize = this.diskCacheSize;
                    diskCacheSize = availableSpace > diskCacheSize ? diskCacheSize : availableSpace;
                    try {
                        mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, diskCacheSize);
                        LogUtil.d("create disk cache success");
                    } catch (Throwable e) {
                        mDiskLruCache = null;
                        LogUtil.e("create disk cache error", e);
                    }
                }
            }
        }
    }

    public void put(String url, String result) {
        if (url == null || result == null) return;
        if (mDiskLruCache == null) initDiskCache();
        if (mDiskLruCache != null) {
            try {
                DiskLruCache.Editor editor = mDiskLruCache.edit(url);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
                    byte[] buffer = new byte[4096];
                    int len = 0;
                    BufferedInputStream bis = null;
                    bis = new BufferedInputStream(new ByteArrayInputStream(result.getBytes("UTF-8")));
                    BufferedOutputStream out = new BufferedOutputStream(outputStream);
                    while ((len = bis.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.flush();
                    out.close();
                    editor.commit();
                }
            } catch (Throwable e) {
                LogUtil.e(e.getMessage(), e);
            }
        }
    }

    public String get(String url) {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache == null) initDiskCache();
            if (mDiskLruCache != null) {
                DiskLruCache.Snapshot snapshot = null;
                try {
                    snapshot = mDiskLruCache.get(url);
                    if (snapshot != null) {
                        String result = getStringFromInputStream(snapshot.getInputStream(DISK_CACHE_INDEX));
                        return result;
                    }
                } catch (Throwable e) {
                    LogUtil.e(e.getMessage(), e);
                } finally {
                    closeQuietly(snapshot);
                }
            }
        }
        return null;
    }

    /**
     * 将InputStream转换成String
     *
     * @param in InputStream
     * @return String
     * @throws Exception
     */
    private String getStringFromInputStream(InputStream in) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int count = -1;
        while ((count = in.read(data, 0, 4096)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(), "UTF-8");
    }

    public void clear() {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
                try {
                    mDiskLruCache.delete();
                    mDiskLruCache.close();
                } catch (Throwable e) {
                    LogUtil.e(e.getMessage(), e);
                }
                mDiskLruCache = null;
            }
        }
        initDiskCache();
    }

    public String getDiskCachePath() {
        return diskCachePath;
    }

    public void setDiskCachePath(String diskCachePath) {
        this.diskCachePath = diskCachePath;
    }

    long getAvailableSpace(File dir) {
        try {
            final StatFs stats = new StatFs(dir.getPath());
            return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
        } catch (Throwable e) {
            LogUtil.e(e.getMessage(), e);
            return -1;
        }
    }

    void closeQuietly(/*Auto*/Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }
}
