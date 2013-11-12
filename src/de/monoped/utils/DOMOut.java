package de.monoped.utils;

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

public class DOMOut
{
    private StringBuilder       buf;
    private ArrayList<Layer>    stack;
    private int                 indent, margin;

    //----------------------------------------------------------------------

    public DOMOut(int indent)
    {
        this.indent = indent;

        buf = new StringBuilder();
        stack = new ArrayList<Layer>();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
    }

    //----------------------------------------------------------------------

    public DOMOut()
    {
        this(0);
    }

    //----------------------------------------------------------------------

    public DOMOut openElement(String name)
    {
        if (stack.size() > 0)
        {
            Layer layer = stack.get(stack.size() - 1);

            if (! layer.ended)
                endElement();
        }

        stack.add(new Layer(name));

        leftmargin();
        buf.append("<").append(name);
        margin += indent;
        return this;
    }

    //----------------------------------------------------------------------

    public DOMOut addAttribute(String name, String value)
    {
        buf.append(" ").append(name).append("=\"").append(value).append("\"");
        return this;
    }

    //----------------------------------------------------------------------

    public DOMOut cdata(String text)
    {
        buf.append("<![CDATA[").append(text).append("]]>");
        return this;
    }

    //----------------------------------------------------------------------

    public DOMOut cdataElement(String name, String text)
    {
        openElement(name);
        endElement();
        leftmargin();
        buf.append("<![CDATA[").append(text).append("]]>");
        closeElement();
        return this;
    }

    //----------------------------------------------------------------------

    public DOMOut endElement()
    {
        buf.append(">");
        stack.get(stack.size() - 1).ended = true;
        return this;
    }

    //----------------------------------------------------------------------

    public void finish()
    {
        while (stack.size() > 0)
            closeElement();
    }

    //----------------------------------------------------------------------

    private void leftmargin()
    {
        buf.append("\n");
        
        for (int i = 0; i < margin; ++i)
            buf.append(' ');
    }

    //----------------------------------------------------------------------

    public DOMOut closeElement()
    {
        int     i = stack.size() - 1;
        Layer   layer = stack.get(i);

        margin -= indent;

        if (! layer.ended)
            buf.append("/>");
        else
        {
            leftmargin();
            buf.append("</").append(stack.get(i).name).append(">");
        }

        stack.remove(i);
        return this;
    }

    //----------------------------------------------------------------------

    public DOMOut text(String text)
    {
        buf.append(text).append("\n");
        return this;
    }

    //----------------------------------------------------------------------

    public DOMOut textElement(String name, String text)
    {
        openElement(name);
        endElement();
        leftmargin(); buf.append(text);
        closeElement();
        return this;
    }

    //----------------------------------------------------------------------

    public String toString()
    {
        finish();
        return buf.toString();
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    static class Layer
    {
        String  name;
        boolean ended;

        //----------------------------------------------------------------------

        Layer(String name)
        {
            this.name = name;
        }

    }

}

