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

import java.util.*;

/** A node in the clip tree. 
 *  Holds a JBoomNode and a list of children.
 */

class ClipNode
{
    private ArrayList<ClipNode> children;
    private JBoomNode           node;

    //----------------------------------------------------------------------

    /** Construct a ClipNode object with initially empty children list.
     * 
     *  @param node         The selected node.
     *  @param recursive    Whether children of node are to be selected recursively.
     */

    ClipNode(JBoomNode node, boolean recursive)
    {
        this.node = node;
        children = new ArrayList<ClipNode>();

        if (node != null && recursive && ! node.isLeaf())
        {
            // Add complete folder 

            int n = node.getChildCount();

            for (int i = 0; i < n; ++i)
                children.add(new ClipNode((JBoomNode)node.getChildAt(i), recursive));
        }
    }

    //----------------------------------------------------------------------

    /** Construct an empty ClipNode. */

    ClipNode()
    {
        this(null, false);
    }
    
    //----------------------------------------------------------------------

    /** Add JBoomNode if not already contained. 
     *
     *  @param child    Node to be added.
     */
    
    void add(ClipNode child)
    {
        if (! children.contains(child))
            children.add(child);
    }

    //----------------------------------------------------------------------

    /** Replace all JBoomNode references by deep copies. */

    void copy()
    {
        if (node != null)
            node = new JBoomNode(node);

        for (ClipNode child: children)
            child.copy();
    }

    //----------------------------------------------------------------------

    /** Find clip child equal to selected node. 
     *
     *  @param node The node to find.
     *  @return     The clip child or null.
     */
    
    ClipNode findChild(JBoomNode node)
    {
        for (ClipNode child: children)
            if (child.node == node)
                return child;

        return null;
    }

    //----------------------------------------------------------------------

    /** Get the list of children. */

    ArrayList<ClipNode> getChildren()
    {
        return children;
    }
    
    //----------------------------------------------------------------------

    /** Return a deep copy of this node. */

    JBoomNode getCopy()
    {
        JBoomNode k = new JBoomNode(node);
        
        for (ClipNode child: children)
            k.add(child.getCopy());

        return k;
    }

    //----------------------------------------------------------------------

    /** Return the node. */

    JBoomNode getNode()
    {
        return node;
    }
    
    //----------------------------------------------------------------------

    /** Return number of children. */
    
    int getSize()
    {
        return children.size();
    }

    //----------------------------------------------------------------------

    /** Check if this node is a leaf (bookmark). */
    
    boolean isLeaf()
    {
        return node.isLeaf();
    }
    
}

