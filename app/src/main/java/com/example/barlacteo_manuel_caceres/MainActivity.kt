package com.example.barlacteo_manuel_caceres

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.barlacteo_manuel_caceres.ui.nav.AppNav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            com.example.barlacteo_manuel_caceres.ui.theme.BarLacteo_Manuel_CaceresTheme {
                AppNav()
            }
        }
    }
}
