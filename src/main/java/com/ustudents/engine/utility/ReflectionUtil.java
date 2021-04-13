package com.ustudents.engine.utility;

import com.ustudents.engine.core.cli.print.In;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReflectionUtil {
    public static class TypeGraph {
        public String name;
        public List<TypeGraph> children = new ArrayList<>();

        public TypeGraph() {

        }

        public TypeGraph(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "TypeGraph{" +
                    "name='" + name + '\'' +
                    ", children=" + children +
                    '}';
        }
    }

    public static Type getActualTypeFromGenericType(Field field, int index) {
        return ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[index];
    }

    public static boolean isGeneric(Type type) {
        return !type.getTypeName().contains(".");
    }

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

    public static TypeGraph searchType(Field field, Type genericTypes, TypeGraph typeGraph) {
        String signature = getSignature(field);

        if (!typeGraph.children.isEmpty() && field.getType() == Object.class && signature != null && (genericTypes instanceof ParameterizedType || !genericTypes.getTypeName().contains("."))) {
            for (int i = 0; i < field.getDeclaringClass().getTypeParameters().length; i++) {
                if (signature.equals(field.getDeclaringClass().getTypeParameters()[i].getName())) {
                    return typeGraph.children.get(i);
                }
            }
        }

        return null;
    }

    public static TypeGraph generateTypeGraph(String name) {
        return generateTypeGraph(StringUtil.removeWhitespaces(name), 0).getObject1().get(0);
    }

    public static Pair<List<TypeGraph>, Integer> generateTypeGraph(String name, int index) {
        if (name.contains("<") || name.contains(",") || name.contains(">")) {
            StringBuilder currentType = new StringBuilder();
            List<TypeGraph> typeGraphs = new ArrayList<>();

            for (int i = index; i < name.length(); i++) {
                char c = name.charAt(i);

                if (c == '<') {
                    TypeGraph typeGraph = new TypeGraph();
                    typeGraph.name = currentType.toString();
                    currentType = new StringBuilder();
                    Pair<List<TypeGraph>, Integer> values = generateTypeGraph(name, i + 1);
                    typeGraph.children = values.getObject1();
                    i = values.getObject2();
                    typeGraphs.add(typeGraph);
                } else if (c == ',' && !currentType.toString().isEmpty()) {
                    TypeGraph typeGraph = new TypeGraph();
                    typeGraph.name = currentType.toString();
                    currentType = new StringBuilder();
                    typeGraph.children = new ArrayList<>();
                    typeGraphs.add(typeGraph);
                } else if (c == '>') {
                    if (!currentType.toString().isEmpty()) {
                        TypeGraph typeGraph = new TypeGraph();
                        typeGraph.name = currentType.toString();
                        currentType = new StringBuilder();
                        typeGraph.children = new ArrayList<>();
                        typeGraphs.add(typeGraph);
                    }
                    return new Pair<>(typeGraphs, i);
                } else if (c != ',') {
                    currentType.append(c);
                }
            }

            return new Pair<>(typeGraphs, name.length());
        } else {
            return new Pair<>(new ArrayList<>(Collections.singletonList(new TypeGraph(name))), name.length());
        }
    }
}
