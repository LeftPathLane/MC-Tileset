package com.github.leftpathlane.tileset;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
		for (Tile tile : tileList) {
			tile.setBlock(x, location.getBlockY(), z, material, data);
		}
	}

	public void removeTile(Location location) {
		tileList.removeIf(tile -> tile.equals(location));
	}

	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	public class Tile {
		private final int x, z;

		public void setBlock(int x, int y, int z, Material material, byte data) {
			Location location = new Location(world, (this.x << 4) + x, y, (this.z << 4) + z);
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
