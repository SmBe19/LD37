package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.world.GameWorld;
import com.smeanox.games.ld37.world.Hero;


public class EntitySilverCoin extends Entity {
	public EntitySilverCoin(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntitySilverCoin(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}

	@Override
	public String useItem(Hero hero, MapObject object) {
		if("barwoman".equals(object.getName())){
			hero.removeCurrentItemFromInventory();
			hero.inventory.add(hero.gameWorld.entityHashMap.get("mead"));
			hero.activeInventory = hero.inventory.size() - 1;
			hero.gameWorld.speechQueue.addLast(new GameWorld.Speech("I'll have a bottle of mead, please.", 0, 0, false, true));
			hero.gameWorld.speechQueue.addLast(new GameWorld.Speech("Very well. Here you go. Enjoy.", 12.5f, 7, false));
			return null;
		}
		return getDontDoThisString();
	}
}
