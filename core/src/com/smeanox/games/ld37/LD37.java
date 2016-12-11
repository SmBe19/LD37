package com.smeanox.games.ld37;

import com.badlogic.gdx.Game;
import com.smeanox.games.ld37.io.Level;
import com.smeanox.games.ld37.screen.GameScreen;

public class LD37 extends Game {

	private GameScreen gameScreen;

	@Override
	public void create() {
		gameScreen = new GameScreen(Level.lvl_ward);

		showGameScreen();
	}

	public void showGameScreen(){
		setScreen(gameScreen);
	}
}
