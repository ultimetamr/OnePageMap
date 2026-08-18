package com.pico.swan.onepagemap.ui

import com.pico.swan.onepagemap.domain.model.RoadmapPlan
import com.pico.swan.onepagemap.domain.model.RoadmapTemplate
import com.pico.swan.onepagemap.domain.model.SavedPlan

enum class AppScreen { GUIDE, TEMPLATE, GOAL, EDITOR, SAVES, EXPORT }

enum class EditorPanel { OVERVIEW, STEP, RISK, RESOURCE }

sealed interface PendingDialog {
    data class DeleteStep(val stepId: String, val incidentEdges: Int) : PendingDialog
    data class RestorePlan(val saved: SavedPlan) : PendingDialog
    data class RecoverDraft(val plan: RoadmapPlan) : PendingDialog
    data object LeaveDirty : PendingDialog
}

data class OnePageMapUiState(
    val screen: AppScreen = AppScreen.TEMPLATE,
    val plan: RoadmapPlan = RoadmapPlan(id = "draft"),
    val selectedTemplate: RoadmapTemplate = RoadmapTemplate.BLANK,
    val selectedStepId: String? = null,
    val selectedRiskId: String? = null,
    val editorPanel: EditorPanel = EditorPanel.OVERVIEW,
    val connectingFromId: String? = null,
    val saves: List<SavedPlan> = emptyList(),
    val pendingDialog: PendingDialog? = null,
    val quickGuideStep: Int = 0,
    val quickGuideReturnScreen: AppScreen? = null,
    val dirty: Boolean = false,
    val message: String? = null,
    val exportUri: String? = null,
)
