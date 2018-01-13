package ozosync.domain

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import ozosync.common.VerboseName
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "erp_ebay_api_config")
@VerboseName("eBay API 配置")
data class EBayApiConfigInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0) {

    @ManyToOne(targetEntity = ShopAccountInfo::class)
    @JoinColumn(name = "shop_id", nullable = false, unique = true)
    @Fetch(FetchMode.SELECT)
    @VerboseName("店铺")
    var shopAccount: ShopAccountInfo = ShopAccountInfo()

    @Column(name = "app_id")
    @VerboseName("app id")
    var appId: String = ""

    @Column(name = "cert_id")
    @VerboseName("cert id")
    var certId: String = ""

    @Column(name = "dev_id")
    @VerboseName("dev id")
    var devId: String = ""

    @Column(name = "token")
    @VerboseName("token")
    var token: String = ""

    @Column(name = "create_time")
    @VerboseName("创建时间")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @VerboseName("更新时间")
    var updateTime: Date = Date()
}
