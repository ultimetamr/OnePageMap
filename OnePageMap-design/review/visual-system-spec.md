# Visual System Spec · OnePageMap

> Active revision: 3 | Sources: interaction r8, PM r7, UXR r6 | CR-DS-01 authoritative supplement below

## 2. Spatial Visual Direction Candidates

| Direction | Thesis / first view | Container/depth | Hierarchy/cues | Spatial value / Dashboard risk |
|---|---|---|---|---|
| D1 黑夜营地 | quiet dark terrain, glowing flag and path | one matte Planar; signs at 3 depth bands | flag→selected sign→risk/toolbox; handles appear on focus | strong path, low dashboard risk; dark may feel heavy |
| D2 纸模小径 | warm paper diorama with cut-paper signs | one Planar; restrained layered shadows | tactile edges, rope-like curves, stamp statuses | playful/personal; best fit; risk of childishness controlled by typography |
| D3 透明导航图 | technical glass navigation surface | glass-heavy planar grid | coordinate marks and luminous nodes | clear precision but resembles professional dashboard |

Selected approved reference: **D2 纸模小径**, approved by structured design-effect review: semantic metaphor fidelity 5/5, first-view focus 5/5, readability 4/5, spatial restraint 5/5, dashboard risk 1/5, feasibility 4/5. Reject D1 (low warmth/contrast risk) and D3 (professional/dashboard tone).

## 3. Tokens

| Token | Value | Use |
|---|---|---|
| accent | #F2B84B | focus/goal |
| surface | #F5E9D2 | paper cards |
| brandPrimary | #315B4C | path/primary actions |
| ink | #20312B | text |
| risk | #C8443A | risk + triangle/label |
| background | #203029 | terrain |
| radius | 20dp | card baseline |
| spacing | 8dp | base |

Typography: display sans 32/40/700; title sans 22/28/700; metric mono 18/24/600; body sans 16/24/500; caption sans 13/18/500.

| Semantic | color | shape | label | desc | aliases |
|---|---|---|---|---|---|
| not_started | #8A8175 | circle | 未开始 | queued | not_started,未开始 |
| in_progress | #2F77C4 | dashed | 进行中 | active | in_progress,进行中 |
| completed | #2E8B57 | square | 完成 | done | completed,完成 |
| risk | #C8443A | triangle | 风险 | obstacle | risk,风险 |
| warning | #D9822B | diamond | 需确认 | destructive/overwrite | warning,需确认 |

Materials: `paperMatte` matte/none/1.0; `windowGlass` glass/Regular/0.92; `dialogGlass` glass/Thickest/0.97. Scale: spacing xs4/s8/m16/l24/xl32; radius s12/m20/l32; icons s20/m28/l40.

Environment: one WindowContainer may use Regular system glass; all cards use opaque paperMatte (never color+glass). Key text contrast uses opaque backing. Vibrant disabled for paper/gradient regions; fallback is solid background. Minimum font 12sp; targets 56dp.

## 5.0 Window Structure

RoadmapWindow Planar 1600×900dp, min 960×720, max 1920×1080, inset 24, no docked attachment.

```
┌─ RoadmapWindow 1600×900 ─────────────────┐
│ Header 64: ┈ActionPanel.header variant┈          │
│ gap16                                            │
│ ┌─Route Region 1216×788─────────┐ ┌─Edit 320─┐ │
│ │┈GoalFlag┈ ┈StepSign×12┈      │ │┈EditPanel┈│ │
│ │┈ConnectionCurve┈ ┈RiskStone┈   │ │┈Toolbox┈ │ │
│ └──────────────────────────────┘ └──────────┘ │
└───────────────────────────────────────────────┘
```

Grid: inset top/bottom 24 + header 64 + gap16 + body 772 = 900dp; body 1216:320 with 16 gap. GoalFlag and Toolbox are owned by RouteCanvas render elements `canvas_goal`/`canvas_toolbox`; Template/Save/Export header controls are the `ActionPanel.header` variant; edit content is `ActionPanel` edit variants. Large adds canvas breathing room; Compact moves edit rail to bottom sheet; Constrained collapses header commands to the ActionPanel menu and scrolls canvas.

## 5. Core Component Blocks

