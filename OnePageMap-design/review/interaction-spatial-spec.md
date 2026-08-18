# Interaction / Spatial Design Spec · OnePageMap

> Active revision: 8 | Roles: task_decision_designer, interaction_xr_designer, spatial_design_system_designer | Sources: PM r7, UXR r6, visual r3 | CR-SC-01/02 + CR-DS-01

## 2. Design Principles

| # | Principle | Scope | Basis | Checkpoint | Precedence |
|---|---|---|---|---|---|
| P1 | One visible focus: the selected route object or active dialog. | interaction/spatial | five-minute target | state + layout | safety > task > decoration |
| P2 | Space communicates route, adjacency and depth; it never adds PM machinery. | product/spatial | R-METAPHOR/R-PERSONAL | components | P1 first |
| P3 | Text stays readable and semantics never rely on color alone. | visual/accessibility | R-MANIPULATE + methodology | StepSign/RiskStone | overrides rotation aesthetics |
| P4 | Every mutation is local, reversible where possible, and explicit when destructive. | trust | edge cases | dialogs/save | overrides speed for delete/restore |

Negative: no Stage, dashboard, dates, auto schedule, critical path, team assignment, camera movement, rotated text, orphan/duplicate/self edge.

## 3. Task / Decision Model

| Task ID | Actor / scenario | Input | Decision output | Error consequence | Frequency | Dependencies | Duration |
|---|---|---|---|---|---|---|---|
| T1 | individual/entry | four template meanings | starting template | wrong start adds edits | once | none | 10–20s |
| T2 | individual/setup | goal text | valid goal/flag | empty route anchor | once | T1 | ≤15s |
| T3 | individual/build | count/title/status | add/edit/state step | cap/readability failure | 1–12 | T2 | ≤3 actions |
| T4 | individual/arrange | position/rotation | route geometry | occlusion/unreadable | repeated | T3 | ≤10s |
| T5 | individual/order | two distinct cards/edges | directed relationship | duplicate/orphan | 0–many | T3 | ≤10s |
| T6 | individual/risk | risk title/nearby step | risk placement | risk association unclear | 0–many | T3 | ≤15s |
| T7 | individual/resources | resource labels + planned steps | decide resource set is sufficient for next actions | missing prerequisite | occasional | T2 | ≤20s |
| T8a | individual/save | dirty flag/10 slots | select new/overwrite slot and persist | unsaved data | occasional | T2–T7 | ≤20s |
| T8b | individual/restore | saved metadata/current dirty flag | cancel or restore selected plan | accidental overwrite | occasional | T8a | explicit |
| T9 | individual/share | valid plan/export frame | confirmed screenshot | clipped/unreadable | occasional | T2 | ≤20s |
| T10 | individual/delete | selected card/edge count | cancel or atomic delete | orphan edges | rare | T3/T5 | explicit |

Dependencies: T1→T2; T3/T7 follow T2; T4/T5/T6 may interleave; T8/T9 follow any valid edit. Competitor coverage deliberately omits views, assignment, dates, schedules and critical path.

## 4. Spatial Value Justification

| Task | Spatial dimensions | Rationale | 2D counterfactual | Evidence | Rating |
|---|---|---|---|---|---|
| T2/T3 | direction, distance, depth | flag gives directional pull; sign depth distinguishes route order | equally capable 2D freeform canvas with flag icon, cards and arrows can deliver most logic; PICO-planar benefit is embodied focus/hover and layered presentation, still a hypothesis | R-METAPHOR; CB-G-01 gap | Medium hypothesis |
| T4 | position, body/hand, motion | indirect eye-hand/controller manipulation on a spatially placed window may increase tangibility | equally capable 2D canvas drag/rotate is functionally sufficient | R-MANIPULATE | Medium; platform presentation value, not unique necessity |
| T5 | position/change | endpoint geometry updates continuously | static arrows/list relations | CB-A-04 | Medium |
| T6/T7 | adjacency, near-body depth | risk beside step/toolbox near bottom supports memory | badges/side list | R-METAPHOR | Medium hypothesis |
| T1/T8/T9 | none | reading/forms dominate | conventional planar UI | platform fact | Low; no Stage |

## 5. Design Hypotheses

| Hypothesis | Information model | Spatialization | Container | Path | Primary interaction | Risk/cost |
|---|---|---|---|---|---|---|
| A 路标小径 | goal-axis route; risks/resources adjacent | layered 2.5D inside planar | one Planar | template→goal→build | direct card/sign manipulation | medium; curves/overlap |
| B 卡片地图册 | pages of small local maps | low; paged 2D | one Planar | template→page→list | form + reorder | low; weak spatial value |
| C 环形路线盘 | goal center, steps around ring | high radial depth | Volumetric window | goal→orbit steps | rotate/zoom ring | high; text/head movement |

