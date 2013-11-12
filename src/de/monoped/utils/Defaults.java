package de.monoped.utils;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Enumeration;

/** Contains static methods that show some system properties. */

public class Defaults
{
    private Defaults()
    { }
    
    //----------------------------------------------------------------------

    /** Print UI defaults into file "uidefaults.out" */

    static void uiDefaults() {
        try
        {
            System.setOut(new PrintStream(new FileOutputStream("uidefaults.out")));
        }
        catch (IOException ignored)
        {   }

        UIDefaults      defaults = UIManager.getDefaults();
        int             i = 0, n = defaults.size();
        String[]        names = new String[n];

        Enumeration keys = defaults.keys();

        while (keys.hasMoreElements()) 
            names[i++] = keys.nextElement().toString();

        Arrays.sort(names);

        for (i = 0; i < n; ++i)
            System.out.println(names[i] + " = " + defaults.get(names[i]));
    }

    //----------------------------------------------------------------------

    /** Print system properties into file "sysprops.out" */

    static void sysProps()
        throws IOException
    {
        System.getProperties().store(new FileOutputStream("sysprops.out"), "System properties");
    }

    //----------------------------------------------------------------------

    /** Main (no args) */

    static public void main(String[] args)
        throws Exception
    {
        uiDefaults();
        sysProps();
    }
}

