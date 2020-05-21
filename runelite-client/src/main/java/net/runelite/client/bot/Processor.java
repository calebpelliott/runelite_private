package net.runelite.client.bot;

import java.awt.*;
import java.awt.event.KeyEvent;

import net.runelite.api.ItemID;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

public class Processor {
    /*
    Use @itemFirst on @itemSecond and wait until @itemSecond no longer exists in inventory
     */
    enum State{
        INVENTORY_EMPTY,
        OPENING_BANK,
        BANK_OPEN,
        OBTAINING_ITEM,
        ITEM_OBTAINED,
        CLOSING_BANK,
        USE_ITEM_ON_ITEM,
        CONFIRM_POPUP,
        WAIT_PROCESSING,
        DEPOSIT_INVENTORY,
        PROCESSING,
        UNKNOWN,
        ERROR
    }

    private static State getState()
    {
        if(Inventory.isEmpty())
        {
            return State.INVENTORY_EMPTY;
        }

        return State.UNKNOWN;
    }

    public static void mixPotions()
    {
        State state = getState();

        if (state == State.INVENTORY_EMPTY){
            System.out.println("OPENING BANK");
            state = State.OPENING_BANK;
            state = Bank.openBank();

            if (state == State.ERROR){
                return;
            }
        }
        if (state == State.BANK_OPEN)
        {
            System.out.println("WITHDRAWING ITEM");
            State tempState = Bank.withdrawItem(Bank.WATER_VILE_LOCATION, ItemID.WATERFILLED_VIAL);
            if (tempState == State.ERROR){
                return;
            }
        }
        if (state == State.BANK_OPEN)
        {
            System.out.println("WITHDRAWING ITEM");
            State tempState = Bank.withdrawItem(Bank.HERB_LOCATION, ItemID.GUAM_LEAF);
            if (tempState == State.ERROR){
                return;
            }
        }
        if (state == State.BANK_OPEN)
        {
            System.out.println("CLOSING BANK");
            state = Bank.closeBank();
            if (state == State.ERROR){
                return;
            }
        }
        if(state == State.USE_ITEM_ON_ITEM)
        {
            System.out.println("USING ITEM ON ITEM");
            state = Inventory.useItemOnItemWait(ItemID.VIAL_OF_WATER, ItemID.GUAM_LEAF);
        }
        if(state == State.CONFIRM_POPUP)
        {
            System.out.println("CONFIRMING POPUP");
            Click.clickButton(KeyEvent.VK_SPACE);
            Inventory.waitUntilItemConsumed(ItemID.VIAL);
            state = State.OPENING_BANK;
        }
        if(state == State.OPENING_BANK)
        {
            System.out.println("OPENING BANK");
            state = Bank.openBank();

            if (state == State.ERROR){
                return;
            }
        }
        if(state == State.BANK_OPEN)
        {
            System.out.println("DEPOSITING ITEM");
            Inventory.clickItem(ItemID.GUAM_POTION_UNF);
            if (Inventory.containsItem(ItemID.GUAM_POTION_UNF))
            {
                return;
            }
        }

    }


}