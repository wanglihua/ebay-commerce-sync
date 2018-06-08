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
    var creationDate: Date? = null // member?, my

    @Column(name = "receive_date")
    var receiveDate: Date? = null // my

    @Column(name = "expiration_date")
    var expirationDate: Date? = null // my

    @Column(name = "external_message_id")
    var externalMessageId: String = "" // my

    @Column(name = "parent_message_id")
    var parentMessageId: String = "" // member

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

    // MemberMessage download time
    @Column(name = "member_download_time")
    @Type(type = "timestamp")
    var memberDownloadTime: Date? = null

    // MyMessage download time
    @Column(name = "my_download_time")
    @Type(type = "timestamp")
    var myDownloadTime: Date? = null

    /*
	SaleUserId         int64             `xorm:"bigint notnull index 'sale_user_id' default(0)" json:"sale_user_id" smith:"verbose_name=销售人员"`
	SaleUser           string            `xorm:"nvarchar(255) notnull 'sale_user'" json:"sale_user"`
	ServiceUserId      int64             `xorm:"bigint notnull index 'service_user_id' default(0)" json:"service_user_id" smith:"verbose_name=客服人员"`
	ServiceUser        string            `xorm:"nvarchar(255) notnull 'service_user'" json:"service_user"`
	DevUserId          int64             `xorm:"bigint notnull index 'dev_user_id' default(0)" json:"dev_user_id" smith:"verbose_name=开发人员"`
	DevUser            string            `xorm:"nvarchar(255) notnull 'dev_user'" json:"dev_user"`
	AssignedUserId     int64             `xorm:"bigint notnull index 'assigned_user_id' default(0)" json:"assigned_user_id" smith:"verbose_name=指派人员"`
	AssignedUser       string            `xorm:"nvarchar(255) notnull 'assigned_user'" json:"assigned_user"`
	TypeId             int64             `xorm:"bigint notnull index 'type_id' default(0)" json:"type_id"`
	TypeName           string            `xorm:"nvarchar(100) notnull 'type_name'" json:"type_name"`

     */

    @Column(name = "create_time")
    @Type(type = "timestamp")
    @VerboseName("创建时间")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @Type(type = "timestamp")
    @VerboseName("修改时间")
    var updateTime: Date = Date()
}
