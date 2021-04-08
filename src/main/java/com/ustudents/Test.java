package com.ustudents;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.Json;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.network.Server;

import java.util.HashMap;
import java.util.Map;

public class Test {
    @JsonSerializable
    public static class test {
        @JsonSerializable
        Map<String, Object> test = new HashMap<>();
    }

    public static void main(String[] args) {
        Out.start(args, true, true);

        test test = new test();
        test.test.put("qsd", 5);

        Map<String, Object> test2 = Json.serialize(test);

        Out.end();
    }
}
