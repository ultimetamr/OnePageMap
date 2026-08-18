package com.pico.swan.onepagemap.domain.model

enum class StepStatus(val label: String) {
    NOT_STARTED("未开始"),
    IN_PROGRESS("进行中"),
    COMPLETED("完成"),
}

data class Point2(val x: Float, val y: Float)

const val GOAL_NODE_ID = "__goal__"

data class Step(
    val id: String,
    val title: String,
    val status: StepStatus = StepStatus.NOT_STARTED,
    val position: Point2,
    val rotation: Float = 0f,
)

data class Connection(val fromId: String, val toId: String)

data class Risk(
    val id: String,
    val title: String,
    val position: Point2,
    val nearStepId: String? = null,
)

data class Resource(val id: String, val label: String)

data class RoadmapPlan(
    val id: String,
    val goal: String = "",
    val goalPosition: Point2 = Point2(.82f, .10f),
    val steps: List<Step> = emptyList(),
    val connections: List<Connection> = emptyList(),
    val risks: List<Risk> = emptyList(),
    val resources: List<Resource> = emptyList(),
    val updatedAt: Long = System.currentTimeMillis(),
)

data class SavedPlan(
    val slotId: String,
    val name: String,
    val savedAt: Long,
    val plan: RoadmapPlan,
)

enum class RoadmapTemplate(val title: String, val subtitle: String) {
    BLANK("空白", "从一面旗帜开始"),
    EVENT("活动筹备", "把准备工作铺成一条小路"),
    STUDY("学习计划", "从目标倒推学习台阶"),
    LAUNCH("产品上线", "用少量步骤走到发布"),
}
