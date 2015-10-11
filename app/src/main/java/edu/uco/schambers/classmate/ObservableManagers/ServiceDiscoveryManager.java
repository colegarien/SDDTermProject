package edu.uco.schambers.classmate.ObservableManagers;

import java.util.Observable;

/**
 * Created by WenHsi on 10/5/2015.
 */
public class ServiceDiscoveryManager extends Observable {

    private static volatile ServiceDiscoveryManager instance = null;
    private ServiceDiscoveryManager() { }

    public static synchronized ServiceDiscoveryManager getInstance() {
        if (instance == null) {
            instance = new ServiceDiscoveryManager();
        }

        return instance;
    }

    public void directNotifyObservers(Object data){
        instance.setChanged();
        instance.notifyObservers(data);
    }
}
