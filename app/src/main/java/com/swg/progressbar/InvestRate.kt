package com.swg.progressbar

/**
 *
 * @ProjectName:    hiwallet
 * @ClassName:      InvestRate
 * @Author:         Owen
 * @CreateDate:     2020/11/16 16:35
 * @UpdateUser:     更新者
 * @Description:    年化收益
 */
class InvestRate {

    var investRate: Double = 0.0 // 截止时间年化收益率
    var rateDate: Long = 0 // 计算收益率日期

    override fun toString(): String {
        return "InvestRate(investRate=$investRate, rateDate=$rateDate)"
    }

}