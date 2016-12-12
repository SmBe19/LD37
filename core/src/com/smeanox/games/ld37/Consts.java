package com.smeanox.games.ld37;

import com.badlogic.gdx.Input;

public class Consts {
	public static final String GAME_NAME = "Room 51";
	public static final String GAME_CREDITS = "Matteo Signer\nGianluca Vagli\nBenjamin Schmid\nPhilipp Wallimann";
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final float SCALE = 1/48f;
	public static final int TEX_SIZE = 16;
	public static final float UNIT_SCALE = 1.f / TEX_SIZE;

	public static final float CAMERA_BORDER_X = 6;
	public static final float CAMERA_BORDER_Y = 5;
	public static final float INVENTORY_ACTIVE_SIZE = 1;
	public static final float FONT_SIZE = 1.f/16;
	public static final float FONT_LINE_SPACING = 1.125f;
	public static final float FONT_LINE_SPACING_CREDITS = 1.5f;
	public static final long OBSERVE_MIN_DURATION = 1000;
	public static final float SPEECH_BUBBLE_OFFSET = 0.5f;
	public static final float SPEECH_BUBBLE_SIZE = 0.5f;
	public static final float SPEECH_BUBBLE_DURATION = 2f;
	public static final float SPEECH_BUBBLE_DURATION_PER_CHAR = 0.03f;
	public static final float SPEECH_BUBBLE_ANIM_OFFSET = 0.5f;
	public static final float SPEECH_BUBBLE_ANIM_DURATION = 0.5f;
	public static final float SUBTITLES_OFFSET_Y = 0.5f;

	public static final float FADE_DURATION = 1;

	public static final String LAYER_META = "meta";
	public static final String LAYER_COLLISION = "collision";
	public static final String TYPE_EXIT = "exit";
	public static final String TYPE_ENTITY = "entity";
	public static final String PROP_FROMLEVEL = "fromlevel";
	public static final String PROP_TOLEVEL = "tolevel";
	public static final String PROP_ACTIVE = "active";
	public static final String PROP_PORTALID = "portalid";
	public static final String PROP_DELAY = "delay";
	public static final String PROP_START = "start";
	public static final String PROP_COLLISION = "collision";
	public static final String PROP_ONINTERACT = "oninteract";
	public static final String PROP_ONNOINTERACT = "onnointeract";
	public static final String PROP_EXAMINE = "examine";
	public static final String PROP_ANIMATIONONLY = "animationonly";
	public static final String PROP_DISPLAYNAME = "displayname";
	public static final String PROP_TYPE = "type";
	public static final String PROP_MAPSCALE = "mapscale";
	public static final String PROP_VELOSCALE = "veloscale";
	public static final String PROP_DRAWBACKGROUND = "drawbackground";
	public static final String PROP_ANIMATIONSTART = "animationstart";
	public static final String PROP_ANIMATIONONCE = "animationonce";

//	public static final int INPUT_MOVE_UP = Input.Keys.UP;
//	public static final int INPUT_MOVE_LEFT = Input.Keys.LEFT;
//	public static final int INPUT_MOVE_DOWN = Input.Keys.DOWN;
//	public static final int INPUT_MOVE_RIGHT = Input.Keys.RIGHT;
	public static final int INPUT_MOVE_UP = Input.Keys.W;
	public static final int INPUT_MOVE_LEFT = Input.Keys.A;
	public static final int INPUT_MOVE_DOWN = Input.Keys.S;
	public static final int INPUT_MOVE_RIGHT = Input.Keys.D;
	public static final int INPUT_INTERACT = Input.Keys.F;
	public static final int INPUT_EXAMINE = Input.Keys.E;
	public static final int INPUT_USE_ITEM = Input.Keys.Q;
	public static final int INPUT_INVENTORY = Input.Keys.C;
	public static final int INPUT_INVENTORY_NEXT = Input.Keys.R;
	public static final int INPUT_SKIP = Input.Keys.ENTER;

	public static final float HERO_VELO_X = 4;
	public static final float HERO_VELO_Y = 2;
	public static final float HERO_COL_OFF_X = 0f;
	public static final float HERO_COL_OFF_Y = -0.9f;
	public static final float HERO_COL_OFF_VX = 0.35f;
	public static final float HERO_COL_OFF_VY = 0.1f;
	public static final float HERO_INTERACT_RAD = 1;

	public static final String ONINTERACT_THIS = "$this";
	public static final String ONINTERACT_CLASS = "class";
	public static final String ONINTERACT_TAKE = "take";
	public static final String ONINTERACT_PAUSE = "pause";
	public static final String ONINTERACT_PAUSEWALK = "pausewalk";
	public static final String ONINTERACT_UNPAUSEWALK = "unpausewalk";
	public static final String ONINTERACT_CLEARSPEECH = "clearspeech";
	public static final String ONINTERACT_CLEARSUBS = "clearsubs";
	public static final String ONINTERACT_ACTIVATE = "activate";
	public static final String ONINTERACT_DEACTIVATE = "deactivate";
	public static final String ONINTERACT_SET_EXAMINE = "set_examine";
	public static final String ONINTERACT_SET_VAR = "setvar";
	public static final String ONINTERACT_HIDE = "hide";
	public static final String ONINTERACT_SHOW = "show";
	public static final String ONINTERACT_SAY = "say";
	public static final String ONINTERACT_SAYXY = "sayxy";
	public static final String ONINTERACT_QSAY = "qsay";
	public static final String ONINTERACT_QSAYXY = "qsayxy";
	public static final String ONINTERACT_QTHINK = "qthink";
	public static final String ONINTERACT_QTHINKXY = "qthinkxy";
	public static final String ONINTERACT_SAYSUB = "saysub";
}
