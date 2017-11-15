/*
This class defines a point that will be present on the grid panel.
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class GridPoint extends JPanel
{
    // Private variables
    private final int POINTRADIUS = 3;
    private int pointSize, controlX, controlY;
    private boolean moved;

    public GridPoint(int pointSize)
    {
        this.pointSize = pointSize;
        this.controlX = pointSize / 2;
        this.controlY = controlX;
        this.moved = false;
    }

    // Function to repaint this component
    protected void paintComponent(Graphics g)
    {
        //super.paintComponent(g);

        // Draw a rectangle around the boundaries
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, pointSize, pointSize);

        // Draw lines from each opposing corners
        g.drawLine(0, 0, pointSize, pointSize);
        g.drawLine(0, pointSize, pointSize, 0);

        // Draw the control point
        g.drawOval(controlX-POINTRADIUS, controlY-POINTRADIUS, POINTRADIUS*2, POINTRADIUS*2);
        if (moved) g.setColor(Color.GREEN);
        else g.setColor(Color.RED);
        g.fillOval(controlX-POINTRADIUS, controlY-POINTRADIUS, POINTRADIUS*2, POINTRADIUS*2);
    }

    // Get preferred size
    public Dimension getPreferredSize()
    {
        return new Dimension(pointSize, pointSize);
    }
}
