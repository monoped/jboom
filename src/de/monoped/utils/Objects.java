package de.monoped.utils;

public class Objects
{
    /** Klassen-Skala */
    
    static final private int
        BYTE = 0, SHORT = 1, INT = 2, LONG = 3, FLOAT = 4, DOUBLE = 5, STRING = 6;

    static final private Class[] CLASSES = 
    {
        Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, String.class
    };

    //----------------------------------------------------------------------

    private Objects()
    { }
    
    //----------------------------------------------------------------------

    /** Addition zweier Objekte */

    static public Object add(Object v1, Object v2)
    {
        switch (highestClassIndex(v1, v2))
        {
            case STRING:
                return v1.toString() + v2.toString();

            case BYTE:
                return new Byte((byte)(((Number)v1).byteValue() + ((Number)v2).byteValue()));

            case SHORT:
                return new Short((short)(((Number)v1).shortValue() + ((Number)v2).shortValue()));

            case INT:
                return new Integer(((Number)v1).intValue() + ((Number)v2).intValue());

            case LONG:
                return new Long(((Number)v1).longValue() + ((Number)v2).longValue());

            case FLOAT:
                return new Float(((Number)v1).floatValue() + ((Number)v2).floatValue());

            case DOUBLE:
                return new Double(((Number)v1).doubleValue() + ((Number)v2).doubleValue());
        }

        return null;
    }

    //----------------------------------------------------------------------

    static public Object cast(String type, Object x)
    {
        try
        {
            if (type.equals("int") || type.equals("java.lang.Integer"))
                return new Integer(((Number)x).intValue());

            if (type.equals("double") || type.equals("java.lang.Double"))
                return new Double(((Number)x).doubleValue());
            
            if (type.equals("float") || type.equals("java.lang.Float"))
                return new Float(((Number)x).floatValue());

            if (type.equals("short") || type.equals("java.lang.Short"))
                return new Short(((Number)x).shortValue());

            if (type.equals("byte") || type.equals("java.lang.Byte"))
                return new Byte(((Number)x).byteValue());

            if (type.equals("long") || type.equals("java.lang.Long"))
                return new Long(((Number)x).longValue());
        }
        catch (ClassCastException ex)
        {
            ex.printStackTrace();
            System.err.println("type=" + type + "  Object: " + x.getClass() + " " + x);
        }
        
        return x;
    }

    //----------------------------------------------------------------------

    /** Return a casted to the type of b. */

    static public Object castTo(Number a, Number b)
    {
        return cast(b.getClass().getName(), a);
    }
    
    //----------------------------------------------------------------------

    /** Gib den Index einer Klasse (byte - double, String). */

    static private int classIndex(Class cl)
    {
        for (int i = 0; i  < CLASSES.length; ++i)
            if (cl == CLASSES[i])
                return i;

        return -1;
    }

    //----------------------------------------------------------------------

    static public Object create(String type, String value)
    {
        Object wert = null;

        if (type.equals("int"))
            wert = new Integer(value);
        else if (type.equals("double"))
            wert = new Double(value);
        else if (type.equals("float"))
            wert = new Float(value);
        else if (type.equals("boolean"))
            wert = new Boolean(value);
        else if (type.equals("short"))
            wert = new Short(value);
        else if (type.equals("byte"))
            wert = new Byte(value);
        else if (type.equals("long"))
            wert = new Long(value);
        else if (type.equals("String"))
            wert = value;
        else
            try
            {
                wert = Class.forName(type).getConstructor((java.lang.Class[])null).newInstance(value);
            }
            catch (Exception ex)
            { }
        
        return wert;
    }

    //----------------------------------------------------------------------

    /** Division zweier Objekte */
    
    static public Object div(Object v1, Object v2)
    {
        switch (highestClassIndex(v1, v2))
        {
            case BYTE:
                return new Byte((byte)(((Number)v1).byteValue() / ((Number)v2).byteValue()));

            case SHORT:
                return new Short((short)(((Number)v1).shortValue() / ((Number)v2).shortValue()));

            case INT:
                return new Integer(((Number)v1).intValue() / ((Number)v2).intValue());

            case LONG:
                return new Long(((Number)v1).longValue() / ((Number)v2).longValue());

            case FLOAT:
                return new Float(((Number)v1).floatValue() / ((Number)v2).floatValue());

            case DOUBLE:
                return new Double(((Number)v1).doubleValue() / ((Number)v2).doubleValue());
        }

        return null;
    }

