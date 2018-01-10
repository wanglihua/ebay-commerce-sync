package ozosync.domain

import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


data class OrderInfo(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0) {

}

/*
create table erp_order
(
	id bigint auto_increment primary key,
	shop_id bigint not null,
	code varchar(200) not null,
	`desc` varchar(500) null,
	type_id bigint not null,
	ebay_status varchar(200) not null,
	status_id bigint not null,
	adjustment_amount_currency varchar(10) not null,
	adjustment_amount decimal(18,6) not null,
	adjustment_amount_rmb decimal(18,6) not null,
	amount_paid_currency varchar(10) not null,
	amount_paid decimal(18,6) not null,
	amount_paid_rmb decimal(18,6) not null,
	amount_saved_currency varchar(10) not null,
	amount_saved decimal(18,6) not null,
	amount_saved_rmb decimal(18,6) not null,
	total_amount_currency varchar(10) not null,
	total_amount decimal(18,6) not null,
	total_amount_rmb decimal(18,6) not null,
	subtotal_amount_currency varchar(10) not null,
	subtotal_amount decimal(18,6) not null,
	subtotal_amount_rmb decimal(18,6) not null,
	payment_methods varchar(50) not null,
	seller_email varchar(200) not null,
	seller_user_id varchar(50) not null,
	buyer_user_id varchar(50) not null,
	buyer_checkout_message varchar(5000) null,
	created_time datetime null,
	modified_time datetime null,
	paid_time datetime null,
	shipped_time datetime null,
	hold_type_id bigint null,
	exchange_rate decimal(18,6) not null,
	create_time datetime not null,
	update_time datetime not null,
	express_fee decimal(18,6) not null,
	tran_id varchar(200) null,
	buyer_email varchar(200) not null,
	batch_code bigint null,
	stock_id bigint not null,
	address_id bigint not null,
	company_id bigint not null,
	mode_id bigint not null,
	delivery_person varchar(20) null,
	waybill_number varchar(50) null,
	full_tran_id varchar(200) not null,
	tag_id bigint not null,
	pre_hold_type_id bigint not null,
	profit_au decimal(18,6) not null,
	profit_rmb decimal(18,6) not null,
	cancel_status_id bigint default '0' not null,
	constraint UQE_erp_order_code
		unique (code)
)
engine=InnoDB
;

create index IDX_erp_order_shop_id
	on erp_order (shop_id)
;

create index IDX_erp_order_type_id
	on erp_order (type_id)
;

create index IDX_erp_order_status_id
	on erp_order (status_id)
;

create index IDX_erp_order_hold_type_id
	on erp_order (hold_type_id)
;

create index ix_order_batch_code
	on erp_order (batch_code)
;

create index ix_order_stock_id
	on erp_order (stock_id)
;

create index ix_order_address_id
	on erp_order (address_id)
;

create index ix_order_company_id
	on erp_order (company_id)
;

create index ix_order_mode_id
	on erp_order (mode_id)
;

create index ix_order_tag_id
	on erp_order (tag_id)
;

create index IDX_erp_order_pre_hold_type_id
	on erp_order (pre_hold_type_id)
;


 */