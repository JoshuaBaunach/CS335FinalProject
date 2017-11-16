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
    private boolean moved, rubberbanding, partnerMoved;
    private GridPoint partnerPoint;

    public GridPoint(int pointSize)
    {
        this.pointSize = pointSize;
        this.controlX = pointSize / 2;
        this.controlY = controlX;
        this.moved = false;
        this.rubberbanding = false;

        addMouseListener(new RubberbandListener());
        addMouseMotionListener(new RubberbandMotionListener());

        setOpaque(false);
    }

    // Getters/setters
    public void setPartnerPoint(GridPoint partner) { partnerPoint = partner; }
    public void setPartnerMoved(boolean moved) { partnerMoved = true; }
    public void setControlPoint(Point p) { controlX = p.x; controlY = p.y; }

    public Point getControlPoint() { return new Point(controlX, controlY); }

    // Function to repaint this component
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // Draw a rectangle around the boundaries
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, pointSize, pointSize);

        // Draw lines from each opposing to the point
        g.drawLine(0, 0, controlX - POINTRADIUS, controlY - POINTRADIUS);
        g.drawLine(0, pointSize, controlX - POINTRADIUS, controlY + POINTRADIUS);
        g.drawLine(pointSize, 0, controlX + POINTRADIUS, controlY - POINTRADIUS);
        g.drawLine(pointSize, pointSize, controlX + POINTRADIUS, controlY + POINTRADIUS);

        // Draw the control point
        g.drawOval(controlX-POINTRADIUS, controlY-POINTRADIUS, POINTRADIUS*2, POINTRADIUS*2);
        if (moved || partnerMoved) g.setColor(Color.GREEN);
        else g.setColor(Color.RED);
        g.fillOval(controlX-POINTRADIUS, controlY-POINTRADIUS, POINTRADIUS*2, POINTRADIUS*2);
    }

    // Get preferred size
    public Dimension getPreferredSize()
    {
        return new Dimension(pointSize, pointSize);
    }

    // This private class handles the motion for the rubberbanding.
    private class RubberbandMotionListener extends MouseMotionAdapter
    {
        public void mouseDragged(MouseEvent e)
        {
            if (rubberbanding)
            {
                // If mouse x is out of bounds, set the x pos to the bound
                if (e.getX() > pointSize)
                    controlX = pointSize;
                else if (e.getX() < 0)
                    controlX = 0;
                else
                    controlX = e.getX();

                // If mouse y is out of bounds, set the y pos to the bound
                if (e.getY() > pointSize)
                    controlY = pointSize;
                else if (e.getY() < 0)
                    controlY = 0;
                else
                    controlY = e.getY();

                repaint();
                partnerPoint.repaint();
            }
        }
    }
    // This private class handles the rubberbanding.
    private class RubberbandListener extends MouseAdapter
    {
        public void mousePressed(MouseEvent e)
        {
            // Determine if the mouse is within the control point
            double mouseDist = Math.sqrt(Math.pow(e.getX() - controlX, 2) + Math.pow(e.getY() - controlY, 2));
            if (mouseDist <= (float)POINTRADIUS)
            {
                rubberbanding = true;
                moved = true;
                partnerPoint.setPartnerMoved(true);
            }
        }

        public void mouseReleased(MouseEvent e)
        {
            rubberbanding = false;
        }
    }
}
