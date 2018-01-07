package ozosync.web.mvc;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    private Logger log = Logger.getLogger(HomeController.class);

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() throws Exception {

        /*
        List<ProductInfo> productList = productDao.getProductList();
        log.debug("product list found");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from UserInfo");
        List<UserInfo> userList = query.list();
        */

        log.warn("Hello World haha!");

        return "home";
    }
}
