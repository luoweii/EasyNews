package com.luowei.easynews.entity;

import java.io.Serializable;
import java.lang.reflect.Field;

public class BaseEntity implements Serializable {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("-------------------------------\n");
        try {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields) {
                String name = field.getName();
                if (!name.startsWith("this")) {
                    sb.append(field.getName() + "  :  " + field.get(this) + "\n");
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}