The following five core families are complete and intentionally cover all semantic objects; TemplateCard/SaveList/ExportFrame/EditPanel are variants of `ActionPanel`.

### Component: RouteCanvas
Base fields: derivedFromTasks=T2–T7; data=`plan.*`; purpose=build route; layoutRole=primary_explore; priority=primary; runtimeRole=spatialCanvas.

**anatomy.layout**: `┌ goal zone ─ far path ─┐ / │ step+curve field │ / └ near toolbox+risk zone ┘`; 12-column free-position grid, labels billboard/front-face.

| sizing | Regular | Compact | Constrained |
|---|---|---|---|
| w×h | 1216×788 | 912×520 | 912×500 scroll |

| metric | value |
|---|---|
| background | customColor #203029 |
| radius/padding/gap/stroke | l32/l24/m16/1dp #587064 |
| icon/text/hit | m28/body16/caption13/56dp |

| element id | label | type | bind | role |
|---|---|---|---|---|
| canvas_goal | 目标 | region | goal | hero |
| canvas_steps | 路标 | collection | steps | explore |
| canvas_edges | 连线 | svg/path | connections | relationship |
| canvas_near | 资源与风险 | region | resources,risks | near-field |

Bindings: `goal→canvas_goal` fallback “先写下目标” display; `steps→canvas_steps` fallback “新建第一个路标”; `connections→canvas_edges` fallback hidden; `risks/resources→canvas_near` fallback guidance. Variants=editor/export(read-only fixed 1600×900). States=default/focused stroke accent/selected outline/loading skeleton/empty guidance/error retry/overflow internal pan; disabled rejects edits; stacking selected>focused, disabled suppresses hover, loading suppresses focus.

### Component: StepSign
Base fields: tasks=T3–T5/T10; data=`steps[i]`; purpose=show/manipulate step; role=critical_primary; priority=primary; runtime=manipulableCard.

**layout**: `┌┈statusShape┈ ┈title┈ ┈connector┈┐ / │┈rotate┈ ┈delete┈│`; 2 rows, title spans; text layer counter-rotates to face user.

Sizing Regular 224×144; Compact 196×132; Constrained 180×124. Metrics: paperMatte; radius m20; padding m16; gap s8; stroke 2dp semantic; icon m28; title 16/24; caption13/18; hit56.

Render: `step_status` 三态 shape `steps[i].status`; `step_title` 标题 text `title`; `step_connector` 连线 handle `id`; `step_rotate` 旋转 handle `rotation`; `step_delete` 删除 button `id`.
Bindings: status semantic fallback 未开始; title display fallback 未命名步骤; id handles disabled fallback; rotation frame only/text stays upright fallback 0. Variants compact/regular/export. States default, focused 1.03+accent 120ms, selected 3dp, dragging 0.92 opacity, connecting dashed, disabled label, loading skeleton, empty prompt, error retry, overflow 2-line ellipsis; selected>focused, disabled>hover, dragging>selected.

### Component: ConnectionCurve
Base: tasks=T5/T10; data=`connections[i]+step positions`; purpose=precedence; role=supporting; priority=secondary; runtime=liveRelationship.

**layout**: SVG cubic Bezier start→two control points→arrow end; offset 18dp when overlapping title bounds.
Sizing owns canvas bounds Regular/Compact/Constrained; metrics background none, radius0, padding0, gap0, stroke 4dp #D8C49F, arrow20, caption13, endpoint target56.
Render: `edge_path` 前后关系 path `from,to,positions`; `edge_arrow` 方向 marker `to`; `edge_error` 连线已存在 text `validation`.
Bindings: endpoints fallback hide+error; positions recompute every frame; validation semantic warning fallback hidden. Variants normal/overlap-offset/export. States default/focused 6dp/selected/acquiring endpoint/duplicate-error 2s/disabled/loading/empty/error/overflow offset index; error>focused, disabled>all.

### Component: RiskStone
Base: task=T6; data=`risks[i]`; purpose=mark obstacle adjacency; role=critical_primary; priority=primary; runtime=manipulableRisk.

