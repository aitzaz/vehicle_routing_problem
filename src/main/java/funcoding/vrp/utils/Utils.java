package funcoding.vrp.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.github.slugify.Slugify;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;

import funcoding.vrp.model.Location;

public class Utils {
	private static Slugify slg = new Slugify();
	private static final int LIMIT_LOCATIONS = 12;
	private static final int MAX_LOCATIONS = 25;

	public static List<Location> getLocations(String filePrefix) throws IOException {
		String filename = slg.slugify(filePrefix) + "_locations.csv";
		List<Location> locations = new CsvToBeanBuilder(new FileReader(filename))
				.withType(Location.class).build().parse();
		return locations.subList(0, LIMIT_LOCATIONS);
	}

	public static float[][] getDistanceDurations(String filePrefix) throws IOException {
		float[][] distances = new float[MAX_LOCATIONS][MAX_LOCATIONS];
		String filename = slg.slugify(filePrefix) + "_distances.csv";
		CSVReader csvReader = new CSVReader(new FileReader(filename));
		String[] nextLine = csvReader.readNext();	// Hack: skipping first line
		int x, y;
		float duration;
		while ((nextLine = csvReader.readNext()) != null) {
			x = Integer.parseInt(nextLine[0]);
			y = Integer.parseInt(nextLine[1]);
			duration = Float.parseFloat(nextLine[3]);
			distances[x][y] = distances[y][x] = duration;
		}
		return distances;
	}
}
