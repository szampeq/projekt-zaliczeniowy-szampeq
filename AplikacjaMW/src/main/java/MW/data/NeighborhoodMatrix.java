package MW.data;

import MW.enums.Neighborhoods;

import java.util.concurrent.ThreadLocalRandom;

public abstract class NeighborhoodMatrix {

    private static final int[][] Moore = {
            {1, 1, 1},
            {1, 0, 1},
            {1, 1, 1}
    };
    private static final int[][] VonNeumann = {
            {0, 1, 0},
            {1, 0, 1},
            {0, 1, 0}
    };
    private static final int[][] LeftHex = {
            {0, 1, 1},
            {1, 0, 1},
            {1, 1, 0}
    };
    private static final int[][] RightHex = {
            {1, 1, 0},
            {1, 0, 1},
            {0, 1, 1}
    };
    private static final int[][] LeftPen = {
            {1, 1, 0},
            {1, 0, 0},
            {1, 1, 0}
    };
    private static final int[][] TopPen = {
            {1, 1, 1},
            {1, 0, 1},
            {0, 0, 0}
    };
    private static final int[][] RightPen = {
            {0, 1, 1},
            {0, 0, 1},
            {0, 1, 1}
    };
    private static final int[][] BottomPen = {
            {0, 0, 0},
            {1, 0, 1},
            {1, 1, 1}
    };
    private static final int[][] Empty = {
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
    };

    static int[][] neighborhoodMatrix(Neighborhoods neighborhood) {
        switch (neighborhood) {
            case RandomHex:
                switch (ThreadLocalRandom.current().nextInt(0, 2)) {
                    case 0:
                        return LeftHex;
                    case 1:
                        return RightHex;
                }
            case RandomPen:
                switch (ThreadLocalRandom.current().nextInt(0, 4)) {
                    case 0:
                        return LeftPen;
                    case 1:
                        return RightPen;
                    case 2:
                        return TopPen;
                    case 3:
                        return BottomPen;
                }
            case Moore:
                return Moore;
            case VonNeumann:
                return VonNeumann;
            case LeftHex:
                return LeftHex;
            case RightHex:
                return RightHex;
            case LeftPen:
                return LeftPen;
            case RightPen:
                return RightPen;
            case TopPen:
                return TopPen;
            case BottomPen:
                return BottomPen;
            default:
                return Empty;
        }
    }

    static public int[][] radiusMatrix(int neighborhoodRandomRadius) {
        int size = neighborhoodRandomRadius*2;

        int[][] matrix = new int[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                matrix[i][j] = 0;

        int Center = size/2;

        for (int x = Center - neighborhoodRandomRadius; x <= Center; x++)
            for (int y = Center - neighborhoodRandomRadius; y <= Center; y++)
            {
                if ((x - Center)*(x - Center) + (y - Center)*(y - Center) <= neighborhoodRandomRadius*neighborhoodRandomRadius)
                {
                    int xSym = Center - (x - Center);
                    int ySym = Center - (y - Center);

                    if (x < 0 || y < 0 || xSym < 0 || ySym < 0)
                        continue;
                    if (x > size-1 || xSym > size-1 || y > size-1 || ySym > size-1)
                        continue;

                    matrix[x][y] = 1;
                    matrix[x][ySym] = 1;
                    matrix[xSym][y] = 1;
                    matrix[xSym][ySym] = 1;
                }
            }


        return matrix;
    }

}
