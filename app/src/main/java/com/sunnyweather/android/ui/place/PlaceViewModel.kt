package com.sunnyweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Place
import retrofit2.http.Query

/**
 * ViewModel层相当于逻辑层和UI层之间的一个桥梁，虽然更偏向于逻辑层
 * 不过由于ViewModel经常和Activity和Fragment是一一对应的，因此这里放在UI层中
 **/

class PlaceViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    /**对界面上现实的城市数据进行缓存，因为原则上与界面相关的数据都应该放到ViewModel中
     * 这样可以保证手机屏幕发生旋转时不会丢失
     **/
    val placeList = ArrayList<Place>()

    /**
     * switchMap（）的使用场景：如果ViewModel中的某个LiveData对象是调用另外的方法获取的
     * 就可以借助switchMap()方法，将这个LiveData对象转换成另一个可观察的LiveData对象
     *
     * 由于仓库层中的searchPlaces方法返回的都是一个新的LiveData实例，所以无法观察到数据的变化
     **/
    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchPlaces(query)
    }

    //当调用该方法时，不是去调用仓库层的对应方法，而是更改searchLiveData的值，以触发switchMap()方法
    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }

    //封装接口逻辑
    fun savePlace(place: Place) = Repository.savePlace(place)

    fun getSavedPlace() = Repository.getSavedPlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()

}