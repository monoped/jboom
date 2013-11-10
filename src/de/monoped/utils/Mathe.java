package de.monoped.utils;

public class Mathe
{
    public static Circle circle3(double x1, double y1, double x2, double y2, double x3, double y3)
    {
       Circle   c = new Circle();
       double   x12 = x2 - x1, y12 = y2 - y1,
                x13 = x3 - x1, y13 = y3 - y1,
                x13y13 = x13 * x13 + y13 * y13,
                x12y12 = x12 * x12 + y12 * y12,
                x13y12 = x13 * y12 - x12 * y13,
                xm1 = 0.5 * (x12y12 * y13 - x13y13 * y12) / x13y12,
                ym1 = - 0.5 * (x12y12 * x13 - x13y13 * x12) / x13y12;

       c.x = x1 - xm1;
       c.y = y1 - ym1;
       c.r = Math.sqrt(xm1 * xm1 + ym1 * ym1);
       return c;
    }

    //----------------------------------------------------------------------

    static public class Circle
    {
        public double x, y, r;
    }
}

