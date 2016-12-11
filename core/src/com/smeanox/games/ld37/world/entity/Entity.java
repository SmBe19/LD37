package com.smeanox.games.ld37.world.entity;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.smeanox.games.ld37.world.Hero;

public class Entity {
	public final String id;
	public final TextureRegion textureRegion;
	public String examineText;
	private static int lastDontDoThisString = 0;
	private static int lastWrongPersonString = 0;

	public Entity(String id, TextureRegion textureRegion) {
		this.id = id;
		this.textureRegion = textureRegion;
	}

	public Entity(String id, TextureRegion textureRegion, String examineText) {
		this.id = id;
		this.textureRegion = textureRegion;
		this.examineText = examineText;
	}

	public String interact(Hero hero){
		return null;
	}

	public String useItem(Hero hero, MapObject object) {
		return getDontDoThisString();
	}

	protected String getDontDoThisString() {
		String dontdothisstring = Hero.dontdothisstrings[lastDontDoThisString];
		lastDontDoThisString++;
		lastDontDoThisString %= Hero.dontdothisstrings.length;
		return dontdothisstring;
	}

	protected String getWrongPersonString() {
		String dontdothisstring = Hero.wrongpersonstrings[lastWrongPersonString];
		lastWrongPersonString++;
		lastWrongPersonString %= Hero.wrongpersonstrings.length;
		return dontdothisstring;
	}
}
