package com.ustudent.engine.core.json;

import com.ustudents.engine.core.json.JsonReader;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked"})
public class TestJsonReader {
    @Test
    public void testRead1() {
        Map<String, Object> json = JsonReader.readMap(getClass().getClassLoader().getResourceAsStream("reader-test1.json"));
        assertNotNull(json);
        assertEquals(json.get("int"), 57);
        assertEquals(json.get("double"), 85.5);
        assertEquals(json.get("false"), false);
        assertEquals(json.get("true"), true);
        assertNull(json.get("null"));

        List<Integer> array = (List<Integer>)json.get("array");
        assertEquals(array.get(0), 5);
        assertEquals(array.get(1), 7);
        assertEquals(array.get(2), 8);
        assertEquals(array.get(3), 9);

        List<List<Integer>> doubleArray = (List<List<Integer>>)json.get("double-array");
        List<Integer> doubleArray1 = doubleArray.get(0);
        assertEquals(doubleArray1.get(0), 5);
        assertEquals(doubleArray1.get(1), 7);
        assertEquals(doubleArray1.get(2), 2);
        List<Integer> doubleArray2 = doubleArray.get(1);
        assertEquals(doubleArray2.get(0), 8);
        assertEquals(doubleArray2.get(1), 9);
        assertEquals(doubleArray2.get(2), 1);
        List<Integer> doubleArray3 = doubleArray.get(2);
        assertEquals(doubleArray3.get(0), 1);
        assertEquals(doubleArray3.get(1), 2);
        assertEquals(doubleArray3.get(2), 7);

        Map<String, Object> map = (Map<String, Object>)json.get("map");
        assertEquals(map.get("string"), "te\rst");
        assertEquals(map.get("char"), '\n');
    }
}
