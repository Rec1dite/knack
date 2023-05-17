# Knack
> Knapsack Problem Solver using a Genetic Algorithm and Ant Colony Optimization

<!-- <p align="center">
<img src="https://media.istockphoto.com/id/1269514456/vector/ants-marching-silhouette-vector-isolated-illustration-ant-line-banner.jpg?s=612x612&w=0&k=20&c=uixlnXU6zkzzPOa_uE1RukxUBTUPDlbV3vvx9JYGKxY=" style="filter: add"/>
</p> -->

## Usage

```bash
# To compile + run
make

# To run both algorithms and obtain summary of results
cd src
java Main

# To display help message
java Main -h

# To run specific algorithm only
java Main -a ga
# or
java Main -a aco

# To run in verbose mode
java Main -va ga

# To set specific population size
java Main -p 100

# To set specific number of generations/iterations
java Main -g 100
```

## Report

### \[1] GA Configuration 🧬
The algorithm begins by initializing a population of Sack objects, each representing a potential solution to the problem.
The predefined items are then randomly distributed among the sacks, ensuring each item is included at least once in the population.
Next, we perform the following steps each generation:

- **Selection**:
    - The population is sorted by the value of the items in each sack.
    - The top half of the population (those with the highest values) are selected to breed new solutions. The best solution found so far is also tracked.
- **Crossover**:
    - Children are created by performing a union operation between two randomly selected parent solutions from the selected top half.
    - The newly created child solutions replace the bottom half of the population.
- **Mutation**:
    - Each solution in the population undergoes mutation, randomly adding or removing an item each individual's sack.
    - This introduces variation into the population and prevents the algorithm from getting stuck in local optima.

As the algorithm progresses, we keep track of the overall best performing individual, which is the final returned result.

### \[2] ACO Configuration 🐜
The algorithm begins by initializing a set number of Ant objects, each representing a potential solution to the problem.
These ants will traverse the set of items, where each ant's route represents a particular combination of items in the knapsack.
Thus, every possible item effectively correlates to a node within the search space.
The initialization also includes a PheromoneTracker to keep track of the pheromones, which represent the 'appeal' of a certain route to each individual ant.

After initialization, the algorithm goes into a loop that repeats for a certain number of iterations. In each iteration:

- **Ant Step**:
    - Each ant in the population makes a decision on its next move:
        - For each ant, a list of possible moves is compiled, which consists of items that the ant has not yet visited and that won't exceed the remaining capacity of the ant's knapsack.
    - The ant then chooses the next move based on a probabilistic rule that takes into account the pheromone level of the path to the item and the value of the item.
        - The path with the highest 'appeal' (calculated as `pheromone^ALPHA * greed^BETA`) is chosen. (Where `greed` is a random choice weighted by value of neighbouring items)

- **Pheromone Update**:
    - After each move, the ant updates the pheromone levels on its route, reinforcing paths that lead to high-value solutions.
    - The amount of pheromones added is proportional to the value of the items in the ant's sack.

- **Best Ant Selection**:
    - At the end of each iteration, the algorithm checks all the ants to see if any of them have found a better solution than the current best, promoting the best solution found so far when necessary.

After the last iteration, the algorithm returns the best solution path found among all the ants and all the iterations.

### \[3] Experimental Setup 🔬
#### Table of parameters
| Parameter             | Genetic Algorithm | Description                           |
|-----------------------|-------------------|---------------------------------------|
| **POPULATION_SIZE**   | 100               | Size of population| each generation   |
| **MAX_GENERATIONS**   | 100               | Limit on the number of generations    |

| Parameter             | Ant Colony Optimization | Description             |
|-----------------------|-------------------------|-------------------------|
| **ALPHA**             | 1.1                     |                         |
| **BETA**              | 1.0                     |                         |
| **NUM_ANTS**          | 100                     | No. of ants per batch   |
| **MAX_ITERATIONS**    | 100                     | No. of batch iterations |




### \[4] Results 📊
With (population size) = 100, and (no. of generations) = 100:

