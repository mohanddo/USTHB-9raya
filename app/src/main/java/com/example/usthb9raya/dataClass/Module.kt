package com.example.usthb9raya.dataClass

data class Module(

    val module_name : String ,
    val course_link : String ,
    val td_link : String ,
    val tp_link : String ,
    val exams_link : String ,
    val others_link : String ,
    val submodules: List<SousModule>

)
