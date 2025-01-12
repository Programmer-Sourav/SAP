package com.service.appdev.coursedetails.fragments_and_activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.service.appdev.coursedetails.HomeController
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.models.ApiServiceBuilder
import com.service.appdev.coursedetails.repository.LoginManagementRepository
import com.service.appdev.coursedetails.viewmodel.LoginViewModel
import com.service.appdev.coursedetails.viewmodelfactory.LoginViewModelFactory

class RegisterUser : AppCompatActivity() {

    private lateinit var userEmail : EditText
    private lateinit var userPassword : EditText
    private lateinit var phoneNumber : EditText
    private lateinit var loginBtn : Button
    private lateinit var viewModel: LoginViewModel;


    //private val intent = getIntent();

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        val apiService = ApiServiceBuilder.createApiService(applicationContext)
        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(LoginManagementRepository(apiService))
        )[LoginViewModel::class.java]


        userEmail = findViewById(R.id.useremail);
        userPassword = findViewById(R.id.userpassword);
        phoneNumber = findViewById(R.id.userphone);

        loginBtn = findViewById(R.id.loginBtn);

        val accountType = intent.getStringExtra("accountType");

        loginBtn.setOnClickListener(View.OnClickListener {
            if(accountType.equals("Student"))
                viewModel.userSignUp(userEmail.text.toString(), phoneNumber.text.toString(), userPassword.text.toString())
//            else if(accountType.equals("Admin"))
//                viewModel.adminLogin(userEmail.text.toString(), userPassword.text.toString())
//            else
//                viewModel.instituteLogin(userEmail.text.toString(), userPassword.text.toString())
        })

        viewModel.loginLiveData.observe(this, Observer { state ->
            when (state) {
                is LoginViewModel.LoginState.Success -> {
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    val authToken = state.message.authToken;
                    val userId = state.message.userId;
                    val username = state.message.username;
                    if(accountType.equals("Student")) {
                        val intent = Intent(this@RegisterUser, HomeController::class.java);
                        saveLogin(userId, username, authToken) //Get it from the Response
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
//                    else if(accountType.equals("Admin")) {
//                        val intent = Intent(this@RegisterUser, AdminPanel::class.java);
//                        saveLogin(userId, username, authToken)
//                        startActivity(intent);
//                    }
//                    else {
//                        val intent = Intent(this@RegisterUser, AdminPanel::class.java);
//                        saveLogin(userId, username, authToken)
//                        startActivity(intent);
//                    }
                }
                is LoginViewModel.LoginState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    private fun saveLogin(userId : String, nameOfUser : String, authToken: String){
        val loginSp = "$userId,$nameOfUser,$authToken";
        val sharedPref: SharedPreferences = getSharedPreferences(getString(com.service.appdev.coursedetails.R.string.save_login_details),
            MODE_PRIVATE
        )
        val editor = sharedPref.edit()
        editor.clear(); //clear previously stored values
        editor.putString(getString(com.service.appdev.coursedetails.R.string.save_login_details), loginSp)
        editor.apply();
    }
}