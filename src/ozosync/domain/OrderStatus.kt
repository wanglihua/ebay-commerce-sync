package ozosync.domain

object OrderStatus {
    const val ID_YCJ = 3850 // 订单状态 已创建
    const val ID_YZF = 3900 // 订单状态 已支付
    const val ID_HOLD = 3950 // 订单状态 Hold
    const val ID_PROCESSED_HOLD = 3975 // 订单状态 已处理Hold
    const val ID_DPH = 4000 // 订单状态 待配货
    const val ID_PHZ = 4100 // 订单状态 配货中
    const val ID_YFH = 4200 // 订单状态 已发货
    const val ID_YWC = 4300 // 订单状态 已完成
    const val ID_YQX = 4350 // 订单状态 已取消
}
