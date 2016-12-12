package com.smeanox.games.ld37.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.TimeUtils;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.io.Sounds;
import com.smeanox.games.ld37.world.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hero {

	public enum HeroAnimation {
		wait_left("hero_wait_left"),
		wait_right("hero_wait_right"),
		walk_left("hero_walk_left"),
		walk_right("hero_walk_right"),
		walk_up("hero_walk_up"),
		walk_down("hero_walk_down"),
		;

		public final String id;

		HeroAnimation(String id) {
			this.id = id;
		}
	}

	public static class PortalAction {
		public String id, name, tolevel;
		public long start, delay;

		public PortalAction(String id, String name, String tolevel, long start, long delay) {
			this.id = id;
			this.name = name;
			this.tolevel = tolevel;
			this.start = start;
			this.delay = delay;
		}
	}

	public static final String[] dontdothisstrings = new String[]{
			"I don't think that's going to work.",
			"That doesn't make sense.",
			"Bad idea.",
			"That's not how the programmer\nenvisioned it.",
			"This won't get me ahead.",
			"I should try something else.",
			"Doesn't seem to be making\nany progress.",
	};

	public static final String[] wrongpersonstrings = new String[]{
			"I think there's a better use for that.",
			"Maybe someone else is better suited.",
			"Now is not the time for this.",
	};

	public final GameWorld gameWorld;
	public float x, y, vx, vy;
	public float animationTime;
	public HeroAnimation animation;
	public PortalAction portalAction;
	public final List<Entity> inventory;
	public int activeInventory;
	public String activeDisplayText;
	public final Map<String, String> variables;
	private long observeLastChange;
	private MapObject observeLastObject;
	private boolean observeDisplayed;
	private GameWorld.Speech observeLastSpeech;
	private boolean lastWalkRight;

	public Hero(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
		inventory = new ArrayList<Entity>();
		variables = new HashMap<String, String>();
		animation = HeroAnimation.wait_right;
		lastWalkRight = false;
		portalAction = null;
		activeInventory = 0;
		activeDisplayText = "";
	}

	protected float getDistance2(MapObject object, float x, float y) {
		float ox = 0, oy = 0;
		if (object instanceof TiledMapTileMapObject) {
			TiledMapTileMapObject objectc = (TiledMapTileMapObject) object;
			ox = (objectc.getX() + objectc.getTextureRegion().getRegionWidth() * objectc.getScaleX() * 0.5f) * Consts.UNIT_SCALE;
			oy = (objectc.getY() + objectc.getTextureRegion().getRegionHeight() * objectc.getScaleY() * 0.5f) * Consts.UNIT_SCALE;
		} else if (object instanceof RectangleMapObject) {
			RectangleMapObject objectc = (RectangleMapObject) object;
			ox = (objectc.getRectangle().x + objectc.getRectangle().width * 0.5f) * Consts.UNIT_SCALE;
			oy = (objectc.getRectangle().y + objectc.getRectangle().height * 0.5f) * Consts.UNIT_SCALE;
		} else {
			return Float.MAX_VALUE;
		}
		return (ox - x) * (ox - x) + (oy - y) * (oy - y);
	}

	protected interface ObjectFilter {
		boolean filter(MapObject object);
	}

	protected MapObject findClosestObject(TiledMap map, ObjectFilter filter) {
		MapObject minOb = null;
		float minDist = Float.MAX_VALUE;
		for (MapLayer layer : map.getLayers()) {
			if (!layer.isVisible()) {
				continue;
			}
			for (MapObject object : layer.getObjects()) {
				float dist = getDistance2(object, x, y);
				if(dist > Consts.HERO_INTERACT_RAD){
					continue;
				}
				if (!object.isVisible() || !filter.filter(object)) {
					continue;
				}
				if (dist < minDist) {
					minOb = object;
					minDist = dist;
				}
			}
		}
		return minOb;
	}

	public static MapObject findObjectByName(TiledMap map, String name, boolean visible){
		for (MapLayer layer : map.getLayers()) {
			if (!layer.isVisible()) {
				continue;
			}
			for (MapObject object : layer.getObjects()) {
				if (object.isVisible() != visible) {
					continue;
				}
				if (name.equals(object.getName())) {
					return object;
				}
			}
		}
		return null;
	}

	public static MapObject findObjectByName(TiledMap map, String name) {
		return findObjectByName(map, name, true);
	}

	public void addToInventory(MapObject object) {
		Entity entity = gameWorld.entityHashMap.get(object.getName());
		String examine = object.getProperties().get(Consts.PROP_EXAMINE, "", String.class);
		if(!inventory.contains(entity)) {
			if (examine.length() > 0 && (entity.examineText == null || entity.examineText.length() == 0)) {
				String prefix = object.getProperties().get(Consts.PROP_DISPLAYNAME, "", String.class);
				if (prefix.length() > 0) {
					prefix += ":\n";
				}
				entity.examineText = prefix + examine;
			}
			if (entity != null) {
				inventory.add(entity);
				activeInventory = inventory.size() - 1;
				object.setVisible(false);
			}
		}
	}

	public void removeCurrentItemFromInventory(){
		inventory.remove(activeInventory);
		if (inventory.size() > 0) {
			activeInventory %= inventory.size();
		} else {
			activeInventory = 0;
		}
	}

	public String getVar(String variable) {
		if(variables.containsKey(variable)) {
			return variables.get(variable);
		}
		return "";
	}

	public void setVar(String variable, String value) {
		variables.put(variable, value);
	}

	public String interact(TiledMap map) {
		MapObject object = findClosestObject(map, new ObjectFilter() {
			@Override
			public boolean filter(MapObject object) {
				return ((object.getProperties().get(Consts.PROP_ONINTERACT, 0, Integer.class) > 0)
						|| (object.getProperties().get(Consts.PROP_ONNOINTERACT, 0, Integer.class) > 0)
						|| (Consts.TYPE_EXIT.equals(object.getProperties().get(Consts.PROP_TYPE, "", String.class))));
			}
		});
		if (object != null) {
			if (Consts.TYPE_EXIT.equals(object.getProperties().get(Consts.PROP_TYPE, "", String.class))
					&& object.getProperties().get(Consts.PROP_ACTIVE, true, Boolean.class)) {
				portalAction = new PortalAction(object.getProperties().get(Consts.PROP_PORTALID, "", String.class),
						object.getName(), object.getProperties().get(Consts.PROP_TOLEVEL, "", String.class),
						TimeUtils.millis() - object.getProperties().get(Consts.PROP_START, 0, Integer.class),
						object.getProperties().get(Consts.PROP_DELAY, 0, Integer.class));
				if(MathUtils.randomBoolean()) {
					Sounds.door2.sound.play();
				} else {
					Sounds.door.sound.play();
				}
			} else {
				StringBuilder sayText = new StringBuilder();
				String oninteracteprefix = object.getProperties().get(Consts.PROP_ACTIVE, true, Boolean.class) ? Consts.PROP_ONINTERACT : Consts.PROP_ONNOINTERACT;
				int oninteract = object.getProperties().get(oninteracteprefix, 0, Integer.class);
				for (int i = 0; i < oninteract; i++) {
					String[] lines = object.getProperties().get(oninteracteprefix + "_" + i, "", String.class).replace("\r", "").split("\n");
					for (String line : lines) {
						String[] cmd = line.split(" ", 2);
						if (Consts.ONINTERACT_CLASS.equalsIgnoreCase(cmd[0])) {
							Entity entity = gameWorld.entityHashMap.get(object.getName());
							if (entity != null) {
								String interact = entity.interact(this);
								if (interact != null) {
									sayText.append(interact);
									sayText.append("\n");
								}
							}
						} else if (Consts.ONINTERACT_ACTIVATE.equalsIgnoreCase(cmd[0])) {
							MapObject ob2 = Consts.ONINTERACT_THIS.equals(cmd[1]) ? object : findObjectByName(map, cmd[1]);
							if (ob2 != null) {
								ob2.getProperties().put(Consts.PROP_ACTIVE, true);
							}
						} else if (Consts.ONINTERACT_DEACTIVATE.equalsIgnoreCase(cmd[0])) {
							MapObject ob2 = Consts.ONINTERACT_THIS.equals(cmd[1]) ? object : findObjectByName(map, cmd[1]);
							if (ob2 != null) {
								ob2.getProperties().put(Consts.PROP_ACTIVE, false);
							}
						} else if (Consts.ONINTERACT_PAUSE.equalsIgnoreCase(cmd[0])) {
							gameWorld.inputPaused = true;
						} else if (Consts.ONINTERACT_PAUSEWALK.equalsIgnoreCase(cmd[0])) {
							gameWorld.walkingPaused = true;
						} else if (Consts.ONINTERACT_UNPAUSEWALK.equalsIgnoreCase(cmd[0])) {
							gameWorld.walkingPaused = false;
						} else if (Consts.ONINTERACT_CLEARSPEECH.equalsIgnoreCase(cmd[0])) {
							gameWorld.skipSpeeches(true);
						} else if (Consts.ONINTERACT_CLEARSUBS.equalsIgnoreCase(cmd[0])) {
							gameWorld.subtitles.clear();
						} else if (Consts.ONINTERACT_TAKE.equalsIgnoreCase(cmd[0])) {
							if (cmd[1].length() == 0 || Consts.ONINTERACT_THIS.equals(cmd[1])){
								addToInventory(object);
							} else {
								Entity entity = gameWorld.entityHashMap.get(cmd[1]);
								if (entity != null) {
									if(!inventory.contains(entity)) {
										inventory.add(entity);
										activeInventory = inventory.size() - 1;
									}
								}
							}
						} else if (Consts.ONINTERACT_SET_VAR.equalsIgnoreCase(cmd[0])) {
							String[] split = cmd[1].split(" ", 2);
							setVar(split[0], split[1]);
						} else if (Consts.ONINTERACT_SET_EXAMINE.equalsIgnoreCase(cmd[0])) {
							String[] split = cmd[1].split(" ", 2);
							MapObject ob2 = Consts.ONINTERACT_THIS.equals(split[0]) ? object : findObjectByName(map, split[0]);
							if (ob2 != null) {
								ob2.getProperties().put(Consts.PROP_EXAMINE, split[1]);
							}
						} else if (Consts.ONINTERACT_HIDE.equalsIgnoreCase(cmd[0])) {
							MapObject ob2 = Consts.ONINTERACT_THIS.equals(cmd[1]) ? object : findObjectByName(map, cmd[1]);
							if (ob2 != null) {
								ob2.setVisible(false);
							}
						} else if (Consts.ONINTERACT_SHOW.equalsIgnoreCase(cmd[0])) {
							MapObject ob2 = Consts.ONINTERACT_THIS.equals(cmd[1]) ? object : findObjectByName(map, cmd[1]);
							if (ob2 != null) {
								ob2.setVisible(true);
							}
						} else if (Consts.ONINTERACT_SAY.equalsIgnoreCase(cmd[0])) {
							sayText.append(cmd[1].replace("%N", "\n"));
							sayText.append("\n");
						} else if (Consts.ONINTERACT_SAYXY.equalsIgnoreCase(cmd[0])) {
							String[] split = cmd[1].split(" ", 3);
							gameWorld.speeches.add(new GameWorld.Speech(split[2].replace("%N", "\n"), Float.parseFloat(split[0]), Float.parseFloat(split[1]), false));
						} else if (Consts.ONINTERACT_QSAY.equalsIgnoreCase(cmd[0])) {
							gameWorld.speechQueue.addLast(new GameWorld.Speech(cmd[1].replace("%N", "\n"), 0, 0, false, true));
						} else if (Consts.ONINTERACT_QSAYXY.equalsIgnoreCase(cmd[0])) {
							String[] split = cmd[1].split(" ", 3);
							gameWorld.speechQueue.addLast(new GameWorld.Speech(split[2].replace("%N", "\n"), Float.parseFloat(split[0]), Float.parseFloat(split[1]), false));
						} else if (Consts.ONINTERACT_QTHINK.equalsIgnoreCase(cmd[0])) {
							gameWorld.speechQueue.addLast(new GameWorld.Speech(cmd[1].replace("%N", "\n"), 0, 0, true, true));
						} else if (Consts.ONINTERACT_QTHINKXY.equalsIgnoreCase(cmd[0])) {
							String[] split = cmd[1].split(" ", 3);
							gameWorld.speechQueue.addLast(new GameWorld.Speech(split[2].replace("%N", "\n"), Float.parseFloat(split[0]), Float.parseFloat(split[1]), true));
						} else if (Consts.ONINTERACT_SAYSUB.equalsIgnoreCase(cmd[0])) {
							gameWorld.subtitles.addLast(new GameWorld.Speech(cmd[1].replace("%N", "\n")));
						} else {
							System.out.println("unknown cmd: " + cmd[0]);
						}
					}
				}
				if(sayText.length() > 0){
					sayText.deleteCharAt(sayText.length() - 1);
					return sayText.toString();
				}
			}
		}
		return null;
	}

	public String examine(TiledMap map) {
		MapObject object = findClosestObject(map, new ObjectFilter() {
			@Override
			public boolean filter(MapObject object) {
				return (object.getProperties().get(Consts.PROP_EXAMINE, "", String.class).length() > 0);
			}
		});

		if (object != null && object.getProperties().get(Consts.PROP_EXAMINE, "", String.class).length() > 0) {
			String prefix = object.getProperties().get(Consts.PROP_DISPLAYNAME, "", String.class);
			if (prefix.length() > 0) {
				prefix += ":\n";
			}
			return prefix + object.getProperties().get(Consts.PROP_EXAMINE, "", String.class);
		}

		if (inventory.size() > 0 && inventory.get(activeInventory).examineText.length() > 0) {
			gameWorld.inventoryVisible = true;
			return inventory.get(activeInventory).examineText;
		}

		return null;
	}

	public String useItem(TiledMap map) {
		MapObject object = findClosestObject(map, new ObjectFilter() {
			@Override
			public boolean filter(MapObject object) {
				return (Consts.TYPE_ENTITY.equals(object.getProperties().get(Consts.PROP_TYPE, "", String.class)));
			}
		});
		if (object != null && activeInventory < inventory.size()) {
			Entity entity = inventory.get(activeInventory);
			return entity.useItem(this, object);
		}
		return null;
	}

	protected boolean checkCollisionXY(int colx, int coly, TiledMapTileLayer collisionLayer) {
		if (collisionLayer == null) {
			return true;
		}
		TiledMapTileLayer.Cell cell = collisionLayer.getCell(colx, coly);
		if (cell == null) {
			return true;
		}
		TiledMapTile tile = cell.getTile();
		return tile.getProperties().get(Consts.PROP_COLLISION, false, Boolean.class);
	}

	protected boolean checkCollisionX(float delta, TiledMapTileLayer collisionLayer) {
		float nx = x + vx * delta;
		int colx = (int) Math.floor(nx + Consts.HERO_COL_OFF_VX * Math.signum(vx) + Consts.HERO_COL_OFF_X);
		int coly = (int) Math.floor(y + Consts.HERO_COL_OFF_Y);

		return checkCollisionXY(colx, coly, collisionLayer);
	}

	protected boolean checkCollisionY(float delta, TiledMapTileLayer collisionLayer) {
		float ny = y + vy * delta;
		int colx = (int) Math.floor(x + Consts.HERO_COL_OFF_X);
		int coly = (int) Math.floor(ny + Consts.HERO_COL_OFF_VY * Math.signum(vy) + Consts.HERO_COL_OFF_Y);

		return checkCollisionXY(colx, coly, collisionLayer);
	}

	protected void updateObserve(TiledMap map) {
		MapObject closestObject = findClosestObject(map, new ObjectFilter() {
			@Override
			public boolean filter(MapObject object) {
				return object.getProperties().get(Consts.PROP_DISPLAYNAME, "", String.class).length() > 0;
			}
		});
		if (closestObject != observeLastObject) {
			observeLastObject = closestObject;
			observeLastChange = TimeUtils.millis();
			observeDisplayed = false;
			if(observeLastSpeech != null){
				observeLastSpeech.age = observeLastSpeech.duration - Consts.SPEECH_BUBBLE_ANIM_DURATION;
				observeLastSpeech = null;
			}
		} else if (closestObject != null) {
			if (!gameWorld.speechHeroActive && ! observeDisplayed && observeLastChange + Consts.OBSERVE_MIN_DURATION < TimeUtils.millis()) {
				observeDisplayed = true;
				observeLastSpeech = new GameWorld.Speech(closestObject.getProperties().get(Consts.PROP_DISPLAYNAME, "", String.class), 0, 0, true, true);
				gameWorld.speeches.add(observeLastSpeech);
			}
		}
	}

	public void update(float delta, TiledMap map, TiledMapTileLayer collisionLayer) {
		animationTime += delta;
		if (!checkCollisionX(delta, collisionLayer)) {
			x += vx * delta;
		}
		if (!checkCollisionY(delta, collisionLayer)) {
			y += vy * delta;
		}
		if (vx > 0.01f) {
			animation = HeroAnimation.walk_right;
			lastWalkRight = true;
		} else if (vx < -0.01f) {
			animation = HeroAnimation.walk_left;
			lastWalkRight = false;
		} else if (vy > 0.01f) {
			animation = HeroAnimation.walk_up;
		} else if (vy < -0.01f) {
			animation = HeroAnimation.walk_down;
		} else {
			animation = lastWalkRight ? HeroAnimation.wait_right : HeroAnimation.wait_left;
		}
		updateObserve(map);
	}
}
