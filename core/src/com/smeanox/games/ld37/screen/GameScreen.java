package com.smeanox.games.ld37.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.utils.TimeUtils;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.io.Font;
import com.smeanox.games.ld37.io.Level;
import com.smeanox.games.ld37.io.Textures;
import com.smeanox.games.ld37.world.GameWorld;
import com.smeanox.games.ld37.world.ObservableValue;

public class GameScreen implements Screen {

	private final SpriteBatch spriteBatch;
	private final OrthographicCamera camera, guicamera;
	private MyMapRenderer mapRenderer;
	private final GameWorld gameWorld;
	private float fadeProgress;
	private float fadeStart;
	private boolean isDark;
	private int screenWidth, screenHeight;
	private float bgwidth;
	private float bgheight;
	private float currentScale;
	public boolean beforeStart;
	public Level cinematicNextLevel;
	public float cinematicProgress;
	public float cinematicAlpha;
	public MapObject cinematicDragon;
	public float cinematicLogoAlpha;
	public float cinematicCreditsAlpha;
	public boolean drawCreditsDragon;

	private final TextureRegion fadeTexture;
	private final Texture logo;
	private final TextureRegion dragon;
	private final Texture castlebg;
	private final TextureRegion inventoryBackground;
	private final TextureRegion[] speechBubble;
	private final Music introNarration;
	private final Music music;

	private class Keyframe {
		float time;
		float scale;
		float centerx;
		float centery;
		float alpha;
		float logo;
		float credits;
		float dragonX;
		float dragonY;

		public Keyframe(float time, float scale, float centerx, float centery, float alpha, float credits, float logo) {
			this.time = time;
			this.scale = scale;
			this.centerx = centerx;
			this.centery = centery;
			this.alpha = alpha;
			this.credits = credits;
			this.logo = logo;
		}

		public Keyframe(float time, float scale, float centerx, float centery, float alpha, float credits, float dragonX, float dragonY) {
			this.time = time;
			this.scale = scale;
			this.centerx = centerx;
			this.centery = centery;
			this.alpha = alpha;
			this.credits = credits;
			this.dragonX = dragonX;
			this.dragonY = dragonY;
		}
	}

	private Keyframe[] keyframesIntro = new Keyframe[]{
			new Keyframe(0, 0.1f, 9, 12, 1, 1, 0),
			new Keyframe(2, 0.1f, 11f, 11.4f, 0, 0, 0),
			new Keyframe(4, 0.2f, 12, 8.75f, 0, 0, 0),
			new Keyframe(4.01f, 0.1f, 12, 8.75f, 0, 0, 0),
			new Keyframe(6, 0.1f, 13.5f, 7, 0, 0, 0),
			new Keyframe(7, 0.1f, 13.5f, 7, 0, 0, 0),
			new Keyframe(12, 0.5f, 12, 7, 0, 0, 0),
			new Keyframe(18, 1, 12, 7, 0, 0, 1),
			new Keyframe(26, 1, 12, 7, 0, 0, 1),
			new Keyframe(30, 1, 12, 7, 1, 0, 1),
	};

	private Keyframe[] keyframesOutro = new Keyframe[]{
			new Keyframe(0, 1, 12, 7, 0, 0, 13, 7),
			new Keyframe(10, 0.2f, 22, 12, 0, 0, 20, 10),
			new Keyframe(15, 0.1f, 23, 12, 1, 0, 21, 11),
			new Keyframe(17, 0.1f, 23, 12, 1, 1, 21, 11),
	};

	private Keyframe[] keyframes = null;

