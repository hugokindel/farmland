package com.ustudents.engine.core.json;

import com.ustudents.engine.core.json.annotation.JsonSerializable;

import java.lang.reflect.Field;
import java.lang.Class;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/** Contains utility functions to deserialize and serialize Json data format files. */
@SuppressWarnings({"unchecked", "unused"})
public class Json {
    /**
     * Deserialize a Json file to an object.
     *
     * @param filePath The file path to deserialize.
     * @param classType The Class to create.
     * @param <T> The type to return.
     *
     * @return the class of type T created.
     */
    public static <T> T deserialize(String filePath, Class<T> classType) {
        try {
            checkSerializable(classType);
            return deserialize(JsonReader.readMap(filePath), classType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Deserialize a Json map to an object.
     *
     * @param json The json map to deserialize.
     * @param classType The Class to create.
     * @param <T> The type to return.
     *
     * @return the class of type T created.
     */
    public static <T> T deserialize(Map<String, Object> json, Class<T> classType) {
        try {
            checkSerializable(classType);

            T object = classType.getConstructor().newInstance();
            ArrayList<Field> fields = new ArrayList<>(Arrays.asList(classType.getDeclaredFields()));
            Class<?> parent = classType.getSuperclass();
            while (parent != null && parent.isAnnotationPresent(JsonSerializable.class)) {
                fields.addAll(Arrays.asList(parent.getDeclaredFields()));
                parent = parent.getSuperclass();
            }
            fields.removeIf(field -> !field.isAnnotationPresent(JsonSerializable.class));

            for (Field field : fields) {
                JsonSerializable serializable = field.getAnnotation(JsonSerializable.class);
                String key = serializable.path().isEmpty() ? field.getName() : serializable.path();
                String[] path = key.split("\\.");
                Map<String, Object> mapToSearch = json;

                if (path.length > 1) {
                    for (int i = 0; i < path.length - 1; i++) {
                        if (mapToSearch.get(path[i]) instanceof Map) {
                            mapToSearch = (Map<String, Object>)mapToSearch.get(path[i]);
                        } else {
                            throw new Exception("Wrong path!");
                        }
                    }
                }

                field.setAccessible(true);

                if (!mapToSearch.containsKey(path[path.length - 1])) {
                    if (serializable.necessary()) {
                        throw new Exception("Missing key '" + key + "'!");
                    } else {
                        if (field.getType().equals(boolean.class)) {
                            field.set(object, false);
                        } else if (field.getType().equals(String.class)) {
                            field.set(object, "");
                        } else if (field.getType().equals(Integer.class)) {
                            field.set(object, 0);
                        } else if (field.getType().equals(Double.class)) {
                            field.set(object, 0.0);
                        } else {
                            field.set(object, null);
                        }
                        continue;
                    }
                }

                Object value = mapToSearch.get(path[path.length - 1]);

                if (field.getType().isAnnotationPresent(JsonSerializable.class)) {
                    field.set(object, deserialize((Map<String, Object>)value, field.getType()));
                    continue;
                } else if (field.getType().isAssignableFrom(List.class)) {
                    ParameterizedType parameterizedType = (ParameterizedType)field.getGenericType();
                    Type tType = parameterizedType.getActualTypeArguments()[0];

                    if (!tType.getTypeName().contains("<") &&
                            Class.forName(tType.getTypeName()).isAnnotationPresent(JsonSerializable.class)) {
                        List<Object> list = new ArrayList<>();

                        for (Object element : (List<Object>)value) {
                            list.add(deserialize((Map<String, Object>)element, Class.forName(tType.getTypeName())));
                        }

                        field.set(object, list);
                        continue;
                    }
                }

                field.set(object, mapToSearch.get(path[path.length - 1]));
            }

            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Serialize a Json map from an object.
     *
     * @param object The object to serialize.
     * @param <T> The type of the object.
     *
     * @return the map.
     */
    public static <T> Map<String, Object> serialize(T object) {
        try {
            Class<T> classType = (Class<T>) object.getClass();

            checkSerializable(classType);

            Map<String, Object> json = new LinkedHashMap<>();
            ArrayList<Field> fields = new ArrayList<>(Arrays.asList(classType.getDeclaredFields()));
            Class<?> parent = classType.getSuperclass();
            while (parent != null && parent.isAnnotationPresent(JsonSerializable.class)) {
                fields.addAll(Arrays.asList(parent.getDeclaredFields()));
                parent = parent.getSuperclass();
            }
            fields.removeIf(field -> !field.isAnnotationPresent(JsonSerializable.class));

            for (Field field : fields) {
                JsonSerializable serializable = field.getAnnotation(JsonSerializable.class);
                String key = serializable.path().isEmpty() ? field.getName() : serializable.path();
                String[] path = key.split("\\.");
                field.setAccessible(true);
                addToMap(json, path, field.get(object));
            }

            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Serialize a Json file from an object.
     *
     * @param filePath The file path to use.
     * @param object The object to serialize.
     * @param <T> The type of the object.
     */
    public static <T> void serialize(String filePath, T object) {
        try {
            JsonWriter.writeToFile(filePath, serialize(object));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if a class is serializable.
     *
     * @param classType The Java Class to use.
     * @param <T> The class's type.
     */
    public static <T> void checkSerializable(Class<T> classType) throws Exception {
        if (!classType.isAnnotationPresent(JsonSerializable.class)) {
            throw new Exception("Not a serializable class!");
        }
    }

    /**
     * Add an object to a map.
     *
     * @param map The map to insert in.
     * @param path The path to search in.
     * @param value The value to add.
     */
    private static void addToMap(Map<String, Object> map, String[] path, Object value) {
        if (path.length == 1) {
            /*if (value instanceof String) {
                map.put(path[0], StringUtil.getEscaped((String)value));
            } else if (value instanceof Character) {
                map.put(path[0], StringUtil.getEscaped(((Character)value).toString()).charAt(0));
            } else {
                map.put(path[0], value);
            }*/
            map.put(path[0], value);
            return;
        }

        if (!map.containsKey(path[0])) {
            addToMap(map, new String[] {path[0]}, new LinkedHashMap<String, Object>());
        }

        String[] newPath = new String[path.length - 1];
        System.arraycopy(path, 1, newPath, 0, path.length - 1);
        addToMap((Map<String, Object>) map.get(path[0]), newPath, value);
    }
}
