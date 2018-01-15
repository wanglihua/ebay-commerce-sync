package ozosync.job.shop.database;

import com.ebay.soap.eBLBaseComponents.AddressType;
import com.ebay.soap.eBLBaseComponents.CompleteStatusCodeType;
import com.ebay.soap.eBLBaseComponents.OrderStatusCodeType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import com.ebay.soap.eBLBaseComponents.TransactionArrayType;
import com.ebay.soap.eBLBaseComponents.TransactionType;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import ozosync.domain.EBayApiConfigInfo;
import ozosync.domain.ExchangeRateInfo;
import ozosync.domain.OrderHistoryInfo;
import ozosync.domain.OrderInfo;
import ozosync.domain.OrderShippingAddressInfo;
import ozosync.domain.OrderStatus;
import ozosync.domain.StatusInfo;
import ozosync.domain.schema.OrderSchema;
import ozosync.domain.schema.OrderShippingAddressSchema;
import ozosync.service.ExchangeRateService;

public class OrderSaveExecuter extends DataBaseExecuter {

    private OrderType orderDownload;

    public void setOrderDownload(OrderType orderDownload) {
        this.orderDownload = orderDownload;
    }

    private EBayApiConfigInfo apiConfigInfo;

    public void setApiConfigInfo(EBayApiConfigInfo apiConfigInfo) {
        this.apiConfigInfo = apiConfigInfo;
    }

