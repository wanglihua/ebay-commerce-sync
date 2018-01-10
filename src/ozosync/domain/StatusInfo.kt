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
@Table(name = "erp_status")
@VerboseName("状态")
data class StatusInfo(
        @Id
        @Column(name = "id")
        var id: Long = 0) {

    @Column(name = "type")
    @VerboseName("类型")
    var type: Long = 0

    @Column(name = "name")
    @VerboseName("名称")
    var name: String = ""

    @Column(name = "desc")
    @VerboseName("描述")
    var desc: String = ""

    @Column(name = "create_time")
    @VerboseName("创建时间")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @VerboseName("更新时间")
    var updateTime: Date = Date()
}
