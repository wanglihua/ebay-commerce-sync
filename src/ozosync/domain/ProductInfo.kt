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
@Table(name = "erp_product")
@VerboseName("商品")
data class ProductInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0) {

    @Column(name = "sku")
    var sku: String = ""

    @Column(name = "cn_name")
    var cnName: String = ""

    @Column(name = "name")
    var name: String = ""

    @Column(name = "`desc`")
    var desc: String = ""

    @Column(name = "image_url")
    var imageUrl: String = ""

    @Column(name = "cate_id")
    var cateId: Long? = null

    @Column(name = "cost_price")
    var costPrice: Float = 0f

    @Column(name = "length")
    var length: Float = 0f

    @Column(name = "width")
    var width: Float = 0f

    @Column(name = "height")
    var height: Float = 0f

    @Column(name = "weight")
    var weight: Float = 0f

    @Column(name = "cn_hs_code")
    var cnHsCode: String = ""

    @Column(name = "au_hs_code")
    var auHsCode: String = ""

    @Column(name = "cn_decl_name")
    var cnDeclName: String = ""

    @Column(name = "au_decl_name")
    var auDeclName: String = ""

    @Column(name = "supplier_id")
    var supplierId: Long? = null

    @Column(name = "pkg_mtrl_id")
    var pkgMtrlId: Long? = null

    @Column(name = "need_invoice")
    var needInvoice: Long = 0

    @Column(name = "batch_code")
    var batchCode: Long = 0

    @Column(name = "brand_id")
    var brandId: Long = 0

    @Column(name = "style_id")
    var styleId: Long = 0

    @Column(name = "cert1_id")
    var cert1Id: Long = 0

    @Column(name = "cert2_id")
    var cert2Id: Long = 0

    @Column(name = "cert3_id")
    var cert3Id: Long = 0

    @Column(name = "stock_alert_qty")
    var stockAlertQty: Long = 0

    @Column(name = "stock_alert_days")
    var stockAlertDays: Long = 0

    @Column(name = "purchase_days")
    var purchaseDays: Long = 0

    @Column(name = "sale_user_id")
    var saleUserId: Long = 0

    @Column(name = "service_user_id")
    var serviceUserId: Long = 0

    @Column(name = "dev_user_id")
    var devUserId: Long = 0

    @Column(name = "daily_mean_sales")
    var dailyMeanSales: BigDecimal = BigDecimal.ZERO

    @Column(name = "postage_cal_weight")
    var postageCalWeight: BigDecimal = BigDecimal.ZERO

    @Column(name = "tax_point")
    var taxPoint: BigDecimal = BigDecimal.ZERO

    @Column(name = "drawback_point")
    var drawbackPoint: BigDecimal = BigDecimal.ZERO

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