**layout**: `▲ triangle stone / title / linked-step label`; 2 rows. Sizing 176×128 / 160×120 / 152×112. Metrics customColor #C8443A, radius l32 irregular silhouette, padding16, gap8, stroke2 #7B241E, icon28, title16, caption13, hit56.
Render `risk_shape` 风险 triangle `severity`; `risk_title` text `title`; `risk_near` 靠近… text `nearStepId`; `risk_delete` button id. Bindings fallback title 未命名风险; near fallback 未关联; severity maps risk semantic. Variants linked/unlinked/export. States default/focused/selected/dragging/disabled/loading/empty/error/overflow; dragging>selected>focused, disabled suppresses hover.

### Component: ActionPanel
Base: tasks=T1/T2/T3/T6/T8/T9/T10; data=`ui.panel + selected + saves + export`; purpose=focused form/list/confirm; role=supporting or modal primary; priority=primary when modal; runtime=detailPanel/dialog.

**layout**: `┌┈title┈┈close┈┐ / │┈body fields/list/preview┈│ / │┈cancel┈┈primary┈│`; 3 rows.
Sizing Regular320×788 (dialog640×480); Compact bottom sheet 960×420; Constrained overlay 912×500 scroll. Metrics background glass Thickest (no customColor), radius l32, padding l24, gap m16, stroke1 accent, icon28, title22, body16, hit56.
Render `panel_title`, `panel_body`, `panel_error`, `panel_cancel`, `panel_primary`. Bindings: title fallback 编辑; body fallback guidance; validation fallback hidden; actions fallback disabled. Variants template/goal/step/risk/save/export/delete/dirty-restore. States default/focused/selected/disabled/loading/empty/error/overflow; modal blocks canvas; error>focused; disabled suppresses press.

## 5.1 Structure Checklist

| Component | base | layout | sizing | metrics | render | bindings | variants | states/stack | verdict |
|---|---|---|---|---|---|---|---|---|---|
| RouteCanvas | yes | yes | yes | yes | yes | yes | yes | yes | pass |
| StepSign | yes | yes | yes | yes | yes | yes | yes | yes | pass |
| ConnectionCurve | yes | yes | yes | yes | yes | yes | yes | yes | pass |
| RiskStone | yes | yes | yes | yes | yes | yes | yes | yes | pass |
| ActionPanel | yes | yes | yes | yes | yes | yes | yes | yes | pass |

## 5.2 Coverage Reconciliation

Table A: Plan/Goal/Step/Connection/Risk/Resource/SavedPlan/ExportSnapshot → RouteCanvas/StepSign/Curve/RiskStone/ActionPanel bindings respectively; local immediate timeliness; no gaps. Table B: T1–T10 outputs actionable through ActionPanel/StepSign/Curve/RiskStone; T4 move/rotate via StepSign; no read-only loss. Table C: RouteCanvas empty/error/overflow/edit/export; StepSign loading/editing/dragging/connecting/error/cap-disabled; RiskStone unlinked/linked/dragging/error; ActionPanel loading/empty/validation/error/overflow/modal; each maps to render elements/bindings above.

## 6. Material/Depth

Near modal z64 uses Thickest; edit rail z36 Thickest; route z20 solid terrain; paper cards z24–48 matte. Shared Space readability uses opaque paper and window Regular glass; no Vibrant on gradients. Text always backed.

## 7. Data Contract

displayOnlyPaths: goal.title, steps[].title, risks[].title, resources[].label, saves[].name, saves[].savedAt. semanticEnumPaths: steps[].status→not_started/in_progress/completed; validation.kind→warning; risk semantic→risk. States: loading/fresh(local dirty or saved)/partial(recovered plan)/conflicting(restore over dirty)/error. Trust: dirty marker always visible; saved time visible; stale/cloud claims forbidden.

Formatting: blank titles→human guidance; savedAt→`8月12日 18:30`; missing edge endpoint→hide curve + recovery message; machine enums→colorSemantics label.

## 8. PICO Numeric Spec

radius 12/20/32dp; font floor 12sp; target 56dp; central zones 65°×40° core and 85°×55° secondary per workflow methodology; physical results require device validation.

## 9. Assets

