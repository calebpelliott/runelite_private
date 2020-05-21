/*
 * Copyright (c) 2017, Kronos <https://github.com/KronosDesign>
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.devtools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GraphicsObject;
import net.runelite.api.TileItem;
import net.runelite.api.GroundObject;
import net.runelite.api.ItemLayer;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Node;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Projectile;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.bot.Bot;
import net.runelite.client.bot.GameObjectIterpolate;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

import static net.runelite.api.ObjectID.*;

@Singleton
class DevToolsOverlay extends Overlay
{
	private static final Font FONT = FontManager.getRunescapeFont().deriveFont(Font.BOLD, 16);
	private static final Color RED = new Color(221, 44, 0);
	private static final Color GREEN = new Color(0, 200, 83);
	private static final Color ORANGE = new Color(255, 109, 0);
	private static final Color YELLOW = new Color(255, 214, 0);
	private static final Color CYAN = new Color(0, 184, 212);
	private static final Color BLUE = new Color(41, 98, 255);
	private static final Color DEEP_PURPLE = new Color(98, 0, 234);
	private static final Color PURPLE = new Color(170, 0, 255);
	private static final Color GRAY = new Color(158, 158, 158);

	private static final int MAX_DISTANCE = 2400;

	private final Client client;
	private final DevToolsPlugin plugin;
	private final TooltipManager toolTipManager;

	@Inject
	private DevToolsOverlay(Client client, DevToolsPlugin plugin, TooltipManager toolTipManager)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(OverlayPriority.HIGHEST);
		this.client = client;
		this.plugin = plugin;
		this.toolTipManager = toolTipManager;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		graphics.setFont(FONT);

		if (plugin.getPlayers().isActive())
		{
			renderPlayers(graphics);
		}

		if (plugin.getNpcs().isActive())
		{
			renderNpcs(graphics);
		}

		if (plugin.getGroundItems().isActive() || plugin.getGroundObjects().isActive() || plugin.getGameObjects().isActive() || plugin.getWalls().isActive() || plugin.getDecorations().isActive() || plugin.getTileLocation().isActive())
		{
			renderTileObjects(graphics);
		}

		if (plugin.getInventory().isActive())
		{
			renderInventory(graphics);
		}

		if (plugin.getProjectiles().isActive())
		{
			renderProjectiles(graphics);
		}

		if (plugin.getGraphicsObjects().isActive())
		{
			renderGraphicsObjects(graphics);
		}

		return null;
	}

	private void renderPlayers(Graphics2D graphics)
	{
		List<Player> players = client.getPlayers();
		Player local = client.getLocalPlayer();

		for (Player p : players)
		{
			if (p != local)
			{
				String text = p.getName() + " (A: " + p.getAnimation() + ") (G: " + p.getGraphic() + ")";
				OverlayUtil.renderActorOverlay(graphics, p, text, BLUE);
			}
		}

		String text = local.getName() + " (A: " + local.getAnimation() + ") (G: " + local.getGraphic() + ")";
		OverlayUtil.renderActorOverlay(graphics, local, text, CYAN);
		renderPlayerWireframe(graphics, local, CYAN);
	}

	private void renderNpcs(Graphics2D graphics)
	{
		List<NPC> npcs = client.getNpcs();
		for (NPC npc : npcs)
		{
			NPCComposition composition = npc.getComposition();
			Color color = composition.getCombatLevel() > 1 ? YELLOW : ORANGE;
			if (composition.getConfigs() != null)
			{
				NPCComposition transformedComposition = composition.transform();
				if (transformedComposition == null)
				{
					color = GRAY;
				}
				else
				{
					composition = transformedComposition;
				}
			}

			String text = String.format("%s (ID: %d) (A: %d) (G: %d)",
				composition.getName(),
				composition.getId(),
				npc.getAnimation(),
				npc.getGraphic());

			OverlayUtil.renderActorOverlay(graphics, npc, text, color);
		}
	}

	private void renderTileObjects(Graphics2D graphics)
	{
		Scene scene = client.getScene();
		Tile[][][] tiles = scene.getTiles();

		int z = client.getPlane();

		for (int x = 0; x < Constants.SCENE_SIZE; ++x)
		{
			for (int y = 0; y < Constants.SCENE_SIZE; ++y)
			{
				Tile tile = tiles[z][x][y];

				if (tile == null)
				{
					continue;
				}

				Player player = client.getLocalPlayer();
				if (player == null)
				{
					continue;
				}

				if (plugin.getGroundItems().isActive())
				{
					renderGroundItems(graphics, tile, player);
				}

				if (plugin.getGroundObjects().isActive())
				{
					renderGroundObject(graphics, tile, player);
				}

				if (plugin.getGameObjects().isActive())
				{
					renderGameObjects(graphics, tile, player);
				}

				if (plugin.getWalls().isActive())
				{
					renderWallObject(graphics, tile, player);
				}

				if (plugin.getDecorations().isActive())
				{
					renderDecorObject(graphics, tile, player);
				}

				if (plugin.getTileLocation().isActive())
				{
					renderTileTooltip(graphics, tile);
				}
			}
		}
	}

	private void renderTileTooltip(Graphics2D graphics, Tile tile)
	{
		Polygon poly = Perspective.getCanvasTilePoly(client, tile.getLocalLocation());
		if (poly != null && poly.contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY()))
		{
			toolTipManager.add(new Tooltip("World Location: " + tile.getWorldLocation().getX() + ", " + tile.getWorldLocation().getY() + ", " + client.getPlane()));
			OverlayUtil.renderPolygon(graphics, poly, GREEN);
		}
	}

	private void renderGroundItems(Graphics2D graphics, Tile tile, Player player)
	{
		ItemLayer itemLayer = tile.getItemLayer();
		if (itemLayer != null)
		{
			if (player.getLocalLocation().distanceTo(itemLayer.getLocalLocation()) <= MAX_DISTANCE)
			{
				Node current = itemLayer.getBottom();
				while (current instanceof TileItem)
				{
					TileItem item = (TileItem) current;
					OverlayUtil.renderTileOverlay(graphics, itemLayer, "ID: " + item.getId() + " Qty:" + item.getQuantity(), RED);
					current = current.getNext();
				}
			}
		}
	}

	private void renderGameObjects(Graphics2D graphics, Tile tile, Player player)
	{
		GameObject[] gameObjects = tile.getGameObjects();
		if (gameObjects != null)
		{
			for (GameObject gameObject : gameObjects)
			{
				if (gameObject != null)
				{
					if (player.getLocalLocation().distanceTo(gameObject.getLocalLocation()) <= MAX_DISTANCE)
					{
						OverlayUtil.renderTileOverlay(graphics, gameObject, "ID: " + gameObject.getId(), GREEN);
					}

					// Draw a polygon around the convex hull
					// of the model vertices
					Shape p = gameObject.getConvexHull();
					if (p != null)
					{
						graphics.draw(p);
					}

					graphics.drawPolygon(GameObjectIterpolate.getBankShape());
				}
			}
		}
	}

	private void renderGroundObject(Graphics2D graphics, Tile tile, Player player)
	{
		GroundObject groundObject = tile.getGroundObject();
		if (groundObject != null)
		{
			if (player.getLocalLocation().distanceTo(groundObject.getLocalLocation()) <= MAX_DISTANCE)
			{
				OverlayUtil.renderTileOverlay(graphics, groundObject, "ID: " + groundObject.getId(), PURPLE);
			}
		}
	}

	private void renderWallObject(Graphics2D graphics, Tile tile, Player player)
	{
		WallObject wallObject = tile.getWallObject();
		if (wallObject != null)
		{
			if (player.getLocalLocation().distanceTo(wallObject.getLocalLocation()) <= MAX_DISTANCE)
			{
				OverlayUtil.renderTileOverlay(graphics, wallObject, "ID: " + wallObject.getId(), GRAY);
			}
		}
	}

	private void renderDecorObject(Graphics2D graphics, Tile tile, Player player)
	{
		DecorativeObject decorObject = tile.getDecorativeObject();
		if (decorObject != null)
		{
			if (player.getLocalLocation().distanceTo(decorObject.getLocalLocation()) <= MAX_DISTANCE)
			{
				OverlayUtil.renderTileOverlay(graphics, decorObject, "ID: " + decorObject.getId(), DEEP_PURPLE);
			}

			Shape p = decorObject.getConvexHull();
			if (p != null)
			{
				graphics.draw(p);
			}

			p = decorObject.getConvexHull2();
			if (p != null)
			{
				graphics.draw(p);
			}
		}
	}

	private void renderInventory(Graphics2D graphics)
	{
		Widget widget = client.getWidget(WidgetInfo.GRAND_EXCHANGE_WINDOW_CONTAINER);
		if(widget != null) {
			Rectangle rectangle = widget.getBounds();
			graphics.setColor(new Color(255, 255, 255, 65));
			graphics.fill(rectangle);

			Rectangle topOffersTopHori = new Rectangle();
			topOffersTopHori.height = 1;
			topOffersTopHori.width = rectangle.width;
			topOffersTopHori.x = rectangle.x;
			topOffersTopHori.y = (int)(rectangle.y + (rectangle.height * .38));
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(topOffersTopHori);

			Rectangle topOffersBottomHori = new Rectangle();
			topOffersBottomHori.height = 1;
			topOffersBottomHori.width = rectangle.width;
			topOffersBottomHori.x = rectangle.x;
			topOffersBottomHori.y = (int)(rectangle.y + (rectangle.height * .49));
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(topOffersBottomHori);

			Rectangle botOffersTopHori = new Rectangle();
			botOffersTopHori.height = 1;
			botOffersTopHori.width = rectangle.width;
			botOffersTopHori.x = rectangle.x;
			botOffersTopHori.y = (int)(rectangle.y + (rectangle.height * .74));
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(botOffersTopHori);

			Rectangle botOffersBottomHori = new Rectangle();
			botOffersBottomHori.height = 1;
			botOffersBottomHori.width = rectangle.width;
			botOffersBottomHori.x = rectangle.x;
			botOffersBottomHori.y = (int)(rectangle.y + (rectangle.height * .85));
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(botOffersBottomHori);

			Rectangle firstColumnLeftBuy = new Rectangle();
			firstColumnLeftBuy.height = rectangle.height;
			firstColumnLeftBuy.width = 1;
			firstColumnLeftBuy.x = (int)(rectangle.x + (rectangle.width * .06));
			firstColumnLeftBuy.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(firstColumnLeftBuy);

			Rectangle firstColumnRightBuy = new Rectangle();
			firstColumnRightBuy.height = rectangle.height;
			firstColumnRightBuy.width = 1;
			firstColumnRightBuy.x = (int)(rectangle.x + (rectangle.width * .14));
			firstColumnRightBuy.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(firstColumnRightBuy);

			Rectangle firstColumnLeftSell = new Rectangle();
			firstColumnLeftSell.height = rectangle.height;
			firstColumnLeftSell.width = 1;
			firstColumnLeftSell.x = (int)(rectangle.x + (rectangle.width * .17));
			firstColumnLeftSell.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(firstColumnLeftSell);

			Rectangle firstColumnRightSell = new Rectangle();
			firstColumnRightSell.height = rectangle.height;
			firstColumnRightSell.width = 1;
			firstColumnRightSell.x = (int)(rectangle.x + (rectangle.width * .25));
			firstColumnRightSell.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(firstColumnRightSell);

			Rectangle secondColumnLeftBuy = new Rectangle();
			secondColumnLeftBuy.height = rectangle.height;
			secondColumnLeftBuy.width = 1;
			secondColumnLeftBuy.x = (int)(rectangle.x + (rectangle.width * .29));
			secondColumnLeftBuy.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(secondColumnLeftBuy);

			Rectangle secondColumnRightBuy = new Rectangle();
			secondColumnRightBuy.height = rectangle.height;
			secondColumnRightBuy.width = 1;
			secondColumnRightBuy.x = (int)(rectangle.x + (rectangle.width * .37));
			secondColumnRightBuy.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(secondColumnRightBuy);

			Rectangle secondColumnLeftSell = new Rectangle();
			secondColumnLeftSell.height = rectangle.height;
			secondColumnLeftSell.width = 1;
			secondColumnLeftSell.x = (int)(rectangle.x + (rectangle.width * .40));
			secondColumnLeftSell.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(secondColumnLeftSell);

			Rectangle secondColumnRightSell = new Rectangle();
			secondColumnRightSell.height = rectangle.height;
			secondColumnRightSell.width = 1;
			secondColumnRightSell.x = (int)(rectangle.x + (rectangle.width * .48));
			secondColumnRightSell.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(secondColumnRightSell);

			Rectangle thirdColumnLeftBuy = new Rectangle();
			thirdColumnLeftBuy.height = rectangle.height;
			thirdColumnLeftBuy.width = 1;
			thirdColumnLeftBuy.x = (int)(rectangle.x + (rectangle.width * .52));
			thirdColumnLeftBuy.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(thirdColumnLeftBuy);

			Rectangle thirdColumnRightBuy = new Rectangle();
			thirdColumnRightBuy.height = rectangle.height;
			thirdColumnRightBuy.width = 1;
			thirdColumnRightBuy.x = (int)(rectangle.x + (rectangle.width * .59));
			thirdColumnRightBuy.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(thirdColumnRightBuy);

			Rectangle thirdColumnLeftSell = new Rectangle();
			thirdColumnLeftSell.height = rectangle.height;
			thirdColumnLeftSell.width = 1;
			thirdColumnLeftSell.x = (int)(rectangle.x + (rectangle.width * .63));
			thirdColumnLeftSell.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(thirdColumnLeftSell);

			Rectangle thirdColumnRightSell = new Rectangle();
			thirdColumnRightSell.height = rectangle.height;
			thirdColumnRightSell.width = 1;
			thirdColumnRightSell.x = (int)(rectangle.x + (rectangle.width * .70));
			thirdColumnRightSell.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(thirdColumnRightSell);

			Rectangle fourthColumnLeftBuy = new Rectangle();
			fourthColumnLeftBuy.height = rectangle.height;
			fourthColumnLeftBuy.width = 1;
			fourthColumnLeftBuy.x = (int)(rectangle.x + (rectangle.width * .75));
			fourthColumnLeftBuy.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(fourthColumnLeftBuy);

			Rectangle fourthColumnRightBuy = new Rectangle();
			fourthColumnRightBuy.height = rectangle.height;
			fourthColumnRightBuy.width = 1;
			fourthColumnRightBuy.x = (int)(rectangle.x + (rectangle.width * .82));
			fourthColumnRightBuy.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(fourthColumnRightBuy);

			Rectangle fourthColumnLeftSell = new Rectangle();
			fourthColumnLeftSell.height = rectangle.height;
			fourthColumnLeftSell.width = 1;
			fourthColumnLeftSell.x = (int)(rectangle.x + (rectangle.width * .86));
			fourthColumnLeftSell.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(fourthColumnLeftSell);

			Rectangle fourthColumnRightSell = new Rectangle();
			fourthColumnRightSell.height = rectangle.height;
			fourthColumnRightSell.width = 1;
			fourthColumnRightSell.x = (int)(rectangle.x + (rectangle.width * .93));
			fourthColumnRightSell.y = rectangle.y;
			graphics.setColor(new Color(78, 209, 38, 65));
			graphics.fill(fourthColumnRightSell);
		}

		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
		if (inventoryWidget == null || inventoryWidget.isHidden())
		{
			return;
		}

		for (WidgetItem item : inventoryWidget.getWidgetItems())
		{
			Rectangle slotBounds = item.getCanvasBounds();

			String idText = "" + item.getId();
			FontMetrics fm = graphics.getFontMetrics();
			Rectangle2D textBounds = fm.getStringBounds(idText, graphics);

			int textX = (int) (slotBounds.getX() + (slotBounds.getWidth() / 2) - (textBounds.getWidth() / 2));
			int textY = (int) (slotBounds.getY() + (slotBounds.getHeight() / 2) + (textBounds.getHeight() / 2));

			graphics.setColor(new Color(255, 255, 255, 65));
			graphics.fill(slotBounds);

			graphics.setColor(Color.BLACK);
			graphics.drawString(idText, textX + 1, textY + 1);
			graphics.setColor(YELLOW);
			graphics.drawString(idText, textX, textY);
		}


	}

	private void renderProjectiles(Graphics2D graphics)
	{
		List<Projectile> projectiles = client.getProjectiles();

		for (Projectile projectile : projectiles)
		{
			int projectileId = projectile.getId();
			String text = "(ID: " + projectileId + ")";
			int x = (int) projectile.getX();
			int y = (int) projectile.getY();
			LocalPoint projectilePoint = new LocalPoint(x, y);
			Point textLocation = Perspective.getCanvasTextLocation(client, graphics, projectilePoint, text, 0);
			if (textLocation != null)
			{
				OverlayUtil.renderTextLocation(graphics, textLocation, text, Color.RED);
			}
		}
	}

	private void renderGraphicsObjects(Graphics2D graphics)
	{
		List<GraphicsObject> graphicsObjects = client.getGraphicsObjects();

		for (GraphicsObject graphicsObject : graphicsObjects)
		{
			LocalPoint lp = graphicsObject.getLocation();
			Polygon poly = Perspective.getCanvasTilePoly(client, lp);

			if (poly != null)
			{
				OverlayUtil.renderPolygon(graphics, poly, Color.MAGENTA);
			}

			String infoString = "(ID: " + graphicsObject.getId() + ")";
			Point textLocation = Perspective.getCanvasTextLocation(
				client, graphics, lp, infoString, 0);
			if (textLocation != null)
			{
				OverlayUtil.renderTextLocation(graphics, textLocation, infoString, Color.WHITE);
			}
		}
	}

	private void renderPlayerWireframe(Graphics2D graphics, Player player, Color color)
	{
		Polygon[] polys = player.getPolygons();

		if (polys == null)
		{
			return;
		}

		graphics.setColor(color);

		for (Polygon p : polys)
		{
			graphics.drawPolygon(p);
		}
	}

}
