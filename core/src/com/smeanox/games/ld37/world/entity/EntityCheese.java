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
			object.getProperties().put(Consts.PROP_ACTIVE, false);
			return null;
		}
		return getDontDoThisString();
	}
}
