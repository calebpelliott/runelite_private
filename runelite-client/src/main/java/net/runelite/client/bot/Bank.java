package net.runelite.client.bot;

import java.awt.*;
import java.awt.event.KeyEvent;

import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.bot.Processor.State;
import net.runelite.client.plugins.itemstats.stats.Stat;

import static net.runelite.client.bot.Randomizer.WaitEvent.POST_MOUSE_MOVE_LONG;

public class Bank {
    public final static Rectangle WATER_VILE_LOCATION = initRectangle(457,115,479,143);
    public final static Rectangle HERB_LOCATION = initRectangle(502,115,535,144);


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

        while(!Inventory.containsItem(item))
        {
            System.out.println("Waiting for item to enter inventory");
            Processor.sleep();
        }

        return State.ITEM_OBTAINED;
    }

    public static State closeBank()
    {
        Click.clickButton(KeyEvent.VK_ESCAPE);
        Widget widget = Bot.client.getWidget(WidgetInfo.BANK_CONTAINER);

        while(!widget.isHidden()){
            widget = Bot.client.getWidget(WidgetInfo.BANK_CONTAINER);
            Processor.sleep();
        }

        return State.USE_ITEM_ON_ITEM;
    }

    public static State openBank() {
        Polygon bankPoly = GameObjectIterpolate.getBankShape();
        bankPoly = Randomizer.reducePolygonSize(bankPoly);
        Point p = Randomizer.findRandomPointInPoly(bankPoly);
        Click.clickPointFromCurrent(p, POST_MOUSE_MOVE_LONG);

        Widget widget = Bot.client.getWidget(WidgetInfo.BANK_CONTAINER);
        while(widget == null || widget.isHidden())
        {
            widget = Bot.client.getWidget(WidgetInfo.BANK_CONTAINER);
            Processor.sleep();
        }
        System.out.println("openBank returning");
        return State.BANK_OPEN;
    }
}