SVG flag/step-post/toolbox/risk silhouette, tintable; no required bitmap. Optional simple 3D-like paper textures are procedural, no model. Sounds: focus none, confirm soft 48kHz mono, risk error short non-spatial. Motion follows interaction r4. Asset names lowercase semantic.

## 9A. Authoritative Incompressible Component Supplement

This section supersedes the compact prose in §5. Each block is independent. Owning window tiers are Large/default=1600×900 (content 1552×852), Compact=1280×800 (content 1232×752), Constrained=960×720 (content 912×672).

### Component: RouteCanvas · authoritative

| Field | Content |
|---|---|
| derivedFromTasks | T2,T3,T4,T5,T6,T7 |
| derivedFromData | Plan,Goal,Step[],Connection[],Risk[],Resource[] |
| Purpose | arrange and read one route |
| layoutRole | primary_explore |
| Priority | primary |
| runtimeRole | spatialCanvas |

**Anatomy · Layout**
```
┌─far: ┈GoalFlag┈──────────┐
│mid: ┈StepSign×12┈ ┈Curves┈│
└─near: ┈RiskStone┈ ┈Toolbox┈┘
```
Grid: 12 columns; free-position objects clamp to content bounds; text layers billboard.

**Anatomy · Sizing**
| Tier | Size | Fit |
|---|---|---|
| Regular/Large | 1216×772 | within 1552×852 after rail/gap |
| Compact | 1232×500 | rail becomes sheet |
| Constrained | 912×500 scroll | within 912×672 |

**Metrics**
| Metric | Value |
|---|---|
| background | customColor #203029 |
| radius | l 32dp |
| padding | l 24dp |
| gap | m 16dp |
| stroke | 1dp #587064 |
| icon | m 28dp |
| primary text | body 16/24/500 |
| secondary | caption 13/18/500 |
| hitTarget | 56dp |

**renderSpec.elements[]**
| id | label | type | bind | role |
|---|---|---|---|---|
| canvas_goal | 目标 | region | goal | hero |
| canvas_steps | 路标 | collection | steps | explore |
| canvas_edges | 连线 | svg | connections | relation |
| canvas_risks | 风险 | collection | risks | critical |
| canvas_toolbox | 资源工具箱 | list | resources | support |

**dataBindings[]**
| Source | Target | fallback | kind |
|---|---|---|---|
| goal | canvas_goal | 先写下目标 | display |
| steps | canvas_steps | 新建第一个路标 | display |
| connections | canvas_edges | hidden | display |
| risks | canvas_risks | hidden | semantic |
| resources | canvas_toolbox | 还没有资源 | display |

**Variants**: `editor` interactive handles; `export` no handles, fixed crop.

**States**
| State | Trigger | Visual | Size | Motion | Accessibility |
|---|---|---|---|---|---|
| default | enter | normal | none | none | labels |
| focused | gaze/ray | accent outline | none | 120ms | outline |
| selected | canvas select | selected object front | none | 180ms | spoken title |
| disabled | modal | 60% opacity | none | none | blocked label |
| loading | restore | skeleton | none | 220ms fade | loading text |
| empty | no steps | guidance | none | none | add button |
| error | corrupt data | retry banner | none | none | error text |
| overflow | bounds | internal pan | none | direct | scroll controls |
Stacking: error>loading>disabled>selected>focused.

### Component: StepSign · authoritative

| Field | Content |
|---|---|
| derivedFromTasks | T3,T4,T5,T10 |
| derivedFromData | steps[i] |
| Purpose | edit/status/manipulate one step |
| layoutRole | critical_primary |
| Priority | primary |
| runtimeRole | manipulableCard |

**Anatomy · Layout**
```
┌┈status┈ ┈title (upright)┈ ┈connector┈┐
│┈rotate┈                     ┈delete┈│
└─post/frame may rotate; text counter-rotates─┘
```
Grid: 2 rows, 3 columns; title spans center.

**Sizing**: Regular 224×144 within Large canvas; Compact 196×132; Constrained 180×124, all ≤ owning canvas.

**Metrics**
| Metric | Value |
|---|---|
| background | customColor #F5E9D2 |
| radius | m20 |
| padding | m16 |
| gap | s8 |
| stroke | 2dp semantic |
| icon | m28 |
| primary text | body16/24/500 |
| secondary | caption13/18/500 |
| hitTarget | 56dp |

