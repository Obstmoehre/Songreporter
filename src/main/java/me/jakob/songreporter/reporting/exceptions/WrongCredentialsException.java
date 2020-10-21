package me.jakob.songreporter.reporting.exceptions;

public class WrongCredentialsException extends Exception {

    public WrongCredentialsException() {
        super("The credentials given are invalid");
    }
}
