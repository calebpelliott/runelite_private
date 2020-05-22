package net.runelite.client.bot;

import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.bot.Processor.State;

import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;
import java.util.concurrent.ThreadLocalRandom;


public class Inventory {

    public static State useItemOnItemWait(int itemFirst, int itemSecond)
    {
        Rectangle rectangle = Inventory.getClientRectangle(itemFirst, 3);
        rectangle = Randomizer.reduceRectangleSize(rectangle);
        Point firstItemPoint = Randomizer.randomPointFromRectangle(rectangle);

        Click.clickPointFromCurrent(firstItemPoint);

        rectangle = Inventory.getClientRectangle(itemSecond, 3);
        rectangle = Randomizer.reduceRectangleSize(rectangle);
        Point secondItemPoint = Randomizer.randomPointFromRectangle(rectangle);

        Click.clickPointFromCurrent(secondItemPoint);


        System.out.println("BEGIN PROCESSING");
        return State.CONFIRM_POPUP;
    }

    public static List<WidgetItem> getInventory()
    {
        List<WidgetItem> list = new ArrayList<WidgetItem>();

        Widget inventoryWidget = Bot.client.getWidget(WidgetInfo.INVENTORY);

        for (WidgetItem item : inventoryWidget.getWidgetItems())
        {
            list.add(item);
        }

        return list;
    }

    public static boolean isEmpty()
    {
        return getInventory().isEmpty();
    }

    public static List<WidgetItem> getInventoryFromId(int itemId)
    {
        List<WidgetItem> list = new ArrayList<WidgetItem>();

        Widget inventoryWidget = Bot.client.getWidget(WidgetInfo.INVENTORY);

        for (WidgetItem item : inventoryWidget.getWidgetItems())
        {
            if(item.getId() == itemId) {
                list.add(item);
            }
        }

        return list;
    }

    /*
    Returns client rectangle of item @itemId from first @random number of item occurrences
     */
    public static Rectangle getClientRectangle(int itemId, int random)
    {
        Rectangle rec = new Rectangle();
        List<WidgetItem> items = getInventoryFromId(itemId);

        int occurrences = items.size();

        if(occurrences == 0){
            return rec;
        }

        if(random > occurrences)
        {
            random = occurrences;
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(0, random);

        return items.get(randomIndex).getCanvasBounds();
    }

    public static boolean containsItem(int itemId) {
        List<WidgetItem> items = getInventory();

        for (WidgetItem item : items) {
            if (item.getId() == itemId) {
                return true;
            }
        }

        return false;
    }

    public static void waitUntilItemConsumed(int itemID) {
        System.out.println("Sleeping until " + itemID + " not longer exists");
        while(Inventory.containsItem(itemID)){
            System.out.println("Item still in inventory");
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void clickItem(int itemID) {
        Rectangle rec = Inventory.getClientRectangle(itemID, 4);
        rec = Randomizer.reduceRectangleSize(rec);
        Click.clickPointFromCurrent(Randomizer.randomPointFromRectangle(rec));
    }
}
