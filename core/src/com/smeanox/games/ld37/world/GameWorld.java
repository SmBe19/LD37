package com.smeanox.games.ld37.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
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
	public boolean inventoryVisible;

	public static class Speech{
		public String text;
		public float age;
		public float x, y;
		public boolean think;
		public boolean followHero;

		public Speech(String text, float x, float y, boolean think, boolean followHero) {
			this.text = text;
			this.x = x;
			this.y = y;
			this.think = think;
			this.followHero = followHero;
		}

		public Speech(String text, float x, float y, boolean think) {
			this.text = text;
			this.x = x;
			this.y = y;
			this.think = think;
			this.followHero = false;
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

		speeches.add(new Speech("Hello there", 0, 0, false, true));

		initEntities();
	}

	public void loadLevel(Level level, String portalId) {
		this.level.set(level);
		this.fadeIn.set(Consts.FADE_DURATION);
		inventoryVisible = false;

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
		for(int i = speeches.size() - 1; i >= 0; i--) {
			speeches.get(i).age += delta;
			if(speeches.get(i).followHero){
				speeches.get(i).x = hero.x;
				speeches.get(i).y = hero.y + Consts.SPEECH_BUBBLE_OFFSET;
			}
			if(speeches.get(i).age > Consts.SPEECH_BUBBLE_DURATION){
				speeches.remove(i);
			}
		}
	}

	public void update(float delta){
		if(hero.portalAction == null) {
			updateInput(delta);
			hero.update(delta, (TiledMapTileLayer) level.get().map.getLayers().get(Consts.LAYER_COLLISION));
		} else {
			if(fadeOut.get() == 0 && TimeUtils.millis() > hero.portalAction.start + hero.portalAction.delay - (long)(1000 * Consts.FADE_DURATION)){
				fadeOut.set(Math.min((hero.portalAction.start + hero.portalAction.delay - TimeUtils.millis()) / 1000.f, Consts.FADE_DURATION));
			}
			if (TimeUtils.millis() > hero.portalAction.start + hero.portalAction.delay) {
				loadLevel(Level.valueOf(hero.portalAction.tolevel), hero.portalAction.id);
				hero.portalAction = null;
			}
		}
		updateSpeeches(delta);
	}
}
