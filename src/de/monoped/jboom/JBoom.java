package de.monoped.jboom;

/**
 * Created with IntelliJ IDEA.
 * Date: 09.11.13
 * Time: 19:15
 */

import de.monoped.swing.RunWait;
import de.monoped.swing.UIAction;
import de.monoped.swing.UIFactory;
import de.monoped.swing.Utilities;
import de.monoped.utils.KeyBundle;
import de.monoped.utils.Strings;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.prefs.Preferences;

/**
 * Main class with GUI
 */

public class JBoom
        extends JPanel
        implements MouseListener, PopupMenuListener, TreeSelectionListener {
    static public final String OPTION_FILE = "jboomrc.xml",
            PASSWD_TOKEN = "${PASSWD}",
            ACTIVE_COLOR = "#20b231",

    DEFAULT_MAIL_CMD = "mailto ",
            DEFAULT_BROWSER_CMD = "open-url ";
    static private final int MAX_TEXT_HEIGHT = 80;
    static private final Color TEXT_BG = new Color(0xfaffbd);
    static KeyBundle bundle = (KeyBundle) ResourceBundle.getBundle("de.monoped.jboom.Resources");
    private static boolean debug = false;
    private boolean application, changed, initialized;
    private Clip clip;
    private String configFile;
    private ConnectionPopupListener connPopupListener;
    private UploadPopupListener uploadPopupListener;
    private JBoomNode jboomRoot;
    private JMenuItem copyItem;
    private JMenuItem insertItem;
    private JMenuItem newBookmarkItem;
    private JMenuItem newFolderItem;
    private JMenuItem rootItem;
    private JButton downButton;
    private JButton downloadButton;
    private JButton ledButton;
    private JButton newMarkButton;
    private JButton newFolderButton;
    private JButton sortButton;
    private JButton upButton;
    private JButton uploadButton;
    private Action downAction;
    private Action downloadAction;
    private Action findAction;
    private Action enterAction;
    private Action markAction;
    private Action markAction0;
    private Action markAction1;
    private Action folderAction;
    private Action optionsAction;
    private Action rootAction;
    private Action rootDirAction;
    private Action upAction;
    private Action uploadAction;
    private Action pasteAction;
    private Action cutAction;
    private Action copyAction;
    private Action editAction;
    private FindPanel findPanel;
    private GridBagConstraints gbc;
    private JBoomModel model;
    private JBoomNodeRenderer renderer;
    private JScrollPane textScroll;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private JPopupMenu treePopupMenu;
    private URLHandler urlHandler;
    private Preferences prefs;
    private ActionListener listener;
    private UIFactory uif;

    //----------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param configFile  Path/URL of config file or "-" for no config file
     * @param urlHandler  Provides the method handleURL which will be called if a
     *                    bookmark is activated by double-click or enter.
     * @param application True if part of a standalone program.
     */

    JBoom(String configFile, URLHandler urlHandler, boolean application, File privateKey) {
        this.application = application;
        this.configFile = configFile;
        uif = new UIFactory();
        prefs = Preferences.userRoot().node("jboom");

        // Before exiting, reset the "running" indicator

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                setRunning(false);
            }
        });

        // if another jboom is running, warn user

        if (isRunning()) {
            if (application) {
                if (JOptionPane.showConfirmDialog(this, bundle.getText("askrunning"), bundle.getText("confirm"),
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)

                    System.exit(1);
            } else
                JOptionPane.showMessageDialog(this, bundle.getText("isrunning"), bundle.getText("warn"),
                        JOptionPane.WARNING_MESSAGE);
        }

        setRunning(true);
        RunWait.setDefaultSize(300, 100);

        try {
            model = new JBoomModel(configFile, privateKey);

            if (!model.isOK() && configFile != null) {
                // could not read configuration

                FilePanel filePanel = new FilePanel(
                        bundle.getText("useLocal", model.getErrmsg()),
                        bundle.getText("localFile"),
                        new File(System.getProperty("user.home"), "bookmarks.xml"));

                int ret = JOptionPane.showConfirmDialog(this, filePanel, bundle.getText("error"),
                        JOptionPane.YES_NO_CANCEL_OPTION);

                if (ret == JOptionPane.YES_OPTION)
                    model.setLocalPath(filePanel.getPath());
                else if (ret == JOptionPane.CANCEL_OPTION && application)
                    System.exit(1);
            }

            this.urlHandler = urlHandler;

            // Control panel on top

            JPanel controlPanel = createControlPanel();

            gbc = new GridBagConstraints();

            gbc.gridx = gbc.gridy = 0;
            gbc.gridwidth = gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.weightx = 1;
            gbc.weighty = 0;

            setLayout(new GridBagLayout());
            add(controlPanel, gbc);

            // Main panel with tree

            createTree();

            JScrollPane scroll = new JScrollPane(tree);

            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1;

            gbc.gridy = 1;
            add(scroll, gbc);

            // Create find panel

            findPanel = new FindPanel(this);

            createInitialTree();
            setButtonStatusAndText();
        } catch (Exception ex) {
            Utils.showException(null, ex);

            if (application)
                System.exit(1);
        }

        setLed();
    }

    //----------------------------------------------------------------------

    static public boolean isDebug() {
        return debug;
    }

    //----------------------------------------------------------------------

    static void setDebug(boolean debug) {
        JBoom.debug = debug;
    }

    //----------------------------------------------------------------------

    void activateNode(JBoomNode node) {
        if (node.isMail())
            composeMail(node.getURL());
        else
            gotoURL(node.getURL());
    }

    //----------------------------------------------------------------------

    public void addActionListener(ActionListener l) {
        listener = AWTEventMulticaster.add(listener, l);
    }

    //----------------------------------------------------------------------

    void composeMail(String addr) {
        String cmd = model.getMailCommand(),
                errmsg;

        if (cmd != null) {
            // Explicit command given

            cmd = Strings.replaceString(cmd, "${ADDR}", addr);

            try {
                Runtime.getRuntime().exec(cmd);
                return;
            } catch (Exception ex) {
                if (isDebug())
                    ex.printStackTrace();

                errmsg = ex.toString();
            }
        }

        // No command or command failed, try Desktop

        Desktop desktop = Desktop.getDesktop();

        if (desktop.isSupported(Desktop.Action.MAIL)) {
            try {
                URI uri = new URI("mailto", addr, null);

                desktop.mail(uri);
                return;
            } catch (Exception ex) {
                if (isDebug())
                    ex.printStackTrace();

                errmsg = ex.toString();
            }
        }

        // Fallback

        try {
            Runtime.getRuntime().exec(JBoom.DEFAULT_MAIL_CMD + addr);
            return;
        } catch (Exception ex) {
            if (isDebug())
                ex.printStackTrace();

            errmsg = ex.toString();
        }

        JOptionPane.showMessageDialog(this, errmsg, bundle.getText("error"), JOptionPane.ERROR_MESSAGE);
    }

    //----------------------------------------------------------------------

    private void composeMailList(JBoomNode node) {
        String cmd = model.getMailCommand(),
                errmsg = null,
                addresses = node.getURL();

        if (cmd != null) {
            cmd = Strings.replaceString(cmd, "${ADDR}", addresses);

            try {
                Runtime.getRuntime().exec(cmd);
                return;
            } catch (Exception ex) {
                if (isDebug())
                    ex.printStackTrace();

                errmsg = ex.toString();
            }
        }

        Desktop desktop = Desktop.getDesktop();

        if (desktop.isSupported(Desktop.Action.MAIL)) {
            try {
                URI uri = new URI("mailto:" + addresses);

                desktop.mail(uri);
                return;
            } catch (Exception ex) {
                if (isDebug())
                    ex.printStackTrace();

                errmsg = ex.toString();
            }
        }

        JOptionPane.showMessageDialog(this, errmsg, bundle.getText("error"), JOptionPane.ERROR_MESSAGE);
    }

    //----------------------------------------------------------------------

    /**
     * Copy selected nodes into clipboard.
     */

    public void copyNode() {
        TreePath[] paths = Utils.getSelectionPathSequence(tree);

        clip = new Clip(paths, true);
        insertItem.setEnabled(true);
    }

    //----------------------------------------------------------------------

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();

        // cut entry

        cutAction = new UIAction("del.gif", bundle, "cut", "ctrl X") {
            public void actionPerformed(ActionEvent e) {
                cutNodes();
            }
        };

        // copy entry

        copyAction = new UIAction("copy.gif", bundle, "copy", "ctrl C") {
            public void actionPerformed(ActionEvent e) {
                copyNode();
            }
        };

        // move node down

        downAction = new UIAction("down.gif", bundle, "down") {
            public void actionPerformed(ActionEvent e) {
                moveNodeDown();
            }
        };

        // download (popup menu)

        downloadAction = new UIAction("download.gif", bundle, "download", "alt PAGE_DOWN") {
            public void actionPerformed(ActionEvent e) {
                download();
            }
        };

        // edit entry

        editAction = new UIAction("edit.gif", bundle, "edit", "ctrl E") {
            public void actionPerformed(ActionEvent e) {
                editNode();
            }
        };

        // enter entry, show URL or mail

        enterAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String addr = getAddressList();

                if (addr != null) {
                    if (addr.startsWith("@"))
                        composeMail(addr.substring(1));
                    else
                        gotoURL(addr);
                }
            }
        };

        // new folder

        folderAction = new UIAction("folder.png", bundle, "newFolder", "ctrl F") {
            public void actionPerformed(ActionEvent e) {
                newFolder();
            }
        };

        // find

        findAction = new UIAction("find.gif", bundle, "find", KeyStroke.getKeyStroke("F3")) {
            public void actionPerformed(ActionEvent e) {
                showFindPanel();
            }
        };

        // LED -> about

        Action ledAction = new UIAction(bundle, "about") {
            public void actionPerformed(ActionEvent e) {
                showAbout();
            }
        };

        // new bookmark

        markAction0 = new MarkAction(MarkAction.CLIP_NO);       // Shift-Ctrl-B
        markAction1 = new MarkAction(MarkAction.CLIP_YES);      // Ctrl-B
        markAction = new MarkAction(MarkAction.CLIP_SHIFT);     // Button

        // preferences

        optionsAction = new UIAction("preferences.gif", bundle, "prefs", "ctrl P") {
            public void actionPerformed(ActionEvent e) {
                setOptions();
            }
        };

        // paste entry

        pasteAction = new UIAction("paste.gif", bundle, "insert", "ctrl V") {
            public void actionPerformed(ActionEvent e) {
                insertNodeArray(clip.getCopy());
            }
        };

        // show/hide root

        rootAction = new UIAction("root.gif", bundle, "root", "ctrl R") {
            public void actionPerformed(ActionEvent e) {
                toggleRootVisible();
            }
        };

        // set root

        rootDirAction = new UIAction("rootdir.gif", bundle, "rootDir") {
            public void actionPerformed(ActionEvent e) {
                makeRoot();
            }
        };

        // sort

        Action sortAction = new UIAction("sort.png", bundle, "sort") {
            public void actionPerformed(ActionEvent e) {
                sortNode();
            }
        };

        // move node up

        upAction = new UIAction("up.gif", bundle, "up", KeyStroke.getKeyStroke("alt UP")) {
            public void actionPerformed(ActionEvent e) {
                moveNodeUp();
            }
        };

        // upload

        uploadAction = new UIAction("upload.gif", bundle, "upload", KeyStroke.getKeyStroke("alt PAGE_UP")) {
            public void actionPerformed(ActionEvent e) {
                upload();
            }
        };

        uif.setMargin(0, 0, 0, 0);
        ledButton = uif.iconButton(ledAction);
        JButton optionsButton = uif.button(optionsAction);
        newMarkButton = uif.button(markAction);
        newFolderButton = uif.button(folderAction);
        uploadButton = uif.button(uploadAction);
        downloadButton = uif.button(downloadAction);
        upButton = uif.button(upAction);
        downButton = uif.button(downAction);
        JButton showRootButton = uif.button(rootAction);
        sortButton = uif.button(sortAction);
        JButton findButton = uif.button(findAction);

        controlPanel.add(ledButton);
        controlPanel.add(optionsButton);
        controlPanel.add(newMarkButton);
        controlPanel.add(newFolderButton);
        controlPanel.add(upButton);
        controlPanel.add(downButton);
        controlPanel.add(sortButton);
        controlPanel.add(findButton);
        controlPanel.add(uploadButton);
        controlPanel.add(downloadButton);
        controlPanel.add(showRootButton);

        return controlPanel;
    }

    //----------------------------------------------------------------------

    private void createTree() {
        tree = new JTree();
        tree.addTreeSelectionListener(this);
        tree.setRowHeight(20);
        tree.setCellRenderer(renderer = new JBoomNodeRenderer());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.addMouseListener(this);

        setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, new JBoomInputMap(getInputMap()));
        setActionMap(new JBoomActionMap(getActionMap()));

        tree.setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, new JBoomInputMap(tree.getInputMap()));
        ActionMap treeActionMap;
        tree.setActionMap(treeActionMap = new JBoomActionMap(tree.getActionMap()));

        tree.setModel(null);

        // Create tree popup

        treePopupMenu = new JPopupMenu();
        treePopupMenu.addPopupMenuListener(this);

        JMenuItem editItem = new JMenuItem(editAction);
        insertItem = new JMenuItem(pasteAction);
        insertItem.setEnabled(false);
        copyItem = new JMenuItem(copyAction);
        JMenuItem cutItem = new JMenuItem(cutAction);
        newBookmarkItem = new JMenuItem(markAction);
        newFolderItem = new JMenuItem(folderAction);
        rootItem = new JMenuItem(rootDirAction);

        treePopupMenu.add(editItem);
        treePopupMenu.add(cutItem);
        treePopupMenu.add(copyItem);
        treePopupMenu.add(insertItem);
        treePopupMenu.add(newBookmarkItem);
        treePopupMenu.add(newFolderItem);
        treePopupMenu.add(rootItem);

        connPopupListener = new ConnectionPopupListener();
        uploadPopupListener = new UploadPopupListener();

        ToolTipManager.sharedInstance().registerComponent(tree);
        ToolTipManager.sharedInstance().setInitialDelay(750);
    }

    //----------------------------------------------------------------------

    /**
     * Create an initial tree model
     */

    private void createInitialTree() {
        if (initialized)
            return;

        jboomRoot = new JBoomNode(bundle.getText("myMarks"));  // root folder
        jboomRoot.add(new JBoomNode("jBoom", "http://jboom.sourceforge.net/", bundle.getText("jboomhome"), false));
        treeModel = new DefaultTreeModel(jboomRoot);
        tree.setModel(treeModel);
    }

    //----------------------------------------------------------------------

    /**
     * Create TreeModel from XML document.
     */

    private void createTreeModel(Document document) {
        jboomRoot = new JBoomNode(document.getDocumentElement());
        treeModel = new DefaultTreeModel(jboomRoot);
        tree.setModel(treeModel);
    }

    //----------------------------------------------------------------------

    /**
     * Cut selected nodes into clipboard.
     */

    public void cutNodes() {
        TreePath[] paths = Utils.getSelectionPathSequence(tree);
        JBoomNode selectNode = (JBoomNode) paths[0].getLastPathComponent(),
                parent = (JBoomNode) selectNode.getParent();

        if (selectNode == jboomRoot) {
            if (JOptionPane.showConfirmDialog(this, bundle.getText("initwarn"), bundle.getText("confirm"),
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
                return;

            // Reset tree

            initialized = false;
            createInitialTree();
            tree.setSelectionRow(0);
            insertItem.setEnabled(true);
            informListener();
            changed = true;
            informListener();
            return;
        }

        // Selection after cut: previous child or parent

        if (parent == null)
            parent = jboomRoot;

        selectNode = (JBoomNode) parent.getChildBefore(selectNode);

        if (selectNode == null)
            selectNode = parent;

        // Create new clip from selection

        clip = new Clip(paths, true);

        // remove selected nodes from model

        for (TreePath path : paths)
            ((JBoomNode) path.getLastPathComponent()).detachFrom(treeModel);

        changed = true;
        insertItem.setEnabled(true);

        // change selection

        tree.setSelectionPath(new TreePath(selectNode.getPath()));

        informListener();
    }

    //----------------------------------------------------------------------

    /**
     * Download bookmark file.
     * If only one connection is defined, use that.
     * Otherwise show a popup menu with availabe connections.
     */

    public void download() {
        ArrayList<Transport> connections = model.getConnections();

        if (connections.size() == 0) {
            // no connections

            download(-1);
            return;
        }

        if (connections.size() == 1 && model.getLocalPath() == null) {
            // just one

            download(0);
            return;
        }

        // show connection popup menu

        JPopupMenu downloadPopupMenu = new JPopupMenu();
        int index = 0;

        for (Transport connection : connections) {
            JMenuItem item = new JMenuItem(model.getLabelText(connection), Icons.SERVER);

            // index is action command

            item.setActionCommand(String.valueOf(index++));
            downloadPopupMenu.add(item);
            item.addActionListener(connPopupListener);
        }

        if (model.getLocalPath() != null) {
            // add 'local'

            JMenuItem item = new JMenuItem(bundle.getText("local"), Icons.DISK);

            item.setActionCommand("local");
            downloadPopupMenu.add(new JSeparator());
            downloadPopupMenu.add(item);
            item.addActionListener(connPopupListener);
        }

        // separator add import item

        downloadPopupMenu.add(new JSeparator());

        downloadPopupMenu.add(new UIAction(Icons.MARK, bundle, "importHTML") {
            public void actionPerformed(ActionEvent e) {
                importHTML();
            }
        });

        downloadPopupMenu.add(new UIAction(Icons.MAIL, bundle, "importCSV") {
            public void actionPerformed(ActionEvent e) {
                importCSV();
            }
        });

        downloadPopupMenu.show(this, downloadButton.getX() + 10, downloadButton.getY());
    }

    //----------------------------------------------------------------------

    /**
     * Download the XML source and create the tree model.
     */

    void download(int index) {
        boolean ok = false;

        if (index >= 0) {
            ok = downloadRemote(index);

            if (!ok) {
                Transport transport = model.getConnections().get(index);

                model.getPasswordManager().remove(transport.getSpec());

                if (index == model.getActiveTransportIndex()) {
                    JOptionPane.showMessageDialog(this, bundle.getText("usingLocal"),
                            bundle.getText("warn"), JOptionPane.WARNING_MESSAGE);

                    downloadLocal();
                    model.setActiveTransportIndex(-1);
                }
            }
        } else if (model.isLocal())
            ok = downloadLocal();

        if (!ok)
            createInitialTree();

        setLed();
        tree.repaint();
        tree.setSelectionRow(0);
        tree.requestFocusInWindow();
//        changed = index != model.getActiveTransportIndex();
        informListener();
    }

    //----------------------------------------------------------------------

    /**
     * Download from active connection.
     */

    void downloadActive() {
        download(model.getActiveTransportIndex());
    }

    //----------------------------------------------------------------------

    /**
     * Load local XML file.
     */

    private boolean downloadLocal() {
        File localFile = model.getLocalFile();

        if (localFile == null)
            return false;

        if (!localFile.exists()) {
            JOptionPane.showMessageDialog(this, bundle.getText("fileNotExist", model.getLocalPath()),
                    bundle.getText("warn"), JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;
            BufferedReader bufread = new BufferedReader(new FileReader(localFile));

            document = builder.parse(new InputSource(bufread));
            bufread.close();
            createTreeModel(document);
            initialized = true;
        } catch (Exception ex) {
            Utils.showException(this, ex);
            return false;
        }

        return true;
    }

    //----------------------------------------------------------------------

    /**
     * Download remote bookmarks.
     */

    boolean downloadRemote(int index) {
        Transport transport = model.getConnections().get(index);

        String passwd = null;

        if (transport.needsDownloadPasswd()) {
            passwd = transport.getPasswd();

            // ask for password if unknown

            if (passwd == null)
                passwd = model.getPasswordManager().getPassword(transport.getSpec());

            if (passwd == null)
                return false;
        }

        try {
            final String xml = transport.readFile(passwd);
            String message = transport.getMessage();

            if (message != null) {
                // An error occured or transport was cancelled

                if (!message.equals(Transport.CANCELLED)) {
                    // error

                    model.getPasswordManager().remove(transport.getSpec());
                    JOptionPane.showMessageDialog(JBoom.this, transport.getMessage(),
                            bundle.getText("warn"), JOptionPane.ERROR_MESSAGE);
                }

                return false;
            }

            // OK, got it

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            if (xml != null && xml.length() > 0) {
                // Parse XML

                StringReader reader = new StringReader(xml);
                Document doc = builder.parse(new InputSource(reader));
                JBoomNode otherRoot = new JBoomNode(doc.getDocumentElement());

                if (!initialized || jboomRoot == null)
                    jboomRoot = otherRoot;
                else if (index == model.getActiveTransportIndex())
                    jboomRoot.mergeChildren(otherRoot);
                else
                    getSelectedNode().merge(otherRoot);

                initialized = true;

                // Create tree model from document

                treeModel = new DefaultTreeModel(jboomRoot);
                tree.setModel(treeModel);
                tree.setSelectionRow(0);
                tree.requestFocusInWindow();

                // Save to local file

                if (model.getLocalPath() != null)
                    uploadLocal(xml);
            } else
                return false;
        } catch (Exception ex) {
            Utils.showException(this, ex);
            return false;
        }

        return true;
    }

    //----------------------------------------------------------------------

    /**
     * Edit a node.
     */

    public void editNode() {
        TreePath path = tree.getSelectionPath();
        JBoomNode node = getSelectedNode();

        if (node == null)
            return;

        JBoomNode parent = (JBoomNode) node.getParent();

        if (parent == null)
            parent = jboomRoot;

        // show edit dialog

        BookmarkDialog dialog = new BookmarkDialog(parent, node,
                bundle.getText(node.isLeaf() ? "changeMark" : "changeFolder"), true);

        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            // ok

            int i = parent.getIndex(node);

            if (parent != jboomRoot)
                treeModel.removeNodeFromParent(node);

            if (parent != jboomRoot)
                treeModel.insertNodeInto(node, parent, i);
            else {
                treeModel = new DefaultTreeModel(jboomRoot);
                tree.setModel(treeModel);
            }

            changed = true;
            informListener();
        }

        tree.setSelectionPath(path);
    }

    //----------------------------------------------------------------------

    /**
     * The end.
     * If bookmarks are changed and user confirms, upload them.
     * If options have been changed, save to property file.
     */

    void finish() {
        if ((changed || model.isChanged()) && (!model.isAskSave() ||
                JOptionPane.showConfirmDialog(this, bundle.getText("askUpload"), bundle.getText("confirm"),
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)) {   // save bookmark changes

            Transport connection = model.getActiveTransport();
            String xml = treeToXML();

            if (changed)
                try {
                    if (connection != null)
                        if (model.isRemote())
                            uploadRemote(xml, connection);
                        else if (model.isLocal())
                            uploadLocal(xml);

                    if (model.getLocalPath() != null)
                        uploadLocal(xml);
                } catch (Exception ex) {
                    Utils.showException(this, ex);
                }

            // save config changes

            if (model.isChanged())
                model.saveConfig();
        }

        setRunning(false);
    }

    //----------------------------------------------------------------------

    /**
     * Returns string containing a comma separated list of mail addresses
     * (if path contains only mail) or URLs (if path contains at least one)
     * or null.
     */

    private String getAddressList() {
        TreePath[] selPaths = tree.getSelectionPaths();
        String addressList = null;
        boolean allmail = true;

        for (TreePath path : selPaths) {
            JBoomNode node = (JBoomNode) path.getLastPathComponent();

            if (!node.isMail()) {
                if (allmail) {
                    if (node.isLeaf())
                        addressList = node.getURL();

                    allmail = false;
                }
            } else if (addressList == null)
                addressList = node.getURL();
            else
                addressList += "," + node.getURL();
        }

        return allmail ? "@" + addressList : addressList;
    }

    //----------------------------------------------------------------------

    public String getConfigFile() {
        return configFile;
    }

    //----------------------------------------------------------------------

    /**
     * Return the JBoom model.
     */

    public JBoomModel getModel() {
        return model;
    }

    //----------------------------------------------------------------------

    void setModel(JBoomModel model) {
        this.model = model;
    }

    //----------------------------------------------------------------------

    /**
     * Return the tree's root node.
     */

    JBoomNode getRootNode() {
        return (JBoomNode) tree.getModel().getRoot();
    }

    //----------------------------------------------------------------------

    /**
     * Return the first selected node or null.
     */

    JBoomNode getSelectedNode() {
        TreePath path = tree.getSelectionPath();

        if (path == null)
            return null;

        return (JBoomNode) path.getLastPathComponent();
    }

    //----------------------------------------------------------------------

    /**
     * Return the tree object.
     */

    JTree getTree() {
        return tree;
    }

    //----------------------------------------------------------------------

    /**
     * Activate a URL by calling the URLHandler methode.
     *
     * @param url The URL.
     */

    public void gotoURL(String url) {
        try {
            urlHandler.handleURL(url);
        } catch (Exception ex) {
            Utils.showException(this, ex);
        }
    }

    //----------------------------------------------------------------------

    /**
     * Import mail addresses from csv file
     */

    private void importCSV() {
        JBoomNode node = getSelectedNode();

        if (node == null)
            return;

        // set up file chooser

        JFileChooser chooser = new JFileChooser();

        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;

                String name = f.getName().toUpperCase();

                return name.endsWith(".CSV");
            }

            //----------------------------------------------------------------------

            public String getDescription() {
                return bundle.getText("csvFiles");
            }
        });

        chooser.setDialogTitle(bundle.getText("importCSVTitle"));

        int res = chooser.showOpenDialog(this);

        if (res != JFileChooser.APPROVE_OPTION)
            return;

        // Show dialog. User must denote ad least the address field

        String[] names = new String[]{bundle.getText("mailName"),
                bundle.getText("mailAdr"),
                bundle.getText("mailText")
        };

        CsvDialog dia = new CsvDialog(chooser.getSelectedFile(), names);

        dia.setVisible(true);

        String[][] values = dia.getValues();

        if (values == null) {
            JOptionPane.showMessageDialog(null, bundle.getText("emptyFile"),
                    bundle.getText("warn"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        JBoomNode parent;
        int index;

        if (node.isLeaf()) {
            parent = (JBoomNode) node.getParent();

            if (parent == null)
                parent = jboomRoot;

            index = parent.getIndex(node) + 1;
        } else {
            parent = node;
            index = 0;
        }

        outer:
        for (String[] record : values) {
            JBoomNode newnode = new JBoomNode(record[0] != null && record[0].length() > 0 ? record[0] : record[1],
                    record[1], record[2], false);

            for (int j = 0; j < parent.getChildCount(); ++j) {
                JBoomNode child = (JBoomNode) parent.getChildAt(j);

                if (child.equals(newnode))
                    continue outer;
            }

            newnode.insertInto(treeModel, parent, index++);
        }

        changed = true;
    }

    //----------------------------------------------------------------------

    private void importHTML() {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;

                String name = f.getName().toUpperCase();

                return name.endsWith(".HTML") || name.endsWith(".HTM");
            }

            //----------------------------------------------------------------------

            public String getDescription() {
                return bundle.getText("htmlFiles");
            }
        });

        chooser.setDialogTitle(bundle.getText("importHTMLTitle"));

        int res = chooser.showOpenDialog(this);

        if (res != JFileChooser.APPROVE_OPTION)
            return;

        ArrayList<JBoomNode> nodes = null;

        try {
            nodes = new HTMLImporter(new FileReader(chooser.getSelectedFile())).importHTML();
        } catch (Exception e) {
            Utils.showException(this, e);
            return;
        }

        if (nodes == null)
            JOptionPane.showMessageDialog(this, bundle.getText("nomarks"),
                    bundle.getText("warn"), JOptionPane.WARNING_MESSAGE);
        else
            insertNodeArray(nodes.toArray(new JBoomNode[0]));
    }

    //----------------------------------------------------------------------

    private void informListener() {
        if (listener != null)
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "change"));
    }

    //----------------------------------------------------------------------

    /**
     * Insert nodes recursively into parent node.
     *
     * @param nodes  Array of nodes to be inserted
     * @param parent Parent node
     */

    private void insertNodes(JBoomNode[] nodes, JBoomNode parent, int index) {
        if (nodes == null || nodes.length == 0)
            return;

        outer:
        for (JBoomNode node : nodes) {
            // Check if already in parent

            for (int j = 0; j < parent.getChildCount(); ++j) {
                JBoomNode child = (JBoomNode) parent.getChildAt(j);

                if (child.equals(node)) {
                    // Node present

                    if (child.isLeaf())
                        continue outer;        // Leaf, ignore

                    // Folder, enter and insert children recursively

                    insertNodes(node.getChildren(), child, 0);
                    continue outer;
                }
            }

            node.insertInto(treeModel, parent, index++);
        }

        changed = true;
        informListener();
    }

    //----------------------------------------------------------------------

    /**
     * Insert nodes after (leaf) or into (folder) the first selected node.
     */

    public void insertNodeArray(JBoomNode[] nodes) {
        TreeLocation loc = new TreeLocation(tree);

        if (loc.getParent() == null)
            return;

        insertNodes(nodes, loc.getParent(), loc.getIndex());
        tree.expandPath(tree.getSelectionPath());
        tree.scrollPathToVisible(tree.getSelectionPath());
        changed = true;
        informListener();
    }

    //----------------------------------------------------------------------

    /**
     * Check if we are an application.
     */

    boolean isApplication() {
        return application;
    }

    //----------------------------------------------------------------------

    /**
     * JBoom already running?
     */

    boolean isRunning() {
        return "yes".equals(prefs.get("running", null));
    }

    //----------------------------------------------------------------------

    /**
     * Set the "running" property in prefereneces.
     */

    void setRunning(boolean running) {
        if (running)
            prefs.put("running", "yes");
        else
            prefs.remove("running");

        try {
            prefs.flush();
        } catch (Exception ex) {
            if (isDebug())
                ex.printStackTrace();
        }
    }

    //----------------------------------------------------------------------

    /**
     * Make this folder the root, make old root first subfolder of this one.
     */

    public void makeRoot() {
        JBoomNode last = getSelectedNode(),
                parent = last;

        if (last == null)
            return;

        // if a folder is selected, use it as parent

        if (last.isLeaf()) {
            parent = (JBoomNode) last.getParent();

            if (parent == jboomRoot)
                return;
        }

        if (parent == jboomRoot)
            return;

        parent.detachFrom(treeModel);
        treeModel = new DefaultTreeModel(parent);
        jboomRoot.insertInto(treeModel, parent, 0);
        jboomRoot = parent;
        tree.setModel(treeModel);
        tree.setSelectionRow(0);
        tree.requestFocusInWindow();
        changed = true;
        informListener();
    }

    //----------------------------------------------------------------------

    /**
     * Move the selected nodes (as a compact sequence) 1 position down within the folder.
     * If at the end, move to beginning of folder.
     */

    public void moveNodeDown() {
        TreePath[] paths = Utils.getSelectionPathSequence(tree);

        if (paths == null)
            return;

        JBoomNode node = (JBoomNode) paths[0].getLastPathComponent(),
                prenode = (JBoomNode) node.getPreviousSibling(),
                nextnode,
                parent = (JBoomNode) node.getParent();

        if (parent == null)
            return;

        for (TreePath path : paths)
            ((JBoomNode) path.getLastPathComponent()).detachFrom(treeModel);

        if (prenode != null)
            nextnode = (JBoomNode) prenode.getNextSibling();
        else
            nextnode = (JBoomNode) parent.getFirstChild();

        int index = nextnode != null ? parent.getIndex(nextnode) + 1 : 0;

        for (int i = paths.length - 1; i >= 0; --i)
            ((JBoomNode) paths[i].getLastPathComponent()).insertInto(treeModel, parent, index);

        tree.setSelectionPaths(paths);
        tree.scrollPathToVisible(paths[0]);
        tree.requestFocus();
        changed = true;
        informListener();
    }

    //----------------------------------------------------------------------

    /**
     * Move the selected nodes (as a compact sequence) 1 position up within the
     * parent folder.
     * If at the beginning, move to end of folder.
     */

    public void moveNodeUp() {
        TreePath[] paths = Utils.getSelectionPathSequence(tree);

        if (paths == null)
            return;

        JBoomNode node = (JBoomNode) paths[0].getLastPathComponent(),
                prenode = (JBoomNode) node.getPreviousSibling(),
                parent = (JBoomNode) node.getParent();

        if (parent == null)
            return;

        for (TreePath path : paths)
            ((JBoomNode) path.getLastPathComponent()).detachFrom(treeModel);

        int index = prenode == null ? parent.getChildCount() : parent.getIndex(prenode);

        for (int i = paths.length - 1; i >= 0; --i)
            ((JBoomNode) paths[i].getLastPathComponent()).insertInto(treeModel, parent, index);

        tree.setSelectionPaths(paths);
        tree.scrollPathToVisible(paths[0]);
        tree.requestFocus();
        changed = true;
        informListener();
    }

    //----------------------------------------------------------------------

    /**
     * Create a new bookmark and show dialog.
     */

    public void newBookmark(boolean clip) {
        TreeLocation loc = new TreeLocation(tree);

        // URL is initialized with content of system tray

        String sysclip = null;

        if (clip) {
            try {
                sysclip = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            } catch (Exception e) {
            }
        }

        // create new node

        JBoomNode node = new JBoomNode(null, sysclip, null, model.isExtractName());

        // show edit dialog

        newEntry(loc.getParent(), node, bundle.getText("newMarkName"), loc.getIndex());
    }

    //----------------------------------------------------------------------

    /**
     * Edit newly created entry and insert into tree.
     */

    void newEntry(JBoomNode parent, JBoomNode newnode, String text, int where) {
        BookmarkDialog dialog = new BookmarkDialog(parent, newnode, text, false);

        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            newnode.insertInto(treeModel, parent, where);

            TreePath path = new TreePath(newnode.getPath());

            tree.setSelectionPath(path);
            tree.scrollPathToVisible(path);
            changed = true;
            informListener();
        }
    }

    //----------------------------------------------------------------------

    /**
     * Create a new folder and show dialog.
     */

    void newFolder() {
        TreeLocation loc = new TreeLocation(tree);

        if (loc.getParent() == null)
            return;

        String text = bundle.getText("newFolderName");

        newEntry(loc.getParent(), new JBoomNode(text), text, loc.getIndex());
    }

    //----------------------------------------------------------------------

    /**
     * Show bookmark popup menu.
     *
     * @param e The triggering mouse event.
     */

    private void popup(MouseEvent e) {
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());

        if (path == null)
            return;

        if (!tree.isPathSelected(path))
            tree.setSelectionPath(path);

        boolean notroot = path.getLastPathComponent() != jboomRoot;

        copyItem.setEnabled(notroot);
        treePopupMenu.show(tree, e.getX() + 10, e.getY());
    }

    //----------------------------------------------------------------------

    public void removeActionListener(ActionListener l) {
        listener = AWTEventMulticaster.remove(listener, l);
    }

    //----------------------------------------------------------------------

    /**
     * Select a node.
     *
     * @param node node to be selected.
     */

    void selectNode(JBoomNode node) {
        TreePath path = new TreePath(node.getPath());

        tree.setSelectionPath(path);
        tree.scrollPathToVisible(path);
    }

    //----------------------------------------------------------------------

    /**
     * Set status of buttons according to selection.
     */

    private void setButtonStatusAndText() {
        TreePath[] paths = Utils.getSelectionPathSequence(tree);

        boolean single = paths != null && paths.length == 1,
                brothers = Utilities.treePathsAreBrothers(paths),
                sameParent = Utilities.treePathsSameParent(paths),
                mustRepaint = false;
        String text = null;

        // Handle text area

        if (single)
            text = getSelectedNode().getText();

        // if text area is present, remove it

        if (textScroll != null) {
            remove(textScroll);
            textScroll = null;
            mustRepaint = true;
        }

        if (text != null && text.length() > 0) {
            // create new text area

            JTextArea textArea = new JTextArea(text);

            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setBackground(TEXT_BG);
            textScroll = new JScrollPane(textArea);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.BOTH;
            add(textScroll, gbc);

            Dimension prefSize = textScroll.getPreferredSize();

            if (prefSize.height > MAX_TEXT_HEIGHT)
                prefSize.height = MAX_TEXT_HEIGHT;

            textScroll.setMinimumSize(prefSize);
            mustRepaint = true;
        }

        if (mustRepaint) {
            // text area status has changed

            validate();
            repaint();
        }

        // Set enabled status of buttons and menu items

        editAction.setEnabled(single);
        rootItem.setEnabled(sameParent);
        upButton.setEnabled(brothers);
        downButton.setEnabled(brothers);
        sortButton.setEnabled(sameParent);

        newBookmarkItem.setEnabled(sameParent);
        newMarkButton.setEnabled(sameParent);
        newFolderItem.setEnabled(sameParent);
        newFolderButton.setEnabled(sameParent);
    }

    //----------------------------------------------------------------------

    /**
     * Enable or disable the find button.
     *
     * @param enabled Button status
     */

    void setFindEnabled(boolean enabled) {
        findAction.setEnabled(enabled);
    }

    //----------------------------------------------------------------------

    /**
     * Set the LED icon correponding to the actual mode.
     */

    private void setLed() {
        if (model.isLocal()) {
            ledButton.setIcon(Icons.LED_ORANGE);
            uploadButton.setEnabled(true);
            downloadButton.setEnabled(true);
        } else if (model.isRemote()) {
            ledButton.setIcon(Icons.LED_GREEN);
            uploadButton.setEnabled(true);
            downloadButton.setEnabled(true);
        } else {
            ledButton.setIcon(Icons.LED_RED);
            uploadButton.setEnabled(false);
            downloadButton.setEnabled(false);
        }
    }

    //----------------------------------------------------------------------

    /**
     * Show preferences dialog and set properties.
     */

    public void setOptions() {
        OptionDialog dia = new OptionDialog(this);

        dia.setVisible(true);
        downloadAction.setEnabled(model.getConnections().size() > 0 || model.getLocalPath() != null);
        setLed();
    }

    //----------------------------------------------------------------------

    /**
     * Show the "about" message dialog.
     */

    private void showAbout() {
        JPanel p = new JPanel();
        GridBagConstraints gbca = new GridBagConstraints();

        gbca.gridx = gbca.gridy = 0;
        gbca.gridwidth = gbca.gridheight = 1;
        gbca.anchor = GridBagConstraints.WEST;
        gbca.insets = new Insets(0, 5, 5, 5);

        p.setLayout(new GridBagLayout());

        // Version

        p.add(new JLabel("<html><font color=\"red\">JBoom " + Version.VERSION), gbca);
        ++gbca.gridx;
        p.add(new JLabel(Version.BUILD), gbca);

        // Config file with password replaced by ***

        ++gbca.gridy;
        gbca.gridx = 0;
        p.add(new JLabel(bundle.getText("config")), gbca);
        ++gbca.gridx;

        String confile;

        if (configFile != null) {
            confile = configFile;

            if (confile.startsWith("ftp://") || confile.startsWith("ssh://"))
                confile = confile.replaceAll("^((ftp|ssh)://.*:)(.*)@", "$1***@");
        } else
            confile = "-std-";

        p.add(new JLabel(confile), gbca);

        // Active connection

        ++gbca.gridy;
        gbca.gridx = 0;

        Transport trans = model.getActiveTransport();

        if (trans != null) {
            p.add(new JLabel(bundle.getText("activeLabel")), gbca);
            ++gbca.gridx;
            p.add(new JLabel("<html><font color=\"" + ACTIVE_COLOR + "\">" + trans.getName()), gbca);
        }

        // System

        ++gbca.gridy;
        gbca.gridx = 0;
        p.add(new JLabel("System:"), gbca);
        ++gbca.gridx;
        p.add(new JLabel(System.getProperty("os.name") + " " + System.getProperty("os.arch")
                + " " + System.getProperty("os.version")), gbca);

        // Java version

        ++gbca.gridy;
        gbca.gridx = 0;
        p.add(new JLabel("Java:"), gbca);
        ++gbca.gridx;
        p.add(new JLabel(System.getProperty("java.version")), gbca);

        // Home button

        JButton homeButton = new JButton("http://jboom.sourceforge.net/");

        homeButton.setContentAreaFilled(false);
        homeButton.setMargin(new Insets(0, 0, 0, 0));

        ++gbca.gridy;
        gbca.gridx = 0;
        p.add(new JLabel(bundle.getText("doc")), gbca);
        ++gbca.gridx;
        p.add(homeButton, gbca);

        final JDialog dia = new JOptionPane(p, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION).createDialog(this, bundle.getText("about"));

        homeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gotoURL("http://jboom.sourceforge.net/");
                dia.setVisible(false);
            }
        });

        dia.setVisible(true);
    }

    //----------------------------------------------------------------------

    /**
     * Show find panel.
     */

    public void showFindPanel() {
        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(findPanel, gbc);
        setFindEnabled(false);
        validate();
        repaint();
        findPanel.start();
    }

    //----------------------------------------------------------------------

    /**
     * Sort folder.
     *
     * @param folder Folder to be sorted.
     */

    private void sortFolder(JBoomNode folder) {
        int n = folder.getChildCount();
        TreeSet<JBoomNode> folderSet = new TreeSet<JBoomNode>(),
                leafSet = new TreeSet<JBoomNode>();

        for (int i = 0; i < n; ++i) {
            JBoomNode child = (JBoomNode) folder.getChildAt(0);

            // recursive?

            if (!child.isLeaf() && model.isSortRecursive())
                sortFolder(child);

            // If option folderFirst is set, sort subfolders and leafs separately.

            if (child.isLeaf() || !model.isFolderFirst())
                leafSet.add(child);
            else
                folderSet.add(child);

            child.detachFrom(treeModel);
        }

        int i = 0;

        // subfolders first

        for (JBoomNode node : folderSet)
            treeModel.insertNodeInto(node, folder, i++);

        // leafs

        for (JBoomNode leaf : leafSet)
            treeModel.insertNodeInto(leaf, folder, i++);
    }

    //----------------------------------------------------------------------

    /**
     * Sort selected folder.
     */

    public void sortNode() {
        JBoomNode parent = getSelectedNode();

        if (parent != null) {
            // If leaf selected, sort parent folder

            if (parent.isLeaf())
                parent = (JBoomNode) parent.getParent();
        } else
            parent = jboomRoot;

        sortFolder(parent);
        tree.setSelectionPath(tree.getSelectionPath());
        tree.requestFocus();
        changed = true;
        informListener();
    }

    //----------------------------------------------------------------------

    /**
     * Toggle root visibility.
     */

    public void toggleRootVisible() {
        // collapse all rows

        if (tree.isRootVisible())
            for (int i = tree.getRowCount() - 1; i > 0; i--)
                tree.collapseRow(i);

        tree.setRootVisible(!tree.isRootVisible());
        tree.setSelectionRow(0);
    }

    //----------------------------------------------------------------------

    /**
     * Convert the tree into an XML String.
     */

    private String treeToXML() {
        StringBuilder buf = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");

        JBoomNode node = (JBoomNode) tree.getModel().getRoot();

        node.toBuffer(buf, "");
        return buf.toString();
    }

    //----------------------------------------------------------------------

    /**
     * Upload data.
     */

    public void upload() {
        JPopupMenu uploadPopupMenu = new JPopupMenu();
        JMenuItem item;
        Transport activeConnection = model.getActiveTransport();

        if (activeConnection != null) {
            item = new JMenuItem(model.getLabelText(activeConnection), Icons.SERVER);
            item.setActionCommand(activeConnection.getName());
            item.addActionListener(uploadPopupListener);
            uploadPopupMenu.add(item);
        }

        for (Transport connection : model.getConnections())
            if (connection != activeConnection) {
                item = new JMenuItem(model.getLabelText(connection), Icons.SERVER);
                item.setActionCommand(connection.getName());
                item.addActionListener(uploadPopupListener);
                uploadPopupMenu.add(item);
            }

        if (model.getConnections().size() > 0)
            uploadPopupMenu.add(new JSeparator());

        uploadPopupMenu.add(new ExportAction(Icons.ALL, bundle, "exportMarksMail", true, true));
        uploadPopupMenu.add(new ExportAction(Icons.MARK, bundle, "exportMarks", true, false));
        uploadPopupMenu.add(new ExportAction(Icons.MAIL, bundle, "exportMail", false, true));

        uploadPopupMenu.show(this, uploadButton.getX() + 10, uploadButton.getY());
    }

    //----------------------------------------------------------------------

    /**
     * Upload to remote server.
     *
     * @param xml Bookmarks as XML string.
     */

    void uploadRemote(String xml, Transport transport)
            throws IOException {
        if (!transport.isWriteable())
            return;

        String passwd = null;

        if (transport.needsUploadPasswd()) {
            passwd = transport.getPasswd();

            if (passwd == null)
                passwd = model.getPasswordManager().getPassword(transport.getSpec());

            if (passwd == null)
                return;
        }

        boolean ok = transport.writeFile(xml, passwd);

        if (!ok)
            model.getPasswordManager().remove(transport.getSpec());

        String message = transport.getMessage();

        if (message != null) {
            // An error occured or transport was cancelled

            if (!message.equals(Transport.CANCELLED))
                model.getPasswordManager().remove(transport.getSpec());

            JOptionPane.showMessageDialog(JBoom.this, transport.getMessage(),
                    bundle.getText("error"), JOptionPane.ERROR_MESSAGE);
        }

        // save local copy

        if (model.getLocalPath() != null)
            uploadLocal(xml);
    }

    //----------------------------------------------------------------------

    /**
     * Store to local file.
     *
     * @param xml Bookmarks as XML string.
     */

    private void uploadLocal(String xml)
            throws IOException {
        File file = model.getLocalFile();

        if (file == null)
            return;

        boolean exists = file.exists();
        File parent = file.getParentFile();

        if (exists && !file.canWrite() || !exists && !parent.exists() && !parent.mkdirs()) {
            JOptionPane.showMessageDialog(this, bundle.getText("cantWrite", model.getLocalPath()),
                    bundle.getText("warn"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        FileWriter writer = new FileWriter(file);

        writer.write(xml);
        writer.close();
    }

    //----------------------------------------------------------------------
    // MouseListener methods
    //----------------------------------------------------------------------

    public void mouseClicked(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popup(e);
            return;
        }

        if (e.getClickCount() < 2)
            return;

        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());

        if (selPath == null)
            return;

        JBoomNode node = (JBoomNode) selPath.getLastPathComponent();

        if (node.isLeaf()) {
            if (node.isMail())
                composeMail(node.getURL());
            else
                gotoURL(node.getURL());
        }
    }

    //----------------------------------------------------------------------

    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger())
            popup(e);
    }

    //----------------------------------------------------------------------

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
            popup(e);
    }

    //----------------------------------------------------------------------

    public void mouseEntered(MouseEvent e) {
    }

    //----------------------------------------------------------------------

    public void mouseExited(MouseEvent e) {
    }

    //----------------------------------------------------------------------
    // PopupMenuListener methods
    //
    // Disables tree tooltips while popup is visible
    //----------------------------------------------------------------------

    public void popupMenuWillBecomeInvisible(PopupMenuEvent popupmenuevent) {
        renderer.setTipEnabled(true);
    }

    //----------------------------------------------------------------------

    public void popupMenuCanceled(PopupMenuEvent popupmenuevent) {
        renderer.setTipEnabled(true);
    }

    //----------------------------------------------------------------------
    // TreeSelectionListener method
    //----------------------------------------------------------------------

    public void valueChanged(TreeSelectionEvent e) {
        JBoomNode node = (JBoomNode) tree.getLastSelectedPathComponent();

        if (node == null)
            return;

        setButtonStatusAndText();
    }

    //----------------------------------------------------------------------

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    }

    //======================================================================
    // Connection popup listener class
    //======================================================================

    class ConnectionPopupListener
            implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();

            if (cmd.equals("local")) {
                downloadLocal();
                informListener();
                tree.repaint();
                tree.setSelectionRow(0);
                tree.requestFocusInWindow();
            } else
                download(Integer.parseInt(cmd));
        }
    }

    //======================================================================
    // Key input
    //======================================================================

    class JBoomInputMap
            extends InputMap {
        JBoomInputMap(InputMap parent) {
            put(KeyStroke.getKeyStroke("INSERT"), Keys.PASTE);
            put(KeyStroke.getKeyStroke("DELETE"), Keys.CUT);
            put(KeyStroke.getKeyStroke("ctrl X"), Keys.CUT);
            put(KeyStroke.getKeyStroke("ctrl V"), Keys.PASTE);
            put(KeyStroke.getKeyStroke("ctrl C"), Keys.COPY);
            put(KeyStroke.getKeyStroke("ctrl E"), Keys.EDIT);
            put(KeyStroke.getKeyStroke("ctrl B"), Keys.MARK);
            put(KeyStroke.getKeyStroke("ctrl shift B"), Keys.MARK0);
            put(KeyStroke.getKeyStroke("ctrl F"), Keys.FOLDER);
            put(KeyStroke.getKeyStroke("F3"), Keys.FIND);
            put(KeyStroke.getKeyStroke("ctrl P"), Keys.PREF);
            put(KeyStroke.getKeyStroke("ctrl R"), Keys.SHOWROOT);
            put(KeyStroke.getKeyStroke("alt UP"), Keys.UP);
            put(KeyStroke.getKeyStroke("alt DOWN"), Keys.DOWN);
            put(KeyStroke.getKeyStroke("alt PAGE_UP"), Keys.UPLOAD);
            put(KeyStroke.getKeyStroke("alt PAGE_DOWN"), Keys.DOWNLOAD);
            put(KeyStroke.getKeyStroke("ENTER"), Keys.ENTER);
            setParent(parent);
        }
    }

    //======================================================================

    class JBoomActionMap
            extends ActionMap {
        JBoomActionMap(ActionMap parent) {
            put(Keys.PASTE, pasteAction);
            put(Keys.CUT, cutAction);
            put(Keys.COPY, copyAction);
            put(Keys.EDIT, editAction);
            put(Keys.MARK0, markAction0);
            put(Keys.MARK, markAction1);
            put(Keys.FOLDER, folderAction);
            put(Keys.FIND, findAction);
            put(Keys.PREF, optionsAction);
            put(Keys.SHOWROOT, rootAction);
            put(Keys.UP, upAction);
            put(Keys.DOWN, downAction);
            put(Keys.UPLOAD, uploadAction);
            put(Keys.DOWNLOAD, downloadAction);
            put(Keys.ENTER, enterAction);
            setParent(parent);
        }
    }

    //======================================================================

    /**
     * Create new bookmark
     */

    class MarkAction
            extends UIAction {
        static final int CLIP_YES = 1,           // use system clipboard
                CLIP_NO = -1,           // don't use
                CLIP_SHIFT = 0;         // use if shift not pressed
        int useClip;

        //----------------------------------------------------------------------

        MarkAction(int useClip) {
            super("mark.png", bundle, "newMark", "ctrl B");
            this.useClip = useClip;
        }

        //----------------------------------------------------------------------

        public void actionPerformed(ActionEvent e) {
            newBookmark(useClip == CLIP_YES ||
                    useClip == CLIP_SHIFT && (e.getModifiers() & ActionEvent.SHIFT_MASK) == 0);
        }
    }

    //======================================================================
    // Upload popup listener class
    //======================================================================

    class UploadPopupListener
            implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String name = e.getActionCommand();
            String xml = treeToXML();
            Transport uploadConnection = null;

            for (Transport connection : model.getConnections())
                if (connection.getName().equals(name))
                    uploadConnection = connection;

            try {
                if (model.isRemote())
                    uploadRemote(xml, uploadConnection);
                else if (model.isLocal())
                    uploadLocal(xml);
            } catch (Exception ex) {
                Utils.showException(JBoom.this, ex);
                return;
            }

            // Save configuration

            if (model.isChanged())
                model.saveConfig();

            changed = false;
        }
    }

    //======================================================================
    // Export Action
    //======================================================================

    class ExportAction
            extends UIAction {
        private boolean exportMarks, exportMail;

        //----------------------------------------------------------------------

        ExportAction(ImageIcon icon, KeyBundle bundle, String key,
                     boolean exportMarks, boolean exportMail) {
            super(icon, bundle, key);
            this.exportMarks = exportMarks;
            this.exportMail = exportMail;
        }

        //----------------------------------------------------------------------

        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();

            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory())
                        return true;

                    String name = f.getName().toUpperCase();

                    return name.endsWith(".HTML") || name.endsWith(".HTM");
                }

                //----------------------------------------------------------------------

                public String getDescription() {
                    return bundle.getText("htmlFiles");
                }
            });

            chooser.setDialogTitle(bundle.getText("exportHTMLTitle"));

            int res = chooser.showOpenDialog(JBoom.this);

            if (res != JFileChooser.APPROVE_OPTION)
                return;

            try {
                HTMLExporter exporter = new HTMLExporter(new FileWriter(chooser.getSelectedFile()), jboomRoot,
                        exportMarks, exportMail);

                exporter.export();
            } catch (IOException ex) {
                Utils.showException(JBoom.this, ex);
            }
        }

    }


}

