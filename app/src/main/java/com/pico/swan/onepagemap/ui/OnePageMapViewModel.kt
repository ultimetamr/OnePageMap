package com.pico.swan.onepagemap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pico.swan.onepagemap.data.RoadmapRepository
import com.pico.swan.onepagemap.domain.model.*
import com.pico.swan.onepagemap.domain.usecase.RoadmapRules
import com.pico.swan.onepagemap.domain.usecase.RuleResult
import com.pico.swan.onepagemap.platform.DemoLaunch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import kotlin.math.pow

class OnePageMapViewModel(private val repository: RoadmapRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<OnePageMapUiState> = _uiState.asStateFlow()

    private fun initialState(): OnePageMapUiState {
        val demo = DemoLaunch.mode
        if (demo == "guide") {
            return OnePageMapUiState(screen = AppScreen.GUIDE, saves = repository.loadSaves())
        }
        if (demo == "editor" || demo == "export") {
            val sample = readingClubSample()
            if (demo == "export") runCatching { repository.export(sample) }
            val evidenceUri = runCatching { repository.exportEvidence(sample, demo) }.getOrNull()
            return OnePageMapUiState(screen = if (demo == "export") AppScreen.EXPORT else AppScreen.EDITOR, plan = sample, dirty = false, saves = repository.loadSaves(), exportUri = evidenceUri)
        }
        val recovered = repository.loadDraft()?.takeIf { it.goal.isNotBlank() }
        return OnePageMapUiState(
            screen = if (recovered == null && !repository.isQuickGuideSeen()) AppScreen.GUIDE else AppScreen.TEMPLATE,
            saves = repository.loadSaves(),
            pendingDialog = recovered?.let(PendingDialog::RecoverDraft),
        )
    }

    fun chooseTemplate(template: RoadmapTemplate) {
        _uiState.value = OnePageMapUiState(screen = AppScreen.GOAL, selectedTemplate = template, plan = templatePlan(template), saves = repository.loadSaves())
    }

    fun updateGoal(value: String) = _uiState.update { it.copy(plan = it.plan.copy(goal = value), message = null) }

    fun confirmGoal() {
        when (val result = RoadmapRules.validateGoal(_uiState.value.plan.goal)) {
            is RuleResult.Error -> message(result.message)
            is RuleResult.Ok -> mutate(_uiState.value.plan.copy(goal = result.value), screen = AppScreen.EDITOR)
        }
    }

    fun addStep() {
        val state = _uiState.value
        if (state.plan.steps.size >= RoadmapRules.MAX_STEPS) return message("最多只能放 12 张步骤卡")
        val index = state.plan.steps.size
        val step = Step(
            id = UUID.randomUUID().toString(), title = "新步骤 ${index + 1}",
            position = Point2(.14f + (index % 4) * .22f, .22f + (index / 4) * .25f),
        )
        mutate(state.plan.copy(steps = state.plan.steps + step), selectedStepId = step.id, editorPanel = EditorPanel.STEP)
    }

    fun selectStep(id: String) = _uiState.update { it.copy(selectedStepId = id, selectedRiskId = null, editorPanel = EditorPanel.STEP) }
    fun updateStepTitle(value: String) = updateSelectedStep { it.copy(title = value.take(28)) }
    fun updateStepStatus(status: StepStatus) = updateSelectedStep { it.copy(status = status) }
    fun rotateSelected(delta: Float) = updateSelectedStep { it.copy(rotation = normalizeRotation(it.rotation + delta)) }
    fun moveStep(id: String, dx: Float, dy: Float) = updateStep(id) { it.copy(position = Point2((it.position.x + dx).coerceIn(.06f, .94f), (it.position.y + dy).coerceIn(.12f, .78f))) }

    fun moveGoal(dx: Float, dy: Float) = mutate(
        _uiState.value.plan.copy(
            goalPosition = Point2(
                (_uiState.value.plan.goalPosition.x + dx).coerceIn(.12f, .88f),
                (_uiState.value.plan.goalPosition.y + dy).coerceIn(.08f, .88f),
            )
        )
    )

    fun connectToGoal() {
        val from = _uiState.value.connectingFromId ?: return message("请先从一个路标拉出连线")
        completeConnection(from, GOAL_NODE_ID)
    }

    fun requestDeleteSelected() {
        val id = _uiState.value.selectedStepId ?: return
        val edges = _uiState.value.plan.connections.count { it.fromId == id || it.toId == id }
        _uiState.update { it.copy(pendingDialog = PendingDialog.DeleteStep(id, edges)) }
    }

    fun beginOrCompleteConnection(stepId: String) {
        val from = _uiState.value.connectingFromId
        if (from == null) {
            _uiState.update { it.copy(connectingFromId = stepId, message = "已抓住连线端点，请指向另一个路标") }
            return
        }
        completeConnection(from, stepId)
    }

    fun completeConnectionDrag(fromId: String, targetX: Float, targetY: Float) {
        val goal = _uiState.value.plan.goalPosition
        if ((goal.x - targetX).pow(2) + (goal.y - targetY).pow(2) <= .045f) {
            completeConnection(fromId, GOAL_NODE_ID)
            return
        }
        val target = _uiState.value.plan.steps.filterNot { it.id == fromId }.minByOrNull {
            (it.position.x - targetX).pow(2) + (it.position.y - targetY).pow(2)
        }
        if (target == null || (target.position.x - targetX).pow(2) + (target.position.y - targetY).pow(2) > .035f) {
            _uiState.update { it.copy(connectingFromId = null, message = "没有落在路标上，连线已取消") }
        } else completeConnection(fromId, target.id)
    }

    private fun completeConnection(fromId: String, toId: String) {
        when (val result = RoadmapRules.addConnection(_uiState.value.plan, fromId, toId)) {
            is RuleResult.Error -> _uiState.update { it.copy(connectingFromId = null, message = result.message) }
            is RuleResult.Ok -> mutate(result.value, connectingFromId = null, message = "前后关系已连接")
        }
    }

    fun deleteConnection(connection: Connection) {
        val state = _uiState.value
        if (connection !in state.plan.connections) return
        mutate(
            state.plan.copy(connections = state.plan.connections - connection),
            connectingFromId = null,
            message = "路线已删除",
        )
    }

    fun addRisk() {
        val index = _uiState.value.plan.risks.size
        val risk = Risk(UUID.randomUUID().toString(), "新风险 ${index + 1}", Point2(.28f + index * .12f, .82f))
        mutate(_uiState.value.plan.copy(risks = _uiState.value.plan.risks + risk), selectedRiskId = risk.id, selectedStepId = null, editorPanel = EditorPanel.RISK)
    }

    fun selectRisk(id: String) = _uiState.update { it.copy(selectedRiskId = id, selectedStepId = null, editorPanel = EditorPanel.RISK) }
    fun updateRiskTitle(value: String) = updateSelectedRisk { it.copy(title = value.take(28)) }
    fun moveRisk(id: String, dx: Float, dy: Float) {
        val plan = _uiState.value.plan
        val risk = plan.risks.find { it.id == id } ?: return
        val point = Point2((risk.position.x + dx).coerceIn(.05f, .95f), (risk.position.y + dy).coerceIn(.05f, .95f))
        val near = plan.steps.minByOrNull { (it.position.x - point.x).pow(2) + (it.position.y - point.y).pow(2) }
        updateRisk(id) { it.copy(position = point, nearStepId = near?.id) }
    }
    fun deleteSelectedRisk() {
        val id = _uiState.value.selectedRiskId ?: return
        mutate(_uiState.value.plan.copy(risks = _uiState.value.plan.risks.filterNot { it.id == id }), selectedRiskId = null, editorPanel = EditorPanel.OVERVIEW)
    }

    fun addResource(label: String) {
        if (label.isBlank()) return message("资源名称不能为空")
        val item = Resource(UUID.randomUUID().toString(), label.trim().take(24))
        mutate(_uiState.value.plan.copy(resources = _uiState.value.plan.resources + item), editorPanel = EditorPanel.RESOURCE)
    }
    fun deleteResource(id: String) = mutate(_uiState.value.plan.copy(resources = _uiState.value.plan.resources.filterNot { it.id == id }))

    fun openSaves() = _uiState.update { it.copy(screen = AppScreen.SAVES, saves = repository.loadSaves(), message = null) }
    fun saveCurrent() {
        val state = _uiState.value
        if (state.saves.size >= RoadmapRules.MAX_SAVES) return message("本地最多保存 10 份方案，请先删除一份")
        repository.save(state.plan)
        repository.clearDraft()
        _uiState.update { it.copy(saves = repository.loadSaves(), dirty = false, message = "方案已保存到本地") }
    }
    fun requestRestore(saved: SavedPlan) {
        if (_uiState.value.dirty) _uiState.update { it.copy(pendingDialog = PendingDialog.RestorePlan(saved)) }
        else restore(saved.plan)
    }
    fun deleteSave(slotId: String) { repository.deleteSave(slotId); _uiState.update { it.copy(saves = repository.loadSaves()) } }

    fun openExport() {
        if (_uiState.value.plan.goal.isBlank()) return message("目标为空，无法导出")
        _uiState.update { it.copy(screen = AppScreen.EXPORT, message = null) }
    }
    fun requestExport() {
        runCatching { repository.export(_uiState.value.plan) }
            .onSuccess { uri ->
                _uiState.update {
                    it.copy(
                        screen = AppScreen.EDITOR,
                        exportUri = uri,
                        message = "截图已保存到 Pictures/OnePageMap，已返回编辑",
                    )
                }
            }
            .onFailure { error ->
                _uiState.update { it.copy(message = "导出失败：${error.message ?: "未知错误"}") }
            }
    }
    fun confirmDialog() {
        when (val dialog = _uiState.value.pendingDialog) {
            is PendingDialog.DeleteStep -> mutate(RoadmapRules.deleteStep(_uiState.value.plan, dialog.stepId), selectedStepId = null, editorPanel = EditorPanel.OVERVIEW, pendingDialog = null, message = "路标和 ${dialog.incidentEdges} 条关联线已删除")
            is PendingDialog.RestorePlan -> restore(dialog.saved.plan)
            is PendingDialog.RecoverDraft -> restore(dialog.plan)
            PendingDialog.LeaveDirty -> { repository.clearDraft(); _uiState.value = OnePageMapUiState(saves = repository.loadSaves()) }
            null -> Unit
        }
    }
    fun dismissDialog() {
        val dialog = _uiState.value.pendingDialog
        if (dialog is PendingDialog.RecoverDraft) repository.clearDraft()
        _uiState.update { it.copy(pendingDialog = null) }
    }

    fun back() {
        val state = _uiState.value
        when (state.screen) {
            AppScreen.GUIDE -> dismissQuickGuide()
            AppScreen.TEMPLATE -> Unit
            AppScreen.GOAL -> _uiState.value = OnePageMapUiState(saves = repository.loadSaves())
            AppScreen.EDITOR -> if (state.dirty) _uiState.update { it.copy(pendingDialog = PendingDialog.LeaveDirty) } else _uiState.value = OnePageMapUiState(saves = repository.loadSaves())
            AppScreen.SAVES, AppScreen.EXPORT -> _uiState.update { it.copy(screen = AppScreen.EDITOR, message = null) }
        }
    }
    fun clearMessage() = _uiState.update { it.copy(message = null) }

    fun openQuickGuide() = _uiState.update {
        it.copy(screen = AppScreen.GUIDE, quickGuideStep = 0, quickGuideReturnScreen = it.screen)
    }

    fun previousQuickGuide() = _uiState.update { state ->
        state.copy(quickGuideStep = (state.quickGuideStep - 1).coerceAtLeast(0))
    }

    fun nextQuickGuide() = _uiState.update { state ->
        state.copy(quickGuideStep = (state.quickGuideStep + 1).coerceAtMost(QUICK_GUIDE_LAST_STEP))
    }

    fun dismissQuickGuide() {
        repository.setQuickGuideSeen()
        _uiState.update {
            it.copy(
                screen = it.quickGuideReturnScreen ?: AppScreen.TEMPLATE,
                quickGuideStep = 0,
                quickGuideReturnScreen = null,
            )
        }
    }

    fun startEventTemplateFromQuickGuide() {
        repository.setQuickGuideSeen()
        chooseTemplate(RoadmapTemplate.EVENT)
    }

    private fun restore(plan: RoadmapPlan) {
        repository.storeDraft(plan)
        _uiState.update { it.copy(screen = AppScreen.EDITOR, plan = plan, selectedStepId = null, selectedRiskId = null, editorPanel = EditorPanel.OVERVIEW, pendingDialog = null, dirty = false, message = "方案已恢复") }
    }

    private fun updateSelectedStep(block: (Step) -> Step) { _uiState.value.selectedStepId?.let { updateStep(it, block) } }
    private fun updateStep(id: String, block: (Step) -> Step) = mutate(_uiState.value.plan.copy(steps = _uiState.value.plan.steps.map { if (it.id == id) block(it) else it }))
    private fun updateSelectedRisk(block: (Risk) -> Risk) { _uiState.value.selectedRiskId?.let { updateRisk(it, block) } }
    private fun updateRisk(id: String, block: (Risk) -> Risk) = mutate(_uiState.value.plan.copy(risks = _uiState.value.plan.risks.map { if (it.id == id) block(it) else it }))

    private fun mutate(
        plan: RoadmapPlan,
        screen: AppScreen = _uiState.value.screen,
        selectedStepId: String? = _uiState.value.selectedStepId,
        selectedRiskId: String? = _uiState.value.selectedRiskId,
        editorPanel: EditorPanel = _uiState.value.editorPanel,
        connectingFromId: String? = _uiState.value.connectingFromId,
        pendingDialog: PendingDialog? = _uiState.value.pendingDialog,
        message: String? = _uiState.value.message,
    ) {
        val updated = plan.copy(updatedAt = System.currentTimeMillis())
        repository.storeDraft(updated)
        _uiState.update { it.copy(plan = updated, screen = screen, selectedStepId = selectedStepId, selectedRiskId = selectedRiskId, editorPanel = editorPanel, connectingFromId = connectingFromId, pendingDialog = pendingDialog, dirty = true, message = message) }
    }

    private fun message(text: String) = _uiState.update { it.copy(message = text) }
    private fun normalizeRotation(value: Float): Float = ((value + 180f) % 360f + 360f) % 360f - 180f

    private companion object { const val QUICK_GUIDE_LAST_STEP = 3 }

    class Factory(private val repository: RoadmapRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = OnePageMapViewModel(repository) as T
    }
}

