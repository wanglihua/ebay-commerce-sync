package ozosync.job.shop.database;

import com.ebay.soap.eBLBaseComponents.AddressType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ozosync.domain.OrderInfo;
import ozosync.domain.OrderShippingAddressInfo;
import ozosync.domain.OrderStatus;
import ozosync.domain.schema.OrderShippingAddressSchema;

public class OrderShippingAddressRepository {

    public void saveOrderShippingAddress(Session session, OrderType orderDownload, OrderInfo orderInfo) {
        // 只同步已支付及之前的订单内容
        Long statusId = orderInfo.getStatusId();
        if (statusId > OrderStatus.ID_YZF) {
            return;
        }

        AddressType shippingAddress = orderDownload.getShippingAddress();

        if (shippingAddress != null) {

            String addressOwner = "eBay";
            if (shippingAddress.getAddressOwner() != null) {
                addressOwner = shippingAddress.getAddressOwner().value();
            }

            OrderShippingAddressInfo shippingAddressInDb = (OrderShippingAddressInfo) session.createCriteria(OrderShippingAddressInfo.class)
                    .add(Restrictions.and(
                            Restrictions.eq(OrderShippingAddressSchema.addressOwner, addressOwner),
                            Restrictions.eq(OrderShippingAddressSchema.orderId, orderInfo.getId())
                    ))
                    .uniqueResult();

            boolean orderHasShippingAddress = shippingAddressInDb != null;

            if (shippingAddressInDb == null) {
                shippingAddressInDb = new OrderShippingAddressInfo();
            }

            if (shippingAddressInDb.getModified() != 0) {
                return;
            }

            shippingAddressInDb.setOrderId(orderInfo.getId());
            shippingAddressInDb.setName(nullToEmpty(shippingAddress.getName()));
            shippingAddressInDb.setStreet1(nullToEmpty(shippingAddress.getStreet1()));
            shippingAddressInDb.setStreet2(nullToEmpty(shippingAddress.getStreet2()));
            shippingAddressInDb.setPhone(nullToEmpty(normalizePhone(shippingAddress.getPhone())));
            shippingAddressInDb.setCityName(nullToEmpty(shippingAddress.getCityName()));
            shippingAddressInDb.setProvince(nullToEmpty(shippingAddress.getStateOrProvince()));

            if (shippingAddress.getCountry() != null) {
                shippingAddressInDb.setCountry(nullToEmpty(shippingAddress.getCountry().value()));
            } else {
                shippingAddressInDb.setCountry("");
            }

            shippingAddressInDb.setCountryName(nullToEmpty(shippingAddress.getCountryName()));
            shippingAddressInDb.setPostalCode(nullToEmpty(shippingAddress.getPostalCode()));
            shippingAddressInDb.setAddressId(nullToEmpty(shippingAddress.getAddressID()));

            if (shippingAddress.getAddressOwner() != null) {
                shippingAddressInDb.setAddressOwner(nullToEmpty(shippingAddress.getAddressOwner().value()));
            } else {
                shippingAddressInDb.setAddressOwner(nullToEmpty(""));
            }
            shippingAddressInDb.setExternalAddressId(nullToEmpty(shippingAddress.getExternalAddressID()));

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
        if (phone != null && (!phone.equals(""))) {
            phone = phone.replace(" ", "");

            if (phone.length() > 4) {
                return phone.substring(0, 4) + " " + phone.substring(4);
            }
        }

        return phone;
    }

    private String nullToEmpty(String str) {
        if (str == null) {
            str = "";
        }

        return str;
    }
}
