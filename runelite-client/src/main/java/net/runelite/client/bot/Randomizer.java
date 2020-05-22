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
        SHORT_CLICK,
        POST_MOUSE_MOVE_LONG
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
        waitTimes.put(WaitEvent.POST_MOUSE_MOVE_LONG, Arrays.asList(new Integer[]{200,300}));

        System.out.println("Initialized wait time dictionary");
    }

    public static Polygon reducePolygonSize(Polygon p)
    {
        p.xpoints[0] = p.xpoints[0] + 10;
        p.ypoints[0] = p.ypoints[0] + 10;

        p.xpoints[1] = p.xpoints[1] + 10;
        p.ypoints[1] = p.ypoints[1] - 10;

        p.xpoints[2] = p.xpoints[2] - 10;
        p.ypoints[2] = p.ypoints[2] - 10;

        p.xpoints[3] = p.xpoints[3] - 10;
        p.ypoints[3] = p.ypoints[3] + 10;

        return p;
    }

    public static Rectangle reduceRectangleSize(Rectangle rectangle)
    {
        rectangle.x = rectangle.x + 5;
        rectangle.y = rectangle.y + 5;
        rectangle.width = rectangle.width - 10;
        rectangle.height = rectangle.height - 10;

        return rectangle;
    }

    public static Point findRandomPointInPoly(Polygon p)
    {
        Rectangle rec = p.getBounds();
        Point point = randomPointFromRectangle(rec);

        while (!rec.contains(new java.awt.Point(point.getX(), point.getY())))
        {
            point = randomPointFromRectangle(rec);
        }

        return point;
    }
}
