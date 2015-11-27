package edu.uco.schambers.classmate.AdapterModels;

/**
 * Created by Nelson.
 */

public class StudentAbsenceByClass {
    private String rollCall;
    private boolean isPresent;

    public String getRollCall() {
        return rollCall;
    }

    public void setRollCall(String rollCall) {
        this.rollCall = rollCall;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setIsPresent(boolean isPresent) {
        this.isPresent = isPresent;
    }
}
