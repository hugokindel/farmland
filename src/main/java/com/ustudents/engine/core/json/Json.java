package com.ustudents.engine.core.json;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.cli.print.style.Style;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.core.json.annotation.JsonSerializableType;
import com.ustudents.engine.scene.ecs.Component;
import com.ustudents.engine.scene.ecs.Entity;
import com.ustudents.engine.scene.ecs.Registry;
import com.ustudents.engine.scene.Scene;
import org.joml.*;

import java.lang.reflect.Field;
import java.lang.Class;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.Collator;
import java.util.*;

import static com.ustudents.engine.utility.ReflectionUtil.findFieldInClass;
import static com.ustudents.engine.utility.ReflectionUtil.searchSignature;

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

    public static <T> T deserialize(Map<String, Object> json, Class<T> classType) {
        return deserialize(json, classType, null, null);
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
    public static <T ,U> T deserialize(Map<String, Object> json, Class<T> classType, Class<U> declaringClass, String declaringFieldName) {
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

                if (serializable.type() == JsonSerializableType.SerializableOnly) {
                    continue;
                }

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
                        if (field.getType().equals(Boolean.class)) {
                            field.set(object, false);
                        } else if (field.getType().equals(String.class)) {
                            field.set(object, "");
                        } else if (field.getType().equals(Integer.class)) {
                            field.set(object, 0);
                        } else if (field.getType().equals(Double.class)) {
                            field.set(object, 0.0);
                        } else if (field.getType().equals(Float.class)) {
                            field.set(object, 0.0f);
                        } else if (field.getType().equals(Vector2f.class)) {
                            field.set(object, new Vector2f());
                        } else if (field.getType().equals(Vector3f.class)) {
                            field.set(object, new Vector3f());
                        } else if (field.getType().equals(Vector4f.class)) {
                            field.set(object, new Vector4f());
                        } else if (field.getType().equals(Vector2i.class)) {
                            field.set(object, new Vector2i());
                        } else if (field.getType().equals(Vector3i.class)) {
                            field.set(object, new Vector3i());
                        } else if (field.getType().equals(Vector4i.class)) {
                            field.set(object, new Vector4i());
                        } else {
                            field.set(object, null);
                        }
                        continue;
                    }
                }

                Object value = mapToSearch.get(path[path.length - 1]);

                if (field.getType().isAnnotationPresent(JsonSerializable.class)) {
                    if (value == null) {
                        field.set(object, null);
                    } else {
                        field.set(object, deserialize((Map<String, Object>)value, field.getType(), field.getDeclaringClass(), field.getName()));
                    }
                    continue;
                } else if (field.getType().getName().startsWith("java.util.Map")) {
                    if (mapExtraction(field, mapToSearch.get(path[path.length - 1]), object, declaringClass)) {
                        continue;
                    }
                } else if (field.getType().getName().startsWith("java.util.List")) {
                    if (listExtraction(field, value, object, declaringClass)) {
                        continue;
                    }
                }

                convertAndAdd(field, value, object, declaringClass, declaringFieldName);
            }

            for (Method method : classType.getMethods()) {
                if (method.isAnnotationPresent(JsonSerializableConstructor.class)) {
                    if (method.getParameterTypes().length == 0) {
                        method.invoke(object);
                    } else {
                        method.invoke(object, json);
                    }
                }
            }

            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> boolean listExtraction(Field field, Object value, Object object, Class<T> declaringClass) {
       try {
           ParameterizedType parameterizedType = (ParameterizedType)field.getGenericType();
           Type tType = parameterizedType.getActualTypeArguments()[0];

           if (!tType.getTypeName().contains("<") &&
                   Class.forName(tType.getTypeName()).isAnnotationPresent(JsonSerializable.class)) {
               List<Object> list = new ArrayList<>();

               for (Object element : (List<Object>) value) {
                   if (element == null) {
                       list.add(null);
                   } else {
                       if (element.getClass().isAnnotationPresent(JsonSerializable.class)) {
                           list.add(element);
                       } else {
                           list.add(deserialize((Map<String, Object>)element, Class.forName(tType.getTypeName()), field.getDeclaringClass(), field.getName()));
                       }
                   }
               }

               field.set(object, list);
               return true;
           } else if (tType.getTypeName().contains("<") && tType.getTypeName().startsWith("java.util.List") &&
                   Class.forName(getBetweenFirstAndLast(tType.getTypeName(), '<', '>')).isAnnotationPresent(JsonSerializable.class)) {
               List<Object> list = new ArrayList<>();

               int i = 0;
               for (Object element : (List<Object>) value) {
                   list.add(new ArrayList<>());

                   for (Object realElement : (List<Object>)element) {
                       if (realElement == null) {
                           ((List<Object>)list.get(i)).add(null);
                       } else {
                           if (realElement.getClass().isAnnotationPresent(JsonSerializable.class)) {
                               list.add(realElement);
                           } else {
                               ((List<Object>)list.get(i)).add(deserialize((Map<String, Object>) realElement,
                                       Class.forName(getBetweenFirstAndLast(tType.getTypeName(), '<', '>')),
                                       field.getDeclaringClass(), field.getName()));
                           }
                       }
                   }
                   i++;
               }

               field.set(object, list);
               return true;
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       return false;
    }

    public static <T> boolean mapExtraction(Field field, Object value, Object object, Class<T> declaringClass) {
        ParameterizedType parameterizedType = (ParameterizedType)field.getGenericType();
        Type tType = parameterizedType.getActualTypeArguments()[1];

        Map<String, Object> returnMap = new LinkedHashMap<>();

        try {
            Class type = Class.forName(tType.getTypeName());
            Map<String, Object> realMap = (Map<String, Object>)value;
            if (realMap.isEmpty()) {
                field.set(object, new LinkedHashMap<>());
                return true;
            } else if (type.isAnnotationPresent(JsonSerializable.class)) {
                for (Map.Entry<String, Object> entry : realMap.entrySet()) {
                    returnMap.put(entry.getKey(), deserialize((Map<String, Object>)entry.getValue(), type,
                            field.getDeclaringClass(), field.getName()));
                }
                field.set(object, returnMap);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getBetweenFirstAndLast(String text, char first, char last) {
        int firstSep = text.indexOf(first);
        int lastSep = text.lastIndexOf(last);
        return text.substring(firstSep + 1, lastSep);
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

                if (serializable.type() == JsonSerializableType.DeserializableOnly) {
                    continue;
                }

                String key = serializable.path().isEmpty() ? field.getName() : serializable.path();
                String[] path = key.split("\\.");
                field.setAccessible(true);

                if (field.getType().isAnnotationPresent(JsonSerializable.class)) {
                    if (field.get(object) == null) {
                        addToMap(json, path, null);
                    } else {
                        addToMap(json, path, serialize(field.get(object)));
                    }
                } else {
                    addToMap(json, path, field.get(object));
                }
            }

            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T extends Scene> Map<String, Object> serializeScene(T object) {
        try {
            Class<T> classType = (Class<T>) object.getClass();
            Registry registry = object.getRegistry();

            if (!classType.isAnnotationPresent(JsonSerializable.class)) {
                Out.printlnWarning("A scene can be serialized without the " + Style.Bold + "@JsonSerializable" + Style.Reset + " annotation, but it should still be defined!");
            }

            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", object.getClass().getName());
            List<Map<String, Object>> entitiesArray = new ArrayList<>();

            for (Map.Entry<Integer, Entity> entitySet : registry.getEntities().entrySet()) {
                Map<String, Object> entityMap = new LinkedHashMap<>();
                Entity entity = entitySet.getValue();

                entityMap.put("id", entity.getId());

                if (entity.hasName()) {
                    entityMap.put("name", entity.getName());
                }

                List<String> tags = new ArrayList<>(entity.getTags());
                tags.sort(Collator.getInstance());

                if (entity.hasParent()) {
                    entityMap.put("parent", entity.getParent().getId());
                }

                entityMap.put("enabled", entity.isEnabled());

                if (!tags.isEmpty()) {
                    entityMap.put("tags", tags);
                }

                List<Map<String, Object>> componentsMap = new ArrayList<>();

                for (Component component : entity.getComponents()) {
                    Map<String, Object> componentMap = new LinkedHashMap<>();

                    componentMap.put("type", component.getClass().getName());
                    componentMap.put("data", serialize(component));

                    componentsMap.add(componentMap);
                }

                if (!componentsMap.isEmpty()) {
                    entityMap.put("components", componentsMap);
                }

                entitiesArray.add(entityMap);
            }

            json.put("entities", entitiesArray);

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
            if (object instanceof Scene) {
                JsonWriter.writeToFile(filePath, serializeScene((Scene)object));
            } else {
                JsonWriter.writeToFile(filePath, serialize(object));
            }
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
        boolean isSerializable = false;
        Class<?> type = classType;

        while (type != null) {
            if (type.isAnnotationPresent(JsonSerializable.class)) {
                isSerializable = true;
                break;
            }

            type = classType.getSuperclass();
        }

        if (!isSerializable) {
            throw new Exception("Not a serializable class!");
        }
    }

    // TODO: Minify
    public static String minify(String json) {
        // FIXME: Does not handle between strings ""
        //return json.replace("\n", "").replace("\t", "").replace(" ", "");
        return json;
    }

    /**
     * Adds an object to a map.
     *
     * @param map The map to insert in.
     * @param path The path to search in.
     * @param value The value to add.
     */
    private static void addToMap(Map<String, Object> map, String[] path, Object value) {
        if (path.length == 1) {
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

    /**
     * Converts (if necessary) a value of field's type before adding it to it.
     *
     * @param field The field.
     * @param value The value.
     * @param object The instance (object) of the field.
     */
    private static <T> void convertAndAdd(Field field, Object value, Object object, Class<T> declaringClass, String declaringFieldName) {
        try {
            Class<?> type;

            if (declaringClass == null) {
                type = field.getType();
            } else {
                Type genericType = Objects.requireNonNull(findFieldInClass(declaringFieldName, declaringClass)).getGenericType();
                Type realType = searchSignature(field, genericType);

                if (realType != null) {
                    type = Class.forName(realType.getTypeName());
                } else {
                    type = field.getType();
                }
            }

            if (value == null) {
                field.set(object, null);
            } else  if (type == Float.class) {
                field.set(object, ((Double)value).floatValue());
            } else  if (type == Integer.class) {
                field.set(object, ((Long)value).intValue());
            } else if (type == Vector2f.class) {
                Map<String, Object> map = (Map<String, Object>)value;
                field.set(object, new Vector2f(
                        ((Double)map.get("x")).floatValue(),
                        ((Double)map.get("y")).floatValue())
                );
            } else if (type == Vector3f.class) {
                Map<String, Object> map = (Map<String, Object>)value;
                field.set(object, new Vector3f(
                        ((Double)map.get("x")).floatValue(),
                        ((Double)map.get("y")).floatValue(),
                        ((Double)map.get("z")).floatValue())
                );
            } else if (type == Vector4f.class) {
                Map<String, Object> map = (Map<String, Object>)value;
                field.set(object, new Vector4f(
                        ((Double)map.get("x")).floatValue(),
                        ((Double)map.get("y")).floatValue(),
                        ((Double)map.get("z")).floatValue(),
                        ((Double)map.get("w")).floatValue())
                );
            } else if (type == Vector2i.class) {
                Map<String, Object> map = (Map<String, Object>)value;
                field.set(object, new Vector2i(
                        ((Integer)map.get("x")),
                        ((Integer)map.get("y")))
                );
            } else if (type == Vector3i.class) {
                Map<String, Object> map = (Map<String, Object>)value;
                field.set(object, new Vector3i(
                        ((Integer)map.get("x")),
                        ((Integer)map.get("y")),
                        ((Integer)map.get("z")))
                );
            } else if (type == Vector4i.class) {
                Map<String, Object> map = (Map<String, Object>)value;
                field.set(object, new Vector4i(
                        ((Integer)map.get("x")),
                        ((Integer)map.get("y")),
                        ((Integer)map.get("z")),
                        ((Integer)map.get("w")))
                );
            } else if (type == Matrix4f.class) {
                Map<String, Object> map = (Map<String, Object>)value;
                field.set(object, new Matrix4f(
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
                        ((Double)map.get("m33")).floatValue())
                );
            } else {
                field.set(object, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
