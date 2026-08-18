package com.pico.swan.onepagemap.ui

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pico.spatial.ui.design.Button
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.design.TextField
import com.pico.spatial.ui.design.windows.AlertDialog
import com.pico.spatial.ui.foundation.haptic.controllerHapticFeedback
import com.pico.spatial.ui.foundation.hover.spatialHoverEffect
import com.pico.swan.onepagemap.data.LocalRoadmapRepository
import com.pico.swan.onepagemap.domain.model.*
import com.pico.swan.onepagemap.ui.components.RoadmapCanvas
import com.pico.swan.onepagemap.ui.components.RoadmapPalette
import com.pico.swan.onepagemap.ui.components.QuickGuideDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OnePageMapRoute(modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext
    val factory = remember(context) { OnePageMapViewModel.Factory(LocalRoadmapRepository(context)) }
    val viewModel: OnePageMapViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    OnePageMapScreen(state, viewModel, modifier)
}

@Composable
private fun OnePageMapScreen(state: OnePageMapUiState, actions: OnePageMapViewModel, modifier: Modifier) {
    Box(modifier.fillMaxSize()) {
        when (state.screen) {
            AppScreen.GUIDE -> QuickGuideDialog(
                stepIndex = state.quickGuideStep,
                onPrevious = actions::previousQuickGuide,
                onNext = actions::nextQuickGuide,
                onDismiss = actions::dismissQuickGuide,
                onStartEventTemplate = actions::startEventTemplateFromQuickGuide,
                returnToEditor = state.quickGuideReturnScreen == AppScreen.EDITOR,
            )
            AppScreen.TEMPLATE -> TemplateScreen(actions::chooseTemplate, actions::openQuickGuide)
            AppScreen.GOAL -> GoalScreen(state, actions)
            AppScreen.EDITOR -> EditorScreen(state, actions)
            AppScreen.SAVES -> SavesScreen(state, actions)
            AppScreen.EXPORT -> ExportScreen(state, actions)
        }
        state.message?.let { MessageBanner(it, actions::clearMessage, Modifier.align(Alignment.BottomCenter).padding(24.dp)) }
        state.pendingDialog?.let { PendingDialogView(it, actions::confirmDialog, actions::dismissDialog) }
    }
}

@Composable
private fun TemplateScreen(onChoose: (RoadmapTemplate) -> Unit, onOpenGuide: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.fillMaxWidth()) {
            Text("一页路线图", color = PicoTheme.colorScheme.labelPrimary, style = PicoTheme.typography.displayMedium, modifier = Modifier.align(Alignment.Center))
            Button(onClick = onOpenGuide, modifier = Modifier.align(Alignment.CenterEnd)) { Text("使用帮助") }
        }
        Text("选一条小路的起点。它不是专业项目管理系统。", color = PicoTheme.colorScheme.labelSecondary, style = PicoTheme.typography.bodyLarge)
        Spacer(Modifier.height(40.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            RoadmapTemplate.entries.forEach { template ->
                TemplateCard(template, { onChoose(template) }, Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(28.dp))
        Text("远处是目标旗帜，中间是步骤路标，脚边是资源工具箱，红色石块提醒风险。", color = PicoTheme.colorScheme.labelSecondary, style = PicoTheme.typography.bodyMedium)
    }
}

@Composable
private fun TemplateCard(template: RoadmapTemplate, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier.height(260.dp).clip(RoundedCornerShape(28.dp)).background(RoadmapPalette.Paper)
            .border(2.dp, RoadmapPalette.Brand, RoundedCornerShape(28.dp))
            .spatialHoverEffect().controllerHapticFeedback(interactionSource = interaction)
            .clickable(interactionSource = interaction, indication = LocalIndication.current, onClick = onClick)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(if (template == RoadmapTemplate.BLANK) "⚑" else "路标 ${template.ordinal + 1}", color = RoadmapPalette.Brand, style = PicoTheme.typography.titleLarge)
        Column {
            Text(template.title, color = RoadmapPalette.Ink, style = PicoTheme.typography.headlineLarge)
            Text(template.subtitle, color = RoadmapPalette.Ink, style = PicoTheme.typography.bodyMedium)
        }
        Text(if (template == RoadmapTemplate.BLANK) "0 张预设卡" else "6 张可编辑卡", color = RoadmapPalette.Brand, style = PicoTheme.typography.labelMedium)
    }
}

