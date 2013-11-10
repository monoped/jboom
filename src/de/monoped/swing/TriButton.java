package de.monoped.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TriButton
    extends JToggleButton
    implements MouseListener
{
    private boolean defined;
    private Icon    icon, undefIcon;

    //----------------------------------------------------------------------

    public TriButton(Icon icon, Icon undefIcon)
    {
        super(icon); 
        this.icon = icon;
        this.undefIcon = undefIcon;
        addMouseListener(this);
    }

    //----------------------------------------------------------------------

    public TriButton(Icon icon)
    {
        this(icon, null);
        undefIcon = getDisabledSelectedIcon();
    }

    //----------------------------------------------------------------------

    public int getSelectionState()
    {
        return ! defined ? -1 : isSelected() ? 1 : 0;
    }

    //----------------------------------------------------------------------

    public boolean isDefined()
    {
        return defined;
    }

    //----------------------------------------------------------------------

    public boolean isSelected()
    {
        return defined && super.isSelected();
    }

    //----------------------------------------------------------------------

    public void setDefined(boolean defined)
    {
        this.defined = defined;
        setIcon(defined ? icon : undefIcon);
    }

    //----------------------------------------------------------------------

    public void mouseClicked(MouseEvent mouseevent)
    {   
        if (! defined)
            setDefined(true);
    }

    public void mousePressed(MouseEvent mouseevent)
    {   }

    public void mouseReleased(MouseEvent mouseevent)
    {   
    }

    public void mouseEntered(MouseEvent mouseevent)
    {   }

    public void mouseExited(MouseEvent mouseevent)
    {   }

}

