package com.mistersam.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mistersam.app.ui.AppViewModel
import com.mistersam.app.ui.SamApp
import com.mistersam.app.ui.SamTheme

class MainActivity : ComponentActivity() {
    private val vm: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SamTheme {
                SamApp(vm)
            }
        }
    }
}
