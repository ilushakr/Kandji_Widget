package com.example.kandji_widget

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

lateinit var itemList : List<Item>
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    fun getData()
    {
        itemList = mutableListOf()
        CoroutineScope(IO).launch {
            try {
                val item : Item

                var formElement: Elements = (Jsoup.connect("https://japanese-words.org/kanji").get()).select("tr.hidden-xs")
                for(inputElement in formElement)
                {
                    itemList.
                    Log.d("tag", inputElement.select("div.kanji").text())
                    Log.d("tag", inputElement.select("div.kun").text())
                    var str = "/v.*".toRegex().findAll(inputElement.select("a").attr("href")).map { it.value }.toList()[0]
                    var ell: Elements = (Jsoup.connect("https://japanese-words.org/kanji$str").get()).select("table[class=kanji-table]").select("span.hint")
                    Log.d("tag", ell[2].text())
                }

            }catch (e:Exception){Log.d("tag", "err")}

        }
    }
}
