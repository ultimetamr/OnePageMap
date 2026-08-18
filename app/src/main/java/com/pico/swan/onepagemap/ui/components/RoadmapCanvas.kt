package com.pico.swan.onepagemap.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.foundation.haptic.controllerHapticFeedback
import com.pico.spatial.ui.foundation.hover.spatialHoverEffect
import com.pico.swan.onepagemap.domain.model.*
import kotlin.math.roundToInt

@Composable
fun RoadmapCanvas(
    plan: RoadmapPlan,
    selectedStepId: String?,
    selectedRiskId: String?,
    connectingFromId: String?,
    interactive: Boolean,
    onSelectStep: (String) -> Unit,
    onMoveStep: (String, Float, Float) -> Unit,
    onConnectorClick: (String) -> Unit,
    onConnectorDrag: (String, Float, Float) -> Unit,
    onSelectRisk: (String) -> Unit,
    onMoveRisk: (String, Float, Float) -> Unit,
    onMoveGoal: (Float, Float) -> Unit = { _, _ -> },
    onConnectGoal: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var dragPreview by remember { mutableStateOf<ConnectionPreview?>(null) }
    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .background(RoadmapPalette.Terrain)
            .border(1.dp, RoadmapPalette.TerrainLine, RoundedCornerShape(32.dp))
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        Canvas(Modifier.fillMaxSize()) {
            plan.connections.forEachIndexed { index, edge ->
                val from = plan.steps.find { it.id == edge.fromId } ?: return@forEachIndexed
                val toPosition = if (edge.toId == GOAL_NODE_ID) plan.goalPosition else plan.steps.find { it.id == edge.toId }?.position ?: return@forEachIndexed
                val startX = from.position.x * size.width
                val startY = from.position.y * size.height
                val endX = toPosition.x * size.width
                val endY = toPosition.y * size.height
                val offset = ((index % 3) - 1) * 18.dp.toPx()
                val curve = Path().apply {
                    moveTo(startX, startY)
                    cubicTo((startX + endX) / 2f, startY + offset, (startX + endX) / 2f, endY - offset, endX, endY)
                }
                drawPath(curve, RoadmapPalette.Path, style = Stroke(width = if (interactive) 4.dp.toPx() else 5.dp.toPx(), cap = StrokeCap.Round))
                val arrow = Path().apply {
                    moveTo(endX, endY)
                    lineTo(endX - 18.dp.toPx(), endY - 9.dp.toPx())
                    lineTo(endX - 18.dp.toPx(), endY + 9.dp.toPx())
                    close()
                }
                drawPath(arrow, RoadmapPalette.Path)
            }
            dragPreview?.let { preview ->
                val from = plan.steps.find { it.id == preview.fromId } ?: return@let
                val startX = from.position.x * size.width
                val startY = from.position.y * size.height
                val endX = preview.position.x * size.width
                val endY = preview.position.y * size.height
                val curve = Path().apply {
                    moveTo(startX, startY)
                    cubicTo((startX + endX) / 2f, startY, (startX + endX) / 2f, endY, endX, endY)
                }
                drawPath(
                    curve,
                    if (preview.snapped) RoadmapPalette.Completed else RoadmapPalette.Accent,
                    style = Stroke(
                        width = 5.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 10.dp.toPx())),
                    ),
                )
            }
        }

        GoalDestinationCard(
            goal = plan.goal,
            connecting = connectingFromId != null || dragPreview?.targetId == GOAL_NODE_ID,
            interactive = interactive,
            onMove = { dx, dy -> onMoveGoal(dx / widthPx, dy / heightPx) },
            onConnect = onConnectGoal,
            modifier = Modifier.offset {
                IntOffset(
                    (plan.goalPosition.x * widthPx - with(density) { 190.dp.toPx() }).roundToInt(),
                    (plan.goalPosition.y * heightPx - with(density) { 54.dp.toPx() }).roundToInt(),
                )
            },
        )

        plan.steps.forEachIndexed { index, step ->
            key(step.id) {
                StepSign(
                    step = step,
                    number = index + 1,
                    selected = selectedStepId == step.id,
                    connecting = connectingFromId == step.id || dragPreview?.targetId == step.id,
                    interactive = interactive,
                    onSelect = { onSelectStep(step.id) },
                    onMove = { dx, dy -> onMoveStep(step.id, dx / widthPx, dy / heightPx) },
                    onConnectorClick = { onConnectorClick(step.id) },
                    onConnectorDragPreview = { dx, dy ->
                        val position = Point2(step.position.x + dx / widthPx, step.position.y + dy / heightPx)
                        val targetId = connectionTargetAt(plan, step.id, position)
                        dragPreview = ConnectionPreview(step.id, position, targetId, targetId != null)
                    },
                    onConnectorDrag = { dx, dy ->
                        dragPreview = null
                        onConnectorDrag(step.id, step.position.x + dx / widthPx, step.position.y + dy / heightPx)
                    },
                    onConnectorDragCancel = { dragPreview = null },
                    modifier = Modifier.offset {
                        IntOffset(
                            (step.position.x * widthPx - with(density) { 96.dp.toPx() }).roundToInt(),
                            (step.position.y * heightPx - with(density) { 60.dp.toPx() }).roundToInt(),
                        )
                    }
                )
            }
        }

        plan.risks.forEach { risk ->
            key(risk.id) {
                val nearTitle = plan.steps.find { it.id == risk.nearStepId }?.title
                RiskStone(
                    risk = risk,
                    nearTitle = nearTitle,
                    selected = selectedRiskId == risk.id,
                    interactive = interactive,
                    onSelect = { onSelectRisk(risk.id) },
                    onMove = { dx, dy -> onMoveRisk(risk.id, dx / widthPx, dy / heightPx) },
                    modifier = Modifier.offset {
                        IntOffset(
                            (risk.position.x * widthPx - with(density) { 76.dp.toPx() }).roundToInt(),
                            (risk.position.y * heightPx - with(density) { 52.dp.toPx() }).roundToInt(),
                        )
                    }
                )
            }
        }

        Toolbox(plan.resources, Modifier.align(Alignment.BottomStart).padding(24.dp))
        Text(
            text = if (interactive) "抓取路标移动 · 连接点拉线 · 整张路标可旋转" else "固定构图 · 1600×900",
            color = RoadmapPalette.Path,
            style = PicoTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
        )
    }
}

