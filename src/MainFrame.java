/*
This class is the main frame of the project.
It will contain many of the components that will be visible to the user throughout
his/her usage of the application.
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.StringTokenizer;
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
    private JMenu fileMenu, animationMenu; // Menus that will appear at the top of the menu bar
    private JMenu exportSubmenu; // Submenus
    private JMenuItem newItem, openItem, openSrcItem, openDestItem, saveItem, exitItem, previewItem, adjItem, resolutionItem,
    exportJPEGItem;

    private JFileChooser fileChooser, fileSelector;
    private BufferedReader buffReader;
    private PrintWriter writer;
    private int gridWidth, gridHeight, pointSize;

    private JSlider swingSliderFps, swingSliderTime, controlPointXResolutionSlider, controlPointYResolutionSlider;
    private JFrame adjFrame; // frame used for fps and time adjustments
    private JPanel adjPanel;
    private GridLayout gridLayout;
    private JLabel fpsLabel, timeLabel, xResLabel, yResLabel;
    private int fps, aniTime;
    private int frameCount;
    private String srcDirectory, destDirectory;

    // Constant Variables
    private final String DEFAULTSRC = "res/Nathaniel.jpg";
    private final String DEFAULTDEST = "res/Seales.jpg";

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

        fileChooser = new JFileChooser();

        gridWidth = 10;
        gridHeight = 10;
        srcDirectory = DEFAULTSRC;
        destDirectory = DEFAULTDEST;

        initMenu();
        initFrame();
        initSliders();

        add(mainPanel, BorderLayout.SOUTH);
        pack();
        setVisible(true);

        // Create the preview frame and save it for later
        previewFrame = new PreviewFrame();

    }

    /*
    This function resets the frame.
    It is usually called when the control grid resolution changes or something else changes
    such that we must dump the previous data.
     */
    public void resetFrame()
    {
        // Remove the grid panels
        mainPanel.remove(sourcePanel);
        mainPanel.remove(destPanel);

        // Do it all over again
        initFrame();

        pack();
    }
    /*
    This function initializes everything the user sees in the frame.
     */
    public void initFrame()
    {
        // Add two full grid panels
        sourcePanel = new FullGridPanel(gridWidth, gridHeight, true, srcDirectory);
        destPanel = new FullGridPanel(gridWidth, gridHeight, true, destDirectory, sourcePanel.getPanelWidth(), sourcePanel.getPanelHeight());

        sourcePanel.setPartnerPanel(destPanel);
        destPanel.setPartnerPanel(sourcePanel);

        constraints.gridx = 0;
        constraints.gridy = 0;
        mainPanel.add(sourcePanel);

        constraints.gridx = 1;
        constraints.gridy = 0;
        mainPanel.add(destPanel);

    }

    /* This function initializes the sliders.
     */
    public void initSliders()
    {
        // Slider init:

        // setting labels
        fpsLabel = new JLabel("FPS: 60");
        timeLabel = new JLabel("Time: 1 second(s)");
        xResLabel = new JLabel("X Resolution: 10");
        yResLabel = new JLabel("Y Resolution: 10");

        //fps slider init
        swingSliderFps = new JSlider(JSlider.HORIZONTAL, 1, 60, 30);
        swingSliderFps.setLayout(new BorderLayout());
        swingSliderFps.setPaintLabels(true);
        swingSliderFps.setPaintTicks(true);
        swingSliderFps.setPreferredSize(new Dimension(200,30));

        // time slider init
        swingSliderTime = new JSlider(JSlider.HORIZONTAL, 1, 5, 3);
        swingSliderTime.setLayout(new BorderLayout());
        swingSliderTime.setPaintLabels(true);
        swingSliderTime.setPaintTicks(true);
        swingSliderTime.setPreferredSize(new Dimension(200,30));

        // Control Point X Resolution Slider
        controlPointXResolutionSlider = new JSlider(JSlider.HORIZONTAL, 5, 25, 10);
        controlPointXResolutionSlider.setLayout(new BorderLayout());
        controlPointXResolutionSlider.setPaintLabels(true);
        controlPointXResolutionSlider.setPaintTicks(true);
        controlPointXResolutionSlider.setPreferredSize(new Dimension(200,30));

        // Control Point Y Resolution Slider
        controlPointYResolutionSlider = new JSlider(JSlider.HORIZONTAL, 5, 25, 10);
        controlPointYResolutionSlider.setLayout(new BorderLayout());
        controlPointYResolutionSlider.setPaintLabels(true);
        controlPointYResolutionSlider.setPaintTicks(true);
        controlPointYResolutionSlider.setPreferredSize(new Dimension(200,30));
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
        openSrcItem = new JMenuItem("Import Source Image");
        openSrcItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setDialogTitle("Import Source Image");
                int dialogVal = fileChooser.showOpenDialog(getContentPane());

                if (dialogVal == JFileChooser.APPROVE_OPTION)
                {
                    File f = fileChooser.getSelectedFile();
                    if (!f.isDirectory()) {
                        srcDirectory = f.getAbsolutePath();
                        resetFrame();
                    }
                }
            }
        });
        openDestItem = new JMenuItem("Import Destination Image");
        openDestItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setDialogTitle("Import Destination Image");
                int dialogVal = fileChooser.showOpenDialog(getContentPane());

                if (dialogVal == JFileChooser.APPROVE_OPTION)
                {
                    File f = fileChooser.getSelectedFile();
                    if (!f.isDirectory()) {
                        destDirectory = f.getAbsolutePath();
                        resetFrame();
                    }
                }
            }
        });

        openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Allow user to select ONLY TXT files and pull data from them
                fileSelector = new JFileChooser();
                fileSelector.setCurrentDirectory(new java.io.File("."));
                fileSelector.setDialogTitle("Open the saved txt file with data");
                // only allowing the user to choose only txt files
                //fileSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                //fileSelector.setAcceptAllFileFilterUsed(false);
                fileSelector = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
                fileSelector.setFileFilter(filter);

                if (fileSelector.showOpenDialog(mainPanel) == JFileChooser.APPROVE_OPTION)
                {
                    System.out.println("getSelectedFile() : "
                            +  fileSelector.getSelectedFile()); // selected "file" actually grabs the directory the user selected
                    try {
                        // getSelectedFile() returns the actual txt file
                        // SWITCH FROM WRITE TO READ
                        buffReader = new BufferedReader(new FileReader(fileSelector.getSelectedFile()));

                        String line = buffReader.readLine(); // reed entire line
                        StringTokenizer token = new StringTokenizer(line);

                        // read single word and make it an int
                        int numY = Integer.parseInt(token.nextToken()); // num of points in the Y dir
                        int numX = Integer.parseInt(token.nextToken()); // num points in the x dir

                        gridWidth = numX;
                        gridHeight = numY;

                        resetFrame();

                        Point[][] newSourcePoints = new Point[numX][numY];
                        Point[][] newDestPoints = new Point[numX][numY];

                        // Get the source points
                        for (int i = 0; i < numY; i++)
                        {
                            for (int j = 0; j < numX; j++)
                            {
                                // TODO init each point using the info we parsed
                                line = buffReader.readLine(); // reed entire line
                                token = new StringTokenizer(line);
                                int curX = Integer.parseInt(token.nextToken());
                                int curY = Integer.parseInt(token.nextToken());
                                newSourcePoints[i][j] = new Point(curX, curY);
                                //writer.println(gridPoints[i][j].getControlPoint().x + " " + gridPoints[i][j].getControlPoint().y);
                            }
                        }
                        sourcePanel.setPoints(newSourcePoints);
                        buffReader.readLine();
                        // Get the destination points
                        for (int i = 0; i < numY; i++)
                        {
                            for (int j = 0; j < numX; j++)
                            {
                                // TODO init each point using the info we parsed
                                line = buffReader.readLine(); // reed entire line
                                token = new StringTokenizer(line);
                                int curX = Integer.parseInt(token.nextToken());
                                int curY = Integer.parseInt(token.nextToken());
                                newDestPoints[i][j] = new Point(curX, curY);
                                //writer.println(gridPoints[i][j].getControlPoint().x + " " + gridPoints[i][j].getControlPoint().y);
                            }
                        }
                        destPanel.setPoints(newDestPoints);

                    }
                    catch(FileNotFoundException e2) {
                        e2.printStackTrace();

                    }catch(IOException e2) {
                        e2.printStackTrace();
                    } finally
                    {

                    }
                }
                // user did not select anything
                else {
                    System.out.println("User did not select anything");
                }
            }});
        saveItem = new JMenuItem("Save");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // save the data to a file
                fileSelector = new JFileChooser();
                fileSelector.setCurrentDirectory(new java.io.File("."));
                fileSelector.setDialogTitle("Select which DIRECTORY you want to store the save file");
                // only allowing the user to choose directories
                fileSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileSelector.setAcceptAllFileFilterUsed(false);
                if (fileSelector.showOpenDialog(mainPanel) == JFileChooser.APPROVE_OPTION)
                {

                    System.out.println("getSelectedFile() : "
                            +  fileSelector.getSelectedFile()); // selected "file" actually grabs the directory the user selected
                    try {
                        writer = new PrintWriter(fileSelector.getSelectedFile() + "\\savedData.txt", "UTF-8");
                        GridPoint[][] srcGridPoints = sourcePanel.getPoints();

                        // for every grid point in the gridPoints array
                        writer.println(srcGridPoints.length + " " + srcGridPoints[0].length);
                        for (int i = 0; i < srcGridPoints.length; i++)
                        {
                            for (int j = 0; j < srcGridPoints[0].length; j++)
                            {
                                writer.println(srcGridPoints[i][j].getControlPoint().x + " " + srcGridPoints[i][j].getControlPoint().y);
                            }
                        }
                        writer.println("DEST");
                        GridPoint[][] destGridPoints = destPanel.getPoints();

                        for (int i = 0; i < destGridPoints.length; i++)
                        {
                            for (int j = 0; j < destGridPoints[0].length; j++)
                            {
                                writer.println(destGridPoints[i][j].getControlPoint().x + " " + destGridPoints[i][j].getControlPoint().y);
                            }
                        }

                        writer.println("MARKER"); // denote the end of the control points
                        writer.println(fps);
                        writer.println(aniTime);
                        writer.close();
                    }
                    catch(FileNotFoundException e2) {
                        e2.printStackTrace();

                    }catch(UnsupportedEncodingException e2) {

                        e2.printStackTrace();
                    }
                    finally
                    {
                        // closing file stream
                        if(writer != null){
                            writer.close();
                        }
                    }
                }
                // user did not select anything
                else {
                    System.out.println("User did not select anything");
                }
            }});
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            //@Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Create the menus needed for exporting the image
        exportSubmenu = new JMenu("Export...");
        exportJPEGItem = new JMenuItem("JPEG Images");
        exportJPEGItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setDialogTitle("Choose Export Directory");
                fileChooser.setApproveButtonText("Export");
                int dialogVal = fileChooser.showOpenDialog(getContentPane());

                if (dialogVal == JFileChooser.APPROVE_OPTION)
                {
                    File f = fileChooser.getSelectedFile();
                    ExportMorphFrame emf = new ExportMorphFrame(f.getAbsolutePath());
                    Vector<TweenDataPoint> tweens = new Vector();
                    for (int i = 0; i < gridWidth; i++)
                    {
                        for (int j = 0; j < gridHeight; j++)
                        {
                            //if (sourcePanel.getPoint(i, j).getMoved() || destPanel.getPoint(i, j).getMoved())
                            tweens.add(new TweenDataPoint(sourcePanel.getPoint(i, j), destPanel.getPoint(i, j), i, j));
                        }
                    }
                    emf.setTweens(tweens);
                    // default: 60 fps and a framecount of 60 - represents 60 fps at a total animation time of 1 second
                    fps = swingSliderFps.getValue();
                    aniTime = swingSliderTime.getValue();
                    frameCount = fps * aniTime;
                    emf.init(fps, frameCount, gridWidth, gridHeight, sourcePanel.getMorphableImage().getBufferedImage(),
                            destPanel.getMorphableImage().getBufferedImage(), sourcePanel.getPoints(), destPanel.getPoints());
                    emf.renderFrames();
                    emf.dispatchEvent(new WindowEvent(emf, WindowEvent.WINDOW_CLOSING));
                }
            }
        });
        fileMenu.add(newItem);
        fileMenu.add(openSrcItem);
        fileMenu.add(openDestItem);
        fileMenu.add(openItem);

        fileMenu.addSeparator();

        fileMenu.add(saveItem);

        fileMenu.addSeparator();

        fileMenu.add(exportSubmenu);
        exportSubmenu.add(exportJPEGItem);
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

        // Menu for allowing users to adjust control grid resolution
        resolutionItem = new JMenuItem("Control Grid Resolution");
        resolutionItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // make another frame pop-up for slider adjustment
                adjFrame = new JFrame("Adjust Control Grid Resolution");
                adjFrame.setLayout(new GridLayout(2, 0));
                adjPanel = new JPanel();

                //setting layout on the adjPanel
                gridLayout = new GridLayout(0,2);
                adjPanel.setLayout(gridLayout);

                // adding labels overtop the sliders
                adjPanel.add(xResLabel);
                adjPanel.add(yResLabel);

                //adding the sliders to the frame/panel
                adjPanel.add(controlPointXResolutionSlider);
                adjPanel.add(controlPointYResolutionSlider);
                adjFrame.getContentPane().add(adjPanel);

                //adjFrame.setSize(745, 470);
                adjFrame.pack();
                adjFrame.setVisible(true);

                controlPointXResolutionSlider.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        xResLabel.setText("X Resolution: " + controlPointXResolutionSlider.getValue());
                    }
                });

                controlPointYResolutionSlider.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        yResLabel.setText("Y Resolution: " + controlPointYResolutionSlider.getValue());
                    }
                });

                // Add a button that will apply the changes
                JButton applyChanges = new JButton("Apply");
                applyChanges.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        gridWidth = controlPointXResolutionSlider.getValue();
                        gridHeight = controlPointYResolutionSlider.getValue();
                        resetFrame();
                        adjFrame.dispatchEvent(new WindowEvent(adjFrame, WindowEvent.WINDOW_CLOSING));
                    }
                });
                adjFrame.add(applyChanges);
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
                        destPanel.getMorphableImage().getBufferedImage(), sourcePanel.getPoints(), destPanel.getPoints());
                previewFrame.setVisible(true);
            }
        });
        animationMenu.add(previewItem);
        animationMenu.add(adjItem);
        animationMenu.add(resolutionItem);

        add(menuBar, BorderLayout.NORTH);
    }
}
