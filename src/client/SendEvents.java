package client;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.*;

public class SendEvents implements KeyListener, MouseMotionListener, MouseWheelListener, MouseListener {
    private Socket cSocket = null;
    private JPanel cPanel = null;
    private PrintWriter writer = null;
    String width = "", height = "";
    double w;
    double h;

    SendEvents(Socket s, JPanel p, String width, String height) {
        cSocket = s;
        cPanel = p;
        this.width = width;
        this.height = height;
        w = Double.valueOf(width.trim()).doubleValue();
        h = Double.valueOf(width.trim()).doubleValue();

        cPanel.addKeyListener(this);
        cPanel.addMouseListener(this);
        cPanel.addMouseMotionListener(this);
        cPanel.addMouseWheelListener(this);

        try {
            writer = new PrintWriter(cSocket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void mouseDragged(MouseEvent e) {
        double xScale = (double) w / cPanel.getWidth();
        double yScale = (double) h / cPanel.getHeight();
        writer.println(Commands.MOUSE_DRAGGED.getAbbrev());
        writer.println((int) (e.getX() * xScale));
        writer.println((int) (e.getY() * yScale));
        writer.flush();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        double xScale = (double) w / cPanel.getWidth();
        double yScale = (double) h / cPanel.getHeight();
        writer.println(Commands.MOVE_MOUSE.getAbbrev());
        writer.println((int) (e.getX() * xScale));
        writer.println((int) (e.getY() * yScale));
        writer.flush();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        writer.println(Commands.CLICK_MOUSE.getAbbrev());
        writer.println(e.getButton());
        writer.flush();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        writer.println(Commands.PRESS_MOUSE.getAbbrev());
        int button = e.getButton();
        int xButton = 16;
        if (button == MouseEvent.BUTTON3) {
            xButton = 4;
        }

        writer.println(xButton);
        writer.flush();
    }

    public void mouseReleased(MouseEvent e) {
        writer.println(Commands.RELEASE_MOUSE.getAbbrev());
        int button = e.getButton();
        int xButton = 16;
        if (button == MouseEvent.BUTTON3) {
            xButton = 4;
        }

        writer.println(xButton);
        writer.flush();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        // check if the key pressed is ctrl + c
        if (e.getKeyCode() == KeyEvent.VK_C && e.isControlDown()) {
            System.out.println("Copying text from remote computer");
        } else if (e.getKeyCode() == KeyEvent.VK_V && e.isControlDown()) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable transferable = clipboard.getContents(null);
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    String content = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                    System.out.println("Pasting text: " + content);
                    StringSelection stringSelection = new StringSelection(content);
                    clipboard.setContents(stringSelection, stringSelection);

                    // Send the content of the clipboard to the server
                    writer.println(Commands.PASTE_TEXT.getAbbrev());
                    // writer.println(content);
                    writer.flush();
                } catch (UnsupportedFlavorException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            writer.println(Commands.PRESS_KEY.getAbbrev());
            writer.println(e.getKeyCode());
            writer.flush();
        }
    }

    public void keyReleased(KeyEvent e) {
        writer.println(Commands.RELEASE_KEY.getAbbrev());
        writer.println(e.getKeyCode());
        writer.flush();
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        writer.println(Commands.MOUSE_WHEEL_MOVED.getAbbrev());
        writer.println(e.getWheelRotation());
        writer.flush();
    }
}
