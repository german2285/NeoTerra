# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build / verify

**Не запускайте `./gradlew build` локально.** Сборка и верификация выполняются только через GitHub Actions (`.github/workflows/release.yml`, self-hosted runner на Hetzner с лейблом `hetzner`). Workflow триггерится тегами `v*` и собирает оба варианта jar-а (`fabric/build/libs/...` и `neoforge/build/libs/...`).

Полезно знать про модель сборки, не запуская её:
- Корневой `build.gradle` использует Architectury Loom (`dev.architectury.loom 1.7.435`); `common` помечен как `architectury.common("fabric", "neoforge")`, его `namedElements` затягивается в платформенные jar-ы через конфигурации `common`/`shadowCommon`.
- Релизные артефакты лежат под `<loader>/build/libs/neoterra-<version>-<loader>-<mc>.jar`. Версия читается из `gradle.properties` (`mod_version`), цель Minecraft из `minecraft_version` (1.21.1).
- Java 21, UTF-8, Mojang mappings.

Запускаемые в IDE Gradle-конфигурации (если действительно нужны):
- `:fabric:runClient` — клиент Fabric.
- `:fabric:runData` — datagen Fabric (отдельная конфигурация в `fabric/build.gradle:13-23`, генерирует в `fabric/src/main/generated`, JVM-арги `-Dfabric-api.datagen*`).
- `:neoforge:runClient` / `:neoforge:runData` — клиент и datagen NeoForge. NeoForge datagen пишет в `neoforge/src/generated/resources` и регистрируется через `GatherDataEvent` в `NTNeoForge.gatherData`.
- Чтобы получить подробные логи мода в любой run-конфигурации, добавьте VM-аргумент `-Dneoterra.debug=true` (см. ниже).

Тестов нет.

## Layout (Architectury split)

Три Gradle-подпроекта с общим пакетом `neoterra`:

