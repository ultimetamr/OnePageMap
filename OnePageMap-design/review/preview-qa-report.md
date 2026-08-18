# Preview / QA Test Report · OnePageMap

> Active revision: 5 | scope=`web_design_validation_only` | sources: interaction r8, visual r3, design-system review r7 | preview r5 (content-equivalent rebuild of r4) | CR-PROC-01

## 2. Test Scope / Input Readiness

Object: single-file `preview.html`. Device validation not performed.

| Input | Evidence | Verdict |
|---|---|---|
| design review | design_review_r1 pass | pass |
| states/transitions | interaction §10 X1–X15 | pass |
| 5×8 component structure | visual §9A | pass |
| elements/bindings | visual §9A stable ids/fallbacks | pass |
| variants/states | visual §9A | pass |
| responsive/motion | interaction §9/§13 | pass |
| grammar | visual §3 | pass |

## 2.2 Coverage Manifest (before generation)

### States/transitions

States: S1,S2,S3,S4,S5,D1. Transitions: X1–X15. Each source=`interaction r8 §10`; D1/X7/X8 and dirty restore require Dialog; each transition has trigger/action/visible state or mutation feedback.

### renderSpec denominator (22)

| Component | IDs | Source | rule |
|---|---|---|---|
| RouteCanvas | canvas_goal,canvas_steps,canvas_edges,canvas_risks,canvas_toolbox | visual §9A | fallback/empty conditional |
| StepSign | step_status,step_title,step_connector,step_rotate,step_delete | visual §9A | export hides actions |
| ConnectionCurve | edge_path,edge_arrow,edge_error | visual §9A | no edges hides path |
| RiskStone | risk_shape,risk_title,risk_near,risk_delete | visual §9A | export hides delete |
| ActionPanel | panel_title,panel_body,panel_error,panel_cancel,panel_primary | visual §9A | variant changes content |

### bindings denominator (21)

RouteCanvas 5; StepSign 4; Curve 3; RiskStone 4; ActionPanel 5 =21 bindings. Every row in visual §9A has normal and fallback/error switch.

### Variants/states denominator

Variants: RouteCanvas2, StepSign3, Curve3, RiskStone3, ActionPanel9 =20. States: Canvas8, Step10, Curve10, Risk9, Panel8 =45 plus declared stacking precedence 5.

### Responsive/motion

Large=1600×900 rail; Compact=1280×800 bottom sheet; Constrained=960×720 menu+scroll; Reduce Motion removes slide/scale and uses fade/outline.

## 2.3 Declarative Checklist

| Check | Source | Selector/mechanism | Trigger | Expected/Actual(generator observation) | Verdict |
|---|---|---|---|---|---|
| manifest | §2.2 | manifest tables | manual count | 6/15/22/21/20+45+5/4 declared | pass |
| state machine | interaction §10 | `[data-state]`, renderScene | state buttons/actions | distinct state visible | pass |
| transitions | X1–X15 | `[data-action]` | each command | state/mutation feedback | pass |
| elements | 22 IDs | `[data-preview-id]` | inspect/demo | stable canonical selector; repeated instances use `data-instance-field` | pass |
| bindings | visual §9A | `[data-binding]` | normal/fallback/error | text/semantic changes | pass |
| variants/states | visual §9A | variant/state selects | each option | observable class/label | pass |
| Dialog | X7/X8/X4 | `#confirmDialog` | delete/restore | cancel+confirm block | pass |
| responsive/motion | interaction §9/§13 | `data-responsive`, `data-reduce-motion` | controls | structural reflow/no scale | pass |

## 2.4 Denominator Reconciliation (independent QA)

| Type | design | manifest | QA rebuilt | diff | verdict |
|---|---:|---:|---:|---:|---|
| states | 6 | 6 | 6 | 0 | pass |
| transitions | 15 | 15 | 15 | 0 | pass |
| render elements | 22 | 22 | 22 | 0 | pass |
| bindings | 21 | 21 | 21 | 0 | pass |
| variants | 20 | 20 | 20 | 0 | pass |
| component states+stack | 50 | 50 | 50 | 0 | pass |
| responsive/motion | 4 | 4 | 4 | 0 | pass |

## 3. Generation Mapping

### 3.1 States/transitions

S1–S5,D1 map to `[data-state]`. X1 template; X2 valid goal; X3 save list; X4 dirty restore Dialog; X5 export preview; X6 export confirm/return; X7 delete Dialog; X8 cancel/atomic delete; X9 add/cap; X10 edit/status; X11 move/rotate+upright text; X12 duplicate reject; X13 risk move; X14 resource update; X15 cancel draft. Each has a `[data-action]` trigger and visible result.

### 3.2 Elements→DOM

