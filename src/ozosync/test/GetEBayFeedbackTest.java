package ozosync.test;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetOrdersCall;
import com.ebay.sdk.call.GetFeedbackCall;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.FeedbackDetailType;
import com.ebay.soap.eBLBaseComponents.FeedbackTypeCodeType;
import com.ebay.soap.eBLBaseComponents.OrderIDArrayType;
import com.ebay.soap.eBLBaseComponents.OrderStatusCodeType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import com.ebay.soap.eBLBaseComponents.TradingRoleCodeType;
import com.ebay.soap.eBLBaseComponents.TransactionArrayType;
import com.ebay.soap.eBLBaseComponents.TransactionType;
import org.junit.Test;

public class GetEBayFeedbackTest {
    @Test
    public void getFeedback() throws Exception {
        ApiContext apiContext = TestUtil.getApiContext();
        GetFeedbackCall getFeedbackCall = new GetFeedbackCall(apiContext);
        getFeedbackCall.setUserID("x.warrior");
        getFeedbackCall.addDetailLevel(DetailLevelCodeType.RETURN_ALL);
        getFeedbackCall.setFeedbackType(FeedbackTypeCodeType.FEEDBACK_RECEIVED_AS_SELLER);
        // getFeedbackCall.setTransactionID("");

        // 253223558296-2229394735015
        // 253305504431-2221863171015
        getFeedbackCall.setOrderLineItemID("253305504431-2221863171015");
        // getFeedbackCall.setTransactionID("2229394735015");

        FeedbackDetailType[] feedbackDetailTypes = getFeedbackCall.getFeedback();

        int i = 0;
    }
}
