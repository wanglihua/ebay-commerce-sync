package ozosync.test;

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

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {SpringConfigLocation.configLocation})
public class ProductBindTest {

    @Autowired
    private SessionFactory sessionFactory;

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
}
