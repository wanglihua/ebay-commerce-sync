package ozosync.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.OrderHistoryInfo;
import ozosync.domain.OrderInfo;
import ozosync.domain.StatusInfo;

@Service
public class OrderHistoryService {

    @Autowired
    private SessionFactory sessionFactory;

    public void insertOrderHistory(OrderInfo orderInfo, String description) {
        Session session = sessionFactory.getCurrentSession();

        OrderHistoryInfo orderHistoryInfo = new OrderHistoryInfo();
        orderHistoryInfo.setOrderId(orderInfo.getId());
        orderHistoryInfo.setOrderCode(orderInfo.getCode());
        orderHistoryInfo.setStatusId(orderInfo.getStatusId());

        StatusInfo statusInfo = (StatusInfo) session.get(StatusInfo.class, orderInfo.getStatusId());
        if (statusInfo != null) {
            orderHistoryInfo.setStatusName(statusInfo.getName());
        } else {
            orderHistoryInfo.setStatusName("");
        }

        orderHistoryInfo.setDescription(description);
        orderHistoryInfo.setUserId(0);
        orderHistoryInfo.setDelete(0);

        session.save(orderHistoryInfo);
        session.flush();
    }
}
