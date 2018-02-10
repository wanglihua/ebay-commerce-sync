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
@Table(name = "erp_message")
@VerboseName("消息")
data class MessageInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0) {

    @Column(name = "shop_id")
    var shopId: Long = 0

    @Column(name = "message_id")
    var messageId: String = ""

    @Column(name = "product_id")
    var productId: Long = 0

    @Column(name = "item_id")
    var itemId: String = ""

    @Column(name = "sku")
    var sku: String = ""

    @Column(name = "creation_date")
    var creationDate: Date? = null

    @Column(name = "last_modified_date")
    var lastModifiedDate: Date? = null

    @Column(name = "message_status")
    var messageStatus: String = ""

    @Column(name = "display_to_public")
    var displayToPublic: Boolean = false

    @Column(name = "message_type")
    var messageType: String = ""

    @Column(name = "question_type")
    var questionType: String = ""

    @Column(name = "recipient_id")
    var recipientId: String = ""

    @Column(name = "sender_email")
    var senderEmail: String = ""

    @Column(name = "sender_id")
    var senderId: String = ""

    @Column(name = "subject")
    var subject: String = ""

    @Column(name = "body")
    @Type(type = "text")
    var body: String = ""

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
