package ozosync.job.message.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MyMessageService {

    public String getMessageBody(String messageHtmlPage) {

        messageHtmlPage = messageHtmlPage.replace("<![CDATA[", "");
        messageHtmlPage = messageHtmlPage.replace("]]>", "");

        StringBuilder bodyBuilder = new StringBuilder();

        Document document = Jsoup.parse(messageHtmlPage, "UTF-8");

        List<String> selectorList = new ArrayList<String>();
        selectorList.add("#UserInputtedText");
        selectorList.add("#bodyContainer > tbody > tr > td > table > tbody > tr > td > p");
        selectorList.add("#SingleItemCTA > div > table > tbody > tr > td > font > div");
        selectorList.add("body > table.device-width:nth-child(3) > tbody > tr > td > table.header-logo > tbody > tr > td > h1");

        int selectorCount = selectorList.size();

        int index = 0;
        boolean found = false;
        do {
            String selector = selectorList.get(index);
            Elements messageElements = document.select(selector);
            if (messageElements.size() > 0) {
                found = true;
                for (Element element : messageElements) {
                    bodyBuilder.append("<p>");
                    bodyBuilder.append(element.html());
                    bodyBuilder.append("</p>");
                }
            }

            index++;
        } while ((!found) && index < selectorCount);

        return bodyBuilder.toString();
    }

    public String getCData1(String cdataStr) {
        Pattern pattern = Pattern.compile("\\<\\!\\[CDATA\\[(?<text>[^\\]]*)\\]\\]\\>");
        Matcher matcher = pattern.matcher(cdataStr);

        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return cdataStr;
        }
    }

    public String getCData2(String cdataStr) {
        cdataStr = cdataStr.replace("<![CDATA[", "");
        cdataStr = cdataStr.replace("]]>", "");

        return cdataStr;
    }

}
