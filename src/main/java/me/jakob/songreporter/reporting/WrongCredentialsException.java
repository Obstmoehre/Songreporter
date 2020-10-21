package me.jakob.songreporter.reporting;

public class WrongCredentialsException extends Exception {

    public WrongCredentialsException() {
        super("The credentials given are invalid");
    }
}
