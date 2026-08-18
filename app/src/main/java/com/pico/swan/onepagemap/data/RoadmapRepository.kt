package com.pico.swan.onepagemap.data

import com.pico.swan.onepagemap.domain.model.RoadmapPlan
import com.pico.swan.onepagemap.domain.model.SavedPlan

interface RoadmapRepository {
    fun loadSaves(): List<SavedPlan>
    fun save(plan: RoadmapPlan, slotId: String? = null): SavedPlan
    fun deleteSave(slotId: String)
    fun loadDraft(): RoadmapPlan?
    fun storeDraft(plan: RoadmapPlan)
    fun clearDraft()
    fun isQuickGuideSeen(): Boolean
    fun setQuickGuideSeen()
    fun export(plan: RoadmapPlan): String
    fun exportEvidence(plan: RoadmapPlan, mode: String): String
}