Each of 22 IDs maps to `[data-preview-id="<id>"]`: canvas_goal/steps/edges/risks/toolbox; step_status/title/connector/rotate/delete; edge_path/arrow/error; risk_shape/title/near/delete; panel_title/body/error/cancel/primary. Export hides handles; fallback shows guidance.

### 3.3 Bindings

All 21 declared binding paths map to `[data-binding]`; sample control writes normal/fallback/error evidence and changes visible steps/error. Missing endpoint behavior uses edge-error evidence.

### 3.4 Variants/states

`#componentSelect` scopes the component; `#variantSelect` contains all 20 variants and visibly changes label/attribute; `#componentStateSelect` exposes declared states and applies outline/opacity/pulse/labels. Dominant error/disabled/loading selections prove the five stacking precedence rules.

### 3.5 Responsive/motion

`#tierSelect` sets Large/Compact/Constrained: right rail → 420dp bottom sheet → 180dp labeled command menu plus scroll canvas. `#motionToggle` removes transforms/animation.

## 4. Requirements Traceability

All PM §8 requirements map to S1–S5/D1 and the five component families. Validation: trigger action + stable selector; 12/12 covered.

## 5. Sample Data

Goal=筹备读书会; six steps=确定主题/选定书目/确认场地/发布报名/准备引导问题/举办读书会; links 2; risk=场地临时变更; resources=场地联系人/报名表/讨论提纲. Fallbacks are human-readable.

## 6. Web Tolerance

Exact ID relationship/token presence only; excludes visual diff, CSS-to-physical size, device color, Web/PICO parity.

## 7. Device Boundary

Physical distance/readability, occlusion/FOV, fatigue, hand/controller precision, runtime performance and actual screenshot are `not_performed`. Web logical coverage passed independent QA; it is not device evidence.

## 8. Defects

CR-PV-01–03 and CR-PROC-01 Preview gate are closed by exact-r5 independent review `/root/design_package/preview_review_r4`.

minimumCompletenessGate=pass; previewImplementationFidelity=pass; deviceValidation.status=not_performed; independent evidence=`/root/design_package/preview_review_r4`.

## 9. Preview r5 Itemwise Implementation Maps

### 9.1 State/transition map

| Item | Selector/trigger | Visible result |
|---|---|---|
| S1 | sceneSelect=S1 | template choices copy; canvas hidden |
| S2 | X1 or sceneSelect=S2 | goal-required scene; canvas hidden |
| S3 | X2/restore/cancel | full route editor |
| S4 | X3 | save shelf; canvas hidden |
| S5 | X5 | fixed 1600×900 export view; handles hidden |
| D1 | X7 | blocking delete Dialog |
| X1 | `[data-action=x1]` | S1→S2 template chosen |
| X2 | `[data-action=x2]` | validates goal, S2→S3 |
| X3 | `[data-action=x3]` | S3→S4 |
| X4 | `[data-action=x4]` | dirty restore Dialog; confirm reloads, cancel S3 |
| X5 | `[data-action=x5]` | S3→S5 |
| X6 | `[data-action=x6]` | export Dialog; confirm capture feedback/cancel |
| X7 | `[data-action=x7]` | S3→D1 |
| X8 | `#dialogCancel/#dialogConfirm` | cancel preserves; confirm removes first card + edge |
| X9 | `[data-action=x9]` | appends step or visible 12-cap reason |
| X10 | `[data-action=x10]` | mutates first title/status feedback |
| X11 | `[data-action=x11]` | moves/rotates card, counter-rotates text, updates path d |
| X12 | `[data-action=x12]` | creates once; duplicate/self attempt shows edge_error |
| X13 | `[data-action=x13]` | edits/moves risk and updates near label |
| X14 | `[data-action=x14]` | updates visible toolbox text |
| X15 | `[data-action=x15]` | discards draft feedback, original data retained |

### 9.2 renderSpec element map (22)

| Component.element | Selector | Condition/behavior |
|---|---|---|
| RouteCanvas.canvas_goal | `[data-preview-id=canvas_goal]` | goal normal/fallback |
| RouteCanvas.canvas_steps | `[data-preview-id=canvas_steps]` | collection/empty/error |
| RouteCanvas.canvas_edges | `[data-preview-id=canvas_edges]` | hidden endpoint fallback |
| RouteCanvas.canvas_risks | `[data-preview-id=canvas_risks]` | risk manipulation |
| RouteCanvas.canvas_toolbox | `[data-preview-id=canvas_toolbox]` | resource mutation |
| StepSign.step_status | canonical first `[data-preview-id=step_status]`; repetitions `[data-instance-field=step_status]` | semantic shape+label |
| StepSign.step_title | canonical first selector | title/update/fallback |
| StepSign.step_connector | canonical first selector | X12 |
| StepSign.step_rotate | canonical first selector | X11 |
| StepSign.step_delete | canonical first selector | X7 via command equivalent |
| Curve.edge_path | `[data-preview-id=edge_path]` | X11 geometry/export width |
| Curve.edge_arrow | `[data-preview-id=edge_arrow]` | direction |
| Curve.edge_error | `[data-preview-id=edge_error]` | duplicate/missing endpoint |
| Risk.risk_shape | `[data-preview-id=risk_shape]` | triangle + red |
| Risk.risk_title | `[data-preview-id=risk_title]` | X13/fallback |
| Risk.risk_near | `[data-preview-id=risk_near]` | linked/unlinked |
| Risk.risk_delete | `[data-preview-id=risk_delete]` | hidden export |
| Panel.panel_title | `[data-preview-id=panel_title]` | scene/variant title |
| Panel.panel_body | `[data-preview-id=panel_body]` | unique panel body |
| Panel.panel_error | `[data-preview-id=panel_error]` | validation |
| Panel.panel_cancel | `[data-preview-id=panel_cancel]` | X15 |
| Panel.panel_primary | `[data-preview-id=panel_primary]` | X10/enabled binding |

