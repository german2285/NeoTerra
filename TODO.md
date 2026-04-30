# TODO

Технический долг репозитория: TODO/FIXME-маркеры, `@Deprecated`-помеченные элементы и закомментированный Java-код. По каждому пункту нужно принять решение: удалить, реализовать, переформулировать или оставить.

Все usage-counts посчитаны вне файла объявления, по `common/`, `fabric/`, `neoforge/` (без `generated/`/`build/`).

---

## 1. Закомментированный Java-код (11 блоков)

- [ ] `common/.../cell/heightmap/Heightmap.java:133` — альтернативная инициализация `islandsOceans` через `ContinentLerper3` с другим порядком параметров. Живая строка ниже использует `oceans = new ContinentLerper3(deepOcean, shallowOcean, coast, ...)`.
- [ ] `common/.../cell/heightmap/Levels.java:29-31` — граничный `if (value >= 1.0F) return this.worldHeight - 1;` в `scale()`. Сейчас просто `(int) (value * this.worldHeight)`.
- [ ] `common/.../cell/biome/type/BiomeType.java:99-109` — три статических метода `apply` / `applyLinear` / `applyCurve(Cell)`. Ниже используется `get(temperature, moisture)` напрямую.
- [ ] `common/.../client/data/LanguageProvider.java:84-100` — заглушки `addEnchantment(...)` и блок `addBiome(...)` под `/* */`. Активны только `addEffect`, `addEntityType`, `addItemStack`.
- [ ] `common/.../platform/RegistryUtil.java:17-39` — старая реализация до перехода на `@ExpectPlatform`-прокси (строки 41-43+).
- [ ] `common/.../data/worldgen/preset/PresetTerrainProvider.java:18-20` — старый `LinearSplineFunction.builder(ridge).addPoint(-1.0D, -0.4D).build()`. Заменён на `DensityFunctions.constant(0.0D)`.
- [ ] `common/.../mixin/MixinContext.java:10-22` — закомментированный `@ModifyVariable` на `SurfaceRules.Context.<init>`. После удаления тело mixin'а станет пустым — нужно решить, удалять класс целиком и снимать ссылку из `neoterra-common.mixins.json` или оставлять как заготовку.
- [ ] `common/.../data/worldgen/preset/PresetPlacedFeatures.java:184` — отключённая регистрация `TREES_WINDSWEPT_SAVANNA` (с `NTPlacementModifiers.disabled()`). Связано с TODO 2.23.
- [ ] `common/.../data/worldgen/tags/NTBlockTagsProvider.java:35-39` — закомментированная else-ветка про `oreCompatibleStoneOnly` (внутри ещё голый `//TODO` — см. 2.7).
- [ ] `common/.../world/worldgen/feature/BushFeature.java:70` — старый способ выставления Y через `world.getHeight(Heightmap.Types.WORLD_SURFACE, ...)`.
- [ ] `fabric/.../platform/fabric/RegistryUtilImpl.java:19-30` — старые версии `getWritable` / `createRegistry` / `createDataRegistry`. Заменены ниже.

---

## 2. TODO / FIXME (31 маркер)

### Голые / без содержания (8)

- [ ] 2.1. `common/.../config/PerformanceConfig.java:17` — голый `//TODO` без текста.
- [ ] 2.2. `common/.../world/worldgen/util/Scaling.java:5` — `// TODO clean this up some more`.
- [ ] 2.3. `common/.../client/gui/widget/Slider.java:10` — `//TODO this should be cleaned up`.
- [ ] 2.4. `common/.../client/data/NTLanguageProvider.java:7` — `// TODO add some more languages` (общее пожелание).
- [ ] 2.5. `common/.../data/worldgen/preset/settings/SurfaceSettings.java:33` — `public float screeValue; //TODO` без пояснения.
- [ ] 2.6. `common/.../data/worldgen/preset/settings/CaveSettings.java:31` — голый `//TODO` без пояснения.
- [ ] 2.7. `common/.../data/worldgen/tags/NTBlockTagsProvider.java:38` — `//TODO` внутри полностью закомментированной else-ветки (см. блок 1.9).
- [ ] 2.8. `common/.../mixin/MixinBiomeGenerationSettings.java:17` — `//TODO do this with access wideners instead` на работающем mixin'е; миграция не запланирована.

### Содержательные (22)

