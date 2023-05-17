public class Item {
    private float weight;
    private float value;

    public Item(float weight, float value) {
        this.weight = weight;
        this.value = value;
    }

    public float getWeight() {
        return this.weight;
    }

    public float getValue() {
        return this.value;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Item { " +
                "weight=" + weight +
                ", value=" + value +
                " }";
    }
}