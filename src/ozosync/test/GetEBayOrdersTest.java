package ozosync.test;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiException;
import com.ebay.sdk.SdkException;
import com.ebay.sdk.call.GetOrdersCall;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.OrderStatusCodeType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import com.ebay.soap.eBLBaseComponents.PaginationType;
import com.ebay.soap.eBLBaseComponents.TradingRoleCodeType;
import org.junit.Test;

public class GetEBayOrdersTest {

    private void getSalesOrders() throws ApiException, SdkException, Exception {
        ApiContext apiContext = TestUtil.getApiContext();

        GetOrdersCall getOrdersCall = new GetOrdersCall(apiContext);
        getOrdersCall.setNumberOfDays(1);
        getOrdersCall.setOrderRole(TradingRoleCodeType.SELLER);
        getOrdersCall.setOrderStatus(OrderStatusCodeType.ALL);
        getOrdersCall.addDetailLevel(DetailLevelCodeType.RETURN_ALL);
        getOrdersCall.setIncludeFinalValueFee(true);

        PaginationType pagination = new PaginationType();
        pagination.setEntriesPerPage(100);
        getOrdersCall.setPagination(pagination);

        int pageNumber = 0;
        int orderCount = 0;

        do {
            getOrdersCall.getPagination().setPageNumber(++pageNumber);
            OrderType[] orderList = getOrdersCall.getOrders();
            if (getOrdersCall.getReturnedReturnedOrderCountActual() > 0) {
                for (OrderType order : orderList) {
                    System.out.println("order: " + ++orderCount);
                    System.out.println("order id: " + order.getOrderID());
                    System.out.println("created time: " + order.getCreatedTime());
                    System.out.println("last modified time: " + order.getCheckoutStatus().getLastModifiedTime());
                    System.out.println("paid time: " + order.getPaidTime());
                    System.out.println("shipped time: " + order.getShippedTime());

                    /*
        orderInDb.setCreatedTime(orderDownload.getCreatedTime().getTime());
        orderInDb.setModifiedTime(orderDownload.getCheckoutStatus().getLastModifiedTime().getTime());
        orderInDb.setPaidTime(orderDownload.getPaidTime().getTime());
        orderInDb.setShippedTime(orderDownload.getShippedTime().getTime());

                     */

                    /*
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

                    ShippingServiceOptionsType shippingServiceOptions = order.getShippingServiceSelected();
                    if (shippingServiceOptions == null) {
                        if (order.getCheckoutStatus().getStatus() == CompleteStatusCodeType.COMPLETE) {
                            shippingServiceOptions = order.getShippingDetails().getShippingServiceOptions(0);
                        } else {
                            shippingServiceOptions = new ShippingServiceOptionsType();
                        }
                    }
                    */

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
                }
            }

        } while (getOrdersCall.getReturnedHasMoreOrders());
    }

    @Test
    public void getEBayOrders() {
        try {
            getSalesOrders();
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SdkException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}