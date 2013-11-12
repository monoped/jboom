package de.monoped.utils;

/** Some string formatting methods. */

public class Format
{
    public static final int LEFT = -1, CENTER = 0, RIGHT = 1, INTERNAL = 2;

    //----------------------------------------------------------------------

    /** Convert integer to fixed lenght string.
     *
     *  @param x        Value to convert
     *  @param lng      Field length
     *  @param align    Alignment (LEFT, CENTER or RIGHT)
     *  @param fill     Fill character
     *  @return         x converted to String
     */
    
    static public String format(long x, int lng, int align, char fill)
    {
        return format(String.valueOf(x), lng, align, fill);
    }

    //----------------------------------------------------------------------

    /** Convert integer to string right aligned.
     *
     *  @param x        Value to convert
     *  @param lng      Field length
     *  @param fill     Fill character
     *  @return         x converted to String
     */

    static public String format(long x, int lng, char fill)
    {
        return format(x, lng, RIGHT, fill);
    }
    
    //----------------------------------------------------------------------

    /** Convert integer to string right aligned using blanks.
     *
     *  @param x        Value to convert
     *  @param lng      Field length
     *  @return         x converted to String
     */

    static public String format(long x, int lng)
    {
        return format(x, lng, RIGHT, ' ');
    }

    //----------------------------------------------------------------------

    static public String hexFormat(long x, int lng, int align, char fill)
    {
        String hex = Long.toHexString(x);

        if (align != INTERNAL)
            return format(hex, lng, align, fill);

        StringBuffer    b = new StringBuffer("0x");
        int             l = hex.length(),
                        nfill = lng - 2 - l;

        while (nfill-- > 0)
            b.append(fill);

        b.append(hex);
        return b.toString();
    }
    
    //----------------------------------------------------------------------

    static public String hexFormat(long x, int lng)
    {
        return hexFormat(x, lng, INTERNAL, '0');
    }

    //----------------------------------------------------------------------

    static public String hexFormat(int x, int lng, int align, char fill)
    {
        String hex = Integer.toHexString(x);

        if (align != INTERNAL)
            return format(hex, lng, align, fill);

        StringBuffer    b = new StringBuffer("0x");
        int             l = hex.length(),
                        nfill = lng - 2 - l;

        while (nfill-- > 0)
            b.append(fill);

        b.append(hex);
        return b.toString();
    }
    
    //----------------------------------------------------------------------

    static public String hexFormat(int x, int lng)
    {
        return hexFormat(x, lng, INTERNAL, '0');
    }

    //----------------------------------------------------------------------

    /** Format string.
     *
     *  @param s        String to format
     *  @param lng      Field length
     *  @param align    Alignment (LEFT, CENTER, RIGHT)
     *  @param fill     Fill character
     *  @return         Formatted string
     */

    static public String format(String s, int lng, int align, char fill)
    {
        int l = s.length();

        if (l >= lng)
            return s;

        StringBuffer    b = new StringBuffer(lng);
        int             links;

        if (align == LEFT)
            links = 0;
        else if (align == CENTER)
            links = (lng - l) / 2;
        else 
            links = lng - l;
        
        for (int i = 0; i < links; ++i)
            b.append(fill);

        b.append(s);

        for (int i = links + l; i < lng; ++i)
            b.append(fill);
        
        return b.toString();
    }

    //----------------------------------------------------------------------

    /** Format string using blanks.
     *
     *  @param s        String to format
     *  @param lng      Field length
     *  @param align    Alignment (LEFT, CENTER, RIGHT)
     *  @return         Formatted string
     */

    static public String format(String s, int lng, int align)
    {
        return format(s, lng, align, ' ');
    }

    //----------------------------------------------------------------------

    /** Format string left aligned using blanks.
     *
     *  @param s        String to format
     *  @param lng      Field length
     *  @return         Formatted string
     */

    static public String format(String s, int lng)
    {
        return format(s, lng, LEFT, ' ');
    }
    
}

