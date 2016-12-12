package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.world.Hero;


public class EntityBlueElixir extends Entity {
	public EntityBlueElixir(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntityBlueElixir(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}

	@Override
	public String useItem(Hero hero, MapObject object) {
		if("king".equals(object.getName())){
			if(hero.getVar("greenelixir").length() == 0){
				return "The bodyguard will notice";
			}
			hero.removeCurrentItemFromInventory();
			MapObject fire;
			while ((fire = Hero.findObjectByName(hero.gameWorld.level.get().map, "king")) != null){
				fire.setVisible(false);
			}
			while ((fire = Hero.findObjectByName(hero.gameWorld.level.get().map, "sleepingking", false)) != null){
				fire.setVisible(false);
			}
			hero.setVar("sleepingking", "1");
			return null;
		}
		return getWrongPersonString();
	}
}
