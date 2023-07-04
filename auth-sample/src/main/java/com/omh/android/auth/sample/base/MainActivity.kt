package com.omh.android.auth.sample.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.sample.R
import com.omh.android.auth.sample.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var authClient: OmhAuthClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(
            /* id = */ R.id.nav_host_fragment
        ) as NavHostFragment
        navController = navHostFragment.navController
        setupGraph()
        setupToolbar()
    }

    private fun setupGraph() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        val startDestId = selectStartDestination()
        navGraph.setStartDestination(startDestId)
        navController.graph = navGraph
    }

    private fun setupToolbar() {
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.logged_in_fragment, R.id.login_fragment)
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun selectStartDestination(): Int {
        return if (authClient.getUser() == null) {
            R.id.login_fragment
        } else {
            R.id.logged_in_fragment
        }
    }
}
