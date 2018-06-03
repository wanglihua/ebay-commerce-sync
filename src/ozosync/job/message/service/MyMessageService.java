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


        Document document = Jsoup.parse(messageHtmlPage, "UTF-8");

        List<List<String>> selectorList = new ArrayList<>();

        {
            List<String> selectorSubList = new ArrayList<>();
            selectorSubList.add("#UserInputtedText");

            selectorList.add(selectorSubList);
        }

        {
            List<String> selectorSubList = new ArrayList<>();
            selectorSubList.add("#bodyContainer > tbody > tr > td > table > tbody > tr > td > p");

            selectorList.add(selectorSubList);
        }

        {
            List<String> selectorSubList = new ArrayList<>();
            selectorSubList.add("#SingleItemCTA > div > table > tbody > tr > td > font > div");

            selectorList.add(selectorSubList);
        }

        {
            List<String> selectorSubList = new ArrayList<>();
            selectorSubList.add("body > table.device-width:nth-child(3) > tbody > tr > td > table.header-logo > tbody > tr > td > h1");

            selectorList.add(selectorSubList);
        }

        {
            List<String> selectorSubList = new ArrayList<>();
            selectorSubList.add(".mobile-dealmaker-headline");
            // selectorSubList.add(".mobile-dealmaker-storyline");

            selectorList.add(selectorSubList);
        }

        int selectorCount = selectorList.size();

        String bodyContent = "";
        int index = 0;
        boolean found = false;
        do {
            List<String> selectors = selectorList.get(index);
            boolean foundAll = true;
            StringBuilder sb = new StringBuilder();
            for (String selector : selectors) {
                Elements messageElements = document.select(selector);
                if (messageElements.size() > 0) {
                    for (Element element : messageElements) {
                        sb.append("<p>");
                        sb.append(element.html());
                        sb.append("</p>");
                    }
                } else {
                    foundAll = false;
                }
            }

            if (foundAll) {
                found = true;
                bodyContent = sb.toString();
            }

            index++;
        } while ((!found) && index < selectorCount);

        return bodyContent;
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
