package com.example.geminiapi2.features.budget.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.geminiapi2.data.dto.MacroResponse
import com.example.geminiapi2.features.budget.viewmodel.BudgetUiState
import com.example.geminiapi2.features.budget.viewmodel.BudgetViewModel
import com.example.geminiapi2.features.savingpot.ui.SavingPotRowItem
import com.example.geminiapi2.features.savingpot.viewmodel.SavingPotUiState
import com.example.geminiapi2.features.savingpot.viewmodel.SavingPotViewModel
import com.example.geminiapi2.ui.theme.*
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.PieChart
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

data class MacroPreset(
    val name: String,
    val description: String,
    val needPercent: Int,
    val wantPercent: Int,
    val savePercent: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel = hiltViewModel(),
    savingPotViewModel: SavingPotViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isCreating by viewModel.isCreating.collectAsState()
    val savingPotUiState by savingPotViewModel.uiState.collectAsStateWithLifecycle()
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var showModifyDialog by remember { mutableStateOf(false) }
    var currentMacro by remember { mutableStateOf<MacroResponse?>(null) }

    // Auto show create dialog when no macro
    LaunchedEffect(uiState) {
        val currentState = uiState
        if (currentState is BudgetUiState.NoMacro) {
            showCreateDialog = true
        }
    }

    if (showCreateDialog) {
        MacroDialog(
            isUpdate = false,
            currentMacro = null,
            isLoading = isCreating,
            onDismiss = { 
                showCreateDialog = false
                val currentState = uiState
                if (currentState is BudgetUiState.NoMacro) {
                    // If no macro exists, don't allow dismissing
                    showCreateDialog = true
                }
            },
            onConfirm = { name, need, want, save ->
                viewModel.createMacro(name, need, want, save)
                showCreateDialog = false
            }
        )
    }

    if (showModifyDialog && currentMacro != null) {
        MacroDialog(
            isUpdate = true,
            currentMacro = currentMacro,
            isLoading = isCreating,
            onDismiss = { showModifyDialog = false },
            onConfirm = { name, need, want, save ->
                viewModel.updateMacro(currentMacro!!.id, name, need, want, save)
                showModifyDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Budget Management",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        val currentState = uiState
        when (currentState) {
            is BudgetUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is BudgetUiState.Error -> {
                val errorMessage = currentState.message
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.loadMacro() }
                    ) {
                        Text("Thử lại")
                    }
                }
            }

            is BudgetUiState.NoMacro -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is BudgetUiState.Success -> {
                val macro = currentState.macro
                currentMacro = macro
                MacroContent(
                    macro = macro,
                    savingPotUiState = savingPotUiState,
                    onModify = { showModifyDialog = true },
                    onReloadSavingPots = { savingPotViewModel.loadSavingPots() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun MacroContent(
    macro: MacroResponse,
    savingPotUiState: SavingPotUiState,
    onModify: () -> Unit,
    onReloadSavingPots: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // MyFitnessPal Style Card with Gradient Background
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFF0F0FF),
                                    Color.White
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Header with title and edit button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = macro.macroName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            IconButton(
                                onClick = onModify,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Pie Chart Section with improved spacing
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Chart with total in center
                            Box(
                                modifier = Modifier.size(180.dp),
        contentAlignment = Alignment.Center
                            ) {
                                BudgetPieChart(
                                    needPercent = macro.needPercent,
                                    wantPercent = macro.wantPercent,
                                    savePercent = macro.savePercent
                                )
                                
                                // Total in center similar to DashboardScreen
//                                Column(
//                                    horizontalAlignment = Alignment.CenterHorizontally
//                                ) {
//                                    Text(
//                                        text = "Total Budget",
//                                        style = MaterialTheme.typography.bodySmall.copy(
//                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
//                                            fontSize = 12.sp,
//                                            letterSpacing = 0.4.sp
//                                        )
//                                    )
//                                    Text(
//                                        text = formatAmount(macro.needAmount + macro.wantAmount + macro.saveAmount),
//                                        style = MaterialTheme.typography.bodyLarge.copy(
//                                            color = MaterialTheme.colorScheme.onSurface,
//                                            fontWeight = FontWeight.Medium,
//                                            fontSize = 16.sp
//                                        )
//                                    )
//                                }
                            }

                            // Legends with improved layout
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    LegendItem(
                                        color = Color(0xFF0066EE),
                                        label = "Needs",
                                        percentage = macro.needPercent,
                                        amount = macro.needAmount,
                                        modifier = Modifier.weight(1f)
                                    )
                                    LegendItem(
                                        color = Color(0xFFEC3762),
                                        label = "Wants",
                                        percentage = macro.wantPercent,
                                        amount = macro.wantAmount,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    LegendItem(
                                        color = Color(0xFFFA9B31),
                                        label = "Savings",
                                        percentage = macro.savePercent,
                                        amount = macro.saveAmount,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // Saving Pots Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Hũ Tiết Kiệm",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when (savingPotUiState) {
                is SavingPotUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = primaryBrand
                        )
                    }
                }

                is SavingPotUiState.Success -> {
                    val savingPots = savingPotUiState.savingPots
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        savingPots.forEach { savingPot ->
                            SavingPotRowItem(
                                savingPot = savingPot,
                                onClick = { /* Handle click */ }
                            )
                        }
                    }
                }

                is SavingPotUiState.Empty -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Chưa có hũ tiết kiệm nào",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    ),
                                    color = neutralGrey1,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = "Tạo hũ tiết kiệm đầu tiên của bạn",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 12.sp
                                    ),
                                    color = neutralGrey2,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                is SavingPotUiState.Error -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Có lỗi xảy ra",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Button(
                                    onClick = onReloadSavingPots,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text(
                                        text = "Thử lại",
                                        color = MaterialTheme.colorScheme.onError
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetPieChart(
    needPercent: Int,
    wantPercent: Int,
    savePercent: Int
) {
    val needColor = Color(0xFF0066EE)
    val wantColor = Color(0xFFEC3762)
    val saveColor = Color(0xFFFA9B31)

    // Create pie data similar to DashboardScreen
    val pieData = listOf(
        Pie(
            label = "Needs",
            data = needPercent.toDouble(),
            color = needColor,
            selectedColor = needColor
        ),
        Pie(
            label = "Wants", 
            data = wantPercent.toDouble(),
            color = wantColor,
            selectedColor = wantColor
        ),
        Pie(
            label = "Savings",
            data = savePercent.toDouble(), 
            color = saveColor,
            selectedColor = saveColor
        )
    ).filter { it.data > 0 } // Only show non-zero segments

    if (pieData.isNotEmpty()) {
        PieChart(
            modifier = Modifier.size(150.dp),
            data = pieData,
            onPieClick = { },
            selectedScale = 1.0f,
            style = Pie.Style.Stroke(width = 35.dp)
        )
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String,
    percentage: Int,
    amount: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, CircleShape)
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    letterSpacing = 0.4.sp
                )
            )
            Text(
                text = "$percentage% (${formatAmount(amount)})",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF939090),
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF484646),
                letterSpacing = 0.5.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp,
                fontWeight = if (valueColor == Color(0xFF0066EE)) FontWeight.Medium else FontWeight.Normal,
                color = valueColor,
                letterSpacing = 0.15.sp
            )
        )
    }
}

