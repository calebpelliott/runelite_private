package net.runelite.client.bot;

import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;

import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;
import java.util.concurrent.ThreadLocalRandom;

public class Inventory {

    static public List<WidgetItem> getInventory()
    {
        List<WidgetItem> list = new ArrayList<WidgetItem>();

        Widget inventoryWidget = Bot.client.getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget == null || inventoryWidget.isHidden())
        {
            return list;
        }

        for (WidgetItem item : inventoryWidget.getWidgetItems())
        {
            list.add(item);
        }

        return list;
    }

    static public List<WidgetItem> getInventoryFromId(int itemId)
    {
        List<WidgetItem> list = new ArrayList<WidgetItem>();

        Widget inventoryWidget = Bot.client.getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget == null || inventoryWidget.isHidden())
        {
            return list;
        }

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
    static public Rectangle getClientRectangle(int itemId, int random)
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
}
