package MW.data;

import MW.enums.Neighborhoods;

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
    private static final int[][] RandomHex = {
            {1, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
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
    private static final int[][] Radius = {
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
    };

    static int[][] neighborhoodMatrix(Neighborhoods neighborhood) {
        switch (neighborhood) {
            case Moore:
                return Moore;
            case VonNeumann:
                return VonNeumann;
            case LeftHex:
                return LeftHex;
            case RightHex:
                return RightHex;
            case RandomHex:
                return RandomHex;
            case RandomPen:
                return LeftPen;
            default:
                return Radius;
        }
    }
}