    //----------------------------------------------------------------------

    /** Hilfsmethode für a == b */
    
    static public boolean equ(Object a, Object b)
    {
        // direkter Vergleich bei gleicher Klasse
           
        if (a.getClass() == b.getClass())
            return a.equals(b);

        // verschiedene (numerische) Klassen

        switch (highestClassIndex(a, b))
        {
            case BYTE:
                return ((Number)a).byteValue() == ((Number)b).byteValue();

            case SHORT:
                return ((Number)a).shortValue() == ((Number)b).shortValue();

            case INT:
                return ((Number)a).intValue() == ((Number)b).intValue();

            case LONG:
                return ((Number)a).longValue() == ((Number)b).longValue();

            case FLOAT:
                return ((Number)a).floatValue() == ((Number)b).floatValue();

            case DOUBLE:
                return ((Number)a).doubleValue() == ((Number)b).doubleValue();
        }
       
        return false;
    }
    
    //----------------------------------------------------------------------

    /** Abfrage a == b */

    static public Boolean equal(Object a, Object b)
    {
        return new Boolean(equ(a, b));
    }

    //----------------------------------------------------------------------

    /** Hilfsmethode für a > b */
    
    static private boolean gt(Object a, Object b)
    {
        switch (highestClassIndex(a, b))
        {
            case BYTE:
                return ((Number)a).byteValue() > ((Number)b).byteValue();

            case SHORT:
                return ((Number)a).shortValue() > ((Number)b).shortValue();

            case INT:
                return ((Number)a).intValue() > ((Number)b).intValue();

            case LONG:
                return ((Number)a).longValue() > ((Number)b).longValue();

            case FLOAT:
                return ((Number)a).floatValue() > ((Number)b).floatValue();

            case DOUBLE:
                return ((Number)a).doubleValue() > ((Number)b).doubleValue();
        }
       
        return false;
    }

    //----------------------------------------------------------------------

    /** Abfrage a > b */

    static public Boolean greater(Object a, Object b)
    {
        return new Boolean(gt(a, b));
    }
    
    //----------------------------------------------------------------------

    /** Abfrage a >= b */

    static public Boolean greaterEqual(Object a, Object b)
    {
        return new Boolean(! lt(a, b));
    }

    //----------------------------------------------------------------------

    /** Compare the classes of two numbers.
     * 
     * @param   a   A number.
     * @param   b   A number.
     * @return  true, if class of a higher than class of b in the sequence
     *          byte - short - int - long.
     */

    static public boolean higherClass(Number a, Number b)
    {
        return classIndex(a.getClass()) > classIndex(b.getClass());
    }
    
    //----------------------------------------------------------------------

    /** Gib den höchsten Klassenindex zweier Operanden. */
    
    static int highestClassIndex(Object n1, Object n2)
    {
        int     i1 = classIndex(n1.getClass()),
                i2 = classIndex(n2.getClass());

        return i1 > i2 ? i1 : i2;
    }
    
    //----------------------------------------------------------------------

    /** Linksshift */

    static public Object lshift(Object v1, Object v2)
    {
        int n = ((Integer)v2).intValue();

        switch (classIndex(v1.getClass()))
        {
            case BYTE:
                return new Byte((byte)(((Number)v1).byteValue() << n));

            case SHORT:
                return new Short((short)(((Number)v1).shortValue() << n));

            case INT:
                return new Integer(((Number)v1).intValue() << n);

            case LONG:
                return new Long(((Number)v1).longValue() << n);
        }

        return null;
    }

    //----------------------------------------------------------------------

    /** Hilfsmethode für a < b */
    
    static private boolean lt(Object a, Object b)
    {
        switch (highestClassIndex(a, b))
        {
            case BYTE:
                return ((Number)a).byteValue() < ((Number)b).byteValue();

            case SHORT:
                return ((Number)a).shortValue() < ((Number)b).shortValue();

            case INT:
                return ((Number)a).intValue() < ((Number)b).intValue();

            case LONG:
                return ((Number)a).longValue() < ((Number)b).longValue();

            case FLOAT:
                return ((Number)a).floatValue() < ((Number)b).floatValue();

            case DOUBLE:
                return ((Number)a).doubleValue() < ((Number)b).doubleValue();
        }
       
        return false;
    }

    //----------------------------------------------------------------------

    /** Abfrage a < b */

    static public Boolean less(Object a, Object b)
    {
        return new Boolean(lt(a, b));
    }
    
