package ozosync.domain

object OrderTypeId {

    // 手工创建
    const val SGCJ: Long = 1010

    // 正常下载订单
    const val ZCDD: Long = 1020

    // 拆分订单
    const val CFDD: Long = 1030

    // 合并订单
    const val HBDD: Long = 1040

    // 导入订单
    const val DRDD: Long = 1050
}
