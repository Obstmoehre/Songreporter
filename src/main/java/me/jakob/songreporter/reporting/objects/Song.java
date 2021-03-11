package me.jakob.songreporter.reporting.objects;

import me.jakob.songreporter.reporting.enums.Reason;

public class Song {
    private String id;
    private String title;
    private String ccliSongNo;
    private boolean publicDomain;
    private transient boolean reported;
    private transient Reason reason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCcliSongNo() {
        return ccliSongNo;
    }

    public void setCcliSongNo(String ccliSongNo) {
        this.ccliSongNo = ccliSongNo;
    }

    public boolean isPublicDomain() {
        return publicDomain;
    }

    public void setPublicDomain(boolean publicDomain) {
        this.publicDomain = publicDomain;
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