    //----------------------------------------------------------------------

    /** Abfrage a <= b */

    static public Boolean lessEqual(Object a, Object b)
    {
        return new Boolean(! gt(a, b));
    }
    
    //----------------------------------------------------------------------

    /** Modulo-Bildung */
    
    static public Object mod(Object v1, Object v2)
    {
        switch (highestClassIndex(v1, v2))
        {
            case BYTE:
                return new Byte((byte)(((Number)v1).byteValue() % ((Number)v2).byteValue()));

            case SHORT:
                return new Short((short)(((Number)v1).shortValue() % ((Number)v2).shortValue()));

            case INT:
                return new Integer(((Number)v1).intValue() % ((Number)v2).intValue());

            case LONG:
                return new Long(((Number)v1).longValue() % ((Number)v2).longValue());

            case FLOAT:
                return new Float(((Number)v1).floatValue() % ((Number)v2).floatValue());

            case DOUBLE:
                return new Double(((Number)v1).doubleValue() % ((Number)v2).doubleValue());
        }

        return null;
    }

    //----------------------------------------------------------------------

    /** Multiplikation zweier Objekte */
    
    static public Object mul(Object v1, Object v2)
    {
        switch (highestClassIndex(v1, v2))
        {
            case BYTE:
                return new Byte((byte)(((Number)v1).byteValue() * ((Number)v2).byteValue()));

            case SHORT:
                return new Short((short)(((Number)v1).shortValue() * ((Number)v2).shortValue()));

            case INT:
                return new Integer(((Number)v1).intValue() * ((Number)v2).intValue());

            case LONG:
                return new Long(((Number)v1).longValue() * ((Number)v2).longValue());

            case FLOAT:
                return new Float(((Number)v1).floatValue() * ((Number)v2).floatValue());

            case DOUBLE:
                return new Double(((Number)v1).doubleValue() * ((Number)v2).doubleValue());
        }

        return null;
    }

    //----------------------------------------------------------------------

    /** Abfrage a != b */

    static public Boolean notEqual(Object a, Object b)
    {
        return new Boolean(! equ(a, b));
    }

    //----------------------------------------------------------------------

    /** a >> n */

    static public Object rshift(Object v1, Object v2)
    {
        int n = ((Integer)v2).intValue();

        switch (classIndex(v1.getClass()))
        {
            case BYTE:
                return new Byte((byte)(((Number)v1).byteValue() >> n));

            case SHORT:
                return new Short((short)(((Number)v1).shortValue() >> n));

            case INT:
                return new Integer(((Number)v1).intValue() >> n);

            case LONG:
                return new Long(((Number)v1).longValue() >> n);
        }

        return null;
    }

    //----------------------------------------------------------------------

    /** a >>> n */

    static public Object rshift0(Object v1, Object v2)
    {
        int n = ((Integer)v2).intValue();

        switch (classIndex(v1.getClass()))
        {
            case BYTE:
                return new Byte((byte)(((Number)v1).byteValue() >>> n));

            case SHORT:
                return new Short((short)(((Number)v1).shortValue() >>> n));

            case INT:
                return new Integer(((Number)v1).intValue() >>> n);

            case LONG:
                return new Long(((Number)v1).longValue() >>> n);
        }

        return null;
    }

    //----------------------------------------------------------------------

    /** Subtraktion zweier Objekte */
    
    static public Object sub(Object v1, Object v2)
    {
        switch (highestClassIndex(v1, v2))
        {
            case BYTE:
                return new Byte((byte)(((Number)v1).byteValue() - ((Number)v2).byteValue()));

            case SHORT:
                return new Short((short)(((Number)v1).shortValue() - ((Number)v2).shortValue()));

            case INT:
                return new Integer(((Number)v1).intValue() - ((Number)v2).intValue());

            case LONG:
                return new Long(((Number)v1).longValue() - ((Number)v2).longValue());

            case FLOAT:
                return new Float(((Number)v1).floatValue() - ((Number)v2).floatValue());

            case DOUBLE:
                return new Double(((Number)v1).doubleValue() - ((Number)v2).doubleValue());
        }

        return null;
    }

    //----------------------------------------------------------------------

    public static String toString(Number x)
    {
        String s = x.toString();

        if (x instanceof Long)
            s += 'L';
        else if (x instanceof Float && ! ((Float)x).isInfinite() && ! ((Float)x).isNaN())
            s +='F';

        return s;
    }
    
}
