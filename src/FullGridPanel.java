/*
This class defines a panel of points that will be displayed and manipulated by the user.
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FullGridPanel extends JPanel
{
    // Private variables
    private int gridSize, pointSize;
    private GridLayout layout;
    private GridPoint[][] points;
    private BufferedImage img;

    public FullGridPanel(int gridSize, int pointSize)
    {
        this.gridSize = gridSize;
        this.pointSize = pointSize;

        layout = new GridLayout(gridSize, gridSize);
        setLayout(layout);

        points = new GridPoint[gridSize][gridSize];

        for (GridPoint[] subArray: points)
        {
            for (GridPoint point: subArray)
            {
                point = new GridPoint(pointSize);
                add(point);
            }
        }
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // TODO: Get the black rectangle to paint
        // If no image is present, just draw a black background
        if (img == null)
        {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, pointSize * gridSize, pointSize * gridSize);
        }
    }

    public Dimension getPreferredSize()
    {
        int dim = pointSize * gridSize;
        return new Dimension(dim, dim);
    }
}