- [ ] 2.9. `common/.../world/worldgen/cell/terrain/Populators.java:20` — `//TODO remove all the seed parameters`. Связано с `@Deprecated` на record-параметрах (см. 3.14-3.24).
- [ ] 2.10. `common/.../world/worldgen/cell/terrain/Populators.java:234` — `// TODO only use erosion + ridge combos that respect continentalness`.
- [ ] 2.11. `common/.../world/worldgen/cell/terrain/populator/IslandPopulator.java:76` — `// TODO sample noise for this, thisll give us the islands we want`.
- [ ] 2.12. `common/.../world/worldgen/feature/ErodeFeature.java:60` — `// TODO expose desert erosion variance to config`.
- [ ] 2.13. `common/.../world/worldgen/biome/NTBiomes.java:18` — `//TODO add shrubbery and stuff`.
- [ ] 2.14. `common/.../world/worldgen/surface/rule/NTSurfaceRules.java:31` — `//TODO: Convert to MapCodec`.
- [ ] 2.15. `common/.../world/worldgen/densityfunction/tile/TileCache.java:58` — `//TODO i dont think get should be able to return null here`.
- [ ] 2.16. `common/.../world/worldgen/densityfunction/StructureGenMask.java:3` — `//TODO remove ConditionalArrayCache and make it part of this`.
- [ ] 2.17. `common/.../world/worldgen/densityfunction/CellSampler.java:123` — `//TODO move this somewhere else`.
- [ ] 2.18. `common/.../client/gui/screen/presetconfig/SurfaceSettingsPage.java:19` — `private Slider dirtSteepness; //TODO ensure is above rockSteepness` (инвариант значений).
- [ ] 2.19. `common/.../client/gui/screen/presetconfig/SurfaceSettingsPage.java:20` — `private Slider screeSteepness; //TODO ensure is above dirtSteepness` (инвариант значений).
- [ ] 2.20. `common/.../client/gui/screen/presetconfig/PresetConfigScreen.java:86` — `//TODO update the seed edit box`.
- [ ] 2.21. `common/.../client/data/NTLanguageProvider.java:170` — `//TODO move the trailing colon and space to PresetEditorPage`.
- [ ] 2.22. `common/.../data/worldgen/preset/PresetConfiguredCarvers.java:30` — `//TODO make lava level configurable`.
- [ ] 2.23. `common/.../data/worldgen/preset/PresetPlacedFeatures.java:183` — `// TODO shattered savanna`. Связано с блоком 1.8.
- [ ] 2.24. `common/.../data/worldgen/preset/PresetSurfaceRuleData.java:18` — `//TODO add forest surfaces`.
- [ ] 2.25. `common/.../data/worldgen/preset/settings/Presets.java:150` — `//TODO make mushroom islands bigger`.
- [ ] 2.26. `common/.../data/worldgen/preset/settings/Presets.java:281` — `throw new UnsupportedOperationException("TODO")` — это **живой код**, а не комментарий. Нужно понять, какой кодпуть в это попадает.
- [ ] 2.27. `fabric/.../NTFabric.java:34` — `//TODO merge this with forge's datagen since they're the same now`.
- [ ] 2.28. `fabric/.../platform/fabric/RegistryUtilImpl.java:45` — `// TODO what does SyncOption.SKIP_WHEN_EMPTY do?`.
- [ ] 2.29. `neoforge/.../platform/neoforge/RegistryUtilImpl.java:24` — `//TODO make this non public`.
- [ ] 2.30. `common/.../client/gui/screen/presetconfig/PresetListPage.java:286` — **FIXME** `//FIXME delete old pack before save` (потенциальный data-loss bug при перезаписи пресета). Единственный FIXME в репо.

---

## 3. `@Deprecated` (24)

### Декларативные — на классах / полях / методах (13)

Все 13 элементов **активно используются** (usage-count посчитан, замена в пакете отсутствует, если не указано иначе).

