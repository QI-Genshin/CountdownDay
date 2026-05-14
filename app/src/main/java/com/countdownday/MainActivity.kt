package com.countdownday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.countdownday.ui.theme.CountdownDayTheme
import com.countdownday.ui.theme.Primary
import com.countdownday.ui.theme.Secondary
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Anniversary(
    val id: Long,
    val name: String,
    val date: LocalDate,
    val color: Color = Primary
)

private val sampleData = listOf(
    Anniversary(1, "生日", LocalDate.of(2026, 6, 15), Primary),
    Anniversary(2, "纪念日", LocalDate.of(2026, 5, 20), Secondary),
    Anniversary(3, "新年", LocalDate.of(2027, 1, 1), Color(0xFFFF6B6B)),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CountdownDayTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var anniversaries by remember { mutableStateOf(sampleData) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("倒数日") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("搜索纪念日...") },
                singleLine = true
            )

            if (anniversaries.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("📅", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("还没有纪念日", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("点击右下角按钮添加第一个纪念日", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(anniversaries) { anniversary ->
                        AnniversaryCard(anniversary)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, date ->
                val newItem = Anniversary(
                    id = System.currentTimeMillis(),
                    name = name,
                    date = date
                )
                anniversaries = anniversaries + newItem
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AnniversaryCard(anniversary: Anniversary) {
    val daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), anniversary.date)
    val displayText = when {
        daysRemaining == 0L -> "今天"
        daysRemaining > 0 -> "还有 $daysRemaining 天"
        else -> "已过 ${-daysRemaining} 天"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(anniversary.color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        anniversary.name.take(1),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(anniversary.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    anniversary.date.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    kotlin.math.abs(daysRemaining).toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        daysRemaining == 0L -> anniversary.color
                        daysRemaining in 1..7 -> Secondary
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
                Text(displayText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun AddEditDialog(onDismiss: () -> Unit, onSave: (String, LocalDate) -> Unit) {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加纪念日") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("名称") },
                    modifier = Modifier.fillMaxWidth()
                )
                DatePickerField(date = date, onDateChange = { date = it })
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onSave(name, date) },
                enabled = name.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
fun DatePickerField(date: LocalDate, onDateChange: (LocalDate) -> Unit) {
    Column {
        Text("日期: ${date.year}-${date.monthValue}-${date.dayOfMonth}", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { onDateChange(date.minusYears(1)) }) { Text("-1年") }
            OutlinedButton(onClick = { onDateChange(date.minusMonths(1)) }) { Text("-1月") }
            OutlinedButton(onClick = { onDateChange(date.plusMonths(1)) }) { Text("+1月") }
            OutlinedButton(onClick = { onDateChange(date.plusYears(1)) }) { Text("+1年") }
        }
    }
}
