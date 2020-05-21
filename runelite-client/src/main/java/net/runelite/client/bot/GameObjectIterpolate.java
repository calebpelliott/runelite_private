package net.runelite.client.bot;

import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.Scene;
import net.runelite.api.Tile;

import java.awt.*;
import java.awt.geom.PathIterator;
import java.util.Arrays;

public class GameObjectIterpolate {
    private static Point leftGeBankWall = new Point(51,51);
    private static Point rightGeBankWall = new Point(51,48);

    public static Polygon getBankShape()
    {
        Point[] leftPoints = findLeftPoints();
        Point[] rightPoints = findRightPoints();

        Polygon poly = new Polygon();
        poly.addPoint(leftPoints[0].getX(), leftPoints[0].getY());
        poly.addPoint(leftPoints[1].getX(), leftPoints[1].getY());
        poly.addPoint(rightPoints[1].getX(), rightPoints[1].getY());
        poly.addPoint(rightPoints[0].getX(), rightPoints[0].getY());

        return poly;
    }

    private static Point[] findRightPoints() {
        GameObject go = getGameObject(rightGeBankWall);
        return getLeastXXX(go);
    }

    private static Point[] findLeftPoints() {
        GameObject go = getGameObject(leftGeBankWall);
        return getGreatestXXX(go);
    }

    private static Point[] getGreatestXXX(GameObject gameObject) {
        Shape s = gameObject.getConvexHull();
        PathIterator pi = s.getPathIterator(null);

        //get point with greatest x and point with greatest y
        Point init = new Point(-1, -1);
        Point[] greatestXs = new Point[3];
        Arrays.fill(greatestXs, init);

        //find 3 greates x coords
        for(;!pi.isDone(); pi.next())
        {
            double[] coords = new double[2];
            pi.currentSegment(coords);

            //for each element in the array, check if given number is greater than element. iterate until greater hit. on greater hit, iterate from
            //end of array, replacing each element with the preceding elements value until the current point is hit, at which point replace with given number and break
            for (int i = 0; i < greatestXs.length; i++) {
                if (coords[0] > greatestXs[i].getX())
                {
                    for (int j = greatestXs.length - 1; j > i ; j--) {
                        greatestXs[j] = greatestXs[j-1];
                    }
                    greatestXs[i] = new Point((int)coords[0], (int)coords[1]);
                    break;
                }
            }
        }

        Point greatestY = new Point(-1,-1);
        Point leastY = new Point(100000,100000);
        for(Point p : greatestXs)
        {
            if(p.getY() > greatestY.getY())
            {
                greatestY = p;
            }
            if(p.getY() < leastY.getY())
            {
                leastY = p;
            }
        }

        return new Point[]{leastY, greatestY};
    }

    private static Point[] getLeastXXX(GameObject gameObject) {
        Shape s = gameObject.getConvexHull();
        PathIterator pi = s.getPathIterator(null);

        //get point with greatest x and point with greatest y
        Point init = new Point(100000, 100000);
        Point[] greatestXs = new Point[3];
        Arrays.fill(greatestXs, init);

        //find 3 greates x coords
        for(;!pi.isDone(); pi.next())
        {
            double[] coords = new double[2];
            pi.currentSegment(coords);

            //for each element in the array, check if given number is greater than element. iterate until greater hit. on greater hit, iterate from
            //end of array, replacing each element with the preceding elements value until the current point is hit, at which point replace with given number and break
            for (int i = 0; i < greatestXs.length; i++) {
                if (coords[0] < greatestXs[i].getX())
                {
                    for (int j = greatestXs.length - 1; j > i ; j--) {
                        greatestXs[j] = greatestXs[j-1];
                    }
                    greatestXs[i] = new Point((int)coords[0], (int)coords[1]);
                    break;
                }
            }
        }

        Point greatestY = new Point(-1,-1);
        Point leastY = new Point(100000,100000);
        for(Point p : greatestXs)
        {
            if(p.getY() > greatestY.getY())
            {
                greatestY = p;
            }
            if(p.getY() < leastY.getY())
            {
                leastY = p;
            }
        }

        return new Point[]{leastY, greatestY};
    }

    private static Point[] getGreatestXX(GameObject gameObject) {

        Shape s = gameObject.getConvexHull();
        PathIterator pi = s.getPathIterator(null);

        //get point with greatest x and point with greatest y
        Point top = new Point(-1, -1);
        Point bot = new Point(-1, -1);
        for(;!pi.isDone(); pi.next())
        {
            double[] coords = new double[2];
            pi.currentSegment(coords);

            if(coords[0] > top.getX())
            {
                bot = top;
                top = new Point((int)coords[0], (int)coords[1]);
                continue;
            }
            if(coords[0] > bot.getX())
            {
                bot = new Point((int)coords[0], (int)coords[1]);
            }


        }
        if(top.getY() > bot.getY())
        {
            Point tmp = top;
            top = bot;
            bot = tmp;
        }

        return new Point[]{top, bot};
    }

    private static Point[] getLeastXX(GameObject gameObject) {

        Shape s = gameObject.getConvexHull();
        PathIterator pi = s.getPathIterator(null);

        //get point with greatest x and point with greatest y
        Point top = new Point(100000, 100000);
        Point bot = new Point(100000, 100000);
        for(;!pi.isDone(); pi.next())
        {
            double[] coords = new double[2];
            pi.currentSegment(coords);

            if(coords[0] < bot.getX())
            {
                top = bot;
                bot = new Point((int)coords[0], (int)coords[1]);
                continue;
            }
            if(coords[0] < top.getX())
            {
                top = new Point((int)coords[0], (int)coords[1]);
            }



        }
        if(top.getY() > bot.getY())
        {
            Point tmp = top;
            top = bot;
            bot = tmp;
        }

        return new Point[]{top, bot};
    }

    private static GameObject getGameObject(Point point) {
        Scene scene = Bot.client.getScene();
        Tile[][][] tiles = scene.getTiles();

        int z = Bot.client.getPlane();

        Tile tile = tiles[z][point.getX()][point.getY()];


        GameObject[] gameObjects = tile.getGameObjects();
        if (gameObjects != null) {
            for (GameObject gameObject : gameObjects) {
                if (gameObject != null) {
                    return gameObject;
                }
            }
        }
        return null;
    }
}
