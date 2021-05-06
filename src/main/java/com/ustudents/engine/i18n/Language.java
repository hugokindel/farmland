package com.ustudents.engine.i18n;

import com.ustudents.engine.core.json.annotation.JsonSerializable;

import java.util.Map;

@JsonSerializable
public class Language {
    @JsonSerializable
    public Map<String, String> content;
}
