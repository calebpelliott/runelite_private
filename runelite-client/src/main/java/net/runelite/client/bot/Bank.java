package net.runelite.client.bot;

import java.awt.*;
import java.awt.event.KeyEvent;

import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.bot.Processor.State;
import net.runelite.client.plugins.itemstats.stats.Stat;

public class Bank {
    public static Rectangle WATER_VILE_LOCATION = initRectangle(521,165,556,196);
    public static Rectangle HERB_LOCATION = initRectangle(570,165,604,195);


    private static Rectangle initRectangle(int topLeftX, int topLeftY, int botRightX, int botRightY) {
        Rectangle rec = new Rectangle();
        rec.x = topLeftX;
        rec.y = topLeftY;
        rec.height = botRightY - topLeftY;
        rec.width = botRightX - topLeftX;

        return rec;
    }

    public static State withdrawItem(Rectangle itemLocation, int item)
    {
        itemLocation = Randomizer.reduceRectangleSize(itemLocation);
        Click.clickPointFromCurrent(Randomizer.randomPointFromRectangle(itemLocation));

        if (Inventory.containsItem(item))
        {
            return State.ITEM_OBTAINED;
        }

        return State.ERROR;
    }

    public static State closeBank()
    {
        Click.clickButton(KeyEvent.VK_ESCAPE);
        Widget widget = Bot.client.getWidget(WidgetInfo.BANK_CONTAINER);
        if(widget == null)
        {
            return State.USE_ITEM_ON_ITEM;
        }

        return State.ERROR;
    }

    public static State openBank() {
        Polygon bankPoly = GameObjectIterpolate.getBankShape();
        bankPoly = Randomizer.reducePolygonSize(bankPoly);
        Point p = Randomizer.findRandomPointInPoly(bankPoly);
        Click.clickPointFromCurrent(p);

        State state = State.ERROR;

        Widget widget = Bot.client.getWidget(WidgetInfo.BANK_CONTAINER);
        if(widget != null)
        {
            state = State.BANK_OPEN;
        }

        return state;
    }
}
