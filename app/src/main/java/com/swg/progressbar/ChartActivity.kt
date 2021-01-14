package com.swg.progressbar

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_chart.*

/**
 *
 * @ProjectName:    ProgressBar
 * @ClassName:      ChartActivity
 * @Author:         Owen
 * @CreateDate:     2021/1/12 17:37
 * @UpdateUser:     更新者
 * @Description:     java类作用描述
 */
class ChartActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        var investRateList = arrayListOf<InvestRate>()
        investRateList.add(InvestRate().apply {
            investRate = 10.3
            rateDate = System.currentTimeMillis()
        })
        investRateList.add(InvestRate().apply {
            investRate = 15.3
            rateDate = System.currentTimeMillis()
        })
        investRateList.add(InvestRate().apply {
            investRate = 14.3
            rateDate = System.currentTimeMillis()
        })
        investRateList.add(InvestRate().apply {
            investRate = 6.3
            rateDate = System.currentTimeMillis()
        })
        investRateList.add(InvestRate().apply {
            investRate = 3.3
            rateDate = System.currentTimeMillis()
        })
        investRateList.add(InvestRate().apply {
            investRate = 6.3
            rateDate = System.currentTimeMillis()
        })
        investRateList.add(InvestRate().apply {
            investRate = 16.3
            rateDate = System.currentTimeMillis()
        })
        lineChart.postDelayed({
            lineChart.setDatas(investRateList)
        },100)
        lineChart.setOnSelectListener {
            Log.d("LineChartView", it.toString())
        }
    }

}