package com.example.geminiapi2.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.geminiapi2.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenAlt() {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var newsletterChecked by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black  // Màu xanh đậm
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Logo image
            Image(
                painter = painterResource(id = R.drawable.chatboticon),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(160.dp)
                    .padding(bottom = 48.dp)
            )

            // Title
            Text(
                text = "Đăng nhập ngay",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Hãy tiếp tục hành trình của bạn bằng\ncách điền vào mẫu dưới đây.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor =Color.White.copy(alpha = 0.2f),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                singleLine = true
            )

            // Login button
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1E3838)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Đăng nhập",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Forgot password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Quên mật khẩu? ",
                    color = Color.White
                )
                Text(
                    text = "Đặt lại ở đây",
                    color = Color(0xFF4DC6B5),
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }

            Text(
                text = "HOẶC",
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Social login buttons
//            Button(
//                onClick = { /* TODO */ },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp)
//                    .height(56.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFF2A4544)
//                ),
//                shape = RoundedCornerShape(28.dp)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_google), // Thêm icon Google
//                    contentDescription = "Google icon",
//                    modifier = Modifier.size(24.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Tiếp tục với Google")
//            }



            // Sign up link
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Bạn chưa có tài khoản? ",
                    color = Color.White
                )
                Text(
                    text = "Đăng ký tại đây",
                    color = Color(0xFF4DC6B5),
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }
        }
    }
}