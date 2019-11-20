package me.jakob.GUI;

import javax.swing.*;

public class ErrorGUI {

    // a little me.jakob.GUI to show important error messages to the user

    public void showNewErrorMessage(String message) {
        JFrame errorWindow = new JFrame();

        JTextArea errorTextArea = new JTextArea(10, 45);
        errorTextArea.setText(message);
        errorTextArea.setEditable(false);

        errorWindow.add(errorTextArea);
        errorWindow.setSize(900, 400);
        errorWindow.setVisible(true);
    }
}
