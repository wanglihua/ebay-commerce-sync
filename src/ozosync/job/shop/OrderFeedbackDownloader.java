package ozosync.job.shop;

import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ozosync.domain.EBayApiConfigInfo;

public class OrderFeedbackDownloader {

    private Logger log = Logger.getLogger(OrderFeedbackDownloader.class);

    private SessionFactory sessionFactory = null;

    private final int NUMBER_OF_DAY = 1;

    public OrderFeedbackDownloader(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public void download() {
        try {
            Session session;
            SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
            boolean existingTransaction = sessionHolder != null;
            if (existingTransaction) {
                log.warn("Found thread-bound Session for HibernateInterceptor");
                session = sessionHolder.getSession();
            } else {
                try {
                    session = sessionFactory.openSession();
                    session.setFlushMode(FlushMode.MANUAL);
                } catch (HibernateException ex) {
                    throw new DataAccessResourceFailureException("Could not open Hibernate Session", ex);
                }
                TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
            }
            try {
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
WHERE o.seller_user_id <> ''
      AND oi.order_line_item_id <> ''
      AND of.id IS NULL
      AND o.paid_time > DATE_ADD(now(), INTERVAL -30 DAY);
                 */

                StringBuilder sb = new StringBuilder();
                sb.append("SELECT\n\r");
                sb.append("    o.id                  AS order_id,\n\r");
                sb.append("    eac.app_id            AS app_id,\n\r");
                sb.append("    eac.cert_id           AS cert_id,\n\r");
                sb.append("    eac.dev_id            AS dev_id,\n\r");
                sb.append("    eac.token             AS token,\n\r");
                sb.append("    o.seller_user_id      AS seller_user_id,\n\r");
                sb.append("    oi.id                 AS order_item_id,\n\r");
                sb.append("    oi.order_line_item_id AS order_line_item_id\n\r");
                sb.append("FROM erp_order o\n\r");
                sb.append("    INNER JOIN erp_order_item oi ON oi.order_id = o.id\n\r");
                sb.append("    INNER JOIN erp_ebay_api_config eac ON o.shop_id = eac.shop_id\n\r");
                sb.append("    LEFT JOIN erp_order_feedback of ON of.order_item_id = oi.id\n\r");
                sb.append("WHERE o.seller_user_id <> ''\n\r");
                sb.append("      AND oi.order_line_item_id <> ''\n\r");
                sb.append("      AND of.id IS NULL\n\r");
                sb.append("      AND o.paid_time > DATE_ADD(now(), INTERVAL -30 DAY);\n\r");

                String sql_get_order_item_list = sb.toString();
                List item_list = session.createSQLQuery(sb.toString()).list();
                for (Object item : item_list) {
                    Object[] itemArray = (Object[]) item;
                    Long orderId = (Long) itemArray[0];
                }

            } catch (Exception ex) {
                //错误信息
                log.fatal(ex.getMessage(), ex);
                //ex.printStackTrace();
                throw ex;
            } finally {
                if (existingTransaction) {
                    log.debug("Not closing pre-bound Hibernate Session after TransactionalQuartzTask");
                } else {
                    SessionFactoryUtils.closeSession(session);
                    TransactionSynchronizationManager.unbindResource(sessionFactory);
                }
            }


        } catch (Exception exp) {
            log.fatal(exp);
            log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
        }
    }

    private ApiContext getApiContextByConfig(EBayApiConfigInfo ebayApiConfigInfo) {
        ApiContext apiContext = new ApiContext();

        ApiCredential apiCredential = apiContext.getApiCredential();

        ApiAccount apiAccount = new ApiAccount();
        apiAccount.setApplication(ebayApiConfigInfo.getAppId());
        apiAccount.setCertificate(ebayApiConfigInfo.getCertId());
        apiAccount.setDeveloper(ebayApiConfigInfo.getDevId());

        apiCredential.setApiAccount(apiAccount);

        apiCredential.seteBayToken(ebayApiConfigInfo.getToken());
        apiContext.setApiServerUrl("https://api.ebay.com/wsapi");

        apiContext.getApiLogging().setLogSOAPMessages(false);// This will log SOAP requests and responses

        apiContext.setSite(SiteCodeType.AUSTRALIA);

        return apiContext;
    }
}
