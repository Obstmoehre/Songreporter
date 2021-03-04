package me.jakob.songreporter.REST;

public class Songdetails {
    private String id;
    private String title;
    private String ccliSongNo;
    private boolean publicDomain;

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
}
