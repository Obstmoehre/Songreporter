import javax.swing.*;

class ErrorGUI {

    // a little GUI to show important error messages to the user

    void showNewErrorMessage(String message) {
        JFrame errorWindow = new JFrame();

        JTextArea errorTextArea = new JTextArea(10, 45);
        errorTextArea.setText(message);
        errorTextArea.setEditable(false);

        errorWindow.add(errorTextArea);
        errorWindow.setSize(900, 400);
        errorWindow.setVisible(true);
    }
}
