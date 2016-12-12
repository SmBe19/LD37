package com.smeanox.games.ld37.io;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public enum Level {
	lvl_intro("lvl/lvl_intro.tmx"),
	lvl_outro("lvl/lvl_outro.tmx"),
	testlvl("lvl/testlvl.tmx"),
	lvl_barn("lvl/lvl_barn.tmx"),
	lvl_cellar("lvl/lvl_cellar.tmx"),
	lvl_magelab("lvl/lvl_magelab.tmx"),
	lvl_palace("lvl/lvl_palace.tmx"),
	lvl_room51("lvl/lvl_room51.tmx"),
	lvl_room51_door("lvl/lvl_room51_door.tmx"),
	lvl_tavern("lvl/lvl_tavern.tmx"),
	lvl_tower("lvl/lvl_tower.tmx"),
	lvl_ward("lvl/lvl_ward.tmx"),
	;

	public TiledMap map;
	private String path;

	Level(String path) {
		this.path = path;
		map = new TmxMapLoader().load(path);
	}

	public void reload(){
		if (map != null) {
			map.dispose();
		}
		map = new TmxMapLoader().load(path);
	}
}
