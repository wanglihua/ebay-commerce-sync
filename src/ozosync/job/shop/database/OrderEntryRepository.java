package ozosync.job.shop.database;

import com.ebay.soap.eBLBaseComponents.OrderType;
import com.ebay.soap.eBLBaseComponents.TransactionArrayType;
import com.ebay.soap.eBLBaseComponents.TransactionType;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ozosync.domain.OrderInfo;
import ozosync.domain.OrderItemInfo;
import ozosync.domain.ProductInfo;
import ozosync.domain.ProductStoreItemInfo;
import ozosync.domain.schema.OrderItemSchema;
import ozosync.domain.schema.ProductSchema;
import ozosync.domain.schema.ProductStoreItemSchema;

public class OrderEntryRepository {

    private ProductBindRespository productBindRespository = new ProductBindRespository();

    public void saveOrderEntries(Session session, OrderInfo orderInfo, OrderType orderDownload) throws Exception {
        // 保存订单明细
        TransactionArrayType transactionArray = orderDownload.getTransactionArray();

        if (transactionArray != null && transactionArray.getTransactionLength() > 0) {
            int transactionLength = transactionArray.getTransactionLength();
            for (int index = 0; index < transactionLength; index++) {
                TransactionType transaction = transactionArray.getTransaction(index);
                String itemSku = transaction.getItem().getSKU();
                if (itemSku.contains("AU-")) {
                    itemSku = itemSku.substring(0, itemSku.indexOf("AU-") + 2); // 123489AU-DG => 123489AU
                }

                if (productBindRespository.isProductBindSku(itemSku, session)) {

                    Map<String, Integer> productBindSkuList = productBindRespository.getProductBindSkuList(itemSku, session);

                    Integer totalQty = 0;
                    for (Map.Entry<String, Integer> productBindSku : productBindSkuList.entrySet()) {
                        Integer productQty = productBindSku.getValue();
                        totalQty = totalQty + productQty * transaction.getQuantityPurchased();
                    }

                    if (totalQty == 0) {
                        totalQty = 1; //这是为了下面的计算不出现错误，一般这种情况也不会出现
                    }

                    for (Map.Entry<String, Integer> productBindSku : productBindSkuList.entrySet()) {
                        String productSku = productBindSku.getKey();
                        Integer productQty = productBindSku.getValue();

                        OrderItemInfo orderItemInfo = (OrderItemInfo) session.createCriteria(OrderItemInfo.class)
                                .add(Restrictions.eq(OrderItemSchema.orderId, orderInfo.getId()))
                                .add(Restrictions.eq(OrderItemSchema.itemSku, productSku))
                                .setMaxResults(1)
                                .uniqueResult();

                        boolean hasOrderItem = orderItemInfo != null;

                        if (orderItemInfo == null) {
                            orderItemInfo = new OrderItemInfo();
                        }

                        orderItemInfo.setOrderId(orderInfo.getId());
                        orderItemInfo.setOrderLineItemId(transaction.getOrderLineItemID());
                        orderItemInfo.setQuantity(transaction.getQuantityPurchased() * productQty); // 数量跟不捆绑销售的时候不一样
                        orderItemInfo.setTransactionId(transaction.getTransactionID());
                        orderItemInfo.setTransactionPriceCurrency(transaction.getTransactionPrice().getCurrencyID().value());
                        orderItemInfo.setTransactionPrice(new BigDecimal(transaction.getTransactionPrice().getValue() * transaction.getQuantityPurchased() * productQty / totalQty, MathContext.DECIMAL64)); // 价格也应该跟不捆绑销售的时候不一样
                        orderItemInfo.setTransactionPriceRmb(orderItemInfo.getTransactionPrice().multiply(orderInfo.getExchangeRate()));
                        orderItemInfo.setItemId(transaction.getItem().getItemID());
                        orderItemInfo.setItemSku(productSku); // SKU跟不捆绑销售的时候不一样
                        orderItemInfo.setItemTitle(transaction.getItem().getTitle());
                        orderItemInfo.setItemConditionId(transaction.getItem().getConditionID().toString());
                        orderItemInfo.setItemConditionDisplayName(transaction.getItem().getConditionDisplayName());

                        ProductInfo productInfo = (ProductInfo) session.createCriteria(ProductInfo.class)
                                .add(Restrictions.eq(ProductSchema.sku, productSku)) // SKU跟不捆绑销售的时候不一样
                                .uniqueResult();
                        orderItemInfo.setItemProductId(productInfo.getId());

                        ProductStoreItemInfo productStoreItemInfo = (ProductStoreItemInfo) session.createCriteria(ProductStoreItemInfo.class)
                                .add(Restrictions.eq(ProductStoreItemSchema.itemId, transaction.getItem().getItemID()))
                                .setMaxResults(1)
                                .uniqueResult();
                        orderItemInfo.setProductStoreItemId(productStoreItemInfo.getId());

                        orderItemInfo.setCreatedDate(transaction.getCreatedDate().getTime());

                        if (hasOrderItem) {
                            session.update(orderItemInfo);
                        } else {
                            session.save(orderItemInfo);
                        }
                        session.flush();
                    }
                } else {
                    OrderItemInfo orderItemInfo = (OrderItemInfo) session.createCriteria(OrderItemInfo.class)
                            .add(Restrictions.eq(OrderItemSchema.orderId, orderInfo.getId()))
                            .add(Restrictions.eq(OrderItemSchema.itemSku, itemSku))
                            .setMaxResults(1)
                            .uniqueResult();

                    boolean hasOrderItem = orderItemInfo != null;

                    if (orderItemInfo == null) {
                        orderItemInfo = new OrderItemInfo();
                    }

                    orderItemInfo.setOrderId(orderInfo.getId());
                    orderItemInfo.setOrderLineItemId(transaction.getOrderLineItemID());
                    orderItemInfo.setQuantity(transaction.getQuantityPurchased());
                    orderItemInfo.setTransactionId(transaction.getTransactionID());
                    orderItemInfo.setTransactionPriceCurrency(transaction.getTransactionPrice().getCurrencyID().value());
                    orderItemInfo.setTransactionPrice(new BigDecimal(transaction.getTransactionPrice().getValue(), MathContext.DECIMAL64));
                    orderItemInfo.setTransactionPriceRmb(orderItemInfo.getTransactionPrice().multiply(orderInfo.getExchangeRate()));
                    orderItemInfo.setItemId(transaction.getItem().getItemID());
                    orderItemInfo.setItemSku(transaction.getItem().getSKU());
                    orderItemInfo.setItemTitle(transaction.getItem().getTitle());
                    orderItemInfo.setItemConditionId(transaction.getItem().getConditionID().toString());
                    orderItemInfo.setItemConditionDisplayName(transaction.getItem().getConditionDisplayName());

                    ProductInfo productInfo = (ProductInfo) session.createCriteria(ProductInfo.class)
                            .add(Restrictions.eq(ProductSchema.sku, itemSku))
                            .uniqueResult();
                    if (productInfo != null) {
                        orderItemInfo.setItemProductId(productInfo.getId());
                    } else {
                        throw new Exception(String.format("订单商品SKU数据库中不存在！ 订单： %s, sku: %s", orderInfo.getCode(), itemSku));
                    }

                    ProductStoreItemInfo productStoreItemInfo = (ProductStoreItemInfo) session.createCriteria(ProductStoreItemInfo.class)
                            .add(Restrictions.eq(ProductStoreItemSchema.itemId, transaction.getItem().getItemID()))
                            .setMaxResults(1)
                            .uniqueResult();
                    if (productStoreItemInfo != null) {
                        orderItemInfo.setProductStoreItemId(productStoreItemInfo.getId());
                    }

                    orderItemInfo.setCreatedDate(transaction.getCreatedDate().getTime());

                    if (hasOrderItem) {
                        session.update(orderItemInfo);
                    } else {
                        session.save(orderItemInfo);
                    }
                    session.flush();
                }
            }
        }
    }
}
