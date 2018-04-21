package ozosync.service;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ozosync.domain.EBayApiConfigInfo;

@Service
public class EBayApiConfigService {

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public List<EBayApiConfigInfo> getConifglist() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(EBayApiConfigInfo.class).list();
    }
}
