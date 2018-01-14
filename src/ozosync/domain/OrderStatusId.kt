package ozosync.domain

object OrderStatusId {
    const val YCJ = 3850 // 订单状态 已创建
    const val YZF = 3900 // 订单状态 已支付
    const val HOLD = 3950 // 订单状态 Hold
    const val PROCESSED_HOLD = 3975 // 订单状态 已处理Hold
    const val DPH = 4000 // 订单状态 待配货
    const val PHZ = 4100 // 订单状态 配货中
    const val YFH = 4200 // 订单状态 已发货
    const val YWC = 4300 // 订单状态 已完成
    const val YQX = 4350 // 订单状态 已取消
}
