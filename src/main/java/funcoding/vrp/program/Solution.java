package funcoding.vrp.program;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.Collections2;

import funcoding.vrp.model.Location;
import funcoding.vrp.utils.PartitionIterable;
import funcoding.vrp.utils.Utils;

/**
 * Brute-force solution to Vehicle Routing Problem, translated from
 * Python code at https://github.com/ybashir/vrpfun
 */
public class Solution {
 	private static float[][] distances;

	public static void main(String[] args) throws Exception {
		assert args.length != 3 : "Please provide file prefix, numVehicles and locations limit arguments.";
		String filePrefix = args[0];
		int numVehicles = Integer.valueOf(args[1]);
		int locationsLimit = Integer.valueOf(args[2]);
		List<Location> locations = Utils.getLocations(filePrefix);
		List<Integer> locationIds = locations.stream().map(Location::getId).limit(locationsLimit).collect(Collectors.toList());
		distances = Utils.getDistanceDurations(filePrefix);

		long startTimeMillis = System.currentTimeMillis();
		long beforeUsedMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		Solution solution = new Solution();
		List<List<Integer>> shortestRouteSet = solution.getShortestPartition(locationIds, numVehicles);
		try (PrintWriter out = new PrintWriter(
				new BufferedWriter(new FileWriter("output.txt", true)))) {
			out.println("Location ids set: " + locationIds + "; numVehicles: " + numVehicles);
			out.printf("Shortest route time: %.1f minutes\n", solution.maxLengthForRouteSet(shortestRouteSet));
			out.println("Shortest route: " + shortestRouteSet);
			out.println("Solution time: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTimeMillis) + " seconds");
			long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			long actualMemUsed = afterUsedMem - beforeUsedMem;
			out.printf("Peak memory usage: %d MBs\n\n", (actualMemUsed / (1024 * 1024)));
		}
		catch (IOException exception) {
			exception.printStackTrace();
			throw exception;
		}
	}

	/**
	 * This function receives all k-subsets of a route and returns the subset
	 * with minimum distance cost. Note the total time is always equal to
	 * the max time taken by any single vehicle
	 * @param locationIds
	 * @param partitions represents number of vehicles
	 */
	private List<List<Integer>> getShortestPartition(List<Integer> locationIds, int partitions) throws Exception {
		List<List<List<Integer>>> kPartitionsWithShortestRoutes = getK_PartitionsWithShortRoutes(locationIds, partitions);
		Map<List<List<Integer>>, Float> partitionToMaxLengthMap = new HashMap<>();
		for (List<List<Integer>> shortestRouteSetInPartition : kPartitionsWithShortestRoutes) {
			partitionToMaxLengthMap.put(shortestRouteSetInPartition, maxLengthForRouteSet(shortestRouteSetInPartition));
		}
		List<List<Integer>> shortestRouteForPartitions = Collections.min(partitionToMaxLengthMap.entrySet(),
				Map.Entry.comparingByValue()).getKey();
		return shortestRouteForPartitions;
	}

	/**
	 * Our partitions represent number of vehicles. This function yields
	 * an optimal path for each vehicle given the destinations assigned to it
	 */
	private List<List<List<Integer>>> getK_PartitionsWithShortRoutes(List<Integer> locationIds, int partitions) throws Exception {
		List<List<List<Integer>>> shortRoutesList = new ArrayList<>();
		Integer depot = locationIds.get(0);
		Iterable<List<List<Integer>>> partitionIterable = new PartitionIterable<>(locationIds.subList(1, locationIds.size()), partitions);
		partitionIterable.iterator().forEachRemaining(p -> {
			List<List<Integer>> shortestRouteWithCurrentPartitions = new ArrayList<>();
			for (List<Integer> q : p) {
				List<Integer> subPart = new ArrayList<>();
				subPart.add(depot);
				subPart.addAll(q);
				List<List<Integer>> allRoutesInPartition = allRoutes(subPart);
				List<Integer> shortestRoute = shortestRoute(allRoutesInPartition);
				shortestRouteWithCurrentPartitions.add(shortestRoute);
			}
			shortRoutesList.add(shortestRouteWithCurrentPartitions);
		});
		return shortRoutesList;
	}

	/**
	 * Return all permutations of a list, each starting with the first item
	 */
	private List<List<Integer>> allRoutes(List<Integer> seq) {
		Integer depot = seq.get(0);
		List<List<Integer>> allPossibleRoutes = new ArrayList<>();
		Collection<List<Integer>> permutations = Collections2.permutations(seq.subList(1, seq.size()));
		for (List<Integer> permutation : permutations) {
			List<Integer> route = new ArrayList<>();
			route.add(depot);
			route.addAll(permutation);
			allPossibleRoutes.add(route);
		}
		return allPossibleRoutes;
	}

	/**
	 * Given a list of routes returns the minimum based on route length
	 */
	private List<Integer> shortestRoute(List<List<Integer>> allRoutesInPartition) {
		Map<List<Integer>, Float> routeLengthsMap = new HashMap<>();
		for (List<Integer> route : allRoutesInPartition) {
			routeLengthsMap.put(route, routeLength(route));
		}
		List<Integer> shortestRoute = Collections.min(routeLengthsMap.entrySet(),
				Map.Entry.comparingByValue()).getKey();
		return shortestRoute;
	}

	/**
	 * Distance between first and last and consecutive elements of a list
	 */
	private float routeLength(List<Integer> route) {
		float sum = 0.0f;
		for (int i = 1; i < route.size(); i++) {
			sum += getDistance(route.get(i), route.get(i - 1));
		}
		sum += getDistance(route.get(0), route.get(route.size() - 1));
		return sum;
	}

	private float maxLengthForRouteSet(List<List<Integer>> routeSet) {
		List<Float> routeLengths = new ArrayList<>();
		for (List<Integer> route : routeSet) {
			routeLengths.add(routeLength(route));
		}
		return Collections.max(routeLengths);
	}

	private float getDistance(int x, int y) {
		return distances[x][y];
	}
}