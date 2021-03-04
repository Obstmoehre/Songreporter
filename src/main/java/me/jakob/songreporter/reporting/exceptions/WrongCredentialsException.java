package me.jakob.songreporter.reporting.exceptions;

public class WrongCredentialsException extends CCLILoginException {

    public WrongCredentialsException() {
        super("The credentials given are invalid");
    }
}