## 6. Concept Selection Matrix

Scores /5.

| Hypothesis | Efficiency | Spatial | Comfort | Domain | Safety | Access | Feasible | Unique | Total | Verdict |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---|
| A | 5 | 3 | 4 | 5 | 4 | 4 | 4 | 5 | 34 | Selected |
| B | 4 | 2 | 5 | 3 | 5 | 5 | 5 | 2 | 31 | reject: insufficient spatial benefit |
| C | 2 | 5 | 2 | 4 | 2 | 2 | 2 | 5 | 24 | reject: comfort/readability/cost |

Selected: **路标小径**. Positioning is a bounded personal path-builder, not a board/timeline. Evidence: CB-T/P/A mechanics, CB-D-01 hypothesis, R-PERSONAL/R-METAPHOR. It absorbs manipulation/connectors and avoids dashboards/schedules.

Selection evidence: equal weights reflect the quality contract's simultaneous five-minute, comfort, accessibility and feasibility constraints; no single axis is authorized to dominate. A rationale by criterion: efficiency=5 (one path, R-ACCEPT-5MIN); spatial=3 after strongest-canvas counterfactual (R-METAPHOR, validation pending); comfort=4 (Planar/no Stage, EV-P1); domain=5 (all T1–T10); safety=4 (dialogs/P4); access=4 (P3/controller parity); feasible=4 (simple Bezier/no algorithms, R-LINE-SIMPLE/R-NO-SCHEDULE); unique=5 (CB-D-01). Sensitivity: doubling comfort or feasibility still selects A (38 vs B 36 vs C 26); doubling spatial yields A 37, B 33, C 29. Acceptance condition: A remains selected only if ≥4/5 users finish in 5 minutes and report no forced head movement; otherwise fall back to B while retaining Planar.

## 7. Experience and Container Architecture

- Experience: **Choose** (template), **Build** (persistent route context), **Confirm** (save/export/delete dialogs). No Immerse layer.
- Space State: **Shared Space**. Container: `RoadmapWindow`, WindowContainer **Planar**, default visible=1. No Stage or perception permissions. Planar depth=640dp.
- Entry: template state. Exit: system back returns editor→template with dirty confirmation, then system close. Fallback: restore last local plan or blank.

## 8. Window Attachment Decision Matrix

| Need | Placement | Selected | Host | Persistence | Frequency | Rationale | Rejected |
|---|---|---|---|---|---|---|---|
| object edit | in-window | InlineControl panel | RoadmapWindow | contextual | high | beside canvas, no extra window | Subwindow too persistent; None insufficient |
| primary commands | in-window | InlineControl rail | RoadmapWindow | persistent | high | commands remain with workspace | Toolbar rejected: would detach semantic context |
| delete/restore/export | in-window modal | Sheet/Dialog | RoadmapWindow | temporary | low/high risk | blocks until choice | Popup/None unsafe |
| page navigation | none | None | RoadmapWindow | N/A | low | state buttons in context | TabBar adds dashboard navigation |
| spatial decoration | none | None | RoadmapWindow | N/A | none | internal route supplies value | Augment gratuitous |

## 9. Window Sizing Derivation

| Window | form/unit | Tier/baseline | Content/topology | Viewing/FOV | Floors/overhead | Candidates | Default | min/max | Aspect/reflow |
|---|---|---|---|---|---|---|---|---|---|
| RoadmapWindow | Planar dp; depth 640dp | productivity; starts 1280×720; legal 320×180–2700×1800 | canvas + 320dp edit rail; route left-to-right | seated/standing, ~1.75m, Dynamic; default fits core 65°×40°; max within secondary 85°×55° | target 56dp, body 12sp, inset 24; no docked attachment | 960×720 constrained; 1280×800 compact; 1600×900 default; 1920×1080 large | 1600×900 | 960×720 / 1920×1080 | 16:9 preferred; ContentMinSize; Compact rail becomes bottom sheet; Constrained tools collapse to labeled menu, canvas scrolls; never scale text |

Clear-zone check: default central route/selected object occupies ~60°×35°; tool rail secondary. Shared Space environment remains visible around one window; no multiple-window occlusion. Downstream validates actual angular occupancy/readability.

## 10. State / Transition Graph

