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
@Table(name = "erp_order_feedback")
@VerboseName("订单反馈")
data class OrderFeedbackInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0
) {
    @Column(name = "order_id")
    var orderId: Long = 0

    @Column(name = "order_item_id")
    var orderItemId: Long = 0

    @Column(name = "comment_type")
    var commentType: Long = 0

    @Column(name = "comment_text")
    var commentText: String = ""

    @Column(name = "create_time")
    @Type(type = "timestamp")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @Type(type = "timestamp")
    var updateTime: Date = Date()
}
