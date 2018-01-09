package ozosync.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.ApiException;
import com.ebay.sdk.SdkException;
import com.ebay.sdk.TimeFilter;
import com.ebay.sdk.call.GetOrdersCall;
import com.ebay.sdk.call.GetSellerTransactionsCall;
import com.ebay.soap.eBLBaseComponents.AddressType;
import com.ebay.soap.eBLBaseComponents.AmountType;
import com.ebay.soap.eBLBaseComponents.CheckoutStatusCodeType;
import com.ebay.soap.eBLBaseComponents.CompleteStatusCodeType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.OrderIDArrayType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import com.ebay.soap.eBLBaseComponents.PaginationType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceOptionsType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;
import com.ebay.soap.eBLBaseComponents.TransactionType;
import java.util.TimeZone;
import org.junit.Test;

/**
 * Sample code to relist an item (with minor modifications) using the RelistFixedPriceItem call
 * <p>
 * The following steps outline what needs to be done for a successfull GetTransactions and GetOrders call
 * <p>
 * 1. Create an ApiContext Object
 * 2. Set the auth token and target api url (Webservice endpoint)
 * 3. Create a GetTransactionsCall object.
 * 4. Set the ModifiedFrom and ModifiedTo filter (just an example)
 * 5. Set Pagination Parameters
 * 6. Do an iteration till all transactions for the specified filter is retrieved (in this example we'll be using do..while )
 * 7. Check if the transaction is part of an order.
 * 8. If yes, add it to a list.
 * 9. Once all transactions are retrieved, check if the transaction list contains any entry
 * 10. If Yes, execute the GetOrders call with all the orders.
 * 11. Replace the transaction level information with the information from the orders.
 */

public class GetEBayOrdersTest {

