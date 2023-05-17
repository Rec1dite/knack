import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GA_Knapsack extends Knapsack {

    GA_Knapsack(float capacity, List<Item> trinkets) {
        super(capacity, trinkets);
    }

    public void optimize() {
        final int POPULATION_SIZE = 10;
        final int MAX_GENERATIONS = 10;

        long startTime = System.nanoTime();

        //===== GENERATE INITIAL POPULATION =====//
        List<Sack> population = new ArrayList<>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            Sack sack = new Sack();
            population.add(sack);
        }

        // Randomly distribute all items among all sacks
        // so each item is in at least one sack
        boolean allFalse = false;
        for (int t = 0; t < 10 && !allFalse; t++) // Repeat a couple times so each sack is likely full
        {
            allFalse = true;

            for (int i = 0; i < trinkets.size(); i++) {
                int numAdds = (int)(Math.random()*5) + 1; //Always add to at least one stack

                for (int j = 0; j < numAdds; j++) {
                    int addTo = (int)(Math.random() * POPULATION_SIZE);

                    if(population.get(addTo).addItem(i)) { allFalse = false; }
                }
            }
        }

        Sack bestEverIndividual = null; //The highest value sack we've ever obtained

        for (int i = 0; i < MAX_GENERATIONS; i++) {
            //===== SELECT PARENTS =====//
            // (Pick upper half of population by value)

            // Sort by value
            Collections.sort(population, Comparator.comparing(Sack::getValue));

            // Try update best ever individual
            Sack bestItemThisRound = population.get(POPULATION_SIZE-1);
            if (
                bestEverIndividual == null ||
                bestItemThisRound.getValue() > bestEverIndividual.getValue()
            ) {
                bestEverIndividual = new Sack(bestItemThisRound);
            }

            // print all sacks
            if (Main.debug) {
                int x = 0;
                for (Sack sack : population) {
                    System.out.println(x + " " + sack);
                    x++;
                }
                System.out.println("");
            }

            //===== CROSSOVER =====//
            // {set union, set intersection}

            // Children replace bottom half of population
            for (int j = 0; j < POPULATION_SIZE/2; j++) {

                // Get two random distinct parents
                int parent1 = POPULATION_SIZE/2 + (int)(Math.random() * POPULATION_SIZE/2);
                int parent2 = POPULATION_SIZE/2 + (int)(Math.random() * (POPULATION_SIZE/2-1));
                if (parent1 == parent2) { parent2++; }

                Sack child = new Sack();

                // Set union
                child.union(population.get(parent1));
                child.union(population.get(parent2));

                population.set(j, child);
            }

            //===== MUTATE =====//
            // {remove random item, add random item}
            for (int j = 0; j < POPULATION_SIZE; j++) {
                population.get(j).mutate();
            }
        }

        long endTime = System.nanoTime();

        //===== RETURN BEST INDIVIDUAL =====//
        System.out.println(Main.GREEN + "BEST SOLUTION: " + Main.YELLOW + bestEverIndividual + Main.RESET);
        System.out.println(Main.GREEN + "VALUE:\t" + Main.RED + bestEverIndividual.getValue() + Main.RESET);
        System.out.println(Main.GREEN + "TIME:\t" + Main.YELLOW + (endTime - startTime) + " ns" + Main.RESET);
    }
}
