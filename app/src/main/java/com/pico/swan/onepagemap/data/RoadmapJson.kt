package com.pico.swan.onepagemap.data

import com.pico.swan.onepagemap.domain.model.*
import org.json.JSONArray
import org.json.JSONObject

internal object RoadmapJson {
    fun encode(plan: RoadmapPlan): String = JSONObject().apply {
        put("id", plan.id)
        put("goal", plan.goal)
        put("goalX", plan.goalPosition.x.toDouble())
        put("goalY", plan.goalPosition.y.toDouble())
        put("updatedAt", plan.updatedAt)
        put("steps", JSONArray().apply { plan.steps.forEach { step -> put(JSONObject().apply {
            put("id", step.id); put("title", step.title); put("status", step.status.name)
            put("x", step.position.x.toDouble()); put("y", step.position.y.toDouble()); put("rotation", step.rotation.toDouble())
        }) } })
        put("connections", JSONArray().apply { plan.connections.forEach { edge -> put(JSONObject().apply {
            put("from", edge.fromId); put("to", edge.toId)
        }) } })
        put("risks", JSONArray().apply { plan.risks.forEach { risk -> put(JSONObject().apply {
            put("id", risk.id); put("title", risk.title); put("x", risk.position.x.toDouble()); put("y", risk.position.y.toDouble())
            put("near", risk.nearStepId ?: "")
        }) } })
        put("resources", JSONArray().apply { plan.resources.forEach { item -> put(JSONObject().apply {
            put("id", item.id); put("label", item.label)
        }) } })
    }.toString()

    fun decode(raw: String): RoadmapPlan = JSONObject(raw).let { root ->
        RoadmapPlan(
            id = root.optString("id", "restored"),
            goal = root.optString("goal"),
            goalPosition = Point2(root.optDouble("goalX", .82).toFloat(), root.optDouble("goalY", .10).toFloat()),
            updatedAt = root.optLong("updatedAt", System.currentTimeMillis()),
            steps = root.optJSONArray("steps").mapObjects { item -> Step(
                id = item.getString("id"), title = item.optString("title"),
                status = runCatching { StepStatus.valueOf(item.optString("status")) }.getOrDefault(StepStatus.NOT_STARTED),
                position = Point2(item.optDouble("x", .5).toFloat(), item.optDouble("y", .5).toFloat()),
                rotation = item.optDouble("rotation", 0.0).toFloat(),
            ) },
            connections = root.optJSONArray("connections").mapObjects { Connection(it.getString("from"), it.getString("to")) },
            risks = root.optJSONArray("risks").mapObjects { item -> Risk(
                id = item.getString("id"), title = item.optString("title"),
                position = Point2(item.optDouble("x", .5).toFloat(), item.optDouble("y", .8).toFloat()),
                nearStepId = item.optString("near").takeIf(String::isNotBlank),
            ) },
            resources = root.optJSONArray("resources").mapObjects { Resource(it.getString("id"), it.optString("label")) },
        )
    }

    private fun <T> JSONArray?.mapObjects(block: (JSONObject) -> T): List<T> {
        if (this == null) return emptyList()
        return (0 until length()).map { block(getJSONObject(it)) }
    }
}
