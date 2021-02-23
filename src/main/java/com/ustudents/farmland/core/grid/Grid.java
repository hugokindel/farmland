package com.ustudents.farmland.core.grid;

import com.ustudents.engine.core.json.annotation.JsonSerializable;

import java.util.List;

@JsonSerializable
public class Grid {
    @JsonSerializable
    List<List<Cell>> cells;
}
