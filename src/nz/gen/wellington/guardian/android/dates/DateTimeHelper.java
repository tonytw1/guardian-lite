package nz.gen.wellington.guardian.android.dates;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeHelper {

	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	public static Date parseDate(String dateString) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_TIME_FORMAT);		
		DateTime dateTime = fmt.parseDateTime(dateString);
		if (dateTime != null) {
			return dateTime.toDate();
		}
		return null;
	}

	public static Date now() {
		return new DateTime().toDate();
	}

	public static Date yesterday() {
		return new DateTime().minusDays(1).toDate();
	}
	
}
