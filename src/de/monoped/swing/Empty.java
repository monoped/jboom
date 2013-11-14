package de.monoped.swing;

import javax.swing.*;
import java.awt.*;

public class Empty
        extends JComponent {
    public Empty(Dimension dim) {
        setSize(dim);
        setPreferredSize(dim);
        setMinimumSize(dim);
        setMaximumSize(dim);
    }
}

