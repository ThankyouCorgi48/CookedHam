package org.aguerra.cookedham.ide.display;

import org.aguerra.cookedham.interpret.run.CookedHam;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CookedHamIDE extends JFrame implements ActionListener {
    private class DirectorySelector extends JFrame implements ActionListener {
        //Class TEK #1
        private CookedHamIDE parent;
        private JFileChooser fileChooser;
        private File directory;
        private String fileName;

        //Class TEK #2
        public DirectorySelector(CookedHamIDE parent) {
            this.parent = parent;

            init();
            setTitle("Select a folder");
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setSize(FILE_SELECTOR_DIMENSION);
            setVisible(true);
        }

        public void init() {
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setApproveButtonText("Select");

            fileChooser.addActionListener(this::actionPerformed);

            getContentPane().add(fileChooser);
        }

        //Class TEk #4
        @Override
        public void actionPerformed(ActionEvent e) {
            parent.setDirectory(fileChooser.getCurrentDirectory());
            this.dispose();
            parent.setFileName(JOptionPane.showInputDialog(this, "Input file name") + ".ch");

            //parent.loadNewFile();
        }

        //Class TEK #3
        public File getDirectory() {
            return directory;
        }

        public String getFileName() {
            return fileName;
        }

        public void setDirectory(File file) {
            this.directory = file;
        }

        public void setFileName(String str) {
            this.fileName = str;
        }
    }

    private class FileSelector extends JFrame implements ActionListener{
        private JFileChooser fileChooser;

        public FileSelector() {
            init();
            setTitle("Select a folder");
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setSize(FILE_SELECTOR_DIMENSION);
            setVisible(true);
        }

        public void init() {
            fileChooser = new JFileChooser();
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CookedHam extension", "ch"));
            fileChooser.setApproveButtonText("Select");

            fileChooser.addActionListener(this::actionPerformed);

            getContentPane().add(fileChooser);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(fileChooser.getCurrentDirectory());
            this.dispose();
            System.out.println(JOptionPane.showInputDialog(this, "Input file name"));
        }
    }

    private Graphics panelGraphics;
    private JPanel mainPanel;
    private MenuBar topMenuBar;
    private Menu exitMenu, fileMenu, runMenu, styleMenu;
    private MenuItem exitItem, safeExitItem, newFileItem, saveFileItem, runItem, textSizeItem;
    private JTextArea codeTextArea;
    private JScrollPane codeTextScroll;

    private final Dimension PANEL_DIMENSION = new Dimension(1800,2000);
    private final Dimension FILE_SELECTOR_DIMENSION = new Dimension(900,1000);
    private final String IDE_NAME = "Cooked Ham IDE";

    private DirectorySelector directorySelector;
    private FileSelector fileSelector;

    private double textSize;
    private File directory;
    private String fileName;

    public CookedHamIDE() {
        init();
        setTitle(IDE_NAME);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(PANEL_DIMENSION);
        setVisible(true);
    }

    public void init() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        exitMenu = new Menu("Exit");
        fileMenu = new Menu("File");
        runMenu = new Menu("Run");

        exitItem = new MenuItem("Exit");
        safeExitItem = new MenuItem("Save & Exit");

        exitItem.addActionListener(this::actionPerformed);
        safeExitItem.addActionListener(this::actionPerformed);

        exitMenu.add(exitItem);
        exitMenu.add(safeExitItem);

        newFileItem = new MenuItem("New File");
        saveFileItem = new MenuItem("Save File");

        newFileItem.addActionListener(this::actionPerformed);
        saveFileItem.addActionListener(this::actionPerformed);

        fileMenu.add(newFileItem);
        fileMenu.add(saveFileItem);

        runItem = new MenuItem("Run");

        runItem.addActionListener(this::actionPerformed);

        runMenu.add(runItem);

        topMenuBar = new MenuBar();
        topMenuBar.add(exitMenu);
        topMenuBar.add(fileMenu);
        topMenuBar.add(runMenu);

        setMenuBar(topMenuBar);

        codeTextArea = new JTextArea();
        codeTextScroll = new JScrollPane(codeTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(codeTextScroll);
        getContentPane().add(mainPanel);

        panelGraphics = getGraphics();
    }

    private void saveFile() {
        System.out.println(codeTextArea.getText());
    }

    private void exitFrame() {
        System.exit(3);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(source == exitItem) {
            exitFrame();
        } else if(source == safeExitItem) {
            saveFile();
            exitFrame();
        } else if(source == newFileItem) {
            if(fileSelector != null) {
                fileSelector.dispose();
                fileSelector = null;
            }

            //Class TEK #5
            directorySelector = new DirectorySelector(this);

        } else if(source == saveFileItem) {
            saveFile();
        } else if(source == runItem) {
            CookedHam cookedHam = new CookedHam();
        }
    }

    @Override
    public void update(Graphics g) {
        super.update(g);
    }

    public File getDirectory() {
        return directory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void loadNewFile(String fileCreator) {
        setTitle(IDE_NAME + " - " + directorySelector.getFileName());

        try {
            //Class TEK #8
            File newFile = new File(directorySelector.getDirectory() + directorySelector.getFileName());

            newFile.createNewFile();
            new FileWriter(newFile).write(fileCreator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CookedHamIDE();
    }
}