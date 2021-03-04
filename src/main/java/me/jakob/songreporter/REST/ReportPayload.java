package me.jakob.songreporter.REST;

import me.jakob.songreporter.reporting.objects.Song;

import java.util.Date;

public class ReportPayload {
    String id;
    String date;
    Songdetails[] songs;
    RecordedBy recordedBy;
    Categories lyrics;
    String[] sheetMusic;
    String[] rehearsals;
    String[] cslps;

    public ReportPayload(Songdetails[] songs) {
        this.songs = songs;
        this. id = "";
        this. date = "";
        this.recordedBy = new RecordedBy();
        this.lyrics = new Categories(0, "1", "1", 0);
        this.sheetMusic = new String[0];
        this.rehearsals = new String[0];
        this.cslps = new String[0];
    }
}
