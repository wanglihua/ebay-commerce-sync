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
    @Column(name = "order_id")
    var orderId: Long = 0

    @Column(name = "transaction_id")
    var transactionId: String = ""

    @Column(name = "amount")
    var amount: BigDecimal = BigDecimal.ZERO

    @Column(name = "description")
    var description: String = ""

    @Column(name = "create_time")
    @Type(type = "timestamp")
    var createTime: Date = Date()
}
