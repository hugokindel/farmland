package com.ustudents.farmland.core.player;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.component.GridComponent;
import com.ustudents.farmland.core.grid.Cell;
import com.ustudents.farmland.core.item.Crop;
import com.ustudents.farmland.core.item.Item;
import com.ustudents.farmland.scene.InGameScene;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class Bot {
    public static void playTurn() {
        Player player = Farmland.get().getCurrentSave().getCurrentPlayer();
        // TODO: seed
        SeedRandom random = new SeedRandom();

        int action = makeChoice();

        if (action == 0) {
            buyLand(random);
        } else {
            for (int i = 0; i < action ; i++){
                addItem(random);
            }
        }

        sellInventory();
        maintenanceCost();

        Random rand = new Random();
        boolean cantPayDebt = false;
        if(player.debtMoney == 0 && rand.nextInt(100) <= 15){
            //Out.println("Le bot prend un pret");
            botTakesOutALoan();
            cantPayDebt = true;
        }

        if(player.debtMoney > 0 && !cantPayDebt){
            //Out.println("Dette à payer : " + player.debtMoney);
            payDebt();
        }
    }

    public static int makeChoice(){
        Player player = Farmland.get().getCurrentSave().getCurrentPlayer();
        int c = 0;

        for (int x = 0; x < Farmland.get().getCurrentSave().cells.size(); x++) {
            for (int y = 0; y < Farmland.get().getCurrentSave().cells.get(x).size(); y++) {
                Cell cell = Farmland.get().getCurrentSave().cells.get(x).get(y);

                if (cell.isOwned() && cell.ownerId.equals(player.getId())){
                    if (!cell.hasItem()){
                        c++;
                    }
                }
            }
        }
        return c;
    }

    public static void maintenanceCost(){
        Player player = Farmland.get().getCurrentSave().getCurrentPlayer();

        for (int x = 0; x < Farmland.get().getCurrentSave().cells.size(); x++) {
            for (int y = 0; y < Farmland.get().getCurrentSave().cells.get(x).size(); y++) {
                Cell cell = Farmland.get().getCurrentSave().cells.get(x).get(y);

                if (cell.isOwned() && cell.ownerId.equals(player.getId())){
                    player.setMoney(player.money - 1);
                }
            }
        }

    }

    public static void buyLand(SeedRandom random) {
        Player player = Farmland.get().getCurrentSave().getCurrentPlayer();

        if (player.money < 25) {
            return;
        }

        List<Cell> cells = player.getCloseCellsAvailable();

        if (!cells.isEmpty()) {
            Cell cellWanted = cells.get(random.generateInRange(0, cells.size() - 1));
            cellWanted.setOwned(true, player.getId());
            player.setMoney(player.money - 25);
        }
    }

    public static void addItem(SeedRandom random) {
        Player player = Farmland.get().getCurrentSave().getCurrentPlayer();

        int i = 0;
        while (true) {
            if (i == 20) {
                return;
            }

            List<Item> items = Farmland.get().getCurrentSave().getResourceDatabase().values().stream().filter(Objects::nonNull).collect(Collectors.toList());
            Item item = items.get(random.generateInRange(0, items.size() - 1));
            Farmland.get().getCurrentSave().fillTurnItemDataBase(Item.clone(item), true);

            if (player.money < item.buyingValue) {
                i++;
                continue;
            }

            player.setMoney(player.money - item.buyingValue);
            Farmland.get().getCurrentSave().fillTurnItemDataBase(Item.clone(item), true);
            Item clone = Item.clone(item);
            assert clone != null;
            clone.quantity = 1;

            List<Cell> cells = player.getOwnedCellsWithNoItem();

            if (!cells.isEmpty()) {
                Cell cellWanted = cells.get(random.generateInRange(0, cells.size() - 1));
                cellWanted.setItem(clone);
                Farmland.get().getSceneManager().getCurrentScene().getEntityByName("map").getComponent(GridComponent.class).onItemUsed.dispatch();
                break;
            } else {
                i++;
            }
        }
    }

    public static void sellInventory(){
        Player player = Farmland.get().getCurrentSave().getCurrentPlayer();

        for(Item item: player.getAllItemOfSellInventory().values()){
            item.sellingValue = Farmland.get().getCurrentSave().getResourceDatabase().get(item.id).sellingValue;
            Farmland.get().getCurrentSave().fillTurnItemDataBase(Item.clone(item), false);
            player.money += item.quantity * item.sellingValue;
        }

        player.getAllItemOfSellInventory().clear();
    }

    public static void botTakesOutALoan(){
        Player player = Farmland.get().getCurrentSave().getCurrentPlayer();

        Random rand = new Random();
        int maxBorrow = Farmland.get().getCurrentSave().maxBorrow;
        int randValue = rand.nextInt(maxBorrow - maxBorrow/10);
        player.money += randValue;
        //Out.println("Avant l'enprunt : " + player.loanMoney);
        player.loanMoney = randValue + (int)(randValue*0.03f);
        //Out.println("Après l'enprunt : " + player.loanMoney);
        player.debtMoney += randValue + (int)(randValue*0.03f);
        //Out.println("Dette du bot : " + player.loanMoney);
        ((InGameScene)Farmland.get().getSceneManager().getCurrentScene()).onSelectedItemOrMoneyChanged();
        ((InGameScene)Farmland.get().getSceneManager().getCurrentScene()).leaderBoardUpdate();
    }

    public static void payDebt(){
        Player player = Farmland.get().getCurrentSave().getCurrentPlayer();

        Random rand = new Random();
        int debt = player.debtMoney;
        int randValue = rand.nextInt(debt);
        //Out.println("Remboursement de : " + randValue);
        player.money -= randValue;
        player.debtMoney -= randValue;
        ((InGameScene)Farmland.get().getSceneManager().getCurrentScene()).onSelectedItemOrMoneyChanged();
        ((InGameScene)Farmland.get().getSceneManager().getCurrentScene()).leaderBoardUpdate();
    }
}
