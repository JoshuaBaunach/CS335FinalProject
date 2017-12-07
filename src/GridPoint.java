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
    private int pointWidth, pointHeight, controlX, controlY;
    private boolean moved, rubberbanding, partnerMoved, pointUpdated;
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
        this.rubberbanding = false;
        this.pointUpdated = true;

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
        this.pointUpdated = true;

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

    // Setters for the neighbors
    public void setNorthNeighbor(GridPoint neighbor) { this.northNeighbor = neighbor; }
    public void setEastNeighbor(GridPoint neighbor) { this.eastNeighbor = neighbor; }
    public void setSouthNeighbor(GridPoint neighbor) { this.southNeighbor = neighbor; }
    public void setWestNeighbor(GridPoint neighbor) { this.westNeighbor = neighbor; }

    public void setParentPanel(FullGridPanel panel) { this.parentPanel = panel; }

    public Point getControlPoint() { return new Point(controlX, controlY); }
    public boolean getMoved() { return moved; }
    public boolean getPointUpdated() { return pointUpdated; }
    public boolean getRubberbanding() { return rubberbanding; }

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
        if (moved || partnerMoved) g.setColor(Color.GREEN);
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
        public void mouseDragged(MouseEvent e)
        {
            if (rubberbanding)
            {
                // If mouse x is out of bounds, set the x pos to the bound
                /*
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
                    */

                /* Determine if the point is within bounds by forming a polygon of the corner neighbors.
                If the point is within that polygon, it is okay.
                 */
                Polygon boundPoly = new Polygon();
                /*if (northNeighbor != null)
                {
                    if (northNeighbor.westNeighbor != null)
                        boundPoly.addPoint(northNeighbor.westNeighbor.getLocation().x, northNeighbor.westNeighbor.getLocation().y);
                    else
                        boundPoly.addPoint(0,northNeighbor.getLocation().y);
                    boundPoly.addPoint(northNeighbor.getLocation().x, northNeighbor.getLocation().y);
                    if (northNeighbor.eastNeighbor != null)
                        boundPoly.addPoint(northNeighbor.eastNeighbor.getLocation().x, northNeighbor.eastNeighbor.getLocation().y);
                    else
                        boundPoly.addPoint(parentPanel.getPanelWidth(), northNeighbor.getLocation().y);
                }
                else
                {
                    if (westNeighbor != null)
                        boundPoly.addPoint(westNeighbor.getLocation().x, 0);
                    else
                        boundPoly.addPoint(0, 0);
                    if (eastNeighbor != null)
                        boundPoly.addPoint(eastNeighbor.getLocation().x, 0);
                    else
                        boundPoly.addPoint(parentPanel.getPanelWidth(), 0);
                }
                if (southNeighbor != null) {
                    if (southNeighbor.eastNeighbor != null)
                        boundPoly.addPoint(southNeighbor.eastNeighbor.getLocation().x, southNeighbor.eastNeighbor.getLocation().y);
                    else
                        boundPoly.addPoint(parentPanel.getPanelWidth(),southNeighbor.getLocation().y);
                    boundPoly.addPoint(southNeighbor.getLocation().x, southNeighbor.getLocation().y);
                    if (southNeighbor.westNeighbor != null)
                        boundPoly.addPoint(southNeighbor.westNeighbor.getLocation().x, southNeighbor.westNeighbor.getLocation().y);
                    else
                        boundPoly.addPoint(0, southNeighbor.getLocation().y);
                }
                else
                {
                    if (eastNeighbor != null)
                        boundPoly.addPoint(eastNeighbor.getLocation().x, parentPanel.getPanelHeight());
                    else
                        boundPoly.addPoint(parentPanel.getPanelWidth(), parentPanel.getPanelHeight());
                    if (westNeighbor != null)
                        boundPoly.addPoint(westNeighbor.getLocation().x, parentPanel.getPanelHeight());
                    else
                        boundPoly.addPoint(0, parentPanel.getPanelHeight());
                }*/

                if (northNeighbor != null) {
                    if (northNeighbor.westNeighbor != null)
                        boundPoly.addPoint(northNeighbor.westNeighbor.getLocation().x, northNeighbor.westNeighbor.getLocation().y);
                    boundPoly.addPoint(northNeighbor.getLocation().x, northNeighbor.getLocation().y);
                }
                if (eastNeighbor != null)
                    boundPoly.addPoint(eastNeighbor.getLocation().x, eastNeighbor.getLocation().y);
                if (southNeighbor != null) {
                    if (southNeighbor.eastNeighbor != null)
                        boundPoly.addPoint(southNeighbor.eastNeighbor.getLocation().x, southNeighbor.eastNeighbor.getLocation().y);
                    boundPoly.addPoint(southNeighbor.getLocation().x, southNeighbor.getLocation().y);
                }
                if (westNeighbor != null)
                    boundPoly.addPoint(westNeighbor.getLocation().x, westNeighbor.getLocation().y);

                if (boundPoly.contains(parentPanel.getMousePoint())) {
                    setLocation(new Point(parentPanel.getMousePoint().x, parentPanel.getMousePoint().y));
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
                rubberbanding = true;
                moved = true;
                partnerPoint.setPartnerMoved(true);
            //}
        }

        public void mouseReleased(MouseEvent e)
        {
            rubberbanding = false;
            pointUpdated = false;
        }
    }
}
