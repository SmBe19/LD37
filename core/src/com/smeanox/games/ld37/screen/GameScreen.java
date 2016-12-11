package com.smeanox.games.ld37.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.io.Font;
import com.smeanox.games.ld37.io.Level;
import com.smeanox.games.ld37.io.Textures;
import com.smeanox.games.ld37.world.GameWorld;
import com.smeanox.games.ld37.world.ObservableValue;

import java.util.Observable;
import java.util.Observer;

public class GameScreen implements Screen {

	private final SpriteBatch spriteBatch;
	private final OrthographicCamera camera, guicamera;
	private MyMapRenderer mapRenderer;
	private final GameWorld gameWorld;
	private float fadeProgress;
	private float fadeStart;
	private boolean isDark;

	private final TextureRegion fadeTexture;
	private final TextureRegion inventoryBackground;
	private final TextureRegion[] speechBubble;

	public GameScreen(Level level) {
		spriteBatch = new SpriteBatch();
		camera = new OrthographicCamera();
		guicamera = new OrthographicCamera();
		mapRenderer = null;

		fadeTexture = Textures.tiles.getTextureRegion(10, 3);
		inventoryBackground = Textures.tiles.getTextureRegion(14, 13, 2 * Consts.TEX_SIZE, 2 * Consts.TEX_SIZE);
		speechBubble = new TextureRegion[]{
				Textures.tiles.getTextureRegion(29, 0, 8, 8, 8),
				Textures.tiles.getTextureRegion(30, 0, 8, 8, 8),
				Textures.tiles.getTextureRegion(31, 0, 8, 8, 8),
				Textures.tiles.getTextureRegion(29, 1, 8, 8, 8),
				Textures.tiles.getTextureRegion(30, 1, 8, 8, 8),
				Textures.tiles.getTextureRegion(31, 1, 8, 8, 8),
				Textures.tiles.getTextureRegion(29, 2, 8, 8, 8),
				Textures.tiles.getTextureRegion(30, 2, 8, 8, 8),
				Textures.tiles.getTextureRegion(31, 2, 8, 8, 8),
				Textures.tiles.getTextureRegion(28, 0, 8, 16, 8),
				Textures.tiles.getTextureRegion(28, 2, 8, 16, 8),
		};

		gameWorld = new GameWorld();
		gameWorld.level.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				mapRenderer = new MyMapRenderer(((ObservableValue<Level>) o).get().map, Consts.UNIT_SCALE, spriteBatch, gameWorld);
			}
		});
		gameWorld.fadeOut.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				fadeStart = fadeProgress = ((ObservableValue<Float>) o).get();
				isDark = false;
			}
		});
		gameWorld.fadeIn.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				fadeStart = fadeProgress = -((ObservableValue<Float>) o).get();
				isDark = false;
			}
		});
		gameWorld.loadLevel(level, "main");
	}

	@Override
	public void show() {

	}

	private void update(float delta){
		gameWorld.update(delta);

		float left, right, top, bottom;
		left = camera.position.x - camera.viewportWidth / 2;
		right = left + camera.viewportWidth;
		bottom = camera.position.y - camera.viewportHeight / 2;
		top = bottom + camera.viewportHeight;

		if (gameWorld.hero.x - left < Consts.CAMERA_BORDER_X) {
			camera.position.x = Math.max(gameWorld.hero.x - Consts.CAMERA_BORDER_X + camera.viewportWidth / 2, camera.viewportWidth / 2);
		} else if (right - gameWorld.hero.x < Consts.CAMERA_BORDER_X) {
			camera.position.x = Math.min(gameWorld.hero.x + Consts.CAMERA_BORDER_X - camera.viewportWidth / 2,
					gameWorld.level.get().map.getProperties().get("width", 0, Integer.class) - camera.viewportWidth / 2);
		}
		if (gameWorld.hero.y - bottom < Consts.CAMERA_BORDER_Y) {
			camera.position.y = Math.max(gameWorld.hero.y - Consts.CAMERA_BORDER_Y + camera.viewportHeight / 2, camera.viewportHeight / 2);
		} else if (top - gameWorld.hero.y < Consts.CAMERA_BORDER_Y) {
			camera.position.y = Math.min(gameWorld.hero.y + Consts.CAMERA_BORDER_Y - camera.viewportHeight / 2,
					gameWorld.level.get().map.getProperties().get("height", 0, Integer.class) - camera.viewportHeight / 2);
		}
	}

	private void drawDark(float alpha) {
		spriteBatch.setColor(1, 1, 1, alpha);
		spriteBatch.begin();
		spriteBatch.draw(fadeTexture, 0, 0, guicamera.viewportWidth, guicamera.viewportHeight);
		spriteBatch.end();
		spriteBatch.setColor(1, 1, 1, 1);
	}

	private void drawGUI(float delta) {
		spriteBatch.begin();
		if (gameWorld.inventoryVisible && gameWorld.hero.inventory.size() > 0) {
			spriteBatch.draw(inventoryBackground, (guicamera.viewportWidth - 2 * Consts.INVENTORY_ACTIVE_SIZE) / 2, 0,
					2 * Consts.INVENTORY_ACTIVE_SIZE, 2 * Consts.INVENTORY_ACTIVE_SIZE);
			spriteBatch.draw(gameWorld.hero.inventory.get(gameWorld.hero.activeInventory).textureRegion,
					(guicamera.viewportWidth - Consts.INVENTORY_ACTIVE_SIZE) / 2, Consts.INVENTORY_ACTIVE_SIZE / 2,
					Consts.INVENTORY_ACTIVE_SIZE, Consts.INVENTORY_ACTIVE_SIZE);
		}
		spriteBatch.end();
	}

	private void drawSpeechBubble(String text, float x, float y, float ax, boolean think, float alpha) {
		float width = Font.mango.getWidth(text), height = Font.mango.getHeight(text);
		y += 2 * Consts.SPEECH_BUBBLE_SIZE;
		x -= width / 2;
		x = Math.max(x, camera.position.x - camera.viewportWidth / 2 + 2 * Consts.SPEECH_BUBBLE_SIZE);

		spriteBatch.setColor(1, 1, 1, alpha);
		spriteBatch.draw(speechBubble[0], x - Consts.SPEECH_BUBBLE_SIZE, y + height, Consts.SPEECH_BUBBLE_SIZE, Consts.SPEECH_BUBBLE_SIZE);
		spriteBatch.draw(speechBubble[1], x, y + height, width, Consts.SPEECH_BUBBLE_SIZE);
		spriteBatch.draw(speechBubble[2], x + width, y + height, Consts.SPEECH_BUBBLE_SIZE, Consts.SPEECH_BUBBLE_SIZE);
		spriteBatch.draw(speechBubble[3], x - Consts.SPEECH_BUBBLE_SIZE, y, Consts.SPEECH_BUBBLE_SIZE, height);
		spriteBatch.draw(speechBubble[4], x, y, width, height);
		spriteBatch.draw(speechBubble[5], x + width, y, Consts.SPEECH_BUBBLE_SIZE, height);
		spriteBatch.draw(speechBubble[6], x - Consts.SPEECH_BUBBLE_SIZE, y - Consts.SPEECH_BUBBLE_SIZE, Consts.SPEECH_BUBBLE_SIZE, Consts.SPEECH_BUBBLE_SIZE);
		spriteBatch.draw(speechBubble[7], x, y - Consts.SPEECH_BUBBLE_SIZE, width, Consts.SPEECH_BUBBLE_SIZE);
		spriteBatch.draw(speechBubble[8], x + width, y - Consts.SPEECH_BUBBLE_SIZE, Consts.SPEECH_BUBBLE_SIZE, Consts.SPEECH_BUBBLE_SIZE);
		spriteBatch.draw(speechBubble[think ? 10 : 9], ax - Consts.SPEECH_BUBBLE_SIZE / 2, y - 2 * Consts.SPEECH_BUBBLE_SIZE, Consts.SPEECH_BUBBLE_SIZE, 2 * Consts.SPEECH_BUBBLE_SIZE);

		spriteBatch.setColor(0, 0, 0, alpha);
		Font.mango.draw(spriteBatch, text, x, y + height);
		spriteBatch.setColor(1, 1, 1, 1);
	}

	private void drawSpeechBubble(String text, float x, float y, boolean think, float alpha) {
		drawSpeechBubble(text, x - Font.mango.getWidth(text)/3, y, x, think, alpha);
	}

	private void drawSpeechBubbles() {
		spriteBatch.begin();
		for (GameWorld.Speech speech : gameWorld.speeches) {
			float progress = speech.age < Consts.SPEECH_BUBBLE_ANIM_DURATION ? (speech.age / Consts.SPEECH_BUBBLE_ANIM_DURATION) : 1;
			progress = speech.age > speech.duration - Consts.SPEECH_BUBBLE_ANIM_DURATION
					? (speech.duration - speech.age) / Consts.SPEECH_BUBBLE_ANIM_DURATION : progress;
			float off = (1 - progress) * Consts.SPEECH_BUBBLE_ANIM_OFFSET;
			drawSpeechBubble(speech.text, speech.x, speech.y + off, speech.think, progress);
		}
		spriteBatch.setColor(1, 1, 1, 1);
		spriteBatch.end();
	}

	private void draw(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		guicamera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		mapRenderer.setView(camera);
		mapRenderer.render();

		drawSpeechBubbles();

		spriteBatch.setProjectionMatrix(guicamera.combined);
		drawGUI(delta);
		if (fadeProgress != 0) {
			float alpha = fadeStart < 0 ? fadeProgress / fadeStart : (fadeStart - fadeProgress) / fadeStart;
			drawDark(alpha);
			float oldSign = Math.signum(fadeStart);
			fadeProgress += delta * -oldSign;
			if (oldSign != Math.signum(fadeProgress)) {
				fadeProgress = 0;
				if (oldSign < 0) {
					gameWorld.fadeOut.set(0.f);
					gameWorld.fadeIn.set(0.f);
				}
				isDark = oldSign > 0;
			}
		}
		if (isDark) {
			drawDark(1);
		}
	}

	@Override
	public void render(float delta) {
		update(delta);
		draw(delta);
	}

	@Override
	public void resize(int width, int height) {
		float aspect = ((float) width) / height;
		float newheight = Consts.HEIGHT * Consts.SCALE;
		float newwidth = aspect * newheight;
		camera.setToOrtho(false, newwidth, newheight);
		guicamera.setToOrtho(false, newwidth, newheight);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		if (mapRenderer != null) {
			mapRenderer.dispose();
		}
		for (Textures tex : Textures.values()) {
			tex.texture.dispose();
		}
		for (Level level : Level.values()) {
			level.map.dispose();
		}
	}
}
