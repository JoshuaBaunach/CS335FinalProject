/*
This class is the main frame of the project.
It will contain many of the components that will be visible to the user throughout
his/her usage of the application.
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    private JMenuItem newItem, openItem, saveItem, exitItem, previewItem, adjItem;
    private int gridWidth, gridHeight, pointSize;

    private JSlider swingSliderFps, swingSliderTime;
    private JFrame adjFrame; // frame used for fps and time adjustments
    private JPanel adjPanel;
    private GridLayout gridLayout;
    private JLabel fpsLabel, timeLabel;
    private int fps, aniTime;
    private int frameCount;

    // Constant Variables
    private final String DEFAULTSRC = "res/MillenniumForce.jpg";
    private final String DEFAULTDEST = "res/Maverick.jpg";

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
        gridWidth = 10;
        gridHeight = 10;

        // Add two full grid panels
        sourcePanel = new FullGridPanel(gridWidth, gridHeight, true, DEFAULTSRC);
        destPanel = new FullGridPanel(gridWidth, gridHeight, true, DEFAULTDEST, sourcePanel.getPanelWidth(), sourcePanel.getPanelHeight());

        sourcePanel.setPartnerPanel(destPanel);
        destPanel.setPartnerPanel(sourcePanel);

        constraints.gridx = 0;
        constraints.gridy = 0;
        mainPanel.add(sourcePanel);

        constraints.gridx = 1;
        constraints.gridy = 0;
        mainPanel.add(destPanel);

        // Slider init:

        // setting labels
        fpsLabel = new JLabel("FPS: 60");
        timeLabel = new JLabel("Time: 1 second(s)");

        //fps slider init
        swingSliderFps = new JSlider(JSlider.HORIZONTAL, 1, 60, 60);
        swingSliderFps.setLayout(new BorderLayout());
        swingSliderFps.setPaintLabels(true);
        swingSliderFps.setPaintTicks(true);
        swingSliderFps.setPreferredSize(new Dimension(200,30));

        // time slider init
        swingSliderTime = new JSlider(JSlider.HORIZONTAL, 1, 5, 1);
        swingSliderTime.setLayout(new BorderLayout());
        swingSliderTime.setPaintLabels(true);
        swingSliderTime.setPaintTicks(true);
        swingSliderTime.setPreferredSize(new Dimension(200,30));
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

        // for the sliders frame to adjust fps:
        adjItem = new JMenuItem("Adjust Animation");
        adjItem.addActionListener(new ActionListener() {
            //@Override
            public void actionPerformed(ActionEvent e)
            {
                // make another frame pop-up for slider adjustment
                adjFrame = new JFrame("Adjust Animation");
                adjPanel = new JPanel();

                //setting layout on the adjPanel
                gridLayout = new GridLayout(0,2);
                adjPanel.setLayout(gridLayout);

                // adding labels overtop the sliders
                adjPanel.add(fpsLabel);
                adjPanel.add(timeLabel);

                //adding the sliders to the frame/panel
                adjPanel.add(swingSliderFps);
                adjPanel.add(swingSliderTime);
                adjFrame.getContentPane().add(adjPanel);

                //adjFrame.setSize(745, 470);
                adjFrame.pack();
                adjFrame.setVisible(true);

                swingSliderFps.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        fpsLabel.setText("FPS: " + swingSliderFps.getValue());
                    }
                });

                swingSliderTime.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        timeLabel.setText("Time: " + swingSliderTime.getValue() + " second(s)");
                    }
                });

            }
        });


        previewItem = new JMenuItem("Preview Animation");
        previewItem.addActionListener(new ActionListener() {
            //@Override
            public void actionPerformed(ActionEvent e) {
                // Fetch all of the "tween" data points
                Vector<TweenDataPoint> tweens = new Vector();
                for (int i = 0; i < gridWidth; i++)
                {
                    for (int j = 0; j < gridHeight; j++)
                    {
                        //if (sourcePanel.getPoint(i, j).getMoved() || destPanel.getPoint(i, j).getMoved())
                        tweens.add(new TweenDataPoint(sourcePanel.getPoint(i, j), destPanel.getPoint(i, j), i, j));
                    }
                }
                previewFrame.setTweens(tweens);

                // default: 60 fps and a framecount of 60 - represents 60 fps at a total animation time of 1 second
                fps = swingSliderFps.getValue();
                aniTime = swingSliderTime.getValue();
                frameCount = fps * aniTime;

                // init the previewFrame
                previewFrame.init(fps, frameCount, gridWidth, gridHeight, sourcePanel.getMorphableImage().getBufferedImage(),
                        destPanel.getMorphableImage().getBufferedImage());
                previewFrame.setVisible(true);
            }
        });
        animationMenu.add(previewItem);
        animationMenu.add(adjItem);

        add(menuBar, BorderLayout.NORTH);
    }
}
