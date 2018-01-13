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
    var updaetTime: Date = Date()
}

/*

create table erp_order_item
(
	id bigint auto_increment primary key,
	order_id bigint not null,

	order_line_item_id varchar(200) not null,
	quantity int not null,
	transaction_id varchar(200) not null,
	transaction_price_currency varchar(50) not null,
	transaction_price decimal(18,6) not null,
	transaction_price_rmb decimal(18,6) not null,
	product_store_item_id bigint not null,
	item_id varchar(50) not null,
	item_product_id bigint not null,
	item_sku varchar(100) not null,
	item_title varchar(200) not null,
	item_condition_id varchar(50) not null,
	item_condition_display_name varchar(100) not null,
	created_date datetime not null,
	`desc` varchar(500) null,
	cost_price decimal(18,6) default '0.000000' not null,
	gross_profit decimal(18,6) default '0.000000' not null,

	create_time datetime not null,
	update_time datetime not null
)
engine=InnoDB charset=utf8
;

create index IDX_erp_order_item_order_id
	on erp_order_item (order_id)
;

create index IDX_erp_order_item_product_store_item_id
	on erp_order_item (product_store_item_id)
;

create index IDX_erp_order_item_item_product_id
	on erp_order_item (item_product_id)
;

create index IDX_erp_order_item_item_sku
	on erp_order_item (item_sku)
;

 */