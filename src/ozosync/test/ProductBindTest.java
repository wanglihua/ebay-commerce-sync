package ozosync.test;

import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ozosync.common.SpringConfigLocation;
import ozosync.domain.ProductBindInfo;
import ozosync.domain.ProductBindStatus;
import ozosync.domain.schema.ProductBindSchema;
import ozosync.service.ProductBindService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {SpringConfigLocation.configLocation})
public class ProductBindTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ProductBindService productBindService;

    @Test
    @Transactional
    public void getProductBindCount() {
        Session session = sessionFactory.getCurrentSession();
        Object count = session.createCriteria(ProductBindInfo.class)
                .add(Restrictions.eq(ProductBindSchema.code, "124"))
                .add(Restrictions.eq(ProductBindSchema.statusId, ProductBindStatus.Normal))
                .setProjection(Projections.rowCount())
                .uniqueResult();

        System.out.println("count: " + count);
    }

    @Test
    @Transactional
    public void resolveBindSku() {
        Session session = sessionFactory.getCurrentSession();

        String code = "HBZ1001BR8AUx1+HBZ0651008AUx1";
        boolean isBindSku = productBindService.isProductBindSku(code);
        System.out.println("isBindSku: " + isBindSku);

        Map<String, Integer> skuNumList = productBindService.getProductBindSkuList(code);
        for (Map.Entry entry : skuNumList.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
            System.out.println();
        }
    }
}
