package com.countdownday.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.countdownday.MainActivity
import com.countdownday.R
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CountdownWidgetProvider : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onEnabled(context: Context) {
        // 首次创建小组件时调用
    }
    
    override fun onDisabled(context: Context) {
        // 最后一个小部件被删除时调用
    }
    
    companion object {
        private const val PREFS_NAME = "CountdownWidgetPrefs"
        private const val KEY_EVENT_NAME = "event_name_"
        private const val KEY_EVENT_DATE = "event_date_"
        private const val KEY_EVENT_ID = "event_id_"
        
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // 从SharedPreferences获取事件数据
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val eventName = prefs.getString("${KEY_EVENT_NAME}$appWidgetId", "倒数日") ?: "倒数日"
            val eventDateStr = prefs.getString("${KEY_EVENT_DATE}$appWidgetId", "2026-12-31")
            
            // 计算天数
            val daysRemaining = try {
                val eventDate = LocalDate.parse(eventDateStr)
                ChronoUnit.DAYS.between(LocalDate.now(), eventDate)
            } catch (e: Exception) {
                0L
            }
            
            // 创建RemoteViews
            val views = RemoteViews(context.packageName, R.layout.countdown_widget)
            
            // 设置事件名称
            views.setTextViewText(R.id.widget_event_name, eventName)
            
            // 设置天数
            val daysText = when {
                daysRemaining == 0L -> "今天"
                daysRemaining > 0L -> "$daysRemaining"
                else -> "${-daysRemaining}"
            }
            views.setTextViewText(R.id.widget_days, daysText)
            
            // 设置描述
            val descText = when {
                daysRemaining == 0L -> "就是今天！"
                daysRemaining > 0L -> "天"
                else -> "天已过"
            }
            views.setTextViewText(R.id.widget_description, descText)
            
            // 设置日期
            views.setTextViewText(R.id.widget_date, eventDateStr)
            
            // 设置点击事件 - 打开应用
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("widget_id", appWidgetId)
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
            
            // 更新小部件
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        
        fun saveEventToWidget(
            context: Context,
            appWidgetId: Int,
            eventName: String,
            eventDate: String,
            eventId: Long
        ) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString("${KEY_EVENT_NAME}$appWidgetId", eventName)
                putString("${KEY_EVENT_DATE}$appWidgetId", eventDate)
                putLong("${KEY_EVENT_ID}$appWidgetId", eventId)
                apply()
            }
        }
    }
}
