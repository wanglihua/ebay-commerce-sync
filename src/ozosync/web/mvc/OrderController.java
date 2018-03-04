package ozosync.web.mvc;

import java.util.HashMap;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Controller
@RequestMapping(value = "/order")
public class OrderController {

    private Logger log = Logger.getLogger(OrderController.class);

    public ModelAndView ship() {
        return new ModelAndView(new MappingJackson2JsonView(), new HashMap<String, Object>() {{
            put("success", true);
            put("message", "设置已发货和上传快递单号成功！");
        }});
    }
}
