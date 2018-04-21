package ozosync.job.order.service;

import com.ebay.soap.eBLBaseComponents.OrderType;
import java.util.Arrays;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.domain.OrderInfo;
import ozosync.job.order.database.OrderShippingAddressRepository;

@Service
public class OrderDownloadFacadeService {
    private Logger log = Logger.getLogger(OrderDownloadFacadeService.class);

    @Autowired
    private SessionFactory sessionFactory;

    private OrderShippingAddressRepository orderShippingAddressRepository = new OrderShippingAddressRepository();

    @Autowired
    private OrderDownloadService orderDownloadService;

    @Autowired
    private OrderDownloadEntryService orderDownloadEntryService;

    public void save(OrderType orderDownload, EBayApiConfigInfo apiConfigInfo) {

        try {

            Session session = sessionFactory.getCurrentSession();

            OrderInfo orderInfo = null;

            try {
                // order
                orderInfo = orderDownloadService.saveOrder(orderDownload, apiConfigInfo);

                // order entry
                orderDownloadEntryService.saveOrderEntries(orderInfo, orderDownload);
            } catch (Exception exp) {
                log.fatal(exp);
                log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
            }

            // 这样可以在保存明细出错时，继续保存收货地址
            try {
                // order shipping address
                orderShippingAddressRepository.saveOrderShippingAddress(session, orderDownload, orderInfo);
            } catch (Exception exp) {
                log.fatal(exp);
                log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
            }
        } catch (Exception exp) {
            log.fatal(exp);
            log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
        }
    }
}
