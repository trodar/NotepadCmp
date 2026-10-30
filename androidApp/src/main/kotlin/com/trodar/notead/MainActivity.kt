package com.trodar.notead

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.trodar.notead.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {

    private var isDataReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            !isDataReady
        }

        try {
            initKoin {
                androidContext(this@MainActivity)
            }
        } catch (_: Exception) {
            // Already started
        }

        setContent {
            App(
                onDataReady = {
                    isDataReady = true
                }
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