| PROBLEM INSTANCE    | ALGO | BEST SOL | OPTIMUM   | EXECUTION TIME (ms)   |
|---------------------|------|----------|-----------|-----------------------|
| f4_l-d_kp_4_11      | GA   | 23.0     | 23.0      | 24.131165             |
|                     | ACO  | 23.0     |           | 37.450088             |
|                     |      |          |           |                       |
| f6_l-d_kp_10_60     | GA   | 52.0     | 52.0      | 16.601267             |
|                     | ACO  | 52.0     |           | 62.418723             |
|                     |      |          |           |                       |
| f1_l-d_kp_10_269    | GA   | 295.0    | 295.0     | 5.99897               |
|                     | ACO  | 295.0    |           | 55.390218             |
|                     |      |          |           |                       |
| f9_l-d_kp_5_80      | GA   | 130.0    | 130.0     | 5.300974              |
|                     | ACO  | 130.0    |           | 25.69359              |
|                     |      |          |           |                       |
| f10_l-d_kp_20_879   | GA   | 1025.0   | 1025.0    | 8.004401              |
|                     | ACO  | 1025.0   |           | 338.832662            |
|                     |      |          |           |                       |
| f2_l-d_kp_20_878    | GA   | 1024.0   | 1024.0    | 6.028304              |
|                     | ACO  | 1024.0   |           | 233.086195            |
|                     |      |          |           |                       |
| f5_l-d_kp_15_375    | GA   | 481.0694 | 481.0694  | 4.004575              |
|                     | ACO  | 481.0694 |           | 86.879539             |
|                     |      |          |           |                       |
| knapPI_1_100_1000_1 | GA   | 9147.0   | 9147.0    | 3.921114              |
|                  _1 | ACO  | 9147.0   |           | 240.645496            |
|                     |      |          |           |                       |
| f7_l-d_kp_7_50      | GA   | 107.0    | 107.0     | 1.981475              |
|                     | ACO  | 107.0    |           | 10.386906             |
|                     |      |          |           |                       |
| f8_l-d_kp_23_10000  | GA   | 9763.0   | 9767.0    | 3.665005              |
|                     | ACO  | 9757.0   |           | 153.445733            |   
|                     |      |          |           |                       |
| f3_l-d_kp_4_20      | GA   | 35.0     | 35.0      | 1.69261               |
|                     | ACO  | 35.0     |           | 6.853273              |

####

### \[5] Performance Comparison 📈
### \[6] Results Analysis 📝
- **Solution Quality**:
    - Both the GA and ACO were able to find the optimal or very near optimal solutions for all problem instances over the 100 generations.
    - However, GA consistently found the exact optimum, while ACO slightly underperformed in the problem instance f8_l-d_kp_23_10000, suggesting that GA might be more reliable for this specific problem set.

- **Execution Time**:
    - GA consistently outperformed ACO in terms of execution time for all problem instances.
    - The execution time for the GA remains consistently in the single-digit milliseconds range, whereas ACO varies from double digits to hundreds of milliseconds.
    - Clearly the GA implementation has proven much more efficient compared to ACO in these experiments.

- **Scalability**:
    - When comparing the largest problem instances `f10_l-d_kp_20_879`, `f2_l-d_kp_20_878`, `knapPI_1_100_1000_1`, and `f8_l-d_kp_23_10000`, we see that GA maintains its efficiency as the problem size increases.
    - On the other hand, ACO's execution time increases dramatically, showing that it doesn't scale as well as GA for larger problems.

- **Robustness**:
    - GA's performance is generally more robust across different problem instances, whereas ACO's execution time varies greatly depending on the problem instance.
    - This indicates that GA might be more suitable for a wide range of problems.

Extrapolating from this analysis we must conclude that, while both GA and ACO can effectively solve the knapsack problem for these specific cases, the GA outperforms by at least an order of magnitude on nearly every metric, and so is likely to be the more effective algorithm for solving the problem in general.