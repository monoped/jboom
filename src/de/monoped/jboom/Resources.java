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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * English texts.
 */

public class Resources
        extends KeyBundle {
    public Resources() {
        p("about", "About jBoom", 0, "About jBoom");
        p("activ", "Active", KeyEvent.VK_A, "Active connection (on/off)");
        p("activeLabel", "Active:");
        p("askCreateProp", "Create file %s?");
        p("askrunning", "Another instance of jBoom appears to be running.\nContinue?");
        p("askSave", "Ask before saving");
        p("askUpload", "Save changes?");
        p("authfail", "Authentication failed");
        p("browse", "Browse", KeyEvent.VK_B);
        p("cancel", "Cancel", KeyEvent.VK_C, "Discard changes and close window");
        p("cantWrite", "Can't write to file %s");
        p("case", "Case", 0, "Case sensitive search");
        p("changeFolder", "Change folder");
        p("changeMark", "Change bookmark");
        p("close", null, 0, "Close window" + keyText(KeyEvent.VK_ESCAPE));
        p("config", "Configuration:");
        p("configDown", "Downloading configuration");
        p("configLabel", "Configuration");
        p("configUp", "Uploading configuration");
        p("confirm", "Please confirm");
        p("connections", "Connections:");
        p("copy", "Copy");
        p("createFolder", "Create new folder ");
        p("createMail", "Create new address");
        p("createMark", "Create new bookmark ");
        p("csvFiles", "CSV files");
        p("cut", "Cut");
        p("delConn", null, 0, "Delete connection"
                + keyText(KeyEvent.VK_DELETE));
        p("doc", "Documentation:");
        p("down", null, 0, "Move selected entries down"
                + keyText(KeyEvent.VK_DOWN, InputEvent.ALT_MASK));
        p("download", null, 0, "Download"
                + keyText(KeyEvent.VK_PAGE_DOWN, InputEvent.ALT_MASK));
        p("downloading", "Downloading from %s...");
        p("edit", "Edit");
        p("emptyFile", "No data");
        p("error", "Error");
        p("exit", "Exit program");
        p("exitTray", "Remove from tray");
        p("expected", "'%s1' expected (line %s2)");
        p("exportMarks", "export to HTML (bookmarks only)");
        p("exportMail", "export to HTML (mail only)");
        p("exportMarksMail", "export to HTML (everything)");
        p("exportHTMLTitle", "Export bookmarks to HTML file");
        p("extract", "Name from title", KeyEvent.VK_T, "Use page title as bookmark name");
        p("fileNotExist", "File '%s' does not exist");
        p("find", "Find:", 0, "Find"
                + keyText(KeyEvent.VK_F3));
        p("findNext", null, 0, "Find next"
                + keyText(KeyEvent.VK_F3));
        p("findPrev", null, 0, "Find previous"
                + keyText(KeyEvent.VK_F3, InputEvent.SHIFT_MASK));
        p("folderFirst", "Folder first", KeyEvent.VK_F, "Arrange folders before bookmarks (on/off)");
        p("folderLabel", "Folder:");
        p("go", "Goto this page");
        p("host", "Host:", KeyEvent.VK_H);
        p("htmlFiles", "HTML files");
        p("importCSV", "import mail addr.");
        p("importCSVTitle", "Import mail addresses from CSV file");
        p("importHTML", "import bookmarks");
        p("importHTMLTitle", "Import bookmarks from HTML file");
        p("incomplete", "Incomplete options for ");
        p("initwarn", "Discard changes and initialize tree?");
        p("insert", "Paste");
        p("isrunning", "Another jBoom appears to be active!");
        p("jboomhome", "JBoom project page");
        p("keyFile", "Private key file:", KeyEvent.VK_K);
        p("label-mail", "Mail Address:", KeyEvent.VK_M);
        p("label-url", "URL:", KeyEvent.VK_U);
        p("loading", "Loading");
        p("local", "local");
        p("localFile", "Local File:", KeyEvent.VK_L);
        p("mailcommand", "Mailer:", KeyEvent.VK_M);
        p("maillist", "Mailing list");
        p("mailAdr", "Address");
        p("mailName", "Name");
        p("mailText", "Text");
        p("mailTableExpl", "<html><font color='green'>Right-click on a header to associate a column with a mail field. Columns labeled '-' are ignored.");
        p("myMarks", "My bookmarks");
        p("name", "Name:", KeyEvent.VK_N);
        p("names", "Names", 0, "Search bookmark and folder names");
        p("nameExists", "Name '%s' already exists");
        p("newConn", "New connection", 0, "Create new connection"
                + keyText(KeyEvent.VK_INSERT));
        p("newFolder", "New folder", 0, "New folder"
                + keyText(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        p("newFolderName", "New folder");
        p("newMail", "New mail address");
        p("newMark", "New bookmark", 0, "New bookmark"
                + keyText(KeyEvent.VK_B, InputEvent.CTRL_MASK));
        p("newMarkName", "New bookmark");
        p("nomarks", "<html>No bookmarks found.<p>Not a Firefox bookmark file?");
        p("noreponse", "No response from ftp server");
        p("normalSize", "Normal size");
        p("ok", "OK", KeyEvent.VK_O);
        p("opterr", "Illegal option '%s' ignored");
        p("passwd", "Password:", KeyEvent.VK_P);
        p("passwdprompt", "Password for ");
        p("passwdTitle", "Password needed");
        p("position", "Remember position and size", KeyEvent.VK_P, "Restore position and size at the next start");
        p("prefs", null, 0, "Preferences dialog"
                + keyText(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        p("propsError", "Error writing option file");
        p("recursive", "Recursive", KeyEvent.VK_V, "Sort subfolders recursively (on/off)");
        p("recvUrl", "Download URL:", KeyEvent.VK_D);
        p("remFile", "Remote file:", KeyEvent.VK_R);
        p("retrFail", "Could not retrieve file %s");
        p("root", null, 0, "Display root on/off"
                + keyText(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        p("rootDir", "Root dir", KeyEvent.VK_R, "Make this the root folder");
        p("search", "Search bookmarks"
                + keyText(KeyEvent.VK_F3));
        p("sendUrl", "Upload URL:", KeyEvent.VK_U);
        p("separator", "Separator: ");
        p("skip1st", "Skip first line");
        p("sort", null, 0, "Sort folder");
        p("sortLabel", "Sort");
        p("srvClosed", "Server closed connection");
        p("startmin", "Start minimized", KeyEvent.VK_I,
                "Start iconified (and put symbol into system tray if option to the right is selected).");
        p("storeFail", "Could not store file %s");
        p("tagIncompl", "Tag incomplete (line %s)");
        p("text", "Text:", KeyEvent.VK_T);
        p("textIncompl", "Text incomplete (line %s)");
        p("totray", "Minimize to tray", KeyEvent.VK_Y, "When started or minimized, put into system tray.");
        p("up", null, 0, "Move selected entries up"
                + keyText(KeyEvent.VK_UP, InputEvent.ALT_MASK));
        p("upload", null, 0, "Upload"
                + keyText(KeyEvent.VK_PAGE_UP, InputEvent.ALT_MASK));
        p("uploading", "Uploading to %s...");
        p("urlcommand", "Browser:", KeyEvent.VK_W);
        p("urlerr", "Malformed URL: %s");
        p("urls", "URLs", 0, "Search URLs");
        p("useLocal", "<html>Error reading file <font color=\"red\">%s</font>.<P>Do you want to use a local bookmarks file?");
        p("usingLocal", "Error downloading from remote, using local bookmarks file");
        p("user", "User:", KeyEvent.VK_U);
        p("wait", "Please wait");
        p("warn", "Warning");
    }
}

