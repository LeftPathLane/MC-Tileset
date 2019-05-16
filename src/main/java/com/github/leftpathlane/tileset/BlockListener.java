package com.github.leftpathlane.tileset;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockListener implements Listener {

	private Map<UUID, TileSet> uuidTileSetMap = new HashMap<>();
	private Map<Pair, TileSet> tileSetMap = new HashMap<>();

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getItem() == null) return;
			if (event.getItem().getType() == Material.IRON_INGOT) {
				event.setCancelled(true);
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					TileSet tileSet = uuidTileSetMap.get(event.getPlayer().getUniqueId());
					if (tileSet == null) {
						event.getPlayer().sendMessage("You do not have a tileset open ");
						return;
					}
					if (tileSet.addTile(event.getClickedBlock().getLocation())) {
						event.getPlayer().sendMessage("Added chunk to tileset " + tileSet.tileCount());
						tileSetMap.put(new Pair(event.getClickedBlock().getChunk()), tileSet);
					} else {
						event.getPlayer().sendMessage("Could not add chunk to tileset " + tileSet.tileCount());
					}
				} else {
					TileSet tileSet = tileSetMap.computeIfAbsent(new Pair(event.getClickedBlock().getChunk()), chunk -> {
						TileSet set = new TileSet(event.getClickedBlock().getChunk().getWorld());
						set.addTile(event.getClickedBlock().getLocation());
						event.getPlayer().sendMessage("Created a new TileSet " + set.tileCount());
						return set;
					});
					if (tileSet.tileCount() > 1)
						event.getPlayer().sendMessage("Loaded a tileset " + tileSet.tileCount());
					uuidTileSetMap.put(event.getPlayer().getUniqueId(), tileSet);
				}
			} else if (event.getItem().getType() == Material.GOLD_INGOT) {
				event.setCancelled(true);
				TileSet set = tileSetMap.remove(new Pair(event.getClickedBlock().getChunk()));
				if (set != null) {
					set.removeTile(event.getClickedBlock().getLocation());
					event.getPlayer().sendMessage("Removed chunk from tileset " + set.tileCount());
				} else {
					event.getPlayer().sendMessage("Could nto find tileset associated with that chunk");
				}
			} else if (event.getItem().getType() == Material.DIAMOND) {
				TileSet set = tileSetMap.get(new Pair(event.getClickedBlock().getChunk()));
				if (set == null) return;
				TileSet.Tile tile = set.getTile(event.getClickedBlock().getLocation());
				if (tile == null) return;
				event.setCancelled(true);
				tile.setTransformation(tile.getTransformation().next());
				event.getPlayer().sendMessage(tile.getTransformation().name());
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		TileSet tileSet = tileSetMap.get(new Pair(event.getBlock().getChunk()));
		if (tileSet == null) return;
		tileSet.setBlock(event.getBlock().getLocation(), event.getBlock().getType(), event.getBlock().getData());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		TileSet tileSet = tileSetMap.get(new Pair(event.getBlock().getChunk()));
		if (tileSet == null) return;
		tileSet.setBlock(event.getBlock().getLocation(), Material.AIR, (byte) 0);
	}
}
