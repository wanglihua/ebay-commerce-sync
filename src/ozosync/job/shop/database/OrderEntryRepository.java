package ozosync.job.shop.database;

import com.ebay.soap.eBLBaseComponents.OrderType;
import com.ebay.soap.eBLBaseComponents.TransactionArrayType;
import com.ebay.soap.eBLBaseComponents.TransactionType;
import org.hibernate.Session;

public class OrderEntryRepository {

    public void saveOrderEntries(Session session, OrderType orderDownload) {
        // 保存订单明细
        TransactionArrayType transactionArray = orderDownload.getTransactionArray();

        if (transactionArray != null && transactionArray.getTransactionLength() > 0) {
            int transactionLength = transactionArray.getTransactionLength();
            for (int index = 0; index < transactionLength; index++) {
                TransactionType transaction = transactionArray.getTransaction(index);
                String itemSku = transaction.getItem().getSKU();


            }
        }
    }

}

/*


for _, transaction := range order_download.TransactionArray.Transactions {
	// 获取并检查明细是否存在，根据 order_id 和 item_sku
	item_sku := transaction.Item.SKU

	// 考虑是不是绑定商品的情况，先查一下这个SKU是不是在 绑定商品列表里面的SKU,
	is_product_bind_sku := product_bind_service.Is_product_bind_sku(item_sku, db_session)
	if is_product_bind_sku == true {
		product_bind_sku_list := product_bind_service.Get_product_bind_skus(item_sku, db_session)

		//todo: 这里还要做一个根据各个商品的加权平均，计算商品利润的情况。PS，不用了，这个可以在匹配的时候做
		for _, product_bind_sku := range product_bind_sku_list {
			product_sku := product_bind_sku.Sku
			product_sku_count := product_bind_sku.Count

			var order_item ddlc_models.OrderItemInfo

			has_order_item, err := db_session.Where(fmt.Sprintf("order_id = %d and item_sku = '%s'", order_id, product_sku)).Get(&order_item)
			core.HandleError(err)

			// 设置明细字段
			order_item.OrderId = order_id
			order_item.OrderLineItemId = transaction.OrderLineItemID
			order_item.Quantity = transaction.QuantityPurchased * int(product_sku_count)
			order_item.TransactionId = transaction.TransactionID
			order_item.TransactionPriceCurrency = transaction.TransactionPrice.Currency
			order_item.TransactionPrice = transaction.TransactionPrice.Value
			if exchange_rate, found := exchange_rate_map[transaction.TransactionPrice.Currency]; found == true {
				order_item.TransactionPriceRmb = transaction.TransactionPrice.Value * exchange_rate.Rate
			}
			order_item.ItemId = transaction.Item.ItemID

			// 要根据 SKU 下载 商品吗，觉得解析出来的SKU作为商品不一定在eBay上卖的，所以就觉得没必要了的

			//order_item.ItemProductId 如果这时产品不存在会怎样，下载产品, 这时下载的是 绑定的虚拟商品，不是解析出来的sku的商品
			has_product_store_item, _, product_store_item_id := product_store_item_service.Query_product_store_item(transaction.Item.ItemID, db_session)
			if has_product_store_item == false {
				//download product
				// todo: 这里可能会出错的，应该拆分开的商品可能没在该商店销售，造成下载失败
				_, _, product_store_item_id = fetch_product_item_to_db(shop_account_config.APIConfig, db_session, transaction.Item.ItemID)
			}

			product_id := product_service.Get_product_id_by_sku(product_sku, db_session)

			order_item.ItemProductId = product_id
			order_item.ProductStoreItemId = product_store_item_id
			order_item.ItemSku = transaction.Item.SKU
			order_item.ItemTitle = transaction.Item.Title
			order_item.ItemConditionId = transaction.Item.ConditionID
			order_item.ItemConditionDisplayName = transaction.Item.ConditionDisplayName

			transaction_created_date, err := time.ParseInLocation("2006-01-02T15:04:05.000Z", transaction.CreatedDate, time.UTC)
			core.HandleError(err)
			transaction_created_date = transaction_created_date.Local()
			order_item.CreatedDate = core.LongTime(transaction_created_date)

			// 新增或更新明细
			if has_order_item == false {
				//新增
				_, err = db_session.Insert(&order_item)
				core.HandleError(err)
			} else {
				//更新
				_, err = db_session.Id(order_item.Id).Update(&order_item)
				core.HandleError(err)
			}
		}

	} else {
		var order_item ddlc_models.OrderItemInfo

		has_order_item, err := db_session.Where(fmt.Sprintf("order_id = %d and item_sku = '%s'", order_id, item_sku)).Get(&order_item)
		core.HandleError(err)

		// 设置明细字段
		order_item.OrderId = order_id
		order_item.OrderLineItemId = transaction.OrderLineItemID
		order_item.Quantity = transaction.QuantityPurchased
		order_item.TransactionId = transaction.TransactionID
		order_item.TransactionPriceCurrency = transaction.TransactionPrice.Currency
		order_item.TransactionPrice = transaction.TransactionPrice.Value
		if exchange_rate, found := exchange_rate_map[transaction.TransactionPrice.Currency]; found == true {
			order_item.TransactionPriceRmb = transaction.TransactionPrice.Value * exchange_rate.Rate
		}
		order_item.ItemId = transaction.Item.ItemID

		//order_item.ItemProductId 如果这时产品不存在会怎样，下载产品
		has_product_store_item, product_id, product_store_item_id := product_store_item_service.Query_product_store_item(transaction.Item.ItemID, db_session)
		if has_product_store_item == false {
			//download product
			product_id, _, product_store_item_id = fetch_product_item_to_db(shop_account_config.APIConfig, db_session, transaction.Item.ItemID)
		}

		order_item.ItemProductId = product_id
		order_item.ProductStoreItemId = product_store_item_id
		order_item.ItemSku = transaction.Item.SKU
		order_item.ItemTitle = transaction.Item.Title
		order_item.ItemConditionId = transaction.Item.ConditionID
		order_item.ItemConditionDisplayName = transaction.Item.ConditionDisplayName

		transaction_created_date, err := time.ParseInLocation("2006-01-02T15:04:05.000Z", transaction.CreatedDate, time.UTC)
		core.HandleError(err)
		transaction_created_date = transaction_created_date.Local()
		order_item.CreatedDate = core.LongTime(transaction_created_date)

		// 新增或更新明细
		if has_order_item == false {
			//新增
			_, err = db_session.Insert(&order_item)
			core.HandleError(err)
		} else {
			//更新
			_, err = db_session.Id(order_item.Id).Update(&order_item)
			core.HandleError(err)
		}
	}
}



 */