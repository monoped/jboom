package de.monoped.swing;

import java.awt.*;

public class ColorChangeEvent
    extends AWTEvent
{
    private Color   color;

    //----------------------------------------------------------------------

    public ColorChangeEvent(Object source, Color color)
    {
        super(source, RESERVED_ID_MAX + 1);
        this.color = color;
    }

    //----------------------------------------------------------------------

    public Color getColor()
    {
        return color;
    }
}

