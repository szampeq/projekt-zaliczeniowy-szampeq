package MW.data;

import MW.enums.BCs;
import MW.enums.Neighborhoods;
import MW.enums.Nucleations;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static MW.data.NeighborhoodMatrix.neighborhoodMatrix;

public class DataManager {

    int meshSizeX;
    int meshSizeY;
    int cellSize;

    Cell[][] cellMatrix;
    int[][] neighborhoodMatrix;

    BCs selectedBCs;
    Nucleations nucleations;
    Neighborhoods neighborhoodType;

    int nucleationRandomGrains;
    int nucleationRadiusGrains;
    int nucleationRadiusValue;
    int nucleationHomogenousX;
    int nucleationHomogenousY;
    int neighborhoodRandomRadius;

    final Random r = new Random();

    public DataManager(){
        this.cellSize = 10;
    }

    public void setup(Neighborhoods neighborhoods, BCs bcs, Nucleations nucleations, int nucleationRandomGrains,
                      int nucleationRadiusGrains, int nucleationRadiusValue, int nucleationHomogenousX,
                      int nucleationHomogenousY, int neighborhoodRandomRadius) {
        if (neighborhoods != Neighborhoods.Radius)
            neighborhoodMatrix = neighborhoodMatrix(neighborhoods);
        else
            neighborhoodMatrix = NeighborhoodMatrix.radiusMatrix(neighborhoodRandomRadius);
        this.selectedBCs = bcs;
        this.nucleations = nucleations;
        this.neighborhoodType = neighborhoods;
        this.nucleationRandomGrains = nucleationRandomGrains;
        this.nucleationRadiusGrains = nucleationRadiusGrains;
        this.nucleationRadiusValue = nucleationRadiusValue;
        this.nucleationHomogenousX = nucleationHomogenousX;
        this.nucleationHomogenousY = nucleationHomogenousY;
        this.neighborhoodRandomRadius = neighborhoodRandomRadius;
    }

    public int getMeshSizeX() {
        return meshSizeX;
    }

    public void setMeshSizeX(int meshSizeX) {
        this.meshSizeX = meshSizeX;
    }

    public int getMeshSizeY() {
        return meshSizeY;
    }

    public void setMeshSizeY(int meshSizeY) {
        this.meshSizeY = meshSizeY;
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
        cellMatrix = new Cell[meshSizeX][meshSizeY];
        zeroMatrix();

        switch (nucleations) {
            case Random:
                randomMatrix();
                break;
            case Radius:
                radiusMatrix();
                break;
            case Homogeneous:
                homogenousMatrix();
                break;
            default:
                zeroMatrix();
        }
    }

    public void zeroMatrix() {
        for (int i = 0; i < meshSizeX; i++)
            for (int j = 0; j < meshSizeY; j++) {
                cellMatrix[i][j] = new Cell(false, Color.WHITE);
            }
    }

