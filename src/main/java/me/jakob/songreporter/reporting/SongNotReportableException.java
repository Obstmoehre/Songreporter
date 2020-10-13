package me.jakob.songreporter.reporting;

public class SongNotReportableException extends Exception {

    public SongNotReportableException(Song song) {
        super("The Song " + song.getName() + "(CCLI Songnumber: " + song.getCcliNumber() + ") is not reportable. Presumably because" +
                "it is under no license.");
    }
}
