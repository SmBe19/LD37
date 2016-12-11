package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.world.GameWorld;
import com.smeanox.games.ld37.world.Hero;


public class EntityStraw extends Entity {
	public EntityStraw(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntityStraw(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}

	@Override
	public String useItem(Hero hero, MapObject object) {
		if("flamearrow".equals(object.getName())){
			hero.removeCurrentItemFromInventory();
			hero.inventory.add(hero.gameWorld.entityHashMap.get("burningstrawman"));
			return null;
		}
		return getDontDoThisString();
	}
}
