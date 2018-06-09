package ozosync.job.orderfeedback;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetFeedbackCall;
import com.ebay.soap.eBLBaseComponents.CommentTypeCodeType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.FeedbackDetailType;
import com.ebay.soap.eBLBaseComponents.FeedbackTypeCodeType;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.OrderFeedbackInfo;
import ozosync.domain.schema.OrderFeedbackSchema;
import ozosync.job.service.EbayApiContextService;

@Service
public class OrderFeedbackDownloader {

    private Logger log = Logger.getLogger(OrderFeedbackDownloader.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private EbayApiContextService ebayApiContextService;

    @SuppressWarnings("unchecked")
    public void download() {
        try {
            Session session = sessionFactory.getCurrentSession();

            // do something
                /*
SELECT
    o.id                  AS order_id,
    eac.app_id            AS app_id,
    eac.cert_id           AS cert_id,
    eac.dev_id            AS dev_id,
    eac.token             AS token,
    o.seller_user_id      AS seller_user_id,
    oi.id                 AS order_item_id,
    oi.order_line_item_id AS order_line_item_id
FROM erp_order o
    INNER JOIN erp_order_item oi ON oi.order_id = o.id
    INNER JOIN erp_ebay_api_config eac ON o.shop_id = eac.shop_id
    LEFT JOIN erp_order_feedback of ON of.order_item_id = oi.id
WHERE (o.status_id = 4200 OR o.status_id = 4300)
      AND o.seller_user_id <> ''
      AND oi.order_line_item_id <> ''
      AND of.id IS NULL
      AND o.shipped_time > DATE_ADD(now(), INTERVAL -60 DAY);
                 */

            StringBuilder sb = new StringBuilder();
            sb.append("SELECT\n\r");
            sb.append("    o.id                  AS order_id,\n\r"); // 0
            sb.append("    eac.app_id            AS app_id,\n\r"); // 1
            sb.append("    eac.cert_id           AS cert_id,\n\r"); //2
            sb.append("    eac.dev_id            AS dev_id,\n\r"); // 3
            sb.append("    eac.token             AS token,\n\r");  // 4
            sb.append("    o.seller_user_id      AS seller_user_id,\n\r"); // 5
            sb.append("    oi.id                 AS order_item_id,\n\r"); // 6
            sb.append("    oi.order_line_item_id AS order_line_item_id\n\r"); // 7
            sb.append("FROM erp_order o\n\r");
            sb.append("    INNER JOIN erp_order_item oi ON oi.order_id = o.id\n\r");
            sb.append("    INNER JOIN erp_ebay_api_config eac ON o.shop_id = eac.shop_id\n\r");
            sb.append("    LEFT JOIN erp_order_feedback of ON of.order_item_id = oi.id\n\r");
            sb.append("WHERE (o.status_id = 4200 OR o.status_id = 4300)\n\r");
            sb.append("      AND o.seller_user_id <> ''\n\r");
            sb.append("      AND oi.order_line_item_id <> ''\n\r");
            sb.append("      AND of.id IS NULL\n\r");
            sb.append("      AND o.shipped_time > DATE_ADD(now(), INTERVAL -60 DAY);\n\r");

            List item_list = session.createSQLQuery(sb.toString()).list();
            if (item_list != null) {
                log.error("start download order item feedback, count:" + item_list.size());

                for (Object item : item_list) {
                    Object[] itemArray = (Object[]) item;
                    Long orderId = ((java.math.BigInteger) itemArray[0]).longValue();
                    String appId = (String) itemArray[1];
                    String certId = (String) itemArray[2];
                    String devId = (String) itemArray[3];
                    String token = (String) itemArray[4];
                    String sellerUserId = (String) itemArray[5];
                    Long orderItemId = ((java.math.BigInteger) itemArray[6]).longValue();
                    String orderLineItemId = (String) itemArray[7];

                    ApiContext apiContext = ebayApiContextService.getApiContextByConfig(appId, certId, devId, token);

                    GetFeedbackCall getFeedbackCall = new GetFeedbackCall(apiContext);
                    getFeedbackCall.setUserID(sellerUserId);
                    getFeedbackCall.addDetailLevel(DetailLevelCodeType.RETURN_ALL);
                    getFeedbackCall.setFeedbackType(FeedbackTypeCodeType.FEEDBACK_RECEIVED_AS_SELLER);
                    getFeedbackCall.setOrderLineItemID(orderLineItemId);

                    FeedbackDetailType[] feedbackDetailTypes = getFeedbackCall.getFeedback();
                    log.error("downloaded order item feedback, order item id:" + orderItemId);

                    if (feedbackDetailTypes != null && feedbackDetailTypes.length != 0) {
                        FeedbackDetailType feedbackDetailType = feedbackDetailTypes[0];

                        OrderFeedbackInfo orderFeedbackInfo = (OrderFeedbackInfo) session.createCriteria(OrderFeedbackInfo.class)
                                .add(Restrictions.eq(OrderFeedbackSchema.orderId, orderId))
                                .add(Restrictions.eq(OrderFeedbackSchema.orderItemId, orderItemId))
                                .uniqueResult();

                        if (orderFeedbackInfo == null) {
                            orderFeedbackInfo = new OrderFeedbackInfo();
                        }

                        orderFeedbackInfo.setOrderId(orderId);
                        orderFeedbackInfo.setOrderItemId(orderItemId);

                        CommentTypeCodeType commentTypeCodeType = feedbackDetailType.getCommentType();

                        if (commentTypeCodeType == CommentTypeCodeType.NEGATIVE) {
                            orderFeedbackInfo.setCommentType(1);
                        } else if (commentTypeCodeType == CommentTypeCodeType.NEUTRAL) {
                            orderFeedbackInfo.setCommentType(2);
                        } else if (commentTypeCodeType == CommentTypeCodeType.POSITIVE) {
                            orderFeedbackInfo.setCommentType(3);
                        }

                        orderFeedbackInfo.setCommentTime(feedbackDetailType.getCommentTime().getTime());
                        orderFeedbackInfo.setUpdateTime(new Date());

                        session.saveOrUpdate(orderFeedbackInfo);
                        session.flush();
                    }
                }
            } else {
                log.error("no order item to download feedback");
            }
        } catch (Exception exp) {
            log.fatal(exp.getMessage(), exp);
        }
    }
}
