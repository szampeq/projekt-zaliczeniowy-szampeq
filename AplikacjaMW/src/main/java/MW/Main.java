package MW;
/*
 Modelowanie Wieloskalowe (AGH)
         @author  Krzysztof Gąciarz IO
 */

import MW.data.DataManager;
import MW.data.JCanvasPanel;
import MW.elements.Button;
import MW.elements.ComboNum;
import MW.elements.ComboText;
import MW.elements.Label;
import MW.enums.BCs;
import MW.enums.Neighborhoods;
import MW.enums.Nucleations;
import MW.tools.PatternTXT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.cert.CertificateParsingException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Main extends JFrame {

    static DataManager dm;
    private final JPanel buttonPanel;
    private final JPanel mainPanel;
    private static JCanvasPanel canvasPanel;

    public Main(String title){
        super(title);

        // ===========================      MAIN      ==========================

        dm = new DataManager();
        canvasPanel = new JCanvasPanel(dm);
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        buttonPanel = new JPanel();
        program();

        // =========== MAIN PANEL ==========

        mainPanel.add(BorderLayout.EAST, buttonPanel);
        buttonPanel.setBackground(Color.WHITE);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);

        this.setSize(new Dimension(1920, 1080));
        this.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
    }

    // ======================= program main =======================

    public static void main(String[] args) {
        Main mw = new Main("Modelowanie Wieloskalowe");
        mw.setVisible(true);
    }

    public void program(){

        buttonPanel.setLayout(new GridLayout(14, 2));

        // ================= Window Size ==================

        Label meshLabel1 = new Label("Długość okna", buttonPanel);
        ComboNum meshSizeX = new ComboNum(1, 1000, buttonPanel);
        Label meshLabel2 = new Label("Wysokość okna", buttonPanel);
        ComboNum meshSizeY = new ComboNum(1, 1000, buttonPanel);

        // ================= Boundary Conditions ==================

        Label boundaryLabel = new Label("Warunki brzegowe:", buttonPanel);
        ComboText boundaryTypes = new ComboText(Stream.of(BCs.values()).map(BCs::name).toArray(String[]::new), buttonPanel);

        // ================= Nucleation ==================

        Label nucleationLabel = new Label("Zarodkowanie:", buttonPanel);
        ComboText nucleationTypes = new ComboText(Stream.of(Nucleations.values()).map(Nucleations::name).toArray(String[]::new), buttonPanel);
        Label nucleationRandomGrainsLabel = new Label("Losowe - liczba ziaren:", buttonPanel);
        ComboNum nucleationRandomGrains = new ComboNum(1, 50, buttonPanel);
        Label nucleationRadiusGrainsLabel = new Label("Z promieniem - ziarna:", buttonPanel);
        ComboNum nucleationRadiusGrains = new ComboNum(1, 50, buttonPanel);
        Label nucleationRadiusValueLabel = new Label("Z promieniem - promień:", buttonPanel);
        ComboNum nucleationRadiusValue = new ComboNum(1, 20, buttonPanel);
        Label nucleationHomogenousXLabel = new Label("Jednorodne - ziarna po X:", buttonPanel);
        ComboNum nucleationHomogenousX = new ComboNum(1, 20, buttonPanel);
        Label nucleationHomogenousYLabel = new Label("Jednorodne - ziarna po Y:", buttonPanel);
        ComboNum nucleationHomogenousY = new ComboNum(1, 20, buttonPanel);

        // ================= Neighborhood ==================

        Label neighborhoodLabel = new Label("Sąsiedztwo:", buttonPanel);
        ComboText neighborhoodTypes = new ComboText(Stream.of(Neighborhoods.values()).map(Neighborhoods::name).toArray(String[]::new), buttonPanel);
        Label neighborhoodRandomLabel = new Label("Losowe z promieniem:", buttonPanel);
        ComboNum radiusNeighborhood = new ComboNum(1, 20, buttonPanel);

        // ================= Mesh & Board Options ==================

        AtomicInteger selectedMeshX = new AtomicInteger();
        AtomicInteger selectedMeshY = new AtomicInteger();

        MW.elements.Button clear = new MW.elements.Button("Wyczyść planszę", buttonPanel);
        clear.button.addActionListener(e -> {
            canvasPanel.dataManager.zeroMatrix();
            canvasPanel.repaint();
        });

        AtomicBoolean isBoardCreated = new AtomicBoolean(false);

        MW.elements.Button board = new MW.elements.Button("Stwórz planszę", buttonPanel);
        board.button.addActionListener(e -> {
            // ====== MESH SIZE ======
            selectedMeshX.set((Integer) meshSizeX.comboBox.getSelectedItem());
            canvasPanel.dataManager.setMeshSizeX(selectedMeshX.intValue());
            selectedMeshY.set((Integer) meshSizeY.comboBox.getSelectedItem());
            canvasPanel.dataManager.setMeshSizeY(selectedMeshY.intValue());

            // ====== CALCULATE CELL SIZE ======
            int cellSize = 1000 / (Integer) meshSizeX.comboBox.getSelectedItem();
            canvasPanel.dataManager.setCellSize(cellSize);

            // ====== SETUP ======
            dm.setup(Neighborhoods.valueOf((String)neighborhoodTypes.comboBox.getSelectedItem()),
                    BCs.valueOf((String) boundaryTypes.comboBox.getSelectedItem()),
                    Nucleations.valueOf((String) nucleationTypes.comboBox.getSelectedItem()),
                    (Integer) nucleationRandomGrains.comboBox.getSelectedItem(),
                    (Integer) nucleationRadiusGrains.comboBox.getSelectedItem(),
                    (Integer) nucleationRadiusValue.comboBox.getSelectedItem(),
                    (Integer) nucleationHomogenousX.comboBox.getSelectedItem(),
                    (Integer) nucleationHomogenousY.comboBox.getSelectedItem(),
                    (Integer) radiusNeighborhood.comboBox.getSelectedItem());


            // SET MATRIX
            canvasPanel.dataManager.fillMatrix();
            isBoardCreated.set(true);
            // REPAINT
            canvasPanel.repaint();
        });

        // ================= Files Options ==================

        Button saveButton = new MW.elements.Button("Zapisz do pliku", buttonPanel);
        saveButton.button.addActionListener(e -> {
            if (isBoardCreated.get())
                PatternTXT.savePattern(canvasPanel.dataManager);
        });

        MW.elements.Button openButton = new MW.elements.Button("Wczytaj z pliku", buttonPanel);
        JFileChooser pattern = new JFileChooser(new File("src/main/resources/patterns/"));

        openButton.button.addActionListener(e -> {
            int result = pattern.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = pattern.getSelectedFile();
                String patternPath = selectedFile.getAbsolutePath();
                try {
                    canvasPanel.dataManager.setMatrix(PatternTXT.openPattern(patternPath));
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
            }
        });

        // ================= Simulation ==================

        MW.elements.Button startBtn = new MW.elements.Button("Start!", buttonPanel);
        AtomicBoolean isStarted = new AtomicBoolean(false);

        // =============== THREAD TO RUN SIMULATION ================

        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {

            try {
                if (isStarted.get()) {
                    canvasPanel.dataManager.cellNeighborhood();
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        }, 0, 500, TimeUnit.MILLISECONDS);


        // ========================================================

        startBtn.button.addActionListener(e -> {
            if (isBoardCreated.get()) {
                isStarted.set(true);
            }
        });

        MW.elements.Button stopBtn = new MW.elements.Button("Stop!", buttonPanel);

        stopBtn.button.addActionListener(e -> isStarted.set(false));

        // ===================================

        mainPanel.add(canvasPanel);

        // ===================      MOUSE LISTENERS      ====================
        final int windowXCorrection = 7;
        final int windowYCorrection = 30;

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() - (windowXCorrection + canvasPanel.dataManager.getCellSize());
                int y = e.getY() - (windowYCorrection + canvasPanel.dataManager.getCellSize());

                int cellX = x/ canvasPanel.dataManager.getCellSize();
                int cellY = y/ canvasPanel.dataManager.getCellSize();

                if (isBoardCreated.get())
                    canvasPanel.dataManager.createMatrixCell(cellX, cellY);
            }

        });

    }

}
