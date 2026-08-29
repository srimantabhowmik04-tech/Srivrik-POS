package com.atoz.pos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.atoz.pos.ui.PosViewModel
import com.atoz.pos.ui.screens.MainScreen
import com.atoz.pos.ui.theme.SriVikPOSTheme

class MainActivity : ComponentActivity() {
    private val viewModel: PosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SriVikPOSTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
