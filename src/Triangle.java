/*
This class defines a typical triangle.
It will be used during morphing
 */

import java.awt.Point;

public class Triangle {
    Point p1, p2 ,p3;

    public Triangle(Point p1, Point p2, Point p3)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public int getX(int i) {
        switch(i)
        {
            case 0:
                return p1.x;
            case 1:
                return p2.x;
            case 2:
                return p3.x;
        }

        return 0;
    }

    public int getY(int i) {
        switch(i)
        {
            case 0:
                return p1.y;
            case 1:
                return p2.y;
            case 2:
                return p3.y;
        }

        return 0;
    }
}
