package nz.gen.wellington.guardian.android.activities.ui;

import java.util.Date;

import nz.gen.wellington.guardian.android.utils.DateTimeHelper;

public class DateFormatter {

	private static final String WEB_PUBLICATION_DATE_FORMAT = "EEEE d MMMM yyyy HH.mm";
	
	public static String formatAsWebPublicationDate(Date date) {
		return DateTimeHelper.format(date, WEB_PUBLICATION_DATE_FORMAT);
	}

}
