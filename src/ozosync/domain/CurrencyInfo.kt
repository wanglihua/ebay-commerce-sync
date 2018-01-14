package ozosync.domain

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Subselect
import org.hibernate.annotations.Synchronize

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "erp_currency")
data class CurrencyInfo(
        @Id
        @Column(name = "id", nullable = false)
        var id: Long = 0) {

    @Column(name = "code")
    var code: String = ""

    @Column(name = "name")
    var name: String = ""

    @Column(name = "desc")
    var desc: String = ""
}

/*

create table erp_currency
(
	id bigint auto_increment primary key,
	code varchar(200) not null,
	name varchar(200) not null,
	`desc` varchar(500) null
)
engine=InnoDB charset=utf8
;

create index IDX_erp_currency_code
	on erp_currency (code)
;

 */