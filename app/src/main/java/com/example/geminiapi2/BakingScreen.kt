//package com.example.geminiapi2
////
//import android.graphics.ImageDecoder
//import android.net.Uri
//import android.os.Build
//import android.provider.MediaStore
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Button
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import coil.compose.rememberAsyncImagePainter
//
//val images = arrayOf(
//  // Image generated using Gemini from the prompt "cupcake image"
//  R.drawable.baked_goods_1,
//  // Image generated using Gemini from the prompt "cookies images"
//  R.drawable.baked_goods_2,
//  // Image generated using Gemini from the prompt "cake images"
//  R.drawable.baked_goods_3,
//)
//val imageDescriptions = arrayOf(
//  R.string.image1_description,
//  R.string.image2_description,
//  R.string.image3_description,
//)
//
//@Composable
//fun BakingScreen(
////  bakingViewModel: BakingViewModel = viewModel(),
//
//) {
//  val selectedImage = remember { mutableIntStateOf(0) }
//  val placeholderPrompt = stringResource(R.string.prompt_placeholder)
//  val placeholderResult = stringResource(R.string.results_placeholder)
//  var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
//  var result by rememberSaveable { mutableStateOf(placeholderResult) }
//  val uiState by bakingViewModel.uiState.collectAsState()
//  val context = LocalContext.current
//
//
//  var imageUri by remember { mutableStateOf<Uri?>(null) }
//  val launcher = rememberLauncherForActivityResult(
//    contract = ActivityResultContracts.GetContent()
//  ) { uri: Uri? ->
//    imageUri = uri
//  }
//  Column(
//    modifier = Modifier.fillMaxSize()
//  ) {
//    Text(
//      text = stringResource(R.string.baking_title),
//      style = MaterialTheme.typography.titleLarge,
//      modifier = Modifier.padding(16.dp)
//    )
//
//    imageUri?.let { uri ->
//      Image(
//        painter = rememberAsyncImagePainter(uri),
//        contentDescription = "Selected image",
//        modifier = Modifier
//          .padding(16.dp)
//          .size(200.dp)
//          .clip(RoundedCornerShape(8.dp))
//      )
//    }
//    Button(
//      onClick = { launcher.launch("image/*") },
//      modifier = Modifier
//        .padding(16.dp)
//        .align(Alignment.CenterHorizontally)
//    ) {
//      Text("Chọn ảnh từ thư viện")
//    }
//    Row(
//      modifier = Modifier.padding(all = 16.dp)
//    ) {
//      TextField(
//        value = prompt,
//        label = { Text(stringResource(R.string.label_prompt)) },
//        onValueChange = { prompt = it },
//        modifier = Modifier
//          .weight(0.8f)
//          .padding(end = 16.dp)
//          .align(Alignment.CenterVertically)
//      )
//
////      Button(
////        onClick = {
////          val bitmap = BitmapFactory.decodeResource(
////            context.resources,
////            images[selectedImage.intValue]
////          )
////          bakingViewModel.sendPrompt(bitmap, prompt)
////        },
////        enabled = prompt.isNotEmpty(),
////        modifier = Modifier
////          .align(Alignment.CenterVertically)
////      ) {
////        Text(text = stringResource(R.string.action_go))
////      }
//
//
//    }
//    Button(
//      onClick = {
//        imageUri?.let { uri ->
//          // Convert Uri to Bitmap
//          val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            val source = ImageDecoder.createSource(context.contentResolver, uri)
//            ImageDecoder.decodeBitmap(source)
//          } else {
//            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
//          }
//          bakingViewModel.sendPrompt(bitmap)
//        }
//      },
//      enabled = imageUri != null,
//      modifier = Modifier
//        .padding(16.dp)
//        .align(Alignment.CenterHorizontally)
//    ) {
//      Text("Phân tích ảnh")
//    }
//    if (uiState is UiState.Loading) {
//      CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
//    } else {
//      var textColor = MaterialTheme.colorScheme.onSurface
//      if (uiState is UiState.Error) {
//        textColor = MaterialTheme.colorScheme.error
//        result = (uiState as UiState.Error).errorMessage
//      } else if (uiState is UiState.Success) {
//        textColor = MaterialTheme.colorScheme.onSurface
//        result = (uiState as UiState.Success).outputText
//      }
//      val scrollState = rememberScrollState()
//      Text(
//        text = result,
//        textAlign = TextAlign.Start,
//        color = textColor,
//        modifier = Modifier
//          .align(Alignment.CenterHorizontally)
//          .padding(16.dp)
//          .fillMaxSize()
//          .verticalScroll(scrollState)
//      )
//    }
//  }
//}