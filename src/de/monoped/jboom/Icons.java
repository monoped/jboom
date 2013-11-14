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

import javax.swing.*;

/**
 * Contains all ImageIcons as constants.
 */

class Icons {
    static final ImageIcon
            ALL = img("all.png"),
            BOOKMARK = img("bookmark.gif"),
            CLOSE = img("close.png"),
            COPY = img("copy.gif"),
            DEL = img("del.gif"),
            DISK = img("disk.png"),
            DOWN = img("down.gif"),
            DOWNLOAD = img("download.gif"),
            EDIT = img("edit.gif"),
            ERROR = img("no.png"),
            FIND = img("find.gif"),
            FOLDER = img("folder.png"),
            FOLDER_OPEN = img("folder-open.png"),
            GO = img("go.png"),
            JBOOM = img("trayicon.png"),
            LED_ORANGE = img("ledorange.png"),
            LED_GREEN = img("ledgreen.png"),
            LED_RED = img("ledred.png"),
            MAIL = img("mail.png"),
            MAILLIST = img("maillist.png"),
            MAILLIST_EXPANDED = img("maillistexp.png"),
            MARK = img("mark.png"),
            NEW = img("new.gif"),
            NEXT = img("next.png"),
            OPEN = img("open.gif"),
            PASTE = img("paste.gif"),
            PREFS = img("preferences.gif"),
            PREV = img("prev.png"),
            ROOT = img("root.gif"),
            ROOTDIR = img("rootdir.gif"),
            SERVER = img("server.png"),
            SORT = img("sort.png"),
            UP = img("up.gif"),
            UPLOAD = img("upload.gif");

    //----------------------------------------------------------------------

    private static ImageIcon img(String name) {
        return new ImageIcon(Icons.class.getClassLoader().getResource(name));
    }
}

