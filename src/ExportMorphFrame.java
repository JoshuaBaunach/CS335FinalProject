/*
This class allows the user to export the animation to a series of JPEG images.
It will use many of the same techniques used in PreviewFrame to render the images
as they are morphed.
 */

import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ExportMorphFrame extends JFrame {

    // Private vars from PreviewFrame
    private PreviewGridPanel panel;
    private Vector<TweenDataPoint> tweens;
    BufferedImage b1, b2, intermediate;
    private int fps, frameCount, currentFrame, frameDelay;

    // Own private vars
    private JProgressBar progressBar;
    private String saveDirectory;

    public ExportMorphFrame(String saveDirectory)
    {
        super("Exporting...");
        this.saveDirectory = saveDirectory;

        setLayout(new FlowLayout());

        JLabel progLabel = new JLabel("Exporting...");
        add(progLabel);

        progressBar = new JProgressBar(0, 100);
        add(progressBar);

        pack();
        setVisible(true);
    }

    public void init(int fps, int frameCount, int gridWidth, int gridHeight, BufferedImage b1, BufferedImage b2,
                     GridPoint[][] sourcePoints, GridPoint[][] destPoints)
    {
        this.fps = fps;
        this.frameCount = frameCount;
        this.currentFrame = 1;
        this.b1 = b1;
        this.b2 = b2;

        panel = new PreviewGridPanel(gridWidth, gridHeight, false, b1, b2, sourcePoints, destPoints);
        for (int i = 0; i < tweens.size(); i++)
            panel.getPoint(tweens.get(i).gridX, tweens.get(i).gridY).setMoved(true);
        pack();
        frameDelay = 1000 / fps;
    }

    public void setTweens(Vector<TweenDataPoint> tweens) { this.tweens = tweens; }


    /*
    This function renders each frame, piles each BufferedImage into a uniform BufferedImage, and saves the
    BufferedImage to the directory.
     */
    public void renderFrames()
    {
        for (int currentFrame = 0; currentFrame <  frameCount; currentFrame++) {
            // Update all of the points
            for (int i = 0; i < tweens.size(); i++) {
                Point startPoint = tweens.get(i).sourcePoint.getControlPoint();
                Point endPoint = tweens.get(i).destPoint.getControlPoint();
                float xInterp = (float) startPoint.x + (((float) endPoint.x - (float) startPoint.x) * ((float) currentFrame / (float) frameCount));
                float yInterp = (float) startPoint.y + (((float) endPoint.y - (float) startPoint.y) * ((float) currentFrame / (float) frameCount));
                panel.getPoint(tweens.get(i).gridX, tweens.get(i).gridY).setControlPoint(new Point((int) xInterp, (int) yInterp));
                panel.getPoint(tweens.get(i).gridX, tweens.get(i).gridY).repaint();
            }

            // Morph the images

            panel.setStage((float) currentFrame / (float) frameCount);

            panel.applyMorph();

            // Combine the buffered images from the two panels into one
            BufferedImage srcImg = panel.getImgPair().getBufferedImage();
            BufferedImage destImg = panel.getImgPair().getOtherBufferedImage();

            // Adjust the transparencies of the images appropriately
            BufferedImage combined = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = combined.createGraphics();
            g2d.setComposite(AlphaComposite.SrcOver.derive(1.f - ((float)currentFrame / (float)frameCount)));
            g2d.drawImage(srcImg, 0, 0, null);
            g2d.setComposite(AlphaComposite.SrcOver.derive((float)currentFrame / (float)frameCount));
            g2d.drawImage(destImg, 0, 0, null);

            // Save the Image
            try {
                ImageIO.write(combined, "JPEG", new File(saveDirectory, "frame" + Integer.toString(currentFrame) + ".jpeg"));
            }
            catch (IOException e)
            {
                System.out.println("Error saving image!");
            }

            // Update the progress bar
            progressBar.setValue((int) (((float)currentFrame / (float) frameCount) * 100));
            repaint();
        }
    }
}
