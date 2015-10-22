package edu.uco.schambers.classmate.ObservableManagers;

import java.util.Observable;

/**
 * Created by Cole Garien on 10/22/2015.
 */
public class StudentAttendanceObservable extends Observable {
    private static volatile StudentAttendanceObservable instance = null;
    private StudentAttendanceObservable() { }

    public static synchronized StudentAttendanceObservable getInstance() {
        if (instance == null) {
            instance = new StudentAttendanceObservable();
        }

        return instance;
    }

    public void directNotifyObservers(Object data){
        instance.setChanged();
        instance.notifyObservers(data);
    }
}
