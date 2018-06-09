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
@Table(name = "auth_user")
@VerboseName("用户")
data class UserInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0) {

    @Column(name = "login_name")
    var loginName: String = ""

    @Column(name = "nick_name")
    var nickName: String = ""

    @Column(name = "password")
    var password: String = ""

    @Column(name = "phone")
    var phone: String = ""

    @Column(name = "sex")
    var sex: String = ""

    @Column(name = "is_default_pwd")
    var isDefaultPwd: Long = 0

    @Column(name = "status_id")
    var statusId: Long = 6100

    @Column(name = "create_time")
    @Type(type = "timestamp")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @Type(type = "timestamp")
    var updateTime: Date = Date()
}
