package com.smeanox.games.ld37.world.entity;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.smeanox.games.ld37.world.Hero;

public class Entity {
	public final String id;
	public final TextureRegion textureRegion;

	public Entity(String id, TextureRegion textureRegion) {
		this.id = id;
		this.textureRegion = textureRegion;
	}

	public String interact(Hero hero){
		return null;
	}

	public String useItem(Hero hero) {
		return null;
	}
}
