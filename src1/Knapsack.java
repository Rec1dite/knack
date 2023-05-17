import java.util.List;

public class Knapsack {
    List<Item> trinkets; //All possible items to choose from
    float capacity; //Maximum weight the knapsack can hold

    Knapsack(float capacity, List<Item> trinkets) {
        this.capacity = capacity;
        this.trinkets = trinkets;
    }
}