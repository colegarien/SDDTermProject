package edu.uco.schambers.classmate.ObservableManagers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Author: Wenxi Zeng
 * Purpose:
 *   Use singleton pattern to hold a reference of groupOwnerAddress.
 *   This class is also observable.
 *   If you want to get a notification of group owner address changes,
 *   you'll need to add a observer to it.
 *   Otherwise, there is no extra work to get the group owner address.
 *
 * Edit: 10/12/2015
 *   Added Student IPs list for use by the teacher
 */
public class IPAddressManager extends Observable{

    private InetAddress groupOwnerAddress;
    private ArrayList<InetAddress> studentAddresses = new ArrayList<InetAddress>();
    private static volatile IPAddressManager instance = null;
    private IPAddressManager() { }

    public InetAddress getGroupOwnerAddress() { return groupOwnerAddress; }
    public ArrayList<InetAddress> getStudentAddresses() {
        return studentAddresses;
    }

    public void setGroupOwnerAddress(InetAddress groupOwnerAddress) {
        this.groupOwnerAddress = groupOwnerAddress;
        directNotifyObservers(this.groupOwnerAddress);
    }

    public void addStudentAddress(InetAddress studentAddress){
        if(studentAddress != null){
            studentAddresses.add(studentAddress);
        }
    }

    public static synchronized IPAddressManager getInstance() {
        if (instance == null) {
            instance = new IPAddressManager();
        }

        return instance;
    }

    public void directNotifyObservers(Object data){
        instance.setChanged();
        instance.notifyObservers(data);
    }
}
