package ozosync.domain

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import ozosync.common.VerboseName
import java.math.BigDecimal
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table


@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "erp_order_item")
@VerboseName("订单明细")
data class OrderItemInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0) {

    @ManyToOne(targetEntity = OrderInfo::class)
    @JoinColumn(name = "order_id", nullable = false)
    @Fetch(FetchMode.SELECT)
    @VerboseName("订单")
    var order: OrderInfo = OrderInfo()

    @Column(name = "order_line_item_id")
    var orderLineItemId: String = ""

    @Column(name = "quantity")
    var quantity: Int = 0

    @Column(name = "transaction_id")
    var transactionId: String = ""

    @Column(name = "transaction_price_currency")
    var transactionPriceCurrency: String = ""

    @Column(name = "transaction_price")
    var transactionPrice: BigDecimal = BigDecimal.ZERO

    @Column(name = "transaction_price_rmb")
    var transactionPriceRmb: BigDecimal = BigDecimal.ZERO

    @Column(name = "product_store_item_id")
    var productStoreItemId: Long = 0

    @Column(name = "item_product_id")
    var itemProductId: Long = 0

    @Column(name = "item_id")
    var itemId: String = ""

    @Column(name = "item_sku")
    var itemSku: String = ""

    @Column(name = "item_title")
    var itemTitle: String = ""

    @Column(name = "item_condition_id")
    var itemConditionId: String = ""

    @Column(name = "item_condition_display_name")
    var itemConditionDisplayName: String = ""

    @Column(name = "desc")
    var desc: String = ""

    @Column(name = "cost_price")
    var costPrice: BigDecimal = BigDecimal.ZERO

    @Column(name = "gross_profit")
    var grossProfit: BigDecimal = BigDecimal.ZERO

    @Column(name = "created_date")
    var createdDate: Date = Date()

    @Column(name = "create_time")
    @VerboseName("创建时间")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @VerboseName("修改时间")
    var updateTime: Date = Date()
}
