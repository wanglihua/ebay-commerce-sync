package ozosync.job.order.service;

import com.ebay.soap.eBLBaseComponents.OrderType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.domain.OrderInfo;

import java.util.Arrays;

@Service
public class OrderDownloadFacadeService {

    private Logger log = Logger.getLogger(OrderDownloadFacadeService.class);

    @Autowired
    private OrderDownloadService orderDownloadService;

    @Autowired
    private OrderDownloadEntryService orderDownloadEntryService;

    @Autowired
    private OrderDownloadEbayShippingAddressService orderDownloadEbayShippingAddressService;

    @Autowired
    private OrderDownloadPaypalShippingAddressService orderDownloadPaypalShippingAddressService;

    @Autowired
    private OrderDownloadExternalTransactionService orderDownloadExternalTransactionService;


    public void save(OrderType orderDownload, EBayApiConfigInfo apiConfigInfo) {

        try {

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
                orderDownloadEbayShippingAddressService.saveOrderShippingAddress(orderInfo, orderDownload);
            } catch (Exception exp) {
                log.fatal(exp);
                log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
            }

            try {
                orderDownloadExternalTransactionService.saveOrderExternalTransaction(orderInfo, orderDownload);
            } catch (Exception exp) {
                log.fatal(exp);
                log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
            }

            try {
                orderDownloadPaypalShippingAddressService.saveOrderShippingAddress(orderInfo, orderDownload);
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
