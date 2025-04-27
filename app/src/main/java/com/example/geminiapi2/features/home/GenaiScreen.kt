package com.example.geminiapi2.features.home

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiapi2.GeminiTextViewModel
import com.example.geminiapi2.R
import com.example.geminiapi2.UiState
import kotlinx.coroutines.launch

@Composable
fun GenaiScreen(
    geminiTextViewModel: GeminiTextViewModel = hiltViewModel()

) {
    val uiState by geminiTextViewModel.uiState.collectAsState()
    var prompt by rememberSaveable { mutableStateOf("prompt") }
    var result by rememberSaveable { mutableStateOf("result here") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column {
        Row(
            modifier = Modifier.padding(all = 16.dp)
        ) {
            TextField(
                value = prompt,
                label = { Text("Input here") },
                onValueChange = { prompt = it },
                modifier = Modifier
                    .weight(0.8f)
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically)
            )
        }
//        Button(
//        onClick = {
//            geminiTextViewModel.viewModelScope.launch {
//                geminiTextViewModel.sendPrompt(prompt)
//            }
//        },
//        enabled = prompt.isNotEmpty(),
//      ) {
//        Text(text = stringResource(R.string.action_go))
//      }
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Chọn ảnh từ thư viện")
        }
        Spacer(
            modifier = Modifier
                .padding(16.dp)
        )
        Button(
            onClick = {
                geminiTextViewModel.viewModelScope.launch {
                    geminiTextViewModel.sendImage(imageUri)
                }
            },
            enabled = prompt.isNotEmpty(),
        ) {
            Text(text = stringResource(R.string.action_go))
        }
        if (uiState is UiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            var textColor = MaterialTheme.colorScheme.onSurface
            if (uiState is UiState.Error) {
                textColor = MaterialTheme.colorScheme.error
                result = (uiState as UiState.Error).errorMessage
            } else if (uiState is UiState.Success) {
                textColor = MaterialTheme.colorScheme.onSurface
                result = (uiState as UiState.Success).outputText
            }
            val scrollState = rememberScrollState()
            Text(
                text = result,
                textAlign = TextAlign.Start,
                color = textColor,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            )
        }
    }


}