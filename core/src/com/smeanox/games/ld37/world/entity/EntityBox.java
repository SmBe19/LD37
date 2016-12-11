package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.io.Level;
import com.smeanox.games.ld37.world.GameWorld;
import com.smeanox.games.ld37.world.Hero;


public class EntityBox extends Entity {
	public EntityBox(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntityBox(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}

	@Override
	public String interact(Hero hero) {
		hero.gameWorld.loadLevel(Level.lvl_outro, "main");
		return "Let's go";
	}
}
