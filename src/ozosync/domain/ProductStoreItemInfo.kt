package ozosync.domain

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Type
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
@Table(name = "erp_product_store_item")
data class ProductStoreItemInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0) {

    @Column(name = "product_id")
    var productId: Long = 0

    @Column(name = "shop_id")
    var shopId: Long = 0

    @Column(name = "item_id")
    var itemId: String = ""

    @Column(name = "view_item_url")
    var viewItemUrl: String = ""

    @Column(name = "create_time")
    @Type(type = "timestamp")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @Type(type = "timestamp")
    var updateTime: Date = Date()
}
