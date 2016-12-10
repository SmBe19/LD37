package com.smeanox.games.ld37.world;

import java.util.Observable;

public class ObservableValue<T> extends Observable {
	private T val;

	public ObservableValue(T val) {
		this.val = val;
	}

	public T get(){
		return val;
	}

	public void set(T val) {
		this.val = val;
		setChanged();
		notifyObservers();
	}
}
