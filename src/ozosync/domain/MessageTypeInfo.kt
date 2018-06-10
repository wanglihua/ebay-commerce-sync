package ozosync.domain

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import ozosync.common.VerboseName
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "erp_message_type")
@VerboseName("消息类型")
data class MessageTypeInfo(
        @Id
        @Column(name = "id")
        var id: Long = 0) {

    @Column(name = "big_type_id")
    var bigTypeId: Long = 0

    @Column(name = "big_type_name")
    var bigTypeName: String = ""

    @Column(name = "type_name")
    var typeName: String = ""

    @Column(name = "type_desc")
    var typeDesc: String = ""
}
