package com.example.w03_02  // ← 꼭 build.gradle namespace와 동일하게

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.w03_02.ui.theme.MyAppTheme   // ✅ 네 테마명 반영

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFFFEBEE) // 옅은 분홍색 (Material Pink 50)
                ) {
                    PasswordConfirmScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordConfirmScreen() {
    val desc = stringResource(id = R.string.main_desc)
    val hint = stringResource(id = R.string.password_txt)
    val confirmLabel = stringResource(id = R.string.confirm_button)

    var password by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = desc,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "kkang104@gmail.com", // 예시 이메일
            color = Color(0xFF9E9E9E),
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        )

        Spacer(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFD4D4D3))
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("비밀번호") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        Text(
            text = hint,
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        )

        Button(
            onClick = {
                focusManager.clearFocus()
                // TODO: 비밀번호 검증 로직 추가 가능
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(confirmLabel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordConfirmPreview() {
    MyAppTheme {
        PasswordConfirmScreen()
    }
}
