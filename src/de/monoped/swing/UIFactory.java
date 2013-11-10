package de.monoped.swing;

/* This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * monoped@users.sourceforge.net
 */

import de.monoped.utils.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;

/** Convenience class for creating buttons etc. Contains a set of 
 *  default attributes.
 */

public class UIFactory
{
    private boolean                 borderPainted = true, 
                                    contentAreaFilled = true;
    private Border                  border;
    private Insets                  margin;

    //----------------------------------------------------------------------

    public UIFactory()
    { }

    //----------------------------------------------------------------------

    /** Create a button and connect it with an Action. */

    public JButton button(Action action)
    {
        JButton b = new JButton(action);

        if (border != null)
            b.setBorder(border);

        if (margin != null)
            b.setMargin(margin);

        b.setBorderPainted(borderPainted);
        b.setContentAreaFilled(contentAreaFilled);
        b.setText(null);
        return b;
    }

    //----------------------------------------------------------------------

    /** Create a check box and connect it with an Action. */

    public JCheckBox checkBox(Action action, boolean selected)
    {
        JCheckBox b = new JCheckBox(action);

        b.setSelected(selected);
        return b;
    }

    //----------------------------------------------------------------------

    public JButton iconButton(Action action)
    {
        JButton b = new JButton(action);

        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setText(null);
        return b;
    }

    //----------------------------------------------------------------------

    public JLabel labelFor(KeyBundle bundle, String text, Component comp)
    {
        JLabel label = new JLabel(bundle.getText(text));

        label.setDisplayedMnemonic(bundle.getMnemonic(text));
        label.setLabelFor(comp);
        return label;
    }

    //----------------------------------------------------------------------

    public JRadioButton radioButton(KeyBundle bundle, String text, ButtonGroup group, boolean selected)
    {
        JRadioButton button = new JRadioButton(bundle.getText(text));
        
        button.setMnemonic(bundle.getMnemonic(text));
        button.setSelected(selected);

        if (group != null)
            group.add(button);

        return button;
    }

    //----------------------------------------------------------------------

    public JRadioButton radioButton(KeyBundle bundle, String[] text, ButtonGroup group, boolean selected)
    {
        JRadioButton button = new JRadioButton(bundle.getText(text));
        
        button.setMnemonic(bundle.getMnemonic(text[0]));
        button.setSelected(selected);

        if (group != null)
            group.add(button);

        return button;
    }

    //----------------------------------------------------------------------

    public JRadioButton radioButton(KeyBundle bundle, String text, ButtonGroup group)
    {
        return radioButton(bundle, text, group, false);
    }

    //----------------------------------------------------------------------

    public void setBorderPainted(boolean borderPainted)
    {
        this.borderPainted = borderPainted;
    }

    //----------------------------------------------------------------------

    public void setContentAreaFilled(boolean contentAreaFilled)
    {
        this.contentAreaFilled = contentAreaFilled;
    }

    //----------------------------------------------------------------------

    public void setBorder(Border border)
    {
        this.border = border;
    }

    //----------------------------------------------------------------------

    public void setEmptyBorder(int top, int left, int bottom, int right)
    {
        border = new EmptyBorder(top, left, bottom,right);
    }

    //----------------------------------------------------------------------

    public void setMargin(int top, int left, int bottom, int right)
    {
        this.margin = new Insets(top, left, bottom, right);
    }

}

