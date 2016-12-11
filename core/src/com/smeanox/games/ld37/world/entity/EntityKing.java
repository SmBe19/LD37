package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.world.Hero;


public class EntityKing extends Entity {
	public EntityKing(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntityKing(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}

	@Override
	public String useItem(Hero hero, MapObject object) {
		if("handscanner".equals(object.getName())){
			hero.removeCurrentItemFromInventory();
			Hero.findObjectByName(hero.gameWorld.level.get().map, "exit2").getProperties().put(Consts.PROP_ACTIVE, true);
			return null;
		}
		return getDontDoThisString();
	}
}
