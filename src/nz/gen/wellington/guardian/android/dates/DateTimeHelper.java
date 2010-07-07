package nz.gen.wellington.guardian.android.dates;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeHelper {

	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	public static DateTime parseDate(String dateString) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_TIME_FORMAT);		
		return fmt.parseDateTime(dateString);
	}

	public static DateTime now() {
		return new DateTime();
	}
	
}
