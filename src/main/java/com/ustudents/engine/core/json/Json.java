package com.ustudents.engine.core.json;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.core.json.annotation.JsonSerializableType;
import org.joml.*;

import java.lang.reflect.Field;
import java.lang.Class;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import static com.ustudents.engine.utility.ReflectionUtil.*;

/** Contains utility functions to deserialize and serialize Json data format files. */
@SuppressWarnings({"unchecked", "unused"})
public class Json {
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

    public static <T> void serialize(String filePath, T object, String message) {
        try {
            JsonWriter.writeToFile(filePath, serialize(object), message);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            Class<T> type = (Class<T>) object.getClass();

            isSerializable(type);

            Map<String, Object> json = new LinkedHashMap<>();
            List<Field> fields = findSerializableFields(type);

            for (Field field : fields) {
                JsonSerializable serializable = field.getAnnotation(JsonSerializable.class);

                if (serializable.type() == JsonSerializableType.DeserializableOnly) {
                    continue;
                }

                field.setAccessible(true);

                String key = serializable.path().isEmpty() ? field.getName() : serializable.path();
                String[] path = key.split("\\.");

                if (field.getType().isAnnotationPresent(JsonSerializable.class)) {
                    if (field.get(object) == null) {
                        insertInMapAtSearchPath(json, path, null);
                    } else {
                        insertInMapAtSearchPath(json, path, serialize(field.get(object)));
                    }
                } else {
                    insertInMapAtSearchPath(json, path, field.get(object));
                }
            }

            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Adds an object to a map.
     *
     * @param map The map to insert in.
     * @param path The path to search in.
     * @param value The value to add.
     */
    private static void insertInMapAtSearchPath(Map<String, Object> map, String[] path, Object value) {
        if (path.length == 1) {
            map.put(path[0], value);
            return;
        }

        if (!map.containsKey(path[0])) {
            insertInMapAtSearchPath(map, new String[] {path[0]}, new LinkedHashMap<String, Object>());
        }

        String[] newPath = new String[path.length - 1];
        System.arraycopy(path, 1, newPath, 0, path.length - 1);
        insertInMapAtSearchPath((Map<String, Object>) map.get(path[0]), newPath, value);
    }

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
            isSerializable(classType);
            return deserialize(JsonReader.readMap(filePath), classType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T deserialize(Map<String, Object> json, Class<T> classType) {
        return deserialize(json, classType, null, null, null);
    }

    /**
     * Deserialize a Json map to an object.
     *
     * @param json The json map to deserialize.
     * @param type The Class to create.
     * @param <T> The type to return.
     *
     * @return the class of type T created.
     */
    public static <T ,U> T deserialize(Map<String, Object> json, Class<T> type, Class<U> declaringClass,
                                       String declaringFieldName, TypeGraph remainingTypeGraph) {
        try {
            isSerializable(type);

            T object = type.getConstructor().newInstance();
            List<Field> fields = findSerializableFields(type);

            for (Field field : fields) {
                JsonSerializable annotation = field.getAnnotation(JsonSerializable.class);

                if (annotation.type() == JsonSerializableType.SerializableOnly) {
                    continue;
                }

                field.setAccessible(true);

                String key = annotation.path().isEmpty() ? field.getName() : annotation.path();
                String[] path = key.split("\\.");
                Map<String, Object> search = json;

                if (path.length > 1) {
                    for (int i = 0; i < path.length - 1; i++) {
                        if (search.get(path[i]) instanceof Map) {
                            search = (Map<String, Object>)search.get(path[i]);
                        } else {
                            throw new Exception("Wrong path!");
                        }
                    }
                }

                if (!search.containsKey(path[path.length - 1])) {
                    if (annotation.necessary()) {
                        throw new Exception("Missing key '" + key + "'!");
                    } else {
                        setDefaultValue(field, object);
                        continue;
                    }
                }

                Object value = search.get(path[path.length - 1]);

                if (value == null) {
                    field.set(object, null);
                    continue;
                } else if (field.getType().isAnnotationPresent(JsonSerializable.class)) {
                    field.set(object, deserialize((Map<String, Object>)value, field.getType(),
                            field.getDeclaringClass(), field.getName(), null));
                    continue;
                }  else if (field.getType().getName().startsWith("java.util.Map")) {
                    Object obj = tryDeserializeMap(field, value, object, field.getDeclaringClass(), field.getName(), remainingTypeGraph);
                    field.set(object, obj);
                    continue;
                } else if (field.getType().getName().startsWith("java.util.List")) {
                    Object obj = tryDeserializeList(field, value, object, field.getDeclaringClass(), field.getName(), remainingTypeGraph);
                    field.set(object, obj);
                    continue;
                }

                if (isGeneric(Objects.requireNonNull(findFieldInClass(field.getName(), field.getDeclaringClass())).getGenericType())) {
                    field.set(object, set(field, value, object, declaringClass, declaringFieldName, remainingTypeGraph));
                } else {
                    field.set(object, set(field, value, object, null, field.getName(), remainingTypeGraph));
                }
            }

            invokeSerializableConstructor(type, object, json);

            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> Object tryDeserializeList(Field field, Object value, Object object, Class<T> declaringClass, String declaringFieldName, TypeGraph remainingTypeGraph)
            throws Exception {
        Type genericType = Objects.requireNonNull(findFieldInClass(declaringFieldName, declaringClass))
                .getGenericType();
        TypeGraph completeTypeGraph = remainingTypeGraph == null || remainingTypeGraph.children.isEmpty() ? generateTypeGraph(genericType.getTypeName()) : remainingTypeGraph;
        TypeGraph typeGraph = completeTypeGraph.children.get(0);

        List<Object> list = new ArrayList<>();

        if (!typeGraph.children.isEmpty()) {
            for (Object element : (List<Object>) value) {
                list.add(set(field, element, object, declaringClass, declaringFieldName, typeGraph));
            }
        } else {
            for (Object element : (List<Object>) value) {
                list.add(set(field, element, object, declaringClass, declaringFieldName, typeGraph));
            }
        }

        return list;
    }

    public static <T> Object tryDeserializeMap(Field field, Object value, Object object, Class<T> declaringClass, String declaringFieldName, TypeGraph remainingTypeGraph)
            throws Exception {
        Type genericType = Objects.requireNonNull(findFieldInClass(declaringFieldName, declaringClass))
                .getGenericType();
        TypeGraph completeTypeGraph = remainingTypeGraph == null || remainingTypeGraph.children.isEmpty() ? generateTypeGraph(genericType.getTypeName()) : remainingTypeGraph;
        TypeGraph typeGraph = completeTypeGraph.children.get(1);

        Map<String, Object> map = new LinkedHashMap<>();

        if (!typeGraph.children.isEmpty()) {
            for (Map.Entry<String, Object> element : ((Map<String, Object>)value).entrySet()) {
                map.put(element.getKey(), set(field, element.getValue(), object, declaringClass, declaringFieldName, typeGraph));
            }
        } else {
            for (Map.Entry<String, Object> element : ((Map<String, Object>)value).entrySet()) {
                map.put(element.getKey(), set(field, element.getValue(), object, declaringClass, declaringFieldName, typeGraph));
            }
        }

        return map;
    }

    /**
     * Check if a class is serializable.
     *
     * @param type The Java Class to use.
     * @param <T> The class's type.
     */
    private static <T> void isSerializable(Class<T> type) throws Exception {
        boolean isSerializable = false;

        Class<?> currentType = type;
        while (currentType != null) {
            if (currentType.isAnnotationPresent(JsonSerializable.class)) {
                isSerializable = true;
                break;
            }

            currentType = currentType.getSuperclass();
        }

        if (!isSerializable) {
            throw new Exception("Not a serializable class!");
        }
    }

    private static <T> List<Field> findSerializableFields(Class<T> type) {
        ArrayList<Field> fields = new ArrayList<>();

        Class<?> currentType = type;
        while (currentType != null) {
            if (currentType.isAnnotationPresent(JsonSerializable.class)) {
                fields.addAll(Arrays.asList(currentType.getDeclaredFields()));
            }

            currentType = currentType.getSuperclass();
        }

        fields.removeIf(field -> !field.isAnnotationPresent(JsonSerializable.class));

        return fields;
    }

    private static void setDefaultValue(Field field, Object object) throws Exception {
        Type type = field.getType();

        if (type.equals(Boolean.class)) {
            field.set(object, false);
        } else if (type.equals(String.class)) {
            field.set(object, "");
        } else if (type.equals(Character.class)) {
            field.set(object, '\u0000');
        } else if (type.equals(Integer.class)) {
            field.set(object, 0);
        } else if (type.equals(Long.class)) {
            field.set(object, 0L);
        } else if (type.equals(Double.class)) {
            field.set(object, 0.0);
        } else if (type.equals(Float.class)) {
            field.set(object, 0.0f);
        } else if (type.equals(Vector2f.class)) {
            field.set(object, new Vector2f());
        } else if (type.equals(Vector3f.class)) {
            field.set(object, new Vector3f());
        } else if (type.equals(Vector4f.class)) {
            field.set(object, new Vector4f());
        } else if (type.equals(Vector2i.class)) {
            field.set(object, new Vector2i());
        } else if (type.equals(Vector3i.class)) {
            field.set(object, new Vector3i());
        } else if (type.equals(Vector4i.class)) {
            field.set(object, new Vector4i());
        } else if (type.equals(Matrix4f.class)) {
            field.set(object, new Matrix4f());
        } else {
            field.set(object, null);
        }
    }

    /**
     * Converts (if necessary) a value of field's type before adding it to it.
     *
     * @param field The field.
     * @param value The value.
     * @param object The instance (object) of the field.
     */
    private static <T> Object set(Field field, Object value, Object object, Class<T> declaringClass,
                                String declaringFieldName, TypeGraph remainingTypeGraph) throws Exception {
        Class<?> type;

        if (declaringClass == null) {
            type = field.getType();
        } else {
            Type genericType = Objects.requireNonNull(findFieldInClass(declaringFieldName, declaringClass))
                    .getGenericType();
            TypeGraph completeTypeGraph = remainingTypeGraph == null ? generateTypeGraph(genericType.getTypeName()) : remainingTypeGraph;
            TypeGraph typeGraph = searchType(field, genericType, completeTypeGraph);

            if (completeTypeGraph.children.isEmpty() && Class.forName(completeTypeGraph.name).isAnnotationPresent(JsonSerializable.class)) {
                if (value instanceof Map) {
                    return deserialize((Map<String, Object>)value, Class.forName(completeTypeGraph.name), field.getDeclaringClass(), field.getName(), completeTypeGraph);
                } else {
                    type = Class.forName(completeTypeGraph.name);
                }
            } else if (typeGraph != null) {
                if (!typeGraph.children.isEmpty() && !typeGraph.name.startsWith("java.util.Map") && value instanceof Map) {
                    return deserialize((Map<String, Object>)value, Class.forName(typeGraph.name), field.getDeclaringClass(), field.getName(), typeGraph);
                } else {
                    type = Class.forName(typeGraph.name);
                }
            } else if (completeTypeGraph.children.size() == 0) {
                type = Class.forName(completeTypeGraph.name);
            } else if (completeTypeGraph.name.equals("java.util.List")) {
                return tryDeserializeList(field, value, object, field.getDeclaringClass(), field.getName(), completeTypeGraph);
            } else if (completeTypeGraph.name.equals("java.util.Map")) {
                return tryDeserializeMap(field, value, object, field.getDeclaringClass(), field.getName(), completeTypeGraph);
            } else {
                return deserialize((Map<String, Object>)value, Class.forName(completeTypeGraph.name), field.getDeclaringClass(), field.getName(), completeTypeGraph);
            }
        }

        if (value == null) {
            return null;
        } else if (type == Float.class) {
            if (value instanceof Long) {
                return ((Long) value).floatValue();
            } else if (value instanceof Integer) {
                return ((Integer) value).floatValue();
            } else if (value instanceof Double) {
                return ((Double)value).floatValue();
            }

            return value;
        } else if (type == Integer.class) {
            if (value instanceof Long) {
                return ((Long)value).intValue();
            } else if (value instanceof Double) {
                return ((Double) value).intValue();
            } else if (value instanceof Float) {
                return ((Float) value).intValue();
            }

            return value;
        } else if (type == Long.class) {
            if (value instanceof Integer) {
                return ((Integer) value).longValue();
            } else if (value instanceof Double) {
                return ((Double) value).longValue();
            } else if (value instanceof Float) {
                return ((Float) value).longValue();
            }

            return value;
        } else if (type == Double.class) {
            if (value instanceof Long) {
                return ((Long) value).doubleValue();
            } else if (value instanceof Integer) {
                return ((Integer) value).doubleValue();
            } else if (value instanceof Float) {
                return ((Float) value).doubleValue();
            }

            return value;
        } else if (type == Vector2f.class) {
            Map<String, Object> map = (Map<String, Object>)value;

            if (map.get("x") instanceof Long) {
                map.put("x", ((Long) map.get("x")).doubleValue());
            }
            if (map.get("y") instanceof Long) {
                map.put("y", ((Long) map.get("y")).doubleValue());
            }

            return new Vector2f(
                    ((Double)map.get("x")).floatValue(),
                    ((Double)map.get("y")).floatValue());
        } else if (type == Vector3f.class) {
            Map<String, Object> map = (Map<String, Object>)value;

            if (map.get("x") instanceof Long) {
                map.put("x", ((Long) map.get("x")).doubleValue());
            }
            if (map.get("y") instanceof Long) {
                map.put("y", ((Long) map.get("y")).doubleValue());
            }
            if (map.get("z") instanceof Long) {
                map.put("z", ((Long) map.get("z")).doubleValue());
            }

            return new Vector3f(
                    ((Double)map.get("x")).floatValue(),
                    ((Double)map.get("y")).floatValue(),
                    ((Double)map.get("z")).floatValue());
        } else if (type == Vector4f.class) {
            Map<String, Object> map = (Map<String, Object>)value;

            if (map.get("x") instanceof Long) {
                map.put("x", ((Long) map.get("x")).doubleValue());
            }
            if (map.get("y") instanceof Long) {
                map.put("y", ((Long) map.get("y")).doubleValue());
            }
            if (map.get("z") instanceof Long) {
                map.put("z", ((Long) map.get("z")).doubleValue());
            }
            if (map.get("w") instanceof Long) {
                map.put("w", ((Long) map.get("w")).doubleValue());
            }

            return new Vector4f(
                    ((Double)map.get("x")).floatValue(),
                    ((Double)map.get("y")).floatValue(),
                    ((Double)map.get("z")).floatValue(),
                    ((Double)map.get("w")).floatValue());
        } else if (type == Vector2i.class) {
            Map<String, Object> map = (Map<String, Object>)value;

            if (map.get("x") instanceof Double) {
                map.put("x", ((Double) map.get("x")).longValue());
            }
            if (map.get("y") instanceof Double) {
                map.put("y", ((Double) map.get("y")).longValue());
            }

            return new Vector2i(
                    ((Long)map.get("x")).intValue(),
                    ((Long)map.get("y")).intValue());
        } else if (type == Vector3i.class) {
            Map<String, Object> map = (Map<String, Object>)value;

            if (map.get("x") instanceof Double) {
                map.put("x", ((Double) map.get("x")).longValue());
            }
            if (map.get("y") instanceof Double) {
                map.put("y", ((Double) map.get("y")).longValue());
            }
            if (map.get("z") instanceof Double) {
                map.put("z", ((Double) map.get("z")).longValue());
            }

            return new Vector3i(
                    ((Long)map.get("x")).intValue(),
                    ((Long)map.get("y")).intValue(),
                    ((Long)map.get("z")).intValue());
        } else if (type == Vector4i.class) {
            Map<String, Object> map = (Map<String, Object>)value;

            if (map.get("x") instanceof Double) {
                map.put("x", ((Double) map.get("x")).longValue());
            }
            if (map.get("y") instanceof Double) {
                map.put("y", ((Double) map.get("y")).longValue());
            }
            if (map.get("z") instanceof Double) {
                map.put("z", ((Double) map.get("z")).longValue());
            }
            if (map.get("w") instanceof Double) {
                map.put("w", ((Double) map.get("w")).longValue());
            }

            return new Vector4i(
                    ((Long)map.get("x")).intValue(),
                    ((Long)map.get("y")).intValue(),
                    ((Long)map.get("z")).intValue(),
                    ((Long)map.get("w")).intValue());
        }/* else if (type == Matrix4f.class) {
            Map<String, Object> map = (Map<String, Object>)value;
            return new Matrix4f(
                    ((Double)map.get("m00")).floatValue(),
                    ((Double)map.get("m01")).floatValue(),
                    ((Double)map.get("m02")).floatValue(),
                    ((Double)map.get("m03")).floatValue(),
                    ((Double)map.get("m10")).floatValue(),
                    ((Double)map.get("m11")).floatValue(),
                    ((Double)map.get("m12")).floatValue(),
                    ((Double)map.get("m13")).floatValue(),
                    ((Double)map.get("m20")).floatValue(),
                    ((Double)map.get("m21")).floatValue(),
                    ((Double)map.get("m22")).floatValue(),
                    ((Double)map.get("m23")).floatValue(),
                    ((Double)map.get("m30")).floatValue(),
                    ((Double)map.get("m31")).floatValue(),
                    ((Double)map.get("m32")).floatValue(),
                    ((Double)map.get("m33")).floatValue());
        }*/ else {
            return value;
        }
    }

    private static <T> void invokeSerializableConstructor(Class<T> type, Object object, Map<String, Object> json)
            throws Exception {
        for (Method method : type.getMethods()) {
            if (method.isAnnotationPresent(JsonSerializableConstructor.class)) {
                if (method.getParameterTypes().length == 0) {
                    method.invoke(object);
                } else {
                    method.invoke(object, json);
                }
            }
        }
    }
}
