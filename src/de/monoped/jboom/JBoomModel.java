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

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
import de.monoped.swing.PasswordManager;
import de.monoped.swing.RunWait;
import de.monoped.utils.DOMOut;
import de.monoped.utils.DOMUtils;
import de.monoped.utils.KeyBundle;
import de.monoped.utils.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/** JBoom data model. */

public class JBoomModel
{
    static KeyBundle                bundle = (KeyBundle)ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private boolean                 extractName;
    private String                  localPath, findText, config, 
                                    mailCommand, urlCommand, password, user;
    private boolean                 askSave, sortRecursive, sortFolderFirst,
                                    startMinimized, toTray, rememberPosition,
                                    findCase, findName, findURL;
    private int                     activeIndex;
    private ArrayList<Transport>    connections;

    private boolean                 changed, ok;
    private String                  errmsg;
    private PasswordManager         pwManager;
    private File                    privateKey;

    //----------------------------------------------------------------------

    /** Construct an empty model. */

    JBoomModel()
    {
        activeIndex = -1;
        connections = new ArrayList<Transport>();
        pwManager = new PasswordManager(null, bundle.getText("passwdTitle"), bundle.getText("passwdprompt"));
    }
    
    //----------------------------------------------------------------------

    JBoomModel(JBoomModel m)
    {
        this();

        extractName = m.extractName;
        localPath = m.localPath;
        findText = m.findText;
        config = m.config;
        mailCommand = m.mailCommand;
        urlCommand = m.urlCommand;
        password = m.password;
        user = m.user;
        askSave = m.askSave;
        sortRecursive = m.sortRecursive;
        sortFolderFirst = m.sortFolderFirst;
        startMinimized = m.startMinimized;
        toTray = m.toTray;
        rememberPosition = m.rememberPosition;
        findCase = m.findCase;
        findName = m.findName;
        findURL = m.findURL;
        activeIndex = m.activeIndex;
        privateKey = m.privateKey;
        connections.addAll(m.connections);
    }

    //----------------------------------------------------------------------

    /** Construct a model from a config file. A remote will be downloaded first.
     *
     *  @param config   URL string. Simple paths, ftp:// and ssh:// are supported.
     */

    JBoomModel(String config, File privateKey)
    {
        this();
        this.privateKey = privateKey;

        ok = true;
        this.config = config;

        try
        {
            if (this.config == null)
            {
                // try default config path

                this.config = System.getProperty("user.home") + File.separator + JBoom.OPTION_FILE;
                readLocalConfig();
            }
            else if (this.config.startsWith("ftp://"))
                ftpDownload();
            else if (this.config.startsWith("ssh://"))
                sshDownload();
            else if (! this.config.equals("-"))
                readLocalConfig();
        }
        catch (Exception ex)
        {
            setErrorMessage(ex);
        }

        if (! ok)
            pwManager.remove(config);
    }

    //----------------------------------------------------------------------

    /** Read config file via FTP. */

    private void ftpDownload()
    {
        FtpDownloader downloader = new FtpDownloader(); 

        try
        {
            String result = downloader.start();

            if (result != null)
                readOptions(result);
            else
                ok = false;
        }
        catch (Exception e)
        {
            setErrorMessage(e);
        }

        if (downloader.isCancelled())
            setErrorMessage(Transport.CANCELLED);
    }

    //----------------------------------------------------------------------

    /** Write config file via FTP. */

    private void ftpUpload()
    {
        FtpUploader uploader = new FtpUploader();
       
        try
        {
            ok = uploader.start();
        }
        catch (Exception e)
        {
            setErrorMessage(e);
        }

        if (uploader.isCancelled())
            setErrorMessage(Transport.CANCELLED);
    }

    //----------------------------------------------------------------------

    /** Get index of active connection. */

    int getActiveTransportIndex()
    {
        return activeIndex;
    }
    
    //----------------------------------------------------------------------

    /** Get active connection. */

    public Transport getActiveTransport()
    {
        return activeIndex < 0 || activeIndex >= connections.size() 
                ? null
                : connections.get(activeIndex);
    }
    
