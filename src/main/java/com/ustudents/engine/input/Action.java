package com.ustudents.engine.input;

import com.ustudents.engine.core.json.annotation.JsonSerializable;

import java.util.ArrayList;
import java.util.List;

@JsonSerializable
public class Action {

    @JsonSerializable
    public List<Mapping> mappings;

    public Action(){
        mappings = new ArrayList<>();
    }

    public void addMapping(Mapping mapping){
        if(mapping != null && mappings != null)
            mappings.add(mapping);
    }

    public boolean OneMappingIsSuccessful(){
        for(Mapping mapping: mappings){
            if(mapping != null && mapping.isMappingSuccessful()){
                return true;
            }
        }
        return false;
    }

    public int getFirstBindInMapping(){
        if (mappings != null && !mappings.isEmpty() && mappings.get(0) != null){
            return mappings.get(0).getUniqueKey();
        }
        return -1;
    }

    public String getFirstTypeOfBindInMapping(){
        if (mappings != null && !mappings.isEmpty() && mappings.get(0) != null){
            return mappings.get(0).typeOfBind;
        }
        return null;
    }

    public void removeFirstBindInMapping(){
        if (mappings != null && !mappings.isEmpty() && mappings.get(0) != null){
            mappings.get(0).removeUniqueKey();
        }
    }

    public void addFirstBindInMapping(int key, String typeOfList){
        if (mappings != null && !mappings.isEmpty() && mappings.get(0) != null){
            mappings.get(0).addUniqueKey(key, typeOfList);
        }
    }

    public boolean firstBindInMappingIsRemoved(){
        if (mappings != null && !mappings.isEmpty() && mappings.get(0) != null){
            return mappings.get(0).allListsAreEmpty();
        }
        return false;
    }

    public boolean bindNotAlreadyDefine(int key){
        for(Mapping mapping: mappings){
            if(mapping != null && !mapping.bindNotAlreadyDefine(key)){
                return false;
            }
        }
        return true;
    }
}
