package de.monoped.jboom;

import java.awt.*;

class JBoomPopupMenuItem
        extends MenuItem {
    private JBoomNode node;

    //----------------------------------------------------------------------

    JBoomPopupMenuItem(JBoomNode node) {
        this.node = node;
        setLabel(node.getName());
    }

    //----------------------------------------------------------------------

    JBoomNode getNode() {
        return node;
    }

}