@Composable
private fun GoalDestinationCard(
    goal: String,
    connecting: Boolean,
    interactive: Boolean,
    onMove: (Float, Float) -> Unit,
    onConnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    Row(
        modifier.width(380.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(RoadmapPalette.Paper)
            .border(if (connecting) 4.dp else 3.dp, if (connecting) RoadmapPalette.Completed else RoadmapPalette.Accent, RoundedCornerShape(24.dp))
            .then(if (interactive) Modifier.spatialHoverEffect().controllerHapticFeedback(interactionSource = interaction).clickable(interactionSource = interaction, indication = LocalIndication.current, onClick = onConnect).pointerInput(GOAL_NODE_ID) {
                detectDragGestures { change, amount -> change.consume(); onMove(amount.x, amount.y) }
            } else Modifier)
            .padding(end = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.width(64.dp).height(104.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.width(8.dp).height(76.dp).background(RoadmapPalette.Brand))
            Text("⚑", color = RoadmapPalette.Accent, style = PicoTheme.typography.displayMedium, modifier = Modifier.align(Alignment.TopCenter))
        }
        Column(Modifier.padding(vertical = 16.dp)) {
            Text(if (connecting) "松开/点击连接到终点" else "终点 · 最终目标", color = RoadmapPalette.Brand, style = PicoTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Text(goal.ifBlank { "先写下目标" }, color = RoadmapPalette.Ink, style = PicoTheme.typography.titleLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun StepSign(
    step: Step,
    number: Int,
    selected: Boolean,
    connecting: Boolean,
    interactive: Boolean,
    onSelect: () -> Unit,
    onMove: (Float, Float) -> Unit,
    onConnectorClick: () -> Unit,
    onConnectorDragPreview: (Float, Float) -> Unit,
    onConnectorDrag: (Float, Float) -> Unit,
    onConnectorDragCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    Box(modifier.size(192.dp, 120.dp).graphicsLayer { rotationZ = step.rotation }) {
        Box(
            Modifier.fillMaxSize().padding(5.dp)
                .background(RoadmapPalette.Shadow, RoundedCornerShape(20.dp))
        )
        Column(
            Modifier.fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(RoadmapPalette.Paper)
                .border(if (selected || connecting) 3.dp else 2.dp, if (selected || connecting) RoadmapPalette.Accent else statusColor(step.status), RoundedCornerShape(20.dp))
                .then(if (interactive) Modifier.spatialHoverEffect().controllerHapticFeedback(interactionSource = interaction).clickable(interactionSource = interaction, indication = LocalIndication.current, onClick = onSelect).pointerInput(step.id) {
                    detectDragGestures { change, amount -> change.consume(); onMove(amount.x, amount.y) }
                } else Modifier)
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(28.dp).background(statusColor(step.status), CircleShape), contentAlignment = Alignment.Center) {
                    Text(statusSymbol(step.status), color = RoadmapPalette.White, style = PicoTheme.typography.labelMedium)
                }
                Spacer(Modifier.width(8.dp))
                Text("$number. ${step.title.ifBlank { "未命名步骤" }}", color = RoadmapPalette.Ink, style = PicoTheme.typography.bodyLarge, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${step.status.label} · ${step.rotation.toInt()}°", color = RoadmapPalette.Ink, style = PicoTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                if (interactive) ConnectorHandle(onClick = onConnectorClick, onDragPreview = onConnectorDragPreview, onDrag = onConnectorDrag, onDragCancel = onConnectorDragCancel)
            }
        }
    }
}

@Composable
private fun ConnectorHandle(onClick: () -> Unit, onDragPreview: (Float, Float) -> Unit, onDrag: (Float, Float) -> Unit, onDragCancel: () -> Unit) {
    val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    Box(
        Modifier.size(40.dp).clip(CircleShape).background(RoadmapPalette.Brand)
            .spatialHoverEffect().controllerHapticFeedback(interactionSource = interaction)
            .clickable(interactionSource = interaction, indication = LocalIndication.current, onClick = onClick)
            .pointerInput(Unit) {
                var totalX = 0f; var totalY = 0f
                detectDragGestures(
                    onDragStart = { totalX = 0f; totalY = 0f },
                    onDragEnd = { onDrag(totalX, totalY) },
                    onDragCancel = onDragCancel,
                    onDrag = { change, amount ->
                        change.consume(); totalX += amount.x; totalY += amount.y
                        onDragPreview(totalX, totalY)
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) { Text("→", color = RoadmapPalette.White, style = PicoTheme.typography.labelLarge) }
}

private data class ConnectionPreview(
    val fromId: String,
    val position: Point2,
    val targetId: String?,
    val snapped: Boolean,
)

private fun connectionTargetAt(plan: RoadmapPlan, fromId: String, point: Point2): String? {
    val goal = plan.goalPosition
    if ((goal.x - point.x) * (goal.x - point.x) + (goal.y - point.y) * (goal.y - point.y) <= .045f) return GOAL_NODE_ID
    return plan.steps.filterNot { it.id == fromId }.minByOrNull {
        (it.position.x - point.x) * (it.position.x - point.x) + (it.position.y - point.y) * (it.position.y - point.y)
    }?.takeIf {
        (it.position.x - point.x) * (it.position.x - point.x) + (it.position.y - point.y) * (it.position.y - point.y) <= .035f
    }?.id
}

@Composable
private fun RiskStone(
    risk: Risk,
    nearTitle: String?,
    selected: Boolean,
    interactive: Boolean,
    onSelect: () -> Unit,
    onMove: (Float, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    Column(
        modifier.size(152.dp, 104.dp).clip(RiskStoneShape).background(RoadmapPalette.Risk)
            .border(if (selected) 3.dp else 2.dp, if (selected) RoadmapPalette.Accent else RoadmapPalette.RiskDark, RiskStoneShape)
            .then(if (interactive) Modifier.spatialHoverEffect().controllerHapticFeedback(interactionSource = interaction).clickable(interactionSource = interaction, indication = LocalIndication.current, onClick = onSelect).pointerInput(risk.id) {
                detectDragGestures { change, amount -> change.consume(); onMove(amount.x, amount.y) }
            } else Modifier)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("▲ 风险", color = RoadmapPalette.White, style = PicoTheme.typography.labelLarge)
        Text(risk.title.ifBlank { "未命名风险" }, color = RoadmapPalette.White, style = PicoTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(nearTitle?.let { "靠近：$it" } ?: "未关联步骤", color = RoadmapPalette.White, style = PicoTheme.typography.bodySmall, maxLines = 1)
    }
}

@Composable
private fun Toolbox(resources: List<Resource>, modifier: Modifier = Modifier) {
    Column(modifier.widthIn(max = 520.dp).background(RoadmapPalette.Paper, RoundedCornerShape(24.dp)).padding(horizontal = 18.dp, vertical = 12.dp)) {
        Text("🧰 资源工具箱", color = RoadmapPalette.Ink, style = PicoTheme.typography.labelLarge)
        Text(resources.joinToString(" · ") { it.label }.ifBlank { "还没有资源" }, color = RoadmapPalette.Ink, style = PicoTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

private fun statusColor(status: StepStatus) = when (status) {
    StepStatus.NOT_STARTED -> RoadmapPalette.NotStarted
    StepStatus.IN_PROGRESS -> RoadmapPalette.InProgress
    StepStatus.COMPLETED -> RoadmapPalette.Completed
}

private fun statusSymbol(status: StepStatus) = when (status) {
    StepStatus.NOT_STARTED -> "○"
    StepStatus.IN_PROGRESS -> "◌"
    StepStatus.COMPLETED -> "■"
}
