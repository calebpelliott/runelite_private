package net.runelite.client.bot;

import java.awt.*;
import java.util.List;

import static net.runelite.api.NpcID.BANKER_1613;
import static net.runelite.api.NpcID.BANKER_1633;

public class NPC {
    private static final int banker1 = BANKER_1633;
    private static final int banker2 = BANKER_1613;

    public static Rectangle getNpcTileRectangle()
    {
        List<net.runelite.api.NPC> npcs = Bot.client.getNpcs();
        Rectangle rec = null;
        for (net.runelite.api.NPC npc : npcs)
        {
            if(npc.getId() != banker1)
            {
                continue;
            }

            Polygon poly = npc.getCanvasTilePoly();
            Point v1 = new Point(poly.xpoints[0], poly.ypoints[0]);
            Point v2 = new Point(poly.xpoints[1], poly.ypoints[1]);
            Point v3 = new Point(poly.xpoints[2], poly.ypoints[2]);
            Point v4 = new Point(poly.xpoints[3], poly.ypoints[3]);

            Point midpoint = new Point((int)((v1.x + v3.x)/2), (int)((v1.y + v3.y)/2));

            Rectangle r = new Rectangle();
            r.width = 3;
            r.height = 3;
            r.x = midpoint.x - 1;
            r.y = midpoint.y - 1;

            rec = r;
        }


        return rec;
    }
}
