package ozosync.job.shop.database;

import com.ebay.soap.eBLBaseComponents.CancelStatusCodeType;
import com.ebay.soap.eBLBaseComponents.CompleteStatusCodeType;
import com.ebay.soap.eBLBaseComponents.OrderStatusCodeType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceOptionsType;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.domain.ExchangeRateInfo;
import ozosync.domain.OrderInfo;
import ozosync.domain.OrderStatus;
import ozosync.domain.OrderTypeId;
import ozosync.domain.ShopAccountInfo;
import ozosync.domain.schema.OrderSchema;

public class OrderRepository {

    private OrderHistoryRepository orderHistoryRepository = new OrderHistoryRepository();
    private ExchangeRateRespository exchangeRateRespository = new ExchangeRateRespository();


    public OrderInfo saveOrder(Session session, OrderType orderDownload, EBayApiConfigInfo apiConfigInfo) {
        String orderCode = orderDownload.getOrderID();
        String[] orderCodeSplitArray = orderCode.split("-");
        if (orderCodeSplitArray.length > 1 && orderCodeSplitArray[1].length() >= 13) {
            orderCode = orderCodeSplitArray[1];
        }

        OrderInfo orderInDb = (OrderInfo) session.createCriteria(OrderInfo.class)
                .add(Restrictions.eq(OrderSchema.code, orderCode))
                .uniqueResult();

        boolean orderNotExist = orderInDb == null;
        if (orderNotExist) {
            orderInDb = new OrderInfo();
        }

        orderInDb.setCode(orderCode);
        orderInDb.setShopId(apiConfigInfo.getShopId());
        orderInDb.setTranId(orderDownload.getShippingDetails().getSellingManagerSalesRecordNumber().toString());

        ShopAccountInfo shopAccountInfo = (ShopAccountInfo) session.get(ShopAccountInfo.class, apiConfigInfo.getShopId());
        orderInDb.setFullTranId(shopAccountInfo.getCode() + orderDownload.getShippingDetails().getSellingManagerSalesRecordNumber());

        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        orderInDb.setCreatedTime(orderDownload.getCreatedTime().getTime());
        orderInDb.setModifiedTime(orderDownload.getCheckoutStatus().getLastModifiedTime().getTime());

        if (orderDownload.getPaidTime() != null) {
            orderInDb.setPaidTime(orderDownload.getPaidTime().getTime());
        }

        if (orderDownload.getShippedTime() != null) {
            orderInDb.setShippedTime(orderDownload.getShippedTime().getTime());
        }

        ExchangeRateInfo exchangeRateInfo = exchangeRateRespository.getAudExchangeRate(session);

        orderInDb.setExchangeRate(new BigDecimal(exchangeRateInfo.getRate(), MathContext.DECIMAL64));

        orderInDb.setAdjustmentAmountCurrency(orderDownload.getAdjustmentAmount().getCurrencyID().value());
        orderInDb.setAdjustmentAmount(new BigDecimal(orderDownload.getAdjustmentAmount().getValue(), MathContext.DECIMAL64));
        orderInDb.setAdjustmentAmountRmb(orderInDb.getAdjustmentAmount().multiply(new BigDecimal(exchangeRateInfo.getRate(), MathContext.DECIMAL64)));

        orderInDb.setAmountPaidCurrency(orderDownload.getAmountPaid().getCurrencyID().value());
        orderInDb.setAmountPaid(new BigDecimal(orderDownload.getAmountPaid().getValue(), MathContext.DECIMAL64));
        orderInDb.setAmountPaidRmb(orderInDb.getAmountPaid().multiply(new BigDecimal(exchangeRateInfo.getRate(), MathContext.DECIMAL64)));

        orderInDb.setAmountSavedCurrency(orderDownload.getAmountSaved().getCurrencyID().value());
        orderInDb.setAmountSaved(new BigDecimal(orderDownload.getAmountSaved().getValue(), MathContext.DECIMAL64));
        orderInDb.setAmountSavedRmb(orderInDb.getAmountSaved().multiply(new BigDecimal(exchangeRateInfo.getRate(), MathContext.DECIMAL64)));

        orderInDb.setTotalAmountCurrency(orderDownload.getTotal().getCurrencyID().value());
        orderInDb.setTotalAmount(new BigDecimal(orderDownload.getTotal().getValue(), MathContext.DECIMAL64));
        orderInDb.setTotalAmountRmb(orderInDb.getTotalAmount().multiply(new BigDecimal(exchangeRateInfo.getRate(), MathContext.DECIMAL64)));

        orderInDb.setSubtotalAmountCurrency(orderDownload.getSubtotal().getCurrencyID().value());
        orderInDb.setSubtotalAmount(new BigDecimal(orderDownload.getSubtotal().getValue(), MathContext.DECIMAL64));
        orderInDb.setSubtotalAmountRmb(orderInDb.getSubtotalAmount().multiply(new BigDecimal(exchangeRateInfo.getRate(), MathContext.DECIMAL64)));

        orderInDb.setSellerEmail(orderDownload.getSellerEmail());
        orderInDb.setSellerUserId(orderDownload.getSellerUserID());

        orderInDb.setBuyerEmail(orderDownload.getTransactionArray().getTransaction(0).getBuyer().getEmail());
        orderInDb.setBuyerUserId(orderDownload.getBuyerUserID());
        orderInDb.setBuyerCheckoutMessage(orderDownload.getBuyerCheckoutMessage());

        orderInDb.setPaymentMethods(orderDownload.getCheckoutStatus().getPaymentMethod().value());

        orderInDb.setEBayStatus(orderDownload.getOrderStatus().value());

        //2. shipping.ShippingServiceCost.Value;

        ShippingServiceOptionsType shippingOptions = orderDownload
                .getShippingServiceSelected();
        if (shippingOptions == null) {
            if (orderDownload.getCheckoutStatus().getStatus() == CompleteStatusCodeType.COMPLETE) {
                shippingOptions = orderDownload.getShippingDetails()
                        .getShippingServiceOptions(0);
            } else {
                shippingOptions = new ShippingServiceOptionsType();
            }
        }

        if (shippingOptions.getShippingServiceCost() != null) {
            orderInDb.setExpressFee(new BigDecimal(shippingOptions.getShippingServiceCost().getValue(), MathContext.DECIMAL64));
        }

        Long statusId = orderInDb.getStatusId();

        // 如果是新的订单，一般是新增的订单，系统数据库中还没有的订单
        if (statusId < OrderStatus.ID_YCJ) { // statusId < OrderStatus.ID_YCJ 这个状态检查不能少，否则 待配货，已发货的状态可能会被覆盖 //  这个条件先不用 && orderDownload.getPaidTime() == null
            orderInDb.setStatusId(OrderStatus.ID_YCJ); // 这个状态，有可能会被接下来的状态逻辑所修改，可能会，更进一步改成 “已支付”, “已完成”
        }

        // 有可能是新增的订单（最有可能），或者是原来 已创建 的状态过来的。这时支付时间不为空
        if (statusId < OrderStatus.ID_YZF
                && orderDownload.getPaidTime() != null
                && orderDownload.getCheckoutStatus().getStatus() == CompleteStatusCodeType.COMPLETE) { // statusId < OrderStatus.ORDER_STATUS_YZF 这个状态检查不能少，否则 待配货，已发货的状态可能会被覆盖
            orderInDb.setStatusId(OrderStatus.ID_YZF); // 这个状态，有可能会被接下来的状态逻辑所修改，可能会，更进一步改成 “已完成”
        }

        // 平台已取消的订单的处理
        if (orderDownload.getCancelStatus() == CancelStatusCodeType.CANCEL_COMPLETE) { // 如果用户在平台上已取消订单
            orderInDb.setStatusId(OrderStatus.ID_YQX);
        }

        // 已发货
        if (orderDownload.getShippedTime() != null
                && orderDownload.getPaidTime() != null
                && orderDownload.getCheckoutStatus().getStatus() == CompleteStatusCodeType.COMPLETE) {
            orderInDb.setStatusId(OrderStatus.ID_YFH);
        }

        // 已完成
        if (orderDownload.getShippedTime() != null
                && orderDownload.getPaidTime() != null
                && orderDownload.getCheckoutStatus().getStatus() == CompleteStatusCodeType.COMPLETE
                && orderDownload.getOrderStatus() == OrderStatusCodeType.COMPLETED) {
            orderInDb.setStatusId(OrderStatus.ID_YWC);
        }

        if (orderNotExist) {
            orderInDb.setTypeId(OrderTypeId.ZCDD);
            session.save(orderInDb);
            session.flush();

            orderHistoryRepository.insertOrderHistory(session, orderInDb, "订单下载新增订单");
        } else {
            orderInDb.setUpdateTime(new Date());
            session.update(orderInDb);
            session.flush();

            if (statusId != orderInDb.getStatusId()) {
                orderHistoryRepository.insertOrderHistory(session, orderInDb, "订单下载状态更新");
            }
        }

        session.flush();

        return orderInDb;
    }
}
