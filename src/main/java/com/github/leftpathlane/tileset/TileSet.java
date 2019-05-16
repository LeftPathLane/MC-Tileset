package com.github.leftpathlane.tileset;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class TileSet {

	private final World world;

	private final List<Tile> tileList = new ArrayList<Tile>();

	public int tileCount() {
		return tileList.size();
	}

	public boolean addTile(Location location) {
		int x = location.getBlockX() >> 4;
		int z = location.getBlockZ() >> 4;
		for (Tile tile : tileList) {
			if (tile.getX() == x && tile.getZ() == z) return false;
		}
		tileList.add(new Tile(x, z));
		return true;
	}

	public void setBlock(Location location, Material material, byte data) {
		int x = location.getBlockX() & 15;
		int z = location.getBlockZ() & 15;
		Tile thisTile = getTile(location);
		if (thisTile == null) return;
		Pair vals = thisTile.transformation.normalise(x, z);
		for (Tile tile : tileList) {
			tile.setBlock(vals.getX(), location.getBlockY(), vals.getZ(), material, data);
		}
	}

	public void removeTile(Location location) {
		tileList.removeIf(tile -> tile.equals(location));
	}

	public Tile getTile(Location location) {
		for (Tile tile : tileList) {
			if (tile.equals(location)) return tile;
		}
		return null;
	}

	public enum TileTransformation {
		NORMAL,
		ROTATE_90,
		ROTATE_180,
		ROTATE_270;

		public TileTransformation next() {
			int ord = this.ordinal() + 1;
			if (ord >= values().length) return values()[0];
			return values()[ord];
		}

		public Pair transform(int x, int z) {
			switch (this) {
				case ROTATE_90:
					return new Pair(15 - z, x);
				case ROTATE_180:
					return new Pair(15 - x, 15 - z);
				case ROTATE_270:
					return new Pair(z, 15 - x);
				default:
					return new Pair(x, z);
			}
		}

		public Pair normalise(int x, int z) {
			switch (this) {
				case ROTATE_90:
					return new Pair(z, 15 - x);
				case ROTATE_180:
					return new Pair(15 - x, 15 - z);
				case ROTATE_270:
					return new Pair(15 - z, x);
				default:
					return new Pair(x, z);
			}
		}
	}

	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	public class Tile {
		private final int x, z;

		@Setter
		private TileTransformation transformation = TileTransformation.NORMAL;

		public void setBlock(int x, int y, int z, Material material, byte data) {
			Pair vals = transformation.transform(x, z);
			Location location = new Location(world, (this.x << 4) + vals.getX(), y, (this.z << 4) + vals.getZ());
			Block block = world.getBlockAt(location);
			block.setType(material);
			block.setData(data);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Tile) {
				return ((Tile) obj).z == z && ((Tile) obj).x == x;
			} else if (obj instanceof Location) {
				return ((Location) obj).getBlockX() >> 4 == x && ((Location) obj).getBlockZ() >> 4 == z;
			}
			return false;
		}
	}
}
