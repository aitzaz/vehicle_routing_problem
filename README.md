# A brute-force solution for the Vehicle Routing Problem (VRP) - Translated from a Python code

VRP is a generalized version of the Traveling Salesperson Problem (TSP): Given a depot, a set of delivery locations and the number of vehicles starting from the depot, we need to minimize the time (or travel distance) required to visit all locations.

This repo is translation of a pythonic implementation of the brute-force solution to the VRP. VRP is an NP-Hard problem so the brute force obviously becomes very prohibitive very quickly as the number of destinations is increased or the number of vehicles is reduced.

Original Repo [link](https://github.com/ybashir/vrpfun)

To run the code, use following command in project's root directory (Note: it may take some time to download Gradle Wrapper):
```./gradlew -q clean run -PappArgs="['Sample', '3']"```