    private void getSalesTransactions() throws ApiException, SdkException, Exception {
        ApiContext apiContext = TestUtil.getApiContext();

        apiContext.getApiLogging().setLogSOAPMessages(true);// This will log SOAP requests and responses

        apiContext.setSite(SiteCodeType.UK); // Set site to UK

        GetSellerTransactionsCall getTrans = new GetSellerTransactionsCall(
                apiContext);

        getTrans.addDetailLevel(DetailLevelCodeType.RETURN_ALL);
        getTrans.setIncludeFinalValueFee(true);

// Create a filter, in this example we are using from and to filter, to get all transactions in the last 15 minutes.
        Calendar to = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        Calendar from = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        from.roll(Calendar.MINUTE, -15);

        TimeFilter filter = new TimeFilter(from, to);
        getTrans.setModifiedTimeFilter(filter);
        getTrans.setIncludeContainingOrder(true);// We need to get the containing order.
        getTrans.setIncludeFinalValueFee(true);

        PaginationType pagination = new PaginationType();
        pagination.setEntriesPerPage(50);

        getTrans.setPagination(pagination);

        int pageNumber = 0;
        int tranCounter = 0;

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");

        List<String> orderIdList = new ArrayList<String>();

        do {
            System.out
                    .println("-------------------------------------------------");
            System.out.println("Calling getseller transactions");
            System.out.println("Requesting Page : " + ++pageNumber + " .....");
            getTrans.getPagination().setPageNumber(pageNumber);
            TransactionType[] transactions = getTrans.getSellerTransactions();

            System.out.print(" *********************************");
            System.out.print("Page " + pageNumber + " of "
                    + getTrans.getPaginationResult().getTotalNumberOfPages());
            System.out.println("*********************************");
            if (getTrans.getReturnedTransactionCountActual() > 0) {
                for (TransactionType transaction : transactions) {

                    System.out.println(" Transaction "
                            + ++tranCounter
                            + " of "
                            + getTrans.getPaginationResult()
                            .getTotalNumberOfEntries());

                    System.out.println(" BuyerId: "
                            + transaction.getBuyer().getUserID());

                    System.out.println("Transaction ID "
                            + transaction.getTransactionID() + " ItemID: "
                            + transaction.getItem().getItemID()
                            + " Item Title : "
                            + transaction.getItem().getTitle());

                    System.out.println("Quantity: "
                            + transaction.getQuantityPurchased());

                    System.out.println("Transaction Price : "
                            + transaction.getTransactionPrice().getValue()
                            + " CheckOut Status : "
                            + transaction.getStatus().getCheckoutStatus()
                            .value()
                            + " PaymentStatus : "
                            + transaction.getStatus().getEBayPaymentStatus()
                            .value());

                    System.out.println("CreatedDate : "
                            + dateFormatter.format(transaction.getCreatedDate()
                            .getTime())
                            + " Last Time Modified : "
                            + dateFormatter.format(transaction.getStatus()
                            .getLastTimeModified().getTime()));
                    if (transaction.getShippingDetails().getSalesTax()
                            .getSalesTaxAmount() != null) {
                        AmountType tax = transaction.getShippingDetails()
                                .getSalesTax().getSalesTaxAmount();
                        System.out.println("Sales Tax : " + tax.getValue());
                    } else {
                        System.out.println("Sales Tax : 0.0");
                    }

                    if (transaction.getFinalValueFee() != null) {
                        AmountType finalValueFee = transaction
                                .getFinalValueFee();
                        System.out.println("Final Value Fee : "
                                + finalValueFee.getValue());
                    }

                    if (transaction.getStatus().getCheckoutStatus()
                            .equals(CheckoutStatusCodeType.CHECKOUT_COMPLETE)) {// To check if the item is ready for shipping
                        System.out
                                .format("Payment Method: %s Total: %s Adjustment Amount: %s ",
                                        transaction.getStatus()
                                                .getPaymentMethodUsed().value(),
                                        transaction.getAmountPaid().getValue(),
                                        transaction.getAdjustmentAmount()
                                                .getValue());
                        if (transaction.getShippingServiceSelected() != null) {
                            System.out.println("Shipping Service: "
                                    + transaction.getShippingServiceSelected()
                                    .getShippingService());
                            if (transaction.getShippingServiceSelected()
                                    .getShippingServiceCost() != null) {
                                System.out.println("Shipping Cost: "
                                        + transaction
                                        .getShippingServiceSelected()
                                        .getShippingServiceCost()
                                        .getValue());
                            }
                        }

                        AddressType shippingAddress = transaction.getBuyer()
                                .getBuyerInfo().getShippingAddress();

                        StringBuffer address = new StringBuffer();
                        address.append(shippingAddress.getName());
                        if (shippingAddress.getStreet() != null) {
                            address.append(",").append(
                                    shippingAddress.getStreet());
                        }
                        if (shippingAddress.getStreet1() != null) {
                            address.append(",").append(
                                    shippingAddress.getStreet1());
                        }
                        if (shippingAddress.getStreet2() != null) {
                            address.append(",").append(
                                    shippingAddress.getStreet2());
                        }
                        if (shippingAddress.getCityName() != null) {
                            address.append(",").append(
                                    shippingAddress.getCityName());
                        }
                        if (shippingAddress.getStateOrProvince() != null) {
                            address.append(",").append(
                                    shippingAddress.getStateOrProvince());
                        }
                        if (shippingAddress.getPostalCode() != null) {
                            address.append(",").append(
                                    shippingAddress.getPostalCode());
                        }
                        if (shippingAddress.getCountryName() != null) {
                            address.append(",").append(
                                    shippingAddress.getCountryName());
                        }
                        if (shippingAddress.getPhone() != null) {
                            address.append(",").append(
                                    shippingAddress.getPhone());
                        }

                        System.out.println("Shipping Address: " + address);

                    }

                    if (transaction.getPaidTime() != null) {
                        System.out.println("Paid Time:"
                                + dateFormatter.format(transaction
                                .getPaidTime().getTime()));
                    }

                    if (transaction.getShippedTime() != null) {
                        System.out.println("Shipped Time: "
                                + dateFormatter.format(transaction
                                .getShippedTime().getTime()));
                    }

                    // Check if the transaction is part of an order. If it is we
                    // need to get the latest information from the order.
                    if (transaction.getContainingOrder() != null) {
                        if (!orderIdList.contains(transaction
                                .getContainingOrder().getOrderID())) {
                            String orderID = transaction.getContainingOrder()
                                    .getOrderID();
                            orderIdList.add(orderID); // Collect all the orders
                            // so that we need only
                            // one call with
                            // GetOrders.
                            System.out
                                    .println("This transaction is part of the order : "
                                            + orderID);
                        }
                    }
                    System.out.println();
                }
            }
        } while (getTrans.getHasMoreTransactions());

        // Get all order information for related orders
        if (!orderIdList.isEmpty()) {
            System.out
                    .println("*********************************************************************");

            System.out.println("Calling getOrders");
            System.out.println("Order List : " + orderIdList);

            System.out.println("*********************************************************************");

            GetOrdersCall getOrders = new GetOrdersCall(apiContext);

            OrderIDArrayType orderIdArray = new OrderIDArrayType();
            // Convert ArrayList to String Array
            String[] orderId = orderIdList.toArray(new String[orderIdList
                    .size()]);
            // Set it to the orderIdArrayType
            orderIdArray.setOrderID(orderId);

            getOrders.setOrderIDArray(orderIdArray);

            OrderType[] orders = getOrders.getOrders();

            for (OrderType order : orders) {
                ShippingServiceOptionsType shipping = order
                        .getShippingServiceSelected();
                if (shipping == null) {
                    if (order.getCheckoutStatus().getStatus() == CompleteStatusCodeType.COMPLETE) {
                        shipping = order.getShippingDetails()
                                .getShippingServiceOptions(0);
                    } else {
                        shipping = new ShippingServiceOptionsType();
                    }
                }

                double salesTax = order.getShippingDetails().getSalesTax()
                        .getSalesTaxAmount() == null ? 0.0 : order
                        .getShippingDetails().getSalesTax()
                        .getSalesTaxAmount().getValue();

                System.out.println("Information for order : " + order.getOrderID());

                System.out.format("Sales Tax %f ", salesTax);

                AddressType shippingAddress = order.getShippingAddress();

                StringBuffer address = new StringBuffer();
                address.append(shippingAddress.getName());
                if (shippingAddress.getStreet() != null) {
                    address.append(",").append(shippingAddress.getStreet());
                }
                if (shippingAddress.getStreet1() != null) {
                    address.append(",")
                            .append(shippingAddress.getStreet1());
                }
                if (shippingAddress.getStreet2() != null) {
                    address.append(",")
                            .append(shippingAddress.getStreet2());
                }
                if (shippingAddress.getCityName() != null) {
                    address.append(",").append(
                            shippingAddress.getCityName());
                }
                if (shippingAddress.getStateOrProvince() != null) {
                    address.append(",").append(
                            shippingAddress.getStateOrProvince());
                }
                if (shippingAddress.getPostalCode() != null) {
                    address.append(",").append(
                            shippingAddress.getPostalCode());
                }
                if (shippingAddress.getCountryName() != null) {
                    address.append(",").append(
                            shippingAddress.getCountryName());
                }
                if (shippingAddress.getPhone() != null) {
                    address.append(",").append(shippingAddress.getPhone());
                }

                System.out.println("Shipping Address: " + address);

                //Update your system with the order information. For all transactions that match this OrderID -
                //Replace the following Transaction information with information from the Order
                System.out
                        .println("Update Shipping and Payment information associated with transactions that belong to OrderID: "
                                + order.getOrderID());

                //1. shipping.ShippingService;
                //2. shipping.ShippingServiceCost.Value;
                //3. salesTax;
                //4. order.AdjustmentAmount.Value;
                //5. order.Total.Value;
                //6. order.CheckoutStatus.Status;
                //7. order.CheckoutStatus.eBayPaymentStatus;
                //8. order.CheckoutStatus.PaymentMethod;
                //9. shipAddress;
            }
        }

    }

    @Test
    public void getEBayOrders() {
        try {
            getSalesTransactions();
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SdkException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}