**renderSpec.elements[]**
| id | label | type | bind | role |
|---|---|---|---|---|
| step_status | 状态 | badge | status | semantic |
| step_title | 标题 | text | title | primary |
| step_connector | 拉出连线 | handle | id | action |
| step_rotate | 旋转 | handle | rotation | action |
| step_delete | 删除 | button | id | destructive |

**dataBindings[]**
| Source | Target | fallback | kind |
|---|---|---|---|
| steps[i].status | step_status | 未开始 | semantic |
| steps[i].title | step_title | 未命名步骤 | display |
| steps[i].id | connector/delete | disabled | display |
| steps[i].rotation | frame transform | 0; text upright | display |

**Variants**: regular full actions; compact icons+tooltip; export actions hidden.

**States**
| State | Trigger | Visual | Size | Motion | Accessibility |
|---|---|---|---|---|---|
| default | none | paper | 1 | none | title |
| focused | gaze/ray | accent stroke | 1.03 | 120ms | outline |
| selected | activate | 3dp stroke | 1 | 180ms | selected label |
| dragging | hold move | 92% opacity | 1 | direct | position announced |
| connecting | connector hold | dashed stroke | 1 | direct | target hint |
| disabled | 12-cap/modal | muted+label | 1 | none | reason text |
| loading | restore | skeleton | 1 | fade | loading |
| empty | blank title | prompt | 1 | none | required |
| error | invalid | warning diamond | 1 | none | error text |
| overflow | long title | 2-line ellipsis | 1 | none | full tooltip |
Stacking: error>disabled>dragging>connecting>selected>focused.

### Component: ConnectionCurve · authoritative

| Field | Content |
|---|---|
| derivedFromTasks | T5,T10 |
| derivedFromData | connections[i], steps[].position |
| Purpose | show directed precedence live |
| layoutRole | supporting |
| Priority | secondary |
| runtimeRole | liveRelationship |

**Layout**: `start●─╲ cubic control1/control2 ╱─▶end`; offset index adds 18dp; title bounding boxes excluded.

**Sizing**: Regular/Compact/Constrained equals owning canvas bounds; clipped to it.

**Metrics**: background none; radius0; padding0; gap18dp overlap offset; stroke4dp #D8C49F; arrow20dp; caption13/18; endpoint hit56dp.

**renderSpec.elements[]**: `edge_path`(前后关系,path,from/to/positions,relation); `edge_arrow`(方向,marker,to,direction); `edge_error`(连线已存在,text,validation,warning).

**dataBindings[]**: endpoints→path fallback hide+error display; positions→control points fallback last valid display; validation→edge_error fallback hidden semantic.

**Variants**: normal; overlap-offset; export thicker 5dp.

**States**: default 4dp; focused 6dp/120ms; selected accent; acquiring dashed/direct; duplicate_error warning text/2s; disabled muted; loading hidden; empty hidden; error broken endpoint banner; overflow offset-index. Stacking error>disabled>selected>focused.

### Component: RiskStone · authoritative

| Field | Content |
|---|---|
| derivedFromTasks | T6 |
| derivedFromData | risks[i] |
| Purpose | place obstacle by step |
| layoutRole | critical_primary |
| Priority | primary |
| runtimeRole | manipulableRisk |

**Layout**: `┌┈triangle┈┈title┈┐/│┈near-step label┈┈delete┈│`; 2x2 grid.

**Sizing**: Regular176×128; Compact160×120; Constrained152×112; all within canvas.

**Metrics**: customColor #C8443A; radius l32; padding16; gap8; stroke2 #7B241E; icon28; body16; caption13; hit56.

**renderSpec.elements[]**: risk_shape(风险,triangle,severity); risk_title(风险名,text,title); risk_near(靠近,text,nearStepId); risk_delete(删除,button,id).

**dataBindings[]**: title fallback 未命名风险 display; nearStepId fallback 未关联 display; severity maps risk semantic; id fallback delete disabled.

**Variants**: linked with tether; unlinked with 未关联; export no actions.

