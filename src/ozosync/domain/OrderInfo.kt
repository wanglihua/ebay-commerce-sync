package ozosync.domain

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Type
import ozosync.common.VerboseName
import java.math.BigDecimal
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "erp_order")
@VerboseName("订单")
data class OrderInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0) {

    @Column(name = "shop_id")
    var shopId: Long = 0

    @Column(name = "status_id")
    var statusId: Long = 0


    @Column(name = "address_id")
    @VerboseName("送货地址id")
    var addressId: Long = 0

    @Column(name = "code")
    @VerboseName("订单编号")
    var code: String = ""

    @Column(name = "desc")
    @VerboseName("描述")
    var desc: String = ""

    @Column(name = "type_id")
    @VerboseName("订单类型编号")
    var typeId: Long = 0

    @Column(name = "ebay_status")
    @VerboseName("eBay状态")
    var eBayStatus: String = ""

    @Column(name = "adjustment_amount_currency")
    @VerboseName("调整金额币种")
    var adjustmentAmountCurrency: String = ""

    @Column(name = "adjustment_amount")
    @VerboseName("调整金额澳币")
    var adjustmentAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "adjustment_amount_rmb")
    @VerboseName("调整金额RMB")
    var adjustmentAmountRmb: BigDecimal = BigDecimal.ZERO

    @Column(name = "amount_paid_currency")
    @VerboseName("支付金额币种")
    var amountPaidCurrency: String = ""

    @Column(name = "amount_paid")
    @VerboseName("支付金额澳币")
    var amountPaid: BigDecimal = BigDecimal.ZERO

    @Column(name = "amount_paid_rmb")
    @VerboseName("支付金额RMB")
    var amountPaidRmb: BigDecimal = BigDecimal.ZERO

    @Column(name = "amount_saved_currency")
    @VerboseName("节省金额币种")
    var amountSavedCurrency: String = ""

    @Column(name = "amount_saved")
    @VerboseName("节省金额澳币")
    var amountSaved: BigDecimal = BigDecimal.ZERO

    @Column(name = "amount_saved_rmb")
    @VerboseName("节省金额RMB")
    var amountSavedRmb: BigDecimal = BigDecimal.ZERO

    @Column(name = "total_amount_currency")
    @VerboseName("总金额币种")
    var totalAmountCurrency: String = ""

    @Column(name = "total_amount")
    @VerboseName("总金额澳币")
    var totalAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "total_amount_rmb")
    @VerboseName("总金额RMB")
    var totalAmountRmb: BigDecimal = BigDecimal.ZERO

    @Column(name = "subtotal_amount_currency")
    @VerboseName("小计金额币种")
    var subtotalAmountCurrency: String = ""

    @Column(name = "subtotal_amount")
    @VerboseName("小计金额澳币")
    var subtotalAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "subtotal_amount_rmb")
    @VerboseName("小计金额RMB")
    var subtotalAmountRmb: BigDecimal = BigDecimal.ZERO

    @Column(name = "payment_methods")
    @VerboseName("支付方式")
    var paymentMethods: String = ""

    @Column(name = "seller_email")
    @VerboseName("卖家邮件")
    var sellerEmail: String = ""

    @Column(name = "seller_user_id")
    @VerboseName("卖家账号")
    var sellerUserId: String = ""

    @Column(name = "buyer_email")
    @VerboseName("买家邮件")
    var buyerEmail: String = ""

    @Column(name = "buyer_user_id")
    @VerboseName("买家账号")
    var buyerUserId: String = ""

    @Column(name = "buyer_checkout_message")
    @VerboseName("买家留言")
    var buyerCheckoutMessage: String? = null

    @Column(name = "created_time")
    @Type(type = "timestamp")
    @VerboseName("订单创建时间")
    var createdTime: Date? = null

    @Column(name = "modified_time")
    @Type(type = "timestamp")
    @VerboseName("订单修改时间")
    var modifiedTime: Date? = null

    @Column(name = "paid_time")
    @Type(type = "timestamp")
    @VerboseName("订单修改时间")
    var paidTime: Date? = null

    @Column(name = "shipped_time")
    @Type(type = "timestamp")
    @VerboseName("订单修改时间")
    var shippedTime: Date? = null

    @Column(name = "hold_type_id")
    @VerboseName("hold类型id")
    var holdTypeId: Long? = null

    @Column(name = "exchange_rate")
    @VerboseName("汇率")
    var exchangeRate: BigDecimal = BigDecimal.ZERO

    @Column(name = "express_fee")
    @VerboseName("快递费")
    var expressFee: BigDecimal = BigDecimal.ZERO

    @Column(name = "tran_id")
    @VerboseName("订单代码")
    var tranId: String? = null

    @Column(name = "full_tran_id")
    @VerboseName("完整订单代码")
    var fullTranId: String = ""

    @Column(name = "batch_code")
    @VerboseName("批次号")
    var batchCode: Long = 0

    @Column(name = "stock_id")
    @VerboseName("仓库id")
    var stockId: Long = 0

    @Column(name = "company_id")
    @VerboseName("公司id")
    var companyId: Long = 0

    @Column(name = "mode_id")
    @VerboseName("物流id")
    var modeId: Long = 0

    @Column(name = "delivery_person")
    @VerboseName("发货员")
    var deliveryPerson: String? = ""

    @Column(name = "waybill_number")
    @VerboseName("快递单号")
    var waybillNumber: String? = ""

    @Column(name = "tag_id")
    @VerboseName("标签")
    var tagId: Long = 0

    @Column(name = "pre_hold_type_id")
    @VerboseName("之前hold类型id")
    var preHoldTypeId: Long = 0

    @Column(name = "profit_au")
    @VerboseName("利润澳币")
    var profitAu: BigDecimal = BigDecimal.ZERO

    @Column(name = "profit_rmb")
    @VerboseName("利润RMB")
    var profitRmb: BigDecimal = BigDecimal.ZERO

    @Column(name = "cancel_status_id")
    @VerboseName("利润RMB")
    var cancelStatusId: Long = 0

    @Column(name = "create_time")
    @Type(type = "timestamp")
    @VerboseName("创建时间")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @Type(type = "timestamp")
    @VerboseName("修改时间")
    var updateTime: Date = Date()
}

