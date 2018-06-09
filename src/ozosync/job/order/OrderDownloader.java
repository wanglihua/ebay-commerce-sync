package ozosync.job.order;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetOrdersCall;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.OrderStatusCodeType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import com.ebay.soap.eBLBaseComponents.PaginationType;
import com.ebay.soap.eBLBaseComponents.TradingRoleCodeType;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.job.service.EbayApiContextService;
import ozosync.service.EBayApiConfigService;
import ozosync.job.order.service.OrderDownloadFacadeService;

@Service
public class OrderDownloader {

    private Logger log = Logger.getLogger(OrderDownloader.class);

    @Autowired
    private EbayApiContextService ebayApiContextService;

    @Autowired
    private EBayApiConfigService eBayApiConfigService;

    @Autowired
    private OrderDownloadFacadeService orderDownloadFacadeService;

    private final int NUMBER_OF_DAY = 1;

    @SuppressWarnings("unchecked")
    public void download() {
        try {
            List<EBayApiConfigInfo> ebayApiConfigList = eBayApiConfigService.getConfiglist();
            for (EBayApiConfigInfo ebayApiConfigInfo : ebayApiConfigList) {
                try {
                    GetOrdersCall getOrdersCall = createGetOrdersCall(ebayApiConfigInfo);

                    int pageNumber = 0;
                    // int orderCount = 0;

                    do {
                        getOrdersCall.getPagination().setPageNumber(++pageNumber);
                        OrderType[] orderList = getOrdersCall.getOrders();
                        if (getOrdersCall.getReturnedReturnedOrderCountActual() > 0) {
                            for (OrderType orderDownload : orderList) {
                                // System.out.println("order: " + ++orderCount);
                                // System.out.println("order id: " + order.getOrderID());
                                log.error("同步订单： order id: " + orderDownload.getOrderID());

                                if (orderDownload.getPaidTime() != null) {
                                    long payDays = (new Date().getTime() - orderDownload.getPaidTime().getTime().getTime()) / (1000 * 24 * 3600);
                                    if (payDays > 10) { // 支付时间超过了 10 天
                                        log.error("忽略10天前支付的订单");
                                        continue;
                                    }
                                }

                                // 保存订单信息，订单明细，订单邮寄地址等
                                orderDownloadFacadeService.save(orderDownload, ebayApiConfigInfo);
                            }
                        }

                    } while (getOrdersCall.getReturnedHasMoreOrders());
                } catch (Exception exp) {
                    log.fatal(exp.getMessage(), exp);
                }
            }
        } catch (Exception exp) {
            log.fatal(exp.getMessage(), exp);
        }
    }


    private GetOrdersCall createGetOrdersCall(EBayApiConfigInfo ebayApiConfigInfo) {
        ApiContext apiContext = ebayApiContextService.getApiContextByConfig(ebayApiConfigInfo);

        GetOrdersCall getOrdersCall = new GetOrdersCall(apiContext);
        getOrdersCall.setNumberOfDays(NUMBER_OF_DAY);
        getOrdersCall.setOrderRole(TradingRoleCodeType.SELLER);
        getOrdersCall.setOrderStatus(OrderStatusCodeType.ALL);
        getOrdersCall.addDetailLevel(DetailLevelCodeType.RETURN_ALL);
        getOrdersCall.setIncludeFinalValueFee(true);

        PaginationType pagination = new PaginationType();
        pagination.setEntriesPerPage(100);
        getOrdersCall.setPagination(pagination);

        return getOrdersCall;
    }
}
