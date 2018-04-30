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
@Table(name = "erp_paypal_account")
data class PaypalAccountInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0
) {

    @Column(name = "paypal_account")
    var paypalAccount: String = ""

    @Column(name = "`desc`")
    var desc: String = ""

    @Column(name = "client_id")
    var clientId: String = ""

    @Column(name = "secret")
    var secret: String = ""

    @Column(name = "balance")
    var balance: BigDecimal = BigDecimal.ZERO

    @Column(name = "status_id")
    var statusId: Long = 0

    @Column(name = "create_time")
    @Type(type = "timestamp")
    @VerboseName("创建时间")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @Type(type = "timestamp")
    @VerboseName("修改时间")
    var updateTime: Date = Date()
}
