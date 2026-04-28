# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Что это

NeoTerra — мультилоадерный (Fabric + NeoForge) мод для Minecraft **1.21.1**, продолжение линии TerraForged → ReTerraForged → NeoTerraForged. Цель — кастомная генерация мира (шум, биомы, поверхностные правила, фичи, структуры, пресеты). Java 21, Mojang mappings.

## Сборка и запуск

Используется Gradle wrapper + Architectury Loom. Все команды — из корня проекта; `./gradlew` (Linux/Mac) или `gradlew.bat` (Windows).

- `./gradlew build` — собрать всё. Артефакты в `fabric/build/libs/` и `neoforge/build/libs/` (ремап-jar с classifier `fabric-1.21.1` / без classifier для NeoForge).
- `./gradlew :fabric:build` / `./gradlew :neoforge:build` — собрать один лоадер.
- `./gradlew :fabric:runClient` / `:neoforge:runClient` — запуск dev-клиента.
- `./gradlew :fabric:runServer` / `:neoforge:runServer` — запуск dev-сервера.
- `./gradlew :fabric:runData` — datagen для Fabric (выход в `fabric/src/main/generated`, vmArg `-Dfabric-api.datagen.modid=neoterra`).
- `./gradlew :neoforge:runData` — datagen для NeoForge (выход в `neoforge/src/generated/resources`, programArgs `--all --mod neoterra`).
- `./gradlew clean` — очистка `build/`.

Тестов в проекте нет.

Важные параметры в `gradle.properties`: `minecraft_version`, `fabric_loader_version`, `fabric_api_version`, `neoforge_version`, `mod_version`. JVM heap для Gradle: `-Xmx4G`.

## Архитектура

### Раскладка модулей (Architectury, multi-loader)

- `common/` — платформонезависимый код мода. Только сюда добавляются основные классы worldgen, регистрации, миксины общего слоя, datagen-провайдеры. Apply в `common/build.gradle`: `architectury { common("fabric", "neoforge") }`.
- `fabric/` — Fabric-специфичные точки входа и реализации `@ExpectPlatform`-методов. Entry point `neoterra.fabric.NTFabric` (см. `fabric.mod.json`). Использует shadow-jar для упаковки common-классов через `transformProductionFabric`.
- `neoforge/` — NeoForge-специфичные точки входа и реализации. Entry point `neoterra.neoforge.NTNeoForge` (см. `META-INF/neoforge.mods.toml`). Аналогичная shadow-схема через `transformProductionNeoForge`.

Реализация платформонезависимых API — через Architectury-аннотацию `@ExpectPlatform` на методах в `common/.../platform/*Util.java` (`RegistryUtil`, `ConfigUtil`, `DataGenUtil`, `ModLoaderUtil`). Соответствующие `*Impl.java` лежат в `fabric/.../platform/fabric/` и `neoforge/.../platform/neoforge/`. При добавлении нового платформенного метода обязательно реализовать в обоих модулях, иначе runtime-падение из-за `IllegalStateException` в common-стабе.

### Mod ID и точки входа

- `MOD_ID = "neoterra"`, плюс `LEGACY_MOD_ID = "terraforged"` (см. `NTCommon.java`) — присутствие легаси-id важно для совместимости с ресурсами/датапаками TerraForged.
- Логгер: `NTCommon.LOGGER` (Log4j2 имя `"NeoTerra"`).
- Бутстрап общего слоя: `NTCommon.bootstrap()` вызывается из `NTFabric.onInitialize()` и из конструктора `NTNeoForge`. Внутри по очереди дёргаются `bootstrap()` всех подсистем (`NTBuiltInRegistries`, `NTFeatures`, `Noises`, `Domains`, `CurveFunctions`, `NTSurfaceRules`, `StructureRules`, `TemplatePlacements`, `TemplateDecorators`, `NTPlacementModifiers`, `NTChanceModifiers`, `NTHeightProviderTypes`, `NTFloatProviderTypes`, `NTDensityFunctions`) и регистрируются динамические data-registries (`NOISE`, `PRESET`, `STRUCTURE_RULE`).

