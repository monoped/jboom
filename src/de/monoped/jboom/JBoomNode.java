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

import de.monoped.utils.DOMUtils;
import de.monoped.utils.KeyBundle;
import de.monoped.utils.Strings;
import org.w3c.dom.Element;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * JBoom Tree node.
 */

class JBoomNode
        extends DefaultMutableTreeNode
        implements Comparable {
    static final String FOLDER = "Folder",
            MAIL = "Mail",
            MARK = "Mark",
            NAME = "Name",
            URL = "URL";
    static private int newMarkIndex = 0,
            indent = 0;
    static KeyBundle bundle = (KeyBundle) ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private String name, url, text;
    private boolean isLeaf, isMail;

    //----------------------------------------------------------------------

    /**
     * Construct JBoomNode from DOM element
     */

    JBoomNode(Element el) {
        String tag = el.getTagName();

        name = Strings.xmlEntitiesToChars(DOMUtils.getAttributeValue(el, NAME));

        if (tag.equals(FOLDER))
            for (Element node : DOMUtils.getChildElements(el))
                add(new JBoomNode(node));
        else {
            isLeaf = true;
            url = DOMUtils.getAttributeValue(el, URL);

            if (DOMUtils.getAttributeValue(el, MAIL) != null)
                isMail = true;
        }

        text = Strings.xmlEntitiesToChars(DOMUtils.getText(el));

        if (DOMUtils.getAttributeValue(el, MAIL) != null)
            isMail = true;
    }

    //----------------------------------------------------------------------

    /**
     * Construct a folder
     */

    JBoomNode(String name, String text) {
        this.name = name;
        this.text = text;
    }

    //----------------------------------------------------------------------

    /**
     * Construct a folder
     */

    JBoomNode(String name) {
        this.name = name;
    }

    //----------------------------------------------------------------------

    /**
     * Construct a bookmark
     */

    JBoomNode(String name, String url, String text, boolean extract) {
        this.name = name;
        this.text = text;
        isLeaf = true;
        this.url = url;
        setKind();

        if (this.name == null)
            if (url != null && !isMail)
                setNameFromURL(extract);
            else
                this.name = bundle.getText("newMarkName");
    }

    //----------------------------------------------------------------------

    /**
     * Copy a JBoomNode
     */

    JBoomNode(JBoomNode x) {
        this.name = x.name;

        if (x.url != null)
            this.url = x.url;

        setKind();
        this.isLeaf = x.isLeaf;
        this.isMail = x.isMail;
    }

    //----------------------------------------------------------------------

    /**
     * Compare 2 nodes.
     */

    public int compareTo(Object x) {
        String pureName = getPureString(name),
                pureNameX = getPureString(((JBoomNode) x).name);

        return pureName.compareToIgnoreCase(pureNameX);
    }

    //----------------------------------------------------------------------

    /**
     * Remove this node from tree.
     */

    void detachFrom(DefaultTreeModel model) {
        if (getParent() != null)
            model.removeNodeFromParent(this);
    }

    //----------------------------------------------------------------------

    void dump() {
        String format = "%"
                + (indent == 0 ? "" : String.valueOf(indent))
                + "sname=%s url=%s text=%s\n";

        System.out.printf(format, "", name, url == null ? "" : url, text == null ? "" : text);

        if (!isLeaf) {
            indent += 2;

            int n = getChildCount();

            for (int i = 0; i < n; ++i)
                ((JBoomNode) getChildAt(i)).dump();

            indent -= 2;
        }
    }

    //----------------------------------------------------------------------

    /**
     * Test equality.
     */

    public boolean equals(Object x) {
        JBoomNode k = (JBoomNode) x;

        return isLeaf == k.isLeaf && name.equals(k.name) &&
                (!isLeaf || url.equals(k.url));
    }

    //----------------------------------------------------------------------

    /**
     * Return child with a given name.
     *
     * @param name Name to find.
     * @return Child with this name or null.
     */

    JBoomNode getChild(String name) {
        int n = getChildCount();

        for (int i = 0; i < n; ++i) {
            JBoomNode child = (JBoomNode) getChildAt(i);

            if (child.getName().equals(name))
                return child;
        }

        return null;
    }

    //----------------------------------------------------------------------

    /**
     * Return an array of children.
     */

    JBoomNode[] getChildren() {
        int n = getChildCount();
        JBoomNode[] kinder = new JBoomNode[n];

        for (int i = 0; i < n; ++i)
            kinder[i] = (JBoomNode) getChildAt(i);

        return kinder;
    }

    //----------------------------------------------------------------------

    /**
     * Return the name.
     */

    String getName() {
        return name;
    }

    //----------------------------------------------------------------------

    /**
     * String content with HTML tags removed.
     */

    private String getPureString(String str) {
        if (str == null)
            return null;

        StringBuilder buf = new StringBuilder(str);

        while (true) {
            int i = buf.indexOf("<");

            if (i < 0)
                break;

            int j = buf.indexOf(">", i);

            if (j < 0) {
                buf.delete(i, buf.length());
                break;
            }

            buf.delete(i, j + 1);
        }

        return buf.toString();
    }

    //----------------------------------------------------------------------

    /**
     * Get pure name, no HTML.
     *
     * @return Name with HTML tags removed.
     */

    String getPureName() {
        return getPureString(name);
    }

    //----------------------------------------------------------------------

    /**
     * Get pure text, no HTML.
     *
     * @return Name with HTML tags removed.
     */

    String getPureText() {
        return getPureString(text);
    }

    //----------------------------------------------------------------------

    /**
     * Get the URL or mail address.
     *
     * @return If it is a leaf, the url. If it is a directory, a list of
     *         mail addresses.
     */

    String getURL() {
        if (isLeaf)
            return url;

        // Directory

        String addr = "";

        int n = getChildCount();

        for (int i = 0; i < n; ++i) {
            JBoomNode child = (JBoomNode) getChildAt(i);

            String ad = child.getURL();

            if (addr.length() == 0)
                addr = ad;
            else
                addr += "," + ad;
        }

        return addr;
    }

    //----------------------------------------------------------------------

    /**
     * Return the text of this node.
     */

    String getText() {
        return text;
    }

    //----------------------------------------------------------------------

    /**
     * Insert a clipboard node into parent node.
     *
     * @param model  Tree model.
     * @param parent Target node.
     * @param index  Index at which to insert.
     */

    void insertInto(DefaultTreeModel model, JBoomNode parent, int index) {
        model.insertNodeInto(this, parent, index);
    }

    //----------------------------------------------------------------------

    /**
     * Is this a leaf?
     */

    public boolean isLeaf() {
        return isLeaf;
    }

    //----------------------------------------------------------------------

    /**
     * Is this a mail address or mailing list?
     */

    public boolean isMail() {
        if (isLeaf)
            return isMail;

        int n = getChildCount();

        if (n == 0)
            return false;

        for (int i = 0; i < n; ++i) {
            JBoomNode child = (JBoomNode) getChildAt(i);

            if (!child.isMail())
                return false;
        }

        return true;
    }

    //----------------------------------------------------------------------

    /**
     * Merge a node into this one.
     */

    void merge(JBoomNode node) {
        if (getChildCount() > 0)
            for (JBoomNode child = (JBoomNode) getFirstChild(); child != null; child = (JBoomNode) child.getNextSibling())
                if (child.equals(node)) {
                    if (!child.isLeaf())
                        child.mergeChildren(node);

                    return;
                }

        insert(node, 0);
    }

    //----------------------------------------------------------------------

    /**
     * Merge all children into this node.
     *
     * @param node Merge this nodes children here.
     */

    void mergeChildren(JBoomNode node) {
        for (JBoomNode mchild = (JBoomNode) node.getFirstChild(); mchild != null; mchild = (JBoomNode) mchild.getNextSibling())
            merge(mchild);
    }

    //----------------------------------------------------------------------

    /**
     * Try to find out which kind of address (URL, mail) we have, and
     * try to extract the name if it is a mail address.
     */

    void setKind() {
        isMail = false;

        if (!isLeaf)
            return;

        if (url == null || url.length() == 0)
            return;

        // remove "mailto:" if present

        if (url.toLowerCase().startsWith("mailto:")) {
            isMail = true;
            url = url.substring(7);
        } else
            try {
                new URL(url);
            } catch (MalformedURLException ex) {
                if (url.indexOf("@") >= 0)
                    isMail = true;
            }

        if (isMail) {
            // check if format is "name <address>"

            int ileft = url.indexOf("<");

            if (ileft >= 0) {
                if (name == null && ileft > 0)
                    name = url.substring(0, ileft - 1).trim();

                url = url.substring(ileft + 1);

                int iright = url.indexOf(">");

                if (iright >= 0)
                    url = url.substring(0, iright);
            }

            int iat = url.indexOf("@");

            if (iat < 0)
                url = "nobody@" + url;

            if (name == null) {
                if (iat < 0)
                    name = "nobody";
                else {
                    name = url.substring(0, iat).trim();

                    ileft = name.indexOf(".");

                    if (ileft >= 0)
                        name = Character.toUpperCase(name.charAt(0))
                                + name.substring(1, ileft) + " "
                                + Character.toUpperCase(name.charAt(ileft + 1))
                                + name.substring(ileft + 2);
                }
            }
        }
    }

    //----------------------------------------------------------------------

    void setName(String name) {
        this.name = name;
    }

    //----------------------------------------------------------------------

    void setNameFromURL(boolean extract) {
        if (url == null) {
            name = bundle.getText("newMark") + " " + ++newMarkIndex;
            return;
        }

        if (extract) {
            // Try getting the name from "title" tag.

            URL theURL = null;

            try {
                theURL = new URL(url);
            } catch (MalformedURLException e) {
                // Invalid address, try with prepended "http://" 

                if (!url.startsWith("http"))
                    try {
                        theURL = new URL("http://" + url);
                    } catch (MalformedURLException ex) {
                        // Still invalid, bail out

                        theURL = null;
                    }
            }

            if (theURL != null) {
                // Got a valid URL. Start a TitleReader to get the page's title.

                TitleReader titread = new TitleReader(theURL);

                try {
                    name = titread.start();
                } catch (Exception e) {
                }

                if (name != null)
                    return;
            }
        }

        // Couldn't read title, make name from URL

        int ke = url.length();

        if (ke == 0) {
            name = bundle.getText("newMark");
            return;
        }

        // Try to create a name from URL

        if (url.endsWith("/"))
            --ke;

        int ka = url.lastIndexOf("/", ke - 1);

        if (ka < 0) {
            name = url;
            return;
        }

        if (url.startsWith("index.", ka + 1)) {
            ke = ka;
            ka = url.lastIndexOf("/", ka - 1);
        }

        name = url.substring(ka + 1, ke);
    }

    //----------------------------------------------------------------------

    void setText(String text) {
        this.text = text;
    }

    //----------------------------------------------------------------------

    void setURL(String url) {
        this.url = url;
        setKind();
    }

    //----------------------------------------------------------------------

    /**
     * Append content to string buffer.
     *
     * @param buf   The buffer.
     * @param inset Inset string (left margin).
     */

    void toBuffer(StringBuilder buf, String inset) {
        buf.append(inset).append("<").append(isLeaf ? MARK : FOLDER)
                .append(" ").append(NAME).append("=\"")
                .append(Strings.xmlCharsToEntities(name))
                .append('"');

        String txt = Strings.xmlCharsToEntities(text);

        if (isLeaf) {
            buf.append(" ").append(URL).append("=\"")
                    .append(Strings.xmlCharsToEntities(url))
                    .append("\"");

            if (isMail)
                buf.append(" ").append(MAIL).append("=\"1\"");

            if (txt != null && txt.length() > 0)
                buf.append(">").append(txt).append("</").append(MARK).append(">\n");
            else
                buf.append("/>\n");
        } else {
            buf.append(">");

            if (txt != null)
                buf.append(txt);

            buf.append("\n");

            int n = getChildCount();

            String insetneu = inset + "  ";

            for (int i = 0; i < n; ++i)
                ((JBoomNode) getChildAt(i)).toBuffer(buf, insetneu);

            buf.append(inset).append("</").append(FOLDER).append(">\n");
        }
    }

    //----------------------------------------------------------------------

    public String toString() {
        return name;
    }
}

