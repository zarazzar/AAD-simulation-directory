package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat.getParcelableExtra
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.habitapp.utils.NOTIF_UNIQUE_WORK
import java.util.concurrent.TimeUnit

class CountDownActivity : AppCompatActivity() {

    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        val habit = getParcelableExtra(intent, HABIT, Habit::class.java)

        if (habit != null) {
            findViewById<TextView>(R.id.tv_count_down_title).text = habit.title

            val viewModel = ViewModelProvider(this).get(CountDownViewModel::class.java)

            //TODO 10 : Set initial time and observe current time. Update button state when countdown is finished
            viewModel.setInitialTime(habit.minutesFocus)
            viewModel.currentTimeString.observe(this) {
                findViewById<TextView>(R.id.tv_count_down).text = it
            }

            viewModel.eventCountDownFinish.observe(this) {
                if (it) {
                    updateButtonState(false)
                }
            }

            //TODO 13 : Start and cancel One Time Request WorkManager to notify when time is up.
            workManager = WorkManager.getInstance(this)

            findViewById<Button>(R.id.btn_start).setOnClickListener {
                updateButtonState(true)
                viewModel.startTimer()
                startNotificationWorker(habit.id, habit.title, viewModel.getInitialTime())
            }

            findViewById<Button>(R.id.btn_stop).setOnClickListener {
                updateButtonState(false)
                viewModel.resetTimer()
                cancelNotificationWorker()
            }
        }
    }


    private fun cancelNotificationWorker() {
        workManager.cancelAllWorkByTag(NOTIF_UNIQUE_WORK)
    }

    private fun startNotificationWorker(habitId: Int, habitTitle: String, duration: Long?) {
        val data = Data.Builder()
            .putInt(HABIT_ID, habitId)
            .putString(HABIT_TITLE, habitTitle)
            .build()

        if (duration != null) {
            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                .setInputData(data)
                .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                .addTag(NOTIF_UNIQUE_WORK)
                .build()

            workManager.enqueue(oneTimeWorkRequest)
        }
    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }
}