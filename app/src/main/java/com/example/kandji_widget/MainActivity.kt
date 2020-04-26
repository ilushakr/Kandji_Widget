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

lateinit var itemList : MutableList<Item>

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
                var formElement: Elements = (Jsoup.connect("https://japanese-words.org/kanji").get()).select("tr.hidden-xs")
                for(inputElement in formElement)
                {
                    var str = "/v.*".toRegex().findAll(inputElement.select("a").attr("href")).map { it.value }.toList()[0]
                    itemList.add(Item(inputElement.select("div.kanji").text(),
                        inputElement.select("div.meaning").text(),
                        inputElement.select("div.kun").text(),
                        (Jsoup.connect("https://japanese-words.org/kanji$str").get()).select("table[class=kanji-table]").select("span.hint")[2].text()))
                }
            }catch (e:Exception){Log.d("tag", "err")}

        }
    }
}
