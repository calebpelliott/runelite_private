package net.runelite.client.plugins.aaa;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.bot.Bot;
import net.runelite.client.callback.Hooks;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientUI;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseListener;

@Slf4j
@PluginDescriptor(
        name = "my aaa plugin",
        description = "a test"
)
public class aaa extends Plugin {
    transient MouseListener mouseListener;
    public static Hooks ml;

    @Inject
    private Client client;

    @Inject
    private ClientUI clientUi;

    @Subscribe
    public void onStatChanged(StatChanged statChanged)
    {
        System.out.println("yo");
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        int x,y;
        LocalPoint lp = client.getLocalPlayer().getLocalLocation();
        WorldPoint wp = WorldPoint.fromLocal(client, lp);

        final Point canvasOffset = clientUi.getCanvasOffset();

        Point frameLocation = clientUi.getAbsoluteFrameLocation();

        Point canvasLocation = clientUi.getAbsoluteCanvasLocation();

        Bot b = new Bot();
        b.botTick(client, canvasLocation);
    }
}
