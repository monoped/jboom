package de.monoped.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class ColorButton
    extends JButton
    implements ActionListener
{
    private Color       color;
    private int w;
    private int h;
    private String      title;
    private java.util.List<ColorChangeListener>  listeners;

    //----------------------------------------------------------------------

    public void addColorChangeListener(ColorChangeListener listener)
    {
        listeners.add(listener);
    }

    //----------------------------------------------------------------------

    private void dispatchColorChangeEvent(ColorChangeEvent e)
    {
        for (ColorChangeListener listener: listeners)
            listener.colorChanged(e);
    }

    //----------------------------------------------------------------------

    public Color getColor()
    {
        return color;
    }

    //----------------------------------------------------------------------

    public void setColor(Color color)
    {
        BufferedImage   img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics        g = img.getGraphics();

        g.setColor(color);
        g.fillRect(0, 0, w, h);
        g.dispose();
        this.color = color;
        setIcon(new ImageIcon(img));
    }

    //----------------------------------------------------------------------

    public void actionPerformed(ActionEvent e)
    {
        Color col = JColorChooser.showDialog(this, title, color);

        if (col != null)
        {
            setColor(col);
            setText(null);
            repaint();
            dispatchColorChangeEvent(new ColorChangeEvent(this, col));
        }
    }
}