	public GameScreen(final Level level) {
		spriteBatch = new SpriteBatch();
		camera = new OrthographicCamera();
		guicamera = new OrthographicCamera();
		mapRenderer = null;
		currentScale = 1;
		drawCreditsDragon = false;

		introNarration = Gdx.audio.newMusic(Gdx.files.internal("snd/IntroNarration.mp3"));
		music = Gdx.audio.newMusic(Gdx.files.internal("snd/Song001.mp3"));
		music.setLooping(true);
		fadeTexture = Textures.tiles.getTextureRegion(10, 3);
		logo = Textures.logo.texture;
		dragon = Textures.dragon.getTextureRegion(4, 0, 32, 32, 32);
		castlebg = Textures.castlebg.texture;
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
		gameWorld.level.addObserver(new ObservableValue.Observer<Level>() {
			@Override
			public void update(ObservableValue<Level> o) {
				TiledMap map = o.get().map;
				mapRenderer = new MyMapRenderer(map, Consts.UNIT_SCALE, spriteBatch, gameWorld);
				setScale(map.getProperties().get(Consts.PROP_MAPSCALE, 1.f, Float.class));
				cinematicProgress = 0;
				if(o.get() == Level.lvl_intro){
					initIntro();
				} else if (o.get() == Level.lvl_outro){
					initOutro();
				}
			}
		});
		gameWorld.fadeOut.addObserver(new ObservableValue.Observer<Float>() {
			@Override
			public void update(ObservableValue<Float> o) {
				fadeStart = fadeProgress = o.get();
				isDark = false;
			}
		});
		gameWorld.fadeIn.addObserver(new ObservableValue.Observer<Float>() {
			@Override
			public void update(ObservableValue<Float> o) {
				fadeStart = fadeProgress = -o.get();
				isDark = false;
			}
		});
		gameWorld.loadLevel(level, "main");
	}

	private void initIntro(){
		beforeStart = true;
		cinematicNextLevel = Level.lvl_magelab;
		keyframes = keyframesIntro;
		gameWorld.subtitles.addLast(new GameWorld.Speech("Once upon a time, there was a cute\nlittle castle on a hill.", 10));
		gameWorld.subtitles.addLast(new GameWorld.Speech("Unfortunately, that castle was attacked\nby an enemy army and is now under siege.", 10));
		gameWorld.subtitles.addLast(new GameWorld.Speech("It would seem that there is only one hope...", 6));
		gameWorld.subtitles.addLast(new GameWorld.Speech("Aaaaaah! I was hit!", 4));
		gameWorld.subtitles.addLast(new GameWorld.Speech("", 20));
		gameWorld.subtitles.addLast(new GameWorld.Speech("Press F to pay respects", 60));
		gameWorld.walkingPaused = true;
	}

	private void initOutro(){
		gameWorld.hero.inventory.clear();
		gameWorld.hero.activeInventory = 0;

		cinematicNextLevel = Level.lvl_intro;
		drawCreditsDragon = true;
		keyframes = keyframesOutro;
		gameWorld.subtitles.addLast(new GameWorld.Speech("The dragon killed all the invaders", 5));
		gameWorld.subtitles.addLast(new GameWorld.Speech("", 2));
		gameWorld.subtitles.addLast(new GameWorld.Speech("and also all the defenders", 3));
		gameWorld.subtitles.addLast(new GameWorld.Speech("and lived happily ever after", 5));
		cinematicDragon = Level.lvl_outro.map.getLayers().get("Objects").getObjects().get("Dragon");
	}

