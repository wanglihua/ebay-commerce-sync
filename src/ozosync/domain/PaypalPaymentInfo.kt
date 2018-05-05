package ozosync.domain

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Type
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
@Table(name = "erp_paypal_payment")
data class PaypalPaymentInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0
) {

    @Column(name = "transaction_id")
    var transactionId: String = ""

    @Column(name = "type")
    var type: String = ""

    @Column(name = "payer")
    var payer: String = ""

    @Column(name = "payer_display_name")
    var payerDisplayName: String = ""

    @Column(name = "currency")
    var currency: String = ""

    @Column(name = "gross_amount")
    var grossAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "fee_amount")
    var feeAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "net_amount")
    var netAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "status")
    var status: String = ""

    @Column(name = "pay_time")
    @Type(type = "timestamp")
    var payTime: Date = Date()

    @Column(name = "create_time")
    @Type(type = "timestamp")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @Type(type = "timestamp")
    var updateTime: Date = Date()
}
