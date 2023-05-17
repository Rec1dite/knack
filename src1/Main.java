import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final boolean debug = false;
    public static final String RED = "\033[0;31m";
    public static final String BLUE = "\033[0;34m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String RESET = "\033[0m";

    public static void main(String[] args) {
        String inputFolder = "../knaps";

        File folder = new File(inputFolder);
        File[] inputs = folder.listFiles();

        final int maxFiles = 100;
        int i = 0;
        for (File f : inputs) {
            if (f.isFile()) {
                i++; if(i > maxFiles) { return; }

                System.out.println("\n=====================================================");
                String filePath = f.getAbsolutePath();

                KnapsackData data = readKnapsackDataFromFile(filePath);

                // Print total items values
                float tot = 0;
                for (Item item : data.items) {
                    if (debug) { System.out.println(item); }
                    tot += item.getValue();
                }
                System.out.println("Total item values:\t" + tot);
                System.out.println("");

                Knapsack knapsack = new ACO_Knapsack(data.capacity, data.items);
                knapsack.optimize();
            }
        }
    }

    // FILE FORMAT:
    //  <total no. of items> <knapsack capacity>
    //  <item value> <item weight>
    public static KnapsackData readKnapsackDataFromFile(String filePath) {
        KnapsackData res = new KnapsackData();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            if ((line = br.readLine()) != null) {
                System.out.println("File: " + BLUE + filePath + RESET);
                String[] parts = line.split(" ");

                System.out.println("Number of items:\t" + parts[0]);
                System.out.println("Knapsack Capacity:\t" + parts[1]);

                res.capacity = Integer.parseInt(parts[1]);
            }

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) { // We expect exactly 2 parts
                    float part1 = Float.parseFloat(parts[0]);
                    float part2 = Float.parseFloat(parts[1]);
                    res.items.add(new Item(part2, part1));
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading " + filePath + ":");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("An error occurred while parsing the numbers in " + filePath + ":");
            e.printStackTrace();
        }
        return res;
    }

    static class KnapsackData {
        int capacity;
        List<Item> items;

        KnapsackData() {
            capacity = 0;
            items = new ArrayList<>();
        }
    }
}