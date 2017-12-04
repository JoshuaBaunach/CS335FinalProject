/*
This class defines a panel that will be used in the preview
for an animation.
 */

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class PreviewGridPanel extends FullGridPanel{

    MorphableImagePair imgPair;
    float stage;

    // Constructor
    public PreviewGridPanel(int gridWidth, int gridHeight, boolean editMode, BufferedImage b1, BufferedImage b2)
    {
        super(gridWidth, gridHeight, editMode, b1);
        stage = 0.5f;
        imgPair = new MorphableImagePair(b1, b2, stage);
    }

    /*
    This paintComponent method will fade the two images together.
     */
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, pointWidth * gridWidth, pointHeight * gridHeight);
        g2d.setComposite(AlphaComposite.SrcOver.derive(1.f - stage));
        g2d.drawImage(imgPair.getBufferedImage(), 0, 0, this);
        g2d.setComposite(AlphaComposite.SrcOver.derive(stage));
        g2d.drawImage(imgPair.getOtherBufferedImage(), 0, 0, this);

        g2d.setComposite(AlphaComposite.SrcOver.derive(1.f));

        // Draw a border around the entire object
        g2d.setColor(Color.WHITE);
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRect(0, 0, pointWidth * gridWidth, pointHeight * gridHeight);
        g2d.setStroke(oldStroke);
    }
}
