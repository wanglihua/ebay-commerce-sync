package ozosync.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ozosync.common.SpringConfigLocation;
import ozosync.domain.ProductInfo;
import ozosync.domain.schema.ProductSchema;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {SpringConfigLocation.configLocation})
public class ProductTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    @Transactional
    public void getProduct() {
        Session session = sessionFactory.getCurrentSession();
        ProductInfo productInfo = (ProductInfo) session.createCriteria(ProductInfo.class)
                .add(Restrictions.eq(ProductSchema.id, 17579L))
                .uniqueResult();

        System.out.println(productInfo.getName());
    }
}
