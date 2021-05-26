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
    List<Point> points;

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

    /* MC FIELDS */
    double kt;
    int numOfIter;

    /* RECRYSTALLIZATION FIELDS */
    double parA;
    double parB;
    double dislDiv;
    double timeStep;
    double packageSize;
    double propability;
    double timeLimit;

    double prevPool;
    double currPool;
    double averagePoolPerCell;
    double deltaPool;

    double criticalValue;

    public List<Double> listOfSteps = new ArrayList<>();
    public List<Double> listOfDislocationPool = new ArrayList<>();
    List<Point> recrystallizedInPreviousStep = new ArrayList<>();
    List<Point> recrystallizedInCurrentStep = new ArrayList<>();

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
        createPointsList();
    }

    public void createPointsList() {
        points = new ArrayList<>();
        for (int i = 0; i < meshSizeX; i++)
            for (int j = 0; j < meshSizeY; j++) {
                points.add(new Point(i, j));
            }
    }

    public void setMonteCarlo(double kt, int numOfIter) {
        this.kt = kt;
        this.numOfIter = numOfIter;
    }

    public void setRecrystallization(double parA, double parB, double dislDiv, double timeStep, double packageSize, double propability, double timeLimit) {
        this.parA = parA;
        this.parB = parB;
        this.dislDiv = dislDiv;
        this.timeStep = timeStep;
        this.packageSize = packageSize;
        this.propability = propability;
        this.timeLimit = timeLimit;
        this.prevPool = 1.0;
        this.currPool = 1.0;
        this.averagePoolPerCell = 0.0;
        this.deltaPool = 0.0;
        this.criticalValue = 4.21584E+12 / (meshSizeX * meshSizeY);
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
                cellMatrix[i][j] = new Cell(false, Color.WHITE, new Point(i, j));
            }
    }

    void homogenousMatrix() {
        int fixedX = meshSizeX/nucleationHomogenousX;
        int fixedY = meshSizeY/nucleationHomogenousY;

        for (int i = 0; i < nucleationHomogenousX; i++)
            for (int j = 0; j < nucleationHomogenousY; j++) {
                int x = fixedX/2 + fixedX*i;
                int y = fixedY/2 + fixedY*j;
                if (!cellMatrix[x][y].isActive())
                    cellMatrix[x][y].born();

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

    public void printInfoAboutCell(int x, int y) {
        System.out.println(
                "Id: " + cellMatrix[x][y].getId() + "\n" +
                "Color: " + cellMatrix[x][y].getColor().getRGB() + "\n" +
                "Energy: " + cellMatrix[x][y].getEnergy() + "\n" +
                "Dislocation: " + cellMatrix[x][y].getDislocation() + "\n" +
                "Recrystallized: " + cellMatrix[x][y].isRecrystallized + "\n"
        );
    }

    public void cellNeighborhood() {

        // ====== matrix to store newer data ======
        Cell[][] newCells = new Cell[meshSizeX][meshSizeY];
        for (int i = 0; i < meshSizeX; i++)
            for (int j = 0; j < meshSizeY; j++) {
                newCells[i][j] = new Cell(false, Color.WHITE, new Point(i, j));
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
                if (activeNeighbors.size() > 0) {
                    newCells[i][j].copyData(dominantCell(activeNeighbors));
                }
                else {
                    newCells[i][j] = cellMatrix[i][j];
                }

            }

        // ======= SHIFT MATRIX =======
        cellMatrix = newCells;
    }

    // can be shorten (only hashmap instead of mix list + hashmap)
    Cell dominantCell(List<Cell> cellList) {

        if (cellList.size() == 1)
           return cellList.get(0);

        HashMap<Color, Integer> colors = new HashMap<>();

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

    Cell getRandomNeighbor(int cx, int cy) {
        int leftNeigh = -neighborhoodMatrix.length / 2;
        int rightNeigh = -leftNeigh;

        List<Cell> neighbors = new ArrayList<>();

        for (int m = leftNeigh; m <= rightNeigh; m++) {
            for (int n = leftNeigh; n <= rightNeigh; n++) {

                int x = cx + m;
                int y = cy + n;

                /* Random Neighborhood */
                if (neighborhoodType == Neighborhoods.RandomHex || neighborhoodType == Neighborhoods.RandomPen)
                    neighborhoodMatrix = neighborhoodMatrix(neighborhoodType);

                if (m + rightNeigh > neighborhoodMatrix.length-1 || n + rightNeigh > neighborhoodMatrix.length-1)
                    continue;
                if (neighborhoodMatrix[m+rightNeigh][n+rightNeigh] == 0)
                    continue;

                if (x == cx && y == cy)
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

                neighbors.add(cellMatrix[x][y]);
            }
        }

        try {
            return neighbors.get(r.nextInt(neighbors.size()));
        } catch (Exception e) {
            return null;
        }
    }

    /* = = = = = = = = = = = MONTE CARLO METHODS = = = = = = = = = = = */

    public void monteCarlo() throws InterruptedException {

            for (int i = 0; i < numOfIter; i++) {
                if (Thread.currentThread().isInterrupted())
                    break;

                Collections.shuffle(points);

            for (Point p : points) {
                /* Current cell energy */
                pointEnergy(p);
                int beforeEnergy = cellMatrix[p.x][p.y].getEnergy();

                /* Random neighbor */
                int currId = cellMatrix[p.x][p.y].getId();
                Color currColor = cellMatrix[p.x][p.y].getColor();

                Cell randomNeighbor = getRandomNeighbor(p.x, p.y);
                if (randomNeighbor == null)
                    continue;

                cellMatrix[p.x][p.y].copyData(randomNeighbor);

                /* New Energy */
                pointEnergy(p);
                int afterEnergy = cellMatrix[p.x][p.y].getEnergy();
                int dE = afterEnergy - beforeEnergy;

                /* Accept changes */
                if (dE > 0) {
                    double chance = Math.exp(-dE / kt);
                    double draw = r.nextDouble();
                    if (draw >= chance) {
                        cellMatrix[p.x][p.y].id = currId;
                        cellMatrix[p.x][p.y].color = currColor;
                        cellMatrix[p.x][p.y].energy = beforeEnergy;
                    }
                }
            }
        }
    }

    public void pointEnergy(Point p) {
        int energy = 0;

        int leftNeigh = -neighborhoodMatrix.length / 2;
        int rightNeigh = -leftNeigh;

        for (int m = leftNeigh; m <= rightNeigh; m++) {
            for (int n = leftNeigh; n <= rightNeigh; n++) {
                int x = p.x + m;
                int y = p.y + n;

                /* Random Neighborhood */
                if (neighborhoodType == Neighborhoods.RandomHex || neighborhoodType == Neighborhoods.RandomPen)
                    neighborhoodMatrix = neighborhoodMatrix(neighborhoodType);

                if (m + rightNeigh > neighborhoodMatrix.length-1 || n + rightNeigh > neighborhoodMatrix.length-1)
                    continue;
                if (neighborhoodMatrix[m+rightNeigh][n+rightNeigh] == 0)
                    continue;

                if (x == p.x && y == p.y)
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

                if (cellMatrix[p.x][p.y].getId() != cellMatrix[x][y].getId())
                    energy++;

            }
        }

        cellMatrix[p.x][p.y].setEnergy(energy);

    }

    /* = = = = = = = = = = = RECRYSTALIZATION METHODS = = = = = = = = = = = */


    private void calcDislocationPool(double currentStep) {
        currPool = (parA / parB) + (1 - (parA/parB)) * Math.exp(-parB * currentStep);

        listOfDislocationPool.add(currPool);

        deltaPool = currPool - prevPool;
        prevPool = currPool;

        averagePoolPerCell = deltaPool / (meshSizeX * meshSizeY);

    }

    private void setDislocationEveryCell(double dislocation) {

        for (int i = 0; i < meshSizeX; i++)
            for (int j = 0; j < meshSizeY; j++) {
                if (cellMatrix[i][j].isRecrystallized)
                    continue;
                cellMatrix[i][j].dislocation += dislocation;
                if (cellMatrix[i][j].dislocation > criticalValue && cellMatrix[i][j].getEnergy() > 0) {
                    cellMatrix[i][j].recrystallization();
                    recrystallizedInCurrentStep.add(new Point(i, j));
                }
            }
    }

    private void dislocationGiveaway(double restPool) {

        double packageD = restPool * (packageSize/100);

        while (restPool > 0) {
            int randomX = ThreadLocalRandom.current().nextInt(0, meshSizeX-1);
            int randomY = ThreadLocalRandom.current().nextInt(0, meshSizeY-1);

            if (cellMatrix[randomX][randomY].isRecrystallized)
                continue;

           // Point testPoint = new Point(randomX, randomY);
            //pointEnergy(testPoint);
            double draw = r.nextDouble();

            if (cellMatrix[randomX][randomY].getEnergy() == 0 && draw < (1 - (propability / 100))) {
                cellMatrix[randomX][randomY].dislocation += packageD;
               /* if (cellMatrix[randomX][randomY].dislocation > criticalValue) {
                    cellMatrix[randomX][randomY].recrystallization();
                    recrystallizedInCurrentStep.add(testPoint);
                }*/
                restPool -= packageD;
            }
            else if (cellMatrix[randomX][randomY].getEnergy() > 0 && draw < (propability / 100)) {
                    cellMatrix[randomX][randomY].dislocation += packageD;
                   /* if (cellMatrix[randomX][randomY].dislocation > criticalValue) {
                        cellMatrix[randomX][randomY].recrystallization();
                        recrystallizedInCurrentStep.add(testPoint);
                    }*/
                restPool -= packageD;
            }
        }
    }

    private void nucletaion() {
        for (int i = 0; i < meshSizeX; i++)
            for (int j = 0; j < meshSizeY; j++) {
                if (cellMatrix[i][j].dislocation > criticalValue && cellMatrix[i][j].getEnergy() > 0) {
                    cellMatrix[i][j].recrystallization();
                    recrystallizedInCurrentStep.add(new Point(i, j));
                }
        }
    }

    private boolean checkNeighborsDislocation(int i, int j) {
        double myValue = cellMatrix[i][j].getDislocation();
        List<Cell> neighbors = new ArrayList<>();
        for (int m = -1; m <= 1; m++) {
            for (int n = -1; n <= 1; n++) {

                int x = i + m;
                int y = j + n;

                if (m + 1 > neighborhoodMatrix.length-1 || n + 1 > neighborhoodMatrix.length-1)
                    continue;
                if (neighborhoodMatrix[m+1][n+1] == 0)
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

                neighbors.add(cellMatrix[x][y]);
            }
        }

        int trues = 0;

        for (Cell c : neighbors) {
            if (c.getDislocation() < myValue)
                trues++;
        }

        return trues == 4;

    }


    private void transitionRules() {

        for (int i = 0; i < meshSizeX; i++)
            for (int j = 0; j < meshSizeY; j++) {
                for (Point p : recrystallizedInPreviousStep) {
                    if ((p.x == i-1 && p.y == j) || (p.x == i+1 && p.y == j) || (p.x == i && p.y == j-1) || (p.x == i && p.y == j+1)) {
                        if(checkNeighborsDislocation(i, j))
                            cellMatrix[i][j].copyData(cellMatrix[p.x][p.y]);
                    }
                }
            }
    }

    public void recrystallization() throws InterruptedException {

        neighborhoodType = Neighborhoods.VonNeumann;
        for (double currentStep = 0.0; currentStep < timeLimit + timeStep; currentStep += timeStep) {

            listOfSteps.add(currentStep);
            /* calculate dislocation pool */
            if (currentStep != 0.0)
                calcDislocationPool(currentStep);
            else
                listOfDislocationPool.add(currPool);

            /* dislocation for every pool and rest pool giveaway */
            double dislocationPoolForEveryCell = averagePoolPerCell * (dislDiv / 100);

            setDislocationEveryCell(dislocationPoolForEveryCell);

            double restDislocationPool = averagePoolPerCell * (1 - (dislDiv/100));
            dislocationGiveaway(restDislocationPool);

            /* DRX Nucleation */
            nucletaion();

            /* DRX Transition */
            if (recrystallizedInPreviousStep.size() > 0)
                transitionRules();

            /* Swap crystal lists */
            recrystallizedInPreviousStep = recrystallizedInCurrentStep;
            recrystallizedInCurrentStep.clear();

        }

    }

}