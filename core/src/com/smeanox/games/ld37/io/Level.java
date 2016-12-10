package com.smeanox.games.ld37.io;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public enum Level {
	testlvl("lvl/testlvl.tmx"),
	;

	public final TiledMap map;

	Level(String path) {
		map = new TmxMapLoader().load(path);
	}
}
