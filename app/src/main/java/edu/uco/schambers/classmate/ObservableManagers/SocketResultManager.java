package edu.uco.schambers.classmate.ObservableManagers;

import java.util.Observable;

/**
 * Created by WenHsi on 10/12/2015.
 */
public class SocketResultManager  extends Observable {

    private static volatile SocketResultManager instance = null;
    private SocketResultManager() { }

    public static synchronized SocketResultManager getInstance() {
        if (instance == null) {
            instance = new SocketResultManager();
        }

        return instance;
    }

    public void directNotifyObservers(Object data){
        instance.setChanged();
        instance.notifyObservers(data);
    }
}
