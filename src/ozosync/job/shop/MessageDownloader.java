package ozosync.job.shop;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

public class MessageDownloader {

    private Logger log = Logger.getLogger(MessageDownloader.class);

    private SessionFactory sessionFactory = null;

    public MessageDownloader(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public void download() {
    }
}

/*
public class AppGetMyMessage {
    private String configFile = "request_header_param.properties";
    private ApiContext apiContext;
    private boolean _isCore = true;
    private ApiCall _apiCall;

    public AppGetMyMessage() {
        RequestHeaderParam requestHeaderParam = new RequestHeaderParam();
        apiContext = requestHeaderParam.createContext(configFile);
        apiContext.setErrorLanguage("en_US");
        //Enable logging
        ApiLogging logging = new ApiLogging();
        apiContext.setApiLogging(logging);
    }

    public static void main(String[] args) {
        AppGetMyMessage agmm = new AppGetMyMessage();
        GetMyMessagesCall apiCall = new GetMyMessagesCall(agmm.apiContext);
        DetailLevelCodeType[] detailLevels = new DetailLevelCodeType[]{
                // Using DetailLevel.ReturnHeaders filter
                // to get a complete list alert and message headers with their corresponding alert ID and message ID values.
                DetailLevelCodeType.ReturnHeaders,
        };
        apiCall.setDetailLevel(detailLevels);
        try {
            apiCall.getMyMessages();

            MyMessagesMessageType[] msgs = apiCall.getReturnedMyMessages();
            // If there are Messages
            if (msgs != null) {
                int page = 1, totalPages = 0;
                //  retrieve ten messages at a time ( a page)
                if (msgs.length % 10 != 0) {
                    totalPages = msgs.length / 10 + 1;
                } else {
                    totalPages = msgs.length;
                }
                // make second GetMyMessages call
                agmm.retrieveMessages(apiCall, page, totalPages, msgs);
            }

        } catch (Exception e) {
            if (e instanceof SdkSoapException) {
                SdkSoapException sdkSoapExe = (SdkSoapException) e;
                ErrorType error = sdkSoapExe.getErrorType();
                System.out.println("error code: " + error.getErrorCode() + ", error shot message :"
                        + error.getShortMessage());
            }
            if (e instanceof ApiException) {
                ApiException apiExe = (ApiException) e;
                ErrorType[] errs = apiExe.getErrors();
                for (int i = 0; i < errs.length; i++) {
                    ErrorType error = errs[i];
                    System.out.println("error code " + error.getErrorCode() + "error shot message"
                            + error.getShortMessage());
                }
            }
        }

    }


    public void retrieveMessages(GetMyMessagesCall api, int page, int totalPages, MyMessagesMessageType[] msgs) {
        MyMessagesMessageIDType[] msgIds = null;
        int start = 0, end = 0;

        start = (page - 1) * 10;
        if (totalPages == 1 || page == totalPages) {
            end = msgs.length;
        } else end = (page * 10);
        msgIds = new MyMessagesMessageIDType[end - start];

        for (int i = start; i < end; i++) {
            if (msgs[i] != null) {
                for (int j = 0; j < (end - start); j++) {
                    msgIds[j] = msgs[i].getMessageID();
                }
            }
        }
        DetailLevelCodeType[] detail = new DetailLevelCodeType[]{
                // retrieve specific ten alerts and ten specific messages at a time
                //with DetailLevel.ReturnMessages
                DetailLevelCodeType.ReturnMessages,
        };
        api.setDetailLevel(detail);
        api.setMessageIDs(msgIds);
        try {
            api.getMyMessages();
            // make another GetMyMessage call when there are more than ten messages
            if (page < totalPages) {
                retrieveMessages(api, ++page, totalPages, msgs);
            }
        } catch (Exception e) {
        }
    }

}
*/
