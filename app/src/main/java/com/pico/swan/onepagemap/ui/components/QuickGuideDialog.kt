package com.pico.swan.onepagemap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.Button
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.design.Text

private data class QuickGuidePage(
    val title: String,
    val summary: String,
    val detail: String,
)

private val quickGuidePages = listOf(
    QuickGuidePage(
        title = "先选一条起步小路",
        summary = "第一次使用，推荐“活动筹备”模板。它已经放好 6 张可编辑路标。",
        detail = "手柄射线指向按钮，按扳机选择；所有核心命令都能这样完成。",
    ),
    QuickGuidePage(
        title = "写目标，立旗帜",
        summary = "用一句话写下最终目标，例如“筹备读书会”，再选择“生成目标旗帜”。",
        detail = "目标不能为空。旗帜是终点，之后只需要沿着它铺步骤。",
    ),
    QuickGuidePage(
        title = "铺出六步路线",
        summary = "选择路标可改标题和三态；按住扳机拖动路标，使用 ↶/↷ 按钮旋转。",
        detail = "最多 12 张卡。文字会保持正向，移动卡片时连线会持续跟随。",
    ),
    QuickGuidePage(
        title = "连线、标风险、保存",
        summary = "在起点路标选择“从这里拉出连线”，再选择终点；用“+ 风险石”标在步骤旁。",
        detail = "完成后到“保存与恢复”留存方案，或“导出截图”确认 1600×900 PNG。",
    ),
)

@Composable
fun QuickGuideDialog(
    stepIndex: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onDismiss: () -> Unit,
    onStartEventTemplate: () -> Unit,
    returnToEditor: Boolean = false,
) {
    val safeIndex = stepIndex.coerceIn(quickGuidePages.indices)
    val page = quickGuidePages[safeIndex]
    val isLast = safeIndex == quickGuidePages.lastIndex
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.fillMaxWidth(0.68f).widthIn(max = 820.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(RoadmapPalette.Paper)
                .border(2.dp, RoadmapPalette.Accent, RoundedCornerShape(32.dp))
                .padding(30.dp),
        ) {
            Text("快速上手 · ${safeIndex + 1}/${quickGuidePages.size}", color = RoadmapPalette.Brand, style = PicoTheme.typography.titleLarge)
            Spacer(Modifier.height(18.dp))
            Text(page.title, color = RoadmapPalette.Ink, style = PicoTheme.typography.headlineLarge)
            Spacer(Modifier.height(12.dp))
            Text(page.summary, color = RoadmapPalette.Ink, style = PicoTheme.typography.bodyLarge)
            Spacer(Modifier.height(10.dp))
            Text(page.detail, color = RoadmapPalette.Ink, style = PicoTheme.typography.bodyMedium)
            Spacer(Modifier.height(26.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = onDismiss) {
                    Text(if (returnToEditor) "返回当前路线" else if (isLast) "留在模板页" else "跳过")
                }
                if (safeIndex > 0) {
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onPrevious) { Text("上一步") }
                }
                Spacer(Modifier.width(8.dp))
                if (isLast) {
                    Button(onClick = if (returnToEditor) onDismiss else onStartEventTemplate) {
                        Text(if (returnToEditor) "返回继续编辑" else "用活动筹备开始")
                    }
                } else {
                    Button(onClick = onNext) { Text("下一步") }
                }
            }
        }
    }
}
