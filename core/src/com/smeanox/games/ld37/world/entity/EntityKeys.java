package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.world.GameWorld;
import com.smeanox.games.ld37.world.Hero;


public class EntityKeys extends Entity {
	public EntityKeys(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntityKeys(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}

	@Override
	public String useItem(Hero hero, MapObject object) {
		if("barndoor".equals(object.getName())){
			hero.removeCurrentItemFromInventory();
			Hero.findObjectByName(hero.gameWorld.level.get().map, "exit_barn").getProperties().put(Consts.PROP_ACTIVE, true);
			return null;
		}
		return getDontDoThisString();
	}
}
