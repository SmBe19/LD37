package com.smeanox.games.ld37.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.io.Level;
import com.smeanox.games.ld37.io.Textures;
import com.smeanox.games.ld37.world.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameWorld {
	public final ObservableValue<Level> level;
	public final ObservableValue<Float> fadeIn;
	public final ObservableValue<Float> fadeOut;
	public final Hero hero;
	public final List<Entity> entities;
	public final Map<String, Entity> entityHashMap;
	public final List<Speech> speeches;
	public final Queue<Speech> speechQueue;
	public final Queue<Speech> subtitles;
	public boolean speechHeroActive;
	public boolean inventoryVisible;
	public boolean inputPaused;
	public boolean cinematic;

	public static class Speech{
		public String text;
		public float age;
		public float duration;
		public float x, y;
		public boolean think;
		public boolean followHero;

		public Speech(String text) {
			this.text = text;
			this.followHero = false;
			this.duration = Consts.SPEECH_BUBBLE_DURATION + text.length() * Consts.SPEECH_BUBBLE_DURATION_PER_CHAR;
		}

		public Speech(String text, float duration) {
			this.text = text;
			this.duration = duration;
			this.followHero = false;
		}

		public Speech(String text, float duration, float x, float y, boolean think, boolean followHero) {
			this.text = text;
			this.duration = duration;
			this.x = x;
			this.y = y;
			this.think = think;
			this.followHero = followHero;
		}

		public Speech(String text, float x, float y, boolean think, boolean followHero) {
			this.text = text;
			this.x = x;
			this.y = y;
			this.think = think;
			this.followHero = followHero;
			this.duration = Consts.SPEECH_BUBBLE_DURATION + text.length() * Consts.SPEECH_BUBBLE_DURATION_PER_CHAR;
		}

		public Speech(String text, float duration, float x, float y, boolean think) {
			this.text = text;
			this.duration = duration;
			this.x = x;
			this.y = y;
			this.think = think;
			this.followHero = false;
		}

		public Speech(String text, float x, float y, boolean think) {
			this.text = text;
			this.x = x;
			this.y = y;
			this.think = think;
			this.followHero = false;
			this.duration = Consts.SPEECH_BUBBLE_DURATION + text.length() * Consts.SPEECH_BUBBLE_DURATION_PER_CHAR;
		}
	}

	public GameWorld() {
		this.level = new ObservableValue<Level>(null);
		this.fadeIn = new ObservableValue<Float>(0.f);
		this.fadeOut = new ObservableValue<Float>(0.f);
		this.hero = new Hero(this);
		this.entities = new ArrayList<Entity>();
		this.entityHashMap = new HashMap<String, Entity>();
		this.speeches = new ArrayList<Speech>();
		this.speechQueue = new Queue<Speech>();
		this.subtitles = new Queue<Speech>();

		inventoryVisible = true;
		inputPaused = false;
		cinematic = false;

		initEntities();
	}

	public void loadLevel(Level level, String portalId) {
		this.speeches.clear();

		this.cinematic = level == Level.lvl_intro;

		this.level.set(level);

		if(this.cinematic){
			return;
		}

		this.fadeIn.set(Consts.FADE_DURATION);

		for (RectangleMapObject meta : level.map.getLayers().get(Consts.LAYER_META).getObjects().getByType(RectangleMapObject.class)) {
			if (meta.getProperties().get(Consts.PROP_FROMLEVEL, "", String.class).length() > 0 && portalId.equals(meta.getProperties().get(Consts.PROP_PORTALID, "", String.class))) {
				hero.x = (meta.getRectangle().x + meta.getRectangle().width * 0.5f) * Consts.UNIT_SCALE;
				hero.y = (meta.getRectangle().y + meta.getRectangle().height * 0.5f) * Consts.UNIT_SCALE;
				break;
			}
		}
	}

	protected void addEntity(Entity entity) {
		entities.add(entity);
		entityHashMap.put(entity.id, entity);
	}

	protected void initEntities(){
		addEntity(new Entity("kerze", Textures.tiles.getTextureRegion(6, 9)));
		addEntity(new Entity("kocke", Textures.tiles.getTextureRegion(5, 9)));
	}

	protected void updateInput(float delta){
		if (Gdx.input.isKeyPressed(Consts.INPUT_MOVE_LEFT)) {
			hero.vx = -Consts.HERO_VELO_X;
		}
		if (Gdx.input.isKeyPressed(Consts.INPUT_MOVE_RIGHT)) {
			hero.vx = Consts.HERO_VELO_X;
		}
		if (Gdx.input.isKeyPressed(Consts.INPUT_MOVE_UP)) {
			hero.vy = Consts.HERO_VELO_Y;
		}
		if (Gdx.input.isKeyPressed(Consts.INPUT_MOVE_DOWN)) {
			hero.vy = -Consts.HERO_VELO_Y;
		}
		if(!Gdx.input.isKeyPressed(Consts.INPUT_MOVE_LEFT) && !Gdx.input.isKeyPressed(Consts.INPUT_MOVE_RIGHT)){
			hero.vx = 0;
		}
		if (!Gdx.input.isKeyPressed(Consts.INPUT_MOVE_UP) && !Gdx.input.isKeyPressed(Consts.INPUT_MOVE_DOWN)) {
			hero.vy = 0;
		}
		if (Gdx.input.isKeyJustPressed(Consts.INPUT_EXAMINE)) {
			String result = hero.examine(level.get().map);
			if (result != null) {
				speeches.add(new Speech(result, 0, 0, true, true));
			}
		}
		if (Gdx.input.isKeyJustPressed(Consts.INPUT_INTERACT)) {
			String result = hero.interact(level.get().map);
			if (result != null) {
				speeches.add(new Speech(result, 0, 0, true, true));
			}
		}
		if (Gdx.input.isKeyJustPressed(Consts.INPUT_USE_ITEM)) {
			String result = hero.useItem(level.get().map);
			if (result != null) {
				speeches.add(new Speech(result, 0, 0, true, true));
			}
		}
		if (Gdx.input.isKeyJustPressed(Consts.INPUT_INVENTORY)) {
			inventoryVisible = !inventoryVisible && hero.inventory.size() > 0;
		}
		if (Gdx.input.isKeyJustPressed(Consts.INPUT_INVENTORY_NEXT)) {
			if (hero.inventory.size() > 0) {
				hero.activeInventory++;
				hero.activeInventory %= hero.inventory.size();
			} else {
				hero.activeInventory = 0;
			}
		}
	}

	protected void updateSpeeches(float delta) {
		speechHeroActive = false;
		for(int i = speeches.size() - 1; i >= 0; i--) {
			Speech speech = speeches.get(i);
			speech.age += delta;
			if(speech.followHero){
				speech.x = hero.x;
				speech.y = hero.y + Consts.SPEECH_BUBBLE_OFFSET;
			}
			if(speech.age > speech.duration || (speech.followHero && speechHeroActive)){
				speeches.remove(i);
			}
			speechHeroActive = speechHeroActive || speech.followHero;
		}
		if(speeches.isEmpty() && speechQueue.size != 0){
			speeches.add(speechQueue.first());
			speechQueue.removeFirst();
		}
	}

	protected void updateSubtitles(float delta) {
		if (subtitles.size == 0) {
			return;
		}
		Speech first = subtitles.first();
		first.age += delta;
		if(first.age > first.duration){
			subtitles.removeFirst();
		}
	}

	public void update(float delta){
		if(!inputPaused && !cinematic) {
			if (hero.portalAction == null) {
				updateInput(delta);
				hero.update(delta, level.get().map, (TiledMapTileLayer) level.get().map.getLayers().get(Consts.LAYER_COLLISION));
			} else {
				if (fadeOut.get() == 0 && TimeUtils.millis() > hero.portalAction.start + hero.portalAction.delay - (long) (1000 * Consts.FADE_DURATION)) {
					fadeOut.set(Math.min((hero.portalAction.start + hero.portalAction.delay - TimeUtils.millis()) / 1000.f, Consts.FADE_DURATION));
				}
				if (TimeUtils.millis() > hero.portalAction.start + hero.portalAction.delay) {
					loadLevel(Level.valueOf(hero.portalAction.tolevel), hero.portalAction.id);
					hero.portalAction = null;
				}
			}
		}
		updateSpeeches(delta);
		updateSubtitles(delta);
	}
}
