package com.pico.swan.onepagemap.ui

import com.pico.swan.onepagemap.data.RoadmapRepository
import com.pico.swan.onepagemap.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class OnePageMapViewModelTest {
    @Test fun firstRunShowsQuickGuide() {
        val vm = OnePageMapViewModel(FakeRepository())
        assertEquals(AppScreen.GUIDE, vm.uiState.value.screen)
    }

    @Test fun quickGuideStartsRecommendedTemplateAndPersistsCompletion() {
        val repository = FakeRepository()
        val vm = OnePageMapViewModel(repository)
        repeat(3) { vm.nextQuickGuide() }
        vm.startEventTemplateFromQuickGuide()
        assertTrue(repository.quickGuideSeen)
        assertEquals(AppScreen.GOAL, vm.uiState.value.screen)
        assertEquals(RoadmapTemplate.EVENT, vm.uiState.value.selectedTemplate)
        assertEquals(6, vm.uiState.value.plan.steps.size)
        assertEquals(0, vm.uiState.value.quickGuideStep)
    }

    @Test fun completedGuideCanBeOpenedAgain() {
        val vm = OnePageMapViewModel(FakeRepository(quickGuideSeen = true))
        assertEquals(AppScreen.TEMPLATE, vm.uiState.value.screen)
        vm.openQuickGuide()
        assertEquals(AppScreen.GUIDE, vm.uiState.value.screen)
        assertEquals(0, vm.uiState.value.quickGuideStep)
    }

    @Test fun helpReturnsToUnsavedEditorWithoutLosingWork() {
        val vm = readyViewModel()
        vm.addStep()
        val before = vm.uiState.value

        vm.openQuickGuide()
        vm.nextQuickGuide()
        vm.dismissQuickGuide()

        val after = vm.uiState.value
        assertEquals(AppScreen.EDITOR, after.screen)
        assertEquals(before.plan, after.plan)
        assertEquals(before.selectedStepId, after.selectedStepId)
        assertEquals(before.dirty, after.dirty)
        assertNull(after.quickGuideReturnScreen)
    }

    @Test fun backFromHelpAlsoReturnsToUnsavedEditor() {
        val vm = readyViewModel()
        vm.addRisk()
        val before = vm.uiState.value

        vm.openQuickGuide()
        vm.back()

        assertEquals(AppScreen.EDITOR, vm.uiState.value.screen)
        assertEquals(before.plan, vm.uiState.value.plan)
        assertEquals(before.selectedRiskId, vm.uiState.value.selectedRiskId)
        assertTrue(vm.uiState.value.dirty)
    }

    @Test fun completingHelpFromEditorReturnsToExistingPlanInsteadOfCreatingTemplate() {
        val vm = readyViewModel()
        vm.addStep()
        vm.updateStepTitle("保留这个未保存步骤")
        val before = vm.uiState.value.plan

        vm.openQuickGuide()
        repeat(3) { vm.nextQuickGuide() }
        vm.dismissQuickGuide()

        assertEquals(AppScreen.EDITOR, vm.uiState.value.screen)
        assertEquals(before, vm.uiState.value.plan)
        assertEquals(RoadmapTemplate.BLANK, vm.uiState.value.selectedTemplate)
        assertTrue(vm.uiState.value.dirty)
    }

    @Test fun blankGoalDoesNotEnterEditor() {
        val vm = OnePageMapViewModel(FakeRepository())
        vm.chooseTemplate(RoadmapTemplate.BLANK)
        vm.confirmGoal()
        assertEquals(AppScreen.GOAL, vm.uiState.value.screen)
        assertNotNull(vm.uiState.value.message)
    }

    @Test fun stepCountStopsAtTwelve() {
        val vm = readyViewModel()
        repeat(14) { vm.addStep() }
        assertEquals(12, vm.uiState.value.plan.steps.size)
        assertTrue(vm.uiState.value.message?.contains("12") == true)
    }

    @Test fun riskCanMoveAcrossTheWholeCanvas() {
        val vm = readyViewModel()
        vm.addRisk()
        val riskId = requireNotNull(vm.uiState.value.selectedRiskId)
        vm.moveRisk(riskId, 0f, -1f)
        assertEquals(.05f, vm.uiState.value.plan.risks.single().position.y, .001f)
        vm.moveRisk(riskId, 0f, 2f)
        assertEquals(.95f, vm.uiState.value.plan.risks.single().position.y, .001f)
    }

    @Test fun exportFromPreviewSavesAndReturnsToEditor() {
        val repository = FakeRepository()
        val vm = readyViewModel(repository)
        vm.openExport()
        assertEquals(AppScreen.EXPORT, vm.uiState.value.screen)
        vm.requestExport()
        assertEquals(1, repository.exportCount)
        assertEquals(AppScreen.EDITOR, vm.uiState.value.screen)
        assertTrue(vm.uiState.value.message?.contains("Pictures/OnePageMap") == true)
    }

    @Test fun goalCanMoveAndReceiveAConnection() {
        val vm = readyViewModel()
        val stepId = vm.uiState.value.plan.steps.singleOrNull()?.id ?: run {
            vm.addStep(); requireNotNull(vm.uiState.value.selectedStepId)
        }
        vm.moveGoal(-.3f, .4f)
        assertEquals(.52f, vm.uiState.value.plan.goalPosition.x, .001f)
        assertEquals(.50f, vm.uiState.value.plan.goalPosition.y, .001f)
        vm.beginOrCompleteConnection(stepId)
        vm.connectToGoal()
        assertEquals(Connection(stepId, GOAL_NODE_ID), vm.uiState.value.plan.connections.single())
    }

    @Test fun duplicateConnectionShowsReasonAndKeepsOneEdge() {
        val vm = OnePageMapViewModel(FakeRepository())
        vm.chooseTemplate(RoadmapTemplate.EVENT); vm.updateGoal("目标"); vm.confirmGoal()
        val ids = vm.uiState.value.plan.steps.take(2).map { it.id }
        vm.beginOrCompleteConnection(ids[0]); vm.beginOrCompleteConnection(ids[1])
        vm.beginOrCompleteConnection(ids[0]); vm.beginOrCompleteConnection(ids[1])
        assertEquals(1, vm.uiState.value.plan.connections.size)
        assertTrue(vm.uiState.value.message?.contains("已经存在") == true)
    }

    @Test fun connectionCanBeDeletedWithoutDeletingItsStepsOrGoal() {
        val vm = OnePageMapViewModel(FakeRepository())
        vm.chooseTemplate(RoadmapTemplate.EVENT); vm.updateGoal("保留的目标"); vm.confirmGoal()
        val stepsBefore = vm.uiState.value.plan.steps
        val edge = Connection(stepsBefore[0].id, stepsBefore[1].id)
        vm.beginOrCompleteConnection(edge.fromId); vm.beginOrCompleteConnection(edge.toId)

        vm.deleteConnection(edge)

        assertTrue(vm.uiState.value.plan.connections.isEmpty())
        assertEquals(stepsBefore, vm.uiState.value.plan.steps)
        assertEquals("保留的目标", vm.uiState.value.plan.goal)
        assertEquals("路线已删除", vm.uiState.value.message)
        assertTrue(vm.uiState.value.dirty)
    }

    @Test fun deleteConfirmationRemovesConnectedCardAtomically() {
        val vm = OnePageMapViewModel(FakeRepository())
        vm.chooseTemplate(RoadmapTemplate.EVENT); vm.updateGoal("目标"); vm.confirmGoal()
        val ids = vm.uiState.value.plan.steps.take(2).map { it.id }
        vm.beginOrCompleteConnection(ids[0]); vm.beginOrCompleteConnection(ids[1]); vm.selectStep(ids[0]); vm.requestDeleteSelected(); vm.confirmDialog()
        assertNull(vm.uiState.value.plan.steps.find { it.id == ids[0] })
        assertTrue(vm.uiState.value.plan.connections.isEmpty())
    }

    @Test fun dirtyRestoreRequiresConfirmation() {
        val saved = SavedPlan("slot", "旧方案", 1L, RoadmapPlan("old", goal = "旧目标"))
        val repo = FakeRepository(mutableListOf(saved))
        val vm = readyViewModel(repo)
        vm.requestRestore(saved)
        assertTrue(vm.uiState.value.pendingDialog is PendingDialog.RestorePlan)
        assertNotEquals("旧目标", vm.uiState.value.plan.goal)
    }

    private fun readyViewModel(repo: FakeRepository = FakeRepository()): OnePageMapViewModel = OnePageMapViewModel(repo).also {
        it.chooseTemplate(RoadmapTemplate.BLANK); it.updateGoal("测试目标"); it.confirmGoal()
    }
}

private class FakeRepository(
    private val stored: MutableList<SavedPlan> = mutableListOf(),
    var quickGuideSeen: Boolean = false,
) : RoadmapRepository {
    private var draft: RoadmapPlan? = null
    var exportCount: Int = 0
    override fun loadSaves(): List<SavedPlan> = stored.toList()
    override fun save(plan: RoadmapPlan, slotId: String?): SavedPlan = SavedPlan(slotId ?: "slot-${stored.size}", plan.goal, 1L, plan).also { stored.add(it) }
    override fun deleteSave(slotId: String) { stored.removeAll { it.slotId == slotId } }
    override fun loadDraft(): RoadmapPlan? = draft
    override fun storeDraft(plan: RoadmapPlan) { draft = plan }
    override fun clearDraft() { draft = null }
    override fun isQuickGuideSeen(): Boolean = quickGuideSeen
    override fun setQuickGuideSeen() { quickGuideSeen = true }
    override fun export(plan: RoadmapPlan): String {
        exportCount += 1
        return "content://test/export.png"
    }
    override fun exportEvidence(plan: RoadmapPlan, mode: String): String = "content://test/$mode.png"
}
