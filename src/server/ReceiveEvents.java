package server;

import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ReceiveEvents extends Thread {
    Socket socket = null;
    Robot robot = null;
    boolean continueLoop = true;

    public ReceiveEvents(Socket socket, Robot robot) {
        this.socket = socket;
        this.robot = robot;
        start();
    }

    public void run() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(socket.getInputStream());

            while (continueLoop) {
                int command = scanner.nextInt();
                switch (command) {
                    case -1:
                        robot.mousePress(scanner.nextInt());
                        break;
                    case -2:
                        robot.mouseRelease(scanner.nextInt());
                        break;
                    case -3:
                        robot.keyPress(scanner.nextInt());
                        break;
                    case -4:
                        robot.keyRelease(scanner.nextInt());
                        break;
                    case -5:
                        robot.mouseMove(scanner.nextInt(), scanner.nextInt());
                        break;
                    case -7:
                        robot.mouseWheel(scanner.nextInt());
                        break;
                    case -8:
                        robot.mouseMove(scanner.nextInt(), scanner.nextInt());
                        break;
                    case -9:
                        // Get the text to copy from the message
                        String textToCopy = scanner.nextLine().trim();
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                        // Set the contents of the clipboard to the text to copy
                        StringSelection stringSelection = new StringSelection(textToCopy);
                        clipboard.setContents(stringSelection, stringSelection);
                        break;
                    case -10:
                        // Get the contents of the clipboard
                        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        Transferable transferable = clipboard.getContents(null);
                        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                            try {
                                String content = (String) transferable.getTransferData(DataFlavor.stringFlavor);

                                // Simulate the "Paste" command
                                stringSelection = new StringSelection(content);
                                clipboard.setContents(stringSelection, stringSelection);
                                robot.keyPress(KeyEvent.VK_CONTROL);
                                robot.keyPress(KeyEvent.VK_V);
                                robot.keyRelease(KeyEvent.VK_V);
                                robot.keyRelease(KeyEvent.VK_CONTROL);
                            } catch (UnsupportedFlavorException | IOException | HeadlessException
                                    | IllegalArgumentException ex) {
                                ex.printStackTrace();
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
