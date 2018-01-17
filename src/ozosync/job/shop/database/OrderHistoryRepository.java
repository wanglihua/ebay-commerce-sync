package ozosync.job.shop.database;

import org.hibernate.Session;
import ozosync.domain.OrderHistoryInfo;
import ozosync.domain.OrderInfo;
import ozosync.domain.StatusInfo;

public class OrderHistoryRepository {

    public void insertOrderHistory(Session session, OrderInfo orderInfo, String description) {
        OrderHistoryInfo orderHistoryInfo = new OrderHistoryInfo();
        orderHistoryInfo.setOrderId(orderInfo.getId());
        orderHistoryInfo.setOrderCode(orderInfo.getCode());
        orderHistoryInfo.setStatusId(orderInfo.getStatusId());

        StatusInfo statusInfo = (StatusInfo) session.get(StatusInfo.class, orderInfo.getStatusId());
        orderHistoryInfo.setStatusName(statusInfo.getName());

        orderHistoryInfo.setDescription(description);
        orderHistoryInfo.setUserId(0);
        orderHistoryInfo.setDelete(0);

        session.save(orderHistoryInfo);
        session.flush();
    }
}
