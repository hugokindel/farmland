package com.ustudent.engine.core.json;

import com.ustudents.engine.core.json.JsonWriter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings({"unchecked"})
public class TestJsonWriter {
    @Test
    public void testRead1() {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("int", 57);
        json.put("double", 85.5);
        json.put("false", false);
        json.put("true", true);
        json.put("null", null);
        json.put("array", Arrays.asList(5, 7, 8, 9));
        json.put("double-array", Arrays.asList(Arrays.asList(5, 7, 2), Arrays.asList(8, 9, 1), Arrays.asList(1, 2, 7)));
        Map<String, Object> json2 = new LinkedHashMap<>();
        json2.put("string", "te\rst");
        json2.put("char", '\n');
        json.put("map", json2);

        String jsonAsString = JsonWriter.writeToString(json);

        assertEquals(jsonAsString, "{\n" +
                "\t\"int\": 57,\n" +
                "\t\"double\": 85.5,\n" +
                "\t\"false\": false,\n" +
                "\t\"true\": true,\n" +
                "\t\"null\": null,\n" +
                "\t\"array\": [\n" +
                "\t\t5,\n" +
                "\t\t7,\n" +
                "\t\t8,\n" +
                "\t\t9\n" +
                "\t],\n" +
                "\t\"double-array\": [\n" +
                "\t\t[5, 7, 2],\n" +
                "\t\t[8, 9, 1],\n" +
                "\t\t[1, 2, 7]\n" +
                "\t],\n" +
                "\t\"map\": {\n" +
                "\t\t\"string\": \"te\\rst\",\n" +
                "\t\t\"char\": '\\n'\n" +
                "\t}\n" +
                "}");
    }
}
