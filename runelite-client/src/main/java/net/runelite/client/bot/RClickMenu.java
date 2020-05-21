package net.runelite.client.bot;

import net.runelite.api.MenuEntry;
import net.runelite.client.input.KeyListener;

import java.awt.event.KeyEvent;

public class RClickMenu implements KeyListener {
    public static void getMenuOptions()
    {
        MenuEntry[] menuEntries = Bot.client.getMenuEntries();

        for (int i = 0; i < menuEntries.length; i++)
        {
            MenuEntry entry = menuEntries[i];
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_Q)
        {
            RClickMenu.getMenuOptions();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
