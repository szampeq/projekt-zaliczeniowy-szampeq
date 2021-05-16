package MW;
/*
 Modelowanie Wieloskalowe (AGH)
         @author  Krzysztof Gąciarz IO
 */

import MW.data.Cell;
import MW.data.DataManager;
import MW.data.JCanvasPanel;
import MW.elements.*;
import MW.elements.Button;
import MW.elements.Label;
import MW.enums.BCs;
import MW.enums.Neighborhoods;
import MW.enums.Nucleations;
import MW.tools.PatternTXT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Main extends JFrame {

    static DataManager dm;
    private final JPanel buttonPanelRight;
    private final JPanel buttonPanelLeft;
    private final JPanel mainPanel;
    private static JCanvasPanel canvasPanel;

    public Main(String title){
        super(title);

        // ===========================      MAIN      ==========================

        dm = new DataManager();
        canvasPanel = new JCanvasPanel(dm);
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        buttonPanelRight = new JPanel();
        buttonPanelLeft = new JPanel();
        program();

        // =========== MAIN PANEL ==========

        mainPanel.add(BorderLayout.EAST, buttonPanelRight);
        buttonPanelRight.setBackground(Color.WHITE);

        mainPanel.add(BorderLayout.SOUTH, buttonPanelLeft);
        buttonPanelLeft.setBackground(Color.WHITE);

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

        buttonPanelRight.setLayout(new GridLayout(14, 2));
        buttonPanelLeft.setLayout(new GridLayout(1, 14));

        // ================= Window Size ==================

        Label meshLabel1 = new Label("Długość okna", buttonPanelRight);
        ComboNum meshSizeX = new ComboNum(1, 1000, buttonPanelRight);
        Label meshLabel2 = new Label("Wysokość okna", buttonPanelRight);
        ComboNum meshSizeY = new ComboNum(1, 1000, buttonPanelRight);

        // ================= Boundary Conditions ==================

        Label boundaryLabel = new Label("Warunki brzegowe:", buttonPanelRight);
        ComboText boundaryTypes = new ComboText(Stream.of(BCs.values()).map(BCs::name).toArray(String[]::new), buttonPanelRight);

        // ================= Nucleation ==================

        Label nucleationLabel = new Label("Zarodkowanie:", buttonPanelRight);
        ComboText nucleationTypes = new ComboText(Stream.of(Nucleations.values()).map(Nucleations::name).toArray(String[]::new), buttonPanelRight);
        Label nucleationRandomGrainsLabel = new Label("Losowe - liczba ziaren:", buttonPanelRight);
        ComboNum nucleationRandomGrains = new ComboNum(1, 50, buttonPanelRight);
        Label nucleationRadiusGrainsLabel = new Label("Z promieniem - ziarna:", buttonPanelRight);
        ComboNum nucleationRadiusGrains = new ComboNum(1, 50, buttonPanelRight);
        Label nucleationRadiusValueLabel = new Label("Z promieniem - promień:", buttonPanelRight);
        ComboNum nucleationRadiusValue = new ComboNum(1, 50, buttonPanelRight);
        Label nucleationHomogenousXLabel = new Label("Jednorodne - ziarna po X:", buttonPanelRight);
        ComboNum nucleationHomogenousX = new ComboNum(1, 50, buttonPanelRight);
        Label nucleationHomogenousYLabel = new Label("Jednorodne - ziarna po Y:", buttonPanelRight);
        ComboNum nucleationHomogenousY = new ComboNum(1, 50, buttonPanelRight);

        // ================= Neighborhood ==================

        Label neighborhoodLabel = new Label("Sąsiedztwo:", buttonPanelRight);
        ComboText neighborhoodTypes = new ComboText(Stream.of(Neighborhoods.values()).map(Neighborhoods::name).toArray(String[]::new), buttonPanelRight);
        Label neighborhoodRandomLabel = new Label("Z promieniem:", buttonPanelRight);
        ComboNum radiusNeighborhood = new ComboNum(1, 50, buttonPanelRight);

        // ================= Mesh & Board Options ==================

        AtomicInteger selectedMeshX = new AtomicInteger();
        AtomicInteger selectedMeshY = new AtomicInteger();

        MW.elements.Button clear = new MW.elements.Button("Wyczyść planszę", buttonPanelRight);
        clear.button.addActionListener(e -> {
            Cell.resetNumber();
            canvasPanel.dataManager.zeroMatrix();
            canvasPanel.repaint();
        });

        AtomicBoolean isBoardCreated = new AtomicBoolean(false);

        MW.elements.Button board = new MW.elements.Button("Stwórz planszę", buttonPanelRight);
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
            canvasPanel.dataManager.setup(Neighborhoods.valueOf((String)neighborhoodTypes.comboBox.getSelectedItem()),
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

        Button saveButton = new MW.elements.Button("Zapisz do pliku", buttonPanelRight);
        saveButton.button.addActionListener(e -> {
            if (isBoardCreated.get())
                PatternTXT.savePattern(canvasPanel.dataManager);
        });

        MW.elements.Button openButton = new MW.elements.Button("Wczytaj z pliku", buttonPanelRight);
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

        MW.elements.Button startBtn = new MW.elements.Button("Start!", buttonPanelRight);
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

        }, 0, 100, TimeUnit.MILLISECONDS);


        // ========================================================

        startBtn.button.addActionListener(e -> {
            if (isBoardCreated.get()) {
                isStarted.set(true);
            }
        });

        MW.elements.Button stopBtn = new MW.elements.Button("Stop!", buttonPanelRight);

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

                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (isBoardCreated.get()) {
                        canvasPanel.dataManager.createMatrixCell(cellX, cellY);
                    }
                }
                else if (SwingUtilities.isRightMouseButton(e))
                    canvasPanel.dataManager.printInfoAboutCell(cellX, cellY);
            }

        });

        // ================= Monte Carlo ==================
        Label monteCarlo = new Label("- Monte Carlo -", buttonPanelLeft);
        Label monteIterations = new Label("Liczba iteracji:", buttonPanelLeft);
        ComboNum monteIterationsNumber = new ComboNum(1, 200, buttonPanelLeft);
        Label monteKt = new Label("Współczynnik Kt:", buttonPanelLeft);
        ComboDecimal monteKtNumber = new ComboDecimal(0, 5, buttonPanelLeft);
        Label monteControllers = new Label("Kontrola:", buttonPanelLeft);
        Button monteSetup = new MW.elements.Button("Ustaw", buttonPanelLeft);
        Button monteStart = new MW.elements.Button("Start", buttonPanelLeft);
        Button monteStop = new MW.elements.Button("Stop", buttonPanelLeft);
        Label monteBoards = new Label("Plansze:", buttonPanelLeft);
        Button monteMapButton = new MW.elements.Button("Energia", buttonPanelLeft);
        Button monteStructureButton = new MW.elements.Button("Struktura", buttonPanelLeft);
        Label projectLabel = new Label("Projekt MW", buttonPanelLeft);
        Label nameLabel = new Label("Krzysztof Gąciarz", buttonPanelLeft);

        monteSetup.button.addActionListener(e ->
                canvasPanel.dataManager.setMonteCarlo((Double)monteKtNumber.comboBox.getSelectedItem(), (Integer)monteIterationsNumber.comboBox.getSelectedItem())
        );

        monteMapButton.button.addActionListener(e -> canvasPanel.energyMap = true);
        monteStructureButton.button.addActionListener(e -> canvasPanel.energyMap = false);

        // ================= Monte Carlo Simulation ==================

        // Lambda Runnable
        Runnable mcTask = () -> {
            try {
                canvasPanel.dataManager.monteCarlo();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        AtomicReference<Thread> MC = new AtomicReference<>(new Thread(mcTask));

        AtomicBoolean isWorking = new AtomicBoolean();
        isWorking.set(false);

        monteStart.button.addActionListener(e -> {
            if (!isWorking.get()) {
                MC.set(new Thread(mcTask));

                if (!isStarted.get()) {
                    MC.get().start();
                    isWorking.set(true);
                }
            }
            else
                MC.get().interrupt();
        });

        monteStop.button.addActionListener(e -> {
            MC.get().interrupt();
            isWorking.set(false);
        });

    }
}
