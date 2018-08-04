package ozosync.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.EBayApiConfigInfo;

import java.util.List;
import ozosync.domain.schema.EBayApiConfigSchema;

@Service
public class EBayApiConfigService {

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public List<EBayApiConfigInfo> getConfiglist() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(EBayApiConfigInfo.class).list();
    }

    public EBayApiConfigInfo getConfig(Long shopId) {
        Session session = sessionFactory.getCurrentSession();
        return (EBayApiConfigInfo) session.createCriteria(EBayApiConfigInfo.class)
                .add(Restrictions.eq(EBayApiConfigSchema.shopId, shopId))
                .setMaxResults(1)
                .uniqueResult();
    }
}
