package funcoding.vrp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.Collections2;

import funcoding.model.Location;
import funcoding.utils.ListPartitioner;
import funcoding.utils.Utils;

/**
 *
 */
public class Solution {

	public static final int LIMIT_LOCATIONS = 11;
	public static final int MAX_LOCATIONS = 25;

 	private static float[][] distances;	// TODO: static or ??

	public static void main(String[] args) throws Exception {
		assert args.length > 0;
		String filePrefix = args[0];
		int partitions = Integer.valueOf(args[1]);

		List<Location> locations = Utils.getLocations(filePrefix);
		List<Integer> locationIds = locations.stream().map(Location::getId).collect(Collectors.toList());
		System.out.println("All location ids: " + locationIds);
		distances = Utils.getDistances(filePrefix, MAX_LOCATIONS);

		long startingTime = System.currentTimeMillis();

		Solution solution = new Solution();
		List<List<Integer>> shortestRoute = solution.getShortestRouteWithPartitions(locationIds, partitions);
		System.out.println("Shortest Route time: " + solution.maxLengthForRouteSet(shortestRoute) + " minutes");
		System.out.println("Shortest Route: " + shortestRoute);

		System.out.println("Time elapsed (seconds): " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startingTime));

	}

	/**
	 * This function receives all k-subsets of a route and returns the subset
	 * with minimum distance cost. Note the total time is always equal to
	 * the max time taken by any single vehicle
	 *
	 * @param locationIds
	 * @param partitions represents number of vehicles
	 */
	private List<List<Integer>> getShortestRouteWithPartitions(List<Integer> locationIds, int partitions) throws Exception {
		List<List<List<Integer>>> allShortRoutesPerPartitions = allShortRoutesWithPartitions(locationIds, partitions);
		Map<List<List<Integer>>, Float> partitionShortRoutesToLengthMap = new HashMap<>();

		for (List<List<Integer>> shortRoutesInPartition : allShortRoutesPerPartitions) {
			partitionShortRoutesToLengthMap.put(shortRoutesInPartition, maxLengthForRouteSet(shortRoutesInPartition));
		}

		List<List<Integer>> shortestRouteForPartitions = Collections.min(partitionShortRoutesToLengthMap.entrySet(),
				Map.Entry.comparingByValue()).getKey();
		return shortestRouteForPartitions;
	}

	/**
	 * Our partitions represent number of vehicles. This function yields
	 * an optimal path for each vehicle given the destinations assigned to it

	 * @param locationIds
	 * @param numVehicles
	 */
	private List<List<List<Integer>>> allShortRoutesWithPartitions(List<Integer> locationIds, int numVehicles) throws Exception {
		List<List<List<Integer>>> allShortRoutes = new ArrayList<>();
		Integer start = locationIds.get(0);

		int partitionCount = 0;
		List<List<List<Integer>>> allPartitions = ListPartitioner.getAllPartitions(locationIds.subList(1, locationIds.size()));
		for (List<List<Integer>> p : allPartitions) {
			if (p.size() == numVehicles) {
//				System.out.println("Partition count " + ++partitionCount + "\t\t" + p);
				List<List<Integer>> shortestRouteWithCurrentPartitions = new ArrayList<>();

				for (List<Integer> q : p) {
					List<Integer> subPart = new ArrayList<>();
					subPart.add(start);
					subPart.addAll(q);
					List<List<Integer>> allRoutesInPartition = allRoutes(subPart);
					List<Integer> shortestRoute = shortestRoute(allRoutesInPartition);
					shortestRouteWithCurrentPartitions.add(shortestRoute);
//					Collections.sort(shortestRouteWithCurrentPartitions, Comparator.comparing());
				}
				allShortRoutes.add(shortestRouteWithCurrentPartitions);
			}
		}

		return allShortRoutes;
	}

	/**
	 * Return all permutations of a list, each starting with the first item
	 */
	private List<List<Integer>> allRoutes(List<Integer> seq) {
		Integer start = seq.get(0);
		List<List<Integer>> allPossibleRoutes = new ArrayList<>();

		Collection<List<Integer>> permutations = Collections2.permutations(seq.subList(1, seq.size()));
		for (List<Integer> p : permutations) {
			List<Integer> route = new ArrayList<>(p);
			route.add(0, start);
			allPossibleRoutes.add(route);
		}

		return allPossibleRoutes;
	}

	/**
	 * Given a list of routes returns the minimum based on route length
	 */
	private List<Integer> shortestRoute(List<List<Integer>> allRoutesInPartition) {
		Map<List<Integer>, Float> routeLengths = new HashMap<>();
		for (List<Integer> route : allRoutesInPartition) {
			routeLengths.put(route, routeLength(route));
		}

		List<Integer> shortestRoute = Collections.min(routeLengths.entrySet(),
				Map.Entry.comparingByValue()).getKey();

		return shortestRoute;
	}

	/**
	 * Distance between first and last and consecutive elements of a list.
	 */
	private float routeLength(List<Integer> route) {
		float sum = 0.0f;
		for (int i = 1; i < route.size(); i++) {
			sum += getDistance(route.get(i), route.get(i - 1));
		}
		sum += getDistance(route.get(0), route.get(route.size() - 1));

		return sum;
	}

	private float maxLengthForRouteSet(List<List<Integer>> routes) {
		List<Float> routeLengths = new ArrayList<>();
		for (List<Integer> route : routes) {
			routeLengths.add(routeLength(route));
		}

		return Collections.max(routeLengths);
	}

	private float getDistance(int x, int y) {
		return distances[x][y];
	}
}