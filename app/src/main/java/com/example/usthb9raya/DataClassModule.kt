package com.example.usthb9raya

data class DataClassModule(

    val module_name : String ,
    val course_name : String ,
    val td_name : String ,
    val tp_name : String ,
    val submodules: List<DataClassSousModule>

)
