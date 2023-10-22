package algorithms;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import main.Launcher;
import player.CharacterGraphics;
import player.Player;
import states.GameState;
import tiles.Tiles;

public class Toolbox {

	public static BufferedImage flipImage(BufferedImage image) {
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-image.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return op.filter(image, null);
	}

	public static int mouseX() {
		Point mousePosition = Launcher.game.getDisplay().getCanvas().getMousePosition();
		if (mousePosition == null) {
			return -1;
		} else {
			return mousePosition.x;
		}
	}

	public static int mouseY() {
		Point mousePosition = Launcher.game.getDisplay().getCanvas().getMousePosition();
		if (mousePosition == null) {
			return -1;
		} else {
			return mousePosition.y;
		}
	}
	
	public static int currentTile() {
		int x = (int) Math.round(Player.playerX), y = (int) Math.round(Player.playerY);
		return Tiles.typeMap.get(Tiles.hash(x, y));
	}
	
	public static int getTile(double x, double y) {
		int iX = (int) x, iY = (int) y;
		return Tiles.typeMap.get(Tiles.hash(iX, iY));
	}

	public static void drawCenteredImage(Graphics2D g, BufferedImage image, int x, int y, int width, int height) {
		g.drawImage(image, x - width / 2, y - height / 2, width, height, null);
	}

	public static void drawPosition(double x, double y, Graphics2D g) {
		int[] positionOnScreen = positionOnScreen(x, y);
		g.setColor(Color.WHITE);
		g.setStroke(new BasicStroke(1));
		g.drawOval(positionOnScreen[0] - 6, positionOnScreen[1] - 6, 12, 12);
	}

	public static int[] positionOnScreen(double x, double y) {
		double relativeX = x - Player.playerX, relativeY = y - Player.playerY;
		double pixelsRightPerX = GameState.halfTileWidth * GameState.zoom,
				pixelsDownPerX = GameState.halfTileHeight * GameState.zoom;
		double pixelsRightPerY = GameState.halfTileWidth * GameState.zoom,
				pixelsDownPerY = -GameState.halfTileHeight * GameState.zoom;
		double pixelsRight = pixelsRightPerX * relativeX + pixelsRightPerY * relativeY;
		double pixelsDown = pixelsDownPerX * relativeX + pixelsDownPerY * relativeY;
		int screenX = Launcher.WIDTH / 2 + (int) pixelsRight;
		int screenY = Launcher.HEIGHT / 2 + (int) pixelsDown + CharacterGraphics.playerYDisplacement;
		return new int[] { screenX, screenY };
	}

	public static int[] screenDisplacementFromOrigin(double x, double y) {
		double pixelsRightPerX = GameState.halfTileWidth * GameState.zoom,
				pixelsDownPerX = GameState.halfTileHeight * GameState.zoom;
		double pixelsRightPerY = GameState.halfTileWidth * GameState.zoom,
				pixelsDownPerY = -GameState.halfTileHeight * GameState.zoom;
		double pixelsRight = pixelsRightPerX * x + pixelsRightPerY * y;
		double pixelsDown = pixelsDownPerX * x + pixelsDownPerY * y;
		int screenX = Launcher.WIDTH / 2 + (int) pixelsRight;
		int screenY = Launcher.HEIGHT / 2 + (int) pixelsDown
				+ (int) (CharacterGraphics.playerRenderHeight / 2 * GameState.zoom);
		return new int[] { screenX, screenY };
	}

	public static int xOnScreen(double x, double y) {
		double relativeX = x - Player.playerX, relativeY = y - Player.playerY;
		double pixelsRightPerX = GameState.halfTileWidth * GameState.zoom;
		double pixelsRightPerY = GameState.halfTileWidth * GameState.zoom;
		double pixelsRight = pixelsRightPerX * relativeX + pixelsRightPerY * relativeY;
		int screenX = Launcher.WIDTH / 2 + (int) pixelsRight;
		return screenX;
	}

	public static int yOnScreen(double x, double y) {
		double relativeX = x - Player.playerX, relativeY = y - Player.playerY;
		double pixelsDownPerX = GameState.halfTileHeight * GameState.zoom;
		double pixelsDownPerY = -GameState.halfTileHeight * GameState.zoom;
		double pixelsDown = pixelsDownPerX * relativeX + pixelsDownPerY * relativeY;
		int screenY = Launcher.HEIGHT / 2 + (int) pixelsDown
				+ (int) (CharacterGraphics.playerRenderHeight / 2 * GameState.zoom);
		return screenY;
	}

