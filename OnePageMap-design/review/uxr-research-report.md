# User Research Report · OnePageMap

> Role: `research_analyst` | Active revision: 6 | Source: user brief ID `SHA256:7d032d6f52598f0be33629355776f29007801d3e691df3e035767eab9ee878c5` + PM intent r1 only | Observation date: 2026-08-12 | CR-01–05 patched

## 0. Reasoning Guidance

Evidence constrains requirements and risks; it does not select a layout or visual style. Competitor observations are limited to documented behavior and may be absorbed only as needs/opportunities.

## 1. Direct Description of Outputs

Five-category evidence plus a domain model for a small personal spatial roadmap.

## 2. Research Goals and Questions

- **Assumptions to validate**: A1–A6 in PM §6, especially five-minute creation, controller parity, and fixed screenshot readability.
- **Methods used**: user brief analysis; first-party competitor help pages; official PICO/Android XR platform material; explicit evidence gaps.
- **Sample**: no recruited participants yet. Intended validation sample: five Chinese-speaking individual planners, including two XR novices; this is a gap, not claimed research.

## 3. Five Categories of Research Evidence

Canonical claim class: `user_requirement | platform_fact | workflow_methodology | competitor_observation | product_decision | design_hypothesis | validation_gap`. Source type is separate.

### 3.0 Atomic source registry

| Source ID | Exact title/location | Type | Accessed |
|---|---|---|---|
| SRC-USER-01 | User brief, immutable digest `SHA256:7d032d6f52598f0be33629355776f29007801d3e691df3e035767eab9ee878c5`; verbatim units frozen in PM intent r1 | user_supplied | 2026-08-12 |
| SRC-TRELLO-MOVE | Trello “Move cards or lists”, https://support.atlassian.com/trello/docs/moving-cards-or-lists/ | official competitor | 2026-08-12 |
| SRC-TRELLO-DELETE | Trello “Archive or delete a card”, https://support.atlassian.com/trello/docs/archiving-and-deleting-cards/ | official competitor | 2026-08-12 |
| SRC-PLANNER | Microsoft Support “Create a plan in Microsoft Planner”, https://support.microsoft.com/en-us/Planner/create-a-plan-in-microsoft-planner | official competitor | 2026-08-12 |
| SRC-ASANA | Asana Help “Plan and execute projects with Timeline”, https://help.asana.com/s/article/plan-and-execute-projects-with-timeline | official competitor | 2026-08-12 |
| SRC-PICO-ARCH | PICO OS 6 Overview, https://developer.picoxr.com/document/discover/pico-os-6-overview/ | official platform | 2026-08-12 |
| SRC-PICO-COORD | PICO “Convert coordinate spaces”, https://developer.picoxr.com/document/spatial-sdk/convert-coordinate-space/ | official platform | 2026-08-12 |
| SRC-ANDROID-XR | Android XR “Scale, sizes, and visual design”, https://developer.android.com/design/ui/xr/guides/visual-design | official adjacent platform | 2026-08-12 |
| SRC-SKILL-SIZE | `knowledge/spatial-window-sizing-methodology.md` in installed pico-spatial-app-designer skill | workflow methodology | 2026-08-12 |

