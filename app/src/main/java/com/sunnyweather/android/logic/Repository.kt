package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * 仓库层，主要任务是判断调用方请求的数据应该是从本地数据源中获取还是从网络数据源中获取
 * 并将获取到的数据返回给调用方
 **/

object Repository {

    /**** -------------------- 获取网络数据源中的数据，返回给调用方 ---------------------------- ****/
    //将liveData()函数的线程参数类型指定成Dispatchers.IO,因为Android不允许在主线程中进行网络请求
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        //调用网络层的searchPlaces()函数搜索城市数据
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        //如果服务器返回的状态时ok，说明数据获取成功
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            //使用Kotlin内置的Result.success()方法来包装获取的城市数据列表
            Result.success(places)
        } else {
            //如果数据获取失败，使用Kotlin内置的Result.failure()方法来包装异常信息
            Result.failure(java.lang.RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    /**
     * 对调用方而言，需要调用2次请求才能获得其想要的所有天气数据是比较烦琐的行为
     * 最好的做法是仓库层中再进行一次统一封装
     * 仓库层的职责本来就是将获取到的数据返回给调用方，所以在这里进行统一封装再好不过了
     **/
    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            /**
             * 获取实时天气信息和获取未来天气信息这2个请求是没有先后顺序的，可以让他们并发执行提高效率
             * 但是要在同时得到他们的响应结果后才能进一步执行程序
             * 只需要分别在两个async函数中发起网络请求，然后再分别调用它们的await()方法
             * 就可以保证只有在两个网络请求都成功响应之后，才会进一步执行程序
             **/
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            //如果2个请求的响应状态都是ok
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                //则封装到一个Weather对象中
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                //使用Result.success()方法来包装这个Weather对象
                Result.success(weather)
            } else {
                //若不是2个请求的响应都成功，则用Result.failure()方法包装一个异常信息
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" + "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }
    /**
     * 由于使用协程来简化网络回调的写法，所以导致SunnyWeatherNetwork中封装的每个网络请求
     * 接口都肯呢个会异常，于是我们必须在仓库层中为每个网络请求都进行try catch操作，增加了仓库层代码实现的复杂度
     * 可以在某个统一的入口函数中进行封装，使用时只要进行一次try catch处理就行了
     **/
    //仓库层中定义的方法，为了能将异步获取的数据以响应式编程的方式通知给上层，通常会返回一个LiveData对象
    private fun<T> fire(context: CoroutineContext, block: suspend() -> Result<T>) =
            /**liveData()函数，是lifecycle-livedata-ktx库提供的一个非常强大且好用的功能
             * 它可以自动构建并返回一个LiveData对象，然后在它的代码块中提供一个挂起函数的上下文
             * 这样就可以在liveData()函数的代码块中调用任何的挂起函数
             *
             * 在liveData()函数的代码块中，我们是拥有挂起函数上下文的，可是当回调到Lambda表达式中，
             * 代码就没有挂起函数上下文了，但实际上Lambda表达式中的代码一定也是在挂起函数中运行的。
             * 为了解决这个问题，我们需要在函数类型前声明一个suspend关键字，以表示所有传入的Lambda
             * 表达式中的代码也是拥有挂起函数上下文的
             **/
            liveData(context) {
                val result = try {
                    block()
                } catch (e: Exception) {
                    Result.failure<T>(e)
                }
                /**类似于调用LiveData的setValue()方法来通知数据变化
                 * 只不过这里我们无法直接取得返回的LiveData对象，所以ifecycle-livedata-ktx库
                 * 提供了这样一个替代方法
                 *
                 * LiveData中的setValue()：在主线程中给LiveData设置数据
                 **/
                emit(result)
            }

    /**** -------------------- 获取本地数据源中的数据，返回给调用方 ---------------------------- ****/
    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

}