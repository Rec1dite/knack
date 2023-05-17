import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ACO_Knapsack extends Knapsack {

    ACO_Knapsack(float capacity, List<Item> trinkets) {
        super(capacity, trinkets);
    }

    public void optimize() {
        final int NUM_ANTS = 10; //Number of ants per batch
        final int MAX_ITERATIONS = 10; //Number of batches to dispatch

        final float ALPHA = 1.0f;   // Pheromone importance
        final float BETA = 1.0f;    // Greedy importance

        long startTime = System.nanoTime();

        float highestValueNode = 0.0f; // The sum of all trinket values
        for (Item t : trinkets) {
            if (t.getValue() > highestValueNode) {
                highestValueNode = t.getValue();
            }
        }

        //===== INITIALIZE PHEROMONES =====//
        // Assume all pheromones start at 1
        PheromoneTracker pheromones = new PheromoneTracker();

        Ant bestEverAnt = null; //The highest value ant we've ever obtained

        for (int i = 0; i < MAX_ITERATIONS; i++)
        {
            //===== GENERATE ANTS =====//
            List<Ant> ants = new ArrayList<>();

            for (int a = 0; a < NUM_ANTS; a++) {
                // Ants start at position empty {}
                ants.add(new Ant());
            }

            boolean allAntsFinished = false;

            while (!allAntsFinished) {
                allAntsFinished = true;

                //===== EACH ANT TAKES A STEP =====//
                for (int a = 0; a < NUM_ANTS; a++) {
                    Ant ant = ants.get(a);

                    //==== EVALUATE POSSIBLE MOVES FOR THIS ANT =====//
                    float remainingCapacity = ant.sack.getRemainingCapacity();

                    //===== COMPILE LIST OF POSSIBLE MOVES =====//
                    // - All items that the ant has not already visited
                    // - All items that will not exceed sack capacity
                    List<Integer> possibleMoves = new ArrayList<>();

                    for (int m = 0; m < trinkets.size(); m++) {
                        if(
                            !ant.hasVisited(m) &&
                            trinkets.get(m).getWeight() <= remainingCapacity
                        ){
                            possibleMoves.add(m);
                        }
                    }

                    //===== CHECK IF ANT HAS NO POSSIBLE MOVES =====//
                    if (possibleMoves.size() == 0) {
                        continue;
                    }
                    allAntsFinished = false;

                    //===== CHOOSE MOVE =====//
                    int currPos = ant.getPosition();
                    int chosenMove = -1;
                    float chosenMoveAppeal = 0;

                    for (Integer m : possibleMoves) {
                        //===== CALCULATE PROBABILITY OF TAKING THIS MOVE =====//
                        Item move = trinkets.get(m);

                        // Get pheromone for this move
                        Float pher = pheromones.get(currPos, m);
                        pher = pher == null ? 1.0f : pher;

                        // Semi-greedily choose random move weighted by value
                        float greed = (float)(Math.random() * move.getValue())/highestValueNode;

                        // Optimize chosen move for appeal
                        // TODO: Try different pheromone update rules (e.g. add instead of multiply)
                        float appeal = (float)(Math.pow(pher, ALPHA)*Math.pow(greed, BETA));
                        if (appeal > chosenMoveAppeal) {
                            chosenMove = m;
                            chosenMoveAppeal = appeal;
                        }
                    }

                    //===== MAKE MOVE =====//
                    ant.moveTo(chosenMove);

                    //===== UPDATE PHEROMONES =====//
                    // Backtrack through the ant's route and update pheromones
                    ant.markRoute(pheromones);
                }
            }

            //===== FIND BEST ANT =====//
            for (Ant ant : ants) {
                if (
                    bestEverAnt == null ||
                    ant.getValue() > bestEverAnt.getValue()
                ){
                    bestEverAnt = new Ant(ant);
                    // System.out.println((int)(bestEverAnt.getValue()) + " " + bestEverAnt.route);
                }
            }
        }

        long endTime = System.nanoTime();

        //===== RETURN BEST INDIVIDUAL =====//
        System.out.println(Main.GREEN + "BEST SOLUTION: " + Main.YELLOW + bestEverAnt + Main.RESET);
        System.out.println(Main.GREEN + "VALUE:\t" + Main.RED + bestEverAnt.getValue() + Main.RESET);
        System.out.println(Main.GREEN + "TIME:\t" + Main.YELLOW + (endTime - startTime) + " ns" + Main.RESET);
    }
    class Ant {

        // Tracks the order in which the items were picked up
        //  Final item in the route is the current position
        ArrayList<Integer> route;

        // Tracks what items we've picked up
        //  Technically the sack is redundant, but its much more efficient
        //  to check visited items with the O(1) lookup
        Sack sack;

        Ant() {
            route = new ArrayList<>();
            sack = new Sack();
        }

        Ant(Ant other) {
            route = new ArrayList<>(other.route);
            sack = new Sack(other.sack);
        }

        public boolean hasVisited(int item) {
            return sack.items.contains(item);
        }

        public int getPosition() {
            if (route.size() == 0) {
                return -1;
            }

            return route.get(route.size()-1);
        }

        public float getValue() {
            return sack.getValue();
        }

        // Try to move to the given position
        // Returns false if the ant cannot move there
        public boolean moveTo(int pos) {
            if (hasVisited(pos)) {
                return false;
            }

            route.add(pos);
            sack.addItem(pos);
            return true;
        }

        public void markRoute(PheromoneTracker pheromones) {
            for (int i = 0; i < route.size()-1; i++) {
                int itemA = route.get(i);
                int itemB = route.get(i+1);

                pheromones.addPheromones(itemA, itemB, getValue());
            }
        }

        public String toString() {
            // Stringify route
            String routeString = "No route";
            if (route != null && route.size() != 0) {

                routeString = " [" + (int)trinkets.get(route.get(0)).getValue();
                for (int i = 1; i < route.size(); i++) {
                    routeString += ", " + (int)trinkets.get(route.get(i)).getValue();
                }
                routeString += "]";

            }

            return "Ant: { W: " + this.sack.getWeight() + ", V: " + this.sack.getValue() + " }" + routeString;
        }
    }

    class PheromoneTracker {
        Map<Set<Integer>, Float> pheromones;

        PheromoneTracker() {
            pheromones = new HashMap<>();
        }

        public Float get(int itemA, int itemB) {
            return pheromones.get(Set.of(itemA, itemB));
        }

        public Float addPheromones(int itemA, int itemB, float amount) {
            Set<Integer> key = Set.of(itemA, itemB);

            Float pher = pheromones.get(key);
            pher = pher == null ? 1.0f : pher;

            pheromones.put(key, pher + amount);

            return pher;
        }
    }

}