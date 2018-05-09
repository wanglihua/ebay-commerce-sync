package ozosync.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
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

        Calendar to = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println(dateFormatter.format(to.getTime()));
    }

    @Test
    public void learn() {
        System.out.println("集合实例!\n");
        int size;
        Set<String> collection = new HashSet<>();
        String str1 = "Yellow", str2 = "White", str3 =
                "Green", str4 = "Blue";
        Iterator iterator;
        collection.add(str1);
        collection.add(str2);
        collection.add(str3);
        collection.add(str4);
        System.out.print("集合数据: ");
        iterator = collection.iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
        System.out.println();
        collection.remove(str2);
        System.out.println("删除之后 [" + str2 + "]\n");
        System.out.print("现在集合的数据是: ");
        iterator = collection.iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
        System.out.println();
        size = collection.size();
        System.out.println("集合大小: " + size + "\n");

        List stuff = Arrays.asList(new String[]{"a", "b"});
        List list = new ArrayList<String>(stuff);
        list = Collections.unmodifiableList(list);
        try {
            list.set(0, "new value");
        } catch (UnsupportedOperationException e) {
        }
        Set set = new HashSet(stuff);
        set = Collections.unmodifiableSet(set);
        Map map = new HashMap();
        map = Collections.unmodifiableMap(map);
        System.out.println("集合现在是只读");
    }

    @Test
    public void learn1() {
        Integer i = new Integer(1);
        System.out.println(i.toString());
    }

    @Test
    public void format() {
        Map<String, String> containerMap = new HashMap<>();

        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.DATE, -1);
        containerMap.put("start_time", getTimeString(startTime.getTime()));

        Calendar endTime = Calendar.getInstance();
        containerMap.put("end_time", getTimeString(endTime.getTime()));

        System.out.println(containerMap);
    }


    private String getTimeString(Date time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return String.format("%sT%sZ", dateFormat.format(time), timeFormat.format(time));
    }

    @Test
    public void testDateAddHours() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 8);
        System.out.println(calendar.getTime());
    }
}
