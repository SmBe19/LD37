package com.smeanox.games.ld37.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.io.Level;
import com.smeanox.games.ld37.world.Hero;


public class EntityBurningStraw extends Entity {
	public EntityBurningStraw(String id, TextureRegion textureRegion) {
		super(id, textureRegion);
	}

	public EntityBurningStraw(String id, TextureRegion textureRegion, String examineText) {
		super(id, textureRegion, examineText);
	}

	@Override
	public String useItem(Hero hero, MapObject object) {
		if("barnfire".equals(object.getName())){
			if(hero.getVar("bucket").length() > 0){
				MapObject fire;
				while ((fire = Hero.findObjectByName(hero.gameWorld.level.get().map, "fire", false)) != null){
					fire.setVisible(true);
				}
				while ((fire = Hero.findObjectByName(hero.gameWorld.level.get().map, "guardfire", false)) != null){
					fire.setVisible(true);
				}
				Hero.findObjectByName(Level.lvl_palace.map, "exit2").getProperties().put(Consts.PROP_ACTIVE, true);
				Hero.findObjectByName(Level.lvl_tower.map, "exit2").getProperties().put(Consts.PROP_ACTIVE, true);
				while ((fire = Hero.findObjectByName(Level.lvl_palace.map, "guard1")) != null){
					fire.setVisible(false);
				}
				while ((fire = Hero.findObjectByName(Level.lvl_palace.map, "guard2")) != null){
					fire.setVisible(false);
				}

				hero.removeCurrentItemFromInventory();
				Hero.findObjectByName(hero.gameWorld.level.get().map, "exit_barn").getProperties().put(Consts.PROP_ACTIVE, true);
			} else {
				return "As long as the well is still operational\nthe guards will just extinguish it";
			}
			return null;
		}
		return getDontDoThisString();
	}
}
