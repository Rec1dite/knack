import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String inputFolder = "../knaps";

        File folder = new File(inputFolder);
        File[] inputs = folder.listFiles();

        for (File f : inputs) {
            if (f.isFile()) {
                System.out.println("\n=====================================================");
                String filePath = f.getAbsolutePath();

                List<Pair> pairs = readFloatPairsFromFile(filePath);

                // Print total items values
                float tot = 0;
                for (Pair pair : pairs) {
                    tot += pair.getValue();
                }
                System.out.println("Total item values: " + tot);
            }
        }
    }

    // FILE FORMAT:
    //  <total no. of items> <knapsack capacity>
    //  <item value> <item weight>
    public static List<Pair> readFloatPairsFromFile(String filePath) {
        List<Pair> pairs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            if ((line = br.readLine()) != null) {
                System.out.println("File: " + filePath);
                String[] parts = line.split(" ");
                System.out.println("Number of pairs:\t" + parts[0]);
                System.out.println("Knapsack Capacity:\t" + parts[1]);
            }

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) { // We expect exactly 2 parts
                    float part1 = Float.parseFloat(parts[0]);
                    float part2 = Float.parseFloat(parts[1]);
                    pairs.add(new Pair(part1, part2));
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading " + filePath + ":");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("An error occurred while parsing the numbers in " + filePath + ":");
            e.printStackTrace();
        }
        return pairs;
    }
}