### Регистрации

Двухуровневая схема в `common/src/main/java/neoterra/registries`:

- `NTRegistries` — все `ResourceKey<Registry<...>>` (как для типовых codec-реестров `*_TYPE`, так и для data-driven контента: `NOISE`, `STRUCTURE_RULE`, `PRESET`).
- `NTBuiltInRegistries` — встроенные (in-code) реестры `MapCodec`-фабрик, создаются через `RegistryUtil.createRegistry(...)`.
- Data-driven реестры регистрируются `RegistryUtil.createDataRegistry(key, codec, synced)` из `NTCommon.bootstrap()`.

Biome modifier'ы НЕ хранятся в собственном `NTRegistries`-реестре — они применяются через нативные API лоадеров (см. ниже раздел о biome modification).

### Worldgen

Основной код — `common/.../world/worldgen/`. Наиболее важные подпакеты:

- `noise/` (`module/`, `domain/`, `function/`) — собственный noise-фреймворк (Noise, Domain, CurveFunction) с codec-реестрами типов; bootstrapped через `Noises`/`Domains`/`CurveFunctions`.
- `biome/` — `NTBiomes`, `NTClimateSampler`, параметры климата (`Continentalness`, `Humidity`, `Temperature`, `Weirdness`, `Erosion`).
- `densityfunction/` — кастомные `DensityFunction`'ы.
- `surface/` — `NTSurfaceRules` и инфраструктура поверх ванильных `SurfaceRules` (требует широких access wideners, см. ниже).
- `feature/` — `NTFeatures` плюс подсистемы `chance/`, `placement/`, `template/` (template/placement/decorator с собственными codec-реестрами).
- `structure/` — структуры и правила (`StructureRule`, `StructureRules`).
- `floatproviders/`, `heightproviders/` — типы float/height providers.
- `cell/`, `util/`, `WorldErosion`, `WorldFilters`, `GeneratorContext`, `NTRandomState` — вспомогательные структуры данных и контексты.

Контент data-driven worldgen лежит в `common/src/main/resources/data/neoterra/worldgen/` и `data/neoterra/structures/`. Тэги — `data/minecraft/tags/` и `data/neoterra/...`.

Datapack-настройки и пресеты: `common/.../data/worldgen/preset/` и `Datapacks.java`.

### Mixins

Конфиги: `neoterra-common.mixins.json`, `neoterra-fabric.mixins.json`, `neoterra-neoforge.mixins.json`. Common-миксины (`MixinNoiseChunk`, `MixinRandomState`, `MixinChunkMap`, `MixinSurfaceSystem`, `MixinBiomeGenerationSettings`, `MixinClimateSampler`, `MixinStructure`, `MixinSpawnFinder`, `MixinMinecraftServer`, `MixinRegistrySetBuilder$EmptyTagLookup`, `MixinUtil`, `ScreenInvoker`) — в `common/src/main/java/neoterra/mixin/`. Платформенные миксины (например, `MixinChunkStatusTasks`, `MixinPresetEditor` для Fabric; `MixinBiomeGenerationSettingsPlainsBuilder`, `MixinTagsProvider` для NeoForge) — в `*/mixin/` соответствующего модуля. `compatibilityLevel` миксинов — `JAVA_17`, при том что компилятор настроен на release 21.

### Access widener

Один общий файл `common/src/main/resources/neoterra.accesswidener` подключается обоими лоадерами:

- В Fabric: через `loom.accessWidenerPath` в common-модуле плюс `injectAccessWidener = true` в `fabric:remapJar`.
- В NeoForge: путь шарится из common, плюс `atAccessWideners.add('neoterra.accesswidener')` в `neoforge:remapJar` (Architectury конвертирует AW → AT).

При добавлении новых обращений к приватным членам ванилы — править этот файл, а не отдельные AW/AT для каждого лоадера.

