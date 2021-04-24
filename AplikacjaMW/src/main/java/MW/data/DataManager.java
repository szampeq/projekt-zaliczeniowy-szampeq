package MW.data;

import java.util.concurrent.ThreadLocalRandom;

public class DataManager {

    int meshSize;
    int cellSize;
    String initialState;
    int[][] matrix;

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

    public String getInitialState() {
        return initialState;
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }

    public int[][] getMatrix(){
        return matrix;
    }

    public void setMatrix(int[][] matrix){
        this.matrix = matrix;
    }

    public void fillMatrix() {
        matrix = new int[meshSize][meshSize];
        zeroMatrix();
    }

    public void zeroMatrix() {
        for (int i = 0; i < meshSize; i++)
            for (int j = 0; j < meshSize; j++)
                matrix[i][j] = 0;
    }

    public void changeMatrixCell(int x, int y) {
        if (x >= 0 && x < matrix.length && y >= 0 && y < matrix[0].length)
            if (matrix[x][y] == 0)
                matrix[x][y] = 1;
            else
                matrix[x][y] = 0;
    }

    public void drawFillMatrixCell(int x, int y, int value) {
        if (x >= 0 && x < matrix.length && y >= 0 && y < matrix[0].length)
            matrix[x][y] = value;
    }

    public void cellNeighborhood() {

        int neighborsAlive;
        int[][] newCells = new int[meshSize][meshSize];

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

                        neighborsAlive += matrix[x][y];

                    }

                // new cell is born
                if (matrix[i][j] == 0 && neighborsAlive == 3)
                    newCells[i][j] = 1;
                    // cell in crowd dies
                else if (matrix[i][j] == 1 && neighborsAlive > 3)
                    newCells[i][j] = 0;
                    // lonely cell dies
                else if (matrix[i][j] == 1 && neighborsAlive < 2)
                    newCells[i][j] = 0;
                else
                    newCells[i][j] = matrix[i][j];
            }

        matrix = newCells;
    }

}