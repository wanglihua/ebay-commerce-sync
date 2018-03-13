package ozosync.domain

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import ozosync.common.VerboseName
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "erp_combine_order")
@VerboseName("合并订单")
data class CombineOrderInfo(
        @Id
        @Column(name = "id")
        var id: Long = 0) {

    @Column(name = "order_id")
    var orderId: Long = 0

    @Column(name = "order_code")
    var orderCode: String = ""

    @Column(name = "sub_order_id")
    var subOrderId: Long = 0

    @Column(name = "sub_order_code")
    var subOrderCode: String = ""

    @Column(name = "create_time")
    var createTime: Date = Date()
}
