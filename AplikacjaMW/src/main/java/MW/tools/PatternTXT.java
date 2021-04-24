package MW.tools;

import MW.data.Cell;
import MW.data.DataManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PatternTXT {
    public static int numberTXT = 0;
    public static void savePattern(DataManager dataManager){

        try
        {
            FileWriter txt = new FileWriter("src/main/resources/patterns/pattern" + numberTXT + ".txt");
            Cell[][] matrix = dataManager.getMatrix();
            txt.write(dataManager.getMeshSize() + "\n");
            for (int i = 0; i < dataManager.getMeshSize(); i++) {
                for (int j = 0; j < dataManager.getMeshSize(); j++) {
                    if(matrix[i][j].isActive())
                        txt.write("1");
                    else
                        txt.write("0");
                }
                txt.write("\n");
            }
            System.out.println("Pattern Saved");
            numberTXT++;
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
        String meshSizeString = in.nextLine();
        int meshSize = Integer.parseInt(meshSizeString);
        Cell[][] matrix = new Cell[meshSize][meshSize];

        for (int i = 0; i < meshSize; i++)
        {
            String words = in.next();
            for (int j = 0; j < meshSize; j++)
            {
                char c = words.charAt(j);
                matrix[i][j].setActive(Character.getNumericValue(c) != 0);

            }
        }

        return matrix;
    }
}