### 9.3 Binding map (21)

Every row uses `#bindingSelect` + `#bindingMode` to independently show normal/fallback/error in visible `#labOutput`; selectors on live scene are listed.

| Binding | Live selector |
|---|---|
| goal | `[data-binding=goal]` |
| steps | `[data-binding=steps]` |
| connections | `[data-binding=connections]` |
| risks | `[data-binding=risks]` |
| resources | `[data-binding=resources]` |
| steps.status | `[data-binding='steps.status']` |
| steps.title | `[data-binding='steps.title']` |
| steps.id | `[data-binding='steps.id']` |
| steps.rotation | `[data-binding='steps.rotation']` |
| edge.endpoints | `[data-binding='edge.endpoints']` |
| edge.positions | `[data-binding='edge.positions']` |
| edge.validation | `[data-binding='edge.validation']` |
| risk.title | `[data-binding='risk.title']` |
| risk.nearStepId | `[data-binding='risk.nearStepId']` |
| risk.severity | `[data-binding='risk.severity']` |
| risk.id | `[data-binding='risk.id']` |
| panel.title | `[data-binding='panel.title']` |
| panel.body | `[data-binding='panel.body']` |
| panel.validation | `[data-binding='panel.validation']` |
| panel.primaryEnabled | `[data-binding='panel.primaryEnabled']` |
| saves | visible lab row; save scene S4 |

### 9.4 Variant map (20)

All use `#variantSelect`; JS applies observable target behavior.

| Component | Variants | Observable behavior |
|---|---|---|
| RouteCanvas | editor; export | handles shown/hidden |
| StepSign | regular; compact; export | target attribute/label; export hides actions |
| ConnectionCurve | normal; overlap-offset; export | stroke 4/7/5dp |
| RiskStone | linked; unlinked; export | near label/tether meaning; actions hidden export |
| ActionPanel | template; goal; step; risk; resource; save; export; delete; dirty-restore | title/body context changes |

### 9.5 Component state map (45) and precedence (5)

`#statePairSelect` contains one option for every pair below and applies the state only to its owning target, with visible class/label. `#precedenceSelect` contains five combination rules and reports/applies the dominant state.

| Component | Itemwise states |
|---|---|
| RouteCanvas (8) | default, focused, selected, disabled, loading, empty, error, overflow |
| StepSign (10) | default, focused, selected, dragging, connecting, disabled, loading, empty, error, overflow |
| ConnectionCurve (10) | default, focused, selected, acquiring, duplicate_error, disabled, loading, empty, error, overflow |
| RiskStone (9) | default, focused, selected, dragging, disabled, loading, empty, error, overflow |
| ActionPanel (8) | default, focused, selected, disabled, loading, empty, error, overflow |

Precedence rows: Canvas error>loading>disabled>selected>focused; Step error>disabled>dragging>selected>focused; Curve error>disabled>selected>focused; Risk error>disabled>dragging>selected>focused; Panel error>loading>disabled>focused.

### 9.6 Responsive / Reduce Motion

| Scenario | Trigger | Actual structure |
|---|---|---|
| Large | tierSelect Large | right 320dp rail, route canvas primary |
| Compact | tierSelect Compact | 420dp bottom sheet |
| Constrained | tierSelect Constrained | 180dp visible labeled command menu + scroll canvas |
| Reduce Motion | motionToggle | animation/transition/transform disabled; semantic feedback retained |

Generation-side mapping completeness: 6/6 states, 15/15 transitions, 22/22 elements, 21/21 bindings, 20/20 variants, 45/45 component states, 5/5 precedence, 4/4 responsive/motion. QA actual verdict remains independent.

CR-PV-03 adds substantive S1 template, S2 goal-input, S4 save-list, and S5 fixed export-frame scenes; X2 now triggers empty-goal validation; confirmed dirty Back reaches S1; X15 restores a real draft; Curve.overflow closes the state denominator.
