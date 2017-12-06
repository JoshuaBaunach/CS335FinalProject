/*
This class defines a point that will be present on the grid panel.
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class GridPoint extends JPanel
{
    // Private variables
    private final int POINTRADIUS = 2;
    private int pointWidth, pointHeight, controlX, controlY;
    private boolean moved, rubberbanding, partnerMoved;
    private GridPoint partnerPoint;

    public GridPoint(int pointWidth, int pointHeight)
    {
        this.pointWidth = pointWidth;
        this.pointHeight = pointHeight;
        this.controlX = pointWidth / 2;
        this.controlY = pointHeight / 2;
        this.moved = false;
        this.rubberbanding = false;

        addMouseListener(new RubberbandListener());
        addMouseMotionListener(new RubberbandMotionListener());

        setOpaque(false);
    }

    // Copy constructor
    public GridPoint(GridPoint point)
    {
        this.pointWidth = point.pointWidth;
        this.pointHeight = point.pointHeight;
        this.controlX = point.controlX;
        this.controlY = point.controlY;
        this.moved = point.moved;
        this.rubberbanding = false;

        addMouseListener(new RubberbandListener());
        addMouseMotionListener(new RubberbandMotionListener());

        setOpaque(false);
    }

    // Getters/setters
    public void setMoved(boolean moved) { this.moved = moved; }
    public void setPartnerPoint(GridPoint partner) { partnerPoint = partner; }
    public void setPartnerMoved(boolean moved) { partnerMoved = moved; }
    public void setControlPoint(Point p) { controlX = p.x; controlY = p.y; }

    public Point getControlPoint() { return new Point(controlX, controlY); }
    public boolean getMoved() { return moved; }

    // Function to repaint this component
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.SrcOver.derive(1f));

        // Draw a rectangle around the boundaries
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, pointWidth, pointHeight);

        // Draw lines from each opposing to the point
        g.drawLine(0, 0, controlX - POINTRADIUS, controlY - POINTRADIUS);
        g.drawLine(0, pointHeight, controlX - POINTRADIUS, controlY + POINTRADIUS);
        g.drawLine(pointWidth, 0, controlX + POINTRADIUS, controlY - POINTRADIUS);
        g.drawLine(pointWidth, pointHeight, controlX + POINTRADIUS, controlY + POINTRADIUS);

        // Draw the control point
        g.drawOval(controlX-POINTRADIUS, controlY-POINTRADIUS, POINTRADIUS*2, POINTRADIUS*2);
        if (moved || partnerMoved) g.setColor(Color.GREEN);
        else g.setColor(Color.RED);
        g.fillOval(controlX-POINTRADIUS, controlY-POINTRADIUS, POINTRADIUS*2, POINTRADIUS*2);
    }

    // Get preferred size
    public Dimension getPreferredSize()
    {
        return new Dimension(pointWidth, pointHeight);
    }

    // This private class handles the motion for the rubberbanding.
    private class RubberbandMotionListener extends MouseMotionAdapter
    {
        public void mouseDragged(MouseEvent e)
        {
            if (rubberbanding)
            {
                // If mouse x is out of bounds, set the x pos to the bound
                if (e.getX() > pointWidth-POINTRADIUS)
                    controlX = pointWidth-POINTRADIUS;
                else if (e.getX() < POINTRADIUS)
                    controlX = POINTRADIUS;
                else
                    controlX = e.getX();

                // If mouse y is out of bounds, set the y pos to the bound
                if (e.getY() > pointHeight-POINTRADIUS)
                    controlY = pointHeight-POINTRADIUS;
                else if (e.getY() < POINTRADIUS)
                    controlY = POINTRADIUS;
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
            // Determine if the mouse is within the control point box
            // Old calculations:
            //double mouseDist = Math.sqrt(Math.pow(e.getX() - controlX, 2) + Math.pow(e.getY() - controlY, 2));
            //if (mouseDist <= (float)POINTRADIUS)

            if ((e.getX() >= 0 && e.getX() <= pointWidth) && (e.getY() >= 0 && e.getY() <= pointHeight))
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
