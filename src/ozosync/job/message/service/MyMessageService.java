package ozosync.job.message.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class MyMessageService {

    public String getMessageBody(String messageHtmlPage) {

        StringBuilder bodyBuilder = new StringBuilder();

        Document document = Jsoup.parse(messageHtmlPage);
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
}
