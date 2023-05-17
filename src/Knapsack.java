import java.util.SortedSet;
import java.util.TreeSet;
import java.util.List;

public class Knapsack {
    List<Item> trinkets; //All possible items to choose from
    float capacity; //Maximum weight the knapsack can hold

    Knapsack(float capacity, List<Item> trinkets) {
        this.capacity = capacity;
        this.trinkets = trinkets;
    }

    public OptimizationResult optimize() { return null; }

    public class OptimizationResult {
        Sack sack;
        long timeTaken;

        OptimizationResult(Sack sack, long timeTaken) {
            this.sack = sack;
            this.timeTaken = timeTaken;
        }

        public Sack getSack() {
            return this.sack;
        }

        public float getValue() {
            if (this.sack == null) {
                return -1;
            }

            return this.sack.getValue();
        }

        public float getTimeTaken() {
            return this.timeTaken;
        }
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

            if (w > getRemainingCapacity()) {
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

        public float getRemainingCapacity() {
            return capacity - this.weight;
        }

        public String toString() {
            return "Sack: { W: " + this.weight + ", V: " + this.value + " } " + this.items.toString();
        }
    }
}