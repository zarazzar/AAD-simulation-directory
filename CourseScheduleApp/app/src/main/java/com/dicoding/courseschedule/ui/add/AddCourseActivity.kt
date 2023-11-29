package com.dicoding.courseschedule.ui.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.util.TimePickerFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddCourseActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {

    private lateinit var viewModel: AddCourseViewModel
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        supportActionBar?.title = getString(R.string.add_course)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory = AddViewModelFactory.createFactory(this)
        viewModel = ViewModelProvider(this, factory)[AddCourseViewModel::class.java]
        viewModel.saved.observe(this) {
            if (it.getContentIfNotHandled() == true) {
                onBackPressed()
            } else {
                Toast.makeText(this, getString(R.string.input_empty_message), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private fun addCourse() {
        val courseLayout = findViewById<TextInputLayout>(R.id.tl_add_course)
        val course = findViewById<TextInputEditText>(R.id.add_ed_course).text.toString().trim()
        val startTime = findViewById<TextView>(R.id.tv_startTimeDisplay).text.toString().trim()
        val endTime = findViewById<TextView>(R.id.tv_endTimeDisplay).text.toString().trim()

        if (course.isNotEmpty() && startTime.isNotEmpty() && endTime.isNotEmpty()) {
            val day = findViewById<Spinner>(R.id.sp_day).selectedItemPosition
            val lecture = findViewById<TextInputEditText>(R.id.add_ed_lecturer).text.toString().trim()
            val note = findViewById<TextInputEditText>(R.id.add_ed_note).text.toString().trim()

            viewModel.insertCourse(course, day, startTime, endTime, lecture, note)
            finish()
        } else {
            courseLayout.error = getString(R.string.input_empty_message)
        }
    }

    fun startTimePicker(view: View) {
        TimePickerFragment().show(supportFragmentManager, START_TIME)
        this.view = view
    }

    fun endTimePicker(view: View) {
        TimePickerFragment().show(supportFragmentManager, END_TIME)
        this.view = view
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_insert -> {
                addCourse()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        when (tag) {
            START_TIME -> findViewById<TextView>(R.id.tv_startTimeDisplay).text =
                timeFormat.format(calendar.time)

            END_TIME -> findViewById<TextView>(R.id.tv_endTimeDisplay).text =
                timeFormat.format(calendar.time)
        }
    }

    companion object {
        private const val START_TIME = "startTimePicker"
        private const val END_TIME = "endTimePicker"
    }

}