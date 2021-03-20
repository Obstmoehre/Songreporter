package me.jakob.songreporter.reporting.exceptions;

public class TimeoutException extends CCLILoginException {

    public TimeoutException() {
        super("timed out while connecting to ccli online reportin website");
    }
}
