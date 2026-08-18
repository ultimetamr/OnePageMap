# Spatial App Requirement Spec · OnePageMap

> Role: `product_strategist` | Active revision: 7 | Source: UXR r6 + user requirement `SHA256:7d032d6f52598f0be33629355776f29007801d3e691df3e035767eab9ee878c5`; CR-01–05

## 0. Reasoning Guidance

Product outcome only: make a small personal project understandable and buildable as a short spatial path. Unknowns remain assumptions; layout, components and visual direction are not frozen here.

## 1. Direct Description of Outputs

This revision carries the frozen intent definition and quality contract derived from UXR r6; the lineage is user source → PM intent r1 → UXR r6 → PM quality r7.

## 2. Background and Problem (intent definition · foundation)

- **One-sentence requirement description**: OnePageMap helps an individual turn a modest goal into a tangible Chinese spatial route—goal flag in the distance, up to 12 step signs along the way, resources at the feet, and red risk stones beside affected steps—without becoming a professional project-management system.
- **Target users**: Chinese-speaking individuals planning a learning goal, small event, personal product launch, or blank custom project; no administrator or team role.
- **Use scenarios**: a five-minute first build, later short review/edit sessions, local save/restore, and a fixed-composition readable screenshot for sharing.
- **Wearing posture**: primarily seated or standing still; no locomotion required.
- **Frequency and duration**: assumption A1: 1–3 sessions per small project, about 5–20 minutes each; the first usable six-step route must be completable within 5 minutes.
- **Preliminary judgment of spatial necessity**: `design_hypothesis` — direction and distance may externalize progress while adjacency may make risks/resources memorable; validate against the five-minute task. The user separately mandates a PICO Spatial SDK planar project (R-PLATFORM).

## 3. Key Moment

- **The moment a screen cannot achieve as directly**: after moving a sign, the curved dependency line stays attached while the user sees the goal flag, nearby risk stone, and the route's depth order together.
- **Placement on the immersion spectrum**: `user_requirement` Planar (R-PLATFORM); `product_decision` one Planar WindowContainer in Shared Space with no Stage because reading/editing dominates and PICO permits Planar in Shared Space (PF-ARCH-01 in UXR r3).
- **Entry path**: launch into a template-selection window, then enter the editor; no forced immersive transition.

## 4. Product Research (baseline anchors)

| Dimension | Content | Source |
|---|---|---|
| Competitor feature matrix | Trello/Planner/Asana document direct movement, status grouping and dependency connectors; usability remains a hypothesis. | UXR r6 §3A |
| Decision duration baseline | Five-minute end-to-end acceptance is binding; glance/action sub-targets are project hypotheses. | R-ACCEPT-5MIN; UXR r6 §10 |
| Industry safety · comfort conventions | Architecture is supported by PICO first-party docs; numeric sizing comes from the supplied skill methodology; physical comfort remains a device gap. | UXR r6 §3, §11; SRC-SKILL-SIZE |

## 5. Intent Definition (frozen items)

- **Domain / sub-domain**: personal planning / small-project spatial roadmap.
- **Risk level**: low product risk; medium interaction-loss risk for destructive deletion or unsaved edits.
- **Default space**: Shared Space.
- **Core scenario list**: choose one of four templates; create a target flag; add/edit/status a step; manipulate a step; connect steps; add/place a risk; save up to ten plans; restore; confirm export; preview fixed screenshot.
- **Core decisions**: what the goal is; which steps exist and in what order; each step's state; which dependency pairs matter; which risk belongs near which step; which local plan to restore/export.
- **Data / AI / sensors / permissions**: `user_requirement` local save (R-SAVE-10); `product_decision` local structured plan data only, with no AI/network/camera/anchors/plane detection/account permission because none serves the bounded tasks; basic eye/hand/controller interaction remains subject to downstream API validation.
- **Collaboration**: `product_decision` none, because R-PERSONAL defines an individual tool and only screenshot export is requested; revisit only if user adds multi-user scope.

## 6. Assumptions List

