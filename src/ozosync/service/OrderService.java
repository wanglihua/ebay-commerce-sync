package ozosync.service;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.CompleteSaleCall;
import com.ebay.soap.eBLBaseComponents.ShipmentTrackingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShipmentType;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.LogisticsCompanyInfo;
import ozosync.domain.OrderInfo;

@Service
public class OrderService {

    private Logger log = Logger.getLogger(OrderService.class);

    @Autowired
    private ApiContextService apiContextService;

    @Autowired
    private SessionFactory sessionFactory;

    public void ship(OrderInfo orderInfo) {

        Session session = sessionFactory.getCurrentSession();
        
        LogisticsCompanyInfo logisticsCompanyInfo = (LogisticsCompanyInfo) session.get(LogisticsCompanyInfo.class, orderInfo.getCompanyId());

        ApiContext apiContext = apiContextService.getApiContextByShopId(orderInfo.getShopId());

        CompleteSaleCall completeSaleCall = new CompleteSaleCall(apiContext);

        completeSaleCall.setOrderID(orderInfo.getEbayOrderId());
        completeSaleCall.setShipped(true);

        ShipmentType shipmentType = new ShipmentType();

        ShipmentTrackingDetailsType shipmentTrackingDetailsType = new ShipmentTrackingDetailsType();
        shipmentTrackingDetailsType.setShipmentTrackingNumber(orderInfo.getWaybillNumber());
        shipmentTrackingDetailsType.setShippingCarrierUsed(logisticsCompanyInfo.getEbayName());

        shipmentType.setShipmentTrackingDetails(new ShipmentTrackingDetailsType[]{shipmentTrackingDetailsType});

        completeSaleCall.setShipment(shipmentType);

        orderInfo.setEbayShip(1L);
        session.update(orderInfo);
        session.flush();
    }
}
