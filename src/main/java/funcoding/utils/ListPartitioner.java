package funcoding.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Ints;

public class ListPartitioner {

	/**
	 * Returns the set of all partitions for a given set
	 * e.g for [1,2], it returns [[1],[2]] and [[1,2]]
	 */
	public static List<List<List<Integer>>> getAllPartitions(List<Integer> inputList) throws Exception {
		int[] array = inputList.stream().mapToInt(i -> i).toArray();
		int[][][] allPartitions = getAllPartitions(array);

		List<List<List<Integer>>> partitions = new ArrayList<>();
		for (int[][] level2 : allPartitions) {
			List<List<Integer>> p2 = new ArrayList<>();
			for (int[] level1 : level2) {
				List<Integer> p1 = Ints.asList(level1);
				p2.add(p1);
			}
			partitions.add(p2);
		}

		return partitions;
	}

	/*
	 * https://stackoverflow.com/questions/36962150/partitions-of-a-set-storing-results-in-a-series-of-nested-lists
	 */
	private static int[][][] getAllPartitions(int[] array) throws Exception {
		int[][][] res = new int[0][][];
		int n = 1;
		for (int i = 0; i < array.length; i++) {
			n *= 2;
		}
		for (int i = 1; i < n; i += 2) {
			boolean[] contains = new boolean[array.length];
			int length = 0;
			int k = i;
			for (int j = 0; j < array.length; j++) {
				contains[j] = k % 2 == 1;
				length += k % 2;
				k /= 2;
			}
			int[] firstPart = new int[length];
			int[] secondPart = new int[array.length - length];
			int p = 0;
			int q = 0;
			for (int j = 0; j < array.length; j++) {
				if (contains[j]) {
					firstPart[p++] = array[j];
				} else {
					secondPart[q++] = array[j];
				}
			}
			int[][][] partitions;
			if (length == array.length) {
				partitions = new int[][][] {{firstPart}};
			} else {
				partitions = getAllPartitions(secondPart);
				for (int j = 0; j < partitions.length; j++) {
					int[][] partition = new int[partitions[j].length + 1][];
					partition[0] = firstPart;
					System.arraycopy(partitions[j], 0, partition, 1, partitions[j].length);
					partitions[j] = partition;
				}
			}
			int[][][] newRes = new int[res.length + partitions.length][][];
			System.arraycopy(res, 0, newRes, 0, res.length);
			System.arraycopy(partitions, 0, newRes, res.length, partitions.length);
			res = newRes;
		}
		return res;
	}
}