| # | Assumption | Confidence | Impact | Validation Plan |
|---|---|---|---|---|
| A1a | Typical editing session is ≤20 minutes. | medium; based on small-project framing, not user research | Drives density and rest guidance. | 5-user study: record voluntary stop; retain if median session ≤20 min, otherwise revise duration guidance. |
| A1b | Users edit seated or standing still. | low; no posture evidence | Determines manipulation ergonomics. | 5-user study: observe chosen posture; if >1/5 needs locomotion, revisit stationary interaction model. |
| A2 | Editable preset examples improve first-run completion. | medium; adjacent templates exist but effect untested | Determines preset content. | A/B with 5 users each: preset group must reach valid six-step route at least 60s faster median than blank; otherwise keep only minimal prompts. Conformance separately caps initial cards at 6. |
| A3 | A fixed 16:9 export preview is acceptable although the editor itself is resizable. | medium | Drives export crop and legibility checks. | Pass only after stakeholder accepts the 1600×900 logical preview and no label clips. |
| A4 | A compact text-only toolbox satisfies personal resource planning. | medium; inferred from non-professional scope | Prevents asset-manager scope creep. | 5-user task: after planning, ≥4/5 can name and retrieve all needed resource labels without requesting attachment fields; otherwise add bounded URL/note only after scope review. |
| A5 | Exact controller button mapping can be chosen downstream. | low; no specific mapping supplied | Affects controller QA, not the parity requirement itself. | API review then device test: every R-CONTROLLER action must be reachable; if any is not, mapping/design must change before acceptance. |
| A6 | App-only fixed screenshot satisfies export expectations. | medium; user requests fixed screenshot but crop boundary is unspecified | Affects privacy and export composition. | Stakeholder review of 1600×900 app-only sample: pass only on explicit approval and zero clipped labels; otherwise revise crop policy. |

## 7. Quality Contract (acceptance criteria)

- **Required business outcomes**: O1 create a non-empty goal flag; O2 create/edit/status/move/rotate up to 12 readable steps; O3 connect distinct step pairs with duplicate rejection and live-updating low-occlusion curves; O4 create/place a red, shape-redundant risk beside a step; O5 complete the user's 1+6+2+1 route; O6 save/restore ten local plans; O7 export a readable fixed-composition screenshot; O8 perform all core operations with a controller; O9 handle empty goal, card cap, duplicate edge, linked deletion and restore overwrite safely.
- **Success / efficiency criteria**: first usable goal + six steps + two connections + one risk in ≤5 minutes; common add/status/edit action ≤3 selections after target focus; connection creation ≤10 seconds; zero detached lines after card manipulation; export has no clipped goal/card/risk labels at the fixed composition.
- **Risks and must-not-fail items**: deletion Dialog reports incident edge count and removes edges atomically only after confirm; self/duplicate connection is blocked with visible reason; 13th card is not created; empty/whitespace goal never creates a flag; restore prompts when unsaved edits exist; export cannot proceed from invalid/empty goal; text remains front-facing while card frame rotates.
- **Preference for default visible primary windows**: exactly one Planar WindowContainer in Shared Space; no Stage, Toolbar, TabBar, Subwindow, Augment, or second primary window by default.
- **Domain-specialized components**: route canvas, goal flag, step sign with three states, curved dependency, risk stone, resource toolbox, semantic template card, step/risk edit panel, save-slot list, export frame. No task-assignment, dates, calendar, Gantt, workload, critical path, automation, comments or collaboration.
- **Real-time data trust**: all data is local; a persistent saved/unsaved indicator and last-saved timestamp are required. Restore/export show source plan name/time. No fabricated cloud sync or freshness.
- **PICO platform and spatial-design constraints by evidence class**: **user/architecture hard**—Shared Space + Planar, controller-accessible core operations, no device claims from Web; **supplied skill methodology contract**—Planar legal range 320×180–2700×1800dp, depth 640dp, about 1.75m/Dynamic worldScale, core 65°×40° and secondary 85°×55° checks, hit target floor 56×56dp and body 12sp; **validation hypotheses**—final physical readability, comfort and precision require downstream device evidence. The Android XR source independently calls 56dp recommended/optimal, not its minimum.
- **Originality requirement**: absorb direct manipulation, visible status, connector affordance and safe destructive flow from UXR §3A; reject column board, dashboard, schedule and critical-path paradigms; differentiate through the embodied path metaphor—goal distance, risk adjacency, foot-side resource shelf—inside one Planar container.
- **Design / readability / downstream acceptance plan**: six complete role documents; all core components have independent eight-section specs; state/transition and five preview maps reconcile at 100%; Large/Compact/Constrained and Reduce Motion trigger; fixed export preview shows representative six-step sample; independent gates pass; device-only checks remain listed as `not_performed`.

## 8. Requirements Traceability

### 8.0 User-source provenance appendix

