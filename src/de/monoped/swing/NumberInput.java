package de.monoped.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NumberInput
    extends JTextField
{
    static public final int         EMPTY = 0, CHAR = 1, INT = 2, LONG = 3, 
                                    FLOAT = 4, DOUBLE = 5;
    static public final int         DECIMAL = 0, OCTAL = 1, HEXADECIMAL = 2, CHARACTER = 3;
    
    private int             art; 
    private Number          number;

    //----------------------------------------------------------------------

    public NumberInput(int lng)
    {
        super(lng);
    }

    //----------------------------------------------------------------------

    public int getBase()
    {
        return art;
    }

    //----------------------------------------------------------------------

    public Number getNumber()
    {
        return number;
    }

    //----------------------------------------------------------------------

    private int parseChar(String txt)
        throws NumberInputException
    {
        int     l = txt.length(), 
                intval = 0;
        char    last = txt.charAt(l - 1);
        String  zuviel = "zu viele Zeichen";

        try
        {
            // letztes Zeichen muss ' sein
               
            if (last != '\'')
                throw new NumberInputException("Abschließendes ' fehlt");

            // dazwischen mindestens 1 Zeichen
               
            if (l < 3)
                throw new NumberInputException("Zeichen fehlt");

            if (txt.charAt(1) != '\\')
            {
                /* normales Zeichen, keine Escape-Folge.
                 * Länge muss genau 3 sein.
                 */
                   
                if (l > 3)
                    throw new NumberInputException(zuviel);
                
                intval = txt.charAt(1);
            }
            else
            {
                // Escape-Folge. Länge muss mindestens 4 sein
                   
                if (l < 4)
                    throw new NumberInputException("Zeichen hinter \\ fehlt");
                
                // Verzweigen über 2. Zeichen
                   
                switch (txt.charAt(2))
                {
                    case 'b':   
                    {
                        if (l > 4)
                            throw new NumberInputException(zuviel);
                        
                        intval = '\b'; 
                        break;
                    }
                    
                    case 't':   
                    {
                        if (l > 4)
                            throw new NumberInputException(zuviel);
                        
                        intval = '\t'; 
                        break;
                    }
                    
                    case 'n':   
                    {
                        if (l > 4)
                            throw new NumberInputException(zuviel);

                        intval = '\n'; 
                        break;
                    }
                    
                    case 'r':   
                    {
                        if (l > 4)
                            throw new NumberInputException(zuviel);
                        
                        intval = '\r'; 
                        break;
                    }
                    
                    case '"':   
                    {
                        if (l > 4)
                            throw new NumberInputException(zuviel);
                        
                        intval = '\"'; 
                        break;
                    }
                    
                    
                    case '\'':  
                    {
                        if (l > 4)
                            throw new NumberInputException(zuviel);
                        
                        intval = '\''; 
                        break;
                    }
                    
                    case '\\':  
                    {
                        if (l > 4)
                            throw new NumberInputException(zuviel);
                        
                        intval = '\\'; 
                        break;
                    }
                    
                    case 'u':               // Unicode
                    {
                        if (l != 8)
                            throw new NumberInputException("Falsche Anzahl Hex-Ziffern");
                        
                        intval = Integer.valueOf(txt.substring(3, 7), 16).intValue();
                        break;
                    }
                              
                    default:                // falsches Escapezeichen oder oktal
                    {
                        if (l == 4)
                            throw new NumberInputException("Unzulässiges Zeichen nach  \\");

                        if (l != 6)
                            throw new NumberInputException("Falsche Anzahl Oktalziffern");
                        
                        intval = Integer.valueOf(txt.substring(2, 5), 8).intValue();
                        break;
                    }
                }
            }

            number = new Integer(intval);
        }
        catch (NumberFormatException e)
        {
            // Parse-Fehler

            throw new NumberInputException("Falsche Oktal- oder Hex-Zahl");
        }

        return INT;
    }
    
    //----------------------------------------------------------------------

    public int parseInput()
        throws NumberInputException
    {
        String  txt = getText().trim();

        number = null;

        if (txt.length() == 0)
        {
            // Leereingabe

            return EMPTY;
        }
        
        if (txt.charAt(0) == '\'')
        {
            // Zeichen
            
            art = CHARACTER;
            return parseChar(txt);
        }
        
        // Zahl

        int     l = txt.length(),
                vorz = 1; 
        char    last = txt.charAt(l - 1);
        String  txt1 = txt;

        try
        {
            if (txt.startsWith("-"))
            {
                txt1 = txt.substring(1);
                vorz = -1;
            }

            if (txt1.startsWith("0x") || txt1.startsWith("0X"))
            {
                art = HEXADECIMAL;

                if (last == 'l' || last == 'L')
                {
                    number = new Long(vorz * Long.parseLong(txt1.substring(2, txt1.length() - 1), 16));
                    return LONG;
                }
                else
                {
                    number = new Integer(vorz * Integer.parseInt(txt1.substring(2), 16));
                    return INT;
                }
            }
            
            if (last == 'f' || last == 'F')
            {
                number = Float.valueOf(txt);
                return FLOAT;
            }
            
            if (last == 'd' || last == 'D' || txt.indexOf('.') >= 0 
                    || txt.indexOf('e') >= 0 || txt.indexOf('E') >= 0)
            {
                number = Double.valueOf(txt);
                return DOUBLE;
            }
            
            if (txt1.charAt(0) == '0' && txt1.length() > 1 && Character.isDigit(txt1.charAt(1)))
            {
                art = OCTAL;

                if (last == 'l' || last == 'L')
                {
                    number = Long.valueOf(txt.substring(1, txt.length() - 1), 8);
                    return LONG;
                }
                else
                {
                    number = Integer.valueOf(txt, 8);
                    return INT;
                }
            }
            
            art = DECIMAL;

            if (last == 'l' || last == 'L')
            {
                number = Long.valueOf(txt.substring(0, txt.length() - 1));
                return LONG;
            }
            
            number = Integer.valueOf(txt);
            return INT;
        }
        catch (NumberFormatException ex)
        {
            throw new NumberInputException("Falsche Zahldarstellung");
        }
    }
}

