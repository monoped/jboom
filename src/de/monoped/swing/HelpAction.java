package de.monoped.swing;

import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

/** Help Action class, launches the dialog from class HelpDialog */

public class HelpAction
    extends AbstractAction
{
    static private ResourceBundle   bundle;

    private String filename, title;       

    //----------------------------------------------------------------------

    /** Construct an Action.
     * @param   filename    Name of file containing html text 
     * @param   title       Dialog title
     * @param   text        Text (icon / item)
     * @param   mnemonic    Mnemonic key
     */

    public HelpAction(String filename, String title, String text, int mnemonic)
    {
        if (bundle != null)
            this.filename = bundle.getString(filename);
        else
            this.filename = filename;

        this.title = title;

        if (text != null)
           putValue(NAME, text);

        URL iconResource = getClass().getClassLoader().getResource("img/help.png");

        if (iconResource != null)
            putValue(SMALL_ICON, new ImageIcon(iconResource));

        putValue(SHORT_DESCRIPTION, "Hilfe [F1]");

        if (mnemonic != 0)
            putValue(MNEMONIC_KEY, mnemonic);
    }

    //----------------------------------------------------------------------

    /** Construct an Action by reading text from file <classname>.help.
     * @param   obj     Object whose class name is used.
     * @param   title   Help dialog title 
     */

    public HelpAction(Object obj, String title, String text, int mnemonic)
    {
        this(obj.getClass().getName() + ".help", title, text, mnemonic);
    }

    //----------------------------------------------------------------------

    /** Construct an Action by reading text from a file.
     * @param   filename    Name of the file.
     * @param   title       Help dialog title.
     */

    public HelpAction(String filename, String title)
    {
        this(filename, title, null, 0);
    }

    //----------------------------------------------------------------------

    /** Construct an Action by reading text from file <classname>.help.
     * @param   obj     Object whose class name is used.
     * @param   title   Help dialog title 
     */

    public HelpAction(Object obj, String title)
    {
        this(obj.getClass().getName() + ".help", title);
    }

    //----------------------------------------------------------------------

    static public void setBundle(ResourceBundle bundle)
    {
        HelpAction.bundle = bundle;
    }

    //----------------------------------------------------------------------

    /** Set name of help file to display */

    public void setName(String filename)
    {
        this.filename = filename;
    }

    //----------------------------------------------------------------------

    /** Set file name from class */

    public void setName(Object obj)
    {
        setName(obj.getClass().getName() + ".help");
    }

    //----------------------------------------------------------------------
    // ActionListener method
    //----------------------------------------------------------------------

    public void actionPerformed(ActionEvent e)
    {
        try
        {
            HelpDialog help = HelpDialog.createHelpDialog();

            if (title != null)
                help.setTitle(title);

            help.setText(filename);
            help.setVisible(true);
        }
        catch (Exception ex)
        {
            Utilities.showException(ex);
        }
    }

}

