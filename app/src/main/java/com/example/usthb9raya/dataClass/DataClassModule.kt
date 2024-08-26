package com.example.usthb9raya.dataClass

data class DataClassModule(

    val module_name : String ,
    val course_link : String ,
    val td_link : String ,
    val tp_link : String ,
    val submodules: List<DataClassSousModule>

)
