package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.world.GameWorld;
import com.smeanox.games.ld37.world.Hero;


public class EntityGoldCoin extends Entity {
	public EntityGoldCoin(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntityGoldCoin(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}

	@Override
	public String useItem(Hero hero, MapObject object) {
		if("boy".equals(object.getName())){
			hero.removeCurrentItemFromInventory();
			hero.inventory.add(hero.gameWorld.entityHashMap.get("elixicon"));
			hero.setVar("elixicon", "1");
			hero.gameWorld.entityHashMap.get("greenelixir").examineText = "Transforms a person into a frog\nfor a few minutes. Or years.\nSomething like that. I can\nuse it by throwing it at someone.";
			hero.activeInventory = hero.inventory.size() - 1;
			hero.gameWorld.speeches.add(new GameWorld.Speech("It was a pleasure doing\nbusiness with you,\nhere's your stupid old book.", 40, 8, false));
			object.getProperties().put(Consts.PROP_ACTIVE, false);
			return null;
		}
		return getDontDoThisString();
	}
}
