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
@Table(name = "erp_order_refund")
@VerboseName("订单退款信息")
data class OrderRefundInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0
) {
    @Column(name = "shop_id")
    var shopId: Long = 0

    @Column(name = "shop_name")
    var shopName: String = ""

    @Column(name = "order_id")
    var orderId: Long = 0

    @Column(name = "order_code")
    var orderCode: String = ""

    @Column(name = "order_amount")
    var orderAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "pay_tran_id")
    var payTranId: String = ""

    @Column(name = "transaction_id")
    var transactionId: String = ""

    @Column(name = "refund_amount")
    var refundAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "type_id")
    var typeId: Long = 0

    @Column(name = "type_name")
    var typeName: String = ""

    @Column(name = "reason_id")
    var reasonId: Long = 0

    @Column(name = "reason")
    var reason: String = ""

    @Column(name = "method_id")
    var methodId: Long = 0

    @Column(name = "method_name")
    var methodName: String = ""

    @Column(name = "description")
    var description: String = ""

    @Column(name = "product_id")
    var productId: Long = 0

    @Column(name = "product_code")
    var productCode: String = ""

    @Column(name = "product_name")
    var productName: String = ""

    @Column(name = "sales_user_id")
    var salesUserId: Long = 0

    @Column(name = "sales_user_name")
    var salesUserName: String = ""

    @Column(name = "apply_user_id")
    var applyUserId: Long = 0

    @Column(name = "apply_user_name")
    var applyUserName: String = ""

    @Column(name = "apply_time")
    @Type(type = "timestamp")
    var applyTime: Date = Date()

    @Column(name = "status_id")
    var statusId: Long = 0

    @Column(name = "status_name")
    var statusName: String = ""

    @Column(name = "create_time")
    @Type(type = "timestamp")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @Type(type = "timestamp")
    var updateTime: Date = Date()
}
