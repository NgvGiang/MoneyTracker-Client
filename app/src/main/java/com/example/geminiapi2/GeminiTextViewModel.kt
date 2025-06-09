package com.example.geminiapi2

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GoogleGenerativeAIException
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.ServerException
import com.google.ai.client.generativeai.type.TextPart
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.defineFunction
import com.google.ai.client.generativeai.type.generationConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


private val parseReceiptTool = defineFunction(
    name = "parse_receipt",
    description = "Parse categories, amount and date from receipt like restaurant or supermarket",
    parameters = listOf(
        Schema.str("date", "Date of the receipt in format YYYY-MM-DD"),
        Schema.str("category", "Category of the receipt (e.g., restaurant, supermarket, etc.)"),
        Schema.double("amount", "Total amount on the receipt"),
        Schema.str("category_type", "Expense or Income")
    ),
    requiredParameters = listOf("date", "category", "amount", "category_type")
)

@HiltViewModel
class GeminiTextViewModel @Inject constructor(
    private val contentResolver: ContentResolver
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private val currentDate: LocalDate = LocalDate.now()
    private val systemInstruction =
        "You are a smart assistant to help users automatically categorize user's transactions based on their receipts \\\n" +
                "                Try to do it exactly. You need to extract time from the receipt to the form of (yyyy-MM-dd) and process the amount     \\\n" +
                "                and return to \"amount\" field. \\\n" +
                "Careful with , and . in the amount, with vietnam dong it maybe always have a 000 number in the end imply for thousand dong    " +
                "                User's Categories includes: [%s] \\\n" +
                "                Today is %s, if time cannot be extracted from the command, return today time. \\\n" +
                "                If the category extracted from the command does not appear in [User's Categories], try to categorize it. \\\n" +
                "                category_type has only two values: Expense, Income.\\"
    private val categories =
        "Food, Transportation, Shopping, Utilities, Entertainment, Healthcare, Others"
    private val generativeModel = GenerativeModel(
//    gemini-1.5-pro-latest, learnlm-1.5-pro-experimental,
        modelName = "gemini-1.5-pro",
        apiKey = BuildConfig.apiKey,
        requestOptions = RequestOptions(apiVersion = "v1beta"),
        tools = listOf(Tool(listOf(parseReceiptTool))),
        generationConfig = generationConfig {
            temperature = 1f
            topK = 40
            topP = 0.4f
            maxOutputTokens = 100
        },
        systemInstruction = Content(
            role = "system",
            parts = listOf(
                TextPart(
                    String.format(
                        systemInstruction,
                        currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        categories
                    ).trimIndent()
                )
            )
        )
    )


    //    suspend fun sendImages(prompt: Bitmap) {
    suspend fun sendImage(imageUri: Uri?) {
        _uiState.value = UiState.Loading
//        val response = generativeModel.generateContent(
//            content {
//                text(prompt)
//            }
//        )
        if (imageUri == null) {
            _uiState.value = UiState.Error("Vui lòng chọn ảnh")
            return
        }

        val bitmapImage = loadBitmapFromUri(contentResolver, imageUri)
        try {
            val response = generativeModel.generateContent(
                content {
                    if (bitmapImage != null) {
                        image(bitmapImage)
                    }
                }
            )
//        response.text?.let { outputContent ->
//            Log.d("New", "Text: $outputContent")
//            _uiState.value = UiState.Success(outputContent)
//        }

            // Xử lý function calling response
            if (response.functionCalls.isNotEmpty()) {
                val functionCall = response.functionCalls[0]
                if (functionCall.name == "parse_receipt") {
                    val args = functionCall.args
                    val date = args["date"] ?: ""
                    val category = args["category"] ?: ""
                    val amount = args["amount"]
                    val categoryType = args["category_type"] ?: ""
                    Log.d("FunctionCall", "Received function call: ${functionCall.args}")
                    val resultText = """
                            Ngày: $date
                            Danh mục: $category
                            Số tiền: $amount
                            Loại: $categoryType
                        """.trimIndent()

                    Log.d("FunctionCall", "Received function call: $resultText")
                    _uiState.value = UiState.Success(resultText)
                } else {
                    _uiState.value = UiState.Error("Không nhận được kết quả từ function calling")
                }
            } else {
                // Fallback để lấy text response nếu không có function call
                response.text?.let { outputContent ->
                    Log.d("TextResponse", "Received text: $outputContent")
                    _uiState.value = UiState.Success(outputContent)
                } ?: run {
                    _uiState.value = UiState.Error("Không nhận được phản hồi từ model")
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiTextViewModel", "Error processing response: ${e.message}", e)
            _uiState.value = UiState.Error("Lỗi khi xử lý phản hồi: ${e.message}")
            if (e is ServerException && e.message?.contains("RESOURCE_EXHAUSTED") != false) {
                // Lỗi API bị quá tải
                _uiState.value = UiState.Error("API quá tải, vui lòng thử lại sau.")
            } else {
                _uiState.value = UiState.Error("Lỗi khi xử lý phản hồi: ${e.message}")
            }
        } catch (e: GoogleGenerativeAIException) {
            // Handle specific API quota errors or missing fields
            if (e.message?.contains("RESOURCE_EXHAUSTED") == true) {
                _uiState.value = UiState.Error("Quá hạn sử dụng API. Vui lòng thử lại sau.")
            } else {
                Log.e("GeminiTextViewModel", "Error from API: ${e.message}", e)
                _uiState.value = UiState.Error("Lỗi khi xử lý phản hồi: ${e.message}")
            }
        }
    }


    private fun loadBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap? {
        return try {
            contentResolver.openInputStream(uri)?.use {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            Log.e("GeminiTextViewModel", "Error loading bitmap: ${e.message}", e)
            null
        }
    }


}