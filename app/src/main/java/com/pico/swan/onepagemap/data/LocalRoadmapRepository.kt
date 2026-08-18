package com.pico.swan.onepagemap.data

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.provider.MediaStore
import com.pico.swan.onepagemap.domain.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class LocalRoadmapRepository(private val context: Context) : RoadmapRepository {
    private val prefs = context.getSharedPreferences("one_page_map", Context.MODE_PRIVATE)

    override fun loadSaves(): List<SavedPlan> = runCatching {
        val array = JSONArray(prefs.getString(KEY_SAVES, "[]"))
        (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            SavedPlan(item.getString("slotId"), item.getString("name"), item.getLong("savedAt"), RoadmapJson.decode(item.getString("plan")))
        }.sortedByDescending { it.savedAt }
    }.getOrDefault(emptyList())

    override fun save(plan: RoadmapPlan, slotId: String?): SavedPlan {
        val now = System.currentTimeMillis()
        val saved = SavedPlan(slotId ?: UUID.randomUUID().toString(), plan.goal.ifBlank { "未命名路线" }, now, plan.copy(updatedAt = now))
        val next = (loadSaves().filterNot { it.slotId == saved.slotId } + saved).sortedByDescending { it.savedAt }.take(10)
        val array = JSONArray()
        next.forEach { item -> array.put(JSONObject().apply {
            put("slotId", item.slotId); put("name", item.name); put("savedAt", item.savedAt); put("plan", RoadmapJson.encode(item.plan))
        }) }
        prefs.edit().putString(KEY_SAVES, array.toString()).apply()
        return saved
    }

    override fun deleteSave(slotId: String) {
        val next = loadSaves().filterNot { it.slotId == slotId }
        val array = JSONArray()
        next.forEach { item -> array.put(JSONObject().apply {
            put("slotId", item.slotId); put("name", item.name); put("savedAt", item.savedAt); put("plan", RoadmapJson.encode(item.plan))
        }) }
        prefs.edit().putString(KEY_SAVES, array.toString()).apply()
    }

    override fun loadDraft(): RoadmapPlan? = prefs.getString(KEY_DRAFT, null)?.let { runCatching { RoadmapJson.decode(it) }.getOrNull() }
    override fun storeDraft(plan: RoadmapPlan) { prefs.edit().putString(KEY_DRAFT, RoadmapJson.encode(plan)).apply() }
    override fun clearDraft() { prefs.edit().remove(KEY_DRAFT).apply() }
    override fun isQuickGuideSeen(): Boolean = prefs.getBoolean(KEY_QUICK_GUIDE_SEEN, false)
    override fun setQuickGuideSeen() { prefs.edit().putBoolean(KEY_QUICK_GUIDE_SEEN, true).apply() }

    override fun export(plan: RoadmapPlan): String {
        val bitmap = RoadmapBitmapRenderer.render(plan)
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return saveBitmap(bitmap, "OnePageMap_$stamp.png")
    }

    override fun exportEvidence(plan: RoadmapPlan, mode: String): String {
        val bitmap = DemoEvidenceRenderer.render(plan, mode)
        return saveBitmap(bitmap, "OnePageMap_demo_${mode}.png")
    }

    private fun saveBitmap(bitmap: Bitmap, displayName: String): String {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OnePageMap")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = requireNotNull(context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
        context.contentResolver.openOutputStream(uri).use { output -> requireNotNull(output); bitmap.compress(Bitmap.CompressFormat.PNG, 100, output) }
        values.clear(); values.put(MediaStore.Images.Media.IS_PENDING, 0)
        context.contentResolver.update(uri, values, null, null)
        bitmap.recycle()
        return uri.toString()
    }

    private companion object {
        const val KEY_SAVES = "saves"
        const val KEY_DRAFT = "draft"
        const val KEY_QUICK_GUIDE_SEEN = "quick_guide_seen"
    }
}

internal object RoadmapBitmapRenderer {
    private const val W = 1600
    private const val H = 900
    private const val BG = 0xFF203029.toInt()
    private const val PAPER = 0xFFF5E9D2.toInt()
    private const val INK = 0xFF20312B.toInt()
    private const val ACCENT = 0xFFF2B84B.toInt()
    private const val PATH = 0xFFD8C49F.toInt()
    private const val RISK = 0xFFC8443A.toInt()

