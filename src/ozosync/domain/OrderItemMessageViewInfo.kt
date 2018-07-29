package ozosync.domain

import org.hibernate.annotations.Subselect
import ozosync.common.VerboseName
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
@Subselect("""
SELECT
    oi.id           AS id,
    o.id            AS order_id,
    o.buyer_user_id AS buyer_user_id,
    oi.item_id      AS item_id
FROM erp_order_item oi
    INNER JOIN erp_order o ON oi.order_id = o.id
""")
@VerboseName("Order Item 用于发送信息")
data class OrderItemMessageViewInfo(
        @Id
        @Column(name = "id")
        var id: Long = 0) {

    @Column(name = "order_id")
    var orderId: Long = 0

    @Column(name = "buyer_user_id")
    var buyerUserId: String = ""

    @Column(name = "item_id")
    var itemId: String = ""
}
