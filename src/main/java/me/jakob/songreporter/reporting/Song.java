package me.jakob.songreporter.reporting;

public class Song {
    private String name;
    private String ccliNumber;
    private boolean reported;
    private Reason reason;

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

    public void markUnreported(Reason reason) {
        reported = false;
        this.reason = reason;
    }

    public void markReported() {
        this.reported = true;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }
}

