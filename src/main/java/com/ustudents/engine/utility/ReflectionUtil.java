package com.ustudents.engine.utility;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectionUtil {
    public static <T> Field findFieldInClass(String fieldName, Class<T> type) {
        for (Field classField : type.getDeclaredFields()) {
            if (fieldName.equals(classField.getName())) {
                return classField;
            }
        }

        return null;
    }

    public static String getSignature(Field field) {
        try {
            return field.getGenericType().getTypeName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Type searchSignature(Field field, Type genericTypes) {
        String signature = getSignature(field);

        if (field.getType() == Object.class && signature != null && genericTypes instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)genericTypes;

            for (int i = 0; i < field.getDeclaringClass().getTypeParameters().length; i++) {
                if (signature.equals(field.getDeclaringClass().getTypeParameters()[i].getName())) {
                    return parameterizedType.getActualTypeArguments()[i];
                }
            }
        }

        return null;
    }
}
