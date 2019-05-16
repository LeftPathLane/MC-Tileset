package com.github.leftpathlane.tileset;

import org.bukkit.plugin.java.JavaPlugin;

public class TileSetPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new BlockListener(), this);
	}
}
