package com.example.weatherapp

import android.os.AsyncTask              // xu ly cong viec o luong background, giao tiep voi ui ma khong can handle
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

//openweathermap
class MainActivity : AppCompatActivity() {

    val CITY: String = "HaNoi"
    val API: String = "0e41ee82c9583403eca4e45538249841" // Use API key => de truyen vao api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherTask().execute()

    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {    // lop long nhau
        // 1.Gia tri bien duoc truyen vao khi thuc thi va dc truyen vao doinbackground
        // 2.La gia tri bien de update ui luc tien hanh thuc thi, duoc truyen vao onProgressUpdate
        // 3.result la bien de luu ket qua sau khi tien trinh duoc thuc hien xong
        
        override fun onPreExecute() {   //Tự động được gọi đầu tiên khi tiến trình được kích hoạt.
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errortext).visibility = View.GONE
        }

        // ham nay de thuc hien cac tac vu chay ngam
        override fun doInBackground(vararg params: String?): String? {
            var response:String?    // bien nay se nhận phản hồi từ api
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(   // truyen api theo ten CITY
                    //Đọc toàn bộ nội dung của URL này dưới dạng Chuỗi sử dụng UTF-8
                    Charsets.UTF_8 
                )
            }catch (e: Exception){
                response = null
            }
            return response //Trả về một chuỗi với toàn bộ nội dung URL này
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)        // Kieu json để đọc dữ liệu
                val main = jsonObj.getJSONObject("main")             // đọc dữ liệu từ result đã được thực hiện ở hàm doinbackground
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")            
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
                val temp = main.getString("temp")+"°C"
                val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
                val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")

                /* Populating extracted data into our views */
                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText                                        // đổ dữ liệu vào màn hình theo mã id đã cài đặt
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errortext).visibility = View.VISIBLE
            }

        }
    }
}
