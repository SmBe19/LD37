package com.smeanox.games.ld37.io;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.smeanox.games.ld37.Consts;

public enum Font {
	mango("ABCDEFGHIJKLMNOPQRSTUVWXYZ.,!?\"'abcdefghijklmnopqrstuvwxyz()+-/\\0123456789 ",
			Textures.font.texture, 6, 8),
	;

	public String setorder;
	public Texture texture;
	public int glyphWidth, glyphHeight;
	int glyphPerLine;

	Font(String setorder, Texture texture, int glyphWidth, int glyphHeight) {
		this.setorder = setorder;
		this.texture = texture;
		this.glyphWidth = glyphWidth;
		this.glyphHeight = glyphHeight;
		glyphPerLine = texture.getWidth() / glyphWidth;
	}

	public void draw(SpriteBatch batch, String text, float x, float y) {
		float x1 = x, y1 = y - glyphHeight * Consts.FONT_SIZE;
		String[] split = text.split("\n");
		for (String line : split) {
			for (char c : line.toCharArray()) {
				int idx = setorder.indexOf(c);
				if(idx >= 0){
					batch.draw(texture, x1, y1, glyphWidth * Consts.FONT_SIZE, glyphHeight * Consts.FONT_SIZE,
							(idx % glyphPerLine) * glyphWidth, (idx / glyphPerLine) * glyphHeight, glyphWidth, glyphHeight, false, false);
				}
				x1 += glyphWidth * Consts.FONT_SIZE;
			}
			x1 = x;
			y1 -= glyphHeight * Consts.FONT_SIZE * Consts.FONT_LINE_SPACING;
		}
	}

	public float getWidth(String text) {
		float width = 0;
		String[] split = text.split("\n");
		for (String line : split) {
			width = Math.max(width, line.length() * glyphWidth * Consts.FONT_SIZE);
		}
		return width;
	}

	public float getHeight(String text) {
		String[] split = text.split("\n");
		return ((split.length - 1) * Consts.FONT_LINE_SPACING + 1) * glyphHeight * Consts.FONT_SIZE;
	}

	public float getLineHeight(){
		return glyphHeight * Consts.FONT_SIZE;
	}
}
