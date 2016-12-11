package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.world.GameWorld;
import com.smeanox.games.ld37.world.Hero;


public class EntityRattail extends Entity {
	public EntityRattail(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntityRattail(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}

	@Override
	public String useItem(Hero hero, MapObject object) {
		if("cauldron".equals(object.getName()) && hero.getVar("elixicon").length() > 0){
			hero.removeCurrentItemFromInventory();
			hero.inventory.add(hero.gameWorld.entityHashMap.get("blueelixir"));
			hero.activeInventory = hero.inventory.size() - 1;
			return null;
		}
		return getDontDoThisString();
	}
}