### Concurrency / utilities

`common/.../concurrent/` содержит свои примитивы: `ThreadPools`, `ArrayPool`/`ThreadLocalPool`, `Cache`/`CacheManager`/`ExpiringEntry`, `LazySupplier`/`LazyCallable`, `Resource`/`Disposable`. Это hot-path для воркеров чанк-генерации; перед заменой на стандартные коллекции/пулы стоит свериться с местами использования (особенно мутации в миксинах `MixinChunkStatusTasks`).

### Datagen

Общие language-провайдеры — в `common/.../client/data/NTLanguageProvider.java` (вызываются из обоих entry points). Fabric датаген запускается через `runData` (Fabric API datagen, `fabric-api.datagen.modid=neoterra`), NeoForge — через `GatherDataEvent`. Сгенерированные ассеты складываются в `*/src/main/generated` (Fabric) или `*/src/generated/resources` (NeoForge); оба пути добавлены в `sourceSets.main.resources` и заигнорены в git.

### Biome modification

Описательный слой — `common/.../data/worldgen/preset/biomepatch/` (`BiomeFeaturePatches`, `PatchAdd`, `PatchReplace`, `Filter`, `Order`). `PresetBiomeModifierData.collectPatches(preset, ...)` строит из пресета список patches; дальше — платформенно:

- На **NeoForge**: `BiomeModifierPlatform.addPatches(...)` (через `@ExpectPlatform`) добавляет в `RegistrySetBuilder` запись в `NeoForgeRegistries.Keys.BIOME_MODIFIERS`. Конвертер `PatchesToNeoForgeBiomeModifiers` мапит patches на встроенные `AddFeaturesBiomeModifier`/`RemoveFeaturesBiomeModifier`. Для `Order.PREPEND` (нет нативного аналога) есть кастомный `PrependFeaturesBiomeModifier`, codec которого регистрируется в `NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS` через `DeferredRegister` в `NTNeoForge`.
- На **Fabric**: `FabricBiomePatches.register()` вызывается в `NTFabric.onInitialize()` и регистрирует фиксированный набор модификаторов через `BiomeModifications.create(...).add(...)` (Fabric Biome API V1). Активность пресета определяется по proxy-проверке: `selCtx.getPlacedFeatureRegistry().get(PresetPlacedFeatures.ERODE) != null`. Order.PREPEND теряется (Fabric API кладёт в конец фазы).

Trade-off Fabric'а: модификаторы регистрируются статически на onInitialize, до загрузки пресета, поэтому boolean-флаги `MiscellaneousSettings.customBiomeFeatures` / `naturalSnowDecorator` / `smoothLayerDecorator` больше не управляют конкретно набором модификаторов — они применяются всегда, когда NT-датапак активен.

## Ловушки, на которые легко наступить

- При добавлении/правке `@ExpectPlatform`-метода в `common` — он обязан быть реализован в `fabric/.../platform/fabric/*Impl.java` И `neoforge/.../platform/neoforge/*Impl.java`. Architectury генерирует диспетчер; пропуск одной реализации ломает только соответствующий лоадер и часто только в runtime.
- Описательные `PatchAdd`/`PatchReplace` идентифицируются через `ResourceLocation` (не `ResourceKey<...>`); при добавлении нового patch'а используй `NTCommon.location(...)` и не пытайся привязать к удалённому `NTRegistries.BIOME_MODIFIER`.
- На NeoForge codec кастомного `PrependFeaturesBiomeModifier` должен быть зарегистрирован ДО `GatherDataEvent` — текущий `DeferredRegister` делает это автоматически, но если переписать регистрацию вручную (например, через bootstrap-метод вместо DeferredRegister) — не забыть про порядок.
- `common/build.gradle` содержит `compileOnly "com.electronwill.night-config:toml:3.6.7"` (для конфиг-кода), он не уезжает в shadow-jar — runtime эта зависимость должна предоставляться лоадером.
