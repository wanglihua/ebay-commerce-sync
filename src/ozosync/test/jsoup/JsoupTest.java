package ozosync.test.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.File;

@RunWith(BlockJUnit4ClassRunner.class)
public class JsoupTest {

    @Test
    public void testSimple() throws Exception {
        File file = new File("C:\\projects\\ozosync\\src\\ozosync\\test\\jsoup\\html\\simple.html");
        Document document = Jsoup.parse(file, "UTF-8");

        Elements elements = document.select("#container p");

        for (Element element : elements) {
            System.out.println(element.html());
        }
    }

    @Test
    public void testMyMessage1() throws Exception {
        File file = new File("C:\\projects\\ozosync\\src\\ozosync\\test\\jsoup\\html\\ebay_mymessage_test1.html");
        Document document = Jsoup.parse(file, "UTF-8");

        Elements elements = document.select("#bodyContainer > tbody > tr > td > table > tbody > tr > td > p");

        System.out.println(elements.size());

        for (Element element : elements) {
            System.out.println("afafadfafafa");
            System.out.println(element.html());
        }
    }

    @Test
    public void testMyMessage2() throws Exception {
        File file = new File("C:\\projects\\ozosync\\src\\ozosync\\test\\jsoup\\html\\ebay_mymessage_test2.html");
        Document document = Jsoup.parse(file, "UTF-8");

        Elements elements = document.select("#UserInputtedText");

        for (Element element : elements) {
            System.out.println(element.html());
        }
    }

    @Test
    public void testMyMessage3() throws Exception {
        // File file = new File("C:\\projects\\ozosync\\src\\ozosync\\test\\jsoup\\html\\ebay_mymessage_test3.html");
        File file = new File("Z:\\projects\\ozosync\\src\\ozosync\\test\\jsoup\\html\\ebay_mymessage_test3.html");
        Document document = Jsoup.parse(file, "UTF-8");

        Elements elements = document.select("#SingleItemCTA > div > table > tbody > tr > td > font > div");

        for (Element element : elements) {
            System.out.println(element.html());
        }
    }

    // #SingleItemCTA > div > table > tbody > tr > td:nth-child(1) > font > div
}
