package com.service.appdev.coursedetails.fragments_and_activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.service.appdev.coursedetails.R

class BeforeLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.before_login)

        val studentBlock = findViewById<LinearLayout>(R.id.forStudent);
        val adminBlock = findViewById<LinearLayout>(R.id.forAdmin);
        val instituteBlock =  findViewById<LinearLayout>(R.id.forInstitute);


        studentBlock.setOnClickListener(View.OnClickListener {
         val studentIntent = Intent(this@BeforeLogin, LoginActivity::class.java);
          studentIntent.putExtra("accountType", "Student")
         startActivity(studentIntent)
        })

        adminBlock.setOnClickListener(View.OnClickListener {
            val adminIntent = Intent(this@BeforeLogin, LoginActivity::class.java);
            adminIntent.putExtra("accountType", "Admin")
            startActivity(adminIntent)
        })

        instituteBlock.setOnClickListener(View.OnClickListener {
            val instituteIntent = Intent(this@BeforeLogin, LoginActivity::class.java);
            instituteIntent.putExtra("accountType", "Institute")
            startActivity(instituteIntent)
        })
    }
}