@Composable
fun MacroDialog(
    isUpdate: Boolean,
    currentMacro: MacroResponse?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, Int, Int) -> Unit
) {
    var selectedPreset by remember { mutableStateOf<MacroPreset?>(null) }
    var customMode by remember { mutableStateOf(false) }
    var macroName by remember { mutableStateOf(currentMacro?.macroName ?: "") }
    var needPercent by remember { mutableStateOf(currentMacro?.needPercent ?: 50) }
    var wantPercent by remember { mutableStateOf(currentMacro?.wantPercent ?: 30) }
    var savePercent by remember { mutableStateOf(currentMacro?.savePercent ?: 20) }

    val presets = listOf(
        MacroPreset("Basic Stability", "50-30-20 Rule", 50, 30, 20),
        MacroPreset("Financial Growth", "Focus on savings and investments", 40, 20, 40),
        MacroPreset("FIRE", "Financial Independence, Retire Early", 30, 10, 60)
    )

    val totalPercent = needPercent + wantPercent + savePercent
    val isValidTotal = totalPercent == 100

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isUpdate) "Edit Budget Plan" else "Create Budget Plan",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = macroName,
                    onValueChange = { macroName = it },
                    label = { Text("Plan Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Preset section
                Text(
                    text = "Choose a Template:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    presets.forEach { preset ->
                        PresetCard(
                            preset = preset,
                            isSelected = selectedPreset == preset && !customMode,
                            onClick = {
                                selectedPreset = preset
                                customMode = false
                                macroName = preset.name
                                needPercent = preset.needPercent
                                wantPercent = preset.wantPercent
                                savePercent = preset.savePercent
                            }
                        )
                    }
                    PresetCard(
                        preset = MacroPreset("Custom", "Create your own", 0, 0, 0),
                        isSelected = customMode,
                        onClick = {
                            customMode = true
                            selectedPreset = null
                        }
                    )
                }

                if (customMode) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Adjust Percentages:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        @OptIn(ExperimentalMaterial3Api::class)
                        CustomSliderRow("Needs", needPercent, MaterialTheme.colorScheme.error, onValueChange = { needPercent = it })
                        @OptIn(ExperimentalMaterial3Api::class)
                        CustomSliderRow("Wants", wantPercent, MaterialTheme.colorScheme.tertiary, onValueChange = { wantPercent = it })
                        @OptIn(ExperimentalMaterial3Api::class)
                        CustomSliderRow("Savings", savePercent, MaterialTheme.colorScheme.secondary, onValueChange = { savePercent = it })
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Total: $totalPercent%",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (isValidTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    Button(
                        onClick = {
                            if (macroName.isNotBlank() && isValidTotal) {
                                onConfirm(macroName, needPercent, wantPercent, savePercent)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading && macroName.isNotBlank() && isValidTotal,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = if (isUpdate) "Update" else "Create",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSliderRow(
    label: String,
    value: Int,
    color: Color,
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = "$value%",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = value.toFloat(),
            onValueChange = {
                val roundedValue = (it / 5f).roundToInt() * 5
                onValueChange(roundedValue)
            },
            valueRange = 0f..100f,
            steps = 19,
            colors = SliderDefaults.colors(
                activeTrackColor = color,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                thumbColor = color,
                disabledThumbColor = MaterialTheme.colorScheme.outline
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            CircleShape
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(color, CircleShape)
                    )
                }
            },
            track = {
                SliderDefaults.Track(
                    sliderState = it,
                    colors = SliderDefaults.colors(
                        activeTrackColor = color,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.height(4.dp)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PresetCard(
    preset: MacroPreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(88.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 3.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = preset.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = preset.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 2
            )
        }
    }
}

private fun formatAmount(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(amount).replace("₫", "đ")
} 