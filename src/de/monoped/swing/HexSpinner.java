package de.monoped.swing;

import javax.swing.*;

public class HexSpinner
    extends JSpinner
{
    public HexSpinner(int n)
    {
        setEditor(new HexEditor(this, n));   
    }
}