| ID | Category | Atomic claim | Claim class | Source ID / Type | Scope | Confidence | Validation Plan |
|---|---|---|---|---|---|---|---|
| EV-M1 | market | Trello documents click-and-drag card movement. | competitor_observation | SRC-TRELLO-MOVE / official competitor | cited 2D mechanic | high | Prototype; measure unprompted use. |
| EV-M2 | market | Planner documents Board grouping and dragging tasks. | competitor_observation | SRC-PLANNER / official competitor | cited 2D mechanic | high | Prototype without copying columns. |
| EV-U1 | user | Goal + 6 steps + 2 links + 1 risk must be creatable in 5 minutes. | user_requirement | SRC-USER-01 R-ACCEPT-5MIN | OnePageMap | high | Time five first-run sessions. |
| EV-D1 | domain | Asana documents dragging a connector to create a dependency. | competitor_observation | SRC-ASANA / official competitor | cited 2D mechanic | high | Measure discovery in prototype. |
| EV-D2 | domain | Auto-scheduling and critical-path algorithms are prohibited. | user_requirement | SRC-USER-01 R-NO-SCHEDULE | OnePageMap | high | Absence audit. |
| EV-P1 | platform | PICO says Planar suits traditional 2D information and Stage is unconstrained/immersive. | platform_fact | SRC-PICO-ARCH / official | architecture | high | Downstream API review. |
| EV-P2 | platform | PICO View coordinates are top-left virtual pixels; Entity coordinates are metric and center-origin. | platform_fact | SRC-PICO-COORD / official | coordinate conversion | high | Separate coordinate models downstream. |
| EV-S1 | safety | Android XR calls 56dp recommended/optimal and recommends hover/focus. | platform_fact | SRC-ANDROID-XR / official adjacent | accessibility anchor, not PICO proof | medium | PICO device precision test. |
| EV-S2 | safety | This workflow requires a 56dp target floor, 12dp body floor and explicit FOV sizing chain. | workflow_methodology | SRC-SKILL-SIZE / local skill | design-package gate | high | Review sizing derivation; device facts remain open. |
| GAP-U1 | user | Preferred session length/posture/access needs are unknown. | validation_gap | none | target users | low | Moderated test with metrics in PM assumptions. |
| GAP-S1 | safety | Physical readability/fatigue/occlusion/device screenshot fidelity are unknown. | validation_gap | none | PICO device | low | Device validation; never close in Web preview. |

- **Source conflict handling**: PICO-specific rules take precedence for runtime architecture; Android XR target guidance is used as a conservative adjacent accessibility anchor only where the supplied PICO methodology also specifies 56dp.

## 3A. Competitive Benchmark

Every row is exactly one fact. The four required benchmark dimensions are named inside the atomic claim.

| claimId | canonicalClass | sourceId | claim |
|---|---|---|---|
| CB-T-01 | competitor_observation | SRC-TRELLO-MOVE | Trello functional: cards can be moved. |
| CB-T-02 | competitor_observation | SRC-TRELLO-MOVE | Trello interaction: card movement uses click-and-drag. |
| CB-T-03 | competitor_observation | SRC-TRELLO-MOVE | Trello visual: the cited page depicts list columns containing cards. |
| CB-T-04 | validation_gap | SRC-TRELLO-MOVE | Trello spatial: the cited page does not document embodied direction or distance. |
| CB-T-05 | competitor_observation | SRC-TRELLO-DELETE | Trello safety: permanent deletion requires confirmation. |
| CB-P-01 | competitor_observation | SRC-PLANNER | Planner functional: Grid view is documented. |
| CB-P-02 | competitor_observation | SRC-PLANNER | Planner functional: Board view is documented. |
| CB-P-03 | competitor_observation | SRC-PLANNER | Planner functional: Charts view is documented. |
| CB-P-04 | competitor_observation | SRC-PLANNER | Planner functional: Calendar view is documented. |
| CB-P-05 | competitor_observation | SRC-PLANNER | Planner interaction: Board tasks can be dragged between columns. |
| CB-P-06 | competitor_observation | SRC-PLANNER | Planner visual: the cited page depicts board forms. |
| CB-P-07 | validation_gap | SRC-PLANNER | Planner spatial: the cited page does not document embodied direction or distance. |
| CB-A-01 | competitor_observation | SRC-ASANA | Asana functional: dates are documented. |
| CB-A-02 | competitor_observation | SRC-ASANA | Asana functional: dependencies are documented. |
| CB-A-03 | competitor_observation | SRC-ASANA | Asana functional: critical path is documented. |
| CB-A-04 | competitor_observation | SRC-ASANA | Asana interaction: a dependency connector is dragged between tasks. |
| CB-A-05 | competitor_observation | SRC-ASANA | Asana visual: tasks appear as horizontal bars. |
| CB-A-06 | competitor_observation | SRC-ASANA | Asana spatial: the cited page uses a 2D time axis. |
| CB-B-01 | product_decision | SRC-TRELLO-MOVE | Absorb direct manipulation at the requirement level. |
| CB-B-02 | product_decision | SRC-TRELLO-DELETE | Absorb consequence clarity for destructive actions. |
| CB-B-03 | product_decision | SRC-PLANNER | Absorb quick status scanning at the requirement level. |
| CB-B-04 | user_requirement | SRC-USER-01:R-PERSONAL | Reject professional multi-view dashboard scope. |
| CB-B-05 | product_decision | SRC-ASANA | Absorb connector affordance at the requirement level. |
| CB-B-06 | user_requirement | SRC-USER-01:R-NO-SCHEDULE | Reject schedule and critical-path algorithms. |
| CB-D-01 | design_hypothesis | SRC-USER-01:R-METAPHOR | A route metaphor may make dependency and goal progress more memorable. |
| CB-D-02 | user_requirement | SRC-USER-01:R-CARD-12 | Complexity is capped at 12 cards. |
| CB-D-03 | user_requirement | SRC-USER-01:R-SAVE-10 | Local plans are capped at ten. |
| CB-G-01 | validation_gap | SRC-USER-01 | No directly comparable PICO spatial personal-roadmap product was established from this bounded sample. |
| CB-O-01 | product_decision | SRC-USER-01:R-PERSONAL | Competitor absorption remains at the needs/opportunities layer. |

