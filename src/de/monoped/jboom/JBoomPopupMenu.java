package de.monoped.jboom;

import de.monoped.utils.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.tree.*;

/** Popup menu appears after right click on a tree item */

class JBoomPopupMenu
    extends PopupMenu
    implements ActionListener
{
    static KeyBundle        bundle = (KeyBundle)ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private JBoomProgram    program;
    private JBoom           jboom;
    private JBoomNode       root;

    //----------------------------------------------------------------------

    JBoomPopupMenu(JBoomProgram prog)
    {
        program = prog;
        this.jboom = program.getJboom();

        DefaultTreeModel    treeModel = (DefaultTreeModel)jboom.getTree().getModel();

        root = (JBoomNode)treeModel.getRoot();
        setLabel(Utils.html2text(root.getName()));

        for (JBoomNode child: root.getChildren())
        {
            if (child.isLeaf())
            {
                MenuItem    item = new JBoomPopupMenuItem(child);

                item.addActionListener(this);
                add(item);
            }
            else
                add(createMenu(child));
        }

        addSeparator();

        MenuItem exitTrayItem = new MenuItem(bundle.getText("exitTray")),
                 exitItem;

        exitTrayItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                program.removeTray();
            }
        });

        exitItem = new MenuItem(bundle.getText("exit"));

        exitItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jboom.finish();
                System.exit(0);
            }
        });

        add(exitTrayItem);
        add(exitItem);
    }

    //----------------------------------------------------------------------

    private Menu createMenu(JBoomNode node)
    {
        Menu menu = new Menu(Utils.html2text(node.getName()));

        for (JBoomNode child: node.getChildren())
        {
            if (child.isLeaf())
            {
                MenuItem    item = new JBoomPopupMenuItem(child);

                item.addActionListener(this);
                menu.add(item);
            }
            else
                menu.add(createMenu(child));
        }

        return menu;
    }

    //----------------------------------------------------------------------

    public void actionPerformed(ActionEvent e)
    {
        JBoomNode node = ((JBoomPopupMenuItem)e.getSource()).getNode();

        if (! node.isMail())
            jboom.gotoURL(node.getURL());
        else
            jboom.composeMail(node.getURL());
    }

    //----------------------------------------------------------------------

    String getRootName()
    {
        return Utils.html2text(root.getName());
    }

}

