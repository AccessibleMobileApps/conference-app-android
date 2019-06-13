package com.robinkanatzar.conference

import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.robinkanatzar.conference.util.Constants
import com.robinkanatzar.conference.util.PreferenceHelper
import com.robinkanatzar.conference.util.PreferenceHelper.get
import com.robinkanatzar.conference.vo.Conference
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var appBarConfiguration : AppBarConfiguration
    var sideNavView: NavigationView? = null

    lateinit var firestore: FirebaseFirestore
    private var conferenceId: String? = null
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        firestore = FirebaseFirestore.getInstance()
        prefs = PreferenceHelper.defaultPrefs(this)
        conferenceId = prefs[Constants.SP_CONFERENCE_ID]

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val host: NavHostFragment = supportFragmentManager
                .findFragmentById(R.id.container) as NavHostFragment? ?: return

        val navController = host.navController

        val drawerLayout : DrawerLayout? = findViewById(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(
                setOf(R.id.schedule_dest, R.id.speakers_dest, R.id.news_dest, R.id.partners_dest,
                        R.id.contact_dest, R.id.about_dest, R.id.choose_conference_dest, R.id.settings_dest),
                drawerLayout)

        setupActionBar(navController, appBarConfiguration)

        setupNavigationMenu(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destination.id)
            }
        }
    }

    private fun setupActionBar(navController: NavController,
                               appBarConfig : AppBarConfiguration) {
        setupActionBarWithNavController(navController, appBarConfig)
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.container).navigateUp(appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.container))
                || super.onOptionsItemSelected(item)
    }

    private fun setupNavigationMenu(navController: NavController) {
        sideNavView = findViewById<NavigationView>(R.id.navigationView)
        sideNavView?.setupWithNavController(navController)

        setUpNavigationMenuHeader(conferenceId.toString())
    }

    fun setUpNavigationMenuHeader(conferenceId: String) {
        val headerView = sideNavView?.getHeaderView(0)
        val headerText = headerView?.findViewById<TextView>(R.id.tv_menu_header)
        val headerImage = headerView?.findViewById<ImageView>(R.id.iv_menu_header)

        val conferenceDoc = firestore.collection(Constants.FB_CONFERENCE).document(conferenceId)
        conferenceDoc.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val doc = document.toObject(Conference::class.java)
                        headerText?.text = doc?.name ?: ""
                        if (doc?.logo != null && headerImage != null) {
                            headerImage?.visibility = View.VISIBLE
                            Glide.with(this)
                                    .load(doc?.logo)
                                    .into(headerImage)
                        } else {
                            headerImage?.visibility = View.GONE
                        }

                    } else {
                        Snackbar.make(this.findViewById(R.id.content), getString(R.string.error), Snackbar.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Snackbar.make(this.findViewById(R.id.content), getString(R.string.error), Snackbar.LENGTH_LONG).show()
                }
    }
}
