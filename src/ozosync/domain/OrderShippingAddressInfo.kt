package ozosync.domain

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
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
@Table(name = "erp_order_shipping_address")
@VerboseName("订单地址")
data class OrderShippingAddressInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0) {

    @Column(name = "order_id")
    var orderId: Long = 0

    @Column(name = "name")
    @VerboseName("姓名")
    var name: String = ""

    @Column(name = "street1")
    @VerboseName("街道1")
    var street1: String = ""

    @Column(name = "street2")
    @VerboseName("街道2")
    var street2: String = ""

    @Column(name = "city_name")
    @VerboseName("城市")
    var cityName: String = ""

    @Column(name = "province")
    @VerboseName("州")
    var province: String = ""

    @Column(name = "country")
    @VerboseName("国家")
    var country: String = ""

    @Column(name = "country_name")
    @VerboseName("国家名称")
    var countryName: String = ""

    @Column(name = "phone")
    @VerboseName("联系电话")
    var phone: String = ""

    @Column(name = "postal_code")
    @VerboseName("邮编")
    var postalCode: String = ""

    @Column(name = "address_id")
    @VerboseName("地址id")
    var addressId: String = ""

    @Column(name = "address_owner")
    @VerboseName("地址owner")
    var addressOwner: String = ""

    @Column(name = "external_address_id")
    @VerboseName("外部地址id")
    var externalAddressId: String = ""

    @Column(name = "create_time")
    @VerboseName("创建时间")
    var createTime: Date = Date()

    @Column(name = "update_time")
    @VerboseName("修改时间")
    var updateTime: Date = Date()
}

/*

create table erp_order_shipping_address
(
	id bigint auto_increment
		primary key,
	order_id bigint not null,
	name varchar(200) not null,
	street1 varchar(200) not null,
	street2 varchar(200) not null,
	city_name varchar(200) not null,
	province varchar(200) not null,
	country varchar(200) not null,
	country_name varchar(200) not null,
	phone varchar(200) not null,
	postal_code varchar(200) not null,
	address_id varchar(200) not null,
	address_owner varchar(200) not null,
	external_address_id varchar(200) not null,
	create_time datetime not null,
	update_time datetime not null
)
engine=InnoDB charset=utf8
;

create index IDX_erp_order_shipping_address_order_id
	on erp_order_shipping_address (order_id)
;

 */