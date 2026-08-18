# OnePageMap Engineering Notes

- PICO Spatial SDK 0.13.3, Android/Kotlin, `DefaultWindowContainer` Planar only.
- Keep `Main.kt` as entry wiring; business state lives in `ui`, rules in `domain`, persistence/export in `data`.
- Use `PicoTheme` and SpatialUI built-ins for standard controls. Do not import Material or Material3.
- Core limits: 12 steps and 10 local saves. Deleting a step must also remove incident connections. Duplicate/self connections are rejected.
- Selecting a step lists each incident route in the step editor; `删除路线` removes only that connection and keeps both endpoint nodes.
- Dragging a connector renders a live dashed Bézier preview; it turns green and highlights the destination when snapped, and only commits the connection on release.
- Step rotation applies to the complete card (shadow, surface, text, and connector); exported PNG mirrors that transform.
- Risk stones can be placed across the full canvas safety bounds; the goal is a draggable destination node and can receive connections from steps via `GOAL_NODE_ID`.
- Export preview owns the final confirmation: `导出并返回编辑` writes the PNG directly, then returns to `EDITOR` with a visible success message; export failure stays on preview.
- Export preview is height-driven inside the remaining body (`aspectRatio(..., matchHeightConstraintsFirst = true)`) so the 16:9 canvas never covers header actions.
- First launch uses the four-page `AppScreen.GUIDE`; completion persists through `RoadmapRepository`, and `QuickGuideDialog.kt` renders the controller-focusable guide content. Reopening help from the editor is read-only guidance: dismiss, back, and the final primary action all return to the current unsaved editor without creating a template or replacing plan/selection state.
- Emulator demo intents: `--es demo_mode guide`, `--es demo_mode editor`, and `--es demo_mode export`.
