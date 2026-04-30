# Следить за публичным API Fabric для регистрации PresetEditor

`fabric/src/main/java/neoterra/fabric/mixin/MixinPresetEditor.java:16`

Сейчас editor для `neoterra:overworld` цепляется к `PresetEditor.EDITORS` через `@Redirect` на `Map.of(...)` в `<clinit>` (см. ссылку выше) — потому что в Fabric API нет аналога NeoForge `RegisterPresetEditorsEvent`. На NeoForge всё уже на штатном API в `neoforge/src/main/java/neoterra/neoforge/NTNeoForgeClient.java`:

```java
event.register(NTWorldgenKeys.OVERWORLD_WORLD_PRESET, (screen, ctx) -> new PresetConfigScreen(screen));
```

Если в Fabric API когда-нибудь появится публичный hook для регистрации `PresetEditor` (по аналогии с NeoForge `RegisterPresetEditorsEvent`) — выкинуть `MixinPresetEditor` и зарегистрировать editor через этот hook в `NTFabric` (или в его клиентском entry point'е), как уже сделано на NeoForge.
