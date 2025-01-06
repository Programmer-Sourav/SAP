package com.service.appdev.coursedetails//import android.app.Application
//import android.content.Context
//import android.content.SharedPreferences
//import com.service.appdev.coursedetails.R
//
//class MyApplication : Application() {
//    companion object {
//        private lateinit var instance: MyApplication
//
//        fun getAppContext(): Context {
//            return instance.applicationContext
//        }
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        instance = this
//    }
//
//    // Method to get saved login details
//    fun getSavedLoginDetails(): String? {
//        val appContext = getAppContext()
//        val sharedPref: SharedPreferences = appContext.getSharedPreferences(
//            appContext.getString(R.string.save_login_details),
//            Context.MODE_PRIVATE
//        )
//        return sharedPref.getString(appContext.getString(R.string.save_login_details), null)
//    }
//}
