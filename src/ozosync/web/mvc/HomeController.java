package ozosync.web.mvc;

import java.util.HashMap;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    private Logger log = Logger.getLogger(HomeController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public ModelAndView index() {
        return new ModelAndView("home", new HashMap<String, Object>() {{
        }});
    }
}
