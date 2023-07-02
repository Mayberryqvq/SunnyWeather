package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.http.Query
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//定义一个统一的网络数据源访问入口，对所有网络请求的API进行封装
object SunnyWeatherNetwork {

    /****--------------------对WeatherService接口进行封装--------------------****/
    private val weatherService = ServiceCreator.create(WeatherService::class.java)

    suspend fun getDailyWeather(lng: String, lat: String) = weatherService.getDailyWeather(lng, lat).await()

    suspend fun getRealtimeWeather(lng: String, lat: String) = weatherService.getRealtimeWeather(lng, lat).await()

    /****--------------------对PlaceService接口进行封装--------------------****/
    private val placeService = ServiceCreator.create(PlaceService::class.java)

    /**
     * 当外部调用该方法，Retrofit会立即发起网络请求，同时当前的协程也会被阻塞住。
     * 直到服务器响应我们的请求之后，await()函数会将解析出来的数据模型对象取出并返回，同时回复当前协程的执行，
     * searchPlaces()函数在得到await()函数的返回值后会将该数据再返回到上一层
     * **/
    suspend fun searchPlaces(query: String)  = placeService.searchPlaces(query).await()

    //定义成Call<T>的拓展函数，这样所有返回值是Call类型的Retrofit网络请求接口就可以直接调用该函数
    private suspend fun <T> Call<T>.await(): T {
        /** suspendCoroutine函数必须在协程作用域或挂起函数中才能调用，主要作用是将当前协程立即挂起 ，
         *  然后在一个普通的线程中执行Lambda表达式中的代码
         **/
        return suspendCoroutine { continuation ->
            enqueue(object: Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    //如果请求成功，就恢复被挂起的协程，并传入服务器响应的数据
                    if (body != null) continuation.resume(body)
                    //如果请求失败就恢复被挂起的协程，并传入具体的异常原因
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    //如果请求失败就恢复被挂起的协程，并传入具体的异常原因
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}