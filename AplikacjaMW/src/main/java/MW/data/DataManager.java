package MW.data;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DataManager {

    int meshSize;
    int cellSize;
    Cell[][] cellMatrix;

    public DataManager(){
        this.cellSize = 10;
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

    public void changeMatrixCell(int x, int y) {
        if (x >= 0 && x < cellMatrix.length && y >= 0 && y < cellMatrix[0].length)
            cellMatrix[x][y].isActive = !cellMatrix[x][y].isActive();
    }

    public void drawFillMatrixCell(int x, int y, boolean isActive) {
        if (x >= 0 && x < cellMatrix.length && y >= 0 && y < cellMatrix[0].length)
            cellMatrix[x][y].isActive = isActive;
    }

    public void cellNeighborhood() {

        int neighborsAlive;
        Cell[][] newCells = new Cell[meshSize][meshSize];
        for (int i = 0; i < meshSize; i++)
            for (int j = 0; j < meshSize; j++) {
                newCells[i][j] = new Cell(false, Color.WHITE);
            }

        for (int i = 0; i < meshSize; i++)
            for (int j = 0; j < meshSize; j++) {

                neighborsAlive = 0;

                for (int m = -1; m <= 1; m++)
                    for (int n = -1; n <= 1; n++) {

                        int x = i + m;
                        int y = j + n;

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

                        neighborsAlive += cellMatrix[x][y].isActive() ? 1 : 0;

                    }

                // new cell is born
                if (!cellMatrix[i][j].isActive() && neighborsAlive == 3)
                    newCells[i][j].isActive = true;
                    // cell in crowd dies
                else if (cellMatrix[i][j].isActive() && neighborsAlive > 3)
                    newCells[i][j].isActive = false;
                    // lonely cell dies
                else if (cellMatrix[i][j].isActive() && neighborsAlive < 2)
                    newCells[i][j].isActive = false;
                else
                    newCells[i][j] = cellMatrix[i][j];
            }

        cellMatrix = newCells;
    }

}