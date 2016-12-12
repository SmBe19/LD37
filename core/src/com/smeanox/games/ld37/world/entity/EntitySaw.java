package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.world.Hero;


public class EntitySaw extends Entity {
	public EntitySaw(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntitySaw(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}

	@Override
	public String useItem(Hero hero, MapObject object) {
		if("rat".equals(object.getName())){
			return "It's too fast.";
		} else if ("ratcheese".equals(object.getName())) {
			hero.inventory.add(hero.gameWorld.entityHashMap.get("rattail"));
			hero.activeInventory = hero.inventory.size() - 1;
			object.setVisible(false);
			Hero.findObjectByName(hero.gameWorld.level.get().map, "ratdead", false).setVisible(true);
			return null;
		} else if ("king".equals(object.getName())) {
			if(hero.getVar("greenelixir").length() == 0){
				return "The bodyguard will notice";
			}
			if(hero.getVar("sleepingking").length() == 0){
				return "I can't do this if he is awake";
			}
			hero.inventory.add(hero.gameWorld.entityHashMap.get("kingshand"));
			hero.activeInventory = hero.inventory.size() - 1;
			return null;
		}
		return getDontDoThisString();
	}
}
