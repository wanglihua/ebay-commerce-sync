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
@Table(name = "erp_shop_account")
@VerboseName("店铺账户")
data class ShopAccountInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0) {

    @Column(name = "platform_id")
    var platformId: Long = 0

    @Column(name = "code")
    @VerboseName("编码")
    var code: String = ""

    @Column(name = "name")
    @VerboseName("名称")
    var name: String = ""

    @Column(name = "paypal_account")
    @VerboseName("paypal账号")
    var paypalAccount: String = ""

    @Column(name = "manager")
    @VerboseName("负责人")
    var manager: String = ""

    @Column(name = "token_result")
    @VerboseName("token验证结果")
    var tokenResult: String = ""

    @Column(name = "token_expire")
    @VerboseName("token过期时间")
    var tokenExpire: Date = Date()

    @Column(name = "desc")
    @VerboseName("描述")
    var desc: String = ""

    @Column(name = "status_id")
    var statusId: Long = 0

    @Column(name = "create_time")
    @VerboseName("创建时间")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @VerboseName("更新时间")
    var updateTime: Date = Date()
}