@Composable
private fun GoalScreen(state: OnePageMapUiState, actions: OnePageMapViewModel) {
    Row(Modifier.fillMaxSize().padding(48.dp), horizontalArrangement = Arrangement.spacedBy(32.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("先立一面旗帜", color = PicoTheme.colorScheme.labelPrimary, style = PicoTheme.typography.displayMedium)
            Text("你最终想抵达哪里？一句话就够。", color = PicoTheme.colorScheme.labelSecondary, style = PicoTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))
            Text("最终目标", color = PicoTheme.colorScheme.labelPrimary, style = PicoTheme.typography.labelLarge)
            TextField(value = state.plan.goal, onValueChange = actions::updateGoal, modifier = Modifier.fillMaxWidth(), placeholder = { Text("例如：筹备一次读书会") })
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = actions::back) { Text("返回模板") }
                Button(onClick = actions::confirmGoal, enabled = true) { Text("生成目标旗帜") }
            }
        }
        Box(Modifier.weight(1f).height(380.dp).clip(RoundedCornerShape(32.dp)).background(RoadmapPalette.Terrain), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⚑", color = RoadmapPalette.Accent, style = PicoTheme.typography.displayLarge)
                Text(state.plan.goal.ifBlank { "你的目标会出现在这里" }, color = RoadmapPalette.Paper, style = PicoTheme.typography.headlineLarge)
                Text("模板：${state.selectedTemplate.title} · ${state.plan.steps.size} 个起始路标", color = RoadmapPalette.Path, style = PicoTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun EditorScreen(state: OnePageMapUiState, actions: OnePageMapViewModel) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Header(state, actions)
        BoxWithConstraints(Modifier.fillMaxSize()) {
            if (maxWidth >= 1280.dp) {
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    EditorCanvas(state, actions, Modifier.weight(1f).fillMaxHeight())
                    EditPanel(state, actions, Modifier.width(320.dp).fillMaxHeight())
                }
            } else {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    EditorCanvas(state, actions, Modifier.weight(1f).fillMaxWidth())
                    EditPanel(state, actions, Modifier.fillMaxWidth().height(300.dp))
                }
            }
        }
    }
}

@Composable
private fun Header(state: OnePageMapUiState, actions: OnePageMapViewModel) {
    Row(Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = actions::back) { Text("模板") }
        Column(Modifier.weight(1f)) {
            Text(state.plan.goal, color = PicoTheme.colorScheme.labelPrimary, style = PicoTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${state.plan.steps.size}/12 路标 · ${state.plan.connections.size} 连线 · ${state.plan.risks.size} 风险${if (state.dirty) " · 未保存" else " · 已保存"}", color = PicoTheme.colorScheme.labelSecondary, style = PicoTheme.typography.bodySmall)
        }
        Button(onClick = actions::openQuickGuide) { Text("使用帮助") }
        Button(onClick = actions::openSaves) { Text("保存与恢复") }
        Button(onClick = actions::openExport) { Text("导出截图") }
    }
}

@Composable
private fun EditorCanvas(state: OnePageMapUiState, actions: OnePageMapViewModel, modifier: Modifier) {
    RoadmapCanvas(
        plan = state.plan, selectedStepId = state.selectedStepId, selectedRiskId = state.selectedRiskId,
        connectingFromId = state.connectingFromId, interactive = true,
        onSelectStep = actions::selectStep, onMoveStep = actions::moveStep,
        onConnectorClick = actions::beginOrCompleteConnection, onConnectorDrag = actions::completeConnectionDrag,
        onSelectRisk = actions::selectRisk, onMoveRisk = actions::moveRisk,
        onMoveGoal = actions::moveGoal, onConnectGoal = actions::connectToGoal, modifier = modifier,
    )
}

@Composable
private fun EditPanel(state: OnePageMapUiState, actions: OnePageMapViewModel, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(32.dp)).background(RoadmapPalette.Paper).padding(20.dp)) {
        Text("搭路工具", color = RoadmapPalette.Ink, style = PicoTheme.typography.headlineLarge)
        Text("所有命令都可用手柄射线聚焦并按扳机执行", color = RoadmapPalette.Ink, style = PicoTheme.typography.bodySmall)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = actions::addStep, enabled = state.plan.steps.size < 12) { Text("+ 路标") }
            Button(onClick = actions::addRisk) { Text("+ 风险石") }
        }
        Spacer(Modifier.height(16.dp))
        when {
            state.selectedStepId != null -> StepEditor(state, actions)
            state.selectedRiskId != null -> RiskEditor(state, actions)
            else -> ResourceEditor(state, actions)
        }
    }
}

