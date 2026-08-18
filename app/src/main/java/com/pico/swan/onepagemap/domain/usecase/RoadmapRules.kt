package com.pico.swan.onepagemap.domain.usecase

import com.pico.swan.onepagemap.domain.model.Connection
import com.pico.swan.onepagemap.domain.model.GOAL_NODE_ID
import com.pico.swan.onepagemap.domain.model.RoadmapPlan

sealed interface RuleResult<out T> {
    data class Ok<T>(val value: T) : RuleResult<T>
    data class Error(val message: String) : RuleResult<Nothing>
}

object RoadmapRules {
    const val MAX_STEPS = 12
    const val MAX_SAVES = 10

    fun validateGoal(goal: String): RuleResult<String> =
        if (goal.isBlank()) RuleResult.Error("先写下目标，旗帜才知道立在哪里")
        else RuleResult.Ok(goal.trim())

    fun addConnection(plan: RoadmapPlan, fromId: String, toId: String): RuleResult<RoadmapPlan> {
        if (fromId == toId) return RuleResult.Error("不能把路标连回自己")
        if (fromId == GOAL_NODE_ID) return RuleResult.Error("最终目标只能作为终点")
        if (plan.connections.any { it.fromId == fromId && it.toId == toId }) {
            return RuleResult.Error("这条前后关系已经存在")
        }
        if (plan.steps.none { it.id == fromId } || (toId != GOAL_NODE_ID && plan.steps.none { it.id == toId })) {
            return RuleResult.Error("连线端点已经不存在")
        }
        return RuleResult.Ok(plan.copy(connections = plan.connections + Connection(fromId, toId)))
    }

    fun deleteStep(plan: RoadmapPlan, stepId: String): RoadmapPlan = plan.copy(
        steps = plan.steps.filterNot { it.id == stepId },
        connections = plan.connections.filterNot { it.fromId == stepId || it.toId == stepId },
        risks = plan.risks.map { if (it.nearStepId == stepId) it.copy(nearStepId = null) else it },
    )
}
