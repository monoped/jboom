package de.monoped.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

public class ColorButton
    extends JButton
    implements ActionListener
{
    private boolean     defined;
    private Color       color;
    private int         x, y, w, h;
    private String      title;
    private java.util.List<ColorChangeListener>  listeners;

    //----------------------------------------------------------------------

    public ColorButton(int w, int h, Color color, String title, boolean defined)
    {
        this.w = w;
        this.h = h;
        this.title = title;

        if (defined)
            setColor(color);
        else 
            setText(" ");

        addActionListener(this);
        listeners = new ArrayList<ColorChangeListener>();
    }

    //----------------------------------------------------------------------

    public ColorButton(int w, int h, Color color, String title)
    {
        this(w, h, color, title, true);
    }

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
            defined = true;
            setColor(col);
            setText(null);
            repaint();
            dispatchColorChangeEvent(new ColorChangeEvent(this, col));
        }
    }
}

