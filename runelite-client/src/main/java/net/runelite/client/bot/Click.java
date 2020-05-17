package net.runelite.client.bot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadLocalRandom;
import net.runelite.api.Point;

public class Click{
    private static Robot robot;
    private int startX, startY, endX, endY;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public Click(int startX, int startY, int endX, int endY){
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public static void clickPointFromCurrent(Point p)
    {
        java.awt.Point start = MouseInfo.getPointerInfo().getLocation();
        Point begin = new Point(start.x, start.y);

        begin = translateToAbsolutePoint(begin);
        moveMouse(begin, p);

        Randomizer.randomWait(Randomizer.WaitEvent.POST_MOUSE_MOVE);

        leftClickAndRelease();
    }

    private static void leftClickAndRelease()
    {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Randomizer.randomWait(Randomizer.WaitEvent.SHORT_CLICK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private static Point translateToAbsolutePoint(Point p)
    {
        Dimension stretchedDimensions = Bot.client.getStretchedDimensions();
        Dimension realDimensions = Bot.client.getRealDimensions();

        int oldX = (int) (p.getX() * (stretchedDimensions.width / realDimensions.getWidth()));
        int oldY = (int) (p.getY() * (stretchedDimensions.height / realDimensions.getHeight()));

        return new net.runelite.api.Point(oldX + Bot.canvasLocation.getX(), oldY + Bot.canvasLocation.getY());

    }

    private static void moveMouse(Point start, Point end)
    {
        try {
            Process p = Runtime.getRuntime().exec("python3 /tmp/use_mouse.py " + start.getX() + " " + start.getY() + " " + end.getX() + " " + end.getY());

            while (p.isAlive())
            {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("FINISHED MOVING MOUSE");
    }

    public void run(){
        try {
            Process p = Runtime.getRuntime().exec("python3 /tmp/use_mouse.py " + startX + " " + startY + " " + endX + " " + endY);

            while (p.isAlive())
            {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("FINISHED MOVING MOUSE");

        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(60, 120));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}