/*

create table erp_order
(
	id bigint auto_increment primary key,
	shop_id bigint not null,
	code varchar(200) not null,
	`desc` varchar(500) null,
	type_id bigint not null,
	ebay_status varchar(200) not null,
	status_id bigint not null,
	adjustment_amount_currency varchar(10) not null,
	adjustment_amount decimal(18,6) not null,
	adjustment_amount_rmb decimal(18,6) not null,
	amount_paid_currency varchar(10) not null,
	amount_paid decimal(18,6) not null,
	amount_paid_rmb decimal(18,6) not null,
	amount_saved_currency varchar(10) not null,
	amount_saved decimal(18,6) not null,
	amount_saved_rmb decimal(18,6) not null,
	total_amount_currency varchar(10) not null,
	total_amount decimal(18,6) not null,
	total_amount_rmb decimal(18,6) not null,
	subtotal_amount_currency varchar(10) not null,
	subtotal_amount decimal(18,6) not null,
	subtotal_amount_rmb decimal(18,6) not null,
	payment_methods varchar(50) not null,
	seller_email varchar(200) not null,
	seller_user_id varchar(50) not null,
	buyer_user_id varchar(50) not null,
	buyer_checkout_message varchar(5000) null,
	created_time datetime null,
	modified_time datetime null,
	paid_time datetime null,
	shipped_time datetime null,
	hold_type_id bigint null,
	exchange_rate decimal(18,6) not null,
	create_time datetime not null,
	update_time datetime not null,
	express_fee decimal(18,6) not null,
	tran_id varchar(200) null,
	buyer_email varchar(200) not null,
	batch_code bigint null,
	stock_id bigint not null,
	address_id bigint not null,
	company_id bigint not null,
	mode_id bigint not null,
	delivery_person varchar(20) null,
	waybill_number varchar(50) null,
	full_tran_id varchar(200) not null,
	tag_id bigint not null,
	pre_hold_type_id bigint not null,
	profit_au decimal(18,6) not null,
	profit_rmb decimal(18,6) not null,
	cancel_status_id bigint default '0' not null,
	constraint UQE_erp_order_code
		unique (code)
)
engine=InnoDB
;

create index IDX_erp_order_shop_id
	on erp_order (shop_id)
;

create index IDX_erp_order_type_id
	on erp_order (type_id)
;

create index IDX_erp_order_status_id
	on erp_order (status_id)
;

create index IDX_erp_order_hold_type_id
	on erp_order (hold_type_id)
;

create index ix_order_batch_code
	on erp_order (batch_code)
;

create index ix_order_stock_id
	on erp_order (stock_id)
;

create index ix_order_address_id
	on erp_order (address_id)
;

create index ix_order_company_id
	on erp_order (company_id)
;

create index ix_order_mode_id
	on erp_order (mode_id)
;

create index ix_order_tag_id
	on erp_order (tag_id)
;

create index IDX_erp_order_pre_hold_type_id
	on erp_order (pre_hold_type_id)
;


 */