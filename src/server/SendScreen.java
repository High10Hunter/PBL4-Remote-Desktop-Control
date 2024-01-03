package server;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.imageio.ImageIO;

public class SendScreen extends Thread {
    Socket socket = null;
    Robot robot = null;
    Rectangle rectangle = null;
    boolean continueLoop = true;
    OutputStream oos = null;

    public SendScreen(Socket socket, Robot robot, Rectangle rectangle) {
        this.socket = socket;
        this.robot = robot;
        this.rectangle = rectangle;
        start();
    }

    public void run() {
        try {
            oos = socket.getOutputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        while (continueLoop) {
            BufferedImage image = robot.createScreenCapture(rectangle);
            DataOutputStream dos = new DataOutputStream(oos);
            try {
                ImageIO.write(image, "jpeg", oos);

                // Send the scaled mouse coordinates
                Point mousePoint = MouseInfo.getPointerInfo().getLocation();
                double widthRatio = rectangle.width / (double) image.getWidth();
                double heightRatio = rectangle.height / (double) image.getHeight();
                int serverX = (int) (mousePoint.x * widthRatio);
                int serverY = (int) (mousePoint.y * heightRatio);
                dos.writeInt(serverX);
                dos.writeInt(serverY);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
