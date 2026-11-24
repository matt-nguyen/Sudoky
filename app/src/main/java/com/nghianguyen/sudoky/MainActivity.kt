package com.nghianguyen.sudoky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nghianguyen.feature.home.nav.Home
import com.nghianguyen.ui.theme.SudokyTheme

@ExperimentalCamera2Interop
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent { SudokyTheme { NavigationRoot(Home) } }
    }
}

/**
 * Displays a greeting for [name].
 *
 * @param name The name in the greeting.
 * @param modifier Modifier applied to the greeting text.
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

/** Previews the greeting with the app theme. */
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SudokyTheme { Greeting("Android") }
}
