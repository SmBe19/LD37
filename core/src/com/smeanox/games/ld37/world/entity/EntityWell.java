package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.io.Sounds;
import com.smeanox.games.ld37.world.Hero;


public class EntityWell extends Entity {
	public EntityWell(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntityWell(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}


	@Override
	public String interact (Hero hero) {
		MapObject fire;
		while ((fire = Hero.findObjectByName(hero.gameWorld.level.get().map, "well")) != null){
			fire.setVisible(false);
		}
		while ((fire = Hero.findObjectByName(hero.gameWorld.level.get().map, "wellempty", false)) != null){
			fire.setVisible(true);
		}
		Sounds.watersplash.sound.play();
		return null;
	}
}
