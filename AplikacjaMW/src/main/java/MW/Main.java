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
import MW.tools.PatternTXT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.cert.CertificateParsingException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

        this.setSize(new Dimension(820, 710));
        this.setLocationRelativeTo(null);
    }

    // ======================= program main =======================

    public static void main(String[] args) {
        Main mw = new Main("Modelowanie Wieloskalowe");
        mw.setVisible(true);
    }

    public void program(){

        buttonPanel.setLayout(new GridLayout(19, 1));

        // ================= Window Size ==================

        Label meshLabel = new Label("Wymiary okna", buttonPanel);
        ComboNum meshSize = new ComboNum(1, 100, buttonPanel);

        // ================= Grain Size ==================

        Label cellSizeLabel = new Label("Wielkość ziarna:", buttonPanel);
        ComboNum cellSize = new ComboNum(1, 20, buttonPanel);

        // ================= Boundary Conditions ==================

        Label boundaryLabel = new Label("Warunki brzegowe:", buttonPanel);
        ComboText boundaryTypes = new ComboText(new String[]{"Periodyczne", "Absorbujące"}, buttonPanel);

        // ================= Nucleation ==================

        Label nucleationLabel = new Label("Zarodkowanie:", buttonPanel);
        ComboText nucleationTypes = new ComboText(new String[]{"Jednorodne", "Z promieniem", "Wyklikane", "Losowe"}, buttonPanel);

        // ================= Neighborhood ==================

        Label neighborhoodLabel = new Label("Sąsiedztwo:", buttonPanel);
        ComboText neighborhoodTypes = new ComboText(new String[]{"Moore", "Von Neumann", "Left-Hex", "Right-Hex", "Random-Hex", "Random-Pen", "Radius"}, buttonPanel);

        // ================= Mesh & Board Options ==================

        AtomicInteger selectedMesh = new AtomicInteger();
        AtomicInteger selectedCellSize = new AtomicInteger();

        Label boardTools = new Label("Opcje planszy:", buttonPanel);

        MW.elements.Button clear = new MW.elements.Button("Wyczyść", buttonPanel);
        clear.button.addActionListener(e -> {
            canvasPanel.dataManager.zeroMatrix();
            canvasPanel.repaint();
        });

        AtomicBoolean isBoardCreated = new AtomicBoolean(false);

        MW.elements.Button board = new MW.elements.Button("Stwórz", buttonPanel);
        board.button.addActionListener(e -> {
            // MESH SIZE
            selectedMesh.set((Integer) meshSize.comboBox.getSelectedItem());
            canvasPanel.dataManager.setMeshSize(selectedMesh.intValue());
            // CELL SIZE
            selectedCellSize.set((Integer) cellSize.comboBox.getSelectedItem());
            canvasPanel.dataManager.setCellSize(selectedCellSize.intValue());
            // NEIGHBORHOOD
             String selectedNeighborhood = (String) neighborhoodTypes.comboBox.getSelectedItem();
             //canvasPanel.dataManager.setNeighborhood(selectedNeighborhood);
            // SET MATRIX
            canvasPanel.dataManager.fillMatrix();
            //golPanel.golData.generateCA();
            isBoardCreated.set(true);
            // REPAINT
            canvasPanel.repaint();
        });

        // ================= Files Options ==================

        Label patterns = new Label("Zapis/Odczyt:", buttonPanel);

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

        Label setupLabel = new Label("Start/Stop: ", buttonPanel);

        MW.elements.Button startBtn = new MW.elements.Button("Start!", buttonPanel);
        AtomicBoolean isStarted = new AtomicBoolean(false);

        // =============== THREAD TO RUN SIMULATION ================

        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {

            if (isStarted.get()) {
                canvasPanel.dataManager.cellNeighborhood();
            }

        }, 0, 100, TimeUnit.MILLISECONDS);

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
                    canvasPanel.dataManager.changeMatrixCell(cellX, cellY);
            }

        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                super.mouseDragged(e);
                int x = e.getX() - (windowXCorrection + canvasPanel.dataManager.getCellSize());
                int y = e.getY() - (windowYCorrection + canvasPanel.dataManager.getCellSize());

                int cellX = x/ canvasPanel.dataManager.getCellSize();
                int cellY = y/ canvasPanel.dataManager.getCellSize();

                if (isBoardCreated.get()){
                    if (SwingUtilities.isRightMouseButton(e))
                        canvasPanel.dataManager.drawFillMatrixCell(cellX, cellY, false);
                    else if (SwingUtilities.isLeftMouseButton(e))
                        canvasPanel.dataManager.drawFillMatrixCell(cellX, cellY, true);
                }

            }
        });

    }

}
