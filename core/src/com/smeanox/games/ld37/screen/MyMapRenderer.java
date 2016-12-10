package com.smeanox.games.ld37.screen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.TimeUtils;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.io.Textures;
import com.smeanox.games.ld37.world.GameWorld;
import com.smeanox.games.ld37.world.Hero;


public class MyMapRenderer extends OrthogonalTiledMapRenderer {

	private final GameWorld gameWorld;

	public MyMapRenderer(TiledMap map, float unitScale, Batch batch, GameWorld gameWorld) {
		super(map, unitScale, batch);
		this.gameWorld = gameWorld;
	}

	@Override
	public void renderObject(MapObject object) {
		if (!object.isVisible()) {
			return;
		}
		if (object instanceof TiledMapTileMapObject) {
			renderTiledMapTileMapObject((TiledMapTileMapObject) object);
		}
	}

	protected void renderTiledMapTileMapObject(TiledMapTileMapObject object) {
		final TiledMapTile tile = object.getTile();
		if (tile == null) {
			return;
		}

		final boolean flipX = object.isFlipHorizontally();
		final boolean flipY = object.isFlipVertically();
		TextureRegion region = tile.getTextureRegion();

		float x = (object.getX() + tile.getOffsetX()) * unitScale;
		float y = (object.getY() + tile.getOffsetY()) * unitScale;
		float width = region.getRegionWidth() * unitScale;
		float height = region.getRegionHeight() * unitScale;

		float originX = object.getOriginX();
		float originY = object.getOriginY();
		float scaleX = object.getScaleX();
		float scaleY = object.getScaleY();
		float rotation = -object.getRotation();

		if (object.getName() != null && object.getName().startsWith("hero")) {
			Hero hero = gameWorld.hero;
			if (!object.getName().equals(hero.animation.id)) {
				return;
			}
			region = getAnimationRegion(object, (long) (hero.animationTime * 1000));
			if (region == null) {
				return;
			}
			x = hero.x - width * scaleX * 0.5f;
			y = hero.y - height * scaleY * 0.5f;
		} else if (object.getProperties().get(Consts.PROP_ANIMATIONONLY, "", String.class).length() > 0) {
			String animationonly = object.getProperties().get(Consts.PROP_ANIMATIONONLY, "", String.class);
			if(gameWorld.hero.portalAction != null && animationonly.equals(gameWorld.hero.portalAction.name)){
				region = getAnimationRegion(object, TimeUtils.millis() - gameWorld.hero.portalAction.start);
			} else {
				region = getAnimationRegion(object, 0);
			}
		}

		int srcX = region.getRegionX();
		int srcY = region.getRegionY();
		int srcWidth = region.getRegionWidth();
		int srcHeight = region.getRegionHeight();

		batch.draw(region.getTexture(), x, y, originX, originY, width, height, scaleX, scaleY, rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
	}

	protected TextureRegion getAnimationRegion(TiledMapTileMapObject object, long animationTime){
		if(!(object.getTile() instanceof AnimatedTiledMapTile)){
			return object.getTile().getTextureRegion();
		}

		if (object.getProperties().get(Consts.PROP_ANIMATIONONLY, "", String.class).length() > 0) {
			animationTime = animationTime;
		}
		AnimatedTiledMapTile tile = (AnimatedTiledMapTile) object.getTile();
		int[] animationIntervals = tile.getAnimationIntervals();
		StaticTiledMapTile[] frameTiles = tile.getFrameTiles();

		int loopDuration = 0;
		for (int i : animationIntervals) {
			loopDuration += i;
		}

		animationTime %= loopDuration;

		for(int i = 0; i < animationIntervals.length; i++) {
			if (animationTime <= animationIntervals[i]) {
				return frameTiles[i].getTextureRegion();
			}
			animationTime -= animationIntervals[i];
		}
		return null;
	}
}