private fun templatePlan(template: RoadmapTemplate): RoadmapPlan {
    val titles = when (template) {
        RoadmapTemplate.BLANK -> emptyList()
        RoadmapTemplate.EVENT -> listOf("明确主题", "确认场地", "邀请参与者", "准备物料", "现场执行", "整理回顾")
        RoadmapTemplate.STUDY -> listOf("明确成果", "收集资料", "拆分章节", "开始练习", "阶段复盘", "完成作品")
        RoadmapTemplate.LAUNCH -> listOf("确认范围", "完成核心功能", "自测修正", "准备介绍页", "小范围发布", "正式上线")
    }
    val steps = titles.mapIndexed { index, title -> Step("template-$index", title, position = Point2(.14f + (index % 3) * .30f, .25f + (index / 3) * .28f)) }
    return RoadmapPlan(id = UUID.randomUUID().toString(), steps = steps)
}

fun readingClubSample(): RoadmapPlan {
    val titles = listOf("确定主题", "选定书目", "确认场地", "发布报名", "准备引导问题", "举办读书会")
    val steps = titles.mapIndexed { i, title -> Step("book-$i", title, if (i < 2) StepStatus.COMPLETED else if (i == 2) StepStatus.IN_PROGRESS else StepStatus.NOT_STARTED, Point2(.13f + (i % 3) * .31f, .25f + (i / 3) * .29f), if (i % 2 == 0) -4f else 4f) }
    return RoadmapPlan(
        id = "reading-club", goal = "筹备读书会", steps = steps,
        connections = listOf(Connection("book-0", "book-1"), Connection("book-2", "book-3"), Connection("book-5", GOAL_NODE_ID)),
        risks = listOf(Risk("risk-venue", "场地临时变更", Point2(.54f, .78f), "book-2")),
        resources = listOf(Resource("res-1", "场地联系人"), Resource("res-2", "报名表"), Resource("res-3", "讨论提纲")),
    )
}
