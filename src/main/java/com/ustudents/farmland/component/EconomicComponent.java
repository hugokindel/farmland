package com.ustudents.farmland.component;

import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.item.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EconomicComponent {

    public List<Item> lastItemTurn;

    public EconomicComponent(){lastItemTurn = new ArrayList<>();}

    private int knowTheDifferenceBetweenTurn(int turn,int lastTurn){
        int res;
        if(lastTurn>turn){
            float tmp = (float)1/2*(lastTurn-turn);
            res = -(int)tmp;
        }else if (lastTurn<turn){
            res = 2*(turn-lastTurn);
        }else{
            res = 0;
        }
        return res;
    }

    private int countItemPerList(Item item,List<Item> list){
        int count = 0;
        for (Item item1: list){
            if (item != null && item1 != null) {
                if(item.getClass().getName().equals(item1.getClass().getName())){
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * To change the value of some items.
     */
    public void changeValueOfRessource(){
        List<Item> itemsTurn = Farmland.get().getLoadedSave().itemsTurn;
        Set<Item> setItemTurn = new HashSet<>(itemsTurn);
        for(Item item:setItemTurn){
            if (item != null) {
                int lastItemSell = countItemPerList(item,lastItemTurn);
                int turnItemSell = countItemPerList(item,itemsTurn);
                int res = knowTheDifferenceBetweenTurn(turnItemSell,lastItemSell);
                if(item.value + res >= item.initValue) item.value += res;
            }
        }
    }
}
