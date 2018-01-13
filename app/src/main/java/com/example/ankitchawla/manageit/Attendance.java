package com.example.ankitchawla.manageit;

/**
 * Created by Ankit Chawla on 11-11-2017.
 */

class Attendance {
    String atdate;
    String attend;

    public Attendance(String atdate, String attend) {
        this.atdate = atdate;
        this.attend = attend;
    }

    public Attendance() {
    }

    public String getAtdate() {
        return atdate;
    }

    public void setAtdate(String atdate) {
        this.atdate = atdate;
    }

    public String getAttend() {
        return attend;
    }

    public void setAttend(String attend) {
        this.attend = attend;
    }


}
