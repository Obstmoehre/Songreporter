package me.jakob.songreporter.reporting;

public class Song {
    private String name;
    private String ccliNumber;
    private boolean reported;
    private String reason = "";

    public Song(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCcliNumber() {
        return ccliNumber;
    }

    public void setCcliNumber(String ccliNumber) {
        this.ccliNumber = ccliNumber;
    }

    public boolean isReported() {
        return reported;
    }

    public void markUnreported(String reason) {
        reported = false;
        this.reason = reason;
    }

    public void markReported() {
        this.reported = true;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
