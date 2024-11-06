package com.squirtles.musicroad.main

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import androidx.core.content.PermissionChecker
import androidx.navigation.compose.rememberNavController
import com.squirtles.musicroad.R
import com.squirtles.musicroad.ui.theme.MusicRoadTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionsGranted = permissions.all { it.value }
        if (allPermissionsGranted) {
            startMainScreen()
        } else {
            Toast.makeText(this, getString(R.string.main_permission_deny_message), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSelfPermission()) {
            startMainScreen()
        } else {
            permissionLauncher.launch(PERMISSIONS)
        }
    }

    private fun startMainScreen() {
        enableEdgeToEdge()
        setContent {
            MusicRoadTheme {
                val navController = rememberNavController()
                val navigationActions = remember(navController) {
                    MainNavigationActions(navController)
                }
                MainNavGraph(
                    navController = navController,
                    navigationActions = navigationActions
                )
            }
        }
    }

    private fun checkSelfPermission(): Boolean {
        return PermissionChecker.checkSelfPermission(this, PERMISSIONS[0]) ==
                PermissionChecker.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(this, PERMISSIONS[1]) ==
                PermissionChecker.PERMISSION_GRANTED
    }

    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}