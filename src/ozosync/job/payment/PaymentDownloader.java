package ozosync.job.payment;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentHistory;
import com.paypal.base.rest.APIContext;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.PaypalAccountInfo;
import ozosync.service.PaypalApiConfigService;

@Service
public class PaymentDownloader {

    private static final int NUMBER_OF_DAY = 1;

    private Logger log = Logger.getLogger(PaymentDownloader.class);

    @Autowired
    private PaypalApiConfigService paypalApiConfigService;

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public void download() {
        try {
            Session session = sessionFactory.getCurrentSession();

            List<PaypalAccountInfo> paypalAccountInfoList = paypalApiConfigService.getConfiglist();
            for (PaypalAccountInfo paypalAccountInfo : paypalAccountInfoList) {
                try {
                    APIContext apiContext = new APIContext(paypalAccountInfo.getClientId(), paypalAccountInfo.getSecret(), "live");

                    // count={0}&start_id={1}&start_index={2}&start_time={3}&end_time={4}&payee_id={5}&sort_by={6}&sort_order={7}
                    // count={0}&start_index={2}&start_time={3}&end_time={4}

                    Map<String, String> containerMap = new HashMap<>();

                    Calendar startTime = Calendar.getInstance();
                    startTime.add(Calendar.DATE, -NUMBER_OF_DAY);
                    containerMap.put("start_time", getTimeString(startTime.getTime()));

                    Calendar endTime = Calendar.getInstance();
                    containerMap.put("end_time", getTimeString(endTime.getTime()));

                    int count = 200;
                    containerMap.put("count", Integer.toString(count));

                    int startIndex = 0;

                    PaymentHistory paymentHistory = null;
                    do {
                        containerMap.put("start_index", Integer.toString(startIndex));
                        startIndex = startIndex + count;

                        paymentHistory = Payment.list(apiContext, containerMap);

                        // todo 处理

                    } while (paymentHistory != null && paymentHistory.getCount() >= count); // 一般 > 是不会成立的

                } catch (Exception exp) {
                    log.fatal(exp);
                    log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
                }
            }
        } catch (Exception exp) {
            log.fatal(exp);
            log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
        }
    }

    private String getTimeString(Date time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return String.format("%sT%sZ", dateFormat.format(time), timeFormat.format(time));
    }
}
