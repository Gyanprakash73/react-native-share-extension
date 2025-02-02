package com.awesomeproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

class MainActivity : ReactActivity() {

    override fun getMainComponentName(): String = "AwesomeProject"

    override fun createReactActivityDelegate(): ReactActivityDelegate =
        DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Delay intent handling to ensure React Native is initialized
        Handler(Looper.getMainLooper()).postDelayed({
            handleIntent(intent)
        }, 1000) // 1-second delay
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            if (Intent.ACTION_SEND == it.action && it.type?.startsWith("image/") == true) {
                // Delay execution to allow React Native context initialization
                Handler(Looper.getMainLooper()).postDelayed({
                    SharedImageModule.handleIntent(it)

                    // Clear the intent to prevent duplicate processing
                    setIntent(Intent())
                }, 1000) // 1-second delay
            }
        }
    }
}
