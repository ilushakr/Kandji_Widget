package com.example.kandji_widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.activity_config.*


//var itemList : MutableList<WidgetItem> = mutableListOf(WidgetItem("一", "один", "ひとつ", "ichi"))
lateinit var levelOfJPLT : String
var countOfItems = 0

class Config : Activity() {
    var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    var resultValue: Intent? = null
    lateinit var sp : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // извлекаем ID конфигурируемого виджета
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }
        // и проверяем его корректность
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }

        // формируем intent ответа
        resultValue = Intent()
        resultValue!!.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)

        // отрицательный ответ
        setResult(RESULT_CANCELED, resultValue)
        setContentView(R.layout.activity_config)
        sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);

        val cnt = sp.getInt(Config.WIDGET_COUNT + widgetID, -1)
        if (cnt == -1) sp.edit().putInt(WIDGET_COUNT + widgetID, 0)
    }


    fun onClick(v: View?) {

        sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE)


        val selRBColor = (findViewById<RadioGroup>(radio_group.id)).checkedRadioButtonId
        when (selRBColor) {
            level_N5.id -> {levelOfJPLT = "N5"; countOfItems = 103}
            level_N4.id -> {levelOfJPLT = "N4"; countOfItems = 181}
            level_N3.id -> {levelOfJPLT = "N3"; countOfItems = 366}
            level_N2.id -> {levelOfJPLT = "N2"; countOfItems = 373}
            level_N1_1.id -> {levelOfJPLT = "N1/1"; countOfItems = 250}
            level_N1_2.id -> {levelOfJPLT = "N1/2"; countOfItems = 250}
            level_N1_3.id -> {levelOfJPLT = "N1/3"; countOfItems = 250}
            level_N1_4.id -> {levelOfJPLT = "N1/4"; countOfItems = 250}
            level_N1_5.id -> {levelOfJPLT = "N1/5"; countOfItems = 113}
        }


        sp.edit().putString(widgetID.toString(), levelOfJPLT).apply()
        sp.edit().putInt("count$widgetID", countOfItems).apply()

        WidgetProvider.updateWidget(this, AppWidgetManager.getInstance(this), widgetID)
        //
        // положительный ответ
        setResult(RESULT_OK, resultValue)
        finish()
        //getData(widgetID, this)
    }

    companion object {
        const val WIDGET_COUNT = "widget_count"
        const val WIDGET_PREF = "widget_pref"
    }
}