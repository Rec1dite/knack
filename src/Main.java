import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {
    static final String RED = "\033[0;31m";
    static final String BLUE = "\033[0;34m";
    static final String GREEN = "\033[0;32m";
    static final String PURPLE = "\033[0;35m";
    static final String YELLOW = "\033[0;33m";
    static final String RESET = "\033[0m";

    static boolean verbose = false;
    static int maxFiles = 100;
    static Set<Algo> algos = Set.of(Algo.GA, Algo.ACO);
    static int popSize = -1;
    static int maxIter = -1;

    public static void main(String[] args) {
        String inputFolder = "./knaps";
        //===== PARSE ARGUMENTS =====//
        ArgParser parser = new ArgParser(args);
        if(!parser.parse()) {
            return;
        }

        //===== READ INPUT FILES =====//
        File folder = new File(inputFolder);
        File[] inputs = folder.listFiles();

        System.out.println("RESULTS:");
        System.out.println("ALGO\tFILE\t\tVALUE\tEXECUTION TIME (ms)");
        System.out.println(RED + "----\t----\t\t-----\t--------------" + RESET);

        int i = 0;
        for (File f : inputs) {
            if (f.isFile()) {
                i++; if(i > maxFiles) { return; }

                if (verbose) {
                    System.out.println("\n=====================================================");
                }
                String filePath = f.getAbsolutePath();

                KnapsackData data = readKnapsackDataFromFile(filePath);

                // Print total items values
                float tot = 0;
                for (Item item : data.items) {
                    tot += item.getValue();
                }

                if (verbose) {
                    System.out.println("Total item values:\t" + tot);
                    System.out.println("");
                }

                //===== RUN ALGORITHMS =====//
                Knapsack.OptimizationResult gaRes = null;
                Knapsack.OptimizationResult acoRes = null;
                for (Algo algo : algos) {
                    switch(algo) {
                        case GA:
                            if (verbose) {
                                System.out.println(PURPLE + "Running GA" + RESET);
                            }
                            GA_Knapsack ga = new GA_Knapsack(data.capacity, data.items);
                            if (popSize != -1) { ga.setPopulationSize(popSize); }
                            if (maxIter != -1) { ga.setMaxGenerations(maxIter); }

                            gaRes = ga.optimize();
                            break;

                        case ACO:
                            if (verbose) {
                                System.out.println(PURPLE + "Running ACO" + RESET);
                            }
                            ACO_Knapsack aco = new ACO_Knapsack(data.capacity, data.items);
                            if (popSize != -1) { aco.setNumAnts(popSize); }
                            if (maxIter != -1) { aco.setMaxIterations(maxIter); }

                            acoRes = aco.optimize();
                            break;
                    }
                }

                //===== PRINT RESULTS =====//
                if (gaRes != null) {
                    System.out.println(BLUE + "GA\t" + GREEN + f.getName() + "\t" + YELLOW + gaRes.sack.getValue() + RESET + "\t" + gaRes.timeTaken/1000000.0);
                }
                if (acoRes != null) {
                    System.out.println(PURPLE + "ACO\t" + GREEN + f.getName() + "\t" + YELLOW + acoRes.sack.getValue() + RESET + "\t" + acoRes.timeTaken/1000000.0);
                }
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
                String[] parts = line.split(" ");

                if (verbose) {
                    System.out.println("File: " + BLUE + filePath + RESET);
                    System.out.println("Number of items:\t" + parts[0]);
                    System.out.println("Knapsack Capacity:\t" + parts[1]);
                }

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

    static class ArgParser {
        String[] args;

        ArgParser(String[] args) {
            this.args = args;
        }

        boolean parse() {
            if (args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    String arg = args[i];

                    // Check valid argument
                    if (
                        arg.length() <= 1 ||
                        arg.charAt(0) != '-' ||
                        !Character.isLetter(arg.charAt(1))
                    ){
                        System.out.println(RED + "Invalid argument: " + arg + RESET);
                        return false;
                    }

                    // Loop through flags
                    for (int c = 1; c < arg.length(); c++) {
                        switch(arg.charAt(c)) {

                            //===== FLAGS =====//
                            case 'a': //Use specific algorithm
                                if(!handleParameterizedFlag(c, i, 'a')) { return false; }

                                String alg = args[i+1];
                                if (alg.equals("ga")) {
                                    algos = Set.of(Algo.GA);
                                }
                                else if (alg.equals("aco")) {
                                    algos = Set.of(Algo.ACO);
                                }
                                else if (alg.equals("all")) {
                                    algos = Set.of(Algo.GA, Algo.ACO);
                                }
                                else {
                                    System.out.println(RED + "Unknown algorithm: " + alg + RESET);
                                    System.out.println(RED + "Options include: ['ga', 'aco', 'all']" + RESET);
                                    return false;
                                }
                                i++;

                                break;

                            case 'v': //Verbose output
                                verbose = true;
                                break;

                            case 'p': //Population size
                                if(!handleParameterizedFlag(c, i, 'p')) { return false; }

                                try {
                                    popSize = Integer.parseInt(args[i+1]);
                                    if (popSize <= 0) {
                                        System.out.println(RED + "Population must be a positive number" + RESET);
                                        return false;
                                    }
                                    i++; // Skip parsing the next argument
                                }
                                catch (NumberFormatException e) {
                                    System.out.println(RED + "Failed to parse number argument: " + args[i+1] + RESET);
                                    return false;
                                }
                                break;

                            case 'g': //Max no. of generations
                                if(!handleParameterizedFlag(c, i, 'g')) { return false; }

                                try {
                                    maxIter = Integer.parseInt(args[i+1]);
                                    if (maxIter <= 0) {
                                        System.out.println(RED + "Max. Iterations must be a positive number" + RESET);
                                        return false;
                                    }
                                    i++; // Skip parsing the next argument
                                }
                                catch (NumberFormatException e) {
                                    System.out.println(RED + "Failed to parse number argument: " + args[i+1] + RESET);
                                    return false;
                                }
                                break;

                            case 'n': //Max number of files
                                if(!handleParameterizedFlag(c, i, 'n')) { return false; }

                                try {
                                    maxFiles = Integer.parseInt(args[i+1]);
                                    i++; // Skip parsing the next argument
                                }
                                catch (NumberFormatException e) {
                                    System.out.println(RED + "Failed to parse number argument: " + args[i+1] + RESET);
                                    return false;
                                }
                                break;

                            case 'h':
                                System.out.println("Usage: " + BLUE + "java Main [flags]" + RESET);
                                System.out.println("-a <algo> \t: Use specific algorithm");
                                System.out.println("-n <num> \t: Set max number of input files");
                                System.out.println("-p <num> \t: Set population size");
                                System.out.println("-g <num> \t: Set max no. of generations");
                                System.out.println("-v \t\t: Verbose output");
                                System.out.println("-h \t\t: Print this message");
                                return false;

                            default:
                                System.out.println(RED + "Unknown flag: " + arg.charAt(c) + RESET);
                                return false;
                        }
                    }
                }
            }

            return true;
        }

        boolean handleParameterizedFlag(int c, int i, char flag) {
            // Check that we're the last flag in the list
            if (c != args[i].length()-1) {
                System.out.println(RED + "Flag -" + flag + " must be the last flag in the list to supply an argument" + RESET);
                return false;
            }

            // Check that an argument exists
            if (i+1 >= args.length) {
                System.out.println(RED + "No argument supplied for the -" + flag + " flag" + RESET);
                return false;
            }

            return true;
        }
    }

    enum Algo {
        GA,
        ACO
    }
}