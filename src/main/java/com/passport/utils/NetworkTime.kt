package com.passport.utils

import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NetworkTime {
    private val webUrl1 = "http://www.bjtime.cn"//bjTime
    private val webUrl2 = "http://www.baidu.com"//百度
    private val webUrl3 = "http://www.taobao.com"//淘宝
    private val webUrl4 = "http://www.ntsc.ac.cn"//中国科学院国家授时中心
    private val webUrl5 = "http://www.360.cn"//360

    // 取得资源对象
    // 生成连接对象
    // 发出连接
    // 读取网站日期时间
    // 转换为标准时间对象
    val websiteDateTimeLong: Long
        get() {
            try {
                val url = URL(webUrl4)
                val uc = url.openConnection()
                uc.connect()
                val ld = uc.date
                val date = Date(ld)
                return date.time
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return 0L
        }

    /**
     * 获取指定网站的日期时间
     *
     * @param webUrl
     * @return
     * @author SHANHY
     * @date 2015年11月27日
     */
    private fun getWebsiteDatetime(webUrl: String): String? {
        try {
            val url = URL(webUrl)// 取得资源对象
            val uc = url.openConnection()// 生成连接对象
            uc.connect()// 发出连接
            val ld = uc.date// 读取网站日期时间
            val date = Date(ld)// 转换为标准时间对象
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)// 输出北京时间
            return sdf.format(date)
            //            return date.getTime();
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}
