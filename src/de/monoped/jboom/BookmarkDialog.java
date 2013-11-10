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

import de.monoped.efile.*;
import de.monoped.utils.*;
import de.monoped.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;

/** Dialog for editing a tree entry (bookmark or folder). */

class BookmarkDialog
    extends JDialog
    implements DocumentListener
{
    static KeyBundle        bundle = (KeyBundle)ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private JButton         okButton, cancelButton;
    private JTextField      nameField, addrField;
    private JTextArea       textArea;
    private JLabel          urlLabel, iconLabel;
    private boolean         confirmed, nameOK, urlOK;
    private JBoomNode       node, parent;
    private String          oldName, oldAddr;

    //----------------------------------------------------------------------

    private void fix(final JTextComponent comp)
    {
        comp.addMouseListener(new MouseListener()
        {
            public void mouseClicked(MouseEvent e)
            {
                comp.requestFocus();   
            }

            public void mouseEntered(MouseEvent e)
            {
                comp.requestFocus();  
            }

            public void mouseExited(MouseEvent e)
            {
                comp.requestFocus();  
            }

            public void mousePressed(MouseEvent e)
            {
                comp.requestFocus();  
            }

            public void mouseReleased(MouseEvent e)
            {
                comp.requestFocus();  
            }
        });
    }

    //----------------------------------------------------------------------

    /** Constructor.
     *
     *  @param  parent  Parent node (folder).
     *  @param  node    Node to be changed.
     *  @param  title   Dialog Title.
     *  @param  exists  If true, changes an existing node; otherwise, node is
     *                  assumed to be new.
     */

    BookmarkDialog(JBoomNode parent, JBoomNode node, String title, boolean exists)
    {
        super((JFrame)null, title, true);    
        this.node = node;
        this.parent = parent;

        if (exists)
        {
            oldName = node.getName();
            oldAddr = node.getURL();
        }

        JPanel              panel = new JPanel();
        GridBagConstraints  gbc = new GridBagConstraints();

        panel.setLayout(new GridBagLayout());
        nameField = new JTextField(30);
//        fix(nameField);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.EAST;

        // Headline: Folder, Icon

        panel.add(new JLabel(bundle.getText("folderLabel")), gbc);

        ++gbc.gridx;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(parent.getName()), gbc);

        iconLabel = new JLabel();
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(iconLabel, gbc);

        ++gbc.gridy; gbc.gridx = 0;

        JLabel nameLabel = new JLabel(bundle.getText("name"));

        panel.add(nameLabel, gbc);          // Name field
        ++gbc.gridx;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(nameField, gbc);
        nameField.setText(node.getName());
        nameField.getDocument().addDocumentListener(this);
        nameLabel.setLabelFor(nameField);
            
        if (node.isLeaf())
        {
            // URL field 
            
            addrField = new JTextField(30);
            addrField.setText(node.getURL());
            ++gbc.gridy; gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.gridwidth = 1;
            urlLabel = new JLabel(bundle.getText(node.isMail() ? "label-mail" : "label-url"));
            urlLabel.setLabelFor(addrField);
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            panel.add(urlLabel, gbc);
            ++gbc.gridx;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            panel.add(addrField, gbc);
//            fix(addrField);
        }
        else
            iconLabel.setIcon(Icons.FOLDER);

        nameField.requestFocus();

        // Text

        textArea = new JTextArea(3, 30);
        ++gbc.gridy; gbc.gridx = 0; 
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        JLabel textLabel = new JLabel(bundle.getText("text"));

        textLabel.setLabelFor(textArea);

        panel.add(textLabel, gbc);
        ++gbc.gridx;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;

        String text = node.getText();
        
        textArea.setText(text);
//        fix(textArea);
        panel.add(new JScrollPane(textArea), gbc);
        
        // ok and cancel buttons

        okButton = new JButton(new UIAction(bundle, "ok")
        {
            public void actionPerformed(ActionEvent e)
            {
                finish();
            }
        });

        cancelButton = new JButton(new UIAction(bundle, "cancel")
        {
            public void actionPerformed(ActionEvent e)
            {
                if (oldName != null)
                {
                    JBoomNode node = BookmarkDialog.this.node;

                    node.setName(oldName);
                    node.setURL(oldAddr);
                    node.setKind();
                }

                setVisible(false);
            }
        });

        ++gbc.gridy; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0; gbc.weighty = 0;

        panel.add(cancelButton, gbc);
        gbc.gridwidth = 2;
        gbc.gridx = 2; 
        panel.add(okButton, gbc);
        
        if (addrField != null)
            addrField.getDocument().addDocumentListener(this);

        setOKStatus();

        setContentPane(panel);
        getRootPane().setDefaultButton(okButton);
        setSize(new Dimension(500, 250));
    }

    //----------------------------------------------------------------------

    /** Save and exit dialog */

    private void finish()
    {
        String name = Utils.textParam(nameField);

        // Name must be unique

        if (! name.equals(oldName) && parent.getChild(name) != null)
        {  
            JOptionPane.showMessageDialog(BookmarkDialog.this, 
                    bundle.getText("nameExists", name),
                    bundle.getText("warn"), 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        node.setName(name);
        
        if (addrField != null)
        {
            String  url = Utils.textParam(addrField);

            node.setURL(url);
            node.setKind();
        }

        node.setText(Utils.textParam(textArea));

        confirmed = true;
        setVisible(false);
    }

    //----------------------------------------------------------------------

    /** True if user has pressed the OK-button. */

    boolean isConfirmed()
    {
        return confirmed;
    }

    //----------------------------------------------------------------------

    /** set status of OK button according to address and name field */

    private void setOKStatus()
    {
        okButton.setEnabled(nameField.getText().trim().length() > 0 
                && (addrField == null || addrField.getText().trim().length() > 0));
    }

    //----------------------------------------------------------------------

    /** Set status of UI components after receiving a document event */

    private void setStatus(DocumentEvent e)
    {
        if (addrField != null && e.getDocument() == addrField.getDocument() && node.isLeaf())
        {
            String addr = addrField.getText().trim();

            if (addr.length() == 0)
                urlLabel.setText(bundle.getText("label-url"));
            else
            {
                node.setURL(addr);
                node.setKind();
                urlLabel.setText(bundle.getText(node.isMail() ? "label-mail" : "label-url"));
                iconLabel.setIcon(node.isMail() ? Icons.MAIL : Icons.BOOKMARK);
            }
        }
            
        setOKStatus();
    }

    //----------------------------------------------------------------------
    // DocumentListener implementation
    //----------------------------------------------------------------------

    public void insertUpdate(DocumentEvent e)
    {   
        setStatus(e);
    }

    //----------------------------------------------------------------------

    public void removeUpdate(DocumentEvent e)
    {   
        setStatus(e);
    }

    //----------------------------------------------------------------------

    public void changedUpdate(DocumentEvent e)
    {   
        setStatus(e);
    }
}
