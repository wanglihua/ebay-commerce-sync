package ozosync.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.junit.Test;

public class DateTest {

    @Test
    public void getToday() throws Exception {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = simpleDateFormat.parse(simpleDateFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date tomorrow = simpleDateFormat.parse(simpleDateFormat.format(calendar.getTime()));

        System.out.println(today);
        System.out.println(tomorrow);
    }
}
