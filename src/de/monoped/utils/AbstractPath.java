package de.monoped.utils;

import java.io.*;
import java.util.*;

public class AbstractPath
{
    private String[]    components;
    private String      root;
    private String      separator;

    //----------------------------------------------------------------------

    protected AbstractPath()
    { }

    //----------------------------------------------------------------------

    /** Erzeuge generischen AbstractPath */

    public AbstractPath(String path)
    {
        separator = "/";
        root = null;
        setComponents(path.split("/"));
    }
    
    //----------------------------------------------------------------------

    /** Erzeuge systemspezifischem AbstractPath */
      
    public AbstractPath(String path, String separator)
    {
        this.separator = separator;

        String splitter = separator.equals("\\") ? "\\\\" : separator;
        
        try
        {
            String      absolut = new File(path).getCanonicalPath();
            File[]      roots = File.listRoots();
            
            for (int i = 0; i < roots. length; ++i)
            {
                String rootPath = roots[i].getAbsolutePath();
                
                if (absolut.startsWith(rootPath))
                {
                    root = rootPath.substring(0, rootPath.length() - 1);
                    absolut = absolut.substring(rootPath.length());
                    break;
                }
            }
            
            setComponents(absolut.split(splitter));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    //----------------------------------------------------------------------

    public AbstractPath(AbstractPath head, Object[] tail, int n)
    {
        String[]    headComps = head.getComponents();
        String[]    comps = new String[headComps.length + n];

        System.arraycopy(headComps, 0, comps, 0, headComps.length);
        System.arraycopy(tail, 0, comps, headComps.length, n);

        components = comps;
        this.separator = head.separator;
        this.root = head.root;
    }

    //----------------------------------------------------------------------

    public AbstractPath(AbstractPath head, Object[] tail)
    {
        this(head, tail, tail.length);
    }
    
    //----------------------------------------------------------------------

    /** Erzeuge AbstractPath aus Root und Komponenten */

    public AbstractPath(String root, Object[] objs, String separator)
    {
        this.root = root;
        this.separator = separator;
        
        components = new String[objs.length];

        for (int i = 0; i < objs.length; ++i)
            components[i] = objs[i].toString();
    }

    //----------------------------------------------------------------------

    public AbstractPath childPath(String name)
    {
        String[] apath = new String[components.length];

        System.arraycopy(components, 0, apath, 0, components.length - 1);
        apath[components.length - 1] = name;
        return new AbstractPath(root, apath, separator);
    }

    //----------------------------------------------------------------------

    private static String createPath(String[] components, String separator)
    {
        String s = "";

        for (int i = 0; i < components.length; ++i)
            s += separator + components[i];

        return s;
    }

    //----------------------------------------------------------------------

    public File getFile()
    {
        return new File(root + createPath(components, separator));
    }
    
//    //----------------------------------------------------------------------
//
//    private void listRec(File file, ArrayList list)
//        throws IOException
//    {
//        list.add(file.getCanonicalPath());
//
//        if (! file.isDirectory())
//            return;
//
//        File[] children = file.listFiles();
//
//        for (int i = 0; i < children.length; ++i)
//            listRec(children[i], list);
//    }
//    
//    //----------------------------------------------------------------------
//
//    /** List files recursively, if directory */
//    
//    public ArrayList listRecursive()
//        throws IOException
//    {
//        ArrayList   list = new ArrayList();
//        
//        listRec(file, list);
//        return list;
//    }
//    
    //----------------------------------------------------------------------

    public String[] getComponents()
    {
        return components;
    }
    
    //----------------------------------------------------------------------

    public String getGenericPath()
    {
        String s = "";

        for (int i = 0; i < components.length; ++i)
            s += "/" + components[i];

        return s;
    }

    //----------------------------------------------------------------------

    public String getSystemPath()
    {
        String s = "";

        for (int i = 0; i < components.length; ++i)
            s += separator + components[i];

        return s;
    }

    //----------------------------------------------------------------------

    public String getCanonicalPath()
        throws IOException
    {
        return getFile().getCanonicalPath();
    }
    
    //----------------------------------------------------------------------

    public String getRoot()
    {
        return root;
    }
    
    //----------------------------------------------------------------------

    public AbstractPath newChildPath(String name)
    {
        String[] apath = new String[components.length + 1];

        System.arraycopy(components, 0, apath, 0, components.length);
        apath[components.length] = name;
        return new AbstractPath(root, apath, separator);
    }

    //----------------------------------------------------------------------

    public AbstractPath parentPath()
    {
        if (components.length == 0)
            return this;

        String[] apath = new String[components.length - 1];

        System.arraycopy(components, 0, apath, 0, components.length - 1);
        return new AbstractPath(root, apath, separator);
    }

    //----------------------------------------------------------------------

    protected void setComponents(String[] components)
    {
        this.components = components;
    }

    //----------------------------------------------------------------------

    public String toString()
    {
        return createPath(components, separator);
    }
}

