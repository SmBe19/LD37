package com.smeanox.games.ld37.world;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.utils.TimeUtils;
import com.smeanox.games.ld37.Consts;
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
		walk_down("hero_walk_down"),;

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

	public final GameWorld gameWorld;
	public float x, y, vx, vy;
	public float animationTime;
	public HeroAnimation animation;
	public PortalAction portalAction;
	public final List<Entity> inventory;
	public int activeInventory;
	public final Map<String, String> variables;
	private boolean lastWalkRight;

	public Hero(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
		inventory = new ArrayList<Entity>();
		variables = new HashMap<String, String>();
		animation = HeroAnimation.wait_right;
		lastWalkRight = true;
		portalAction = null;
		activeInventory = 0;
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
				if (!object.isVisible() || !filter.filter(object)) {
					continue;
				}
				float dist = getDistance2(object, x, y);
				if (dist < minDist) {
					minOb = object;
					minDist = dist;
				}
			}
		}
		if (minDist > Consts.HERO_INTERACT_RAD) {
			return null;
		}
		return minOb;
	}

	protected MapObject findObjectByName(TiledMap map, String name) {
		for (MapLayer layer : map.getLayers()) {
			if (!layer.isVisible()) {
				continue;
			}
			for (MapObject object : layer.getObjects()) {
				if (!object.isVisible()) {
					continue;
				}
				if (name.equals(object.getName())) {
					return object;
				}
			}
		}
		return null;
	}

	public String interact(TiledMap map) {
		MapObject object = findClosestObject(map, new ObjectFilter() {
			@Override
			public boolean filter(MapObject object) {
				return ((object.getProperties().get(Consts.PROP_ONINTERACT, 0, Integer.class) > 0)
						|| (Consts.TYPE_EXIT.equals(object.getProperties().get(Consts.PROP_TYPE, "", String.class))))
						&& (object.getProperties().get(Consts.PROP_ACTIVE, true, Boolean.class));
			}
		});
		if (object != null) {
			if (Consts.TYPE_EXIT.equals(object.getProperties().get(Consts.PROP_TYPE, "", String.class))) {
				portalAction = new PortalAction(object.getProperties().get(Consts.PROP_PORTALID, "", String.class),
						object.getName(), object.getProperties().get(Consts.PROP_TOLEVEL, "", String.class),
						TimeUtils.millis() - object.getProperties().get(Consts.PROP_START, 0, Integer.class),
						object.getProperties().get(Consts.PROP_DELAY, 0, Integer.class));
			} else {
				int oninteract = object.getProperties().get(Consts.PROP_ONINTERACT, 0, Integer.class);
				for (int i = 0; i < oninteract; i++) {
					String[] cmd = object.getProperties().get(Consts.PROP_ONINTERACT + "_" + i, "", String.class).split(" ", 2);
					if (Consts.ONINTERACT_CLASS.equalsIgnoreCase(cmd[0])) {
						Entity entity = gameWorld.entityHashMap.get(object.getName());
						if (entity != null) {
							entity.interact(this);
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
					} else if (Consts.ONINTERACT_TAKE.equalsIgnoreCase(cmd[0])) {
						Entity entity = gameWorld.entityHashMap.get(object.getName());
						if (entity != null) {
							inventory.add(entity);
							object.setVisible(false);
						}
					} else if (Consts.ONINTERACT_SET_VAR.equalsIgnoreCase(cmd[0])) {
						String[] split = cmd[1].split(" ", 2);
						variables.put(split[0], split[1]);
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
					}
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
			return object.getProperties().get(Consts.PROP_EXAMINE, "", String.class);
		}
		return null;
	}

	public String useItem(TiledMap map) {
		MapObject object = findClosestObject(map, new ObjectFilter() {
			@Override
			public boolean filter(MapObject object) {
				return (object.getProperties().get(Consts.PROP_EXAMINE, "", String.class).length() > 0);
			}
		});
		if (object != null) {
			Entity entity = gameWorld.entityHashMap.get(object.getName());
			if (entity != null) {
				entity.useItem(this);
			}
		}
		return null;
	}

	protected boolean checkCollisionXY(int colx, int coly, TiledMapTileLayer collisionLayer) {
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

	public void update(float delta, TiledMapTileLayer collisionLayer) {
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
	}
}
