package com.smeanox.games.ld37.io;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public enum Sounds {
	door("snd/door.wav"),
	door2("snd/door2.wav"),
	fire("snd/fire.wav"),
	pour("snd/pour.wav"),
	punch("snd/punch.wav"),
	rockfall("snd/rockfall.wav"),
	stairs("snd/stairs.wav"),
	watersplash("snd/watersplash.wav"),
	;

	public final Sound sound;

	Sounds(String path) {
		sound = Gdx.audio.newSound(Gdx.files.internal(path));
	}
}
