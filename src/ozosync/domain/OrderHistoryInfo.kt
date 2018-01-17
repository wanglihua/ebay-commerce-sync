package ozosync.domain

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Type
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
@Table(name = "erp_order_history")
@VerboseName("订单历史")
data class OrderHistoryInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0
) {
    @Column(name = "order_id")
    var orderId: Long = 0

    @Column(name = "status_id")
    var statusId: Long = 0

    @Column(name = "order_code")
    var orderCode: String = ""

    @Column(name = "status_name")
    var statusName: String = ""

    @Column(name = "description")
    var description: String = ""

    @Column(name = "user_id")
    var userId: Long = 0

    @Column(name = "is_delete")
    var delete: Long = 0

    @Column(name = "create_time")
    @Type(type = "timestamp")
    @VerboseName("创建时间")
    var createTime: Date = Date()
}
