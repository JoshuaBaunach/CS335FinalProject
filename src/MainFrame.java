/*
This class is the main frame of the project.
It will contain many of the components that will be visible to the user throughout
his/her usage of the application.
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;

public class MainFrame extends JFrame
{

    // Private variables
    private PreviewFrame previewFrame;
    private FullGridPanel sourcePanel, destPanel;
    private JPanel mainPanel;
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JMenuBar menuBar;
    private JMenu fileMenu, animationMenu;
    private JMenuItem newItem, openItem, saveItem, exitItem, previewItem;
    private int gridSize, pointSize;

    public MainFrame()
    {
        super("Final Project");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Temp Layout
        layout = new GridBagLayout();
        constraints = new GridBagConstraints();
        mainPanel = new JPanel();
        mainPanel.setLayout(layout);
        setLayout(new BorderLayout());

        initMenu();
        initFrame();

        add(mainPanel, BorderLayout.SOUTH);
        pack();
        setVisible(true);

        // Create the preview frame and save it for later
        previewFrame = new PreviewFrame();
    }

    /*
    This function initializes everything the user sees in the frame.
     */
    public void initFrame()
    {
        gridSize = 10;
        pointSize = 50;

        // Add two full grid panels
        sourcePanel = new FullGridPanel(gridSize, pointSize, true);
        destPanel = new FullGridPanel(gridSize, pointSize, true);

        sourcePanel.setPartnerPanel(destPanel);
        destPanel.setPartnerPanel(sourcePanel);

        constraints.gridx = 0;
        constraints.gridy = 0;
        mainPanel.add(sourcePanel);

        constraints.gridx = 1;
        constraints.gridy = 0;
        mainPanel.add(destPanel);
    }

    /*
    This function initializes the menu bar.
     */
    public void initMenu()
    {
        menuBar = new JMenuBar();

        // Add the file menu and all of its components
        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            //@Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Add the animation menu and all of its components
        animationMenu = new JMenu("Animation");
        menuBar.add(animationMenu);

        previewItem = new JMenuItem("Preview Animation");
        previewItem.addActionListener(new ActionListener() {
            //@Override
            public void actionPerformed(ActionEvent e) {
                // Fetch all of the "tween" data points
                Vector<TweenDataPoint> tweens = new Vector();
                for (int i = 0; i < gridSize; i++)
                {
                    for (int j = 0; j < gridSize; j++)
                    {
                        if (sourcePanel.getPoint(i, j).getMoved())
                            tweens.add(new TweenDataPoint(sourcePanel.getPoint(i, j), destPanel.getPoint(i, j), i, j));
                    }
                }
                previewFrame.setTweens(tweens);
                previewFrame.init(60, 60, gridSize, pointSize);
                previewFrame.setVisible(true);
            }
        });
        animationMenu.add(previewItem);

        add(menuBar, BorderLayout.NORTH);
    }
}
