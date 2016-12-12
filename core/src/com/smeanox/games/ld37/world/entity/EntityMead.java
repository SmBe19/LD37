package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.utils.TimeUtils;
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
			MapObject fire;
			while ((fire = Hero.findObjectByName(hero.gameWorld.level.get().map, "drunkguard")) != null){
				fire.setVisible(false);
			}
			while ((fire = Hero.findObjectByName(hero.gameWorld.level.get().map, "drunkguardsleep", false)) != null){
				fire.setVisible(true);
				fire.getProperties().put(Consts.PROP_ANIMATIONSTART, TimeUtils.millis());
			}
			return null;
		}
		return getDontDoThisString();
	}
}
