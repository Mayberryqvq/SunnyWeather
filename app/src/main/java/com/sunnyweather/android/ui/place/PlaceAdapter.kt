package com.sunnyweather.android.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.activity_weather.*
import org.w3c.dom.Text

//适配器需要继承自RecyclerView.Adapter，泛型指定为PlaceAdapter.ViewHolder
class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    //内部类ViewHolder需要继承自RecyclerView.ViewHolder
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        //传入参数是RecyclerView子项的最外层布局place_item，这样可以通过findViewById来访问布局中的实例
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
    }

    //重写onCreateViewHolder()，用来创建ViewHolder实例
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //将RecyclerView子项的布局进行解析，得到子项布局对应的View
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        //创建ViewHolder实例
        val holder = ViewHolder(view)
        /**给place_item.xml的最外层布局注册了一个点击事件监听器
         * 在点击事件中获取当前点击项的经纬度坐标和地区名称，并传入Intent中
         * 最后启动WeatherActivity
         **/
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeList[position]
            /**如果当前Fragment是在WeatherActivity中，就关闭滑动菜单
             * 给WeatherViewModel赋值新的经纬度坐标和地区名称，然后刷新城市的天气信息
             * 如果是在MainActivity中，那么就保持之前的处理逻辑不变即可
             **/
            val activity = fragment.activity
            if (activity is WeatherActivity) {
                activity.drawerLayout.closeDrawers()
                activity.viewModel.locationLng = place.location.lng
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.placeName = place.name
                activity.refreshWeather()
            } else {
                val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                    putExtra("location_lng", place.location.lng)
                    putExtra("location_lat", place.location.lat)
                    putExtra("place_name", place.name)
                }
                fragment.startActivity(intent)
                activity?.finish()
            }
            fragment.viewModel.savePlace(place)
        }
        return holder
    }

    //对子项数据进行赋值
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }

    //RecyclerView中一共有多少子项，返回数据源长度即可
    override fun getItemCount() = placeList.size

}