package UI.Swing;

import connect4.model.State;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class Slot extends JPanel {
    private Color color;
    private Color highlightColor = null;
    private static final int HIGHLIGHT_OFFSET = 20;

    public Slot(Color color) {
        this.color = color;
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int height = getHeight();
        int width = getWidth();
        int diameter = width;
        //Avoid cropped circles.
        if (height < diameter) {
            diameter = height;
        }

        int newX = (width /2 - diameter / 2);
        int newY = (height /2 - diameter / 2);

        g2d.setColor(color);
        g2d.fillOval(newX, newY, diameter, diameter);

        diameter -= HIGHLIGHT_OFFSET;
         newX = (width /2 - diameter / 2);
         newY = (height /2 - diameter / 2);

        if (highlightColor != null) {
            g2d.setColor(highlightColor);
            g2d.fillOval(newX, newY, diameter, diameter);
        }
    }

    public void changeColor(Color color) {
        this.color = color;
        repaint();
    }

    public void highlight(Color color) {
        this.highlightColor = color;
        repaint();
    }


}
