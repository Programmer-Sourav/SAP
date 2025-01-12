package com.service.appdev.coursedetails.fragments_and_activities

import android.R
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.service.appdev.coursedetails.HomeController
import com.service.appdev.coursedetails.models.ApiServiceBuilder
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import com.service.appdev.coursedetails.repository.LoginManagementRepository
import com.service.appdev.coursedetails.viewmodel.AdminManagementViewModel
import com.service.appdev.coursedetails.viewmodel.LoginViewModel
import com.service.appdev.coursedetails.viewmodelfactory.AdminManagementViewModelFactory
import com.service.appdev.coursedetails.viewmodelfactory.LoginViewModelFactory


class LoginActivity : AppCompatActivity() {

    private lateinit var userEmail : EditText
    private lateinit var userPassword : EditText
    private lateinit var loginBtn : Button
    private lateinit var viewModel: LoginViewModel;
    private lateinit var registerHere : TextView;

    //private val intent = getIntent();

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(com.service.appdev.coursedetails.R.layout.logout_activity)

        val apiService = ApiServiceBuilder.createApiService(applicationContext)
        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(LoginManagementRepository(apiService))
        )[LoginViewModel::class.java]


        userEmail = findViewById(com.service.appdev.coursedetails.R.id.useremail);
        userPassword = findViewById(com.service.appdev.coursedetails.R.id.userpassword);
        registerHere = findViewById(com.service.appdev.coursedetails.R.id.registerHere);

        registerHere.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoginActivity, RegisterUser::class.java);
            intent.putExtra("accountType", "Student");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        })

        loginBtn = findViewById(com.service.appdev.coursedetails.R.id.loginBtn);

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
                    val authToken = state.message.authToken;
                    val userId = state.message.userId;
                    val username = state.message.username;
                    if(accountType.equals("Student")) {
                        val intent = Intent(this@LoginActivity, HomeController::class.java);
                        saveLogin(userId, username, authToken, "Student") //Get it from the Response
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else if(accountType.equals("Admin")) {
                        val intent = Intent(this@LoginActivity, AdminPanel::class.java);
                        saveLogin(userId, username, authToken, "Admin")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else {
                        val intent = Intent(this@LoginActivity, AdminPanel::class.java);
                        saveLogin(userId, username, authToken, "Institute")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
                is LoginViewModel.LoginState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    private fun saveLogin(userId : String, nameOfUser : String, authToken: String, userType: String){
        val loginSp = "$userId,$nameOfUser,$authToken,$userType";
        val sharedPref: SharedPreferences = getSharedPreferences(getString(com.service.appdev.coursedetails.R.string.save_login_details),MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear(); //clear previously stored values
        editor.putString(getString(com.service.appdev.coursedetails.R.string.save_login_details), loginSp)
        editor.apply();
    }
}