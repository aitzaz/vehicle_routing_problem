package funcoding.model;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Location {
	@CsvBindByName(column = "Id")
	private int id;

	@CsvBindByName(column = "Address")
	private String address;

	@CsvBindByName(column = "Lat")
	private double latitude;

	@CsvBindByName(column = "Lon")
	private double longitude;
}
