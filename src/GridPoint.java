/*
This class defines a point that will be present on the grid panel.
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class GridPoint extends JPanel
{
    // Public Variables
    public static final int POINTRADIUS = 3;

    // Private variables
    private int pointWidth, pointHeight, controlX, controlY, locX, locY;
    private boolean moved, rubberbanding, partnerMoved, pointUpdated, highlighted, editable;
    private GridPoint partnerPoint; // The point that corresponds to this point on the other panel
    private GridPoint northNeighbor, eastNeighbor, southNeighbor, westNeighbor; // Points that help define the bounding box of this point
    private FullGridPanel parentPanel;

    public GridPoint(int pointWidth, int pointHeight)
    {
        this.pointWidth = pointWidth;
        this.pointHeight = pointHeight;
        this.controlX = pointWidth / 2;
        this.controlY = pointHeight / 2;
        this.moved = false;
        this.partnerMoved = false;
        this.rubberbanding = false;
        this.pointUpdated = true;
        this.highlighted = false;
        editable = true;
        this.locX = 0;
        this.locY = 0;

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
        this.partnerMoved = point.partnerMoved;
        this.rubberbanding = false;
        this.pointUpdated = true;
        this.highlighted = false;
        this.editable = point.editable;
        this.locX = point.locX;
        this.locY = point.locY;
        setLocation(locX, locY);

        addMouseListener(new RubberbandListener());
        addMouseMotionListener(new RubberbandMotionListener());

        setOpaque(false);
    }

    // Getters/setters
    public void setMoved(boolean moved) { this.moved = moved; }
    public void setPartnerPoint(GridPoint partner) { partnerPoint = partner; }
    public void setPartnerMoved(boolean moved) { partnerMoved = moved; }
    public void setControlPoint(Point p) { controlX = p.x; controlY = p.y; }
    public void setPointUpdated(boolean updated) { this.pointUpdated = updated; }
    public void setPointLocation(Point loc) {
        this.locX = loc.x;
        this.locY = loc.y;
        setLocation(loc);
    }
    public void setEditable(boolean editable) { this.editable = editable; }

    // Setters for the neighbors
    public void setNorthNeighbor(GridPoint neighbor) { this.northNeighbor = neighbor; }
    public void setEastNeighbor(GridPoint neighbor) { this.eastNeighbor = neighbor; }
    public void setSouthNeighbor(GridPoint neighbor) { this.southNeighbor = neighbor; }
    public void setWestNeighbor(GridPoint neighbor) { this.westNeighbor = neighbor; }

    public void setParentPanel(FullGridPanel panel) { this.parentPanel = panel; }

    public Point getControlPoint() { return new Point(controlX, controlY); }
    public boolean getMoved() { return moved; }
    public boolean getPartnerMoved() { return partnerMoved; }
    public boolean getPointUpdated() { return pointUpdated; }
    public boolean getRubberbanding() { return rubberbanding; }
    public Point getPointLocation() { return new Point(locX, locY); }
    public boolean getEditable() { return this.editable; }

    // Getters for the neighbors
    public GridPoint getNorthNeighbor() { return northNeighbor; }
    public GridPoint getEastNeighbor() { return eastNeighbor; }
    public GridPoint getSouthNeighbor() { return southNeighbor; }
    public GridPoint getWestNeighbor() { return westNeighbor; }

    // Function to repaint this component
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.SrcOver.derive(1f));

        // Draw a rectangle around the boundaries
        //g.setColor(Color.WHITE);
        //g.drawRect(0, 0, pointWidth, pointHeight);

        // Draw lines from each opposing to the point
        /*
        g.drawLine(0, 0, controlX - POINTRADIUS, controlY - POINTRADIUS);
        g.drawLine(0, pointHeight, controlX - POINTRADIUS, controlY + POINTRADIUS);
        g.drawLine(pointWidth, 0, controlX + POINTRADIUS, controlY - POINTRADIUS);
        g.drawLine(pointWidth, pointHeight, controlX + POINTRADIUS, controlY + POINTRADIUS);
        */

        // Draw lines to each neighbor point
        // Draw the control point
        //g.drawOval(controlX-POINTRADIUS, controlY-POINTRADIUS, POINTRADIUS*2, POINTRADIUS*2);
        g.setColor(Color.BLACK);
        g.drawOval(0, 0, POINTRADIUS*2, POINTRADIUS*2);
        if (highlighted || partnerPoint != null)
        {
            if (partnerPoint.highlighted || highlighted)
                g.setColor(Color.BLUE);
            else if (moved || partnerMoved) g.setColor(Color.GREEN);
            else g.setColor(Color.RED);
        }
        else if (moved || partnerMoved) g.setColor(Color.GREEN);
        else g.setColor(Color.RED);
        //g.fillOval(controlX-POINTRADIUS, controlY-POINTRADIUS, POINTRADIUS*2, POINTRADIUS*2);
        g.fillOval(0, 0, POINTRADIUS*2, POINTRADIUS*2);
    }

    // Get preferred size
    public Dimension getPreferredSize()
    {
        return new Dimension(pointWidth, pointHeight);
        //return new Dimension(POINTRADIUS*2, POINTRADIUS*2);
    }

    // This private class handles the motion for the rubberbanding.
    private class RubberbandMotionListener extends MouseMotionAdapter
    {
        /*
        This function will control the rubberbanding of the point.
         */
        public void mouseDragged(MouseEvent e)
        {
            if (rubberbanding)
            {
                /* Determine if the point is within bounds by forming a polygon of the corner neighbors.
                If the point is within that polygon, it is okay.
                 */
                Polygon boundPoly = new Polygon();

                if (northNeighbor != null) {
                    if (northNeighbor.westNeighbor != null)
                        boundPoly.addPoint(northNeighbor.westNeighbor.getLocation().x, northNeighbor.westNeighbor.getLocation().y);
                    else
                        boundPoly.addPoint(0,northNeighbor.getLocation().y - pointHeight);
                    boundPoly.addPoint(northNeighbor.getLocation().x, northNeighbor.getLocation().y);
                }
                else
                {
                    if (westNeighbor != null)
                        boundPoly.addPoint(westNeighbor.getLocation().x - pointWidth, 0);
                    else
                        boundPoly.addPoint(0,0);
                    if (eastNeighbor != null)
                        boundPoly.addPoint(eastNeighbor.getLocation().x - pointWidth, 0);
                    else
                        boundPoly.addPoint(parentPanel.getWidth(), 0);
                }
                if (eastNeighbor != null)
                    boundPoly.addPoint(eastNeighbor.getLocation().x, eastNeighbor.getLocation().y);
                else
                {
                    if (northNeighbor != null)
                        boundPoly.addPoint(parentPanel.getWidth(), northNeighbor.getLocation().y + pointHeight);
                    if (southNeighbor != null)
                        boundPoly.addPoint(parentPanel.getWidth(), southNeighbor.getLocation().y + pointHeight);
                }
                if (southNeighbor != null) {
                    if (southNeighbor.eastNeighbor != null)
                        boundPoly.addPoint(southNeighbor.eastNeighbor.getLocation().x, southNeighbor.eastNeighbor.getLocation().y);
                    else
                        boundPoly.addPoint(parentPanel.getPanelWidth(), southNeighbor.getLocation().y + pointHeight);
                    boundPoly.addPoint(southNeighbor.getLocation().x, southNeighbor.getLocation().y);
                }
                else
                {
                    if (eastNeighbor != null)
                        boundPoly.addPoint(eastNeighbor.getLocation().x + pointWidth, parentPanel.getHeight());
                    else
                        boundPoly.addPoint(parentPanel.getWidth(), parentPanel.getHeight());
                    if (westNeighbor != null)
                        boundPoly.addPoint(westNeighbor.getLocation().x + pointWidth, parentPanel.getHeight());
                    else
                        boundPoly.addPoint(0, parentPanel.getHeight());
                }
                if (westNeighbor != null)
                    boundPoly.addPoint(westNeighbor.getLocation().x, westNeighbor.getLocation().y);
                else
                {
                    if (southNeighbor != null)
                        boundPoly.addPoint(0, southNeighbor.getLocation().y - pointHeight);
                    if (northNeighbor != null)
                        boundPoly.addPoint(0, northNeighbor.getLocation().y - pointHeight);
                }

                if (boundPoly.contains(parentPanel.getMousePoint())) {
                    setPointLocation(new Point(parentPanel.getMousePoint().x, parentPanel.getMousePoint().y));
                    pointUpdated = true;

                    repaint();
                    partnerPoint.repaint();
                    parentPanel.repaint();
                }
            }
        }
    }
    // This private class handles the rubberbanding.
    private class RubberbandListener extends MouseAdapter
    {
        public void mousePressed(MouseEvent e)
        {
            // Determine if the mouse is within the control point box
            /*double mouseDist = Math.sqrt(Math.pow(e.getX() - controlX, 2) + Math.pow(e.getY() - controlY, 2));
            if (mouseDist <= (float)POINTRADIUS)
            {*/
            if (editable) {
                rubberbanding = true;
                moved = true;
                partnerPoint.setPartnerMoved(true);
            }
            //}
        }

        public void mouseReleased(MouseEvent e)
        {
            rubberbanding = false;
            pointUpdated = false;
        }

        public void mouseEntered(MouseEvent e)
        {
            if (editable) {
                highlighted = true;
                repaint();
                partnerPoint.repaint();
            }
        }

        public void mouseExited(MouseEvent e)
        {
            if (editable) {
                highlighted = false;
                repaint();
                partnerPoint.repaint();
            }
        }
    }
}
