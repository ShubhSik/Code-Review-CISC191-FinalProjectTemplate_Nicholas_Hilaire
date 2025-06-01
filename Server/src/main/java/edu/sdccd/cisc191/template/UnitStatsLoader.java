package edu.sdccd.cisc191.template;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Author Nicholas Hilaire
 *
 * References: "Get Substring from String in Java" by Tom Hombergs  (https://www.baeldung.com/java-substring),
 * Java read from URL stream working selectively (https://stackoverflow.com/questions/54433100/java-read-from-url-stream-working-selectively)
 * Read a File from Resources Directory (https://howtodoinjava.com/java/io/read-file-from-resources-folder)
 *
 */


//Class used to access the CSV file and create units with the data from the file.
public class UnitStatsLoader {
    /**
     * Loads units from a CSV file at the given path.
     * <p>
     * Attempts to read the file, parse each line, and create a list of Unit objects.
     * If the file does not exist in the given path, it tries a fallback path.
     * <p>
     * If a file is not found or cannot be read, this method throws a RuntimeException.
     * Lines with parsing errors are logged and skipped.
     * </p>
     *
     * @param path The path to the CSV file.
     * @return A list of Unit objects loaded from the CSV file. If file is not found or unreadable, an empty list is returned.
     * @throws RuntimeException if the file cannot be found or read.
     */
    public static List<Unit> loadUnits(String path)
    {
        List<Unit> units = new ArrayList<>();

        File file = new File(path);
        // Try to locate the file in the given path or in src/main/resources/
        if (!file.exists())
        {
            file = new File("C:\\Users\\Nicko\\IdeaProjects\\CISC191-FinalProjectTemplate\\Server\\src\\main\\resources" + path);

        // If file is not found will ensure the correct path is used.
        }
        if (!file.exists())
        {
            System.err.println("File not found: " + path);
            return units;
        }

        /* Allows the code to gather data in a buffer and then read text in from a character input stream that takes in the file object that is declared
         in the previous code listed above.
        */
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            // Read and discard the header row
            if ((line = br.readLine()) == null)
            {
                return units;
            }

            // while loop reads the file one line at a time until no more lines occur.
            while ((line = br.readLine()) != null)
            {
                // Split the CSV line using a regex that ignores commas within quotes
                String[] stats = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                // for loop to Trim whitespace from each data point from csv file
                for (int i = 0; i < stats.length; i++)
                {
                    stats[i] = stats[i].trim();
                }

                    //Uses the methods from UnitGenerator that creates an Unit object
                    try
                    {
                        Unit unit = UnitGenerator.createUnit(stats);
                        units.add(unit);
                    }

                        catch (NumberFormatException e)
                        {
                            System.err.println("Error parsing stats: " + line);
                            e.printStackTrace();
                        }
            }
        }

        //Handles any errors if the file is not found
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("File not found: " + path, e);
        }

        // Handles any errors and if an error occurs will return an empty string.
        catch (IOException e)
        {
            throw new RuntimeException("Error reading file: " + path, e);
        }

        return units;
    }
}