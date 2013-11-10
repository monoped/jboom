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

import javax.swing.tree.*;

/** Tree made of ClipNodes, serves as node clipboard. */

class Clip
{
    private ClipNode    clipRoot;

    //----------------------------------------------------------------------

    /** Create clip from selected tree paths.
     *
     *  @param paths        Array of selected paths.
     *  @param recursive    If true, contents of each folder are selected recursively.
     */

    Clip(TreePath[] paths, boolean recursive)
    {
        clipRoot = new ClipNode();
        
        // Create ClipNode tree

        for (TreePath path: paths)
        {
            Object[]    components = path.getPath();   // Components of a selected path
            ClipNode    clipNode = clipRoot;

            for (Object comp: components)
            {
                /* Look for an existing ClipNode equal to this component
                 * (folder with the same name or leaf with the same name
                 * and same URL),
                 */
                
                ClipNode  child = clipNode.findChild((JBoomNode)comp);
                
                if (child != null)
                    clipNode = child;   // found, continue with existing
            } 

            clipNode.add(new ClipNode((JBoomNode)path.getLastPathComponent(), recursive));
        }

        // Replace all JBoomNode references by clones

        clipRoot.copy();
    }

    //----------------------------------------------------------------------

    /** Non-recursive clip. */

    Clip(TreePath[] paths)
    {
        this(paths, false);
    }
    
    //----------------------------------------------------------------------

    /** Create a copy of all clip subtrees.
     *
     *  @return     Array of 1st-level clip subtree copies.
     */

    JBoomNode[] getCopy()
    {
        JBoomNode[] nodes = new JBoomNode[clipRoot.getSize()];
        int         i = 0;
        
        for (ClipNode child: clipRoot.getChildren())
            nodes[i++] = child.getCopy();

        return nodes;
    }

    //----------------------------------------------------------------------

    /** Return the root node. */

    ClipNode getRoot()
    {
        return clipRoot;
    }
    
    //----------------------------------------------------------------------

    /** Check if clip is empty. */

    boolean isEmpty()
    {
        return clipRoot.getChildren().size() == 0;
    }
}

