package ozosync.domain

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import ozosync.common.VerboseName
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
@Table(name = "erp_order_external_transaction")
data class OrderExternalTransactionInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0) {

    @Column(name = "order_id")
    var orderId: Long = 0

    @Column(name = "transaction_id")
    var transactionId: String = ""

    @Column(name = "transaction_time")
    var transactionTime: Date = Date()

    @Column(name = "payment_id")
    var paymentId: Long = 0

    @Column(name = "create_time")
    @VerboseName("创建时间")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @VerboseName("修改时间")
    var updateTime: Date = Date()
}
