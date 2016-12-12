package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.world.Hero;


public class EntityCheese extends Entity {
	public EntityCheese(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntityCheese(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}

	@Override
	public String useItem(Hero hero, MapObject object) {
		if("rat".equals(object.getName())){
			hero.removeCurrentItemFromInventory();
			object.setVisible(false);
			Hero.findObjectByName(hero.gameWorld.level.get().map, "ratcheese", false).setVisible(true);
			return null;
		}
		return getDontDoThisString();
	}
}
