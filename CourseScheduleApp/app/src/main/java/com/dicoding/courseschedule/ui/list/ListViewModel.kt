package com.dicoding.courseschedule.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.util.Event
import com.dicoding.courseschedule.util.SortType

class ListViewModel(private val repository: DataRepository) : ViewModel() {

    private val _sortParams = MutableLiveData<SortType>()

    init {
        _sortParams.value = SortType.TIME
    }

    val courses = _sortParams.switchMap {
        repository.getAllCourse(it)
    }

    fun sort(newValue: SortType) {
        _sortParams.value = newValue
    }

    fun delete(course: Course) {
        repository.delete(course)
    }

    private val _saved = MutableLiveData<Event<Boolean>>()

    fun insertCourse(
        courseName: String,
        day: Int,
        startTime: String,
        endTime: String,
        lecturer: String,
        note: String
    ) {
        if (courseName.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            _saved.value = Event(false)
            return
        }

        val course = Course(
            courseName = courseName,
            day = day + 1,
            startTime = startTime,
            endTime = endTime,
            lecturer = lecturer,
            note = note
        )
        repository.insert(course)
        _saved.value = Event(true)
    }
}