	public static int screenXFromOrigin(double x, double y) {
		double pixelsRightPerX = GameState.halfTileWidth * GameState.zoom;
		double pixelsRightPerY = GameState.halfTileWidth * GameState.zoom;
		double pixelsRight = pixelsRightPerX * x + pixelsRightPerY * y;
		int screenX = Launcher.WIDTH / 2 + (int) pixelsRight;
		return screenX;
	}

	public static int screenYFromOrigin(double x, double y) {
		double pixelsDownPerX = GameState.halfTileHeight * GameState.zoom;
		double pixelsDownPerY = -GameState.halfTileHeight * GameState.zoom;
		double pixelsDown = pixelsDownPerX * x + pixelsDownPerY * y;
		int screenY = Launcher.HEIGHT / 2 + (int) pixelsDown
				+ (int) (CharacterGraphics.playerRenderHeight / 2 * GameState.zoom);
		return screenY;
	}

	public static double constrain(double x, double lowerBound, double upperBound) {
		if (x > upperBound)
			return upperBound;
		if (x < lowerBound)
			return lowerBound;
		return x;
	}

	public static double map(double input, double startMin, double startMax, double mappedMin, double mappedMax) {
		return mappedMin + (input - startMin) * (mappedMax - mappedMin) / (startMax - startMin);
	}

	public static void setFontSize(Graphics2D g, float size) {
		g.setFont(Launcher.gameFont.deriveFont(size));
	}

	public static void drawText(Graphics2D g, String text, int x, int y) {

		int height = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
		int width = g.getFontMetrics().stringWidth(text);
		int textX, textY;

		if (alignX == ALIGN_LEFT) {
			textX = x;
		} else if (alignX == ALIGN_CENTER) {
			textX = x - width / 2;
		} else {
			textX = x - width;
		}

		if (alignY == ALIGN_TOP) {
			textY = y + height;
		} else if (alignY == ALIGN_CENTER) {
			textY = y + height / 2;
		} else {
			textY = y;
		}

		g.drawString(text, textX, textY);

	}

	public static void drawBoundedText(Graphics2D g, String text, int x, int y, int textBoxWidth) {

		TextLayout layout = new TextLayout(text, g.getFont(), g.getFontRenderContext());
		String[] splitted = text.split("\n");
		List<String> lines = new ArrayList<String>();
		for (String line : splitted) {
			if (line.equals("")) {
				lines.add("");
				continue;
			}
			String str = "";
			String[] words = line.split(" ");
			int strWidth = 0;
			for (int i = 0; i < words.length; i++) {
				String word = words[i] + ((i == words.length - 1) ? "" : " ");
				int wordWidth = g.getFontMetrics().stringWidth(word);
				if (strWidth + wordWidth > textBoxWidth) {
					lines.add(str);
					str = word;
					strWidth = wordWidth;
				} else if (i == words.length - 1) {
					str += word;
					lines.add(str);
					str = "";
					strWidth = 0;
				} else {
					str += word;
					strWidth += wordWidth;
				}
			}
			lines.add(str);
		}
		for (int i = 0; i < lines.size(); i++) {
			g.drawString(lines.get(i), x, (int) (y + i * (layout.getBounds().getHeight() + 3)));
		}

	}

	public static int pixelsFromCenter(double x, double y) {
		int[] positionOnScreen = positionOnScreen(x, y);
		int displacementX = positionOnScreen[0] - Launcher.WIDTH / 2;
		int displacementY = positionOnScreen[1] - Launcher.HEIGHT / 2;
		return (int) Math.sqrt(displacementX * displacementX + displacementY * displacementY);
	}
	
	public static int pixelsFromPlayer(double x, double y) {
		int[] positionOnScreen = positionOnScreen(x, y);
		int displacementX = positionOnScreen[0] - Launcher.WIDTH / 2;
		int displacementY = positionOnScreen[1] - (Launcher.HEIGHT / 2 + CharacterGraphics.playerYDisplacement);
		return (int) Math.sqrt(displacementX * displacementX + displacementY * displacementY);
	}

	public static int alignX = 0, alignY = 0;
	public static final int ALIGN_TOP = 0, ALIGN_LEFT = 0, ALIGN_CENTER = 1, ALIGN_BOTTOM = 2, ALIGN_RIGHT = 2;

	public static void setAlignX(int align) {
		alignX = align;
	}

	public static void setAlignY(int align) {
		alignY = align;
	}

	public static void setAlign(int x, int y) {
		alignX = x;
		alignY = y;
	}

}
