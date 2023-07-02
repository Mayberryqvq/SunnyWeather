package com.sunnyweather.android.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 由于构建的Retrofit对象是全局通用的，只需要在调用create()方法时针对不同的Service接口传入相应的Class类型即可
 * 因此可以将这部分功能封装起来
 **/
//Retrofit构建器
object ServiceCreator {
    //服务器的基地址
    private const val BASE_URL = "https://api.caiyunapp.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) //JSON数据转化为Java对象
        .build()

    //根据给定的服务类创建对应的服务实例
    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    /**
    * 使用inline关键字来修饰方法，使用reified关键字修饰泛型，这是泛型实化的两大前提条件
    * 现在可以用val appService = ServiceCreator.create<AppService>() 这种方式来获取AppService接口的动态代理对象
    **/
    inline fun <reified T> create(): T = create(T::class.java)
}