Claim classes used across PM/UXR: `user_requirement` (verbatim ID below), `platform_fact` (first-party anchor), `workflow_methodology` (skill-owned constraint), `competitor_observation` (documented behavior only), `product_decision` (derived choice), `design_hypothesis` (must be tested), `validation_gap` (unknown).

| ID | Exact user wording (2026-08-12 brief) |
|---|---|
| R-TEMPLATES | “提供‘空白、活动筹备、学习计划、产品上线’四个模板。” |
| R-PLATFORM | “创建……PICO Spatial SDK planar 项目。” |
| R-METAPHOR | “远处旗帜代表最终目标，中间路标代表步骤，脚边工具箱代表资源，红色石块代表风险。用户应像在搭一条通往目标的小路。” |
| R-PERSONAL | “开发一个面向个人小项目的中文立体路线图，不要做专业项目管理系统。” |
| R-CARD-12 | “模板内最多 12 张步骤卡；每张卡有标题和未开始/进行中/完成三态。” |
| R-CONNECT | “从一个步骤卡拉出连线到另一个步骤卡表示前后关系。” |
| R-MANIPULATE | “可新建步骤卡、抓取移动、旋转（旋转后文字仍要可读，必要时始终面向用户）。” |
| R-GOAL | “用户填写目标后生成旗帜。” |
| R-RISK | “创建红色风险石并摆到步骤旁。” |
| R-LINE-SIMPLE | “连线随卡片移动更新并尽量不遮挡文字，只需做简单贝塞尔连线和重叠偏移。” |
| R-NO-SCHEDULE | “不要实现自动排期/关键路径算法。” |
| R-SAVE-10 | “支持保存十份本地方案和导出一张固定构图截图。” |
| R-EDGE-CASES | “处理删除已连线卡片、重复连线、超过 12 卡、空目标和恢复保存。” |
| R-CONTROLLER | “所有核心操作也要能通过手柄完成。” |
| R-PAGES | “实现页面：模板选择、空间编辑、步骤/风险编辑面板、保存列表、导出确认。” |
| R-ACCEPT-5MIN | “用户能在 5 分钟内创建一个目标、六步路线、两条连线和一个风险；移动卡片后连线不断；截图可读。” |

| Requirement | Implementation Node | Validation Method |
|---|---|---|
| Four templates: blank/event/learning/launch | Template selection state + TemplateCard | Trigger each preset and verify editable content/count ≤6. |
| Non-empty goal generates flag | Goal setup + GoalFlag | Empty/fallback test; visible flag after valid confirm. |
| Up to 12 titled three-state cards | StepSign + StepEditPanel | Add 12; verify 13th blocked; cycle all statuses. |
| Move, rotate, readable text | RouteCanvas + StepSign orientation rule | Manipulate sample; title remains user-facing. |
| Directed connections | ConnectionCurve | Create two; move endpoints; verify live attachment and text offset. |
| Risks beside steps | RiskStone + RiskEditPanel | Add, place, edit one risk; color+shape+label visible. |
| Local ten-plan save/restore | SaveList | Fill ten; handle 11th; restore after unsaved-edit confirm. |
| Fixed screenshot | ExportConfirmation + ExportFrame | Preview 1600×900 composition with sample; confirm/cancel/export paths. |
| Linked deletion | DeleteStepDialog | Show edge count; cancel preserves; confirm atomically removes card + edges. |
| Duplicate/self links | ConnectionCurve validation | Attempt duplicate/self link; no new edge and reason visible. |
| Controller coverage | Interaction mapping for every core action | Downstream emulator/device checklist; Web shows controller-help map. |
| Restore saved plan | SavedPlan provenance + recovery state | Relaunch simulation and load saved content/state/positions/edges/risks. |

## 9. Minimum Completeness Gate

| Check Item | Minimum Pass Condition | Evidence Anchor | Verdict |
|---|---|---|---|
| Background and intent | Six foundational facts and frozen intent are explicit. | §2–§5 | pass |
| Assumption governance | Every unknown has confidence, impact and validation. | §6 | pass |
| Quality contract | Nine complete, acceptance-testable contract items. | §7 | pass |
| Requirements traceability | Every mandatory outcome maps to node and validation. | §8 | pass |

| Field | Value |
|---|---|
| minimumCompletenessGate | pass |

## 10. Delivery and Recipients

- **Delivered now**: frozen intent definition + quality contract revision 7, derived from UXR r6 after CR-01–05.
- **Recipients**: Research Analyst, then Product Strategist for quality-contract freeze.
