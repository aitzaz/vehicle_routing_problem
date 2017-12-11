# Translation of a Python brute-force solution for the Vehicle Routing Problem (VRP)

VRP is a generalized version of the Traveling Salesperson Problem (TSP): Given a depot, a set of delivery locations and the number of vehicles starting from the depot, we need to minimize the time (or travel distance) required to visit all locations.

This repo is translation of a pythonic implementation of the brute-force solution to the VRP. VRP is an NP-Hard problem so the brute force obviously becomes very prohibitive very quickly as the number of destinations is increased or the number of vehicles is reduced.

The code expects two input files, one with locations and their geo-coordinates and the other with distances between locations. There are some simplistic assumptions:

1. Each delivery location needs to be visited just once
2. Vehicles come back to the depot
3. The first location in the list of locations is the depot
4. Distance from X to Y is the same as the distance from Y to X

Here is a sample plot of the starting input locations resulting from search query ```McDonalds near Lahore``` with the truck icon showing the depot:

![alt text](https://i.imgur.com/82QgV4X.jpg)

Here is the output created after the algorithm has been run with 3 vehicles as input and 11 delivery locations:
```
All location ids: [0, 1, 2, 4, 5, 6, 7, 8, 9, 10, 11]
Solution time: 2 seconds
Shortest route time: 66.2 minutes
Shortest route: [[0, 1, 11, 9], [0, 5, 2, 10, 6], [0, 7, 4, 8]]
```
---

Here is Python Repo's [link](https://github.com/ybashir/vrpfun).

---

To try the code, run following command in project's root directory (Note: it may take some time to download Gradle Wrapper):

```./gradlew -q clean run -PappArgs="['sample', '3']"```

Note: This code requires Java 8.