- `common/` — почти весь код мода (worldgen, биомы, density-функции, GUI, конфиг, mixin'ы) и общие ресурсы (`assets/neoterra/lang/en_us.json`, `data/neoterra/...`, `neoterra-common.mixins.json`, `neoterra.accesswidener`).
- `fabric/` — entry point `neoterra.fabric.NTFabric` (`onInitialize` + `DataGeneratorEntrypoint`), Fabric-специфичные миксины и реализации `*Impl` для платформенных хелперов.
- `neoforge/` — entry point `neoterra.neoforge.NTNeoForge` (`@Mod("neoterra")`), NeoForge-специфичные миксины, нативные `BiomeModifier` (см. ниже).

Платформенные различия живут через **Architectury `@ExpectPlatform`**: каждый `Foo` в `neoterra.platform` имеет в `fabric/`/`neoforge/` подпакет `neoterra.platform.{fabric|neoforge}` с реализацией `FooImpl` (см. `RegistryUtil`, `ConfigUtil`, `ModLoaderUtil`, `DataGenUtil`, `BiomeModifierPlatform`). Не переименовывайте `FooImpl` — Architectury bytecode-rewriter ищет именно этот суффикс в этих пакетах.

## Bootstrap pipeline

`NTCommon.bootstrap()` (`common/src/main/java/neoterra/NTCommon.java`) — единая точка инициализации, вызывается обоими entry point'ами (`NTFabric.onInitialize` и конструктор `NTNeoForge`). В нужном порядке регистрирует:

1. type-реестры в `NTBuiltInRegistries` (созданы через `RegistryUtil.createRegistry`),
2. фичевые подсистемы (`TemplatePlacements`, `TemplateDecorators`, `NTChanceModifiers`, `NTPlacementModifiers`, `NTDensityFunctions`),
3. шум/домены/кривые (`Noises`, `Domains`, `CurveFunctions`),
4. фичи/провайдеры/правила (`NTFeatures`, `NTHeightProviderTypes`, `NTFloatProviderTypes`, `NTSurfaceRules`, `StructureRules`),
5. data-реестры (`NOISE`, `PRESET`, `STRUCTURE_RULE`) через `RegistryUtil.createDataRegistry`.

Если добавляете новую type/data-категорию, продлевайте именно `NTCommon.bootstrap()` — иначе она поднимется только на одном лоадере или не поднимется вовсе.

## Biome modifier — двойная стратегия

NeoTerra патчит features биомов (см. `data/worldgen/preset/biomepatch`: `PatchAdd`, `PatchReplace`, `BiomeFeaturePatches`). Стратегия применения зависит от лоадера:

- **Fabric**: `FabricBiomeModifierApplier` (`fabric/src/main/java/neoterra/fabric/biome`) хукается на `ServerLifecycleEvents.SERVER_STARTING`, собирает `BiomeFeaturePatches` через `PresetBiomeModifierData.collectPatches`, кастует каждый `Biome` в `IModifiableBiome` (интерфейс из `MixinBiome`) и инвалидирует мемоизированный `ChunkGenerator.featuresPerStep` через `IInvalidatableFeaturesPerStep` (см. `MixinChunkGenerator`). Итерация по `ChunkGenerator`'ам — через `Registries.LEVEL_STEM`, не через `server.getAllLevels()` (на `SERVER_STARTING` уровни ещё пустые).
- **NeoForge**: используется нативный `BiomeModifier` API. `PatchesToNeoForgeBiomeModifiers` конвертирует те же `PatchAdd`/`PatchReplace` в `AddFeaturesBiomeModifier`/`RemoveFeaturesBiomeModifier`/кастомный `PrependFeaturesBiomeModifier` (зарегистрирован в `NTNeoForge` через `DeferredRegister`). `IModifiableBiome` на NeoForge не используется.

При изменении контракта патчей (новые поля, новый `Order`, новый `Filter`) обновляйте обе ветки — иначе один из лоадеров будет молча ронять патчи.

## Mixin'ы и access widener

- `common/src/main/resources/neoterra-common.mixins.json` — общие миксины (`MixinNoiseChunk`, `MixinRandomState`, `MixinSurfaceSystem`, `MixinBiomeGenerationSettings`, `MixinChunkMap`, `MixinClimateSampler`, …; client-only `ScreenInvoker`).
- Платформенные миксины — в `fabric/.../mixin` и `neoforge/.../mixin` (`MixinChunkGenerator`, `MixinBiome`, `MixinChunkStatusTasks`, `MixinPresetEditor` и т. д.).
- `common/src/main/resources/neoterra.accesswidener` довольно мясистый (вскрывает internals `SurfaceRules`, `NoiseChunk`, `NoiseRouterData`, `CreateWorldScreen`, `Climate.ParameterPoint` и др.). На Fabric он применяется через `loom.accessWidenerPath`, на NeoForge — через `remapJar { atAccessWideners.add('neoterra.accesswidener') }` в `neoforge/build.gradle:78-83`.

## Логирование

Используйте `NTCommon.debug(...)` вместо `LOGGER.debug(...)` напрямую. NeoForge поставляет `log4j2.xml` с `ThresholdFilter` уровня `INFO` на консольном appender'е, поэтому `debug()` без обхода не виден. Реализация смотрит на `-Dneoterra.debug=true`: если флаг выставлен, всё уходит через `LOGGER.info(...)`, иначе через `LOGGER.debug(...)`. Логгер называется `"NeoTerra"` (не `MOD_ID`).

Резолв id: `NTCommon.location("foo")` → `neoterra:foo`, `NTCommon.location("minecraft:bar")` → `minecraft:bar`. Не пишите `ResourceLocation.fromNamespaceAndPath(...)` руками.

## Worldgen «cell»-конвейер

Мир генерируется через слой `world/worldgen/cell` (адаптация TerraForged-подобной архитектуры). Ключевые сущности:

- `Cell` (`world/worldgen/cell/Cell.java`) — POJO с предвычисленными значениями для одной (x,z): высота, эрозия, климат, идентификаторы региона/континента/биома и т. п. Очень горячий — переиспользуется через `ThreadLocalPool` и `ThreadLocal<Resource<Cell>>` (см. `Cell.getResource()`). Не аллоцируйте `new Cell()` в hot path'е; берите из пула.
- Подкаталоги `cell/continent`, `cell/climate`, `cell/heightmap`, `cell/rivermap`, `cell/terrain`, `cell/biome` — модули, каждый из которых заполняет свой набор полей `Cell`. `CellPopulator`/`CellLookup` — точки входа.
- Density-функции мода (для интеграции с ванильным `NoiseChunk`) живут в `world/worldgen/densityfunction` и регистрируются через `NTDensityFunctions`.

При работе в hot path:
- Используйте `Cell.LOCAL`/`Cell.POOL`, `ArrayPool`, `ThreadLocalPool` из `concurrent/pool` вместо собственных аллокаций.
- Кэши — через `concurrent/cache` (`Cache`, `CacheManager`, `ExpiringEntry`), не самописные `HashMap`.

## Preset как корень датапака

`Preset` (`data/worldgen/preset/settings/Preset.java`) — единый record-конфиг датапака, содержащий `WorldSettings`, `SurfaceSettings`, `CaveSettings`, `ClimateSettings`, `TerrainSettings`, `RiverSettings`, `FilterSettings`, `StructureSettings`, `MiscellaneousSettings`. Загружается в data-реестр `neoterra:worldgen/preset` под ключом `Preset.KEY = neoterra:preset`. `Preset.buildPatch(registries)` строит `RegistrySetBuilder` со всеми порождёнными вторичными реестрами (биомы, configured/placed features, density functions, dimension types, noise settings, structure rules) — это то, что фактически разворачивается в мир при создании.

Если меняете схему `Preset`/sub-settings — не забудьте `optionalFieldOf(... , default)` для обратной совместимости со старыми датапаками.

## Repo / CI

- Origin: `github.com/german2285/NeoTerra` (HTTPS + `gh` credential helper). Codeberg больше не используется.
- Self-hosted GitHub Actions runner живёт в `/srv/docker/gh-runner` на Hetzner; ephemeral, лейблы `self-hosted/linux/x64/docker/hetzner`.
- Релиз: `git tag v<version> && git push --tags` → workflow `Release` собирает оба jar-а и публикует GitHub Release (prerelease если в теге есть `-alpha`/`-beta`).
