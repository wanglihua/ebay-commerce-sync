package ozosync.job.shop;

import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.call.GetOrdersCall;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.OrderStatusCodeType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import com.ebay.soap.eBLBaseComponents.PaginationType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;
import com.ebay.soap.eBLBaseComponents.TradingRoleCodeType;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.job.shop.database.GetEBayApiConfigListExecuter;
import ozosync.job.shop.database.OrderSaveExecuter;

public class OrderDownloader {

    private Logger log = Logger.getLogger(OrderDownloader.class);

    private GetEBayApiConfigListExecuter getEBayApiConfigListExecuter;
    private OrderSaveExecuter orderSaveExecuter;


    public OrderDownloader(SessionFactory sessionFactory) {
        this.getEBayApiConfigListExecuter = new GetEBayApiConfigListExecuter(sessionFactory);
        this.orderSaveExecuter = new OrderSaveExecuter(sessionFactory);
    }

    @SuppressWarnings("unchecked")
    public void download() {
        try {
            List<EBayApiConfigInfo> ebayApiConfigList = (List<EBayApiConfigInfo>) this.getEBayApiConfigListExecuter.execute();
            for (EBayApiConfigInfo ebayApiConfigInfo : ebayApiConfigList) {
                GetOrdersCall getOrdersCall = createGetOrdersCall(ebayApiConfigInfo);

                int pageNumber = 0;
                // int orderCount = 0;

                do {
                    getOrdersCall.getPagination().setPageNumber(++pageNumber);
                    OrderType[] orderList = getOrdersCall.getOrders();
                    if (getOrdersCall.getReturnedReturnedOrderCountActual() > 0) {
                        for (OrderType order : orderList) {
                            // System.out.println("order: " + ++orderCount);
                            // System.out.println("order id: " + order.getOrderID());
                            log.error("同步订单： order id: " + order.getOrderID());

                            // 保存订单信息，订单明细，订单邮寄地址等
                            saveOrder(ebayApiConfigInfo, order);

                            /*

                            // 保存订单明细
                            TransactionArrayType transactionArray = order.getTransactionArray();

                            if (transactionArray != null && transactionArray.getTransactionLength() > 0) {
                                int transactionLength = transactionArray.getTransactionLength();
                                for (int index = 0; index < transactionLength; index++) {
                                    TransactionType transaction = transactionArray.getTransaction(index);
                                    System.out.println("transaction id: " + transaction.getTransactionID());
                                }
                            }

                            ExternalTransactionType[] externalTransactions = order.getExternalTransaction();
                            if (externalTransactions != null && externalTransactions.length > 0) {
                                for (ExternalTransactionType externalTransaction : externalTransactions) {
                                    System.out.println("external transaction id: " + externalTransaction.getExternalTransactionID());
                                }
                            }

                            // 保存订单邮寄地址
                            ShippingServiceOptionsType shippingServiceOptions = order.getShippingServiceSelected();
                            if (shippingServiceOptions == null) {
                                if (order.getCheckoutStatus().getStatus() == CompleteStatusCodeType.COMPLETE) {
                                    shippingServiceOptions = order.getShippingDetails().getShippingServiceOptions(0);
                                } else {
                                    shippingServiceOptions = new ShippingServiceOptionsType();
                                }
                            }

                            //1. shipping.ShippingService;
                            //2. shipping.ShippingServiceCost.Value;
                            //3. salesTax;
                            //4. order.AdjustmentAmount.Value;
                            //5. order.Total.Value;
                            //6. order.CheckoutStatus.Status;
                            //7. order.CheckoutStatus.eBayPaymentStatus;
                            //8. order.CheckoutStatus.PaymentMethod;
                            //9. shipAddress;

                            System.out.println();
                            */
                        }
                    }

                } while (getOrdersCall.getReturnedHasMoreOrders());
            }
        } catch (Exception exp) {
            log.fatal(exp.getMessage());
            log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
        }
    }

    private ApiContext getApiContextByConfig(EBayApiConfigInfo ebayApiConfigInfo) {
        ApiContext apiContext = new ApiContext();

        ApiCredential apiCredential = apiContext.getApiCredential();

        ApiAccount apiAccount = new ApiAccount();
        apiAccount.setApplication(ebayApiConfigInfo.getAppId());
        apiAccount.setCertificate(ebayApiConfigInfo.getCertId());
        apiAccount.setDeveloper(ebayApiConfigInfo.getDevId());

        apiCredential.setApiAccount(apiAccount);

        apiCredential.seteBayToken(ebayApiConfigInfo.getToken());
        apiContext.setApiServerUrl("https://api.ebay.com/wsapi");

        apiContext.getApiLogging().setLogSOAPMessages(true);// This will log SOAP requests and responses

        apiContext.setSite(SiteCodeType.AUSTRALIA);

        return apiContext;
    }

    private GetOrdersCall createGetOrdersCall(EBayApiConfigInfo ebayApiConfigInfo) {
        ApiContext apiContext = getApiContextByConfig(ebayApiConfigInfo);

        GetOrdersCall getOrdersCall = new GetOrdersCall(apiContext);
        getOrdersCall.setNumberOfDays(1);
        getOrdersCall.setOrderRole(TradingRoleCodeType.SELLER);
        getOrdersCall.setOrderStatus(OrderStatusCodeType.ALL);
        getOrdersCall.addDetailLevel(DetailLevelCodeType.RETURN_ALL);
        getOrdersCall.setIncludeFinalValueFee(true);

        PaginationType pagination = new PaginationType();
        pagination.setEntriesPerPage(100);
        getOrdersCall.setPagination(pagination);

        return getOrdersCall;
    }

    private void saveOrder(EBayApiConfigInfo apiConfigInfo, OrderType order) {
        try {
            orderSaveExecuter.setOrderDownload(order);
            orderSaveExecuter.setApiConfigInfo(apiConfigInfo);
            orderSaveExecuter.execute();
        } catch (Exception exp) {
            log.fatal(exp.getMessage());
            log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
        }
    }
}
