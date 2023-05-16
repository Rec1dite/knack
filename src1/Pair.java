public class Pair {
    private float weight;
    private float value;

    public Pair(float weight, float value) {
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
        return "Pair { " +
                "weight=" + weight +
                ", value=" + value +
                " }";
    }
}