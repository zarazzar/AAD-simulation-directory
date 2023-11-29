package com.dicoding.todoapp.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.ui.list.TaskActivity
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID
import com.google.android.material.textfield.TextInputEditText

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var detailViewModel: DetailTaskViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        //TODO 11 : Show detail task and implement delete action
        val detailEdTitle = findViewById<TextInputEditText>(R.id.detail_ed_title)
        val detailEdDesc = findViewById<TextInputEditText>(R.id.detail_ed_description)
        val detailEdDueDate = findViewById<TextInputEditText>(R.id.detail_ed_due_date)
        val btnDelete = findViewById<Button>(R.id.btn_delete_task)

        val taskId = intent.getIntExtra(TASK_ID,0)
        val factory = ViewModelFactory.getInstance(this)

        detailViewModel = ViewModelProvider(this, factory).get(DetailTaskViewModel::class.java)
        detailViewModel.setTaskId(taskId)

        detailViewModel.task.observe(this) {
            detailEdTitle.setText(it.title)
            detailEdDesc.setText(it.description)
            detailEdDueDate.setText(DateConverter.convertMillisToString(it.dueDateMillis))
            btnDelete.setOnClickListener {
                detailViewModel.task.removeObservers(this)
                detailViewModel.deleteTask()

                val intentToMain = Intent(this@DetailTaskActivity, TaskActivity::class.java)
                startActivity(intentToMain)
            }

        }





    }
}