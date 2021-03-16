package me.jakob.songreporter.reporting.objects;

public class ReportPayload {
    String id;
    String date;
    Song[] songs;
    RecordedBy recordedBy;
    Categories lyrics;
    String[] sheetMusic;
    String[] rehearsals;
    String[] cslps;

    public ReportPayload(Song song, Categories categories) {
        this.songs = new Song[]{song};
        this. id = "";
        this. date = "";
        this.recordedBy = new RecordedBy();
        this.lyrics = categories;
        this.sheetMusic = new String[0];
        this.rehearsals = new String[0];
        this.cslps = new String[0];
    }
}
