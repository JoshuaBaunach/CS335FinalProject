/*
This class is the main frame of the project.
It will contain many of the components that will be visible to the user throughout
his/her usage of the application.
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class MainFrame extends JFrame
{
    public MainFrame()
    {
        super("Final Project");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Temp Layout
        setLayout(new GridLayout(1, 2));

        // Add one full grid panel
        FullGridPanel sourcePanel = new FullGridPanel(10, 50);
        FullGridPanel destPanel = new FullGridPanel(10, 50);

        sourcePanel.setPartnerPanel(destPanel);
        destPanel.setPartnerPanel(sourcePanel);

        add(sourcePanel);
        add(destPanel);

        pack();
        setVisible(true);
    }
}
