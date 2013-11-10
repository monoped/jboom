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
import java.io.*;
import java.util.*;

/** Write bookmark tree as a Firefox HTML file */

class HTMLExporter
{
    private BufferedWriter  out;
    private JBoomNode       root;
    private int             depth;
    private String          indent;
    private boolean         mail, marks;

    //----------------------------------------------------------------------

    HTMLExporter(Writer out, JBoomNode root, boolean marks, boolean mail)
    {
        this.out = new BufferedWriter(out);
        this.root = root;
        this.marks = marks;
        this.mail = mail;
        indent = "    ";
        depth = 0;
    }

    //----------------------------------------------------------------------

    /** export complete tree */

    void export()
        throws IOException
    {
        out.write("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n" + 
            "<!-- This is an automatically generated file.\n" +
            "     It will be read and overwritten.\n" +
            "     DO NOT EDIT! -->\n" +
            "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n" +
            "<TITLE>" + root.getPureName() + "</TITLE>\n" +
            "<H1>jBoom</H1>\n\n");

        writeList(root);
        out.close();
    }

    //----------------------------------------------------------------------

    /** Export a node */

    private void writeNode(JBoomNode node)
        throws IOException
    {
        boolean ismail = node.isMail();

        if (mail && ismail || marks && ! ismail)
        {
            writeIndent();
            out.write("<DT>");

            if (node.isLeaf())
                if (ismail)
                    out.write("<A HREF=\"mailto:" + node.getURL() + "\">" + node.getPureName() + "</A>");
                else
                    out.write("<A HREF=\"" + node.getURL() + "\">" + node.getPureName() + "</A>");
            else
                out.write("<H3>" + node.getPureName() + "</H3>");

            out.write("</DT>\n");

            String text = node.getPureText();

            if (text != null && text.length() > 0)
            {
                writeIndent();
                out.write("<DD>" + text + "</DD>\n");
            }

            if (! node.isLeaf())
                writeList(node);
        }
    }

    //----------------------------------------------------------------------

    /** Write indentation (n * 4) */

    private void writeIndent()
        throws IOException
    {
        for (int i = 0; i < depth; ++i)
            out.write(indent);
    }

    //----------------------------------------------------------------------

    /** Export a directory */

    private void writeList(JBoomNode node)
        throws IOException
    {
        writeIndent();
        out.write("<DL><p>\n");
        ++depth;

        for (JBoomNode child: node.getChildren())
            writeNode(child);

        --depth;
        out.write('\n');
        writeIndent();
        out.write("</DL><p>\n");
    }

}

