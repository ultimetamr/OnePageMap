package com.pico.swan.onepagemap.domain.usecase

import com.pico.swan.onepagemap.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class RoadmapRulesTest {
    private val a = Step("a", "A", position = Point2(.2f, .2f))
    private val b = Step("b", "B", position = Point2(.6f, .5f))

    @Test fun emptyGoalIsRejected() {
        assertTrue(RoadmapRules.validateGoal("   ") is RuleResult.Error)
    }

    @Test fun duplicateConnectionIsRejected() {
        val plan = RoadmapPlan("p", steps = listOf(a, b), connections = listOf(Connection("a", "b")))
        assertTrue(RoadmapRules.addConnection(plan, "a", "b") is RuleResult.Error)
    }

    @Test fun selfConnectionIsRejected() {
        assertTrue(RoadmapRules.addConnection(RoadmapPlan("p", steps = listOf(a)), "a", "a") is RuleResult.Error)
    }

    @Test fun stepCanConnectToGoalButGoalCannotConnectOutward() {
        val plan = RoadmapPlan("p", goal = "终点", steps = listOf(a))
        val connected = RoadmapRules.addConnection(plan, "a", GOAL_NODE_ID)
        assertTrue(connected is RuleResult.Ok)
        assertTrue(RoadmapRules.addConnection(plan, GOAL_NODE_ID, "a") is RuleResult.Error)
    }

    @Test fun deletingStepAlsoDeletesIncidentEdgesAndUnlinksRisk() {
        val plan = RoadmapPlan("p", steps = listOf(a, b), connections = listOf(Connection("a", "b")), risks = listOf(Risk("r", "风险", Point2(.3f, .7f), "a")))
        val result = RoadmapRules.deleteStep(plan, "a")
        assertEquals(listOf(b), result.steps)
        assertTrue(result.connections.isEmpty())
        assertNull(result.risks.single().nearStepId)
    }
}
