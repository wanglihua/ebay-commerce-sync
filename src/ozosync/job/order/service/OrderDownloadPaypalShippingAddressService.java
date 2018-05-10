package ozosync.job.order.service;

import com.ebay.soap.eBLBaseComponents.ExternalTransactionType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.OrderInfo;
import ozosync.domain.OrderShippingAddressInfo;
import ozosync.domain.OrderStatus;
import ozosync.domain.schema.OrderShippingAddressSchema;
import ozosync.service.AddressFormatService;

@Service
public class OrderDownloadPaypalShippingAddressService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private AddressFormatService addressFormatService;

    public void saveOrderShippingAddress(OrderInfo orderInfo, OrderType orderDownload) {
        Session session = sessionFactory.getCurrentSession();

        // 只同步已支付及之前的订单内容
        Long statusId = orderInfo.getStatusId();
        if (statusId > OrderStatus.ID_YZF) {
            return;
        }

        // order external transaction
        Long orderId = orderInfo.getId();
        ExternalTransactionType[] externalTransactionList = orderDownload.getExternalTransaction();
        int externalTransactionLength = orderDownload.getExternalTransactionLength();
        if (externalTransactionList != null && externalTransactionLength != 0) {
            for (int index = 0; index < externalTransactionLength; index++) {
                ExternalTransactionType externalTransaction = orderDownload.getExternalTransaction(index);
                String transactionId = externalTransaction.getExternalTransactionID();

                OrderShippingAddressInfo paypalShippingAddressInDb = (OrderShippingAddressInfo) session.createCriteria(OrderShippingAddressInfo.class)
                        .add(Restrictions.eq(OrderShippingAddressSchema.transactionId, transactionId))
                        .add(Restrictions.eq(OrderShippingAddressSchema.orderId, 0L))
                        .uniqueResult();

                paypalShippingAddressInDb.setOrderId(orderId);
                session.update(paypalShippingAddressInDb);
                session.flush();

                OrderShippingAddressInfo ebayShippingAddressInDb = (OrderShippingAddressInfo) session.createCriteria(OrderShippingAddressInfo.class)
                        .add(Restrictions.eq(OrderShippingAddressSchema.addressOwner, "eBay"))
                        .add(Restrictions.eq(OrderShippingAddressSchema.orderId, orderId))
                        .uniqueResult();

                /* 优先使用 ebay 的地址，所以这里的代码就用不着了
                if (paypalShippingAddressInDb.getName() != null && !paypalShippingAddressInDb.getName().equals("") && !isAddressSame(paypalShippingAddressInDb, ebayShippingAddressInDb)) {
                    orderInfo.setAddressId(paypalShippingAddressInDb.getId());
                    session.update(orderInfo);
                }
                */
            }
        }
    }

    private String normalizePhone(String phone) {
        return addressFormatService.normalizePhone(phone);
    }

    private String nullToEmpty(String str) {
        return addressFormatService.nullToEmpty(str);
    }

    private boolean isAddressSame(OrderShippingAddressInfo address1, OrderShippingAddressInfo address2) {
        if (address1 == address2) {
            return true;
        }

        if (address1 == null || address2 == null) {
            return false;
        }

        if (!nullToEmpty(address1.getName()).equals(nullToEmpty(address2.getName()))) {
            return false;
        }

        if (!nullToEmpty(address1.getStreet1()).equals(nullToEmpty(address2.getStreet1()))) {
            return false;
        }

        if (!nullToEmpty(address1.getStreet2()).equals(nullToEmpty(address2.getStreet2()))) {
            return false;
        }

        if (!nullToEmpty(address1.getCityName()).equals(nullToEmpty(address2.getCityName()))) {
            return false;
        }

        if (!nullToEmpty(address1.getProvince()).equals(nullToEmpty(address2.getProvince()))) {
            return false;
        }

        if (!nullToEmpty(address1.getCountry()).equals(nullToEmpty(address2.getCountry()))) {
            return false;
        }

        if (!nullToEmpty(address1.getCountryName()).equals(nullToEmpty(address2.getCountryName()))) {
            return false;
        }

        if (!nullToEmpty(address1.getPhone()).equals(nullToEmpty(address2.getPhone()))) {
            return false;
        }

        if (!nullToEmpty(address1.getPostalCode()).equals(nullToEmpty(address2.getPostalCode()))) {
            return false;
        }

        return true;
    }
}
