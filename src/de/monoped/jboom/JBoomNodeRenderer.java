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

import de.monoped.utils.KeyBundle;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * Tree cell renderer.
 */

class JBoomNodeRenderer
        extends DefaultTreeCellRenderer {
    static KeyBundle bundle = (KeyBundle) ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private boolean tipEnabled;

    //----------------------------------------------------------------------

    JBoomNodeRenderer() {
        setLeafIcon(Icons.MARK);
        setOpenIcon(Icons.FOLDER_OPEN);
        setClosedIcon(Icons.FOLDER);
        tipEnabled = true;
    }

    //----------------------------------------------------------------------

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                  boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        JBoomNode node = (JBoomNode) value;

        if (leaf) {
            if (node.isMail())
                setIcon(Icons.MAIL);

            if (tipEnabled)
                setToolTipText(((JBoomNode) value).getURL());
        } else {
            if (node.isMail()) {
                if (tipEnabled)
                    setToolTipText(bundle.getText("maillist"));

                setIcon(expanded ? Icons.MAILLIST_EXPANDED : Icons.MAILLIST);
            } else
                setToolTipText(null);
        }

        return this;
    }

    //----------------------------------------------------------------------

    void setTipEnabled(boolean tipEnabled) {
        this.tipEnabled = tipEnabled;
    }

}                                   

