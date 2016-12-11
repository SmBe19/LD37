package com.smeanox.games.ld37.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
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
	private int screenWidth, screenHeight;
	private float currentScale;
	public float cinematicProgress;
	public float cinematicAlpha;

	private final TextureRegion fadeTexture;
	private final TextureRegion inventoryBackground;
	private final TextureRegion[] speechBubble;

	private class Keyframe {
		float time;
		float scale;
		float centerx;
		float centery;
		float alpha;

		public Keyframe(float time, float scale, float centerx, float centery, float alpha) {
			this.time = time;
			this.scale = scale;
			this.centerx = centerx;
			this.centery = centery;
			this.alpha = alpha;
		}
	}

	private Keyframe[] keyframesIntro = new Keyframe[]{
			new Keyframe(0, 0.1f, 9, 12, 1),
			new Keyframe(2, 0.1f, 11f, 11.4f, 0),
			new Keyframe(4, 0.2f, 12, 8.75f, 0),
			new Keyframe(4.01f, 0.1f, 12, 8.75f, 0),
			new Keyframe(6, 0.1f, 13.5f, 7, 0),
			new Keyframe(12, 0.5f, 12, 7, 0),
			new Keyframe(18, 1, 12, 7, 0),
			new Keyframe(26, 1, 12, 7, 0),
			new Keyframe(30, 1, 12, 7, 1),
	};

	private Keyframe[] keyframes = null;

	public GameScreen(final Level level) {
		spriteBatch = new SpriteBatch();
		camera = new OrthographicCamera();
		guicamera = new OrthographicCamera();
		mapRenderer = null;
		currentScale = 1;

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
				TiledMap map = ((ObservableValue<Level>) o).get().map;
				mapRenderer = new MyMapRenderer(map, Consts.UNIT_SCALE, spriteBatch, gameWorld);
				setScale(map.getProperties().get(Consts.PROP_MAPSCALE, 1.f, Float.class));
				cinematicProgress = 0;
				if(((ObservableValue<Level>) o).get() == level.lvl_intro){
					initIntro();
				}
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

	private void initIntro(){
		keyframes = keyframesIntro;
		gameWorld.subtitles.addLast(new GameWorld.Speech("Once upon a time, there was a cute\nlittle castle on a hill.", 10));
		gameWorld.subtitles.addLast(new GameWorld.Speech("Unfortunately, that castle was attacked\nby an enemy army and is now under siege.", 10));
		gameWorld.subtitles.addLast(new GameWorld.Speech("It would seem that there is only one hope...", 6));
		gameWorld.subtitles.addLast(new GameWorld.Speech("Aaaaaah! I was hit!", 4));
	}

	@Override
	public void show() {

	}

	private void update(float delta){
		gameWorld.update(delta);

		if (gameWorld.cinematic) {
			return;
		}

		float left, right, top, bottom;
		left = camera.position.x - camera.viewportWidth / 2;
		right = left + camera.viewportWidth;
		bottom = camera.position.y - camera.viewportHeight / 2;
		top = bottom + camera.viewportHeight;

		float cameraBorderX = Consts.CAMERA_BORDER_X * currentScale;
		float cameraBorderY = Consts.CAMERA_BORDER_Y * currentScale;
		if (gameWorld.hero.x - left < cameraBorderX) {
			camera.position.x = Math.max(gameWorld.hero.x - cameraBorderX + camera.viewportWidth / 2, camera.viewportWidth / 2);
		} else if (right - gameWorld.hero.x < cameraBorderX) {
			camera.position.x = Math.min(gameWorld.hero.x + cameraBorderX - camera.viewportWidth / 2,
					gameWorld.level.get().map.getProperties().get("width", 0, Integer.class) - camera.viewportWidth / 2);
		}
		if (gameWorld.hero.y - bottom < cameraBorderY) {
			camera.position.y = Math.max(gameWorld.hero.y - cameraBorderY + camera.viewportHeight / 2, camera.viewportHeight / 2);
		} else if (top - gameWorld.hero.y < cameraBorderY) {
			camera.position.y = Math.min(gameWorld.hero.y + cameraBorderY - camera.viewportHeight / 2,
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
			spriteBatch.draw(inventoryBackground, (guicamera.viewportWidth - 2 * Consts.INVENTORY_ACTIVE_SIZE) / 2,
					guicamera.viewportHeight - 2*Consts.INVENTORY_ACTIVE_SIZE,
					2 * Consts.INVENTORY_ACTIVE_SIZE, 2 * Consts.INVENTORY_ACTIVE_SIZE);
			spriteBatch.draw(gameWorld.hero.inventory.get(gameWorld.hero.activeInventory).textureRegion,
					(guicamera.viewportWidth - Consts.INVENTORY_ACTIVE_SIZE) / 2,
					guicamera.viewportHeight - Consts.INVENTORY_ACTIVE_SIZE * 1.5f,
					Consts.INVENTORY_ACTIVE_SIZE, Consts.INVENTORY_ACTIVE_SIZE);
		}
		drawSubtitles(delta);
		spriteBatch.end();
	}

	private void drawSubtitles(float delta) {
		if (gameWorld.subtitles.size == 0) {
			return;
		}
		GameWorld.Speech subtitle = gameWorld.subtitles.first();
		String text = subtitle.text;
		float width = Font.mango.getWidth(text), height = Font.mango.getHeight(text);
		float progress = subtitle.age < Consts.SPEECH_BUBBLE_ANIM_DURATION ? (subtitle.age / Consts.SPEECH_BUBBLE_ANIM_DURATION) : 1;
		progress = subtitle.age > subtitle.duration - Consts.SPEECH_BUBBLE_ANIM_DURATION
				? (subtitle.duration - subtitle.age) / Consts.SPEECH_BUBBLE_ANIM_DURATION : progress;
		spriteBatch.setColor(1, 1, 1, progress);
		Font.mango.draw(spriteBatch, text, (guicamera.viewportWidth - width) / 2, Consts.SUBTITLES_OFFSET_Y + height);
		spriteBatch.setColor(1, 1, 1, 1);
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
		if(gameWorld.cinematic){
			drawDark(cinematicAlpha);
		}
	}

	protected float interpolate(float a, float b, float progress) {
		return (a * (1 - progress)) + (b * progress);
	}

	protected void updateCinematic(float delta) {
		if (keyframes == null) {
			return;
		}

		cinematicProgress += delta;

		int aidx = -1;
		for(int i = 0; i < keyframes.length; i++) {
			if(keyframes[i].time > cinematicProgress){
				aidx = i - 1;
				break;
			}
		}
		if (aidx < 0 || aidx >= keyframes.length - 1) {
			gameWorld.loadLevel(Level.lvl_magelab, "main");
			keyframes = null;
			return;
		}

		Keyframe k1 = keyframes[aidx];
		Keyframe k2 = keyframes[aidx+1];
		float progress = (cinematicProgress - k1.time) / (k2.time - k1.time);
		cinematicAlpha = interpolate(k1.alpha, k2.alpha, progress);
		float centerx = interpolate(k1.centerx, k2.centerx, progress);
		float centery = interpolate(k1.centery, k2.centery, progress);
		float scale = interpolate(k1.scale, k2.scale, progress);

		setScale(scale);
		camera.position.x = centerx;
		camera.position.y = centery;
	}

	@Override
	public void render(float delta) {
		if (gameWorld.cinematic) {
			updateCinematic(delta);
		}
		update(delta);
		draw(delta);
	}

	@Override
	public void resize(int width, int height) {
		screenWidth = width;
		screenHeight = height;
		setScale(currentScale);
	}

	public void setScale(float scale) {
		currentScale = scale;
		float aspect = ((float) screenWidth) / screenHeight;
		float newheight = Consts.HEIGHT * Consts.SCALE;
		float newwidth = aspect * newheight;
		camera.setToOrtho(false, newwidth * currentScale, newheight * currentScale);
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
