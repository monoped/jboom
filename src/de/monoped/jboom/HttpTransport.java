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
import de.monoped.swing.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;
import javax.swing.*;
import org.w3c.dom.*;

/** Http connection. */

class HttpTransport
    extends Transport
{
    static KeyBundle            bundle = (KeyBundle)ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private String              recvURL, sendURL, passwd;
    private static final int    LPWD = JBoom.PASSWD_TOKEN.length();

    //----------------------------------------------------------------------

    /** Construct a connection object.
     *
     *  @param name     Connection name.
     *  @param recvURL  URL for download (may contain ${PASSWD}).
     *  @param sendURL  URL for upload   (may contain ${PASSWD}).
     *  @param passwd   Password. If null and needed, user will be prompted.
     *  @param active   Connections is active.
     */

    HttpTransport(String name, String recvURL, String sendURL, String passwd, boolean active)
    {
        super(name, active);
        this.recvURL = recvURL;
        this.sendURL = sendURL;
        this.passwd = passwd;
    }

    //----------------------------------------------------------------------

    /** Construct an empty connection object.
     *
     *  @param name     Connection name.
     */

    HttpTransport(String name)
    {
        this(name, null, null, null, false);
    }
    
    //----------------------------------------------------------------------

    /** Append XML representation of this connection to a Buffer. 
     *
     *  @param  buf     The string buffer to append to.
     */
    
    void appendXML(DOMOut out)
    {
        out.openElement(Tags.CONN)
            .addAttribute(Tags.NAME, getName())
            .addAttribute(Tags.MODE, "http")
            .addAttribute(Tags.ACTIVE, String.valueOf(isActive()))
            .endElement()
            .cdataElement(Tags.RECVURL, recvURL)
            .cdataElement(Tags.SENDURL, sendURL);
        
        if (passwd != null)
            out.textElement(Tags.PASSWD, passwd);

        out.closeElement();
    }

    //----------------------------------------------------------------------

    /** Create an HttpTransport object from a DOM element,
     *  using attributes "name", "recvURL", "sendURL", "passwd", "active".
     *
     *  @param  el  The DOM Element.
     *  @return     The correspondig HttpTransport object.
     */

    static HttpTransport createTransport(Element el)
    {
        return new HttpTransport(DOMUtils.getExtAttribute(el, Tags.NAME),
                                DOMUtils.getExtAttribute(el, Tags.RECVURL),
                                DOMUtils.getExtAttribute(el, Tags.SENDURL),
                                DOMUtils.getExtAttribute(el, Tags.PASSWD),
                                "true".equals(DOMUtils.getExtAttribute(el, Tags.ACTIVE)));
    }

    //----------------------------------------------------------------------

    String getRecvURL()
    {
        return recvURL;
    }
    
    //----------------------------------------------------------------------

    String getSendURL()
    {
        return sendURL;
    }

    //----------------------------------------------------------------------

    public String getSpec()
    {
        return recvURL;
    }

    //----------------------------------------------------------------------

    String getPasswd()
    {
        return passwd;
    }
    
    //----------------------------------------------------------------------

    /** Return a UI for this connection. */

    TransportUI getUI()
    {
        return new HttpTransportUI(this);
    }

    //----------------------------------------------------------------------

    boolean isWriteable()
    {
        return sendURL != null && sendURL.length() > 0;
    }
    
    //----------------------------------------------------------------------

    boolean needsDownloadPasswd()
    {
        return recvURL.contains(JBoom.PASSWD_TOKEN) && passwd == null;
    }
    
    //----------------------------------------------------------------------

    boolean needsUploadPasswd()
    {
        return sendURL != null && sendURL.contains(JBoom.PASSWD_TOKEN) && passwd == null;
    }
    
    //----------------------------------------------------------------------

    /** Read bookmark file from remote host using Downloader. 
     *
     *  @param passwd   The password.
     */

    public String readFile(String passwd)
    {
        String url = recvURL;

        if (passwd != null)
            url = Strings.replaceString(recvURL, JBoom.PASSWD_TOKEN, passwd);

        Downloader downloader = new Downloader(url);

        try
        {
            return downloader.start();
        }
        catch (Exception e)
        {
            setMessage(e.getCause().getMessage());
        }

        return null;
    }

    //----------------------------------------------------------------------

    void setPasswd(String passwd)
    {
        this.passwd = passwd;
    }

    //----------------------------------------------------------------------

    void setRecvURL(String recvURL)
    {
        this.recvURL = recvURL;
    }
    
    //----------------------------------------------------------------------

    void setSendURL(String sendURL)
    {
        this.sendURL = sendURL;
    }
    
    //----------------------------------------------------------------------

    /** Send bookmark file to the remote host using Uploader. */

    public boolean writeFile(String s, String passwd)
    { 
        if (sendURL == null)
            return false;

        String urlstr = sendURL;

        if (passwd != null)
            urlstr = Strings.replaceString(sendURL, JBoom.PASSWD_TOKEN, passwd);

        Uploader    uploader = new Uploader(urlstr, s);
        boolean     ok = false;
        String      message;

        try
        {
            setMessage(uploader.start());
        }
        catch (Exception e)
        {
            setMessage(e.getMessage());
        }
        
        if (uploader.isCancelled())
            setMessage(CANCELLED);

        return getMessage() == null;
    }

    //----------------------------------------------------------------------
    // Downloader
    //----------------------------------------------------------------------

    /** Blocks input and downloads bookmarks from server. */

    class Downloader
        extends RunWait<String>
    {
        private String urlstr;

        //----------------------------------------------------------------------

        Downloader(String urlstr)
        {
            super(null, bundle.getText("downloading", getName()), 
                        bundle.getText("wait"), bundle.getText("cancel"));

            this.urlstr = urlstr;
        }

        //----------------------------------------------------------------------

        public String result()
            throws Exception
        {
            InputStream             in = null;
            ByteArrayOutputStream   out = null;

            try
            {
                URL         url = new URL(urlstr);
                byte[]      buf = new byte[1024];
                int         n;
                
                in = url.openStream();
                out = new ByteArrayOutputStream();

                while ((n = in.read(buf)) >= 0)
                    out.write(buf, 0, n);

                String str = out.toString("UTF-8");

                if (str.startsWith("<"))
                    return str;
                else
                    setMessage(str);
            }
            finally
            {
                try
                {
                    if (in != null)
                        in.close();

                    if (out != null)
                        out.close();
                }
                catch (IOException e)
                { }
            }

            return null;
        }
    }

    //----------------------------------------------------------------------
    // Uploader
    //----------------------------------------------------------------------

    /** Blocks input and uploads bookmarks to the remote host. */
    
    class Uploader
        extends RunWait<String>
    {
        private String text, urlstr;

        //----------------------------------------------------------------------

        /** Constructs an Uploader
         *
         *  @param  text    The XML source.
         */

        Uploader(String urlstr, String text)
        {
            super(null, bundle.getText("uploading", getName()), 
                    bundle.getText("wait"), bundle.getText("cancel")); 
            this.urlstr = urlstr;
            this.text = text;
        }

        //----------------------------------------------------------------------

        public String result()
            throws Exception
        {
            int k = urlstr.indexOf("?");

            String  pre, post, 
                    content = URLEncoder.encode(text, "UTF-8");

            if (k >= 0)
            {
                pre = urlstr.substring(0, k);
                post = urlstr.substring(k + 1) + content;
            }
            else
            {
                pre = urlstr;
                post = content;
            }

            HttpURLConnection   conn = (HttpURLConnection)new URL(pre).openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            PrintWriter conout = new PrintWriter(conn.getOutputStream());

            conout.print(post);
            conout.close();

            BufferedReader  in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String          zeile;
            StringBuffer    mess = new StringBuffer();

            while ((zeile = in.readLine()) != null)
                mess.append(zeile);

            in.close();
            return mess.length() == 0 ? null : mess.toString();
        }
    }

}

