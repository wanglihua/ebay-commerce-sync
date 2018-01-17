package ozosync.domain

object OrderStatus {
    const val ID_YCJ: Long = 3850 // 订单状态 已创建
    const val ID_YZF: Long = 3900 // 订单状态 已支付
    const val ID_HOLD: Long = 3950 // 订单状态 Hold
    const val ID_PROCESSED_HOLD: Long = 3975 // 订单状态 已处理Hold
    const val ID_DPH: Long = 4000 // 订单状态 待配货
    const val ID_PHZ: Long = 4100 // 订单状态 配货中
    const val ID_YFH: Long = 4200 // 订单状态 已发货
    const val ID_YWC: Long = 4300 // 订单状态 已完成
    const val ID_YQX: Long = 4350 // 订单状态 已取消
}
