## Заменить printStackTrace на LOGGER

13 вызовов `Throwable.printStackTrace()` в catch-блоках, минующих логгер мода. По правилам `CLAUDE.md` логирование должно идти через `NTCommon.debug(...)` / `LOGGER` — иначе на NeoForge stack trace уходит в stderr мимо `ThresholdFilter` из `log4j2.xml`, и флаг `-Dneoterra.debug=true` на него не влияет.

Полный список:

- `common/src/main/java/neoterra/world/worldgen/cell/biome/type/BiomeTypeColors.java:25`
- `common/src/main/java/neoterra/concurrent/cache/CacheEntry.java:43`
- `common/src/main/java/neoterra/concurrent/task/LazyCallable.java:113`
- `common/src/main/java/neoterra/concurrent/task/LazyCallable.java:136`
- `common/src/main/java/neoterra/client/gui/Toasts.java:20`
- `common/src/main/java/neoterra/platform/ConfigUtil.java:27`
- `common/src/main/java/neoterra/client/gui/screen/presetconfig/PresetListPage.java:70`
- `common/src/main/java/neoterra/client/gui/screen/presetconfig/PresetListPage.java:148`
- `common/src/main/java/neoterra/client/gui/screen/presetconfig/PresetConfigScreen.java:222`
- `common/src/main/java/neoterra/client/gui/screen/presetconfig/PresetEditorPage.java:103`
- `common/src/main/java/neoterra/client/gui/screen/presetconfig/PresetEditorPage.java:202`
- `common/src/main/java/neoterra/world/worldgen/feature/template/template/FeatureTemplateManager.java:37`
- `common/src/main/java/neoterra/world/worldgen/feature/template/template/FeatureTemplate.java:315`

Типовой паттерн:

```java
try {
    ...
} catch (IOException e) {
    e.printStackTrace();
}
```

Заменить на `LOGGER.error("...", e)` (или `NTCommon.debug` для невалидных, но ожидаемых случаев), логгер `"NeoTerra"` уже есть в `NTCommon`.
