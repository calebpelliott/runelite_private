package net.runelite.client.bot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
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

    public static void clickButton(int key)
    {
        robot.keyPress(key);
        Randomizer.randomWait(Randomizer.WaitEvent.SHORT_CLICK);
        robot.keyRelease(key);
        Randomizer.randomWait(Randomizer.WaitEvent.SHORT_CLICK);
    }

    public static void clickPointFromCurrent(Point end)
    {
        java.awt.Point start = MouseInfo.getPointerInfo().getLocation();
        Point begin = new Point(start.x, start.y);

        end = translateToAbsolutePoint(end);
        moveMouse(begin, end);

        Randomizer.randomWait(Randomizer.WaitEvent.POST_MOUSE_MOVE);

        leftClickAndRelease();
    }

    public static void clickPointFromCurrent(Point end, Randomizer.WaitEvent event)
    {
        java.awt.Point start = MouseInfo.getPointerInfo().getLocation();
        Point begin = new Point(start.x, start.y);

        end = translateToAbsolutePoint(end);
        moveMouse(begin, end);

        Randomizer.randomWait(event);

        leftClickAndRelease();
    }
    private static void leftClickAndRelease()
    {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Randomizer.randomWait(Randomizer.WaitEvent.SHORT_CLICK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Randomizer.randomWait(Randomizer.WaitEvent.SHORT_CLICK);
    }

    public static Point translateToAbsolutePoint(Point p)
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

        List<String> list = readFile("/tmp/points.txt");

        startMove(list);

        System.out.println("FINISHED MOVING MOUSE");
    }

    private static void startMove(List<String> list) {
        for (String st : list)
        {
            String[] points = st.split(" ");
            robot.mouseMove(Integer.parseInt(points[0]), Integer.parseInt(points[1]) );
            Processor.sleep(2);
        }
    }

    public static List<String> readFile(String s) {
        File file = new File(s);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String st = "";
        List<String> stringList = new ArrayList<>();

        while (true) {
            try {
                if (!((st = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            stringList.add(st);
        }
        return stringList;
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
