package com.service.appdev.coursedetails

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView

class HomeController : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration;
    private lateinit var navView: NavigationView
    override fun onCreate( savedInstanceState: Bundle? ){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout);

        //First step:
        //get the host view
        //Second Step:
        //get the navController
        //Third:
        //setUp appBarConfiguration
        //Fourth:
        //setUpWithNavController
        //val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment);

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment;
        val navController = navHostFragment.navController;


        //Next, connect the DrawerLayout to your navigation graph by passing it to AppBarConfiguration,
        // as shown in the following example:
        appBarConfiguration = AppBarConfiguration(
            navController.graph, drawerLayout,
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )

        val toolbar =  findViewById<Toolbar>(R.id.toolbar);
        toolbar.setupWithNavController(navController, appBarConfiguration)


        navView =  findViewById<NavigationView>(R.id.nav_view)
        navView.setupWithNavController(navController)
        // Inflate the header view
        val headerView = navView.getHeaderView(0)
        val userTextView = headerView.findViewById<TextView>(R.id.username)

        val userSp = getSavedLoginDetails();
        val splitted = userSp?.split(",")
        val username = splitted?.get(1);
        userTextView.text = username;
        // Handle item selection in the NavigationView (drawer)
//        navView.setNavigationItemSelectedListener { menuItem->
//            when(menuItem.itemId){
//                R.id.about_us ->{
//                    navController.navigate(R.id.about_us)
//                }
//                R.id.contact_us -> {
//                    navController.navigate(R.id.contact_us)
//                }
//                R.id.application_fillup -> {
//                    navController.navigate(R.id.application_fillup )
//                }
//                R.id.course_details ->{
//                    navController.navigate(R.id.course_details)
//                }
//            }
//            // Close the drawer after selection
//            drawerLayout.closeDrawers()
//            true
//        }

        navView.setNavigationItemSelectedListener { menuItem ->
            val handled = menuItem.onNavDestinationSelected(navController)
            Log.d("Inside Nav View", " " + handled)
            if (handled) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            handled
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.drawer_menu, menu)
//        return true;
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun getSavedLoginDetails() : String? {
        val sharedPref: SharedPreferences = getSharedPreferences(getString(R.string.save_login_details),MODE_PRIVATE)
        val loginDetails = sharedPref.getString(getString(R.string.save_login_details), "defaultValue")
        return loginDetails;
    }

}