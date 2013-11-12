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

package de.monoped.efile;

import java.io.*;
import java.util.*;

/** Extended file interface (actually a very simple virtual file system). 
 *  An EFile has a base (e.g., "ftp://user@host") and a path. Only /
 *  is used as internal file separator.
 */

public interface EFile
{
    /** Add a component to the file path. 
     *  @param comp Component to add
     */

    void            addPath(String comp);

    /** Close the file. */

    void            close();

    /** Copy an input stream into the file.
     *
     *  @param  in  Stream is read and copied into the file.
     */

    void            copyFrom(InputStream in);

    /** Copy a normal file into the file.
     *
     *  @param  src  File to be copied.
     */

    void            copyFrom(File src);

    /** Delete the file. If the file is a directory, it must be empty. */

    void            delete();

    /** Delete the file. If it is a directory and the parameter is true, 
     *  delete the directory recursively.
     *
     * @param recursive     If true, delete a directory recursively.
     */

    void            delete(boolean recursive);

    /** Check if file exists. */

    boolean         exists();

    /** Return the file base. */

    String          getBase();

    /** Return file content as array of bytes. */

    byte[]          getBytes();

    /** Return the name (the last path component). */

    String          getName();

    /** Open an input stream that reads from the file and return it. */

    InputStream     getInputStream();

    /** Open an output stream that writes the file and return it. */

    OutputStream    getOutputStream();

    /** Get parent path. */

    String          getParent();

    /** Get absolute file path. */

    String          getPath();

    /** Check if file is a directory. */

    boolean         isDirectory();

    /** Return contents of a directory as array of file names. */

    String[]        list();

    /** Return filtered contents of a directory as array of file names. 
     *
     *  @param filter   File filter
     */

    String[]        list(FilenameFilter filter);

    /** Return an iterator for file names in this directory. */

    Iterator        iterator();
    Iterator        iterator(FilenameFilter filter);
    boolean         mkdirs();

    void            putBytes(byte[] bytes);
    void            setPath(String path);
    void            setName(String name);
    String          toString();
}

