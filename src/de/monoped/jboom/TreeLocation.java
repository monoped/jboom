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

import javax.swing.*;
import javax.swing.tree.TreePath;

/**
 * Class representing a location in a tree.
 */

class TreeLocation {
    private int index;
    private JBoomNode parent;

    //----------------------------------------------------------------------

    TreeLocation(JTree tree) {
        TreePath path = tree.getSelectionPath();

        if (path == null)
            return;

        JBoomNode selected = (JBoomNode) path.getLastPathComponent();

        parent = selected;

        if (selected == null)
            return;

        if (selected.isLeaf())
            parent = (JBoomNode) selected.getParent();
        else
            selected = null;

        if (parent == null)
            parent = (JBoomNode) tree.getModel().getRoot();

        if (selected != null)
            index = parent.getIndex(selected) + 1;
    }

    //----------------------------------------------------------------------

    /**
     * Get index of selected node.
     */

    int getIndex() {
        return index;
    }

    //----------------------------------------------------------------------

    /**
     * Get parent of selected node.
     */

    JBoomNode getParent() {
        return parent;
    }

}