    fun render(plan: RoadmapPlan): Bitmap {
        val image = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawColor(BG)
        paint.color = 0xFF315B4C.toInt(); canvas.drawRoundRect(24f, 24f, 1576f, 876f, 32f, 32f, paint)
        paint.color = BG; canvas.drawRoundRect(40f, 96f, 1560f, 852f, 28f, 28f, paint)
        val goalX = 120f + plan.goalPosition.x * 1280f
        val goalY = 190f + plan.goalPosition.y * 480f
        val goalLeft = goalX - 258f
        val goalTop = goalY - 60f
        paint.color = 0x55000000; canvas.drawRoundRect(goalLeft + 10f, goalTop + 10f, goalLeft + 526f, goalTop + 130f, 28f, 28f, paint)
        paint.color = PAPER; canvas.drawRoundRect(goalLeft, goalTop, goalLeft + 516f, goalTop + 120f, 28f, 28f, paint)
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 5f; paint.color = ACCENT
        canvas.drawRoundRect(goalLeft, goalTop, goalLeft + 516f, goalTop + 120f, 28f, 28f, paint)
        paint.style = Paint.Style.FILL; paint.color = INK; canvas.drawRect(goalLeft + 42f, goalTop + 22f, goalLeft + 50f, goalTop + 92f, paint)
        paint.color = ACCENT
        val flag = Path().apply { moveTo(goalLeft + 50f, goalTop + 24f); lineTo(goalLeft + 112f, goalTop + 40f); lineTo(goalLeft + 50f, goalTop + 62f); close() }
        canvas.drawPath(flag, paint)
        paint.color = INK; paint.textSize = 22f; paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("终点 · 最终目标", goalLeft + 132f, goalTop + 38f, paint)
        paint.textSize = 32f
        canvas.drawText(plan.goal.ifBlank { "未命名目标" }.take(15), goalLeft + 132f, goalTop + 84f, paint)

        fun center(step: Step) = PointF(120f + step.position.x * 1280f, 190f + step.position.y * 480f)
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 6f; paint.color = PATH
        plan.connections.forEachIndexed { index, edge ->
            val a = plan.steps.find { it.id == edge.fromId }?.let(::center) ?: return@forEachIndexed
            val b = if (edge.toId == GOAL_NODE_ID) PointF(goalX, goalY) else plan.steps.find { it.id == edge.toId }?.let(::center) ?: return@forEachIndexed
            val offset = 24f * (index % 3 - 1)
            val path = Path().apply { moveTo(a.x, a.y); cubicTo((a.x+b.x)/2, a.y+offset, (a.x+b.x)/2, b.y-offset, b.x, b.y) }
            canvas.drawPath(path, paint)
        }
        paint.style = Paint.Style.FILL
        plan.steps.forEachIndexed { index, step ->
            val p = center(step); val left = p.x - 105f; val top = p.y - 58f
            canvas.save()
            canvas.rotate(step.rotation, p.x, p.y)
            paint.color = 0x55000000; canvas.drawRoundRect(left+8, top+10, left+218, top+126, 20f, 20f, paint)
            paint.color = PAPER; canvas.drawRoundRect(left, top, left+210, top+116, 20f, 20f, paint)
            paint.color = when(step.status) { StepStatus.NOT_STARTED -> 0xFF8A8175.toInt(); StepStatus.IN_PROGRESS -> 0xFF2F77C4.toInt(); StepStatus.COMPLETED -> 0xFF2E8B57.toInt() }
            canvas.drawCircle(left+26, top+28, 12f, paint)
            paint.color = INK; paint.textSize = 25f; paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText("${index+1}. ${step.title.take(10)}", left+46, top+36, paint)
            paint.textSize = 18f; paint.typeface = Typeface.DEFAULT; canvas.drawText(step.status.label, left+20, top+82, paint)
            canvas.restore()
        }
        plan.risks.forEach { risk ->
            val x = 120f + risk.position.x * 1280f; val y = 190f + risk.position.y * 480f
            paint.color = RISK; val rock = Path().apply { moveTo(x, y-42); lineTo(x-54, y+42); lineTo(x+58, y+42); close() }
            canvas.drawPath(rock, paint); paint.color = Color.WHITE; paint.textSize = 21f; paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText(risk.title.take(8), x-44, y+18, paint)
        }
        paint.color = PAPER; canvas.drawRoundRect(70f, 720f, 700f, 820f, 24f, 24f, paint)
        paint.color = INK; paint.textSize = 24f; paint.typeface = Typeface.DEFAULT_BOLD; canvas.drawText("🧰 资源工具箱", 98f, 760f, paint)
        paint.textSize = 20f; paint.typeface = Typeface.DEFAULT
        canvas.drawText(plan.resources.joinToString(" · ") { it.label }.ifBlank { "还没有资源" }.take(38), 98f, 796f, paint)
        paint.color = 0xFFADC7BA.toInt(); paint.textSize = 18f; canvas.drawText("OnePageMap · 固定构图 1600×900", 1240f, 838f, paint)
        return image
    }
}

