package com.garbhyatra.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.garbhyatra.app.ui.GarbhyatraApp
import com.garbhyatra.app.ui.theme.GarbhyatraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val container = (application as GarbhyatraApplication).container
        setContent {
            GarbhyatraTheme {
                GarbhyatraApp(container = container)
            }
        }
    }
}
