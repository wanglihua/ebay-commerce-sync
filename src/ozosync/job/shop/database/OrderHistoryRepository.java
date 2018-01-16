package ozosync.job.shop.database;

import org.hibernate.Session;
import ozosync.domain.OrderHistoryInfo;
import ozosync.domain.OrderInfo;

public class OrderHistoryRepository {

    public void insertOrderHistory(Session session, OrderInfo orderInfo, String description) {
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
}
