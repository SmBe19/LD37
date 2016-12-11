package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.world.GameWorld;
import com.smeanox.games.ld37.world.Hero;


public class EntityMead extends Entity {
	public EntityMead(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntityMead(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}

	@Override
	public String useItem(Hero hero, MapObject object) {
		if("drunkguard".equals(object.getName())){
			hero.removeCurrentItemFromInventory();
			hero.inventory.add(hero.gameWorld.entityHashMap.get("cheese"));
			hero.activeInventory = hero.inventory.size() - 1;
			hero.gameWorld.speeches.add(new GameWorld.Speech("Sha-Shank youuuuu.", 42, 36, false));
			object.getProperties().put(Consts.PROP_ONNOINTERACT + "_0", "say He doesn't seem very responsive...");
			return null;
		}
		return getDontDoThisString();
	}
}
