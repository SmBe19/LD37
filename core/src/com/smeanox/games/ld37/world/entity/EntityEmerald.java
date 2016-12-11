package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.world.GameWorld;
import com.smeanox.games.ld37.world.Hero;


public class EntityEmerald extends Entity {
	public EntityEmerald(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntityEmerald(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}

	@Override
	public String useItem(Hero hero, MapObject object) {
		if("guard".equals(object.getName())){
			hero.removeCurrentItemFromInventory();
			hero.gameWorld.speeches.add(new GameWorld.Speech("It would seem like your presence in\nthis tower truly is a necessity.\nBut whatever happens to you,\nit is none of my concern.", 58, 33, false));
			object.getProperties().put(Consts.PROP_ONNOINTERACT + "_0", "qsayxy 58 33 No refunds.");
			Hero.findObjectByName(hero.gameWorld.level.get().map, "exit_tower").getProperties().put(Consts.PROP_ACTIVE, true);
			return null;
		}
		return getDontDoThisString();
	}
}