@Composable
private fun StepEditor(state: OnePageMapUiState, actions: OnePageMapViewModel) {
    val step = state.plan.steps.find { it.id == state.selectedStepId } ?: return
    Text("编辑路标", color = RoadmapPalette.Brand, style = PicoTheme.typography.titleLarge)
    Spacer(Modifier.height(10.dp))
    Text("步骤标题", color = RoadmapPalette.Ink, style = PicoTheme.typography.labelLarge)
    TextField(value = step.title, onValueChange = actions::updateStepTitle, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(10.dp))
    Text("状态", color = RoadmapPalette.Ink, style = PicoTheme.typography.labelLarge)
    StepStatus.entries.forEach { status ->
        Button(onClick = { actions.updateStepStatus(status) }, enabled = status != step.status, modifier = Modifier.fillMaxWidth()) { Text(if (status == step.status) "✓ ${status.label}" else status.label) }
        Spacer(Modifier.height(6.dp))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { actions.rotateSelected(-15f) }) { Text("↶ 15°") }
        Button(onClick = { actions.rotateSelected(15f) }) { Text("↷ 15°") }
    }
    Spacer(Modifier.height(8.dp))
    Button(onClick = { actions.beginOrCompleteConnection(step.id) }, modifier = Modifier.fillMaxWidth()) { Text(if (state.connectingFromId == step.id) "取消/换目标" else "从这里拉出连线") }
    Spacer(Modifier.height(8.dp))
    val incidentConnections = state.plan.connections.filter { it.fromId == step.id || it.toId == step.id }
    if (incidentConnections.isNotEmpty()) {
        Text("相连路线", color = RoadmapPalette.Brand, style = PicoTheme.typography.labelLarge)
        incidentConnections.forEach { connection ->
            val from = connectionEndpointLabel(state.plan, connection.fromId)
            val to = connectionEndpointLabel(state.plan, connection.toId)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("$from → $to", color = RoadmapPalette.Ink, style = PicoTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(6.dp))
                Button(onClick = { actions.deleteConnection(connection) }) { Text("删除路线") }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
    Button(onClick = actions::requestDeleteSelected, modifier = Modifier.fillMaxWidth()) { Text("删除路标") }
}

private fun connectionEndpointLabel(plan: RoadmapPlan, id: String): String =
    if (id == GOAL_NODE_ID) "最终目标" else plan.steps.find { it.id == id }?.title ?: "未知路标"

@Composable
private fun RiskEditor(state: OnePageMapUiState, actions: OnePageMapViewModel) {
    val risk = state.plan.risks.find { it.id == state.selectedRiskId } ?: return
    Text("编辑风险石", color = RoadmapPalette.Risk, style = PicoTheme.typography.titleLarge)
    Spacer(Modifier.height(10.dp))
    Text("风险说明", color = RoadmapPalette.Ink, style = PicoTheme.typography.labelLarge)
    TextField(value = risk.title, onValueChange = actions::updateRiskTitle, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(10.dp))
    val near = state.plan.steps.find { it.id == risk.nearStepId }
    Text(near?.let { "当前靠近：${it.title}" } ?: "拖到步骤旁即可建立空间关联", color = RoadmapPalette.Ink, style = PicoTheme.typography.bodyMedium)
    Spacer(Modifier.height(16.dp))
    Button(onClick = actions::deleteSelectedRisk, modifier = Modifier.fillMaxWidth()) { Text("删除风险石") }
}

@Composable
private fun ResourceEditor(state: OnePageMapUiState, actions: OnePageMapViewModel) {
    var draft by remember { mutableStateOf("") }
    Text("脚边工具箱", color = RoadmapPalette.Brand, style = PicoTheme.typography.titleLarge)
    Text("只记资源名称，不做资产管理。", color = RoadmapPalette.Ink, style = PicoTheme.typography.bodySmall)
    Spacer(Modifier.height(10.dp))
    Text("资源", color = RoadmapPalette.Ink, style = PicoTheme.typography.labelLarge)
    TextField(value = draft, onValueChange = { value -> draft = value.take(24) }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("例如：报名表") })
    Spacer(Modifier.height(8.dp))
    Button(onClick = { actions.addResource(draft); if (draft.isNotBlank()) draft = "" }, modifier = Modifier.fillMaxWidth()) { Text("放进工具箱") }
    Spacer(Modifier.height(12.dp))
    state.plan.resources.forEach { item ->
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("• ${item.label}", color = RoadmapPalette.Ink, style = PicoTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Button(onClick = { actions.deleteResource(item.id) }) { Text("移除") }
        }
    }
}

