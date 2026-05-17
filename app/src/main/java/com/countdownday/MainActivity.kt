package com.countdownday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.countdownday.model.*
import com.countdownday.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShiguangCountdownTheme {
                ShiguangCountdownApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiguangCountdownApp() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var events by remember { mutableStateOf(sampleEvents) }
    var categories by remember { mutableStateOf(defaultCategories) }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var showAddEvent by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<Event?>(null) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<Event?>(null) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var privateEventToView by remember { mutableStateOf<Event?>(null) }
    var darkThemeEnabled by remember { mutableStateOf(false) }

    ShiguangCountdownTheme(darkTheme = darkThemeEnabled) {
        // 详情页作为独立页面，不显示导航栏
        if (selectedEvent != null) {
            EventDetailScreen(
                event = selectedEvent!!,
                category = categories.find { it.id == selectedEvent!!.categoryId }!!,
                onEdit = {
                    editingEvent = selectedEvent
                    currentScreen = Screen.NEW
                    selectedEvent = null
                },
                onDelete = {
                    eventToDelete = selectedEvent
                    showDeleteDialog = true
                    // 不立即关闭详情页，保持在详情页直到用户确认删除
                },
                onTogglePin = {
                    events = events.map { if (it.id == selectedEvent!!.id) it.copy(isPinned = !it.isPinned) else it }
                    selectedEvent = selectedEvent?.copy(isPinned = !selectedEvent!!.isPinned)
                },
                onTogglePrivate = {
                    events = events.map { if (it.id == selectedEvent!!.id) it.copy(isPrivate = !it.isPrivate) else it }
                    selectedEvent = selectedEvent?.copy(isPrivate = !selectedEvent!!.isPrivate)
                },
                onClose = { selectedEvent = null }
            )

            // 删除确认对话框
            if (showDeleteDialog && eventToDelete != null) {
                DeleteConfirmDialog(
                    event = eventToDelete!!,
                    onConfirm = {
                        events = events.filter { it.id != eventToDelete!!.id }
                        showDeleteDialog = false
                        eventToDelete = null
                        selectedEvent = null // 确认删除后才关闭详情页
                    },
                    onDismiss = {
                        showDeleteDialog = false
                        eventToDelete = null
                    }
                )
            }

            // 密码对话框
            if (showPasswordDialog && privateEventToView != null) {
                PasswordDialog(
                    onVerify = {
                        showPasswordDialog = false
                        selectedEvent = privateEventToView
                        privateEventToView = null
                    },
                    onDismiss = {
                        showPasswordDialog = false
                        privateEventToView = null
                    }
                )
            }
        } else {
            // 其他页面显示导航栏
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        currentScreen = currentScreen,
                        onScreenSelected = { screen ->
                            currentScreen = screen
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (currentScreen) {
                        Screen.HOME -> HomeScreen(
                            events = events,
                            categories = categories,
                            selectedCategoryId = selectedCategoryId,
                            onCategorySelected = { selectedCategoryId = it },
                            onEventClick = { event ->
                                if (event.isPrivate) {
                                    privateEventToView = event
                                    showPasswordDialog = true
                                } else {
                                    selectedEvent = event
                                }
                            },
                            onEventDelete = { event ->
                                eventToDelete = event
                                showDeleteDialog = true
                            },
                            onEventTogglePin = { event ->
                                events = events.map { if (it.id == event.id) it.copy(isPinned = !it.isPinned) else it }
                            },
                            onAddClick = { currentScreen = Screen.NEW }
                        )
                        Screen.NEW -> AddEditEventScreen(
                            categories = categories,
                            editingEvent = editingEvent,
                            onSave = { event ->
                                if (editingEvent != null) {
                                    events = events.map { if (it.id == editingEvent!!.id) event else it }
                                } else {
                                    events = events + event
                                }
                                showAddEvent = false
                                editingEvent = null
                                currentScreen = Screen.HOME
                            },
                            onCancel = {
                                showAddEvent = false
                                editingEvent = null
                                currentScreen = Screen.HOME
                            }
                        )
                        Screen.CATEGORY -> CategoryScreen(
                            categories = categories,
                            onAddCategory = { category ->
                                categories = categories + category
                            },
                            onEditCategory = { oldCategory, newCategory ->
                                categories = categories.map { if (it.id == oldCategory.id) newCategory else it }
                            },
                            onDeleteCategory = { category ->
                                if (!category.isSystem) {
                                    categories = categories.filter { it.id != category.id }
                                }
                            }
                        )
                        Screen.MINE -> MineScreen(
                            darkThemeEnabled = darkThemeEnabled,
                            onThemeToggle = { darkThemeEnabled = it }
                        )
                    }

                    // 其他对话框
                    if (showDeleteDialog && eventToDelete != null) {
                        DeleteConfirmDialog(
                            event = eventToDelete!!,
                            onConfirm = {
                                events = events.filter { it.id != eventToDelete!!.id }
                                showDeleteDialog = false
                                eventToDelete = null
                            },
                            onDismiss = {
                                showDeleteDialog = false
                                eventToDelete = null
                            }
                        )
                    }

                    if (showPasswordDialog && privateEventToView != null) {
                        PasswordDialog(
                            onVerify = {
                                showPasswordDialog = false
                                selectedEvent = privateEventToView
                                privateEventToView = null
                            },
                            onDismiss = {
                                showPasswordDialog = false
                                privateEventToView = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    val items = listOf(
        BottomNavItem(Screen.HOME, "首页", Icons.Outlined.Home, Icons.Filled.Home),
        BottomNavItem(Screen.NEW, "新建", Icons.Outlined.AddCircle, Icons.Filled.AddCircle),
        BottomNavItem(Screen.CATEGORY, "分类", Icons.Outlined.List, Icons.Filled.List),
        BottomNavItem(Screen.MINE, "我的", Icons.Outlined.Person, Icons.Filled.Person)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentScreen == item.screen,
                onClick = { onScreenSelected(item.screen) },
                icon = {
                    Icon(
                        imageVector = if (currentScreen == item.screen) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        item.label,
                        fontSize = 12.sp,
                        fontWeight = if (currentScreen == item.screen) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    events: List<Event>,
    categories: List<Category>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long?) -> Unit,
    onEventClick: (Event) -> Unit,
    onEventDelete: (Event) -> Unit,
    onEventTogglePin: (Event) -> Unit,
    onAddClick: () -> Unit
) {
    val filteredEvents = remember(events, selectedCategoryId) {
        val sorted = events.sortedWith(
            compareByDescending<Event> { it.isPinned }
                .thenBy { ChronoUnit.DAYS.between(LocalDate.now(), it.date) }
        )
        if (selectedCategoryId == null) sorted else sorted.filter { it.categoryId == selectedCategoryId }
    }
    val isDark = MaterialTheme.colorScheme.onBackground == OnBackgroundDark
    val gradientColors = if (isDark) GradientColorsDark else GradientColors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    "拾光倒数日",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            actions = {
                IconButton(onClick = { /* Search */ }) {
                    Icon(Icons.Outlined.Search, contentDescription = "搜索", tint = MaterialTheme.colorScheme.onBackground)
                }
                IconButton(onClick = { /* Settings */ }) {
                    Icon(Icons.Outlined.Settings, contentDescription = "设置", tint = MaterialTheme.colorScheme.onBackground)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            item {
                CategoryChip(
                    name = "全部",
                    isSelected = selectedCategoryId == null,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { onCategorySelected(null) }
                )
            }
            items(categories) { category ->
                CategoryChip(
                    name = category.name,
                    isSelected = selectedCategoryId == category.id,
                    color = category.color,
                    onClick = { onCategorySelected(category.id) }
                )
            }
        }

        if (filteredEvents.isEmpty()) {
            EmptyState(onAddClick = onAddClick)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredEvents, key = { it.id }) { event ->
                    val category = categories.find { it.id == event.categoryId }!!
                    EventCard(
                        event = event,
                        category = category,
                        onClick = { onEventClick(event) },
                        onDelete = { onEventDelete(event) },
                        onTogglePin = { onEventTogglePin(event) },
                        isDark = isDark
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(25.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                name,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun EmptyState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(60.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "暂无事件",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "点击下方 + 添加第一个倒数日",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.height(50.dp)
        ) {
            Icon(Icons.Outlined.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("添加事件", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    category: Category,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onTogglePin: () -> Unit,
    isDark: Boolean
) {
    val daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), event.date)
    val daysText = when {
        daysRemaining == 0L -> "今天"
        daysRemaining > 0L -> "还有 $daysRemaining 天"
        else -> "已过 ${-daysRemaining} 天"
    }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val gradientColors = if (isDark) GradientColorsDark else GradientColors

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = if (event.isPrivate) {
                            if (isDark) listOf(Color.DarkGray, Color.Gray) else listOf(Color.Gray, Color.LightGray)
                        } else {
                            if (isDark) event.gradientColorsDark else event.gradientColors
                        }
                    )
                )
        ) {
            if (event.isPrivate) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(10.dp)
                        .alpha(0.7f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            event.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = category.color.copy(alpha = if (isDark) 0.4f else 0.3f)
                        ) {
                            Text(
                                category.name,
                                fontSize = 11.sp,
                                color = if (isDark) Color.White else category.color,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (event.isPinned) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "置顶",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            event.date.format(dateFormatter),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (event.note.isNotEmpty()) {
                            Text(
                                event.note.take(20) + if (event.note.length > 20) "..." else "",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            if (event.isPrivate) "***" else kotlin.math.abs(daysRemaining).toString(),
                            fontSize = 45.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            if (event.isPrivate) "私密事件" else daysText,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEventScreen(
    categories: List<Category>,
    editingEvent: Event?,
    onSave: (Event) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(editingEvent?.name ?: "") }
    var date by remember { mutableStateOf(editingEvent?.date ?: LocalDate.now()) }
    var categoryId by remember { mutableStateOf(editingEvent?.categoryId ?: categories[0].id) }
    var note by remember { mutableStateOf(editingEvent?.note ?: "") }
    var isPinned by remember { mutableStateOf(editingEvent?.isPinned ?: false) }
    var isPrivate by remember { mutableStateOf(editingEvent?.isPrivate ?: false) }
    var isWidget by remember { mutableStateOf(editingEvent?.isWidget ?: false) }
    val isDark = MaterialTheme.colorScheme.onBackground == OnBackgroundDark
    // 总是显示浅色渐变供选择，保存时两套都保存
    val gradientColors = GradientColors
    var selectedGradientIndex by remember {
        mutableStateOf(
            editingEvent?.let { e ->
                GradientColors.indexOfFirst { it == e.gradientColors }
            }?.takeIf { it >= 0 } ?: 0
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    if (editingEvent == null) "新增倒数日" else "编辑事件",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "返回", tint = MaterialTheme.colorScheme.onBackground)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("事件名称") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            item {
                DatePickerField(
                    date = date,
                    onDateChange = { date = it }
                )
            }

            item {
                Text("选择分类", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategorySelectionChip(
                            category = category,
                            isSelected = categoryId == category.id,
                            onClick = { categoryId = category.id },
                            isDark = isDark
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SwitchRow(
                        title = "置顶显示",
                        checked = isPinned,
                        onCheckedChange = { isPinned = it }
                    )
                    SwitchRow(
                        title = "私密加密",
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it }
                    )
                    SwitchRow(
                        title = "添加到桌面小组件",
                        checked = isWidget,
                        onCheckedChange = { isWidget = it }
                    )
                }
            }

            item {
                Text("卡片背景", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(gradientColors.size) { index ->
                        GradientSelectionChip(
                            colors = gradientColors[index],
                            isSelected = selectedGradientIndex == index,
                            onClick = { selectedGradientIndex = index }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        if (name.isNotEmpty()) {
                            val newEvent = Event(
                                id = editingEvent?.id ?: System.currentTimeMillis(),
                                name = name,
                                date = date,
                                categoryId = categoryId,
                                note = note,
                                gradientColors = GradientColors[selectedGradientIndex],
                                gradientColorsDark = GradientColorsDark[selectedGradientIndex],
                                isPinned = isPinned,
                                isPrivate = isPrivate,
                                isWidget = isWidget
                            )
                            onSave(newEvent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = name.isNotEmpty()
                ) {
                    Text("保存", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("取消", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun DatePickerField(date: LocalDate, onDateChange: (LocalDate) -> Unit) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日")

    Column {
        Text("选择日期", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onDateChange(date.minusYears(1)) }) {
                    Icon(Icons.Outlined.KeyboardArrowLeft, contentDescription = "-1年")
                }
                IconButton(onClick = { onDateChange(date.minusMonths(1)) }) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "-1月")
                }
                Text(
                    date.format(dateFormatter),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { onDateChange(date.plusMonths(1)) }) {
                    Icon(Icons.Outlined.ArrowForward, contentDescription = "+1月")
                }
                IconButton(onClick = { onDateChange(date.plusYears(1)) }) {
                    Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = "+1年")
                }
            }
        }
    }
}

@Composable
fun CategorySelectionChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(25.dp),
        color = if (isSelected) category.color.copy(alpha = if (isDark) 0.5f else 0.3f) else MaterialTheme.colorScheme.surface,
        border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)) else null,
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(category.color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                category.name,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun GradientSelectionChip(
    colors: List<Color>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .size(50.dp)
            .then(
                if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50)) else Modifier
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(colors))
        )
    }
}

@Composable
fun SwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    categories: List<Category>,
    onAddCategory: (Category) -> Unit,
    onEditCategory: (Category, Category) -> Unit,
    onDeleteCategory: (Category) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    val isDark = MaterialTheme.colorScheme.onBackground == OnBackgroundDark
    val categoryColors = if (isDark) CategoryColorsDark else CategoryColors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    "分类管理",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            actions = {
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Outlined.Add, contentDescription = "添加分类", tint = MaterialTheme.colorScheme.onBackground)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories.chunked(3)) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { category ->
                        CategoryCard(
                            category = category,
                            modifier = Modifier.weight(1f),
                            onEdit = { editingCategory = category },
                            onDelete = { onDeleteCategory(category) },
                            isDark = isDark
                        )
                    }
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    if (showAddDialog || editingCategory != null) {
        AddEditCategoryDialog(
            editingCategory = editingCategory,
            onSave = { category ->
                if (editingCategory != null) {
                    onEditCategory(editingCategory!!, category)
                } else {
                    onAddCategory(category)
                }
                showAddDialog = false
                editingCategory = null
            },
            onDismiss = {
                showAddDialog = false
                editingCategory = null
            },
            isDark = isDark
        )
    }
}

@Composable
fun CategoryCard(
    category: Category,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isDark: Boolean
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(category.color.copy(alpha = if (isDark) 0.4f else 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(category.color)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                category.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (!category.isSystem) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = "编辑", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun AddEditCategoryDialog(
    editingCategory: Category?,
    onSave: (Category) -> Unit,
    onDismiss: () -> Unit,
    isDark: Boolean
) {
    var name by remember { mutableStateOf(editingCategory?.name ?: "") }
    val categoryColors = if (isDark) CategoryColorsDark else CategoryColors
    var selectedColorIndex by remember {
        mutableStateOf(
            editingCategory?.let { c -> categoryColors.indexOf(c.color) }?.takeIf { it >= 0 } ?: 0
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editingCategory == null) "添加分类" else "编辑分类") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("分类名称") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text("选择颜色", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categoryColors.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(categoryColors[index])
                                .clickable { selectedColorIndex = index }
                                .then(
                                    if (selectedColorIndex == index)
                                        Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    else
                                        Modifier
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotEmpty()) {
                        onSave(
                            Category(
                                id = editingCategory?.id ?: System.currentTimeMillis(),
                                name = name,
                                color = categoryColors[selectedColorIndex]
                            )
                        )
                    }
                },
                enabled = name.isNotEmpty()
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event,
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTogglePin: () -> Unit,
    onTogglePrivate: () -> Unit,
    onClose: () -> Unit
) {
    val daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), event.date)
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日")
    val isDark = MaterialTheme.colorScheme.onBackground == OnBackgroundDark
    // 根据主题选择对应的渐变背景
    val currentGradientColors = if (isDark) event.gradientColorsDark else event.gradientColors

    // 修复点击穿透和透视问题：使用深色半透明背景完全遮挡首页内容
    Box(
        modifier = Modifier
            .fillMaxSize()
            // 添加深色半透明背景来遮挡首页内容，防止透视
            .background(Color.Black.copy(alpha = 0.85f))
            // 使用无视觉效果的点击处理来阻止事件穿透
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // 使用不透明的渐变背景覆盖整个屏幕，根据主题选择
                .background(
                    Brush.verticalGradient(
                        colors = currentGradientColors.map { color ->
                            // 确保渐变色不透明，防止透视
                            color.copy(alpha = 1f)
                        }
                    )
                )
        ) {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Outlined.Close, contentDescription = "关闭", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Outlined.Share, contentDescription = "分享", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Outlined.Delete, contentDescription = "删除", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "距离「${event.name}」",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    kotlin.math.abs(daysRemaining).toString(),
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "天",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    event.date.format(dateFormatter),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (event.note.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        event.note,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BottomActionButton(
                        icon = Icons.Outlined.Edit,
                        label = "编辑",
                        onClick = onEdit
                    )
                    BottomActionButton(
                        icon = if (event.isPinned) Icons.Filled.Star else Icons.Outlined.Star,
                        label = if (event.isPinned) "取消置顶" else "置顶",
                        onClick = onTogglePin
                    )
                    BottomActionButton(
                        icon = if (event.isPrivate) Icons.Filled.Lock else Icons.Outlined.Lock,
                        label = if (event.isPrivate) "取消私密" else "私密",
                        onClick = onTogglePrivate
                    )
                    BottomActionButton(
                        icon = Icons.Outlined.DateRange,
                        label = "小组件",
                        onClick = { /* Widget */ }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MineScreen(
    darkThemeEnabled: Boolean,
    onThemeToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "时光记录者",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "记录每一个重要的日子",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(value = "5", label = "已记录")
                    StatItem(value = "2", label = "已完成")
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        MineItem(
                            icon = Icons.Outlined.Settings,
                            title = "主题模式",
                            subtitle = if (darkThemeEnabled) "深色" else "浅色",
                            onClick = { onThemeToggle(!darkThemeEnabled) }
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        MineItem(
                            icon = Icons.Outlined.DateRange,
                            title = "桌面小组件设置",
                            onClick = { /* Widget settings */ }
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        MineItem(
                            icon = Icons.Outlined.Lock,
                            title = "隐私密码锁",
                            onClick = { /* Password lock */ }
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        MineItem(
                            icon = Icons.Outlined.Notifications,
                            title = "消息提醒设置",
                            onClick = { /* Notification settings */ }
                        )
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        MineItem(
                            icon = Icons.Outlined.Build,
                            title = "数据备份与恢复",
                            onClick = { /* Backup */ }
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        MineItem(
                            icon = Icons.Outlined.Delete,
                            title = "清除缓存",
                            onClick = { /* Clear cache */ }
                        )
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        MineItem(
                            icon = Icons.Outlined.Info,
                            title = "关于我们",
                            onClick = { /* About */ }
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        MineItem(
                            icon = Icons.Outlined.Edit,
                            title = "意见反馈",
                            onClick = { /* Feedback */ }
                        )
                    }
                }
            }

            item {
                Text(
                    "版本 1.0.0",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MineItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            Icons.Outlined.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DeleteConfirmDialog(
    event: Event,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("删除事件") },
        text = { Text("确定要删除「${event.name}」吗？删除后不可恢复。") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("删除", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun PasswordDialog(
    onVerify: () -> Unit,
    onDismiss: () -> Unit
) {
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("请输入密码") },
        text = {
            Column {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                TextButton(onClick = { /* Forgot password */ }) {
                    Text("忘记密码？", fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (password.isNotEmpty()) onVerify() },
                enabled = password.isNotEmpty()
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
