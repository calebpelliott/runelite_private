package net.runelite.client.bot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;

@Slf4j
public class Bot {

    //The max distance that the bot will try interacting with something
    private static final int MAX_OBSERVABLE_DISTANCE = 500;

    private Scene selfScene;

    private Client client;

    public void botTick(Client clientRef){
        client = clientRef;

        //update scene objects
        updateSelfScene();

        //perform actions based on current scene
        pickUpItem(1351);
    }

    private void pickUpItem(int pickupItemID) {
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
                            }
                            current = current.getNext();
                        }
                    }
                }
            }
        }

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

                Player player = client.getLocalPlayer();
            }
        }
    }
}
