package com.ustudents.engine.input;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.annotation.JsonSerializable;

import java.util.ArrayList;
import java.util.List;

@JsonSerializable
public class Mapping {

    @JsonSerializable
    public List<Integer> neededPressedAction;

    @JsonSerializable
    public List<Integer> neededReleasedAction;

    @JsonSerializable
    public List<Integer> neededDownAction;

    @JsonSerializable
    public List<Integer> neededUpAction;

    @JsonSerializable
    public String typeOfBind;

    public Mapping(){}

    public Mapping(String typeOfBind){
        neededPressedAction = new ArrayList<>();
        neededReleasedAction = new ArrayList<>();
        neededDownAction = new ArrayList<>();
        neededUpAction = new ArrayList<>();
        this.typeOfBind = typeOfBind;
    }

    public void bindPressedAction(int key){
        removeActionIfIsAlreadyDefine(key);
        neededPressedAction.add(key);
    }

    public void bindReleasedAction(int key){
        removeActionIfIsAlreadyDefine(key);
        neededReleasedAction.add(key);
    }

    public void bindDownAction(int key){
        removeActionIfIsAlreadyDefine(key);
        neededDownAction.add(key);
    }

    public void bindUpAction(int key){
        removeActionIfIsAlreadyDefine(key);
        neededUpAction.add(key);
    }

    private void removeActionIfIsAlreadyDefine(int key){
        if(neededPressedAction.contains(key))
            neededPressedAction.remove(key);
        if(neededReleasedAction.contains(key))
            neededReleasedAction.remove(key);
        if(neededDownAction.contains(key))
            neededDownAction.remove(key);
        if(neededUpAction.contains(key))
            neededUpAction.remove(key);
    }

    public boolean isMappingSuccessful(){
        for(int pressedAction: neededPressedAction){
            if(typeOfBind.equals("keyboard") && !Input.isKeyPressed(pressedAction))
                return false;
            else if(typeOfBind.equals("mouse") && !Input.isMousePressed(pressedAction))
                return false;
        }
        for(int releasedAction: neededReleasedAction){
            if(typeOfBind.equals("keyboard") && !Input.isKeyReleased(releasedAction))
                return false;
            else if(typeOfBind.equals("mouse") && !Input.isMouseRelease(releasedAction))
                return false;
        }
        for(int downAction:neededDownAction){
            if(typeOfBind.equals("keyboard") && !Input.isKeyDown(downAction))
                return false;
            else if(typeOfBind.equals("mouse") && !Input.isMouseDown(downAction))
                return false;
        }
        for(int upAction: neededUpAction){
            if(typeOfBind.equals("keyboard") && !Input.isKeyUp(upAction))
                return false;
            else if(typeOfBind.equals("mouse") && !Input.isMouseUp(upAction))
                return false;
        }

        return !allListsAreEmpty();
    }

    public int getUniqueKey(){
        if(!neededDownAction.isEmpty()){
            return neededDownAction.get(0);
        }
        if(!neededUpAction.isEmpty()){
            return neededUpAction.get(0);
        }
        if(!neededPressedAction.isEmpty()){
            return neededPressedAction.get(0);
        }
        if(!neededReleasedAction.isEmpty()){
            return neededReleasedAction.get(0);
        }
        return -1;
    }

    public void removeUniqueKey(){
        if(!neededDownAction.isEmpty()){
            neededDownAction.remove(0);
        }else if(!neededUpAction.isEmpty()){
            neededUpAction.remove(0);
        }else if(!neededPressedAction.isEmpty()){
            neededPressedAction.remove(0);
        }else if(!neededReleasedAction.isEmpty()){
            neededReleasedAction.remove(0);
        }
    }

    public void addUniqueKey(int key, String typeOfList){
        switch (typeOfList) {
            case "pressed":
                if (neededPressedAction.isEmpty())
                    neededPressedAction.add(key);
                else
                    neededPressedAction.set(0, key);
                break;
            case "released":
                if (neededReleasedAction.isEmpty())
                    neededReleasedAction.add(key);
                else
                    neededReleasedAction.set(0, key);
                break;
            case "down":
                if (neededDownAction.isEmpty())
                    neededDownAction.add(key);
                else
                    neededDownAction.set(0, key);
                break;
            case "up":
                if (neededUpAction.isEmpty())
                    neededUpAction.add(key);
                else
                    neededUpAction.set(0, key);
                break;
        }
    }

    public boolean bindNotAlreadyDefine(int key){
        for(int pressedKey: neededPressedAction){
            if(pressedKey == key)
                return false;
        }
        for(int releasedKey: neededReleasedAction){
            if(releasedKey == key)
                return false;
        }
        for(int downKey:neededDownAction){
            if(downKey == key)
                return false;
        }
        for(int upKey: neededUpAction){
            if(upKey == key)
                return false;
        }
        return true;
    }

    public boolean allListsAreEmpty(){
        return neededDownAction.isEmpty() && neededUpAction.isEmpty() &&
                neededReleasedAction.isEmpty() && neededPressedAction.isEmpty();
    }
}
