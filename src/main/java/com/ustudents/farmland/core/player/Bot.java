package com.ustudents.farmland.core.player;

import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.item.Animal;
import com.ustudents.farmland.core.grid.Cell;
import com.ustudents.farmland.core.system.Caravan;
import com.ustudents.farmland.core.system.Research;
import com.ustudents.farmland.core.item.Crop;
import com.ustudents.farmland.core.item.Item;
import com.ustudents.farmland.scene.InGameScene;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class Bot {
    public enum Difficulty {
        Easy,
        Normal,
        Hard,
        Impossible
    }

    public static void playTurn() {
        Player player = Farmland.get().getLoadedSave().getCurrentPlayer();

        if (!Farmland.get().getLoadedSave().deadPlayers.contains(player.getId())) {

            SeedRandom random = new SeedRandom();

            switch (Farmland.get().getLoadedSave().difficulty) {
                case Easy:
                    int rd = random.generateInRange(0, 20);
                    if (rd < 5) {
                        buyLand(random);
                    } else {
                        addItem(random,1);
                    }
                    sellInventory();
                    break;
                case Normal:
                    int action = makeChoice();
                    if (action == 0) {
                        buyLand(random);
                    } else {
                        for (int i = 0; i < action; i++) {
                            addItem(random,1);
                        }
                    }
                    sellInventory();
                    break;
                case Hard:
                    int action2 = makeChoice();
                    if (action2 == 0) {
                        int choice = random.generateInRange(0, 10);
                        if (choice < 5) {
                            upgradeResearch();
                        } else {
                            buyLand(random);
                        }
                    } else {
                        addItem(random, action2);
                    }
                    exportInventory();
                    sellInventory();
                    break;
                case Impossible:
                    Random rand = new Random();
                    boolean cantPayDebt = false;
                    if (player.remainingDebt == 0 && rand.nextInt(100) <= 50) {
                        botTakesOutALoan();
                        cantPayDebt = true;
                    }

                    if (player.remainingDebt > 0 && !cantPayDebt) {
                        payDebt();
                    }
                    int action3 = makeChoice();
                    if (action3 == 0) {
                        int choice2 = random.generateInRange(0, 10);
                        if (choice2 < 5) {
                            upgradeResearch();
                        } else {
                            buyLand(random);
                        }
                    } else {
                        addItem(random, action3);
                    }
                    exportInventory();
                    sellInventory();
                    break;
            }
        }
    }

    public static void upgradeResearch(){
        Player player = Farmland.get().getLoadedSave().getCurrentPlayer();

        List<Cell> cells = player.getOwnedCells();
        if (!cells.isEmpty()) {
            for (int i = 0; i < cells.size(); i++){
                if(cells.get(i).hasItem()){
                    for (int y = 0; y < player.researches.size(); y++){
                        Research re = player.researches.get(y);
                        if (re.type == Research.Type.Breeder && cells.get(i).item instanceof Animal){
                            player.money -= re.getPrice();
                            re.levelUp(10,1);
                            break;
                        } else if (re.getType() == Research.Type.Farmer && cells.get(i).item instanceof Crop){
                            player.money -= re.getPrice();
                            re.levelUp(10,1);
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public static int makeChoice(){
        Player player = Farmland.get().getLoadedSave().getCurrentPlayer();
        int c = 0;

        for (int x = 0; x < Farmland.get().getLoadedSave().cells.size(); x++) {
            for (int y = 0; y < Farmland.get().getLoadedSave().cells.get(x).size(); y++) {
                Cell cell = Farmland.get().getLoadedSave().cells.get(x).get(y);

                if (cell.isOwned() && cell.ownerId.equals(player.getId())){
                    if (!cell.hasItem()){
                        c++;
                    }
                }
            }
        }
        return c;
    }

    public static void buyLand(SeedRandom random) {
        Player player = Farmland.get().getLoadedSave().getCurrentPlayer();

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

    public static void addItem(SeedRandom random,int nb) {
        Player player = Farmland.get().getLoadedSave().getCurrentPlayer();
        
        int i = 0;
        while (true) {
            if (i == 20) {
                return;
            }

            List<Item> items = Farmland.get().getLoadedSave().getResourceDatabase().values().stream().filter(Objects::nonNull).collect(Collectors.toList());
            Item item = items.get(random.generateInRange(0, items.size() - 1));

            if (player.money < item.buyingValue*nb) {
                i++;
                continue;
            }

            player.setMoney(player.money - (item.buyingValue*nb));
            Item clone = Item.clone(item);
            assert clone != null;
            clone.quantity = nb;
            fillTurnItemDataBasePerQuantity(clone,true);

            List<Cell> cells = player.getOwnedCellsWithNoItem();

            if (!cells.isEmpty()) {
                for (int y = 0; y < nb; y++){
                    Cell cellWanted = cells.get(y);
                    cellWanted.setItem(Item.clone(item));
                }
                Farmland.get().getLoadedSave().itemUsed.dispatch();
                break;
            } else {
                i++;
            }
        }
    }

    public static void sellInventory(){
        Player player = Farmland.get().getLoadedSave().getCurrentPlayer();

        for(Item item: player.getAllItemOfSellInventory().values()){
            item.sellingValue = Farmland.get().getLoadedSave().getResourceDatabase().get(item.id).sellingValue;
            fillTurnItemDataBasePerQuantity(item,false);
            player.money += item.quantity * item.sellingValue;
        }

        player.clearSoldLists();
    }

    private static void fillTurnItemDataBasePerQuantity(Item item, boolean buy){
        for(int i = 0; i < item.quantity; i++){
            Farmland.get().getLoadedSave().fillTurnItemDataBase(Item.clone(item), buy);
        }
    }

    public static void botTakesOutALoan(){
        Player player = Farmland.get().getLoadedSave().getCurrentPlayer();
        Random rand = new Random();
        int maxBorrow = Farmland.get().getLoadedSave().maxBorrow;
        if (Farmland.get().getLoadedSave().difficulty == Difficulty.Impossible) {
            maxBorrow += 300;
        }
        int randValue = rand.nextInt(maxBorrow - maxBorrow / 10);
        player.money += randValue;
        player.loan = randValue + (int)(randValue * 0.03f);
        player.remainingDebt += randValue + (int)(randValue * 0.03f);
        ((InGameScene)Farmland.get().getSceneManager().getCurrentScene()).updateMoneyItemLabel();
        ((InGameScene)Farmland.get().getSceneManager().getCurrentScene()).updateLeaderboard();
    }

    public static void payDebt(){
        Player player = Farmland.get().getLoadedSave().getCurrentPlayer();

        Random rand = new Random();
        int debt = player.remainingDebt;
        int randValue = rand.nextInt(debt);
        player.money -= randValue;
        player.remainingDebt -= randValue;
        ((InGameScene)Farmland.get().getSceneManager().getCurrentScene()).updateMoneyItemLabel();
        ((InGameScene)Farmland.get().getSceneManager().getCurrentScene()).updateLeaderboard();
    }

    public static void exportInventory() {
        Player player = Farmland.get().getLoadedSave().getCurrentPlayer();

        for (Item item : player.getAllItemOfSellInventory().values()) {
            int itemQuantity = item.quantity;
            while (itemQuantity > 1) {
                int fEffect = 0;
                int eEffect = 0;
                for (int i = 0; i < player.researches.size(); i++) {
                    Research re = player.researches.get(i);
                    if (re.getType() == Research.Type.Farmer) {
                        fEffect = re.getEffect();
                    } else if (re.getType() == Research.Type.Breeder) {
                        eEffect = re.getEffect();
                    }
                }
                int researchBonus = (item instanceof Crop) ? fEffect : eEffect;
                int sellValueOfCaravan = (int) (((item.sellingValue + researchBonus) * 1.25) * itemQuantity / 2);
                int travelTime = 4;
                int travelPrice = 10;
                player.money -= travelPrice;
                player.caravans.add(new Caravan(sellValueOfCaravan, travelTime, item.id));
                Item itemCopy = Item.clone(item);
                assert itemCopy != null;
                itemCopy.quantity = itemQuantity/2;
                boolean isAnimal = item instanceof Animal;
                for (int i = 0; i < itemQuantity / 2; i++) {
                    player.deleteFromSoldInventory(item);
                }
                itemQuantity /= 2;
            }
        }
    }

}
