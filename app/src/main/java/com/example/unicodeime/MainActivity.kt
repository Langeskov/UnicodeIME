package com.example.unicodeime

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.unicodeime.ui.theme.UnicodeIMETheme

class MainActivity : ComponentActivity() {
    private var isImeEnabled by mutableStateOf(false) // Manage state here or in a ViewModel
    private var isImeSelected by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnicodeIMETheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ImeSetupScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        // Refresh IME status when the activity resumes
        isImeEnabled = isThisImeEnabled(this)
        isImeSelected = isThisImeSelected(this)
    }
}

@Composable
fun ImeSetupScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isImeEnabled by remember { mutableStateOf(isThisImeEnabled(context)) }
    var isImeSelected by remember { mutableStateOf(isThisImeSelected(context)) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Unicode 输入法设置",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            if (isImeEnabled) "1. 设置输入法" else "1. 设置输入法",
            color = if (isImeEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        if (!isImeEnabled) {
            Button(onClick = {
                val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                context.startActivity(intent)
            }) {
                Text("打开输入法设置启用")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            if (isImeSelected) "2. 选择当前输入法为UnicodeIME" else "2. 选择当前输入法为UnicodeIME",
            color = if (isImeSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        if (isImeEnabled && !isImeSelected) {
            Button(onClick = {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }) {
                Text("选择输入法")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isImeEnabled && isImeSelected) {
            Text("请按照上述步骤启用并选择此输入法。")
        } else {
            Text("请按照上述步骤启用并选择此输入法。")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("现在你可以尝试在任何文本框中输入Unicode码点，例如4F46为中文“但”.")
        Text("APP制作：十二水磷酸二钠")
    }
}

private fun getImeId(context: Context): String {
    return "${context.packageName}/${UnicodeIME::class.java.name}"
}

private fun isThisImeEnabled(context: Context): Boolean {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val enabledImes = imm.enabledInputMethodList
    val thisImeId = getImeId(context)
    return enabledImes.any { it.id == thisImeId }
}

private fun isThisImeSelected(context: Context): Boolean {
    val currentImeId = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.DEFAULT_INPUT_METHOD
    )
    return currentImeId == getImeId(context)
}