package de.monoped.jboom;

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
import de.monoped.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/** Abstract base class for transport UIs. ActionListeners may be added which
 *  will be informed about change of activeness ("active", "inactive").
 */

abstract class TransportUI
    implements DocumentListener
{
    static KeyBundle            bundle = (KeyBundle)ResourceBundle.getBundle("de.monoped.jboom.Resources");

    public static final int     FIELDLEN = 25;
    protected JCheckBox         activeCheck;
    protected JTextField        nameField;
    private ActionListener      listener;

    //----------------------------------------------------------------------

    TransportUI(Transport trans)
    {
        UIFactory uif = new UIFactory();
        nameField = new JTextField(FIELDLEN);
        nameField.getDocument().addDocumentListener(this);
        activeCheck = uif.checkBox(new ActiveAction(), trans.isActive());
    }

    //----------------------------------------------------------------------

    public void addActionListener(ActionListener l)
    {
        listener = AWTEventMulticaster.add(listener, l);
    }

    //----------------------------------------------------------------------

    void checkInput()
    {
        boolean complete = isComplete();

        if (listener != null)
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, complete ? "complete" : "incomplete"));
    }

    //----------------------------------------------------------------------

    public String getName()
    {
        return nameField.getText().trim();
    }

    //----------------------------------------------------------------------

    public void removeActionListener(ActionListener l)
    {
        listener = AWTEventMulticaster.remove(listener, l);
    }

    //----------------------------------------------------------------------

    void setActive(boolean active)
    {
        activeCheck.setSelected(active);       
    }

    //----------------------------------------------------------------------

    abstract void       setData();
    abstract JPanel     getPanel();
    abstract boolean    isComplete();

    //----------------------------------------------------------------------
    // DocumentListener for name
    //----------------------------------------------------------------------

    public void insertUpdate(DocumentEvent e)
    {   
        checkInput();

        if (e.getDocument() == nameField.getDocument() && listener != null)
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "nameChanged"));
    }

    public void removeUpdate(DocumentEvent e)
    {   
        checkInput();

        if (e.getDocument() == nameField.getDocument()  && listener != null)
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "nameChanged"));
    }

    public void changedUpdate(DocumentEvent e)
    {   }

    //======================================================================

    class ActiveAction
        extends UIAction
    {
        ActiveAction()
        {
            super(bundle, "activ");
        }

        //----------------------------------------------------------------------

        public void actionPerformed(ActionEvent e)
        {
            setData();

            if (listener != null) 
                listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
                        activeCheck.isSelected() ? "active" : "inactive"));
        }
    }
}

