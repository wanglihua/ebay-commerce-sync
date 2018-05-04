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
                        int i = 1 + 1;

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

/*

{
  "id": "PAY-1BP41925BA069714SK6SKIAY",
  "intent": "authorize",
  "state": "created",
  "payer":
  {
    "payment_method": "paypal"
  },
  "transactions": [
  {
    "amount":
    {
      "total": "30.11",
      "currency": "USD",
      "details":
      {
        "subtotal": "30.00",
        "tax": "0.07",
        "shipping": "0.03",
        "insurance": "0.01",
        "handling_fee": "1.00",
        "shipping_discount": "-1.00"
      }
    },
    "description": "This is the payment transaction description.",
    "custom": "EBAY_EMS_90048630024435",
    "invoice_number": "48787589673",
    "soft_descriptor": "ECHI5786786",
    "payment_options":
    {
      "allowed_payment_method": "INSTANT_FUNDING_SOURCE",
      "recurring_flag": false
    },
    "item_list":
    {
      "items": [
      {
        "name": "hat",
        "sku": "1",
        "description": "Brown color hat",
        "price": "3.00",
        "currency": "USD",
        "tax": "0.01",
        "quantity": 5
      },
      {
        "name": "handbag",
        "sku": "product34",
        "description": "Black color hand bag",
        "price": "15.00",
        "currency": "USD",
        "tax": "0.02",
        "quantity": 1
      }],
      "shipping_address":
      {
        "recipient_name": "Hello World",
        "line1": "4thFloor",
        "line2": "unit#34",
        "city": "SAn Jose",
        "state": "CA",
        "postal_code": "95131",
        "country_code": "US",
        "phone": "011862212345678"
      }
    },
    "related_resources": []
  }],
  "note_to_payer": "Contact us for any questions on your order.",
  "create_time": "2016-08-05T14:34:42Z",
  "links": [
  {
    "href": "https://api.sandbox.paypal.com/v1/payments/payment/PAY-1BP41925BA069714SK6SKIAY",
    "rel": "self",
    "method": "GET"
  },
  {
    "href": "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=EC-511429312Y0182227",
    "rel": "approval_url",
    "method": "REDIRECT"
  },
  {
    "href": "https://api.sandbox.paypal.com/v1/payments/payment/PAY-1BP41925BA069714SK6SKIAY/execute",
    "rel": "execute",
    "method": "POST"
  }]
}

 */