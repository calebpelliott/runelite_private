package net.runelite.client.bot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.api.ItemID;
import java.awt.Robot;
import java.awt.geom.PathIterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static net.runelite.api.ItemID.*;

@Slf4j
public class Bot {

    //The max distance that the bot will try interacting with something
    private static final int MAX_OBSERVABLE_DISTANCE = 500;

    private Scene selfScene;

    public static Client client;

    //Absolute (x,y) of top left client
    public static Point canvasLocation;

    static public int triggered = 0;

    public static ReentrantLock active = new ReentrantLock();

    public void botTick(Client clientRef, Point canvasLocation){
        client = clientRef;
        this.canvasLocation = canvasLocation;



        //update scene objects
        updateSelfScene();

        test();

        //perform actions based on current scene
        if(triggered == 1 && !active.isLocked())
        {
            System.out.println("Beginning new process!");
            active.lock();
            ParallelProcess processor = new ParallelProcess();
            processor.start();
        }
    }

    void test(){
        List<WidgetItem> list = Inventory.getInventory();
        Widget widget = Bot.client.getWidget(WidgetInfo.BANK_CONTAINER);
        GameObjectIterpolate.getBankShape();
        /*Widget widget = Bot.client.getWidget(WidgetInfo.GRAND_EXCHANGE_WINDOW_CONTAINER);
        Rectangle rectangle = widget.getBounds();
        Point p = widget.getCanvasLocation();
        p = Click.translateToAbsolutePoint(p);

        widget = Bot.client.getWidget(WidgetInfo.GRAND_EXCHANGE_INVENTORY_ITEMS_CONTAINER);
        p = widget.getCanvasLocation();
        p = Click.translateToAbsolutePoint(p);

        widget = Bot.client.getWidget(WidgetInfo.GRAND_EXCHANGE_OFFER_CONTAINER);
        p = widget.getCanvasLocation();
        p = Click.translateToAbsolutePoint(p);

        widget = Bot.client.getWidget(WidgetInfo.GRAND_EXCHANGE_OFFER_TEXT);
        p = widget.getCanvasLocation();
        p = Click.translateToAbsolutePoint(p);

        widget = Bot.client.getWidget(WidgetInfo.GRAND_EXCHANGE_OFFER_PRICE);
        p = widget.getCanvasLocation();
        p = Click.translateToAbsolutePoint(p);*/

        NPC.getNpcTileRectangle();

    }

    private void pickUpItem(int pickupItemID) throws AWTException {
        Robot robot = new Robot();
        Scene scene = client.getScene();
        Tile[][][] tiles = scene.getTiles();

        int z = client.getPlane();

        for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
            for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                Tile tile = tiles[z][x][y];

                if (tile == null) {
                    continue;
                }

                Player player = client.getLocalPlayer();

                if(player == null){
                    continue;
                }

                ItemLayer itemLayer = tile.getItemLayer();
                if (itemLayer != null)
                {
                    if (player.getLocalLocation().distanceTo(itemLayer.getLocalLocation()) <= MAX_OBSERVABLE_DISTANCE)
                    {
                        Node current = itemLayer.getBottom();
                        while (current instanceof TileItem)
                        {
                            TileItem item = (TileItem) current;
                            if(item.getId() == pickupItemID)
                            {
                                System.out.println("Found item: " + pickupItemID);
                                LocalPoint localPoint = itemLayer.getLocalLocation();
                                int sceneX = localPoint.getSceneX();
                                int sceneY = localPoint.getSceneY();
                                Polygon p = itemLayer.getCanvasTilePoly();
                                Model m = item.getModel();
                                Shape s = Perspective.getClickbox(client, m, client.getCameraYaw(), localPoint);
                                Point pp = Perspective.localToCanvas(client, localPoint, client.getPlane());

                                Point locationOfItem = findDesktopPoint(pp);
                                int l,k,j,h;
                                l = client.getViewportXOffset();
                                Canvas canvas = client.getCanvas();


                                int asdf = 5;
                                triggered = 0;
                            }

                            if(item.getId() == 1265 && triggered == 0)
                            {
                                int xPos = 0, yPos = 0;
                                LocalPoint localPoint = itemLayer.getLocalLocation();
                                Point p = findDesktopPoint(Perspective.localToCanvas(client, localPoint, client.getPlane()));

                                java.awt.Point start = MouseInfo.getPointerInfo().getLocation();

                                System.out.println("Item found at: " + p.getX() + "," + p.getY());
                                //Click c = new Click(start.x, start.y, p.getX(), p.getY());
                                //c.start();

                                ParallelProcess processor = new ParallelProcess();
                                processor.start();

                                triggered = 1;
                            }
                            current = current.getNext();
                        }
                    }
                }
            }
        }

    }

    private Point findDesktopPoint(Point perspectivePoint)
    {
        Dimension stretchedDimensions = client.getStretchedDimensions();
        Dimension realDimensions = client.getRealDimensions();

        int oldX = (int) (perspectivePoint.getX() * (stretchedDimensions.width / realDimensions.getWidth()));
        int oldY = (int) (perspectivePoint.getY() * (stretchedDimensions.height / realDimensions.getHeight()));

        return new Point(oldX + canvasLocation.getX(), oldY + canvasLocation.getY());
    }

    private void moveMouseRelative(Point relativePoint)
    {

    }
    private void updateSelfScene() {
        Scene scene = client.getScene();
        Tile[][][] tiles = scene.getTiles();

        int z = client.getPlane();

        for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
            for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                Tile tile = tiles[z][x][y];

                if (tile == null) {
                    continue;
                }

                if((x == 51) && (y == 51))
                {
                    GameObject[] gameObjects = tile.getGameObjects();
                    if (gameObjects != null) {
                        for (GameObject gameObject : gameObjects) {
                            if (gameObject != null) {
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
                                        top = new Point((int)coords[0], (int)coords[1]);
                                    }
                                    if(coords[1] > bot.getY())
                                    {
                                        bot = new Point((int)coords[0], (int)coords[1]);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
