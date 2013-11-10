package de.monoped.utils;

import java.util.*;
import org.w3c.dom.*;

public class DOMIO
{
    static public void attributesToBuffer(NamedNodeMap atts, StringBuffer buf)
    {
        int n = atts.getLength();

        for (int i = 0; i < n; ++i)
        {
            Attr attr = (Attr)atts.item(i);
            
            buf.append(' ').append(attr.getNodeName()).append("=\"")
                .append(attr.getNodeValue().replaceAll("&", "&amp;").replaceAll("\"", "&quot;"))
                .append('"');
        }
    }

    //----------------------------------------------------------------------

    static private void contentToBuffer(Element el, StringBuffer buf, String indent)
    {
        NodeList    children = el.getChildNodes();
        int         n = children.getLength();

        for (int i = 0; i < n; ++i)
        {
            Node    child = children.item(i);

            switch (child.getNodeType())
            {
                case Node.TEXT_NODE:
                {
                    String txt = child.getNodeValue().trim();

                    if (txt.length() > 0)
                        buf.append(indent).append(txt).append('\n');

                    break;
                }

                case Node.ELEMENT_NODE:
                    elementToBuffer((Element)child, buf, indent);
                    break;
            }
        }
    }

    //----------------------------------------------------------------------

    static public String contentToString(Element el)
    {
        StringBuffer buf = new StringBuffer();

        contentToBuffer(el, buf, "");
        return buf.toString();
    }
    
    //----------------------------------------------------------------------

    static public StringBuffer documentToBuffer(Document document)
    {
        StringBuffer buf = new StringBuffer("<?xml version=\"");

        buf.append(document.getXmlVersion())
            .append('"');
        
        String encoding = document.getXmlEncoding();
        
        if (encoding != null)
            buf.append(" encoding=\"")
                .append(encoding)
                .append('"');

        buf.append(" standalone=\"")
            .append(document.getXmlStandalone() ? "yes\"" : "no\"");

        buf.append("?>\n");
        elementToBuffer(document.getDocumentElement(), buf, "");
        return buf;
    }
    
    //----------------------------------------------------------------------

    static public String documentToString(Document document)
    {
        return documentToBuffer(document).toString();
    }

    //----------------------------------------------------------------------

    static private void elementToBuffer(Element el, StringBuffer buf, String indent)
    {
        buf.append(indent).append("<").append(el.getTagName());

        NamedNodeMap atts = el.getAttributes();
        
        if (atts.getLength() > 0)
            attributesToBuffer(atts, buf);
        
        if (! el.hasChildNodes())
            buf.append("/>\n");
        else
        {
            buf.append(">\n");
            contentToBuffer(el, buf, indent + "    ");
            buf.append(indent).append("</").append(el.getTagName()).append(">\n");        
        }
    }
    
    //----------------------------------------------------------------------

    /** Stringdarstellung
     *
     *  @param el   Das Element.
     */

    static public String toString(Element el)
    {
        StringBuffer buf = new StringBuffer();

        elementToBuffer(el, buf, "");
        return buf.toString();
    }
}
