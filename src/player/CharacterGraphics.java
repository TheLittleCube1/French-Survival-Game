package player;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import algorithms.Toolbox;
import main.Game;
import main.Launcher;
import states.GameState;

public class CharacterGraphics {
	
	public static BufferedImage[] IDLE_LEFT = new BufferedImage[4], IDLE_RIGHT = new BufferedImage[4];
	public static BufferedImage[] RUN_LEFT = new BufferedImage[6], RUN_RIGHT = new BufferedImage[6];
	public static BufferedImage[] SLASH_LEFT = new BufferedImage[5], SLASH_RIGHT = new BufferedImage[5];
	public static BufferedImage[] DIE_LEFT = new BufferedImage[7], DIE_RIGHT = new BufferedImage[7];
	
	public static int playerRenderWidth = (int) (Launcher.WIDTH * 0.1171875), playerRenderHeight = playerRenderWidth * 30 / 50;
	public static int playerYDisplacement = (int) (CharacterGraphics.playerRenderHeight / 2 * GameState.zoom);
	
	public static void renderPlayer(Graphics2D g) {
		if (Player.state == Player.STATE_DEAD) {
			int index = (int) Toolbox.constrain((Game.frameCount - Player.deathFrame) / 5.0, 0, 6);
			g.drawImage(Player.facingRight ? DIE_RIGHT[index] : DIE_LEFT[index],
					Launcher.WIDTH / 2 - (int) (playerRenderWidth / 2 * GameState.zoom),
					Launcher.HEIGHT / 2 - (int) (playerRenderHeight / 2 * GameState.zoom), (int) (playerRenderWidth * GameState.zoom),
					(int) (playerRenderHeight * GameState.zoom), null);
		} else if (Player.action == Player.ACTION_SLASH) {
			g.drawImage(Player.facingRight ? SLASH_RIGHT[Player.slashImageIndex] : SLASH_LEFT[Player.slashImageIndex],
					Launcher.WIDTH / 2 - (int) (playerRenderWidth / 2 * GameState.zoom),
					Launcher.HEIGHT / 2 - (int) (playerRenderHeight / 2 * GameState.zoom), (int) (playerRenderWidth * GameState.zoom),
					(int) (playerRenderHeight * GameState.zoom), null);
		} else if (Player.state == Player.STATE_IDLE) {
			int frameLength = 13, shift = Game.frameCount % (4 * frameLength), index = 0;
			for (int i = 1; i <= 4; i++) {
				if (shift < i * frameLength) {
					index = i - 1;
					break;
				}
			}
			g.drawImage(Player.facingRight ? IDLE_RIGHT[index] : IDLE_LEFT[index],
					Launcher.WIDTH / 2 - (int) (playerRenderWidth / 2 * GameState.zoom),
					Launcher.HEIGHT / 2 - (int) (playerRenderHeight / 2 * GameState.zoom), (int) (playerRenderWidth * GameState.zoom),
					(int) (playerRenderHeight * GameState.zoom), null);
		} else if (Player.state == Player.STATE_RUNNING) {
			int frameLength = 6, shift = Game.frameCount % (6 * frameLength), index = 0;
			for (int i = 1; i <= 6; i++) {
				if (shift < i * frameLength) {
					index = i - 1;
					break;
				}
			}
			g.drawImage(Player.facingRight ? RUN_RIGHT[index] : RUN_LEFT[index],
					Launcher.WIDTH / 2 - (int) ((playerRenderWidth / 2 + (Player.facingRight ? 9 : -9)) * GameState.zoom),
					Launcher.HEIGHT / 2 - (int) (playerRenderHeight / 2 * GameState.zoom), (int) (playerRenderWidth * GameState.zoom),
					(int) (playerRenderHeight * GameState.zoom), null);
		}
	}
	
}
