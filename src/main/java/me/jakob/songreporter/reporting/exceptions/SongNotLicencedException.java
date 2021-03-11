package me.jakob.songreporter.reporting.exceptions;

public class SongNotLicencedException extends Exception {

    public SongNotLicencedException(Song song) {
        super("The Song " + song.getName() + "(CCLI Songnumber: " + song.getCcliNumber() + ") is under no licence");
    }
}
