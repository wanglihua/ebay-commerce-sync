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
@Table(name = "erp_exchange_rate")
data class ExchangeRateInfo(
        @Id
        @Column(name = "id", nullable = false)
        var id: Long = 0) {

    @Column(name = "currency_id")
    var currencyId: Long = 0

    @Column(name = "status_id")
    var statusId: Long = 0

    @Column(name = "rate")
    var rate: Float = 0F

    @Column(name = "create_time")
    @VerboseName("创建时间")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @VerboseName("修改时间")
    var updateTime: Date = Date()
}

/*

create table erp_exchange_rate
(
	id bigint auto_increment
		primary key,
	currency_id bigint not null,
	rate float not null,
	status_id bigint not null,
	create_time datetime not null,
	update_time datetime not null,
	constraint UQE_erp_exchange_rate_currency_id
		unique (currency_id)
)
engine=InnoDB charset=utf8
;

create index IDX_erp_exchange_rate_status_id
	on erp_exchange_rate (status_id)
;

 */