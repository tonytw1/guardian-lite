package nz.gen.wellington.guardian.android.dates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nz.gen.wellington.guardian.android.activities.ui.Plurals;

public class DateTimeHelper {

	//private static final String TAG = "DateTimeHelper";
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	public static Date parseDate(String dateString) {
		 SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		 try {
			 return dateFormat.parse(dateString);
		} catch (ParseException e) {
			//Log.w(TAG, "Failed to parse date string: " + dateString);
		}
		return null;
	}

	public static Date now() {
	    Calendar cal = Calendar.getInstance();
	    return cal.getTime();
	}

	public static Date yesterday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -24);
		return cal.getTime();
	}		

	public static String format(Date date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
	
	public static String calculateTimeTaken(Date startTime, Date now) {
		long mills = now.getTime() - startTime.getTime();
		int seconds = new Long(mills / 1000).intValue();
		if (seconds < 60) {
			return Long.toString(seconds) + " seconds";
		}
	
		StringBuilder output = new StringBuilder();
		int minutes = (seconds / 60);
		int remainer = (seconds % 60);
		output.append(minutes + " " + Plurals.getPrural("minute", minutes));
		if (remainer > 0) {
			output.append(remainer + " " + Plurals.getPrural("second", remainer));
		}
		return output.toString();
	}
	
}
