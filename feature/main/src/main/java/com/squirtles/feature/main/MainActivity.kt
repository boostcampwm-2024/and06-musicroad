package com.squirtles.feature.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.squirtles.core.common.ui.MusicRoadPermissions.CORE_PERMISSIONS
import com.squirtles.core.common.ui.theme.MusicRoadTheme
import com.squirtles.feature.main.navigation.MainNavHost
import com.squirtles.feature.main.navigation.MainNavigator
import com.squirtles.feature.main.navigation.rememberMainNavigator
import com.squirtles.feature.permission.PermissionScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setKeepOnScreenCondition(splashScreen)
        enableEdgeToEdge()

        lifecycleScope.launch {
            mainViewModel.isPermissionGranted.collect { isGranted ->
                if (isGranted) {
                    setMusicRoadContent()
                } else {
                    showPermissionScreen()
                }
            }
        }

        mainViewModel.setPermissionGranted(checkSelfPermission(CORE_PERMISSIONS))
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.setPermissionGranted(checkSelfPermission(CORE_PERMISSIONS))
    }

    private fun setKeepOnScreenCondition(splashScreen: SplashScreen) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.loadingState.collect { state ->
                    when (state) {
                        is LoadingState.Loading -> {
                            splashScreen.setKeepOnScreenCondition { true }
                        }

                        is LoadingState.Success -> {
                            Log.d("MainActivity", "Success: ${state.uid}")
                            splashScreen.setKeepOnScreenCondition { false }
                            cancel()
                        }

                        is LoadingState.UserNotFoundError -> {
                            FirebaseAuth.getInstance().signOut()
                            splashScreen.setKeepOnScreenCondition { false }
                            cancel()
                        }

                        is LoadingState.NetworkError -> {
                            showToast(getString(R.string.main_network_error_message))
                            finish()
                        }

                        is LoadingState.CreatedUserError -> {
                            showToast(getString(R.string.main_create_user_fail_message))
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun checkSelfPermission(permissions: List<String>): Boolean {
        return permissions.all { permission ->
            PermissionChecker.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_GRANTED
        }
    }

    private fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setMusicRoadContent() {
        setContent {
            val navigator: MainNavigator = rememberMainNavigator()

            MusicRoadTheme {
                MainNavHost(
                    navigator = navigator,
                    finishActivity = { this.finish() },
                )
            }
        }
    }

    private fun showPermissionScreen() {
        setContent {
            MusicRoadTheme {
                PermissionScreen(
                    onBackClick = { this.finish() },
                    onNextClick = { setMusicRoadContent() }
                )
            }
        }
    }
}
