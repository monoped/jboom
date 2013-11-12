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

import de.monoped.swing.UIAction;
import de.monoped.swing.UIFactory;
import de.monoped.utils.KeyBundle;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ResourceBundle;

/** The find panel in the south of the main panel. */

class FindPanel
    extends JPanel
    implements ActionListener, DocumentListener, TreeSelectionListener, FocusListener
{
    static KeyBundle            bundle = (KeyBundle)ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private JTextField          textField;
    private JCheckBox           nameCheck,
                                urlCheck,
                                caseCheck;
    private JButton             nextButton;
    private JButton prevButton;
    private JBoom               jboom;
    private JBoomNode           nodeFound;
    private boolean             findInName, findInURL, found, caseSensitive;
    private JTextField          urlField;

    //----------------------------------------------------------------------

    /** Constructor
     *
     *  @param  jboom   The main content pane
     */

    FindPanel(JBoom jboom)
    {
        this.jboom = jboom;
        UIFactory uif = new UIFactory();

        // selectiong a tree node defines start of search
        
        jboom.getTree().addTreeSelectionListener(this);

        // Search text

        textField = new JTextField(20);
        textField.addFocusListener(this);
        
        // Get initial text from model
        
        String value = jboom.getModel().getFindText();

        if (value != null)
            textField.setText(value);

        // Forward/backward button only enabled if text non-empty

        textField.getDocument().addDocumentListener(this);

        // Checkboxes

        nameCheck = uif.checkBox(new NamesAction(), findInName = jboom.getModel().isFindName()); 
        findInURL = jboom.getModel().isFindURL();

        if (! findInName && ! findInURL)
            findInURL = true;

        urlCheck = uif.checkBox(new URLAction(), findInURL);
        caseCheck = uif.checkBox(new CaseAction(), caseSensitive = jboom.getModel().isFindCase());

        // Forward/backward and go button

        Action nextAction = new UIAction("next.png", bundle, "findNext")
        {
            public void actionPerformed(ActionEvent e)
            {
                find(true);
            }
        };
        nextButton = uif.iconButton(nextAction);

        Action nextOrShowAction = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                String text = Utils.textParam(urlField);

                if (text != null)
                    FindPanel.this.jboom.activateNode(nodeFound);
                else
                    find(true);
            }
        };

        Action prevAction = new UIAction("prev.png", bundle, "findPrev")
        {
            public void actionPerformed(ActionEvent e)
            {
                find(false);
            }
        };
        prevButton = uif.iconButton(prevAction);

        Action closeAction = new UIAction("close.png", bundle, "close")
        {
            public void actionPerformed(ActionEvent e)
            {
                quit();
            }
        };
        JButton closeButton = uif.iconButton(closeAction);

        // URL field

        urlField = new JTextField(20);
        urlField.addActionListener(this);
        urlField.getDocument().addDocumentListener(this);

        // Layout

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();  

        gbc.gridx = gbc.gridy = 0;

        // Label

        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 0, 5);
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel(bundle.getText("find")), gbc);

        // Text input

        gbc.gridx += gbc.gridwidth; 
        gbc.gridwidth = 3;
        gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(textField, gbc);

        // Close button

        gbc.gridx += gbc.gridwidth; 
        gbc.gridwidth = 1;
        gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(closeButton,gbc);

        // Checkboxes

        JPanel p = new JPanel();

        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(nameCheck);
        p.add(urlCheck);
        p.add(Box.createHorizontalGlue());
        p.add(caseCheck);

        gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        ++gbc.gridy; gbc.gridx = 0; gbc.gridwidth = 6;
        add(p, gbc);

        // Backward, URL, forward

        gbc.insets = new Insets(5, 5, 5, 5);
        ++gbc.gridy; gbc.gridx = 0; 
        gbc.gridwidth = 1;
        gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        add(prevButton, gbc);
        gbc.gridx += gbc.gridwidth; 
        gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 4;
        add(urlField, gbc);
        gbc.gridx += gbc.gridwidth; 
        gbc.gridwidth = 1;
        gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(nextButton, gbc);

        setButtonStatus();

        // Input map

        InputMap    inputMap = new InputMap();
        ActionMap   actionMap = new ActionMap(); 

        inputMap.put(KeyStroke.getKeyStroke("F3"), "next");
        inputMap.put(KeyStroke.getKeyStroke("shift F3"), "prev");
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "enter");
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "esc");
        inputMap.setParent(getInputMap());

        actionMap.put("next", nextAction);
        actionMap.put("prev", prevAction);
        actionMap.put("enter", nextOrShowAction);
        actionMap.put("esc", closeAction);

        setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
