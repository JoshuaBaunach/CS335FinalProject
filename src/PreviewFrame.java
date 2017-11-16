/*
This class specifies the frame the preview of the animation will live in.
It will be displayed to the user whenever he/she presses the "preview" button.
 */

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class PreviewFrame extends JFrame {

    public PreviewFrame()
    {
        super("Preview");

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        pack();
    }
}
