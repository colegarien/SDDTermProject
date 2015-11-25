package edu.uco.schambers.classmate.AdapterModels;

import java.util.Date;

/**
 * Created by Nelson.
 */

public class Attendance {
    private int enrollmentId;
    private Date date;

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
