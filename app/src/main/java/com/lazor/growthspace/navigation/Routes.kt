package com.lazor.growthspace.navigation


object Routes{
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val MAIN_APP = "main_app"
    const val HOME = "home"
    const val COACH_PROFILE = "coach_profile/{id}"
    const val BOOKING_DATE = "booking_date/{id}"
    const val BOOKING_TIME = "booking_time/{id}/{date}"
    const val BOOKING_CONFIRMATION = "booking_confirmation/{id}/{date}/{time}"
    const val BOOKING_STATUS = "booking_status/{id}/{date}/{time}"
}