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

    private JSlider swingSliderFps, swingSliderTime, controlPointXResolutionSlider, controlPointYResolutionSlider,
    srcIntensitySlider, destIntensitySlider;
    private JFrame adjFrame; // frame used for fps and time adjustments
    private JPanel adjPanel;
    private GridBagLayout gridLayout;
    private JLabel fpsLabel, timeLabel, xResLabel, yResLabel, srcIntensityLabel, destIntensityLabel, totalFramesLabel;
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
        initSliders();
        initFrame();

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

        constraints.weightx = 1;
        constraints.weighty = 1;

        constraints.gridx = 0;
        constraints.gridy = 0;
        mainPanel.add(sourcePanel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        mainPanel.add(destPanel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        mainPanel.add(srcIntensitySlider, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        mainPanel.add(srcIntensityLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        mainPanel.add(destIntensitySlider, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        mainPanel.add(destIntensityLabel, constraints);

    }

    /* This function initializes the sliders.
     */
    public void initSliders()
    {
        // Slider init:

        // setting labels
        fpsLabel = new JLabel("FPS: 30");
        timeLabel = new JLabel("Time: 3 second(s)");
        totalFramesLabel = new JLabel("Total Frames: 90");
        xResLabel = new JLabel("X Resolution: 10");
        yResLabel = new JLabel("Y Resolution: 10");
        srcIntensityLabel = new JLabel("Adjust Source Intensity");
        destIntensityLabel = new JLabel("Adjust Destination Intensity");

        // Intensity slider init
        srcIntensitySlider = new JSlider(JSlider.HORIZONTAL, 1, 2000, 1000);
        srcIntensitySlider.setPaintTicks(false);
        srcIntensitySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sourcePanel.applyIntensity(srcIntensitySlider.getValue());
            }
        });

        // Intensity slider init
        destIntensitySlider = new JSlider(JSlider.HORIZONTAL, 1, 2000, 1000);
        destIntensitySlider.setPaintTicks(false);
        destIntensitySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                destPanel.applyIntensity(destIntensitySlider.getValue());
            }
        });

        //fps slider init
        swingSliderFps = new JSlider(JSlider.HORIZONTAL, 1, 60, 30);
        swingSliderFps.setLayout(new BorderLayout());
        swingSliderFps.setPaintLabels(true);
        swingSliderFps.setPaintTicks(true);
        swingSliderFps.setPreferredSize(new Dimension(200,30));
        swingSliderFps.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                fps = swingSliderFps.getValue();
                totalFramesLabel.setText("Total Frames: " + Integer.toString(fps * aniTime));
            }
        });
        fps = 30;

        // time slider init
        swingSliderTime = new JSlider(JSlider.HORIZONTAL, 1, 5, 3);
        swingSliderTime.setLayout(new BorderLayout());
        swingSliderTime.setPaintLabels(true);
        swingSliderTime.setPaintTicks(true);
        swingSliderTime.setPreferredSize(new Dimension(200,30));
        swingSliderTime.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                aniTime = swingSliderTime.getValue();
                totalFramesLabel.setText("Total Frames: " + Integer.toString(fps * aniTime));
            }
        });
        aniTime = 3;

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
                fileChooser.setCurrentDirectory(new java.io.File("."));
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
                fileChooser.setCurrentDirectory(new java.io.File("."));
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
                        boolean[][] movedPoints = new boolean[numX][numY];
                        boolean[][] partnerMovedSrcPoints = new boolean[numX][numY];
                        boolean[][] partnerMovedDestPoints = new boolean[numX][numY];

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
                                movedPoints[i][j] = Boolean.parseBoolean(token.nextToken());
                                partnerMovedDestPoints[i][j] = Boolean.parseBoolean(token.nextToken());
                                newSourcePoints[i][j] = new Point(curX, curY);
                                //writer.println(gridPoints[i][j].getControlPoint().x + " " + gridPoints[i][j].getControlPoint().y);
                            }
                        }
                        sourcePanel.setPoints(newSourcePoints);
                        for (int i = 0; i < numX; i++)
                        {
                            for (int j = 0; j < numY; j++) {
                                sourcePanel.getPoint(i, j).setMoved(movedPoints[i][j]);
                            }
                        }
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
                                movedPoints[i][j] = Boolean.parseBoolean(token.nextToken());
                                partnerMovedSrcPoints[i][j] = Boolean.parseBoolean(token.nextToken());
                                newDestPoints[i][j] = new Point(curX, curY);
                                //writer.println(gridPoints[i][j].getControlPoint().x + " " + gridPoints[i][j].getControlPoint().y);
                            }
                        }
                        for (int i = 0; i < numX; i++)
                        {
                            for (int j = 0; j < numY; j++) {
                                destPanel.getPoint(i, j).setMoved(movedPoints[i][j]);
                                sourcePanel.getPoint(i, j).setPartnerMoved(partnerMovedSrcPoints[i][j]);
                                destPanel.getPoint(i, j).setPartnerMoved(partnerMovedDestPoints[i][j]);
                            }
                        }
                        destPanel.setPoints(newDestPoints);

                        // Set the FPS and animation time
                        buffReader.readLine();
                        line = buffReader.readLine();
                        token = new StringTokenizer(line);
                        fps = Integer.parseInt(token.nextToken());
                        line = buffReader.readLine();
                        token = new StringTokenizer(line);
                        aniTime = Integer.parseInt(token.nextToken());
                        swingSliderFps.setValue(fps);
                        swingSliderTime.setValue(aniTime);

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
                fileSelector.setCurrentDirectory(new java.io.File("./"));
                fileSelector.setSelectedFile(new File("savefile.txt"));
                fileSelector.setDialogTitle("Save Control Point Information");
                // only allowing the user to choose directories
                fileSelector.setApproveButtonText("Save");
                fileSelector.setAcceptAllFileFilterUsed(false);
                if (fileSelector.showOpenDialog(mainPanel) == JFileChooser.APPROVE_OPTION)
                {

                    System.out.println("getSelectedFile() : "
                            +  fileSelector.getSelectedFile()); // selected "file" actually grabs the directory the user selected
                    try {
                        writer = new PrintWriter(fileSelector.getSelectedFile(), "UTF-8");
                        GridPoint[][] srcGridPoints = sourcePanel.getPoints();
                        GridPoint[][] destGridPoints = destPanel.getPoints();

                        // for every grid point in the gridPoints array
                        writer.println(srcGridPoints.length + " " + srcGridPoints[0].length);
                        for (int i = 0; i < srcGridPoints.length; i++)
                        {
                            for (int j = 0; j < srcGridPoints[0].length; j++)
                            {
                                writer.println(srcGridPoints[i][j].getPointLocation().x + " " + srcGridPoints[i][j].getPointLocation().y + " " +
                                        srcGridPoints[i][j].getMoved() + " " + destGridPoints[i][j].getPartnerMoved());
                            }
                        }
                        writer.println("DEST");

                        for (int i = 0; i < destGridPoints.length; i++)
                        {
                            for (int j = 0; j < destGridPoints[0].length; j++)
                            {
                                writer.println(destGridPoints[i][j].getPointLocation().x + " " + destGridPoints[i][j].getPointLocation().y + " " +
                                        destGridPoints[i][j].getMoved() + " " + srcGridPoints[i][j].getPartnerMoved());
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
                gridLayout = new GridBagLayout();
                GridBagConstraints c = new GridBagConstraints();
                adjPanel.setLayout(gridLayout);

                // adding labels overtop the sliders
                c.gridx = 0;
                c.gridy = 1;
                adjPanel.add(fpsLabel, c);
                c.gridx = 1;
                c.gridy = 1;
                adjPanel.add(timeLabel, c);

                //adding the sliders to the frame/panel
                c.gridx = 0;
                c.gridy = 0;
                adjPanel.add(swingSliderFps, c);
                c.gridx = 1;
                c.gridy = 0;
                adjPanel.add(swingSliderTime, c);

                // Add the label containing the current frame total
                c.gridx = 0;
                c.gridy = 2;
                c.gridwidth = 2;
                adjPanel.add(totalFramesLabel, c);

                // Add a disclaimer concerning timing
                JLabel disclaimer = new JLabel("Disclaimer: Animation preview may not match your settings," +
                        " depending on the strength of your system.");
                c.gridwidth = 2;
                c.gridx = 0;
                c.gridy = 3;
                adjPanel.add(disclaimer, c);
                adjFrame.getContentPane().add(adjPanel);

                // Add a button that will apply the changes
                c.gridy = 4;
                JButton applyChanges = new JButton("Apply");
                applyChanges.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        adjFrame.dispatchEvent(new WindowEvent(adjFrame, WindowEvent.WINDOW_CLOSING));
                    }
                });
                adjPanel.add(applyChanges, c);

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
                gridLayout = new GridBagLayout();
                GridBagConstraints c = new GridBagConstraints();
                adjPanel.setLayout(gridLayout);

                // adding labels overtop the sliders
                c.gridx = 0;
                c.gridy = 1;
                adjPanel.add(xResLabel, c);
                c.gridx = 1;
                c.gridy = 1;
                adjPanel.add(yResLabel, c);

                //adding the sliders to the frame/panel
                c.gridx = 0;
                c.gridy = 0;
                adjPanel.add(controlPointXResolutionSlider, c);
                c.gridx = 1;
                c.gridy = 0;
                adjPanel.add(controlPointYResolutionSlider, c);
                adjFrame.getContentPane().add(adjPanel);

                //adjFrame.setSize(745, 470);

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

                c.gridx = 0;
                c.gridy = 2;
                c.gridwidth = 2;
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
                adjPanel.add(applyChanges, c);
                adjFrame.pack();
                adjFrame.setVisible(true);
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
