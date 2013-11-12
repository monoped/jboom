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

import de.monoped.utils.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/** Panel containing components for file selection (text input, browse button). */

class FilePanel
    extends JPanel
    implements ActionListener
{
    static KeyBundle        bundle = (KeyBundle)ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private JTextField      fileField;
    private JFileChooser    chooser;

    //----------------------------------------------------------------------

    FilePanel(String head, String text, File file)
    {
        setLayout(new BorderLayout());

        add(new JLabel(head, SwingConstants.LEFT), BorderLayout.NORTH);

        JPanel panel = new JPanel();

        fileField = new JTextField(30);
        fileField.setText(file.getPath());
        JButton fileButton = new JButton(bundle.getText("browse"));
        fileButton.setActionCommand("browse");
        fileButton.addActionListener(this);

        chooser = new JFileChooser(file);

        panel.add(new JLabel(text));
        panel.add(fileField);
        panel.add(fileButton);

        add(panel);
    }

    //----------------------------------------------------------------------

    String getPath()
    {
        return fileField.getText();
    }

    //----------------------------------------------------------------------

    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();

        if (cmd.equals("browse") && chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            fileField.setText(chooser.getSelectedFile().getAbsolutePath());
    }

}

