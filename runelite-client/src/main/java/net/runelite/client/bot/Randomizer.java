package net.runelite.client.bot;

import net.runelite.api.HashTable;
import net.runelite.api.Point;

import java.awt.*;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Randomizer {
    static public Point randomPointFromRectangle(Rectangle rectangle)
    {
        int randX = ThreadLocalRandom.current().nextInt(rectangle.x, rectangle.x + rectangle.width);
        int randY = ThreadLocalRandom.current().nextInt(rectangle.y, rectangle.y + rectangle.height);

        return new Point(randX, randY);
    }

    public static enum WaitEvent{
        POST_MOUSE_MOVE,
        SHORT_CLICK
    }

    private static Dictionary<WaitEvent, List<Integer>> waitTimes;

    static public void randomWait(WaitEvent event)
    {
        if (waitTimes == null)
        {
            initializeWaitTimes();
        }

        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(waitTimes.get(event).get(0), waitTimes.get(event).get(1)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void initializeWaitTimes() {
        waitTimes = new Hashtable<WaitEvent, List<Integer>>();
        waitTimes.put(WaitEvent.POST_MOUSE_MOVE, Arrays.asList(new Integer[]{10,20}));
        waitTimes.put(WaitEvent.SHORT_CLICK, Arrays.asList(new Integer[]{60,120}));

        System.out.println("Initialized wait time dictionary");
    }
}