    public OrderSaveExecuter(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    protected Object executeInternal(Session session) {
        // order
        OrderInfo orderInfo = saveOrder(session, orderDownload);

        // order shipping address
        saveOrderShippingAddress(session, orderDownload, orderInfo);

        return orderInfo;
    }

    private OrderInfo saveOrder(Session session, OrderType orderDownload) {
        String orderCode = orderDownload.getOrderID();
        String[] orderCodeSplitArray = orderCode.split("-");
        if (orderCodeSplitArray.length > 1 && orderCodeSplitArray[1].length() > 13) {
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
        orderInDb.setShopAccount(apiConfigInfo.getShopAccount());
        orderInDb.setTranId(orderDownload.getShippingDetails().getSellingManagerSalesRecordNumber().toString());
        orderInDb.setFullTranId(apiConfigInfo.getShopAccount().getCode() + orderDownload.getShippingDetails().getSellingManagerSalesRecordNumber());

        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        orderInDb.setCreatedTime(orderDownload.getCreatedTime().getTime());
        orderInDb.setModifiedTime(orderDownload.getCheckoutStatus().getLastModifiedTime().getTime());

        if (orderDownload.getPaidTime() != null) {
            orderInDb.setPaidTime(orderDownload.getPaidTime().getTime());
        }

        if (orderDownload.getShippedTime() != null) {
            orderInDb.setShippedTime(orderDownload.getShippedTime().getTime());
        }

        ExchangeRateInfo exchangeRateInfo = new ExchangeRateService().getAudExchangeRate();

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

        Long statusId = orderInDb.getStatus().getId();

        // 如果是新的订单，一般是新增的订单，系统数据库中还没有的订单
        if (statusId < OrderStatus.ID_YCJ
                && orderDownload.getPaidTime() == null) { // statusId < OrderStatus.ID_YCJ 这个状态检查不能少，否则 待配货，已发货的状态可能会被覆盖
            StatusInfo statusYcj = (StatusInfo) session.get(StatusInfo.class, OrderStatus.ID_YCJ);
            orderInDb.setStatus(statusYcj); // 这个状态，有可能会被接下来的状态逻辑所修改，可能会，更进一步改成 “已支付”, “已完成”
        }

        // 有可能是新增的订单（最有可能），或者是原来 已创建 的状态过来的。这时支付时间不为空
        if (statusId < OrderStatus.ID_YZF
                && orderDownload.getPaidTime() != null
                && orderDownload.getCheckoutStatus().getStatus() == CompleteStatusCodeType.COMPLETE) { // statusId < OrderStatus.ORDER_STATUS_YZF 这个状态检查不能少，否则 待配货，已发货的状态可能会被覆盖
            StatusInfo statusYzf = (StatusInfo) session.get(StatusInfo.class, OrderStatus.ID_YZF);
            orderInDb.setStatus(statusYzf); // 这个状态，有可能会被接下来的状态逻辑所修改，可能会，更进一步改成 “已完成”
        }

        if (orderDownload.getShippedTime() != null
                && orderDownload.getPaidTime() != null
                && orderDownload.getCheckoutStatus().getStatus() == CompleteStatusCodeType.COMPLETE
                && orderDownload.getOrderStatus() == OrderStatusCodeType.COMPLETED) {
            StatusInfo statusYwc = (StatusInfo) session.get(StatusInfo.class, OrderStatus.ID_YWC);
            orderInDb.setStatus(statusYwc);
        }

        /*
           // 如果 下载订单里面的 OrderStatus 是 Completed， 且订单的发货时间不为空，订单状态就为 已完成
           if order_in_db.ShippedTime.Valid == true {
               order_in_db.StatusId = models.ORDER_STATUS_YWC
           }

         */

        if (orderNotExist) {
            session.save(orderInDb);
            session.flush();

            insertOrderHistory(session, orderInDb, "订单下载新增订单");
        } else {
            orderInDb.setUpdateTime(new Date());
            session.update(orderInDb);
            session.flush();

            if (statusId != orderInDb.getStatus().getId()) {
                insertOrderHistory(session, orderInDb, "订单下载状态更新");
            }
        }

        session.flush();

        return orderInDb;
    }

    private void insertOrderHistory(Session session, OrderInfo orderInfo, String description) {
        OrderHistoryInfo orderHistoryInfo = new OrderHistoryInfo();
        orderHistoryInfo.setOrder(orderInfo);
        orderHistoryInfo.setOrderCode(orderInfo.getCode());
        orderHistoryInfo.setStatus(orderInfo.getStatus());
        orderHistoryInfo.setStatusName(orderInfo.getStatus().getName());
        orderHistoryInfo.setDescription(description);
        orderHistoryInfo.setUserId(0);
        orderHistoryInfo.setDelete(0);

        session.save(orderHistoryInfo);
        session.flush();
    }

    private void saveOrderEntries(Session session, OrderType orderDownload) {
        // 保存订单明细
        TransactionArrayType transactionArray = orderDownload.getTransactionArray();

        if (transactionArray != null && transactionArray.getTransactionLength() > 0) {
            int transactionLength = transactionArray.getTransactionLength();
            for (int index = 0; index < transactionLength; index++) {
                TransactionType transaction = transactionArray.getTransaction(index);
                System.out.println("transaction id: " + transaction.getTransactionID());
            }
        }
    }

    private void saveOrderShippingAddress(Session session, OrderType orderDownload, OrderInfo orderInfo) {

        AddressType shippingAddress = orderDownload.getShippingAddress();

        if (!shippingAddress.getAddressID().equals("")) {
            String orderIdProperytyName = Property.forName(OrderShippingAddressSchema.order)
                    .getProperty(OrderSchema.id)
                    .getPropertyName();

            OrderShippingAddressInfo shippingAddressInDb = (OrderShippingAddressInfo) session.createCriteria(OrderShippingAddressInfo.class)
                    .add(Restrictions.and(
                            Restrictions.eq(OrderShippingAddressSchema.addressOwner, shippingAddress.getAddressOwner().value()),
                            Restrictions.eq(orderIdProperytyName, orderInfo.getId())
                    ))
                    .uniqueResult();

            boolean orderHasShippingAddress = shippingAddressInDb != null;

            if (shippingAddressInDb == null) {
                shippingAddressInDb = new OrderShippingAddressInfo();
            }

            shippingAddressInDb.setOrder(orderInfo);
            shippingAddressInDb.setName(shippingAddress.getName());
            shippingAddressInDb.setStreet1(shippingAddress.getStreet1());
            shippingAddressInDb.setStreet2(shippingAddress.getStreet2());
            shippingAddressInDb.setPhone(normalizePhone(shippingAddress.getPhone()));
            shippingAddressInDb.setCityName(shippingAddress.getCityName());
            shippingAddressInDb.setProvince(shippingAddress.getStateOrProvince());
            shippingAddressInDb.setCountry(shippingAddress.getCountry().value());
            shippingAddressInDb.setCountryName(shippingAddress.getCountryName());
            shippingAddressInDb.setPostalCode(shippingAddress.getPostalCode());
            shippingAddressInDb.setAddressId(shippingAddress.getAddressID());
            shippingAddressInDb.setAddressOwner(shippingAddress.getAddressOwner().value());
            shippingAddressInDb.setExternalAddressId(shippingAddress.getExternalAddressID());

            Long addressId = shippingAddressInDb.getId();
            if (orderHasShippingAddress) {
                // do update
                session.update(shippingAddressInDb);
                session.flush();

                addressId = shippingAddressInDb.getId();
            } else {
                // do insert
                session.save(shippingAddressInDb);
                session.flush();

                addressId = shippingAddressInDb.getId();
            }

            orderInfo.setAddressId(addressId);
            session.update(orderInfo);
            session.flush();
        }
    }

    private String normalizePhone(String phone) {
        if (phone != null && phone.equals("")) {
            phone = phone.replace(" ", "");

            if (phone.length() > 4) {
                return phone.substring(0, 4) + " " + phone.substring(4);
            }
        }

        return phone;
    }
}
