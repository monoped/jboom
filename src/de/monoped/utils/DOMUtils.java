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
import javax.swing.*;
import org.w3c.dom.*;

/** Some convenience methods for DOM.*/

public class DOMUtils
{
    //----------------------------------------------------------------------

    /** Get value of an attribute.
     *
     *  @param el       The element.
     *  @param name     Name of attribute.
     *  @return         Value of attribute or null.
     */
    
    static public String getAttributeValue(Element el, String name)
    {
        Attr attr = el.getAttributeNode(name);
        
        return attr != null ? attr.getValue() : null;
    }

    //----------------------------------------------------------------------

    /** Get value of an attribute or text of an equally named child element.
     *
     *  @param el       The element.
     *  @param name     Name of attribute.
     *  @return         Value of attribute or null.
     */
    
    static public String getExtAttribute(Element el, String name)
    {
        Attr attr = el.getAttributeNode(name);
        
        if (attr != null)
            return attr.getValue();

        Element child = getChildElement(el, name);

        return child == null ? null : getText(child);
    }

    //----------------------------------------------------------------------

    /** Get k-th child element with given tagname.
     *
     *  @param el       The element.
     *  @param name     Tag name.
     *  @param k        Index of child element.
     *
     *  @return         k-th child element or null.
     */

    static public Element getChildElement(Element el, String name, int k)
    {
        if (! el.hasChildNodes())
            return null;

        Node child;

        for (child = el.getFirstChild(); child != null; child = child.getNextSibling())
            if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(name) && k-- == 0)
                return (Element)child;

