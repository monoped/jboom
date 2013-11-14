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

import de.monoped.utils.KeyBundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Read Firefox html bookmark file and transform into
 * array of JBoomNodes
 */

class HTMLImporter {
    private static int I_URL = 0, I_NAME = 1, I_TEXT = 2;
    static KeyBundle bundle = (KeyBundle) ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private Reader in;
    private String text;
    private int index;

    //----------------------------------------------------------------------

    HTMLImporter(Reader in) {
        this.in = in;

        Pattern pattern = Pattern.compile("\\p{Cntrl}");

        // Read HTML completely into string

        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(in);
        String line;

        try {
            while ((line = reader.readLine()) != null)
                builder.append(line).append('\n');

            reader.close();
            text = pattern.matcher(builder.toString()).replaceAll("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        index = 0;
    }

    //----------------------------------------------------------------------

    private int find(String s, int k)
            throws ParseException {
        int i = text.indexOf(s, k);

        if (i < 0)
            throw new ParseException(bundle.getText("expected", s, String.valueOf(lineno(k))), k);

        return i;
    }

    //----------------------------------------------------------------------

    private int findc(String s, int k)
            throws ParseException {
        int l = s.length();

        for (int i = k; i < text.length() - l; ++i)
            if (text.regionMatches(true, i, s, 0, l))
                return i;

        throw new ParseException(bundle.getText("expected", s, String.valueOf(lineno(k))), k);
    }

    //----------------------------------------------------------------------

    /**
     * Find and return one of the supplied tags or null if none found
     */

    private String findTag(String... tags)
            throws ParseException {
        for (; index < text.length() && (index = text.indexOf("<", index)) >= 0; ++index)
            for (String tag : tags)
                if (text.regionMatches(true, index, tag, 0, tag.length())) {
                    index += tag.length();
                    return tag;
                }

        return null;
    }

    //----------------------------------------------------------------------

    /**
     * Return text (up to next tag)
     */

    private String getText()
            throws ParseException {
        StringBuilder s = new StringBuilder("<html>");
        int iStart = index;

        while (true) {
            if (index >= text.length())
                throw new ParseException(bundle.getText("textIncomp", String.valueOf(lineno(iStart))), iStart);

            char c = text.charAt(index);

            if (c == '<')
                break;
            else {
                s.append(c);
                ++index;
            }
        }

        return s.toString().trim();
    }

    //----------------------------------------------------------------------

    // read <A

    private void getUrlName(String[] urlName)
            throws ParseException {
        int i, j, k, l;

        i = findc("HREF=\"", index);
        j = find("\"", i + 6);
        k = find(">", j + 1);
        l = findc("</A>", k + 1);

        urlName[I_URL] = text.substring(i + 6, j);
        urlName[I_NAME] = "<html>" + text.substring(k + 1, l);

        if (urlName[I_NAME].endsWith(".url") || urlName[I_NAME].endsWith(".URL"))
            urlName[I_NAME] = urlName[I_NAME].substring(0, urlName[I_NAME].length() - 4);

        index = l + 4;
    }

    //----------------------------------------------------------------------

    ArrayList<JBoomNode> importHTML()
            throws ParseException {
        if (text == null || findTag("<DL>") == null)
            return null;

        return readList();
    }

    //----------------------------------------------------------------------

    private int lineno(int ichar) {
        int j = 0,
                l = 1;

        while ((j = text.indexOf("\n", j)) >= 0)
            if (j++ > ichar)
                break;
            else
                ++l;

        return l;
    }

    //----------------------------------------------------------------------

    private String nextTag()
            throws ParseException {
        int iStart = index;

        index = text.indexOf("<", index);

        if (index < 0)
            return null;

        StringBuilder s = new StringBuilder();
        char c;
        int i;

        for (i = index + 1; i < text.length() && (c = text.charAt(i)) != ' ' && c != '/' && c != '>'; ++i)
            s.append(c);

        if (i >= text.length())
            throw new ParseException(bundle.getText("tagIncompl", String.valueOf(lineno(iStart))), iStart);

        return s.toString().toUpperCase();
    }

    //----------------------------------------------------------------------

    private ArrayList<JBoomNode> readList()
            throws ParseException {
        ArrayList<JBoomNode> list = new ArrayList<JBoomNode>();
        JBoomNode node;

        while ((node = readNode()) != null)
            if (!node.getURL().startsWith("place:") && !list.contains(node))
                list.add(node);

        return list;
    }

    //----------------------------------------------------------------------

    private JBoomNode readNode()
            throws ParseException {
        String tag = findTag("<DT>", "</DL>");

        if (tag == null || tag.equals("</DL>"))
            return null;

        // Get next tag, should be <A> (leaf) or <H3> (folder)

        tag = nextTag();

        if (tag == null)
            return null;

        String[] urlName = new String[3];

        if (tag.equals("A"))
            getUrlName(urlName);        // leaf
        else if (tag.equals("H3")) {                               // folder
            index = find(">", index);
            ++index;

            urlName[I_URL] = null;
            urlName[I_NAME] = getText();
            index = find(">", index) + 1;
        }

        // comment?

        String dd = nextTag();

        if (dd.equals("DD")) {
            index = find(">", index) + 1;
            urlName[I_TEXT] = getText();
        } else
            urlName[I_TEXT] = null;

        if (urlName[I_URL] == null) {
            // folder

            JBoomNode node = new JBoomNode(urlName[I_NAME], urlName[I_TEXT]);
            ArrayList<JBoomNode> list = readList();

            if (list != null) {
                for (JBoomNode n : list)
                    node.add(n);

                return node;
            } else
                return null;
        } else
            return new JBoomNode(urlName[I_NAME], urlName[I_URL], urlName[I_TEXT], false);
    }

}

