package ozosync.job.shop.database;

import com.ebay.soap.eBLBaseComponents.AddressType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ozosync.domain.OrderInfo;
import ozosync.domain.OrderShippingAddressInfo;
import ozosync.domain.schema.OrderShippingAddressSchema;

public class OrderShippingAddressRepository {

    public void saveOrderShippingAddress(Session session, OrderType orderDownload, OrderInfo orderInfo) {

        AddressType shippingAddress = orderDownload.getShippingAddress();

        if (!shippingAddress.getAddressID().equals("")) {

            OrderShippingAddressInfo shippingAddressInDb = (OrderShippingAddressInfo) session.createCriteria(OrderShippingAddressInfo.class)
                    .add(Restrictions.and(
                            Restrictions.eq(OrderShippingAddressSchema.addressOwner, shippingAddress.getAddressOwner().value()),
                            Restrictions.eq(OrderShippingAddressSchema.orderId, orderInfo.getId())
                    ))
                    .uniqueResult();

            boolean orderHasShippingAddress = shippingAddressInDb != null;

            if (shippingAddressInDb == null) {
                shippingAddressInDb = new OrderShippingAddressInfo();
            }

            shippingAddressInDb.setOrderId(orderInfo.getId());
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
