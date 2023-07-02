package com.sunnyweather.android.ui.place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.fragment_place.*


class PlaceFragment : Fragment() {
    //使用by lazy进行懒加载，允许我们随时使用这个变量，并且在使用时才初始化
    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //如果当前已有存储的城市数据，就获取已存储数据并解析成Place对象
        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            //取得该对象的经纬度坐标和城市名直接跳转并传递给WeatherActivity，这样用户就不需要每次都重新搜索
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        //设置布局管理器
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        //设置适配器，传入ViewModel对象中的placeList作为数据源
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter

        /**调用EditText控件的addTextChangedListener()方法监听搜索框内容的变化情况
         * 每当搜索框中的内容发生了变化，我们就获取新的内容
         * 并传递给PlaceViewModel对象的searchPlaces()方法，发起搜索城市数据的网络请求
         **/
        searchPlaceEdit.addTextChangedListener {  editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                //当输入搜索框中内容为空，就将RecyclerView隐藏起来，同时显示背景图片
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                //通知观察者们，数据已经发生变化
                adapter.notifyDataSetChanged()
            }
        }

        //对服务器的响应数据进行观察，当有任何数据发生变化，就会回调到传入的Observer接口实现中
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer{ result ->
            val places = result.getOrNull()
            if (places != null) {
                //对回调数据进行判断，若不为空就加入PlaceViewModel对象placeList集合中，并刷新页面
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                //通知观察者们，数据已经发生变化
                adapter.notifyDataSetChanged()
            } else {
                //Toast进行提示，打印异常信息
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }

}