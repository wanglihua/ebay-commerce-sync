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
@Table(name = "erp_order_history")
@VerboseName("订单历史")
data class OrderHistoryInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0
) {
    @ManyToOne(targetEntity = OrderInfo::class)
    @JoinColumn(name = "order_id", nullable = false)
    @Fetch(FetchMode.SELECT)
    @VerboseName("订单")
    var order: OrderInfo = OrderInfo()

    @ManyToOne(targetEntity = StatusInfo::class)
    @JoinColumn(name = "status_id", nullable = false)
    @Fetch(FetchMode.SELECT)
    @VerboseName("状态")
    var status: StatusInfo = StatusInfo()

    @Column(name = "order_code")
    var orderCode: String = ""

    @Column(name = "status_name")
    var statusName: String = ""

    @Column(name = "description")
    var description: String = ""

    @Column(name = "user_id")
    var userId: Long = 0

    @Column(name = "is_delete")
    var delete: Long = 0

    @Column(name = "create_time")
    @Type(type = "timestamp")
    @VerboseName("创建时间")
    var createTime: Date = Date()
}

/*

create table erp_order_history
(
	id bigint auto_increment
		primary key,
	order_id bigint not null,
	order_code varchar(200) not null,
	status_id bigint not null,
	status_name varchar(200) not null,
	description varchar(500) not null,
	create_time datetime not null,
	user_id bigint default '0' not null,
	is_delete bigint default '0' not null
)
engine=InnoDB
;

create index IDX_erp_order_history_order_id
	on erp_order_history (order_id)
;

create index IDX_erp_order_history_order_code
	on erp_order_history (order_code)
;

create index IDX_erp_order_history_status_id
	on erp_order_history (status_id)
;

create index IDX_erp_order_history_user_id
	on erp_order_history (user_id)
;

create index ix_order_his_is_delete
	on erp_order_history (is_delete)
;



 */