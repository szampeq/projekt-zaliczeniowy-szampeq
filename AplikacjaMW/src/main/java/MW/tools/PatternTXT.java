package MW.tools;

import MW.data.Cell;
import MW.data.DataManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PatternTXT {
    public static int numberCSV = 0;
    public static void savePattern(DataManager dataManager){

        try
        {
            FileWriter txt = new FileWriter("zapis" + numberCSV + ".csv");

            int size = dataManager.listOfDislocationPool.size();

            for (int i = 0; i < size; i++)
            {
                txt.write(dataManager.listOfSteps.get(i).toString());
                txt.write(";");
                txt.write(dataManager.listOfDislocationPool.get(i).toString());
                txt.write("\n");
            }
            System.out.println("File Saved");
            numberCSV++;
            txt.close();

        } catch (IOException ex)
        {
            ex.printStackTrace();
            System.out.println("Error!");
        }

    }

    public static Cell[][] openPattern(String path) throws FileNotFoundException {

        File file = new File(path);
        Scanner in = new Scanner(file);
        String meshSizeStringX = in.nextLine();
        String meshSizeStringY = in.nextLine();
        int meshSizeX = Integer.parseInt(meshSizeStringX);
        int meshSizeY = Integer.parseInt(meshSizeStringY);
        Cell[][] matrix = new Cell[meshSizeX][meshSizeY];

        for (int i = 0; i < meshSizeX; i++)
        {
            String words = in.next();
            for (int j = 0; j < meshSizeY; j++)
            {
                char c = words.charAt(j);
                matrix[i][j].setActive(Character.getNumericValue(c) != 0);

            }
        }

        return matrix;
    }
}