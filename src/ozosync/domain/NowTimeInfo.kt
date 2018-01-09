package ozosync.domain

import org.hibernate.annotations.Subselect
import ozosync.common.VerboseName
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
@Subselect("""
SELECT
    1     AS id,
    now() AS now
FROM DUAL
""")
@VerboseName("当前时间信息")
data class NowTimeInfo(
        @Id
        @Column(name = "id")
        var id: Long = 0) {

    @Column(name = "now")
    @VerboseName("当前时间")
    var now: Date = Date()
}
