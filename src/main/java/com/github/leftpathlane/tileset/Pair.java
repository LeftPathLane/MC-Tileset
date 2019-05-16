package com.github.leftpathlane.tileset;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;

@Getter
@RequiredArgsConstructor
public class Pair {
	private final int x, z;

	public Pair(Chunk chunk) {
		this.x = chunk.getX();
		this.z = chunk.getZ();
	}

	@Override
	public int hashCode() {
		return x * 7 + z;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Chunk) {
			return ((Chunk) obj).getX() == x && ((Chunk) obj).getZ() == z;
		} else if (obj instanceof Pair) {
			return ((Pair) obj).x == x && ((Pair) obj).z == z;
		}
		return false;
	}
}
