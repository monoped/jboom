package de.monoped.swing;

import java.awt.*;
import javax.swing.*;

public class Empty
    extends JComponent
{
    public Empty(Dimension dim)
    {
        setSize(dim);
        setPreferredSize(dim);
        setMinimumSize(dim);
        setMaximumSize(dim);
    }
}

