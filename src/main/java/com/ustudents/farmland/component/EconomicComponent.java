package com.ustudents.farmland.component;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.item.Animal;
import com.ustudents.farmland.core.item.Crop;
import com.ustudents.farmland.core.item.Item;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class EconomicComponent {

    private boolean appearsInList;

    private int[] makeListOfTurn(List<List<Item>> listPerTurn){
        int[] turnTab = new int[listPerTurn.size()];
        for(int i = 0; i < turnTab.length; i++){
            turnTab[i] = i;
        }
        return turnTab;
    }

    private int countWhereItemAppearsInList(List<List<Item>> listPerTurn, int[] turnTab, Item item){
        int count = 1;
        appearsInList = false;
        for(int i = turnTab.length-1; i>=0; i--){
            if(existInList(listPerTurn.get(turnTab[i]), item) != null){
                appearsInList = true;
                break;
            }
            count++;
        }
        return count;
    }

    private Item existInList(List<Item> l, Item item){
        if(l.isEmpty()) return null;
        for(Item i: l){
            if(item.id.equals(i.id)){
                return i;
            }
        }
        return null;
    }

    private int buyItem(Item item){

        int[] turnTab = makeListOfTurn(Farmland.get().getLoadedSave().buyItemDatabasePerTurn);
        int count = countWhereItemAppearsInList(Farmland.get().getLoadedSave().buyItemDatabasePerTurn, turnTab, item);

        int res = 0;

        if(item.buyingValue > item.initValue / 4 && (!appearsInList && Farmland.get().getLoadedSave().turn > 0
                && Farmland.get().getLoadedSave().turn%2 == 0)){
            if(item instanceof Crop){
                res = -1;
            }else if(item instanceof Animal){
                res = -3;
            }

        }else if(item.buyingValue < item.initValue * 4 && appearsInList && count == 1){
            int previouslyBought = Objects.requireNonNull(existInList(Farmland.get().getLoadedSave().buyItemDatabasePerTurn.get(turnTab[turnTab.length - 1]), item)).quantity;
            if(item instanceof Crop) {
                res = 1 + previouslyBought/4;
            }else if(item instanceof Animal){
                res = 3 + previouslyBought/2;
            }
        }
        return res;
    }

    private int sellItem(Item item){

        int[] turnTab = makeListOfTurn(Farmland.get().getLoadedSave().sellItemDatabasePerTurn);
        int count = countWhereItemAppearsInList(Farmland.get().getLoadedSave().sellItemDatabasePerTurn, turnTab, item);

        int res = 0;
        if(item.sellingValue < item.initValue * 4 && (!appearsInList && Farmland.get().getLoadedSave().turn > 0
                && Farmland.get().getLoadedSave().turn%2 == 0)){
            if(item instanceof Crop){
                res = 1;
            }else if(item instanceof Animal){
                res = 3;
            }
        }else if(item.sellingValue > item.initValue/4  && appearsInList && count == 1){
            int previouslySold = Objects.requireNonNull(existInList(Farmland.get().getLoadedSave().sellItemDatabasePerTurn.get(turnTab[turnTab.length - 1]), item)).quantity;
            if(item instanceof Crop) {
                res = -(1 + previouslySold/4);
            }else if(item instanceof Animal){
                res = -(3 + previouslySold/2);
            }
        }

        return res;
    }

/**
 * To change the value of some items.
 */
    public void changeValueOfRessource(){
        for(Item item : Farmland.get().getLoadedSave().getResourceDatabase().values()){
            item.buyingValue += buyItem(item);
            item.sellingValue += sellItem(item);
        }
    }
}