## 4. Domain Model

- **Domain workflow**: choose blank/preset → name goal → generate flag → create/edit up to 12 steps → arrange and rotate → connect prerequisite pairs → place risks/resources → review progress → save/restore → export fixed composition.
- **Decision variables**: goal text validity; step count; title and three-state value; 2D position + presentation rotation; directed edge endpoints; risk title/nearby step; save-slot availability; export crop readability.
- **Data entities and timeliness**: `Plan` (changes per edit, persisted locally); `Goal` (required); `Step[0..12]`; `Connection` (immediate geometric update); `Risk`; `ResourceItem`; `SavedPlan[0..10]`; `ExportSnapshot` (point-in-time). No remote freshness.
- **Specialized risks**: deleting a connected step leaves orphan edges; duplicate/self links confuse order; 13th card exceeds readability/performance contract; empty goal cannot anchor route; restore may overwrite unsaved work; rotation can make text illegible; overlapping curves obscure labels.
- **User mental model**: “I am laying a path toward a flag,” not maintaining a database or schedule.
- **Mature patterns**: direct manipulation, clear selection, undo/cancel before destructive change, templates as starting content, status visible on card.
- **Anti-patterns**: dashboards, Gantt dates, critical path, team assignment, automation, nested projects, freeform infinite canvases, color-only state, rotated text, connections that detach.

## 5. Persona

### Persona 1: 小林 / “想在五分钟里把小事情想清楚”

| Dimension | Content |
|---|---|
| Basic information | Provisional persona, 20–40, student/knowledge worker/independent maker; XR novice-to-intermediate. Source: user brief + assumption. |
| Use scenario and frequency | Seated/standing, occasional personal project, short sessions; not yet user-validated. |
| Goals / motivations | See an achievable route, remember dependencies and risks, share one readable image. |
| Pain points / frustrations | Professional tools require dates, roles, multiple views and metadata before the idea feels actionable. |
| Spatial usage habits | Assumed stationary; 5–20 min tolerance is a validation target, not evidence. |
| Accessibility needs | Color-independent shapes/labels, controller parity, readable unrotated text; individual needs unknown. |
| Key quote (verbatim) | “用户应像在搭一条通往目标的小路。” — user brief |

## 6. Journey Map

| Stage | 进入 | 初次上手 | 搭路 | 检查与风险 | 保存/分享 |
|---|---|---|---|---|---|
| User goal | Pick a useful start | See a flag quickly | Build six readable steps | Understand order and obstacle | Recover later/share image |
| Behavior | Choose template | Enter goal | Add, edit, move, rotate, connect | Add risk; move card and inspect line | Save; open export confirmation |
| Touchpoint | Planar template state | Goal input in Planar | Spatial canvas within Planar | Canvas + in-window edit panel | Save list / export preview |
| Thought | “Don't make me configure a project.” | “Now I have somewhere to go.” | “This is my path.” | “What could block me?” | “Can I trust the saved/shareable result?” |
| Emotion | 😐 | 😀 | 😀 | 😐 | 😀 |
| Pain point | Template ambiguity | Empty goal | manipulation/connection discoverability | occlusion/duplicate links | overwrite/crop risk |
| Opportunity | Four semantic previews | inline validation | handles + controller hints | offset curves + risk adjacency | explicit overwrite/export confirmation |