@Composable
private fun SavesScreen(state: OnePageMapUiState, actions: OnePageMapViewModel) {
    Column(Modifier.fillMaxSize().padding(36.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = actions::back) { Text("返回路线") }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text("本地方案", color = PicoTheme.colorScheme.labelPrimary, style = PicoTheme.typography.displaySmall)
                Text("${state.saves.size}/10 份，仅保存在本机", color = PicoTheme.colorScheme.labelSecondary, style = PicoTheme.typography.bodyMedium)
            }
            Button(onClick = actions::saveCurrent, enabled = state.saves.size < 10) { Text("保存当前方案") }
        }
        Spacer(Modifier.height(24.dp))
        if (state.saves.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("保存架还是空的", color = PicoTheme.colorScheme.labelSecondary, style = PicoTheme.typography.headlineLarge) }
        } else LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.saves, key = { it.slotId }) { saved ->
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(RoadmapPalette.Paper).padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(saved.name, color = RoadmapPalette.Ink, style = PicoTheme.typography.titleLarge)
                        Text("${formatTime(saved.savedAt)} · ${saved.plan.steps.size} 步 · ${saved.plan.risks.size} 风险", color = RoadmapPalette.Ink, style = PicoTheme.typography.bodySmall)
                    }
                    Button(onClick = { actions.requestRestore(saved) }) { Text("恢复") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { actions.deleteSave(saved.slotId) }) { Text("删除") }
                }
            }
        }
    }
}

@Composable
private fun ExportScreen(state: OnePageMapUiState, actions: OnePageMapViewModel) {
    Column(Modifier.fillMaxSize().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = actions::back) { Text("返回编辑") }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text("导出预览", color = PicoTheme.colorScheme.labelPrimary, style = PicoTheme.typography.headlineLarge)
                Text("固定构图 1600×900 · 操作手柄已隐藏", color = PicoTheme.colorScheme.labelSecondary, style = PicoTheme.typography.bodyMedium)
            }
            Button(onClick = actions::requestExport) { Text("导出并返回编辑") }
        }
        Spacer(Modifier.height(18.dp))
        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            RoadmapCanvas(
                plan = state.plan, selectedStepId = null, selectedRiskId = null, connectingFromId = null, interactive = false,
                onSelectStep = {}, onMoveStep = { _, _, _ -> }, onConnectorClick = {}, onConnectorDrag = { _, _, _ -> }, onSelectRisk = {}, onMoveRisk = { _, _, _ -> },
                modifier = Modifier.fillMaxHeight().aspectRatio(16f / 9f, matchHeightConstraintsFirst = true),
            )
        }
        state.exportUri?.let { Text("最近导出：$it", color = PicoTheme.colorScheme.labelSecondary, style = PicoTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }
    }
}

@Composable
private fun MessageBanner(message: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.clip(RoundedCornerShape(20.dp)).background(RoadmapPalette.Paper).border(2.dp, RoadmapPalette.Accent, RoundedCornerShape(20.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(message, color = RoadmapPalette.Ink, style = PicoTheme.typography.bodyMedium)
        Spacer(Modifier.width(16.dp))
        Button(onClick = onDismiss) { Text("知道了") }
    }
}

@Composable
private fun PendingDialogView(dialog: PendingDialog, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val title: String
    val body: String
    val confirm: String
    when (dialog) {
        is PendingDialog.DeleteStep -> { title = "删除这个路标？"; body = "同时移除 ${dialog.incidentEdges} 条相连路线，风险石会保留但取消关联。"; confirm = "删除" }
        is PendingDialog.RestorePlan -> { title = "恢复保存方案？"; body = "当前未保存修改会被覆盖。"; confirm = "恢复" }
        is PendingDialog.RecoverDraft -> { title = "发现未完成的小路"; body = "上次退出前的草稿可以继续搭建。"; confirm = "继续编辑" }
        PendingDialog.LeaveDirty -> { title = "离开当前路线？"; body = "未保存修改会被丢弃。"; confirm = "丢弃并离开" }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        content = { Text(body) },
        buttons = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = onDismiss) { Text("取消") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onConfirm) { Text(confirm) }
            }
        },
    )
}

private fun formatTime(time: Long): String = SimpleDateFormat("M月d日 HH:mm", Locale.CHINA).format(Date(time))