        return null;
    }

    //----------------------------------------------------------------------

    /** Get k-th child element. 
     *
     *  @param el       The element.
     *  @param k        Index of child element.
     *
     *  @return         k-th child element or null.
     */

    static public Element getChildElement(Element el, int k)
    {
        if (! el.hasChildNodes())
            return null;

        Node child;

        for (child = el.getFirstChild(); child != null; child = child.getNextSibling())
            if (child.getNodeType() == Node.ELEMENT_NODE && k-- == 0)
                return (Element)child;

        return null;
    }

    //----------------------------------------------------------------------

    /** Get first child element with given tagname.
     *
     *  @param el       The element.
     *  @param name     Tag name.
     *
     *  @return         k-th child element or null.
     */

    static public Element getChildElement(Element el, String name)
    {
        if (! el.hasChildNodes())
            return null;

        Node child;

        for (child = el.getFirstChild(); child != null; child = child.getNextSibling())
            if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(name))
                return (Element)child;
        
        return null;
    }

    //----------------------------------------------------------------------

    /** Get first child element. 
     *
     *  @param el       The element.
     *
     *  @return         First child element or null.
     */

    static public Element getChildElement(Element el)
    {
        if (! el.hasChildNodes())
            return null;

        Node child;

        for (child = el.getFirstChild(); child != null; child = child.getNextSibling())
            if (child.getNodeType() == Node.ELEMENT_NODE)
                return (Element)child;
        
        return null;
    }

    //----------------------------------------------------------------------

    /** Get first child element with given tagname and given attribute setting.
     *  
     *  @param el               The element.
     *  @param name             Tag name.
     *  @param attributeName    Attribute name.
     *  @param attributeValue   Attribute value.
     *
     *  @return                 First child found.
     */

    static public Element getChildElementByAttribute(Element el, String name, 
            String attributeName, String attributeValue)
    {
        if (! el.hasChildNodes())
            return null;

        Node child;

        for (child = el.getFirstChild(); child != null; child = child.getNextSibling())
            if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(name))
            {
                Attr    attr = ((Element)child).getAttributeNode(attributeName);

                if (attr != null && attributeValue.equals(attr.getValue()))
                    return (Element)child;
            }
            
        return null;
    }
    
    //----------------------------------------------------------------------

    /** Get all child elements with given tagname and given attribute setting.
     *  
     *  @param el               The element.
     *  @param name             Tag name.
     *  @param attributeName    Attribute name.
     *  @param attributeValue   Attribute value.
     *
     *  @return                 List of found elements.
     */

    static public List<Element> getChildElementsByAttribute(Element el, String name, 
            String attributeName, String attributeValue)
    {
        List<Element> list = new ArrayList<Element>();

        if (! el.hasChildNodes())
            return list;

        Node child;

        for (child = el.getFirstChild(); child != null; child = child.getNextSibling())
            if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(name))
            {
                Attr    attr = ((Element)child).getAttributeNode(attributeName);

                if (attr != null && attributeValue.equals(attr.getValue()))
                    list.add((Element)child);
            }
            
        return list;
    }
    
    //----------------------------------------------------------------------

    /** Get all child elements with given tagname. 
     *  
     *  @param el           The element.
     *  @param name         Tag name.
     *
     *  @return             List of found elements.
     */

    static public List<Element> getChildElements(Element el, String name)
    {
        List<Element> list = new ArrayList<Element>();
        
        if (el.hasChildNodes())
            for (Node node = el.getFirstChild(); node != null; node = node.getNextSibling())
                if (node.getNodeName().equals(name))
                    list.add((Element)node);
            
        return list;
    }
    
    //----------------------------------------------------------------------

    /** Count child elements.
     *
     *  @param el   The element.
     *
     *  @return     Child count.
     */
    
    static public int getChildElementCount(Element el)
    {
        int n = 0; 
        
        for (Node node = el.getFirstChild(); node != null; node = node.getNextSibling())
            if (node.getNodeType() == Node.ELEMENT_NODE)
                ++n;
            
        return n;
    }
    
    //----------------------------------------------------------------------

    /** Get all child elements.
     *
     *  @param el   The element.
     *
     *  @return     List of child elements.
     */

    static public ArrayList<Element> getChildElements(Element el)
    {
        ArrayList<Element>   list = new ArrayList<Element>();
        
        for (Node node = el.getFirstChild(); node != null; node = node.getNextSibling())
            if (node.getNodeType() == Node.ELEMENT_NODE)
                list.add((Element)node);
            
        return list;
    }
    
    //----------------------------------------------------------------------

    /** Get all child elements with one of several tagnames.
     *
     *  @param el   The element.
     *
     *  @return     List of found elements.
     */

    static public List<Element> getChildElements(Element el, String[] names)
    {
        if (! el.hasChildNodes())
            return null;

        List<Element>  list = new ArrayList<Element>();
        Node        child;

        for (child = el.getFirstChild(); child != null; child = child.getNextSibling())
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                String name = child.getNodeName();
                
                for (String nam: names)
                    if (name.equals(nam))
                        list.add((Element)child);
            }

        return list;
    }

    //----------------------------------------------------------------------

    /** Get first child element with given tagname.
     *
     *  @param el       The element.
     *  @param name     Tag name.
     *
     *  @return         First found child or null.
     */

    static public Element getFirstChildElement(Element el, String name)
    {
        return getChildElement(el, name, 0);
    }

    //----------------------------------------------------------------------

    /** Get first child element with one of several tagnames.
     *
     *  @param el       The element.
     *  @param names    Tag names.
     *
     *  @return         First found child or null.
     */

    static public Element getFirstChildElement(Element el, String[] names)
    {
        if (! el.hasChildNodes())
            return null;

        Node child;

        for (child = el.getFirstChild(); child != null; child = child.getNextSibling())
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                String name = child.getNodeName();
                
                for (String nam: names)
                    if (name.equals(nam))
                        return (Element)child;
            }

        return null;
    }

    //----------------------------------------------------------------------

    /** Get first child element that has NOT a given tagname.
     *
     *  @param el   The element.
     *  @param name Tag name.
     *
     *  @return     First found child or null.
     */

    static public Element getFirstChildElementNot(Element el, String name)
    {
        if (! el.hasChildNodes())
            return null;

        Node child;

        for (child = el.getFirstChild(); child != null; child = child.getNextSibling())
            if (child.getNodeType() == Node.ELEMENT_NODE && ! child.getNodeName().equals(name))
                return (Element)child;

        return null;
    }

    //----------------------------------------------------------------------

    /** Get first child element (depth-first search).
     *
     *  @param el   The element.
     *
     *  @return     First child or null.
     */

    static public Element getFirstChildElement(Element el)
    {
        Node first = el.getFirstChild();

        while (first != null && first.getNodeType() != Node.ELEMENT_NODE)
            first = first.getNextSibling();

        return (Element)first;
    }

    //----------------------------------------------------------------------

    /** Get last child element (depth-first search).
     *
     *  @param el   The element.
     *
     *  @return     Last child or null.
     */

    static public Element getLastChildElement(Element el)
    {
        Node last = el.getLastChild();

        while (last != null && last.getNodeType() != Node.ELEMENT_NODE)
            last = last.getPreviousSibling();

        return (Element)last;
    }

    //----------------------------------------------------------------------

    /** Get next element (depth-first).
     *
     *  @param el   Actual element.
     *
     *  @return     Next element or null;
     */

    static public Element getNextElement(Element el)
    {
        Node node = el;
        
        while ((node = node.getNextSibling()) != null && node.getNodeType() != Node.ELEMENT_NODE)
        { }

        return (Element)node;
    }

    //----------------------------------------------------------------------

    /** Get text part of an element.
     *
     *  @param el   The element.
     *
     *  @return     Concatenated text elements.
     */

    static public String getText(Element el)
    {
        if (! el.hasChildNodes())
            return null;

        Node            child;
        StringBuffer    buf = new StringBuffer();

        for (child = el.getFirstChild(); child != null; child = child.getNextSibling())
            if (child.getNodeType() == Node.CDATA_SECTION_NODE)
                buf.append(((CDATASection)child).getWholeText());
            else if (child.getNodeType() == Node.TEXT_NODE)
                buf.append(child.getNodeValue());

        return buf.toString().trim();
    }
    
}

