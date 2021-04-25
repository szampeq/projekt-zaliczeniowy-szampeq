package MW.data;

import MW.enums.Neighborhoods;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static MW.data.NeighborhoodMatrix.neighborhoodMatrix;

public class DataManager {

    int meshSize;
    int cellSize;
    Cell[][] cellMatrix;
    int[][] neighborhoodMatrix;
    final Random r = new Random();

    public DataManager(){
        this.cellSize = 10;
    }

    public void setup(Neighborhoods neighborhoods) {
        neighborhoodMatrix = neighborhoodMatrix(neighborhoods);
    }

    public int getMeshSize() {
        return meshSize;
    }

    public void setMeshSize(int meshSize) {
        this.meshSize = meshSize;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public Cell[][] getMatrix(){
        return cellMatrix;
    }

    public void setMatrix(Cell[][] cellMatrix){
        this.cellMatrix = cellMatrix;
    }

    public void fillMatrix() {
        cellMatrix = new Cell[meshSize][meshSize];
        zeroMatrix();
    }

    public void zeroMatrix() {
        for (int i = 0; i < meshSize; i++)
            for (int j = 0; j < meshSize; j++) {
                cellMatrix[i][j] = new Cell(false, Color.WHITE);
            }
    }

    public void createMatrixCell(int x, int y) {
        if (x >= 0 && x < cellMatrix.length && y >= 0 && y < cellMatrix[0].length && !cellMatrix[x][y].isActive()) {
            cellMatrix[x][y].setActive(true);
            cellMatrix[x][y].setColor(new Color(r.nextInt(256),r.nextInt(256),r.nextInt(256),r.nextInt(256)));
        }
    }

    public void drawFillMatrixCell(int x, int y, boolean isActive) {
        if (x >= 0 && x < cellMatrix.length && y >= 0 && y < cellMatrix[0].length) {
            cellMatrix[x][y].isActive = isActive;
            cellMatrix[x][y].setColor(new Color(r.nextInt(256),r.nextInt(256),r.nextInt(256),r.nextInt(256)));
        }
    }

    public void cellNeighborhood() {

        // ====== matrix to store newer data ======
        Cell[][] newCells = new Cell[meshSize][meshSize];
        for (int i = 0; i < meshSize; i++)
            for (int j = 0; j < meshSize; j++) {
                newCells[i][j] = new Cell(false, Color.WHITE);
            }

        // ====== cell validation ======
        for (int i = 0; i < meshSize; i++)
            for (int j = 0; j < meshSize; j++) {

                // ====== cell neighbors ======
                List<Cell> activeNeighbors = new ArrayList<>();
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {

                        int x = i + m;
                        int y = j + n;

                        if (neighborhoodMatrix[m+1][n+1] == 0)
                            continue;

                        if (x == i && y == j)
                            continue;
                        if (x < 0)
                            x += meshSize;
                        if (y < 0)
                            y += meshSize;
                        if (x > meshSize - 1)
                            x -= meshSize;
                        if (y > meshSize - 1)
                            y -= meshSize;

                        if (cellMatrix[x][y].isActive())
                            activeNeighbors.add(cellMatrix[x][y]);
                    }
                }
                if (activeNeighbors.size() > 0)
                    newCells[i][j] = dominantCell(activeNeighbors);
                else
                    newCells[i][j] = cellMatrix[i][j];
            }

        // ======= SHIFT MATRIX =======
        cellMatrix = newCells;
    }

    Cell dominantCell(List<Cell> cellList) {

        if (cellList.size() == 1)
           return cellList.get(0);

        HashMap<Color, Integer> colors = new HashMap<Color, Integer>();

        for (Cell cell : cellList) {
            Color cellColor = cell.getColor();

            colors.merge(cellColor, 1, Integer::sum);
        }

     //   for (Color c : colors.keySet()) {
       //     System.out.println("key: " + c + " value: " + colors.get(c));
     //   }

        return cellList.get(0);

    }

}