package com.app.studentmanagement.data

import com.app.studentmanagement.data.models.Student

interface StudentAdapterListener {
    fun onEditStudent(student: Student, index:Int)
}
