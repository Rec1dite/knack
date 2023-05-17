import java.util.SortedSet;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GA_Knapsack extends Knapsack {

    GA_Knapsack(float capacity, List<Item> trinkets) {
        super(capacity, trinkets);
    }

    void geneticAlgorithm() {
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

    // A single possible solution instance
    // Acts as the individual in the GA population
    public class Sack {
        SortedSet<Integer> items; //Indexes to the items in the trinkets list
        float weight;
        float value;

        Sack() {
            this.items = new TreeSet<>();
            this.weight = 0;
            this.value = 0;
        }

        Sack(Sack other) {
            this.items = new TreeSet<>(other.items);
            this.weight = other.weight;
            this.value = other.value;
        }

        // Returns false if not enough space
        public boolean addItem(int index) {
            float w = trinkets.get(index).getWeight();

            if (this.weight + w > capacity) {
                return false; // Can't add item
            }

            if (items.add(index))
            {
                weight += w;
                value += trinkets.get(index).getValue();
            }

            return true;
        }

        public boolean removeItem(int item) {
            if (!items.remove(item)) {
                return false; // Item not in sack
            }

            weight -= trinkets.get(item).getWeight();
            value -= trinkets.get(item).getValue();
            return true;
        }

        public void removeRandom() {
            if (items.size() == 0) {
                return; // No items to remove
            }

            // Cleanest way to remove a random item without having to iterate through the set
            // Java is retarded and doesn't have a set get() method
            int randomItem = (int)(Math.random() * items.last());

            Integer toRemove = items.subSet(randomItem, trinkets.size()).first();
            removeItem(toRemove);
        }

        public void union(Sack other) {
            for (Integer item : other.items) {
                this.addItem(item);
            }
        }

        public void mutate() {
            // We may mutate up to 20% of the items
            int mutationDegree = (int)(Math.random() * items.size()*0.2);
            if (mutationDegree == 0) { mutationDegree = 1; } // Always do at least one mutation

            // Remove random items
            for (int i = 0; i < mutationDegree; i++) {
                removeRandom();
            }

            // Try add random items
            // This also happens during crossover so don't try too hard
            int itemRange = trinkets.size();
            for (int i = 0; i < 2*mutationDegree; i++) {
                int item = (int)(Math.random() * itemRange);
                this.addItem(item);
            }
        }

        public float getWeight() {
            return this.weight;
        }

        public float getValue() {
            return this.value;
        }

        public String toString() {
            return "Sack: { W: " + this.weight + ", V: " + this.value + " } " + this.items.toString();
        }
    }
}
