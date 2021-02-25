package me.jakob.songreporter.reporting.exceptions;

public class NoSearchResultsException extends Exception {

    public NoSearchResultsException(String ccli) {
        super("No search results for song with this ccli-songnumber: " +
                ccli + ". Please check again manually.");
    }
}
