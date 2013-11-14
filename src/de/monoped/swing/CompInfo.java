package de.monoped.swing;

import javax.swing.*;
import java.awt.*;

////////////////////////////////////////////////////////////////////////

/**
 * Class containing  saveable / restorable component info
 */

class CompInfo {
    CompInfo(int x, int y, int w, int h, int position) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.position = position;
    }

    void setComponent(JLayeredPane pane, Component component) {
        component.setLocation(x, y);
        component.setSize(w, h);
        pane.setPosition(component, position);
    }

    int x, y, w, h, position;
}

