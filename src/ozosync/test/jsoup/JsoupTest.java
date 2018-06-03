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

    @Test
    public void testMyMessage4() throws Exception {
        // File file = new File("C:\\projects\\ozosync\\src\\ozosync\\test\\jsoup\\html\\ebay_mymessage_test4.html");
        File file = new File("Z:\\projects\\ozosync\\src\\ozosync\\test\\jsoup\\html\\ebay_mymessage_test4.html");
        Document document = Jsoup.parse(file, "UTF-8");

        Elements elements = document.select("body > table.device-width:nth-child(3) > tbody > tr > td > table.header-logo > tbody > tr > td > h1");

        for (Element element : elements) {
            System.out.println(element.html());
        }
    }

    @Test
    public void testMyMessage5() throws Exception {
        File file = new File("C:\\projects\\ozosync\\src\\ozosync\\test\\jsoup\\html\\ebay_mymessage_test6.html");
        // File file = new File("Z:\\projects\\ozosync\\src\\ozosync\\test\\jsoup\\html\\ebay_mymessage_test5.html");
        Document document = Jsoup.parse(file, "UTF-8");

        Elements elements = document.select(".mobile-dealmaker-headline");

        for (Element element : elements) {
            System.out.println(element.html());
        }

        elements = document.select(".mobile-dealmaker-storyline");

        for (Element element : elements) {
            System.out.println(element.html());
        }
    }
}
