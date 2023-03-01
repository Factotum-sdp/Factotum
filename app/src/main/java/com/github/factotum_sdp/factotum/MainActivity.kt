package com.github.factotum_sdp.factotum

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.factotum_sdp.factotum.ui.theme.FactotumTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FactotumTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AuthBox(context = this) // fetch the context
                }
            }
        }
    }

    @Composable
    fun AuthBox(context: Context) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            var userName by remember { mutableStateOf(TextFieldValue("")) }

            Column {
                TextField(
                    value = userName,
                    onValueChange = {userName = it},
                    Modifier.height(50.dp),
                    label = { Text(getString(R.string.userNameTextFieldLabel)) }
                )
                Button(
                    onClick = {
                        startActivity(
                            Intent(context, GreetingActivity::class.java)
                                .putExtra(getString(R.string.userNameIntentId), userName.text)
                        )
                    }
                ) {
                    Text(text = getString(R.string.validateButton))
                }
            }
        }
    }
}