private object DemoEvidenceRenderer {
    private const val W = 1600
    private const val H = 900
    private val Shell = 0xFFECEBE7.toInt()
    private val Ink = 0xFF20312B.toInt()
    private val Paper = 0xFFF5E9D2.toInt()
    private val Brand = 0xFF315B4C.toInt()
    private val Muted = 0xFF6A746F.toInt()

    fun render(plan: RoadmapPlan, mode: String): Bitmap {
        val out = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawColor(Shell)
        paint.color = Ink; paint.typeface = Typeface.DEFAULT_BOLD; paint.textSize = 34f
        canvas.drawText(if (mode == "export") "导出预览" else plan.goal, 32f, 54f, paint)
        paint.typeface = Typeface.DEFAULT; paint.textSize = 18f; paint.color = Muted
        canvas.drawText(if (mode == "export") "固定构图 1600×900 · 操作手柄已隐藏" else "6/12 路标 · 2 连线 · 1 风险 · 模拟器样例", 32f, 80f, paint)
        button(canvas, if (mode == "export") "返回编辑" else "保存与恢复", if (mode == "export") 1280f else 1260f)
        button(canvas, if (mode == "export") "导出并返回" else "导出截图", 1420f)
        val route = RoadmapBitmapRenderer.render(plan)
        if (mode == "export") {
            canvas.drawBitmap(route, null, RectF(110f, 118f, 1490f, 894f), paint)
        } else {
            canvas.drawBitmap(route, null, RectF(24f, 104f, 1240f, 876f), paint)
            paint.color = Paper; canvas.drawRoundRect(1256f, 104f, 1576f, 876f, 30f, 30f, paint)
            paint.color = Ink; paint.typeface = Typeface.DEFAULT_BOLD; paint.textSize = 28f; canvas.drawText("搭路工具", 1280f, 150f, paint)
            paint.typeface = Typeface.DEFAULT; paint.textSize = 17f; canvas.drawText("手柄射线聚焦 · 扳机执行", 1280f, 180f, paint)
            chip(canvas, "+ 路标", 1280f, 206f); chip(canvas, "+ 风险石", 1412f, 206f)
            paint.typeface = Typeface.DEFAULT_BOLD; paint.textSize = 24f; paint.color = Brand; canvas.drawText("脚边工具箱", 1280f, 300f, paint)
            paint.typeface = Typeface.DEFAULT; paint.textSize = 20f; paint.color = Ink
            listOf("场地联系人", "报名表", "讨论提纲").forEachIndexed { index, item -> canvas.drawText("• $item", 1280f, 342f + index * 38f, paint) }
            paint.textSize = 18f; paint.color = Muted; canvas.drawText("选择路标后可编辑标题、状态与旋转", 1280f, 510f, paint)
            canvas.drawText("删除时会同时清理关联连线", 1280f, 544f, paint)
        }
        route.recycle()
        return out
    }

    private fun button(canvas: Canvas, label: String, x: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Brand }
        canvas.drawRoundRect(x, 24f, x + 150f, 82f, 29f, 29f, paint)
        paint.color = Color.WHITE; paint.typeface = Typeface.DEFAULT_BOLD; paint.textSize = 18f
        canvas.drawText(label, x + 18f, 60f, paint)
    }

    private fun chip(canvas: Canvas, label: String, x: Float, y: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Brand }
        canvas.drawRoundRect(x, y, x + 120f, y + 54f, 27f, 27f, paint)
        paint.color = Color.WHITE; paint.typeface = Typeface.DEFAULT_BOLD; paint.textSize = 18f
        canvas.drawText(label, x + 20f, y + 34f, paint)
    }
}
