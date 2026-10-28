package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppColorRuleEntity
import com.example.model.AodConfigurationState
import com.example.model.AppFilterMode
import com.example.model.InstalledAppInfo
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EnergyEmerald
import com.example.ui.theme.MatrixRed
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShareTechMonoFontFamily
import com.example.ui.theme.SolarAmber

@Composable
fun AppFilterRulesSection(
    config: AodConfigurationState,
    appColorRules: List<AppColorRuleEntity>,
    installedApps: List<InstalledAppInfo>,
    onSelectFilterMode: (AppFilterMode) -> Unit,
    onToggleWhitelist: (AppColorRuleEntity) -> Unit,
    onToggleBlacklist: (AppColorRuleEntity) -> Unit,
    onToggleWakeScreen: (AppColorRuleEntity) -> Unit,
    onToggleAutoExpand: (AppColorRuleEntity) -> Unit,
    onUpdateAppRuleColor: (AppColorRuleEntity, Long) -> Unit,
    onAddAppRule: (String, String, String, Long, Boolean, Boolean) -> Unit,
    onDeleteAppRule: (String) -> Unit,
    onTriggerSimulatedAppPush: (AppColorRuleEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0=All, 1=Whitelist, 2=Blacklist, 3=Installed
    var customAppName by remember { mutableStateOf("") }
    var customPackageName by remember { mutableStateOf("") }

    val selectableColorHexes = listOf(
        0xFF38BDF8L,
        0xFF00E676L,
        0xFF00F5D4L,
        0xFFFF2A85L,
        0xFFFFB800L,
        0xFF7B61FFL,
        0xFFFF3B30L
    )

    val whitelistCount = appColorRules.count { it.isWhitelisted && !it.isBlacklisted }
    val blacklistCount = appColorRules.count { it.isBlacklisted }

    val filteredRules = remember(appColorRules, searchQuery, selectedSubTab) {
        appColorRules.filter { rule ->
            val matchesQuery = searchQuery.isBlank() ||
                rule.appDisplayName.contains(searchQuery, ignoreCase = true) ||
                rule.packageName.contains(searchQuery, ignoreCase = true)
            val matchesTab = when (selectedSubTab) {
                1 -> rule.isWhitelisted && !rule.isBlacklisted
                2 -> rule.isBlacklisted
                else -> true
            }
            matchesQuery && matchesTab
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Global Whitelist / Blacklist Mode Selector
        SectionHeaderRow(
            icon = Icons.Default.VerifiedUser,
            title = "Белый и Черный списки приложений",
            badgeText = "Белый: $whitelistCount • Черный: $blacklistCount"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Режим фильтрации входящих пушей для AOD и Динамического Острова:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedSlate
                )

                AppFilterMode.entries.forEach { mode ->
                    val selected = config.appFilterMode == mode
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectFilterMode(mode) }
                            .testTag("filter_mode_${mode.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) Color(0xFF14273B) else Color(0xFF0E1420)
                        ),
                        border = BorderStroke(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) CyberCyan else ObsidianBorder
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = mode.titleRu,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 14.sp,
                                color = if (selected) CyberCyan else PureWhite
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = mode.subtitleRu,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 11.sp,
                                color = MutedSlate
                            )
                        }
                    }
                }
            }
        }

        // 2. Search & Sub-Tabs (All, Whitelist, Blacklist, Installed Apps Scanner)
        SectionHeaderRow(
            icon = Icons.Default.FilterList,
            title = "Управление правилами приложений",
            badgeText = "${appColorRules.size} правил"
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "Поиск по названию или пакету (например, Telegram)...",
                    color = MutedSlate,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CyberCyan)
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("app_filter_search_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = ObsidianBorder,
                focusedTextColor = PureWhite,
                unfocusedTextColor = PureWhite
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf(
                0 to "Все правила (${appColorRules.size})",
                1 to "Белый список ($whitelistCount)",
                2 to "Черный список ($blacklistCount)",
                3 to "Установленные на устройстве (${installedApps.size})"
            )
            tabs.forEach { (idx, label) ->
                FilterChip(
                    selected = selectedSubTab == idx,
                    onClick = { selectedSubTab = idx },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (idx) {
                            1 -> EnergyEmerald
                            2 -> MatrixRed
                            else -> CyberCyan
                        },
                        selectedLabelColor = PitchBlack
                    ),
                    modifier = Modifier.testTag("app_filter_subtab_$idx")
                )
            }
        }

        // 3. Installed Device Packages Scanner View (when sub-tab 3 is selected)
        if (selectedSubTab == 3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Добавить установленное приложение в Белый или Черный список:",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 14.sp,
                        color = PureWhite
                    )

                    val matchingInstalled = installedApps.filter {
                        searchQuery.isBlank() ||
                            it.appName.contains(searchQuery, ignoreCase = true) ||
                            it.packageName.contains(searchQuery, ignoreCase = true)
                    }.take(16)

                    if (matchingInstalled.isEmpty()) {
                        Text(
                            text = "Используйте форму ниже, чтобы добавить любой пакет вручную.",
                            color = MutedSlate,
                            fontSize = 12.sp
                        )
                    } else {
                        matchingInstalled.forEach { app ->
                            val existingRule = appColorRules.find { it.packageName == app.packageName }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0E1420))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = app.appName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = PureWhite,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = app.packageName,
                                        fontFamily = ShareTechMonoFontFamily,
                                        fontSize = 10.sp,
                                        color = MutedSlate
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        onClick = {
                                            onAddAppRule(
                                                app.packageName,
                                                app.appName,
                                                "Установленное",
                                                existingRule?.colorHex ?: 0xFF00F5D4L,
                                                true,
                                                false
                                            )
                                        },
                                        shape = RoundedCornerShape(50),
                                        color = EnergyEmerald.copy(alpha = 0.2f),
                                        border = BorderStroke(1.dp, EnergyEmerald)
                                    ) {
                                        Text(
                                            text = "+ Белый",
                                            fontFamily = ShareTechMonoFontFamily,
                                            fontSize = 10.sp,
                                            color = EnergyEmerald,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                        )
                                    }
                                    Surface(
                                        onClick = {
                                            onAddAppRule(
                                                app.packageName,
                                                app.appName,
                                                "Заблокировано",
                                                0xFFFF3B30L,
                                                false,
                                                true
                                            )
                                        },
                                        shape = RoundedCornerShape(50),
                                        color = MatrixRed.copy(alpha = 0.2f),
                                        border = BorderStroke(1.dp, MatrixRed)
                                    ) {
                                        Text(
                                            text = "+ Черный",
                                            fontFamily = ShareTechMonoFontFamily,
                                            fontSize = 10.sp,
                                            color = MatrixRed,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Quick Add Custom App Rule Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, ObsidianBorder)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Быстрое добавление приложения в список",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 14.sp,
                        color = PureWhite
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customAppName,
                        onValueChange = { customAppName = it },
                        placeholder = { Text("Имя (напр. Discord)", fontSize = 12.sp, color = MutedSlate) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = customPackageName,
                        onValueChange = { customPackageName = it },
                        placeholder = { Text("com.discord (опц.)", fontSize = 12.sp, color = MutedSlate) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (customAppName.isNotBlank()) {
                                onAddAppRule(
                                    customPackageName,
                                    customAppName,
                                    "Белый список VIP",
                                    0xFF00F5D4L,
                                    true,
                                    false
                                )
                                customAppName = ""
                                customPackageName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EnergyEmerald,
                            contentColor = PitchBlack
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_app_to_whitelist_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("В Белый список", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            if (customAppName.isNotBlank()) {
                                onAddAppRule(
                                    customPackageName,
                                    customAppName,
                                    "Черный список",
                                    0xFFFF3B30L,
                                    false,
                                    true
                                )
                                customAppName = ""
                                customPackageName = ""
                            }
                        },
                        border = BorderStroke(1.dp, MatrixRed),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_app_to_blacklist_button")
                    ) {
                        Icon(imageVector = Icons.Default.Block, contentDescription = null, tint = MatrixRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("В Черный список", fontSize = 12.sp, color = MatrixRed)
                    }
                }
            }
        }

        // 5. Per-App Rule Cards with Whitelist/Blacklist Toggles, Wake-on-Push & Color Picker
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            filteredRules.forEach { rule ->
                val ruleColor = Color(rule.colorHex)
                val borderColor = when {
                    rule.isBlacklisted -> MatrixRed.copy(alpha = 0.7f)
                    rule.isWhitelisted -> ruleColor.copy(alpha = 0.6f)
                    else -> ObsidianBorder
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.2.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(if (rule.isBlacklisted) MatrixRed else ruleColor)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = rule.appDisplayName,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = PureWhite
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(
                                                    when {
                                                        rule.isBlacklisted -> MatrixRed.copy(alpha = 0.2f)
                                                        rule.isWhitelisted -> EnergyEmerald.copy(alpha = 0.2f)
                                                        else -> Color(0xFF1B2434)
                                                    }
                                                )
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = when {
                                                    rule.isBlacklisted -> "ЧЕРНЫЙ СПИСОК"
                                                    rule.isWhitelisted -> "БЕЛЫЙ СПИСОК"
                                                    else -> "ОБЫЧНЫЙ"
                                                },
                                                fontFamily = ShareTechMonoFontFamily,
                                                fontSize = 9.sp,
                                                color = when {
                                                    rule.isBlacklisted -> MatrixRed
                                                    rule.isWhitelisted -> EnergyEmerald
                                                    else -> MutedSlate
                                                }
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${rule.categoryRu} • ${rule.packageName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 10.sp,
                                        color = MutedSlate
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = { onTriggerSimulatedAppPush(rule) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ruleColor.copy(alpha = 0.22f),
                                        contentColor = ruleColor
                                    ),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.testTag("simulate_push_${rule.packageName}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Тест пуша",
                                        fontFamily = ShareTechMonoFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (appColorRules.size > 3) {
                                    IconButton(
                                        onClick = { onDeleteAppRule(rule.packageName) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Удалить правило",
                                            tint = MutedSlate,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Whitelist / Blacklist / Wake-on-Push / Auto-Expand Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = rule.isWhitelisted && !rule.isBlacklisted,
                                onClick = { onToggleWhitelist(rule) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                label = { Text("Белый список", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EnergyEmerald,
                                    selectedLabelColor = PitchBlack,
                                    selectedLeadingIconColor = PitchBlack
                                )
                            )

                            FilterChip(
                                selected = rule.isBlacklisted,
                                onClick = { onToggleBlacklist(rule) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                label = { Text("Черный список (Блок)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MatrixRed,
                                    selectedLabelColor = PureWhite,
                                    selectedLeadingIconColor = PureWhite
                                )
                            )

                            FilterChip(
                                selected = rule.wakeScreenOnPush,
                                onClick = { onToggleWakeScreen(rule) },
                                label = { Text("Будить экран AOD", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                                    selectedLabelColor = CyberCyan
                                )
                            )

                            FilterChip(
                                selected = rule.autoExpandIslandOnPush,
                                onClick = { onToggleAutoExpand(rule) },
                                label = { Text("Авто-раскрытие Острова", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SolarAmber.copy(alpha = 0.25f),
                                    selectedLabelColor = SolarAmber
                                )
                            )
                        }

                        // Color Palette Row for this App
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Цвет свечения и Острова:",
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 11.sp,
                                color = MutedSlate
                            )
                            selectableColorHexes.forEach { hex ->
                                val c = Color(hex)
                                val isCurrent = rule.colorHex == hex
                                Box(
                                    modifier = Modifier
                                        .size(if (isCurrent) 22.dp else 18.dp)
                                        .clip(CircleShape)
                                        .background(c)
                                        .border(
                                            width = if (isCurrent) 2.dp else 0.dp,
                                            color = if (isCurrent) PureWhite else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { onUpdateAppRuleColor(rule, hex) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