- [ ] 3.1. `common/.../client/mixin/ScreenInvoker.java:12` — `@Deprecated` интерфейс mixin (access invoker для protected `Screen.addRenderableWidget`). 4 use-site (`ColumnAlignment.java`, `PresetEditorPage.java`).
- [ ] 3.2. `common/.../NTRegistries.java:30` — `@Deprecated public static final ResourceKey<Registry<Preset>> PRESET`. 7 use-site.
- [ ] 3.3. `common/.../platform/RegistryUtil.java:14` — `@Deprecated public final class RegistryUtil`. 40 use-site (фасад платформенных регистраций).
- [ ] 3.4. `common/.../concurrent/cache/CacheManager.java:12` — `@Deprecated public class CacheManager`. 4 use-site.
- [ ] 3.5. `common/.../world/worldgen/heightprovider/LegacyCarverHeight.java:12` — `@Deprecated // pretty sure this can be replicated with UniformHeight`. 4 use-site (`NTHeightProviderTypes`, `PresetConfiguredCarvers`). В JSON-датапак **не зарегистрирован**.
- [ ] 3.6. `common/.../cell/decorator/ColumnDecorator.java:9` — `@Deprecated public class ColumnDecorator`. 7 use-site (`ErodeFeature`, `DecorateSnowFeature`).
- [ ] 3.7. `common/.../world/worldgen/floatprovider/LegacyCanyonYScale.java:10` — `@Deprecated`. 3 use-site (`NTFloatProviderTypes`, `PresetConfiguredCarvers`). В JSON-датапак **не зарегистрирован**. Возможная замена — `UniformFloat`.
- [ ] 3.8. `common/.../concurrent/cache/Cache2d.java:8` — `@Deprecated public record Cache2d(Noise noise, ThreadLocal<Cached> cache) implements Noise`. 10 use-site (`MixinNoiseChunk`, `CellSampler`, `Noises`, `PresetTerrainNoise`).
- [ ] 3.9. `common/.../world/worldgen/placement/LegacyCountExtraModifier.java:16` — `@Deprecated class LegacyCountExtraModifier extends PlacementModifier`. 3 use-site в Java + **зарегистрирован в 9 `placed_feature/*.json`** датапака. Удаление потребует миграции конфигов.
- [ ] 3.10. `common/.../client/gui/screen/presetconfig/PresetWidgets.java:25` — `@Deprecated final class PresetWidgets`. 186 use-site (используется в 8+ страницах конфига).
- [ ] 3.11. `common/.../data/worldgen/preset/settings/Preset.java:45` — `@Deprecated public static final ResourceKey<Preset> KEY`. 7 use-site (primary key реестра Preset).
- [ ] 3.12. `common/.../world/worldgen/placement/NTPlacementModifiers.java:38` — метод `countExtra(...)`, помечен `@deprecated` в Javadoc. 9 use-site в `PresetPlacedFeatures`. Возвращает `LegacyCountExtraModifier`.
- [ ] 3.13. `common/.../data/worldgen/tags/NTDensityFunctionTags.java:11` — поле `ADDITIONAL_NOISE_ROUTER_FUNCTIONS`, `@deprecated` в Javadoc. 2 use-site (`NTDensityFunctionTagsProvider`, `MixinRandomState`).

### На record-параметрах / Javadoc-only (11)

Семантика «не используй этот аргумент». Связаны с TODO 2.9 (`Populators.java:20` — «remove all the seed parameters»).

- [ ] 3.14. `common/.../world/worldgen/noise/Perlin.java:10` — `record Perlin(@Deprecated int seed, ...)`.
- [ ] 3.15. `common/.../world/worldgen/noise/Perlin2.java:10` — `record Perlin2(@Deprecated int seed, ...)`.
- [ ] 3.16. `common/.../world/worldgen/util/Range.java:6` — `record Range(..., @Deprecated boolean exclusive)`.
- [ ] 3.17. `common/.../world/worldgen/cell/terrain/Populators.java:22` — поле `DEFAULT_EROSION` помечено `@Deprecated`.
- [ ] 3.18. `common/.../world/worldgen/cell/terrain/Populators.java:25` — поле `DEFAULT_WEIRDNESS` помечено `@Deprecated`.
- [ ] 3.19. `common/.../world/worldgen/cell/terrain/Populators.java:27` — параметр `seed` метода помечен `@Deprecated`.
- [ ] 3.20. `common/.../world/worldgen/cell/terrain/Populators.java:61` — параметр `seed` метода.
- [ ] 3.21. `common/.../world/worldgen/cell/terrain/Populators.java:79` — параметр `seed` метода.
- [ ] 3.22. `common/.../world/worldgen/cell/terrain/Populators.java:97` — параметр `seed` метода.
- [ ] 3.23. `common/.../mixin/MixinBiomeGenerationSettings.java:18` — `@deprecated` в Javadoc интерфейса (см. также TODO 2.8).
- [ ] 3.24. `fabric/.../mixin/MixinPresetEditor.java:20` — `@deprecated` в Javadoc fabric mixin.

---

## Решение по каждому пункту

Каждый чекбокс требует **отдельного решения**: удалить / реализовать / переформулировать / снять `@Deprecated` / оставить как есть. Просто массово сносить не нужно — у части маркеров есть смысл, у части нет, у `@Deprecated` без замены аннотация только шумит в IDE.

**Полезные напоминания:**
- Перед удалением `LegacyCountExtraModifier` (3.9) нужна миграция 9 JSON-файлов в `data/neoterra/worldgen/placed_feature/`.
- `Presets.java:281` (2.26) — это live `throw`, не комментарий: сначала grep call-path, потом решение.
- `PresetListPage.java:286` (2.30) — потенциальный data-loss; кандидат на немедленный фикс.
- Исходники TerraForged/ReTerraForged/NeoTerraForged для сверки лежат в `~/projects/source-Terra` (см. memory).