    //----------------------------------------------------------------------

    /** Get list of connections. */

    ArrayList<Transport> getConnections()
    {
        return connections;
    }

    //----------------------------------------------------------------------

    /** Get error message. */

    String getErrmsg()
    {
        return errmsg;
    }

    //----------------------------------------------------------------------

    /** Get search string. */

    String getFindText()
    {
        return findText;
    }
    
    //----------------------------------------------------------------------

    /** Get label denoting this connection. */

    String getLabelText(Transport trans)
    {
        String t = "<html>";

        if (trans.isActive())
            t += "<font color=\"" + JBoom.ACTIVE_COLOR + "\">";
                
        return t + trans.getName();
    }

    //----------------------------------------------------------------------

    /** Get the effective local file. If user has chosen "default", a system dependent
     *  path is selected.
     */

    File getLocalFile()
    {
        if (localPath == null)
            return null;

        File file = new File(Strings.replaceString(localPath, "/", File.separator));

        if (! file.isAbsolute())
            file = new File(System.getProperty("user.home"), file.getPath());

        return file;
    }

    //----------------------------------------------------------------------

    /** Get path of local file, if set, null otherwise. */

    String getLocalPath()
    {
        return localPath;
    }
    
    //----------------------------------------------------------------------

    /** Get the mail command string. */

    String getMailCommand()
    {
        return mailCommand;
    }

    //----------------------------------------------------------------------

    PasswordManager getPasswordManager()
    {
        return pwManager;
    }

    //----------------------------------------------------------------------

    public boolean isRememberPosition()
    {
        return rememberPosition;
    }

    //----------------------------------------------------------------------

    /** Get a connection.
     *
     *  @param k    Index of connection in list.
     *
     *  @return     The connection or null.
     */

    Transport getTransport(int k)
    {
        if (connections == null || connections.size() == 0)
            return null;

        if (k >= 0)
            return connections.get(k);

        return activeIndex < 0 ? null : connections.get(activeIndex);
    }
    
    //----------------------------------------------------------------------

    /** Get the URL command string. */

    String getURLCommand()
    {
        return urlCommand;
    }

    //----------------------------------------------------------------------

    public boolean isAskSave()
    {
        return askSave;
    }

    //----------------------------------------------------------------------

    boolean isChanged()
    {
        return changed;
    }

    //----------------------------------------------------------------------

    boolean isExtractName()
    {
        return extractName;
    }
    
    //----------------------------------------------------------------------

    boolean isFindCase()
    {
        return findCase;
    }

    //----------------------------------------------------------------------

    boolean isFindName()
    {
        return findName;
    }
    
    //----------------------------------------------------------------------

    boolean isFindURL()
    {
        return findURL;
    }
    
    //----------------------------------------------------------------------

    boolean isFolderFirst()
    {
        return sortFolderFirst;
    }

    //----------------------------------------------------------------------

    boolean isLocal()
    {
        return activeIndex < 0 && localPath != null && localPath.length() > 0;
    }

    //----------------------------------------------------------------------

    boolean isOK()
    {
        return ok;
    }

    //----------------------------------------------------------------------

    boolean isRemote()
    {
        return activeIndex >= 0 && activeIndex < connections.size();
    }
    
    //----------------------------------------------------------------------

    boolean isSortRecursive()
    {
        return sortRecursive;
    }

    //----------------------------------------------------------------------

    boolean isStartMinimized()
    {
        return startMinimized;
    }

    //----------------------------------------------------------------------

    boolean isToTray()
    {
        return toTray;
    }

    //----------------------------------------------------------------------

    /** Read local config file. */

    private void readLocalConfig()
        throws IOException
    {
        Reader r = new FileReader(config);

        readOptions(r);
        r.close();
    }

    //----------------------------------------------------------------------

    /** Read settings from an XML string. */

