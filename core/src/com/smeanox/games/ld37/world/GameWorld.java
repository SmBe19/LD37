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
import com.smeanox.games.ld37.world.entity.EntityAmazon;
import com.smeanox.games.ld37.world.entity.EntityBlueElixir;
import com.smeanox.games.ld37.world.entity.EntityBurningStraw;
import com.smeanox.games.ld37.world.entity.EntityCheese;
import com.smeanox.games.ld37.world.entity.EntityEmerald;
import com.smeanox.games.ld37.world.entity.EntityGoldCoin;
import com.smeanox.games.ld37.world.entity.EntityGreenElixir;
import com.smeanox.games.ld37.world.entity.EntityKeys;
import com.smeanox.games.ld37.world.entity.EntityKing;
import com.smeanox.games.ld37.world.entity.EntityMead;
import com.smeanox.games.ld37.world.entity.EntityRattail;
import com.smeanox.games.ld37.world.entity.EntitySaw;
import com.smeanox.games.ld37.world.entity.EntitySilverCoin;
import com.smeanox.games.ld37.world.entity.EntityStraw;
import com.smeanox.games.ld37.world.entity.EntityWaterBucket;
import com.smeanox.games.ld37.world.entity.EntityWell;

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
	public float heroVeloScale;
	public boolean speechHeroActive;
	public boolean inventoryVisible;
	public boolean inputPaused;
	public boolean walkingPaused;
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

		heroVeloScale = 1;
		inventoryVisible = true;
		inputPaused = false;
		cinematic = false;

		initEntities();
	}

	public void loadLevel(Level level, String portalId) {
		this.speeches.clear();
		this.cinematic = level == Level.lvl_intro || level == Level.lvl_outro;
		this.level.set(level);
		this.heroVeloScale = level.map.getProperties().get(Consts.PROP_VELOSCALE, 1.f, Float.class);

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

		addEntity(new EntityGreenElixir("greenelixir", Textures.tiles.getTextureRegion(15, 5)));
		addEntity(new EntityBlueElixir("blueelixir", Textures.tiles.getTextureRegion(15, 6), "Blue Elixir:\nMakes a person fall asleep.\nI can use it by throwing it at someone."));
		addEntity(new EntityRattail("rattail", Textures.tiles.getTextureRegion(15, 7), "Rat tail:\nLike Spaghetti, except more rat-tail-y."));
		addEntity(new Entity("rat", Textures.tiles.getTextureRegion(4, 9)));
		addEntity(new EntityEmerald("emerald", Textures.tiles.getTextureRegion(15, 8), "Emerald:\nA glittering green gem."));
		addEntity(new Entity("bucket", Textures.tiles.getTextureRegion(15, 9), "Bucket:\nCan carry liquids.\nOne of these is used in the well."));
		addEntity(new EntityWell("well", Textures.tiles.getTextureRegion(15, 9), "Well:\nA well."));
		addEntity(new EntityWaterBucket("waterbucket", Textures.tiles.getTextureRegion(15, 4), "Bucket with water:\nThe water seems like a good way to extinguish flames."));
		addEntity(new EntityKeys("key", Textures.tiles.getTextureRegion(15, 10), "Key:\nOpens doors, probably."));
		addEntity(new EntityGoldCoin("goldcoin", Textures.tiles.getTextureRegion(15, 11)));
		addEntity(new EntitySilverCoin("silvercoin", Textures.tiles.getTextureRegion(14, 4), "Silver coin:\nEven during a siege, you can\nstill buy stuff with it."));
		addEntity(new EntityStraw("straw", Textures.tiles.getTextureRegion(14, 5)));
		addEntity(new Entity("strawman", Textures.tiles.getTextureRegion(14, 6)));
		addEntity(new EntityBurningStraw("burningstrawman", Textures.tiles.getTextureRegion(14, 7), "Burning straw:\nShit's on fire, yo!"));
		addEntity(new Entity("elixicon", Textures.tiles.getTextureRegion(14, 8), "Elixicon:\nIt contains interesting information and recipes.\nTwo are of interest to me:\nThe green potion can turn people into frogs\nand a blue sleeping elixir is made by\ncooking a rat tail in the cauldron."));
		addEntity(new EntityCheese("cheese", Textures.tiles.getTextureRegion(14, 9), "Cheese:\nLoved by man and beast alike."));
		addEntity(new Entity("king", Textures.tiles.getTextureRegion(14, 10)));
		addEntity(new EntityKing("sleepingking", Textures.tiles.getTextureRegion(14, 10), "Sleeping king:\nI can't believe he fit in my pocket."));
		addEntity(new EntityKing("kingshand", Textures.tiles.getTextureRegion(14, 11), "King's hand:\nMight come in handy later."));
		addEntity(new EntitySaw("saw", Textures.tiles.getTextureRegion(14, 12), "Saw:\nPotentially Dangerous."));
		addEntity(new EntityMead("mead", Textures.tiles.getTextureRegion(15, 12), "Mead:\nCheers people up in times like these."));
		addEntity(new EntityAmazon("amazon", Textures.tiles.getTextureRegion(15, 5), "Amazon box:\nA dragon."));
	}

	protected void updateInput(float delta){
		if(!walkingPaused) {
			if (Gdx.input.isKeyPressed(Consts.INPUT_MOVE_LEFT)) {
				hero.vx = -Consts.HERO_VELO_X * heroVeloScale;
			}
			if (Gdx.input.isKeyPressed(Consts.INPUT_MOVE_RIGHT)) {
				hero.vx = Consts.HERO_VELO_X * heroVeloScale;
			}
			if (Gdx.input.isKeyPressed(Consts.INPUT_MOVE_UP)) {
				hero.vy = Consts.HERO_VELO_Y * heroVeloScale;
			}
			if (Gdx.input.isKeyPressed(Consts.INPUT_MOVE_DOWN)) {
				hero.vy = -Consts.HERO_VELO_Y * heroVeloScale;
			}
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
		if(Gdx.input.isKeyJustPressed(Consts.INPUT_SKIP)){
			skipSpeeches(false);
			if (subtitles.size > 0) {
				subtitles.first().age = subtitles.first().duration - Consts.SPEECH_BUBBLE_ANIM_DURATION;
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
		} else if (speeches.isEmpty()){
			inputPaused = false;
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

	public void skipSpeeches(boolean all) {
		for (Speech speech : speeches) {
			speech.age = Math.max(speech.age, speech.duration - Consts.SPEECH_BUBBLE_ANIM_DURATION);
		}
		if(all) {
			speechQueue.clear();
		}
	}

	public void update(float delta){
		if(!cinematic) {
			if (!inputPaused) {
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
			} else {
				if(Gdx.input.isKeyJustPressed(Consts.INPUT_SKIP)){
					skipSpeeches(false);
					if (subtitles.size > 0) {
						subtitles.first().age = subtitles.first().duration - Consts.SPEECH_BUBBLE_ANIM_DURATION;
					}
				}
			}
		}
		updateSpeeches(delta);
		updateSubtitles(delta);
	}
}
