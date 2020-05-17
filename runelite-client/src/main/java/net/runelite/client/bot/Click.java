package net.runelite.client.bot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadLocalRandom;

public class Click extends Thread{
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
