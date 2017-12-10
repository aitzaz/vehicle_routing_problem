package funcoding.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.github.slugify.Slugify;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;

import funcoding.model.Location;
import funcoding.vrp.Solution;

public class Utils {

	private static Slugify slg = new Slugify();

	public static List<Location> getLocations(String filePrefix) throws IOException {
		String filename = slg.slugify(filePrefix) + "_locations.csv";

		List<Location> locations = new CsvToBeanBuilder(new FileReader(filename))
				.withType(Location.class).build().parse();
		return locations.subList(0, Solution.LIMIT_LOCATIONS);
	}

	// TODO: Exception handling
	public static float[][] getDistances(String filePrefix, final int maxLocations) throws IOException {
		float[][] distances = new float[maxLocations][maxLocations];
		String filename = slg.slugify(filePrefix) + "_distances.csv";	// TODO: dynamic

		CSVReader csvReader = new CSVReader(new FileReader(filename));
		String[] nextLine = csvReader.readNext();	// Hack: skipping first line
		int x = 0, y  = 0;
		float duration = 0.0f;	// TODO: Dynamic ?

		while ((nextLine = csvReader.readNext()) != null) {
			x = Integer.parseInt(nextLine[0]);
			y = Integer.parseInt(nextLine[1]);
			duration = Float.parseFloat(nextLine[3]);
			distances[x][y] = distances[y][x] = duration;
		}

		return distances;
	}

	public static float sum(float... numbers) {
		float result = 0.0f;
		for (int i = 0; i < numbers.length; i++) {
			result += numbers[i];
		}

		return result;
	}
}