    private boolean readOptions(String text)
    {
        Reader r = new StringReader(text);

        boolean b = readOptions(r);

        try
        {
            r.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return b;
    }

    //----------------------------------------------------------------------

    /** Read settings from a Reader. */

    private boolean readOptions(Reader in)
    {
        DocumentBuilderFactory  factory;
        DocumentBuilder         builder = null;
        Document                document;

        try
        {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        }
        catch (Exception ignored)
        { }

        if (in != null)
        {
            try
            {
                // Read XML from props file

                document = builder.parse(new InputSource(in));
                in.close();
            }
            catch (Exception ex)
            {
                Utils.showException(null, ex);
                return false;
            }
        }
        else
        {
            document = builder.newDocument();
            document.appendChild(document.createElement("jboom"));
        }
               
        // Build connections list

        Element root = document.getDocumentElement(),
                connEl = DOMUtils.getChildElement(root, "connections");

        if (connEl != null)
        {
            List<Element>   connList = DOMUtils.getChildElements(connEl, Tags.CONN);
            int             index = 0;

            connections = new ArrayList<Transport>();

            for (Element el: connList)
            {
                Transport   trans = TransportFactory.createTransport(el);
                
                connections.add(trans);

                if (trans.isActive())
                    activeIndex = index;

                ++index;
            }
        }
        
        // Local file
        
        Element locEl = DOMUtils.getChildElement(root, "localFile");
        
        if (locEl != null)
            localPath = DOMUtils.getText(locEl);
        
        // Sort options

        Element sortEl = DOMUtils.getChildElement(root, "sort");
        String  value;

        if (sortEl != null)
        {
            value = DOMUtils.getAttributeValue(sortEl, Tags.RECURSIVE);
            sortRecursive = "true".equals(value);

            value = DOMUtils.getAttributeValue(sortEl, Tags.FOLDERFIRST);
            sortFolderFirst = "true".equals(value);
        }

        // Find options

        Element findEl = DOMUtils.getChildElement(root, Tags.FIND);

        if (findEl != null)
        {
            value = DOMUtils.getExtAttribute(findEl, Tags.CASE);
            findCase = "true".equals(value);

            value = DOMUtils.getExtAttribute(findEl, Tags.NAME);
            findName = "true".equals(value);

            value = DOMUtils.getExtAttribute(findEl, Tags.URL);
            findURL = "true".equals(value);

            findText = DOMUtils.getText(findEl);
        } 

        // Other options

        Element optionEl = DOMUtils.getChildElement(root, Tags.OPTION);

        if (optionEl != null)
        {
            extractName = "true".equals(DOMUtils.getExtAttribute(optionEl, Tags.EXTRACT));
            startMinimized = "true".equals(DOMUtils.getExtAttribute(optionEl, Tags.STARTMIN));
            toTray = "true".equals(DOMUtils.getExtAttribute(optionEl, Tags.TOTRAY));
            askSave = "true".equals(DOMUtils.getExtAttribute(optionEl, Tags.ASKSAVE));
            rememberPosition = "true".equals(DOMUtils.getExtAttribute(optionEl, Tags.REMEMBER));
        }

        // Commands

        Element cmdEl = DOMUtils.getChildElement(root, Tags.URLCOMMAND);

        if (cmdEl != null)
            urlCommand = DOMUtils.getText(cmdEl);

        Element mailCmdEl = DOMUtils.getChildElement(root, Tags.MAILCOMMAND);

        if (mailCmdEl != null)
            mailCommand = DOMUtils.getText(mailCmdEl);

        return true;
    }

    //----------------------------------------------------------------------

    /** Save configuration as XML.
     *
     *  @return String buffer containing XML text.
     */

    String saveOptions()
    {
        DOMOut out = new DOMOut(2);

        out.openElement(Tags.JBOOM)
            .openElement(Tags.CONNECTIONS);

        if (connections != null)
            for (Transport trans: connections)
                trans.appendXML(out);
        
        out.closeElement();

        if (localPath != null)
            out.textElement(Tags.LOCALFILE, localPath);

        out.openElement(Tags.SORT)
            .addAttribute(Tags.RECURSIVE, String.valueOf(sortRecursive))
            .addAttribute(Tags.FOLDERFIRST, String.valueOf(sortFolderFirst))
            .closeElement()
            .openElement(Tags.FIND)
            .addAttribute(Tags.CASE, String.valueOf(findCase))
            .addAttribute(Tags.NAME, String.valueOf(findName))
            .addAttribute(Tags.URL,  String.valueOf(findURL))
            .closeElement()
            .text(findText != null ? findText : "")
            .openElement(Tags.OPTION)
            .addAttribute(Tags.EXTRACT, String.valueOf(extractName))
            .addAttribute(Tags.STARTMIN, String.valueOf(startMinimized))
            .addAttribute(Tags.TOTRAY, String.valueOf(toTray))
            .addAttribute(Tags.ASKSAVE, String.valueOf(askSave))
            .addAttribute(Tags.REMEMBER, String.valueOf(rememberPosition))
            .closeElement();

        if (urlCommand != null)
            out.textElement(Tags.URLCOMMAND, urlCommand);

        if (mailCommand != null)
            out.textElement(Tags.MAILCOMMAND, mailCommand);

        return out.toString();
    }

    //----------------------------------------------------------------------

    /** Save configuration to a Writer. */

    void saveOptions(Writer out)
        throws IOException
    {
        out.write(saveOptions().toString());
        out.close();
    }

    //----------------------------------------------------------------------

    /** Save configuration settings. */

    void saveConfig()
    {
        if (config == null)
            return;

        try
        {
            if (config.startsWith("ftp://"))
                ftpUpload();
            else if (config.startsWith("ssh://"))
                sshUpload();
            else if (! config.equals("-"))
                saveOptions(new FileWriter(config));

            changed = false;
        }
        catch (IOException ex)
        {
            Utils.showException(null, ex);
        }
    }

    //----------------------------------------------------------------------

    /** Set index of active connection.
     *
     *  @param  activeIndex     New index. If < 0, only deactive actual active connection.
     */

    void setActiveTransportIndex(int activeIndex)
    {
        Transport trans;

        // If a connection is active, deactivate it.
        
        if (this.activeIndex >= 0)  
        {
            trans = getActiveTransport();
            
            if (trans != null)
                trans.setActive(false);
        }

        this.activeIndex = activeIndex;

        // Activate new connection

        if (activeIndex >= 0)
        {
            trans = connections.get(activeIndex);
            trans.setActive(true);
        }

        changed = true;
    }

    //----------------------------------------------------------------------

    public void setAskSave(boolean askSave)
    {
        this.askSave = askSave;
    }

    //----------------------------------------------------------------------

    void setErrorMessage(String msg)
    {
        errmsg = msg;
        ok = false;
    }

    //----------------------------------------------------------------------

    private void setErrorMessage(Exception ex)
    {
        if (JBoom.isDebug())
        {
            StringWriter    wex = new StringWriter();
            PrintWriter     pex = new PrintWriter(wex);

            for (StackTraceElement el: ex.getStackTrace())
                pex.println(el);
            
            setErrorMessage(wex.toString());
        }    
        else
            setErrorMessage(ex.toString());

        ok = false;
    }

    //----------------------------------------------------------------------

    void setExtractName(boolean extractName)
    {
        this.extractName = extractName;
        changed = true;
    }
    
    //----------------------------------------------------------------------

    void setFindCase(boolean findCase)
    {
        this.findCase = findCase;
        changed = true;
    }

    //----------------------------------------------------------------------

    void setFindName(boolean findName)
    {
        this.findName = findName;
        changed = true;
    }
    
    //----------------------------------------------------------------------

    void setFindURL(boolean findURL)
    {
        this.findURL = findURL;
        changed = true;
    }

    //----------------------------------------------------------------------

    void setFindText(String findText)
    {
        this.findText = findText;
        changed = true;
    }
    
    //----------------------------------------------------------------------

    void setFolderFirst(boolean sortFolderFirst)
    {
        this.sortFolderFirst = sortFolderFirst;
        changed = true;
    }

    //----------------------------------------------------------------------

    void setLocalPath(String localPath)
    {
        this.localPath = localPath;
        changed = true;
        ok = true;
    }

    //----------------------------------------------------------------------

    void setMailCommand(String mailCommand)
    {
        this.mailCommand = mailCommand;
        changed = true;
    }

    //----------------------------------------------------------------------

    public void setRememberPosition(boolean rememberPosition)
    {
        this.rememberPosition = rememberPosition;
    }

    //----------------------------------------------------------------------

    void setSortRecursive(boolean sortRecursive)
    {
        this.sortRecursive = sortRecursive;
        changed = true;
    }

    //----------------------------------------------------------------------

    void setStartMinimized(boolean startMinimized)
    {
        this.startMinimized = startMinimized;
    }

    //----------------------------------------------------------------------

    void setToTray(boolean toTray)
    {
        this.toTray = toTray;
    }

    //----------------------------------------------------------------------

    void setURLCommand(String command)
    {
        this.urlCommand = command;
        changed = true;
    }

    //----------------------------------------------------------------------

    /** Read config file via SSH. */

    private void sshDownload()
    {
        SshDownloader downloader = new SshDownloader();
        
        try
        {
            String result = downloader.start();

            if (result != null)
                readOptions(new StringReader(result));
            else
                ok = false;
        }
        catch (Exception e)
        {
            setErrorMessage(e);
        }

        if (downloader.isCancelled())
            setErrorMessage(Transport.CANCELLED);
    }

    //----------------------------------------------------------------------

    /** Write config file via SSH. */

    private void sshUpload()
    {
        SshUploader uploader = new SshUploader();
       
        try
        {
            ok = uploader.start();
        }
        catch (Exception e)
        {
            setErrorMessage(e);
        }

        if (uploader.isCancelled())
            setErrorMessage(Transport.CANCELLED);
    }

    //----------------------------------------------------------------------

    public void dump()
    {
        System.out.println("Connections:");
        
        for (Transport connection: connections)
            connection.dump();

        System.out.println("\nSort:  recursive=" + sortRecursive
                + " folderFirst=" + sortFolderFirst);

        System.out.println("\nFind: '" + findText + "' name=" + findName + " url=" + findURL);

    }

    //----------------------------------------------------------------------

    /** FTP configuration downloader. */

    class FtpDownloader
        extends RunWait<String>
    {
        URLComponents urlcom;

        //----------------------------------------------------------------------

        FtpDownloader()
        {
            super(null, bundle.getText("configDown"),
                bundle.getText("wait"), bundle.getText("cancel"));

            urlcom = new URLComponents(config);

            if (urlcom.password == null)
                urlcom.password = pwManager.getPassword("ftp://" + urlcom.getSpec());
        }

        //----------------------------------------------------------------------

        public String result()
            throws Exception
        {
            if (urlcom.password == null)
                return null;

            FTPClient ftp = new FTPClient();
            
            try
            {
                ftp.setRemoteHost(urlcom.host);
                ftp.connect();
                ftp.login(urlcom.user, urlcom.password);
                ftp.setType(FTPTransferType.ASCII);

                return new String(ftp.get(urlcom.path), "UTF-8");
            }
            finally
            {
                ftp.quit();
            }
        }
    }

    //----------------------------------------------------------------------

    /** FTP configuration uploader. */

    class FtpUploader
        extends RunWait<Boolean>
    {
        URLComponents urlcom;

        //----------------------------------------------------------------------

        FtpUploader()
        {   
            super(null, bundle.getText("configUp"),
                bundle.getText("wait"), bundle.getText("cancel")); 

            urlcom = new URLComponents(config);

            if (urlcom.password == null)
                urlcom.password = pwManager.getPassword("ftp://" + urlcom.getSpec());
        }

        //----------------------------------------------------------------------

        public Boolean result()
            throws Exception
        {
            if (urlcom.password == null)
                return false;

            FTPClient ftp = new FTPClient();

            try
            {
                ftp.setRemoteHost(urlcom.host);
                ftp.connect();
                ftp.login(urlcom.user, urlcom.password);
                ftp.setType(FTPTransferType.ASCII);
                ftp.put(new ByteArrayInputStream(saveOptions().toString().getBytes()), urlcom.path);
            }
            finally
            {
                ftp.quit();
            }

            return true;
        }
    }

    //----------------------------------------------------------------------

    /** SSH configuration downloader. */

    class SshDownloader
        extends RunWait<String>
    {
        URLComponents   urlcom;

        //----------------------------------------------------------------------

        SshDownloader()
        {
            super(null, bundle.getText("configDown"),
                bundle.getText("wait"), bundle.getText("cancel")); 

            urlcom = new URLComponents(config);

            if (privateKey == null && urlcom.password == null)
                urlcom.password = pwManager.getPassword("ssh://" + urlcom.getSpec());
        }

        //----------------------------------------------------------------------

        public String result()
            throws Exception
        {
            Connection conn = new Connection(urlcom.host);     

            conn.connect();

            ok = privateKey != null 
                ? conn.authenticateWithPublicKey(urlcom.user, privateKey, null)
                : conn.authenticateWithPassword(urlcom.user, urlcom.password);

            if (ok)
            {
                SCPClient               client = new SCPClient(conn);
                ByteArrayOutputStream   out = new ByteArrayOutputStream();

                client.get(urlcom.path, out);
                out.close();
                return out.toString("UTF-8");
            }
            else
                throw new IOException(bundle.getText("authfail"));
        }
    }

    //----------------------------------------------------------------------

    /** SSH configuration uploader. */

    class SshUploader
        extends RunWait<Boolean>
    {
        URLComponents urlcom;

        //----------------------------------------------------------------------

        SshUploader()
        {
            super(null, bundle.getText("configUp"), bundle.getText("wait"), bundle.getText("cancel")); 
            urlcom = new URLComponents(config);

            if (privateKey == null && urlcom.password == null)
                urlcom.password = pwManager.getPassword("ssh://" + urlcom.getSpec());
        }

        //----------------------------------------------------------------------

        public Boolean result()
            throws Exception
        {
            if (urlcom.password == null)
                return false;

            Connection conn = new Connection(urlcom.host);

            conn.connect();

            ok = privateKey != null 
                ? conn.authenticateWithPublicKey(urlcom.user, privateKey, null)
                : conn.authenticateWithPassword(urlcom.user, urlcom.password);

            if (ok)
            {
                SCPClient client = new SCPClient(conn);
                File        file = new File(urlcom.path);

                client.put(saveOptions().toString().getBytes(), file.getName(), file.getParent());
                return true;
            }
            else
                throw new IOException(bundle.getText("authfail"));
        }
    }

    //----------------------------------------------------------------------

    /** Class holding components of a URL. */

    class URLComponents
    {
        String url, host, user, password, path;

        //----------------------------------------------------------------------

        /** Constructor splits URL into components. */

        URLComponents(String url)
        {
            this.url = url;

            int iat, icolon, iuser, islash;

            if (url.indexOf(JBoom.PASSWD_TOKEN) >= 0)
            {
                password = pwManager.getPassword(config);
                url = Strings.replaceString(url, JBoom.PASSWD_TOKEN, password);
            }
            
            iuser = url.indexOf("://") + 3;
            icolon = url.indexOf(":", iuser);
            iat = url.indexOf("@", iuser);

            if (icolon >= 0)
            {
                user = url.substring(iuser, icolon); 
                password = url.substring(icolon + 1, iat);
            }
            else
                user = url.substring(iuser, iat);

            islash = url.indexOf("/", iat + 1);

            if (islash >= 0)
            {
                host = url.substring(iat + 1, islash);
                path = url.substring(islash);
            }
            else
            {
                host = url.substring(iat + 1);
                path = "/";
            }
        }

        //----------------------------------------------------------------------

        String getSpec()
        {
            return user + "@" + host;
        }

    }

}

