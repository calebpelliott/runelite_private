package net.runelite.client.bot;

import net.runelite.api.ItemID;

public class ParallelProcess extends Thread{

    public void run() {
        Processor.mixPotions();
    }
}