| State | Task/focus | Layout/components | Data | Entry/exit | Recovery/return |
|---|---|---|---|---|---|
| S1 选起点 | T1/template grid | TemplateCard x4 | preset IDs | launch→S2 | back closes |
| S2 立目标 | T2/goal field | GoalFlag preview/input | goal | template→S3 | empty inline error; back S1 |
| S3 搭小路 | T3–T7/canvas selection | RouteCanvas, cards, curves, risk, toolbox, editor | plan graph | goal/restore | errors toast/dialog; back dirty dialog |
| S4 保存架 | T8/save slot | SaveList | ten slots | S3→S3 restored | overwrite dialog/cancel |
| S5 导出取景 | T9/export frame | ExportFrame | plan snapshot | S3→S3 | invalid plan blocks |
| D1 删除路标? | T10/dialog | edge count, cancel/delete | selected step | S3→S3 | cancel stable |

| ID | Start→Target | Trigger | Action | Confirm |
|---|---|---|---|---|
| X1 | S1→S2 | template.select | seed plan | no |
| X2 | S2→S3 | goal.confirm | validate/create flag | no; empty blocks |
| X3 | S3→S4 | save.open | show slots | no |
| X4 | S4→S3 | save.restore | load plan | yes if dirty |
| X5 | S3→S5 | export.open | compose fixed frame | no |
| X6 | S5→S3 | export.confirm/cancel | capture or close | yes for capture |
| X7 | S3→D1 | step.delete | count incident edges | yes |
| X8 | D1→S3 | delete.confirm/cancel | atomic removal or none | yes |
| X9 | S3→S3 | step.add | append if count<12; else cap feedback | no |
| X10 | S3→S3 | step.edit/status | validate and update card | no |
| X11 | S3→S3 | step.move/rotate | update transform and connected curves | no |
| X12 | S3→S3 | connection.dragComplete | reject self/duplicate or append edge | no |
| X13 | S3→S3 | risk.add/edit/move | update risk and adjacency label | no |
| X14 | S3→S3 | resource.add/edit/delete | update compact label list | no |
| X15 | S3→S3 | edit.cancel | discard panel draft | no |

## 11. End-to-End Flow

`launch→template→goal(valid)→add/edit 6→connect 2→risk 1→save→export preview→confirm→editor`; exceptions: empty goal, cap 12, duplicate/self edge, dirty restore and linked delete all remain at stable source state with visible reason/cancel.

## 12. Eye-Hand / Controller

All targets support gaze focus + pinch; controller ray focus + trigger activates. Card drag uses pinch-hold or trigger-hold; rotate uses visible rotate handle or controller contextual axis after selection; connector handle drag uses same hold; system back closes dialog/panel then asks on dirty exit. Exact controller buttons are downstream assumptions, but every action has a focusable command fallback. Delete/restore/export use blocking Dialog.

## 13. Motion

| Scenario | Duration/easing | Amplitude | Reduce Motion | Performance fallback |
|---|---|---|---|---|
| card/risk move | direct + 180ms settle, standard | user-driven | no settle | snap position |
| curve update | same frame, 160ms control-point ease | ≤24dp offset | immediate redraw | straight 2-segment line |
| panel/state | 220ms ease-out | ≤24dp | 120ms fade | instant |
| hover | 120ms ease-out | scale 1.03 | stroke only | stroke only |

Global: reduceMotion=yes; controllerFallback=yes; colorIndependentSemantics=yes; textScaling=wrap/scroll; stableExit=yes. No camera movement/flashing.

## 14. Layout Skeleton / Geometry

| layer | anchor | x/y | w/h | z |
|---|---|---|---|---|
| RouteCanvas | left-center | 24/104 | 1216/772 default | 20 |
| EditRail | right-center | 1256/104 | 320/772 | 36 |
| dialogs | center | 480/210 | 640/480 | 64 |

Single focus=selected card/flag/risk or dialog. Route derives from serial task relationships; rail from high-frequency edit actions; rejected full-width bottom controls because they obscure near-body toolbox. Compact: rail bottom sheet; Constrained: command menu + scrollable canvas.

## 15. Minimum Completeness Gate

| Item | Evidence | Verdict |
|---|---|---|
| principles/tasks | §2–3 | pass |
| spatial/concept/≥3 hypotheses | §4–6 | pass |
| container/attachment | §7–8 | pass |
| sizing chain | §9 | pass |
| states/transitions/exits | §10–11 | pass |
| input/motion/layout | §12–14 | pass |

minimumCompletenessGate=pass
