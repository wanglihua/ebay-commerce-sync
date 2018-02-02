package ozosync.test;


import java.util.Arrays;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class StringTest {

    private Logger log = Logger.getLogger(StringTest.class);

    @Test
    public void indexOf() {
        String sku = "123489AU-DG";
        sku = sku.substring(0, sku.indexOf("AU-") + 2);

        System.out.println(sku);

        Assert.assertEquals(sku, "123489AU");

        try {
            throw new Exception("abcdef");

        } catch (Exception exp) {
            log.fatal(exp);
            log.fatal(Arrays.toString(exp.getStackTrace()).replace(",", "\r\n"));
        }
    }

    @Test
    public void htmlEscape() {
        String str = StringEscapeUtils.unescapeXml("abcdef &#39;");
        System.out.println("abcdef &#39; => " + str);

        str = StringEscapeUtils.unescapeXml("你好 abc 134 &#39;");
        System.out.println("你好 abc 134 &#39; => " + str);
    }
}
