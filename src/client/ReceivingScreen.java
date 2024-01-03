package client;

import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Robot;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import javax.swing.JPanel;
import javax.imageio.ImageIO;

public class ReceivingScreen extends Thread {
    private ObjectInputStream cObjectInputStream = null;
    private JPanel cPanel = null;
    private boolean continueLoop = true;
    InputStream oin = null;
    Image image1 = null;

    public ReceivingScreen(InputStream in, JPanel p) {
        oin = in;
        cPanel = p;
        start();
    }

    public void run() {
        try {
            while (continueLoop) {
                byte[] bytes = new byte[1024 * 1024];
                int count = 0;
                do {
                    count += oin.read(bytes, count, bytes.length - count);
                } while (!(count > 4 && bytes[count - 2] == (byte) -1 && bytes[count - 1] == (byte) -39));

                image1 = ImageIO.read(new ByteArrayInputStream(bytes));
                image1 = image1.getScaledInstance(cPanel.getWidth(), cPanel.getHeight(), Image.SCALE_FAST);

                Graphics g = cPanel.getGraphics();
                g.drawImage(image1, 0, 0, cPanel.getWidth(), cPanel.getHeight(), cPanel);

                DataInputStream dis = new DataInputStream(oin);
                int serverX = dis.readInt();
                int serverY = dis.readInt();

                // Move the mouse to the corresponding position
                Robot robot = new Robot();
                robot.mouseMove(serverX, serverY);
            }
        } catch (IOException | AWTException ex) {
            ex.printStackTrace();
        }

    }
}
