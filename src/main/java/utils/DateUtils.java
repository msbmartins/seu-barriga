package utils;

/*   @author maramartins   */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static String getDateDifferenceDays(Integer numDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, numDays);
        return getDateFormat(cal.getTime());

    }
        public static String getDateFormat(Date date) {
            DateFormat form = new SimpleDateFormat("dd/MM/yyyy");
            return form.format(date);
    }
}
