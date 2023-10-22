package tiles;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import entities.WheatPatch;

public class Tiles {
	
	// Water
	public static BufferedImage TEXTURE_WATER_BACKGROUND, TEXTURE_WATER_RIPPLE_1, TEXTURE_WATER_RIPPLE_2;
	public static final int TILE_WATER = 0, TILE_TYPE_WATER = 0;
	
	// Dirt
	public static BufferedImage TEXTURE_DIRT;
	public static final int TILE_DIRT = 1, TILE_TYPE_DIRT = 1;
	
	// Grass
	public static BufferedImage TEXTURE_GRASS_1, TEXTURE_GRASS_2, TEXTURE_GRASS_3;
	public static final int TILE_GRASS_1 = 2, TILE_GRASS_2 = 3, TILE_GRASS_3 = 4, TILE_TYPE_GRASS = 2;
	
	public static int radius = 100;

	public static Map<String, Integer> map = new HashMap<String, Integer>();
	public static Map<String, Integer> typeMap = new HashMap<String, Integer>();
	public static Map<String, WheatPatch> wheatMap = new HashMap<String, WheatPatch>();

	public static String hash(int x, int y) {
		return x + "," + y;
	}

	public static void initializeTiles() {
		try {
			Scanner s = new Scanner(new File("Tile Data"));
			long lines = Files.lines(Paths.get("Tile Data")).count();
			radius = (int) (lines - 1) / 2;
			for (int y = -radius; y <= radius; y++) {
				for (int x = -radius; x <= radius; x++) {
					int specificTile = s.nextInt(), generalTile = 0;
					map.put(hash(x, y), specificTile);
					if (specificTile == TILE_WATER) {
						generalTile = TILE_TYPE_WATER;
					} else if (specificTile == TILE_DIRT) {
						generalTile = TILE_TYPE_DIRT;
					} else if (specificTile >= TILE_GRASS_1 && specificTile <= TILE_GRASS_3) {
						generalTile = TILE_TYPE_GRASS;
					}
					typeMap.put(hash(x, y), generalTile);
				}
			}
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int fetchGroundTexture(int x, int y) {
		if (!map.containsKey(hash(x, y))) {
			return TILE_WATER;
		}

		int tile = map.get(hash(x, y));
		switch (tile) {
		case TILE_GRASS_1:
			return TILE_GRASS_1;
		case TILE_GRASS_2:
			return TILE_GRASS_2;
		case TILE_GRASS_3:
			return TILE_GRASS_3;
		case TILE_WATER:
			return TILE_WATER;
		case TILE_DIRT:
			return TILE_DIRT;
		default:
			return TILE_WATER;
		}

	}
	
	public static WheatPatch getWheatPatch(int x, int y) {
		String hash = hash(x, y);
		if (wheatMap.containsKey(hash)) {
			return wheatMap.get(hash);
		} else {
			return null;
		}
	}
	
	public static void addWheat(int x, int y, WheatPatch patch) {
		String hash = hash(x, y);
		wheatMap.put(hash, patch);
	}
	
	public static void removeWheat(int x, int y) {
		String hash = hash(x, y);
		wheatMap.remove(hash);
	}

}
