/*
This class specifies the frame the preview of the animation will live in.
It will be displayed to the user whenever he/she presses the "preview" button.
 */

import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.util.TimerTask;
import java.util.Vector;

public class PreviewFrame extends JFrame {

    // Private vars
    private PreviewGridPanel panel;
    private Vector<TweenDataPoint> tweens;
    BufferedImage b1, b2, intermediate;
    private Timer animationTimer;
    private int fps, frameCount, currentFrame, frameDelay;

    public PreviewFrame()
    {
        super("Preview");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        animationTimer = new Timer();
        pack();
    }

    /*
    This function initializes the frame to the beginning of the preview.
     */
    public void init(int fps, int frameCount, int gridWidth, int gridHeight, BufferedImage b1, BufferedImage b2,
                     GridPoint[][] sourcePoints, GridPoint[][] destPoints)
    {
        animationTimer.cancel();
        animationTimer = new Timer();
        this.fps = fps;
        this.frameCount = frameCount;
        this.currentFrame = 1;
        this.b1 = b1;
        this.b2 = b2;

        panel = new PreviewGridPanel(gridWidth, gridHeight, false, b1, b2, sourcePoints, destPoints);
        for (int i = 0; i < tweens.size(); i++)
            panel.getPoint(tweens.get(i).gridX, tweens.get(i).gridY).setMoved(true);
        panel.applyMorph();
        add(panel);
        pack();
        frameDelay = 1000 / fps;
        animationTimer.schedule(new AnimationTimerTask(), frameDelay);
    }

    public void setTweens(Vector<TweenDataPoint> tweens) { this.tweens = tweens; }

    /*
    This timer task causes the animation to move by one frame.
     */
    private class AnimationTimerTask extends TimerTask
    {
        public void run()
        {
            // Update all of the points
            for (int i = 0; i < tweens.size(); i++)
            {
                Point startPoint = tweens.get(i).sourcePoint.getControlPoint();
                Point endPoint = tweens.get(i).destPoint.getControlPoint();
                float xInterp = (float)startPoint.x + (((float)endPoint.x - (float)startPoint.x) * ((float)currentFrame/(float)frameCount));
                float yInterp = (float)startPoint.y + (((float)endPoint.y - (float)startPoint.y) * ((float)currentFrame/(float)frameCount));
                panel.getPoint(tweens.get(i).gridX, tweens.get(i).gridY).setControlPoint(new Point((int)xInterp, (int)yInterp));
                panel.getPoint(tweens.get(i).gridX, tweens.get(i).gridY).repaint();
            }

            // Morph the images
            currentFrame = ((currentFrame) % frameCount) + 1;

            panel.setStage((float)currentFrame / (float)frameCount);

            panel.applyMorph();
            panel.repaint();
            animationTimer.schedule(new AnimationTimerTask(), frameDelay);
        }
    }
}