**States**: default; focused accent/1.03/120ms; selected 3dp; dragging 92% direct; disabled muted+reason; loading skeleton; empty prompt; error warning; overflow 2-line. Stacking error>disabled>dragging>selected>focused.

### Component: ActionPanel · authoritative

| Field | Content |
|---|---|
| derivedFromTasks | T1,T2,T3,T6,T7,T8a,T8b,T9,T10 |
| derivedFromData | ui.panel,selected,saves,export |
| Purpose | forms, lists and confirmation |
| layoutRole | supporting; modal primary |
| Priority | primary when open |
| runtimeRole | detailPanel/dialog |

**Layout**
```
┌┈title┈                   ┈close┈┐
│┈body: fields/list/preview/error┈│
└┈cancel┈                 ┈primary┈┘
```
Grid: 3 rows; body scrolls.

**Sizing**: Regular rail320×772/dialog640×480; Compact bottom sheet1232×420; Constrained overlay912×500 scroll, all within owning content.

**Metrics**: glass Thickest; radius32; padding24; gap16; stroke1 accent; icon28; title22/28; body16/24; hit56.

**renderSpec.elements[]**: panel_title(title); panel_body(body); panel_error(validation); panel_cancel(cancel); panel_primary(confirm).

**dataBindings[]**: panel.title fallback 编辑 display; panel.body fallback guidance display; validation fallback hidden semantic; primaryEnabled fallback false display; saves fallback 暂无保存 display.

**Variants**: template grid; goal form; step form; risk form; resource form; save list; export preview; delete dialog; dirty-restore dialog—each changes body fields and primary label.

**States**: default; focused outline/120ms; selected primary accent; disabled muted+reason; loading progress; empty guidance; error inline+retry; overflow body scroll. Modal blocks canvas; error>loading>disabled>focused.

## 9B. Itemized Coverage Reconciliation

**Table A**
| Entity | Timeliness | Binding | Method | Gap |
|---|---|---|---|---|
| Plan/goal | local immediate | RouteCanvas.goal | display | none |
| Step/title/status/position/rotation | local immediate | StepSign bindings | display+semantic | none |
| Connection/endpoints | live per move | ConnectionCurve | path | none |
| Risk/title/nearStep | local immediate | RiskStone | display+semantic | none |
| Resource/label | local immediate | RouteCanvas.toolbox + ActionPanel | display/action | none |
| SavedPlan/name/time | persisted | ActionPanel save variant | display/action | none |
| ExportSnapshot | point-in-time | RouteCanvas export + ActionPanel | fixed preview | none |

**Table B**
| Output | Kind | Component interaction | Gap |
|---|---|---|---|
| T1 template | actionable | ActionPanel.template confirm | none |
| T2 goal | actionable | ActionPanel.goal primary | none |
| T3 step/state | actionable | StepSign select + ActionPanel.step | none |
| T4 geometry | actionable | StepSign drag/rotate | none |
| T5 relationship | actionable | StepSign.connector→Curve | none |
| T6 risk | actionable | RiskStone + ActionPanel.risk | none |
| T7 resources sufficient | actionable | Toolbox + ActionPanel.resource | none |
| T8a save | actionable | ActionPanel.save | none |
| T8b restore/cancel | actionable | ActionPanel.dirty-restore | none |
| T9 export | actionable | RouteCanvas.export + ActionPanel.export | none |
| T10 delete | actionable | StepSign.delete + ActionPanel.delete | none |

**Table C**
| Primary→subcomponent | Substate | Primitive | Binding |
|---|---|---|---|
| RouteCanvas→steps | empty/loading/error/overflow | guidance/skeleton/banner/pan | steps |
| StepSign→title/frame | editing/dragging/connecting/cap-disabled/error | text/frame/handle/label/diamond | title,position,id,count,validation |
| RiskStone→near | linked/unlinked/dragging/error | tether/label/stone/banner | nearStepId,position,validation |
| ActionPanel→body | loading/empty/editing/error/overflow | progress/guidance/form/retry/scroll | panel.* |

## 10. Minimum Completeness Gate

Visual direction pass; precise tokens/materials pass; window shell/reflow pass; five core component eight-block structures in §9A pass; reconciliation §9B no gaps; trust/fallback pass. `minimumCompletenessGate=pass`.
