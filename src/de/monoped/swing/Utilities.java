package de.monoped.swing;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;

public class Utilities {
    /**
     * Check if TreePaths are brothers (members of the same folder).
     * Leafs and non-leafs may be mixed.
     */

    static public boolean treePathsAreBrothers(TreePath[] paths) {
        if (paths == null)
            return true;

        if (paths.length < 2)
            return true;

        Object[] komp0 = paths[0].getPath();
        int n = komp0.length;

        for (int i = 1; i < paths.length; ++i) {
            TreePath path = paths[i];
            Object[] komp = path.getPath();

            if (komp.length != n)
                return false;

            for (int j = 0; j < n - 1; ++j)
                if (komp[j] != komp0[j])
                    return false;
        }

        return true;
    }

    //----------------------------------------------------------------------

    /**
     * Check if TreePaths denote the same parent.
     * They must be leafs, parent may be included as first path.
     */

    static public boolean treePathsSameParent(TreePath[] paths) {
        if (paths == null)
            return true;

        if (paths.length < 2)
            return true;

        Object[] komp0 = paths[0].getPath();
        TreeNode node0 = (TreeNode) paths[0].getLastPathComponent();
        int n = komp0.length;

        if (node0.isLeaf())
            --n;

        for (int i = 1; i < paths.length; ++i) {
            Object[] komp = paths[i].getPath();
            TreeNode node = (TreeNode) paths[i].getLastPathComponent();

            if (!node.isLeaf())
                return false;

            if (komp.length != n + 1)
                return false;

            for (int j = 0; j < n; ++j)
                if (komp[j] != komp0[j])
                    return false;
        }

        return true;
    }

    //----------------------------------------------------------------------

    /**
     * Center rectangle in a component.
     *
     * @param parent Containing component.
     * @param w      Width of rectangle.
     * @param h      Height of rectangle.
     * @return Centered rectangle.
     */

    static public Rectangle centerComponent(Component parent, int w, int h) {
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        int x = 0,
                y = 0,
                wp = ss.width,
                hp = ss.height;

        if (parent != null) {
            Point loc = parent.getLocationOnScreen();
            Dimension dim = parent.getSize();

            if (dim.width >= w && dim.height >= h) {
                x = loc.x;
                y = loc.y;
                wp = dim.width;
                hp = dim.height;
            }
        }

        return new Rectangle(x + (wp - w) / 2, y + (hp - h) / 2, w, h);
    }

    //----------------------------------------------------------------------

    /**
     * Draw a centered string
     *
     * @param g     Grahics environment
     * @param str   The string
     * @param x     x coordinate of starting point
     * @param y     y coordinate of starting point
     * @param width Horizontal width of the region
     */

    static public void centerString(Graphics2D g, String str, int x, int y, int width) {
        int w = g.getFontMetrics().stringWidth(str);

        g.drawString(str, x + (width - w) / 2, y);
    }

    //----------------------------------------------------------------------

    /**
     * Show exception in an option pane
     */

    static public void showException(Throwable ex) {
        Throwable cause = ex.getCause();

        if (cause != null)
            ex = cause;

        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, ex.toString(), "Fehler", JOptionPane.ERROR_MESSAGE);
    }

}

