package net.runelite.client.bot;

import java.awt.*;
import net.runelite.api.Point;

public class Processor {
    /*
    Use @itemFirst on @itemSecond and wait until @itemSecond no longer exists in inventory
     */
    public static void useItemOnItemWait(int itemFirst, int itemSecond)
    {
        Rectangle rectangle = Inventory.getClientRectangle(itemFirst, 1);
        Point firstItemPoint = Randomizer.randomPointFromRectangle(rectangle);

        Click.clickPointFromCurrent(firstItemPoint);

        rectangle = Inventory.getClientRectangle(itemSecond, 3);
        Point secondItemPoint = Randomizer.randomPointFromRectangle(rectangle);

        Click.clickPointFromCurrent(secondItemPoint);

        while(Inventory.containsItem(itemSecond)){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("FINISHED PROCESSING");
    }
}