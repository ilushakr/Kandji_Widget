package com.example.kandji_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.*
import android.widget.RemoteViews
import android.widget.Toast

var itemList = listOf<WidgetItem>()

class WidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        // обновляем все экземпляры
        for (i in appWidgetIds) {
            updateWidget(context, appWidgetManager, i)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        // Удаляем Preferences
        val editor: SharedPreferences.Editor = context.getSharedPreferences(Config.WIDGET_PREF, Context.MODE_PRIVATE).edit()
        for (widgetID in appWidgetIds) {
            editor.remove(widgetID.toString())
            editor.remove(Config.WIDGET_COUNT + widgetID)
        }
        editor.apply()
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
        val extras = intent.extras

        if (extras != null) mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {

            val sp: SharedPreferences = context.getSharedPreferences(Config.WIDGET_PREF, Context.MODE_PRIVATE)
            var cnt: Int = sp.getInt(Config.WIDGET_COUNT + mAppWidgetId, 0)
            var countItems = sp.getInt("count$mAppWidgetId", 0)

            when {
                intent.action.equals(ACTION_NEXT) -> {
                    if(cnt == countItems - 1) {
                        sp.edit().putInt(Config.WIDGET_COUNT + mAppWidgetId, -1).apply()
                        cnt = sp.getInt(Config.WIDGET_COUNT + mAppWidgetId, 0)
                    }
                    sp.edit().putInt(Config.WIDGET_COUNT + mAppWidgetId, ++cnt).apply()
                    updateWidget(context, AppWidgetManager.getInstance(context), mAppWidgetId)
                }
                intent.action.equals(ACTION_PREVIOUS) -> {
                    if(cnt == 0) {
                        sp.edit().putInt(Config.WIDGET_COUNT + mAppWidgetId, countItems).apply()
                        cnt = sp.getInt(Config.WIDGET_COUNT + mAppWidgetId, 0)
                    }
                    sp.edit().putInt(Config.WIDGET_COUNT + mAppWidgetId, --cnt).apply()
                    updateWidget(context, AppWidgetManager.getInstance(context), mAppWidgetId)
                }
                intent.action.equals(ACTION_COPY) -> {
                    val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cnt = sp.getInt(Config.WIDGET_COUNT + mAppWidgetId, 0)
                    val clip = ClipData.newPlainText("", getList(sp.getString(mAppWidgetId.toString(), "N5")!!, context)[cnt].kandji)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show()
                }
                // Обновляем виджет
            }
            // Обновляем виджет

        }

    }

    companion object {
        const val ACTION_NEXT = "nextCard"
        const val ACTION_PREVIOUS = "previousCard"
        const val ACTION_COPY = "copyKanji"


        fun updateWidget(ctx: Context, appWidgetManager: AppWidgetManager, widgetID: Int) {

            val sp = ctx.getSharedPreferences(Config.WIDGET_PREF, Context.MODE_PRIVATE)

            //what in editText
            // Читаем счетчик
            var str = sp.getString(widgetID.toString(), "N5")
            var count = sp.getInt(Config.WIDGET_COUNT + widgetID, 0)


            //itemList = getList(str!!, ctx)
            val widgetView = RemoteViews(ctx.packageName, R.layout.widget)

            widgetView.setTextViewText(R.id.kandji, getList(str!!, ctx)[count].kandji)
            widgetView.setTextViewText(R.id.hiragana, getList(str!!, ctx)[count].romaji)
            widgetView.setTextViewText(R.id.romaji, getList(str!!, ctx)[count].meaning)
            widgetView.setTextViewText(R.id.meaning, getList(str!!, ctx)[count].hiragana)

            widgetView.setTextViewText(R.id.previousButton, "${count + 1}/${getList(str!!, ctx).size}")

            widgetView.setTextViewText(R.id.nextButton, "Level $str")



            // Счетчик нажатий (третья зона)
            val intentNext = Intent(ctx, WidgetProvider::class.java)
            intentNext.action = ACTION_PREVIOUS
            intentNext.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
            var pendingIntentMove = PendingIntent.getBroadcast(ctx, widgetID, intentNext, 0)
            widgetView.setOnClickPendingIntent(R.id.previousButton, pendingIntentMove)


            val intentPrevious = Intent(ctx, WidgetProvider::class.java)
            intentPrevious.action = ACTION_NEXT
            intentPrevious.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
            pendingIntentMove = PendingIntent.getBroadcast(ctx, widgetID, intentPrevious, 0)
            widgetView.setOnClickPendingIntent(R.id.nextButton, pendingIntentMove)


            val intentCopyKanji = Intent(ctx, WidgetProvider::class.java)
            intentCopyKanji.action = ACTION_COPY
            intentCopyKanji.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
            pendingIntentMove = PendingIntent.getBroadcast(ctx, widgetID, intentCopyKanji, 0)
            widgetView.setOnClickPendingIntent(R.id.copyBotton, pendingIntentMove)

            // Обновляем виджет
            appWidgetManager.updateAppWidget(widgetID, widgetView)
        }

        fun getList(levelOfJPLT : String, context: Context) : List<WidgetItem> {
            var levelList = mutableListOf<WidgetItem>()
            val myDatabase = DatabaseOpenHelper(context).readableDatabase
            val cursor = myDatabase.rawQuery("SELECT kanji, hiragana, meaning, yarksi FROM items_new WHERE lvl=?", arrayOf(levelOfJPLT))
            cursor.moveToFirst();
            while (!cursor.isAfterLast) {
                levelList.add(WidgetItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)))
                cursor.moveToNext();
            }
            cursor.close()
            myDatabase.close()
            return levelList
        }

    }


}