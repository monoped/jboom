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
import de.monoped.utils.Strings;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * Some static methods for jBoom.
 */

class Utils {
    static KeyBundle bundle = (KeyBundle) ResourceBundle.getBundle("de.monoped.jboom.Resources");
    static private Insets buttonInsets = new Insets(0, 0, 0, 0);

    //----------------------------------------------------------------------

    /**
     * Add row with label and text field to a GridBagLayout.
     *
     * @param panel     Containing panel.
     * @param gbc       Layout constraints.
     * @param field     Input field.
     * @param gridwidth Width of input field.
     * @param key       Resource key for label text.
     * @param value     Initial value of input field if != null.
     */

    static void addTextField(JPanel panel, GridBagConstraints gbc, JTextField field,
                             int gridwidth, String key, String value) {
        ++gbc.gridy;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        JLabel label = new JLabel(bundle.getText(key));

        label.setLabelFor(field);
        label.setDisplayedMnemonic(bundle.getMnemonic(key));
        panel.add(label, gbc);
        ++gbc.gridx;
        gbc.gridwidth = gridwidth;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);

        if (value != null)
            field.setText(value);

        gbc.gridwidth = 1;
    }

    //----------------------------------------------------------------------

    /**
     * Get file using a FileChooser.
     *
     * @param file If a a directory, start browsing here; if a file,
     *             start browsing in its parent dir.
     * @return The chosen file or null if cancelled.
     */

    static String browseFile(String file) {
        File dir = null;

        if (file != null)
            dir = new File(file).getParentFile();

        JFileChooser chooser = new JFileChooser(dir);

        chooser.setFileHidingEnabled(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            try {
                return chooser.getSelectedFile().getCanonicalPath();
            } catch (IOException ex) {
                if (JBoom.isDebug())
                    ex.printStackTrace();
            }

        return null;
    }

    //----------------------------------------------------------------------

    /**
     * Create a JButton with command string, icon, ActionListener, and KeyListener.
     *
     * @param icon           Icon to use.
     * @param tip            Tool tip text.
     * @param cmd            Command string or null
     * @param actionListener ActionListener
     * @param keyListener    KeyListener or null
     * @return Newly created button.
     */

    static JButton createButton(ImageIcon icon, String tip, String cmd, ActionListener actionListener, KeyListener keyListener) {
        JButton button = new JButton(icon);

        if (cmd != null)
            button.setActionCommand(cmd);

        if (keyListener != null)
            button.addKeyListener(keyListener);

        button.addActionListener(actionListener);
        button.setMargin(buttonInsets);
        button.setToolTipText(tip);

        return button;
    }

    //----------------------------------------------------------------------

    /**
     * Create a JButton with an icon, ActionListener, and KeyListener.
     *
     * @param icon           Icon to use.
     * @param tip            Tool tip text.
     * @param actionListener ActionListener.
     * @param keyListener    KeyListener.
     * @return Newly created button.
     */

    static JButton createButton(ImageIcon icon, String tip, ActionListener actionListener, KeyListener keyListener) {
        return createButton(icon, tip, null, actionListener, keyListener);
    }

    //----------------------------------------------------------------------

    /**
     * Create a JButton with an icon and ActionListener.
     *
     * @param icon           Icon to use.
     * @param tip            Tool tip text.
     * @param actionListener ActionListener.
     * @return Newly created button.
     */

    static JButton createButton(ImageIcon icon, String tip, ActionListener actionListener) {
        return createButton(icon, tip, null, actionListener, null);
    }

    //----------------------------------------------------------------------

    /**
     * Get selection paths of a tree in row order.
     *
     * @param tree The tree.
     * @return Array of TreePaths.
     */

    static TreePath[] getSelectionPathSequence(JTree tree) {
        int[] rows = tree.getSelectionRows();

        if (rows == null)
            return new TreePath[0];

        TreePath[] paths = new TreePath[rows.length];

        Arrays.sort(rows);

        for (int i = 0; i < rows.length; ++i)
            paths[i] = tree.getPathForRow(rows[i]);

        return paths;
    }

    //----------------------------------------------------------------------

    /**
     * Convert html to plain text - VERY simple!
     */

    static String html2text(String html) {
        return Strings.xmlEntitiesToChars(html.replaceAll("<[^>]*>", "")).trim();
    }

    //----------------------------------------------------------------------

    /**
     * Show exception in a message dialog.
     */

    static void showException(Component parent, Exception ex) {
        if (JBoom.isDebug())
            ex.printStackTrace();

        JOptionPane.showMessageDialog(parent, ex.toString(), bundle.getText("error"), JOptionPane.ERROR_MESSAGE);
    }

    //----------------------------------------------------------------------

    /**
     * Get text of a TextComponent, trimmed, and converted to null if empty.
     */

    static String textParam(JTextComponent field) {
        String text = field.getText();

        if (text == null)
            return null;

        text = text.trim();

        return text.length() == 0 ? null : text;
    }

}

