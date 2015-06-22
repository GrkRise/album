package ru.fegol.album2.core.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Андрей on 17.06.2015.
 */
public abstract class Observable {

    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer){
        observers.add(observer);
    }

    public void removeObserver(Observer observer){
        observers.remove(observer);
    }

    protected void notifyObservers(){
        for (Observer o: observers){
            o.update();
        }
    }
}
