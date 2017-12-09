/*
This class allows the user to export the animation to a series of JPEG images.
It will use many of the same techniques used in PreviewFrame to render the images
as they are morphed.
 */

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IRational;

import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ExportMorphFrame extends JDialog {

    // Private vars from PreviewFrame
    private PreviewGridPanel panel;
    private Vector<TweenDataPoint> tweens;
    BufferedImage b1, b2, intermediate;
    private int fps, frameCount, currentFrame, frameDelay;
    private boolean saveAsMovie;

    // Own private vars
    private JProgressBar progressBar;
    private String saveDirectory;

    public ExportMorphFrame(String saveDirectory, boolean saveAsMovie, JFrame parent)
    {
        super(parent, "Exporting...", false);

        this.saveDirectory = saveDirectory;
        this.saveAsMovie = saveAsMovie;

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        setLayout(new FlowLayout());

        JLabel progLabel = new JLabel("Exporting...");
        add(progLabel);

        if (saveAsMovie)
            progressBar = new JProgressBar(0, 100);
        else
            progressBar = new JProgressBar(0, 50);
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
        setModal(true);
        RenderTask rTask = new RenderTask();
        rTask.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName()))
                    progressBar.setValue((int) evt.getNewValue());
            }
        });
        rTask.execute();
    }

    public static BufferedImage convertToType(BufferedImage sourceImage,
                                              int targetType)
    {
        BufferedImage image;

        // if the source image is already the target type, return the source image

        if (sourceImage.getType() == targetType)
            image = sourceImage;

            // otherwise create a new image of the target type and draw the new
            // image

        else
        {
            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;
    }

    private ExportMorphFrame getMe() { return this; }

    private class RenderTask extends SwingWorker<String, Integer>
    {
        public String doInBackground()
        {
            Vector<String> imageList = new Vector<String>();
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
                    // Delete the previous image, if it exists
                    imageList.add(saveDirectory + "/frame" + Integer.toString(currentFrame) + ".jpeg");
                    Files.deleteIfExists(Paths.get(imageList.get(currentFrame)));
                    ImageIO.write(combined, "JPEG", new File(saveDirectory, "frame" + Integer.toString(currentFrame) + ".jpeg"));
                }
                catch (IOException e)
                {
                    System.out.println("Error saving image!");
                }

                // Update the progress bar
                setProgress((int) (((float)currentFrame / (float) frameCount) * 50));
                publish((int) (((float)currentFrame / (float) frameCount) * 50));
            }

            if (saveAsMovie) {
                // Delete the previous movie, if it exists
                try {
                    Files.deleteIfExists(Paths.get(saveDirectory + "/output.mp4"));
                }
                catch (IOException ex)
                {
                    System.out.println("Creating new movie");
                }
                // Add the images to a video
                final IMediaWriter writer = ToolFactory.makeWriter(saveDirectory + "/output.mp4");
                final IRational framerate = IRational.make(fps, 1);

                writer.addVideoStream(0, 0, framerate, 750, 500);

                long startTime = System.nanoTime();
                for (int i = 0; i < frameCount; i++)
                {
                    MorphableImage img = new MorphableImage(imageList.get(i));
                    // convert to the right image type
                    BufferedImage bgrScreen = convertToType(img.getBufferedImage(),
                            BufferedImage.TYPE_3BYTE_BGR);

                    // encode the image
                    writer.encodeVideo(0,bgrScreen,
                            System.nanoTime()-startTime, TimeUnit.NANOSECONDS);

                    // Update the progress bar
                    setProgress((int) (((float)i / (float) frameCount) * 50) + 50);
                    publish((int) (((float)i / (float) frameCount) * 50) + 50);
                }

                writer.close();

                // Delete all the previous images
                try {
                    for (int i = 0; i < frameCount; i++)
                        Files.deleteIfExists(Paths.get(imageList.get(i)));
                }
                catch (IOException ex)
                {
                    System.out.println("Something went horribly wrong: IO Exception");
                }
            }
            return null;
        }

        protected void done()
        {
            //dispatchEvent(new WindowEvent(getMe(), WindowEvent.WINDOW_CLOSING));
            getMe().dispose();
        }
    }
}