    void homogenousMatrix() {
        int fixedX = meshSizeX/nucleationHomogenousX;
        int fixedY = meshSizeY/nucleationHomogenousY;

        for (int i = 0; i < nucleationHomogenousX; i++)
            for (int j = 0; j < nucleationHomogenousY; j++) {
                int x = fixedX/2 + fixedX*i;
                int y = fixedY/2 + fixedY*j;
                if (!cellMatrix[x][y].isActive()) {
                    cellMatrix[x][y].setActive(true);
                    cellMatrix[x][y].setColor(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256), r.nextInt(256)));
                }
            }
    }

    void radiusMatrix() {
        int Nmax = 1000;
        int borned = 0;

        for (int i = 0; i < Nmax; i++) {
            int randomX = ThreadLocalRandom.current().nextInt(0, meshSizeX - 1);
            int randomY = ThreadLocalRandom.current().nextInt(0, meshSizeY - 1);

            if (!checkRadius(randomX, randomY, nucleationRadiusValue)) {
                cellMatrix[randomX][randomY].born();
                borned++;
            }

            if (borned >= nucleationRadiusGrains)
                break;
        }
    }

    boolean checkRadius(int xCenter, int yCenter, int radius) {

        for (int x = xCenter - radius; x <= xCenter; x++)
            for (int y = yCenter - radius; y <= yCenter; y++)
            {
                // we don't have to take the square root, it's slow
                if ((x - xCenter)*(x - xCenter) + (y - yCenter)*(y - yCenter) <= radius*radius)
                {

                    int xSym = xCenter - (x - xCenter);
                    int ySym = yCenter - (y - yCenter);
                    // (x, y), (x, ySym), (xSym , y), (xSym, ySym) are in the circle

                    if (x < 0 || y < 0 || xSym < 0 || ySym < 0)
                        continue;
                    if (x > meshSizeX-1 || xSym > meshSizeX-1 || y > meshSizeY-1 || ySym > meshSizeY-1)
                        continue;

                    if (cellMatrix[x][y].isActive())
                        return true;
                    if (cellMatrix[x][ySym].isActive())
                        return true;
                    if (cellMatrix[xSym][y].isActive())
                        return true;
                    if (cellMatrix[xSym][ySym].isActive())
                        return true;
                }
            }
        return false;
    }

    void randomMatrix() {
        for (int i = 0; i < nucleationRandomGrains; i++) {
            int randomX = ThreadLocalRandom.current().nextInt(0, meshSizeX-1);
            int randomY = ThreadLocalRandom.current().nextInt(0, meshSizeY-1);

            if (!cellMatrix[randomX][randomY].isActive()) {
                cellMatrix[randomX][randomY].born();
            }
        }
    }

    public void createMatrixCell(int x, int y) {
        if (x >= 0 && x < cellMatrix.length && y >= 0 && y < cellMatrix[0].length && !cellMatrix[x][y].isActive()) {
            cellMatrix[x][y].born();
        }
    }

    public void cellNeighborhood() {

        // ====== matrix to store newer data ======
        Cell[][] newCells = new Cell[meshSizeX][meshSizeY];
        for (int i = 0; i < meshSizeX; i++)
            for (int j = 0; j < meshSizeY; j++) {
                newCells[i][j] = new Cell(false, Color.WHITE);
            }

        int leftNeigh = -neighborhoodMatrix.length / 2;
        int rightNeigh = -leftNeigh;

        // ====== cell validation ======
        for (int i = 0; i < meshSizeX; i++)
            for (int j = 0; j < meshSizeY; j++) {

                // ====== cell neighbors ======
                List<Cell> activeNeighbors = new ArrayList<>();

                if (!cellMatrix[i][j].isActive())
                for (int m = leftNeigh; m <= rightNeigh; m++) {
                    for (int n = leftNeigh; n <= rightNeigh; n++) {

                        int x = i + m;
                        int y = j + n;

                        /* Random Neighborhood */
                        if (neighborhoodType == Neighborhoods.RandomHex || neighborhoodType == Neighborhoods.RandomPen)
                            neighborhoodMatrix = neighborhoodMatrix(neighborhoodType);

                        if (m + rightNeigh > neighborhoodMatrix.length-1 || n + rightNeigh > neighborhoodMatrix.length-1)
                            continue;
                        if (neighborhoodMatrix[m+rightNeigh][n+rightNeigh] == 0)
                            continue;

                        if (x == i && y == j)
                            continue;
                        /* BOUNDARY CONDITIONS */
                        if (selectedBCs == BCs.PERIODIC) {
                            if (x < 0)
                                x += meshSizeX;
                            if (y < 0)
                                y += meshSizeY;
                            if (x > meshSizeX - 1)
                                x -= meshSizeX;
                            if (y > meshSizeY - 1)
                                y -= meshSizeY;
                        } else {
                            if (x < 0 || y < 0 || x > meshSizeX - 1 || y > meshSizeY - 1)
                                continue;
                        }


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

    // can be shorten (only hashmap instead of mix list + hashmap)
    Cell dominantCell(List<Cell> cellList) {

        if (cellList.size() == 1)
           return cellList.get(0);

        HashMap<Color, Integer> colors = new HashMap<Color, Integer>();

        for (Cell cell : cellList) {
            Color cellColor = cell.getColor();

            colors.merge(cellColor, 1, Integer::sum);
        }

        Map.Entry<Color, Integer> maxEntry = null;

        for (Map.Entry<Color, Integer> entry : colors.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }

        for (Cell cell : cellList) {
            Color cellColor = cell.getColor();
            if (cellColor == Objects.requireNonNull(maxEntry).getKey())
                return cell;
        }

        return cellList.get(0);

    }

}