package com.smeanox.games.ld37.io;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.smeanox.games.ld37.Consts;

public enum Textures {
	tiles("img/tiles.png"),
	castlebg("img/castle_bg.png"),
	font("img/font.png"),
	intro("img/intro_bg.png"),
	logo("img/logo.png"),
	dragon("img/dragon.png"),
	;

	public final Texture texture;

	Textures(String path) {
		texture = new Texture(Gdx.files.internal(path));
	}

	public TextureRegion getTextureRegion(int x, int y) {
		return getTextureRegion(x, y, Consts.TEX_SIZE, Consts.TEX_SIZE);
	}

	public TextureRegion getTextureRegion(int x, int y, int w, int h) {
		return getTextureRegion(x, y, w, h, Consts.TEX_SIZE);
	}

	public TextureRegion getTextureRegion(int x, int y, int w, int h, int align) {
		return new TextureRegion(texture, x * align, y * align, w, h);
	}
}
