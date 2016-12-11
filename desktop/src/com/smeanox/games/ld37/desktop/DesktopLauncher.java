package com.smeanox.games.ld37.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.LD37;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Consts.WIDTH;
		config.height = Consts.HEIGHT;
		config.title = Consts.GAME_NAME;
		new LwjglApplication(new LD37(), config);
	}
}
