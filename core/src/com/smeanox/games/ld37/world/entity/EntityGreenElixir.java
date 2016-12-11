package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.world.Hero;


public class EntityGreenElixir extends Entity {
	public EntityGreenElixir(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntityGreenElixir(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}

	@Override
	public String useItem(Hero hero, MapObject object) {
		if("bodyguard".equals(object.getName())){
			hero.removeCurrentItemFromInventory();
			MapObject fire;
			while ((fire = Hero.findObjectByName(hero.gameWorld.level.get().map, "bodyguard")) != null){
				fire.setVisible(false);
			}
			while ((fire = Hero.findObjectByName(hero.gameWorld.level.get().map, "bodyguard_frog", false)) != null){
				fire.setVisible(false);
			}
			hero.setVar("greenelixir", "1");
			return null;
		}
		return getDontDoThisString();
	}
}
