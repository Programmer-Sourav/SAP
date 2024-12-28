package com.service.appdev.coursedetails.fragments_and_activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.service.appdev.coursedetails.HomeController
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.models.ApiServiceBuilder
import com.service.appdev.coursedetails.repository.LoginManagementRepository
import com.service.appdev.coursedetails.viewmodel.LoginViewModel
import com.service.appdev.coursedetails.viewmodelfactory.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var userEmail : EditText
    private lateinit var userPassword : EditText
    private lateinit var loginBtn : Button

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(LoginManagementRepository(ApiServiceBuilder.apiService))
    }

    //private val intent = getIntent();

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logout_activity)

        userEmail = findViewById(R.id.useremail);
        userPassword = findViewById(R.id.userpassword);

        loginBtn = findViewById(R.id.loginBtn);

        val accountType = intent.getStringExtra("accountType");

        loginBtn.setOnClickListener(View.OnClickListener {
            if(accountType.equals("Student"))
                viewModel.userLogin(userEmail.text.toString(), userPassword.text.toString())
            else if(accountType.equals("Admin"))
                viewModel.adminLogin(userEmail.text.toString(), userPassword.text.toString())
            else
                viewModel.instituteLogin(userEmail.text.toString(), userPassword.text.toString())
        })

        viewModel.loginLiveData.observe(this, Observer { state ->
            when (state) {
                is LoginViewModel.LoginState.Success -> {
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    if(accountType.equals("Student")) {
                        val intent = Intent(this@LoginActivity, HomeController::class.java);
                        startActivity(intent);
                    }
                    else if(accountType.equals("Admin")) {
                        val intent = Intent(this@LoginActivity, AdminPanel::class.java);
                        startActivity(intent);
                    }
                    else {
                        val intent = Intent(this@LoginActivity, AdminPanel::class.java);
                        startActivity(intent);
                    }
                }
                is LoginViewModel.LoginState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

    }
}