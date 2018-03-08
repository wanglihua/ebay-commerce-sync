package ozosync.web.mvc;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.CompleteSaleCall;
import com.ebay.soap.eBLBaseComponents.ShipmentTrackingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShipmentType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import ozosync.domain.LogisticsCompanyInfo;
import ozosync.domain.OrderInfo;
import ozosync.domain.schema.OrderSchema;
import ozosync.service.ApiContextService;

@Controller
@RequestMapping(value = "/order")
public class OrderController {

    private Logger log = Logger.getLogger(OrderController.class);

    @Autowired
    private ApiContextService apiContextService;

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/ship", method = RequestMethod.POST)
    public ModelAndView ship(@RequestParam(value = "orderIdList[]") Long[] orderIdList) {

        Session session = sessionFactory.getCurrentSession();

        if (orderIdList == null || orderIdList.length == 0) {
            return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
                put("success", false);
                put("message", "订单列表不能为空！");
            }});
        }

        List<OrderInfo> orderInfoList = (List<OrderInfo>) session.createCriteria(OrderInfo.class)
                .add(Restrictions.in(OrderSchema.id, orderIdList))
                .list();

        for (OrderInfo orderInfo : orderInfoList) {
            if (orderInfo.getCompanyId() == 0 || orderInfo.getWaybillNumber() == null || orderInfo.getWaybillNumber().equals("")) {
                return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "订单货运公司或货运单号未设置！");
                }});
            } else {
                LogisticsCompanyInfo logisticsCompanyInfo = (LogisticsCompanyInfo) session.get(LogisticsCompanyInfo.class, orderInfo.getCompanyId());
                if (logisticsCompanyInfo == null) {
                    return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
                        put("success", false);
                        put("message", "订单指定的货运公司不存在！");
                    }});
                }
            }
        }

        for (OrderInfo orderInfo : orderInfoList) {
            // 上传时进行捕获异常，如果一个失败了，可以进行下一个，设置订单的已上传标记
            try {

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

                orderInfo.setEbayShip(1);
                session.update(orderInfo);
                session.flush();

            } catch (Exception exp) {
                log.fatal(exp);
                log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
            }
        }

        session.flush();

        return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
            put("success", true);
            put("message", "设置已发货和上传快递单号成功！");
        }});
    }
}
