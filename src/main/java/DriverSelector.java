import javax.swing.*;
import java.awt.*;
import java.io.File;

class DriverSelector {

    String selectDriver() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setPreferredSize(new Dimension(900, 700));
        fileChooser.setDialogTitle("Browser Treiber auswählen");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.showDialog(null, "Auswählen");
        File driver = fileChooser.getSelectedFile();
        return driver.getPath();
    }

}