	private void resetGame() {
		gameWorld.hero.inventory.clear();
		gameWorld.hero.activeInventory = 0;
		gameWorld.hero.variables.clear();
		for (Level level : Level.values()) {
			level.reload();
		}
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

	private void drawLogo(float alpha){
		spriteBatch.setColor(1, 1, 1, alpha);
		spriteBatch.begin();
		spriteBatch.draw(logo, 8, 2, 6, 3);
		spriteBatch.end();
		spriteBatch.setColor(1, 1, 1, 1);
	}

	private void drawCentered(String text, float y) {
		float width = Font.mango.getWidth(text);
		Font.mango.draw(spriteBatch, text, (guicamera.viewportWidth - width) / 2, y);
	}

	private void drawCredits(float alpha) {
		spriteBatch.setColor(1, 1, 1, alpha);
		spriteBatch.begin();
		if(drawCreditsDragon){
			spriteBatch.draw(dragon, guicamera.viewportWidth / 2 - 2, guicamera.viewportHeight - 5, 4, 4);
		}
		spriteBatch.draw(logo, guicamera.viewportWidth / 2 - 3, guicamera.viewportHeight - 7, 6, 3);
		float y = guicamera.viewportHeight - 8;
		for (String line : Consts.GAME_CREDITS.split("\n")) {
			drawCentered(line, y);
			y -= Font.mango.getLineHeight() * Consts.FONT_LINE_SPACING_CREDITS;
		}
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
		spriteBatch.setColor(0, 0, 0, progress);
		float off = 1.f / 16;
		Font.mango.draw(spriteBatch, text, (guicamera.viewportWidth - width) / 2 - off, Consts.SUBTITLES_OFFSET_Y + height);
		Font.mango.draw(spriteBatch, text, (guicamera.viewportWidth - width) / 2 + off, Consts.SUBTITLES_OFFSET_Y + height);
		Font.mango.draw(spriteBatch, text, (guicamera.viewportWidth - width) / 2, Consts.SUBTITLES_OFFSET_Y + height - off);
		Font.mango.draw(spriteBatch, text, (guicamera.viewportWidth - width) / 2, Consts.SUBTITLES_OFFSET_Y + height + off);
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

		if(gameWorld.level.get().map.getProperties().get(Consts.PROP_DRAWBACKGROUND, false, Boolean.class)) {
			spriteBatch.setProjectionMatrix(guicamera.combined);
			spriteBatch.begin();
			spriteBatch.draw(Textures.castlebg.texture, 0, guicamera.viewportHeight - bgheight, bgwidth, bgheight);
			spriteBatch.end();
		}

		spriteBatch.setProjectionMatrix(camera.combined);
		mapRenderer.setView(camera);
		mapRenderer.render();

		if (gameWorld.cinematic) {
			drawLogo(cinematicLogoAlpha);
		}

		drawSpeechBubbles();

		spriteBatch.setProjectionMatrix(guicamera.combined);
		drawGUI(delta);
		if(gameWorld.cinematic){
			drawDark(cinematicAlpha);
		}
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

		if (gameWorld.cinematic) {
			drawCredits(cinematicCreditsAlpha);
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
		if(Gdx.input.isKeyJustPressed(Consts.INPUT_SKIP)){
			if (keyframes == keyframesOutro) {
				resetGame();
			}
			keyframes = null;
			cinematicDragon = null;
			introNarration.stop();
			gameWorld.speechQueue.clear();
			gameWorld.subtitles.clear();
			gameWorld.loadLevel(cinematicNextLevel, "main");
			MyMapRenderer.forceOwnTiming = false;
			return;
		}
		if (aidx < 0 || aidx >= keyframes.length - 1) {
			if (keyframes == keyframesOutro) {
				resetGame();
			}
			keyframes = null;
			cinematicDragon = null;
			gameWorld.loadLevel(cinematicNextLevel, "main");
			MyMapRenderer.forceOwnTiming = false;
			return;
		}

		Keyframe k1 = keyframes[aidx];
		Keyframe k2 = keyframes[aidx+1];
		float progress = (cinematicProgress - k1.time) / (k2.time - k1.time);
		cinematicAlpha = interpolate(k1.alpha, k2.alpha, progress);
		cinematicLogoAlpha = interpolate(k1.logo, k2.logo, progress);
		cinematicCreditsAlpha = interpolate(k1.credits, k2.credits, progress);
		float dragonX = interpolate(k1.dragonX, k2.dragonX, progress);
		float dragonY = interpolate(k1.dragonY, k2.dragonY, progress);
		float centerx = interpolate(k1.centerx, k2.centerx, progress);
		float centery = interpolate(k1.centery, k2.centery, progress);
		float scale = interpolate(k1.scale, k2.scale, progress);

		setScale(scale);
		camera.position.x = centerx;
		camera.position.y = centery;

		if (cinematicDragon != null) {
			((TiledMapTileMapObject) cinematicDragon).setX(dragonX/Consts.UNIT_SCALE);
			((TiledMapTileMapObject) cinematicDragon).setY(dragonY/Consts.UNIT_SCALE);
		}
	}

	@Override
	public void render(float delta) {
		if(beforeStart && gameWorld.cinematic){
			if(Gdx.input.isKeyJustPressed(Consts.INPUT_SKIP) || Gdx.input.isTouched()){
				beforeStart = false;
				if(keyframes == keyframesIntro) {
					if (introNarration.isPlaying()) {
						introNarration.stop();
					}
					if (music.isPlaying()) {
						music.stop();
					}
					introNarration.play();
					music.play();
					MyMapRenderer.forceOwnTiming = true;
				}
				MyMapRenderer.initialTimeOffset = TimeUtils.millis();
				return;
			}
			updateCinematic(0);
			draw(delta);
			return;
		}
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
		float aspect = castlebg.getWidth() / ((float) castlebg.getHeight());
		bgwidth = guicamera.viewportWidth;
		bgheight = bgwidth / aspect;
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
		if (introNarration != null) {
			introNarration.dispose();
		}
	}
}
