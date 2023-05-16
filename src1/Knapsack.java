import java.util.ArrayList;
import java.util.List;

public class Knapsack {
    List<Pair> pairs;
    List<Pair> items;

    float capacity;
    float value;
    float weight;

    Knapsack(float capacity) {
        this.capacity = capacity;
        this.value = 0;
        this.weight = 0;

        this.pairs = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    // Returns false and adds nothing if not enough space
    public boolean addPairs(List<Pair> pairs) {
        float totalWeight = 0;
        for (Pair pair : pairs) {
            totalWeight += pair.getWeight();
        }
        if (totalWeight > capacity) {
            return false;
        }

        this.pairs.addAll(pairs);
        this.weight = totalWeight;

        // Calculate the value
        for (Pair pair : pairs) {
            this.value += pair.getValue();
        }

        return true;
    }

    // Returns false if not enough space
    public boolean addPair(Pair pair) {
        if (pair.getWeight() + this.weight > capacity) {
            return false;
        }

        this.pairs.add(pair);
        this.weight += pair.getWeight();
        this.value += pair.getValue();

        return true;
    }


    public float getValue() {
        return this.value;
    }

    public float getRemainingCapacity() {
        return this.capacity - this.weight;
    }
}
