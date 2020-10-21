package me.jakob.songreporter.reporting;

public class SongNotLicencedException extends Exception {

    public SongNotLicencedException(Song song) {
        super("The Song " + song.getName() + "(CCLI Songnumber: " + song.getCcliNumber() + ") is under no licence");
    }
}