//        setInputMap(WHEN_IN_FOCUSED_WINDOW, inputMap);
        setActionMap(actionMap);
    }

    //----------------------------------------------------------------------

    /** Find next occurence.
     *
     *  @param forward  If true, search forward, else backward.
     */

    private void find(boolean forward)
    {
        String text = textField.getText().trim();

        if (! caseSensitive)
            text = text.toUpperCase();
        
        if (text.length() == 0)
            return;

        if (nodeFound == null)
            nodeFound = jboom.getRootNode();

        urlField.setText(null);

        JBoomNode   node = nodeFound;

        found = false;

        while (true)
        {
            if (forward)
            {
                node = (JBoomNode)node.getNextNode();

                if (node == null)
                    node = jboom.getRootNode();
            }
            else
            {
                node = (JBoomNode)node.getPreviousNode();

                if (node == null)
                    node = (JBoomNode)jboom.getRootNode().getLastChild();
            }
            
            if (node == null)
                return;

            if (findInName)
            {
                String name = node.getName();
                
                if (! caseSensitive)
                    name = name.toUpperCase();

                if (name.indexOf(text) >= 0)
                {
                    found = true;
                    break;
                }
            }
            
            if (node.isLeaf() && findInURL)
            {
                String url = node.getURL();

                if (! caseSensitive)
                    url = url.toUpperCase();

                if (url.indexOf(text) >= 0)
                {
                    found = true;
                    break;
                }
            }
            
            if (node == nodeFound)
                return;
        }

        jboom.selectNode(nodeFound = node);

        if (node.isLeaf())
        {
            urlField.setText(node.getURL());
            urlField.setCaretPosition(0);
        }
    }

    //----------------------------------------------------------------------

    /** Remove find from main panel. */

    private void quit()
    {
        jboom.remove(this);
        jboom.setFindEnabled(true);
        jboom.validate();
        jboom.repaint();
    }

    //----------------------------------------------------------------------

    /** Enable buttons according to text und URL. */

    private void setButtonStatus()
    {
        boolean ok = textField.getText().trim().length() > 0;

        nextButton.setEnabled(ok);
        prevButton.setEnabled(ok);
    }

    //----------------------------------------------------------------------

    /** Transfer focus to text field. */
      
    void start()
    {
        textField.requestFocus();
        found = false;
    }
    
    //----------------------------------------------------------------------
    // ActionListener implementation.
    //----------------------------------------------------------------------

    public void actionPerformed(ActionEvent e)
    {
        find(true);
    }

    //----------------------------------------------------------------------
    // DocumentListener implementation
    //----------------------------------------------------------------------

    public void insertUpdate(DocumentEvent documentevent)
    {   
        setButtonStatus();
    }

    //----------------------------------------------------------------------

    public void removeUpdate(DocumentEvent documentevent)
    {   
        setButtonStatus();
    }

    //----------------------------------------------------------------------

    public void changedUpdate(DocumentEvent documentevent)
    {   
        setButtonStatus();
    }

    //----------------------------------------------------------------------
    // FocusListener implementation (for textField)
    //----------------------------------------------------------------------

    public void focusGained(FocusEvent focusevent)
    {   
//        jboom.setEnterKeyEnabled(false);
    }

    //----------------------------------------------------------------------

    public void focusLost(FocusEvent focusevent)
    {   
        jboom.getModel().setFindText(textField.getText());
//        jboom.setEnterKeyEnabled(true);
    }

    //----------------------------------------------------------------------
    // TreeSelectionListener implementation
    //----------------------------------------------------------------------

    /** Remember node to start search from. */

    public void valueChanged(TreeSelectionEvent e)
    {
        nodeFound = jboom.getSelectedNode();
    }

    //======================================================================

    class CaseAction
        extends UIAction
    {
        CaseAction()
        {
            super(bundle, "case");
        }

        //----------------------------------------------------------------------

        public void actionPerformed(ActionEvent e)
        {
            jboom.getModel().setFindCase(caseSensitive = caseCheck.isSelected());
        }
    }

    //======================================================================

    class NamesAction
        extends UIAction
    {
        NamesAction()
        {
            super(bundle, "names");
        }

        //----------------------------------------------------------------------

        public void actionPerformed(ActionEvent e)
        {
            findInName = nameCheck.isSelected();
            jboom.getModel().setFindName(findInName);
            
            if (! findInURL && ! findInName)
            {
                jboom.getModel().setFindURL(findInURL = true);
                urlCheck.setSelected(true);
            }
        }
    }

    //======================================================================

    class URLAction
        extends UIAction
    {
        URLAction()
        {
            super(bundle, "urls");
        }

        //----------------------------------------------------------------------

        public void actionPerformed(ActionEvent e)
        {
            findInURL = urlCheck.isSelected();
            jboom.getModel().setFindURL(findInURL);

            if (! findInURL && ! findInName)
            {
                jboom.getModel().setFindName(findInName = true);
                nameCheck.setSelected(true);
            }
        }
    }
}