- **Emotional low point**: initiating and completing a connection without losing card selection.
- **Key opportunities**: single focus, visible connector handles, persistent route context, simple confirmation, no scheduling features.

## 7. Key Findings

| # | Finding | Evidence | Confidence | Design Implication |
|---|---|---|---|---|
| F1 | Adjacent products implement direct dragging; target-user expectation/discoverability in XR is a hypothesis. | Trello/Planner official docs | high for feature; low for usability inference | Prototype direct movement and measure unprompted first-attempt success. |
| F2 | Asana implements dependency handles alongside scheduling; handle understandability for OnePageMap is unknown. | Asana official docs + user exclusion | high for feature; low for usability inference | Prototype connector handle without dates; pass if 4/5 users form a valid edge without instruction. |
| F3a | Trello documents confirmation before permanent card deletion. | SRC-TRELLO-DELETE | high | competitor_observation only; informs risk discovery. |
| F3b | Linked-card deletion must be handled. | SRC-USER-01 R-EDGE-CASES | high | user_requirement: Dialog lists removed connection count and supports cancel. |
| F4a | One Planar window is the selected architecture. | SRC-PICO-ARCH + R-PLATFORM | medium | product_decision reviewed at concept gate. |
| F4b | Internal position/distance/adjacency may improve first-run comprehension. | SRC-USER-01 R-METAPHOR | medium | design_hypothesis: compare against flat-list counterfactual. |

## 8. Wearing Posture and Field-of-View Insights

- **Usual posture**: unknown; design assumption seated/standing still.
- **Arm range**: unknown on PICO device; indirect controller and eye-hand input avoid sustained reach.
- **Central field of view**: keep selected card and goal-route center as single primary focus; test against the supplied PICO methodology core 65°×40° zone.
- **Fatigue threshold**: unknown; design targets 20-minute sessions and no forced head/body movement, pending device test.

## 9. Eye-Hand Interaction Usability

- **Hit rate**: evidence gap; no measured PICO hit rate.
- **Low-load assumption**: indirect interaction lets hands rest; validate on device.
- **Mis-touch feedback**: every actionable target requires hover/focus, selected, pressed and disabled feedback; recommended target floor 56dp.

## 10. Duration Baseline Data

| Decision Type | Duration Anchor | Source |
|---|---|---|
| Glance decision | Project target: see goal/step status in ≤2s; to validate | assumption based on five-minute user acceptance |
| Fine-tuning dwell | One move/rotate action ≤10s; to validate | product quality target, not external evidence |
| End-to-end build | ≤5 min for 1 goal + 6 steps + 2 links + 1 risk | user brief |

## 11. Motion Sickness / Fatigue and Safety

- **Risk scenarios**: large forced canvas motion, camera movement, text rotating with cards, long arm-held manipulation.
- **High Motion label**: no; design prohibits camera motion and large forced displacement.
- **Duration/rest**: target short sessions; recommend pause after 20 minutes until device evidence exists.
- **Boundary**: comfort, fatigue, hit precision and physical readability remain `not_performed` until device validation.

## 12. Minimum Completeness Gate

| Check Item | Evidence Anchor | Verdict |
|---|---|---|
| Five categories evidence/gaps | §3 | pass |
| ≥3 competitors across four dimensions | §3A | pass |
| Complete domain model | §4 | pass |
| Persona/Journey/findings sourced or labeled assumptions | §5–§7 | pass |
| Quantitative/safety values or explicit gaps | §8–§11 | pass |

| Field | Value |
|---|---|
| minimumCompletenessGate | pass |

## 13. Delivery and Recipients

- **Deliverables**: research evidence + domain model revision 5 after CR-01–04.
- **Recipients**: Interaction/Spatial Designer, Visual Designer, PM.
