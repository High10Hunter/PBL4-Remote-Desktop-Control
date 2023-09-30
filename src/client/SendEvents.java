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
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.*;

public class SendEvents implements KeyListener, MouseMotionListener, MouseListener {
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

        try {
            writer = new PrintWriter(cSocket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void mouseDragged(MouseEvent e) {
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

        // Check for clipboard contents
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String selectedText = "";
            try {
                selectedText = (String) contents.getTransferData(DataFlavor.stringFlavor);
                clipboard.setContents(new StringSelection(""), null); // Clear clipboard
                writer.println(Commands.PASTE.getAbbrev());
                writer.println(selectedText);
                writer.flush();
            } catch (UnsupportedFlavorException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
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
        writer.println(Commands.PRESS_KEY.getAbbrev());
        writer.println(e.getKeyCode());
        writer.flush();

        // Check for clipboard contents
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String selectedText = (String) contents.getTransferData(DataFlavor.stringFlavor);
                clipboard.setContents(new StringSelection(""), null); // Clear clipboard
                writer.println(Commands.PASTE.getAbbrev());
                writer.println(selectedText);
                writer.flush();
            } catch (UnsupportedFlavorException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        writer.println(Commands.RELEASE_KEY.getAbbrev());
        writer.println(e.getKeyCode());
        writer.flush();
    }

}
