package ozosync.domain

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.Type
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
@Table(name = "erp_product_bind")
data class ProductBindInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0
) {

    @Column(name = "code")
    var code: String = ""

    @Column(name = "name")
    var name: String = ""

    @Column(name = "desc")
    var desc: String = ""

    @Column(name = "bind_list")
    var bindList: String = ""

    @ManyToOne(targetEntity = StatusInfo::class)
    @JoinColumn(name = "status_id", nullable = false)
    @Fetch(FetchMode.SELECT)
    @VerboseName("状态")
    var status: StatusInfo = StatusInfo()

    @Column(name = "create_time")
    @Type(type = "timestamp")
    @VerboseName("创建时间")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @Type(type = "timestamp")
    @VerboseName("修改时间")
    var updateTime: Date = Date()
}
