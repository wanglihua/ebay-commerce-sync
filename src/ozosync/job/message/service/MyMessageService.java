package ozosync.job.message.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class MyMessageService {

    public String getMessageBody(String messageHtmlPage) {

        messageHtmlPage = messageHtmlPage.replace("<![CDATA[", "");
        messageHtmlPage = messageHtmlPage.replace("]]>", "");

        StringBuilder bodyBuilder = new StringBuilder();

        Document document = Jsoup.parse(messageHtmlPage, "UTF-8");
        Elements userInputtedTextElements = document.select("#UserInputtedText");

        if (userInputtedTextElements.size() != 0) {
            bodyBuilder.append("<p>");
            for (Element element : userInputtedTextElements) {
                bodyBuilder.append(element.html());
            }
            bodyBuilder.append("</p>");
        } else {
            String selector = "#bodyContainer > tbody > tr > td > table > tbody > tr > td > p";
            Elements messageElements = document.select(selector);
            for (Element element : messageElements) {
                bodyBuilder.append("<p>");
                bodyBuilder.append(element.html());
                bodyBuilder.append("</p>");
            }
        }

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
