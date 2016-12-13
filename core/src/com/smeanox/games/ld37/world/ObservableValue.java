package com.smeanox.games.ld37.world;

import java.util.ArrayList;
import java.util.List;

public class ObservableValue<T> {
	private T val;
	private List<Observer<T>> observers;

	public interface Observer<S>{
		void update(ObservableValue<S> value);
	}

	public ObservableValue(T val) {
		this.val = val;
		this.observers = new ArrayList<Observer<T>>();
	}

	public T get(){
		return val;
	}

	public void set(T val) {
		this.val = val;
		notifyObservers();
	}

	public void addObserver(Observer observer){
		observers.add(observer);
	}

	public void notifyObservers(){
		for(Observer<T> observer: observers){
			observer.update(this);
		}
	}
}
