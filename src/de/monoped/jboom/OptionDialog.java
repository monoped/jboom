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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.ResourceBundle;

/** Modal dialog for jboom options. */

public class OptionDialog
    extends JDialog
    implements ActionListener
{
    private static final int        DIALOG_WIDTH = 600, DIALOG_HEIGHT = 600;
    static KeyBundle                bundle = (KeyBundle)ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private JBoom                   jboom;
    private JBoomModel              model;
    private JList<Object> connectionList;
    private DefaultListModel<Object> listModel;
    private JPanel                  centerPanel;
    private TransportUI             transportUI;
    private JTextField              localFileField, mailCommandField, urlCommandField;
    private UIAction                okAction;
    private JButton okButton;
    private JButton newButton;
    private JCheckBox folderFirstCheck;
    private JCheckBox sortRecursiveCheck;
    private JCheckBox startMinimizedCheck;
    private JCheckBox toTrayCheck;
    private JCheckBox positionCheck;
    private JPopupMenu              newPop;
    private boolean                 listenSelection;
    private UIFactory               uif;

    //----------------------------------------------------------------------

    /** Construct a dialog.
     *
     *  @param jboom            The JBoom object.
     */

    public OptionDialog(JBoom jboom)
    {
        this.jboom = jboom;
        uif = new UIFactory();
        model = new JBoomModel(jboom.getModel());
        listenSelection = true;
        setModalityType(ModalityType.APPLICATION_MODAL);
        setTitle(bundle.getText("configLabel"));

        okAction = new UIAction(bundle, "ok")
        {
            public void actionPerformed(ActionEvent e)
            {
                quit(true);
            }
        };

        setContentPane(createContentPane());
        setSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                quit(false);
            }
        });

        getRootPane().setDefaultButton(okButton);
        okAction.setEnabled(transportUI == null || transportUI.isComplete());
    }

    //----------------------------------------------------------------------

    /** Add a connection.
     *
     *  @param  trans   Connection to add.
     */

    private void addConnection(Transport trans)
    {
        model.getConnections().add(trans);
        listModel.addElement(model.getLabelText(trans));

        int n = model.getConnections().size() - 1;
        
        connectionList.setSelectedIndex(n);
        connectionList.scrollRectToVisible(connectionList.getCellBounds(n, n));
    }

    //----------------------------------------------------------------------

    JComponent createBottomPanel()
    {
        JPanel              panel = new JPanel();
        GridBagConstraints  gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        panel.setLayout(new GridBagLayout());
        localFileField = new JTextField(30);

        if (jboom.isApplication())
            urlCommandField = new JTextField(30);

        mailCommandField = new JTextField(30);

        JButton browseLocalButton = new JButton(new UIAction(bundle, "browse")
        {
            public void actionPerformed(ActionEvent e)
            {
                String  file = Utils.browseFile(localFileField.getText());
                
                if (file != null)
                {
                    model.setLocalPath(file);
                    localFileField.setText(file);
                }
            }
        });

        JCheckBox askSaveCheck = new JCheckBox(new UIAction(bundle, "askSave", model.isAskSave()) {
            public void actionPerformed(ActionEvent e) {
                model.setAskSave((Boolean) getValue(SELECTED_KEY));
            }
        });

        folderFirstCheck = new JCheckBox(new UIAction(bundle, "folderFirst", model.isFolderFirst())
        {
            public void actionPerformed(ActionEvent e)
            {
                model.setFolderFirst((Boolean)getValue(SELECTED_KEY));
            }
        });

        sortRecursiveCheck = new JCheckBox(new UIAction(bundle, "recursive", model.isSortRecursive())
        {
            public void actionPerformed(ActionEvent e)
            {
                boolean b = (Boolean)getValue(SELECTED_KEY);

                model.setSortRecursive(b);
            }
        });

        JCheckBox extractCheck = new JCheckBox(new UIAction(bundle, "extract", model.isExtractName())
        {
            public void actionPerformed(ActionEvent e)
            {
                model.setExtractName((Boolean)getValue(SELECTED_KEY));
            }
        });

  
        if (jboom.isApplication())
        {
            startMinimizedCheck = new JCheckBox(new UIAction(bundle, "startmin", model.isStartMinimized())
            {
                public void actionPerformed(ActionEvent e)
                {
                    model.setStartMinimized((Boolean)getValue(SELECTED_KEY));
                }
            });


            if (SystemTray.isSupported())
                toTrayCheck = new JCheckBox(new UIAction(bundle, "totray", model.isToTray())
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        model.setToTray((Boolean)getValue(SELECTED_KEY));
                    }
                });

            positionCheck = new JCheckBox(new UIAction(bundle, "position", model.isRememberPosition())
            {
                public void actionPerformed(ActionEvent e)
                {
                    model.setRememberPosition((Boolean)getValue(SELECTED_KEY));
                }
            });
        }

        JButton cancelButton = new JButton(new UIAction(bundle, "cancel") {
            public void actionPerformed(ActionEvent e) {
                quit(false);
            }
        });

        gbc.gridx = gbc.gridy = 0;
        panel.add(new JLabel(bundle.getText("sortLabel") + ":"), gbc);

        ++gbc.gridx;
        panel.add(folderFirstCheck, gbc);
        ++gbc.gridx;
        panel.add(sortRecursiveCheck, gbc);

        ++gbc.gridy; gbc.gridx = 0; 
        Utils.addTextField(panel, gbc, localFileField, 2, "localFile", model.getLocalPath());
        gbc.gridx += 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(browseLocalButton, gbc);
        
        if (urlCommandField != null)
        {
            ++gbc.gridy; gbc.gridx = 0; 
            Utils.addTextField(panel, gbc, urlCommandField, 3, "urlcommand", model.getURLCommand());
            gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        }
        
        if (mailCommandField != null)
        {
            ++gbc.gridy; gbc.gridx = 0; 
            Utils.addTextField(panel, gbc, mailCommandField, 3, "mailcommand", model.getMailCommand());
            gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        }
        
        if (jboom.isApplication())
        {
            ++gbc.gridy; gbc.gridx = 1; 
            panel.add(startMinimizedCheck, gbc);

            if (toTrayCheck != null)
            {
                ++gbc.gridx; 
                panel.add(toTrayCheck, gbc);
            }

            ++gbc.gridx;
            panel.add(positionCheck, gbc);
        }

        ++gbc.gridy; gbc.gridx = 1;
        panel.add(extractCheck, gbc);
        ++gbc.gridx; panel.add(askSaveCheck, gbc);

        JSeparator separator = new JSeparator();

        ++gbc.gridy; gbc.gridx = 0; gbc.gridwidth = 4; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(separator, gbc);

        ++gbc.gridy; gbc.gridx = 1; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE; 
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(cancelButton, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.EAST;
        okButton = new JButton(okAction);
        panel.add(okButton, gbc);

        return panel;
    }
    
    //----------------------------------------------------------------------

    JPanel createCenterPanel()
    {
        Transport transport = model.getActiveTransport();
        
        if (transport != null)
        {
            transportUI = transport.getUI();
            transportUI.addActionListener(this);
            return transportUI.getPanel();
        }

        return null;
    }
    
    //----------------------------------------------------------------------

    /** Return scroll pane with connections */

    private JScrollPane createConnectionList()
    {
        listModel = new DefaultListModel<Object>();

        for (Transport trans: model.getConnections())
        {
            String text = model.getLabelText(trans);

            listModel.addElement(text);
        }

        connectionList = new JList<Object>(listModel);
        connectionList.setVisibleRowCount(3);
        connectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        connectionList.addListSelectionListener(new ConnSelectionListener());
        connectionList.setSelectedIndex(model.getActiveTransportIndex());
        return new JScrollPane(connectionList);
    }
    
    //----------------------------------------------------------------------

    /** Create complete content pane for dialog */

    private JPanel createContentPane()
    {
        JPanel  panel = new JPanel();

        panel.setLayout(new BorderLayout(5, 5));
        panel.add(createTopPanel(), BorderLayout.NORTH);
        centerPanel = createCenterPanel();
        
        if (centerPanel != null)
            panel.add(centerPanel);

        panel.add(createBottomPanel(), BorderLayout.SOUTH);
        return panel;
    }

    //----------------------------------------------------------------------

    /** Create connection menu and buttons to move, destroy, and create connections */

    private JComponent createTopPanel()
    {
        JPanel              panel = new JPanel();
        Box                 box = new Box(BoxLayout.X_AXIS);

        panel.setLayout(new BorderLayout(0, 5));

        box.add(Box.createHorizontalStrut(5));
        box.add(new JLabel(bundle.getText("connections")));
        box.add(Box.createHorizontalStrut(15));
        box.add(createConnectionList());
        box.add(Box.createHorizontalStrut(15));

        uif.setMargin(0, 2, 0, 2);

        Action upAction = new UIAction("up.gif", bundle, "up")
        {
            public void actionPerformed(ActionEvent e)
            {
                int index = connectionList.getSelectedIndex();

                moveConnection(index, index - 1);
            }
        };
        
        box.add(uif.button(upAction));
        box.add(Box.createHorizontalStrut(5));

        Action downAction = new UIAction("down.gif", bundle, "down")
        {
            public void actionPerformed(ActionEvent e)
            {
                int index = connectionList.getSelectedIndex();

                moveConnection(index, index + 1);
            }
        };
        
        box.add(uif.button(downAction));
        box.add(Box.createHorizontalStrut(5));
        
        Action newAction = new UIAction("new.gif", bundle, "newConn")
        {
            public void actionPerformed(ActionEvent e)
            {
                newPop.show(getContentPane(), newButton.getX(), newButton.getY() + 20);
            }
        };

        box.add(newButton = uif.button(newAction));
        box.add(Box.createHorizontalStrut(5));

        Action delAction = new UIAction("del.gif", bundle, "delConn")
        {
            public void actionPerformed(ActionEvent e)
            {
                deleteConnection(connectionList.getSelectedIndex());
            }
        };

        box.add(uif.button(delAction));
        box.add(Box.createHorizontalStrut(5));

        // new connection popup menu

        newPop = new JPopupMenu(bundle.getText("newConn"));

        ActionListener newConnListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                addConnection(TransportFactory.createTransport(e.getActionCommand()));
            }
        };

        for (JMenuItem item: TransportFactory.createNewItems())
        {
            item.addActionListener(newConnListener);
            newPop.add(item);
        }

        panel.add(box);

        // Input and action map for top right buttons

        InputMap inputMap = new InputMap();

        inputMap.put(KeyStroke.getKeyStroke("alt UP"), "up");
        inputMap.put(KeyStroke.getKeyStroke("alt DOWN"), "down");
        inputMap.put(KeyStroke.getKeyStroke("DELETE"), "del");
        inputMap.put(KeyStroke.getKeyStroke("INSERT"), "new");
        inputMap.setParent(panel.getInputMap());

        ActionMap actionMap = new ActionMap();

        actionMap.put("up", upAction);
        actionMap.put("down", downAction);
        actionMap.put("del", delAction);
        actionMap.put("new", newAction);
        actionMap.setParent(panel.getActionMap());

        panel.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
        panel.setActionMap(actionMap);

        return panel;
    }

    //----------------------------------------------------------------------

    void deleteConnection(int k)
    {
        listenSelection = false;
        model.getConnections().remove(k);
        listModel.remove(k);
        listenSelection = true;

        int n = model.getConnections().size();
        
        if (k >= n)
            --k;

        if (n > 0)
        {
            connectionList.setSelectedIndex(k);
            connectionList.scrollRectToVisible(connectionList.getCellBounds(k, k));
        }
        else
        {
            getContentPane().remove(centerPanel);
            getContentPane().validate();
            getContentPane().repaint();
            centerPanel = null;
        }
    }
    
    //----------------------------------------------------------------------

    private void moveConnection(int from, int to)
    {
        ArrayList<Transport>    conns = model.getConnections();
        Transport               trans = conns.get(from);

        if (to < 0)
            to = conns.size() - 1;
        else if (to >= conns.size())
            to = 0;

        listenSelection = false;
        conns.remove(from);
        conns.add(to, trans);

        Object s = listModel.get(from);
        
        listModel.removeElementAt(from);
        listModel.add(to, s);
        
        connectionList.repaint();
        listenSelection = true;
        connectionList.setSelectedIndex(to);
        connectionList.scrollRectToVisible(connectionList.getCellBounds(to, to));
    }

    //----------------------------------------------------------------------

    private void quit(boolean save)
    {
        if (save)
        {
            model.setLocalPath(Utils.textParam(localFileField));

            if (urlCommandField != null)
                model.setURLCommand(Utils.textParam(urlCommandField));

            model.setMailCommand(Utils.textParam(mailCommandField));
            model.setSortRecursive(sortRecursiveCheck.isSelected());
            model.setFolderFirst(folderFirstCheck.isSelected());

            if (transportUI != null)
                transportUI.setData();

            jboom.setModel(model);
        }

        setVisible(false);
    }

    //----------------------------------------------------------------------
    // ActionListener 
    //----------------------------------------------------------------------

    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();

        if (cmd.equals("complete"))
            okAction.setEnabled(true);
        else if (cmd.equals("incomplete"))
            okAction.setEnabled(false);
        else if (cmd.equals("nameChanged"))
        {
            int         k = connectionList.getSelectedIndex();
            Transport   trans = model.getConnections().get(k);

            String t = "<html>";

            if (trans.isActive())
                t += "<font color=\"" + JBoom.ACTIVE_COLOR + "\">";
                    
            listModel.set(k, t + transportUI.getName());
        }
        else
        {
            int k = connectionList.getSelectedIndex();

            if (cmd.equals("active"))
            {
                model.setActiveTransportIndex(k);
                transportUI.setActive(true);
            }
            else if (cmd.equals("inactive"))
            {
                model.setActiveTransportIndex(-1);
                transportUI.setActive(false);
            }

            k = 0;
            
            for (Transport trans: model.getConnections())
                listModel.set(k++, model.getLabelText(trans));
        }
    }

    //----------------------------------------------------------------------
    // ConnSelectionListener class
    //----------------------------------------------------------------------

    /** ListSelectionListener for connection list. */
    
    class ConnSelectionListener
        implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        {
            if (! listenSelection || e.getValueIsAdjusting())
                return;
            
            if (transportUI != null)
                transportUI.setData();

            int index = connectionList.getSelectedIndex();

            if (centerPanel != null)
            {
                transportUI.removeActionListener(OptionDialog.this);
                getContentPane().remove(centerPanel);
            }

            if (index >= 0)
            {
                Transport transport = model.getTransport(index);

                transportUI = transport.getUI();
                transportUI.addActionListener(OptionDialog.this);

                getContentPane().add(centerPanel = transportUI.getPanel());
                getContentPane().validate();
            }
            else
                transportUI = null;

            okAction.setEnabled(transportUI == null || transportUI.isComplete());
        }
    }
}

