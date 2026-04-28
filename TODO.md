# TODO: миграция кастомной BiomeModifier-системы на нативные API

## Контекст

NeoTerra унаследовал из ветки TerraForged → ReTerraForged → NeoTerraForged кастомную систему
biome modifier'ов (`common/src/main/java/neoterra/world/worldgen/biome/modifier/`). Она состоит
из общего `BiomeModifier` интерфейса и двух impl (`AddModifier`, `ReplaceModifier`),
зарегистрированных в datapack registry `forge:biome_modifier` (namespace выбран ради
совместимости с NeoForge native API).

**На NeoForge** эти модификаторы — уже нативные NeoForge `BiomeModifier`'ы (`ForgeBiomeModifier`
extends `net.neoforged.neoforge.common.world.BiomeModifier`), и NeoForge сам подхватывает их
из data registry. Никакого хака нет.

**На Fabric** биом-модификации применяются через рефлексионный mixin
`MixinBiomeModificationImpl`, который запихивает наши модификаторы в приватное поле
`BiomeModificationImpl$ModifierRecord` через `Constructor.newInstance(...)`. Это и есть
«janky» часть, ради которой авторы пометили `BiomeModifier` как `@Deprecated(forRemoval)`.
В рамках чистки технического долга `@Deprecated` снят, но архитектурный долг остался —
рефлексионный mixin никуда не делся.

Цель миграции — избавиться от рефлексионного mixin'а на Fabric и упростить инфраструктуру
на обоих лоадерах, переехав на нативные API:
- **NeoForge**: встроенные `AddFeaturesBiomeModifier`, `RemoveFeaturesBiomeModifier`
  (где это возможно).
- **Fabric**: `BiomeModifications` API (`net.fabricmc.fabric.api.biome.v1.BiomeModifications`),
  регистрируемый в `onInitialize`.

## Сложности и trade-off'ы

1. **Список модификаторов зависит от пресета**: текущая логика в `PresetBiomeModifierData`
   создаёт разный набор модификаторов в зависимости от boolean-флагов
   `MiscellaneousSettings` (`customBiomeFeatures`, `erosionDecorator`,
   `naturalSnowDecorator`, `smoothLayerDecorator`). Пресет выбирается пользователем при
   создании мира, а Fabric `BiomeModifications.create(id).add(...)` обычно вызывается в
   `onInitialize` — до выбора пресета. Это главное архитектурное несоответствие.

   **Возможные решения:**
   - (a) Регистрировать ВСЕ модификаторы в `onInitialize`, а в predicate проверять активный
     пресет (нужно научиться вытаскивать активный пресет из `BiomeSelectionContext` — это
     возможно через `ctx.getCurrentLevelGenerationSettings()` и сравнение с
     `ChunkGenerator`'ом NT, но точные детали — в имплементации).
   - (b) Не зависеть от пресета: фиксированный набор модификаторов в коде, флаги пресета
     влияют только на содержимое placed feature'ов (что они генерируют), но не на сам
     факт регистрации модификатора.
   - (c) Использовать Fabric mixin в более «чистый» target (например, `IndexedIterable`
     с ranged-добавлением). Не убирает рефлексию полностью, но локализует её.

2. **Нет нативного `Order.PREPEND` ни на Fabric, ни на NeoForge**: встроенные
   `AddFeaturesBiomeModifier` (NeoForge) и `BiomeModifications.addFeature` (Fabric)
   добавляют в конец списка. Если PREPEND критичен (особенно для `ADD_EROSION` в
   `RAW_GENERATION` — эрозия должна идти первой), потребуется кастомный
   NeoForge `BiomeModifier`-impl на эту единственную фазу + Fabric-mixin/обходной путь.

3. **Replace не имеет нативного аналога**: придётся комбинировать `RemoveFeaturesBiomeModifier`
   + `AddFeaturesBiomeModifier` (NeoForge) и `removeFeature` + `addFeature` (Fabric).

## Референсы

### NeoForge
- Нативный `BiomeModifier` интерфейс:
  `net.neoforged.neoforge.common.world.BiomeModifier`
  https://github.com/neoforged/NeoForge/blob/1.21.x/src/main/java/net/neoforged/neoforge/common/world/BiomeModifier.java
- Встроенные impl (`AddFeaturesBiomeModifier`, `RemoveFeaturesBiomeModifier`,
  `AddSpawnsBiomeModifier`, и т.д.):
  https://github.com/neoforged/NeoForge/tree/1.21.x/src/main/java/net/neoforged/neoforge/common/world
- Регистрация modifier'ов через datagen (`DatapackBuiltinEntriesProvider` +
  `RegistrySetBuilder` для `NeoForgeRegistries.Keys.BIOME_MODIFIERS`):
  поискать в туториалах NeoForge и в samples проекта
  `examplemod` https://github.com/neoforged/NeoForge/tree/1.21.x/tests
- Codec'ные сериализаторы для `BiomeModifier`'ов регистрируются в
  `BIOME_MODIFIER_SERIALIZERS` через `DeferredRegister`.

### Fabric
- `BiomeModifications` API:
  `net.fabricmc.fabric.api.biome.v1.BiomeModifications`
  https://github.com/FabricMC/fabric/blob/1.21/fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/api/biome/v1/BiomeModifications.java
- `BiomeSelectors` (предикаты для выбора биомов — `BiomeSelectors.includeByKey(...)`,
  `BiomeSelectors.foundInOverworld()` и т.д.):
  https://github.com/FabricMC/fabric/blob/1.21/fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/api/biome/v1/BiomeSelectors.java
- `ModificationPhase` (`ADDITIONS`, `REMOVALS`, `REPLACEMENTS`, `POST_PROCESSING`):
  https://github.com/FabricMC/fabric/blob/1.21/fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/api/biome/v1/ModificationPhase.java
- `BiomeModificationContext` для модификации `getGenerationSettings()`:
  https://github.com/FabricMC/fabric/blob/1.21/fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/api/biome/v1/BiomeModificationContext.java

### TerraForged
- Оригинальная ветка проекта (Forge-only, до архитектурного раскола на лоадеры):
  https://github.com/TerraForged/TerraForged
- Конкретно — пакет с feature decorator'ами и biome modification logic в их Forge-only
  коде; интересен как baseline для понимания «что и зачем добавляется в каждый биом».

---

## Этапы работы (каждый этап = отдельный коммит)

### Этап 0 (готов в текущем PR)
- [x] Снять `@Deprecated(forRemoval)` с `BiomeModifier`.
- [x] Заменить комментарий «theres other worldgen libraries…» на ссылку на этот TODO.

---

### Этап 1: Извлечь дескриптивные данные из `PresetBiomeModifierData`

**Цель**: разделить «что и куда добавляется» (платформонезависимо) от «как это превращается
в модификатор» (платформенно). Подготовка к замене кастомного интерфейса.

**Файлы**:
- `common/src/main/java/neoterra/data/worldgen/preset/PresetBiomeModifierData.java`
- новый: `common/src/main/java/neoterra/data/worldgen/preset/biomepatch/BiomeFeaturePatches.java`
- новый: `common/src/main/java/neoterra/data/worldgen/preset/biomepatch/PatchAdd.java`
- новый: `common/src/main/java/neoterra/data/worldgen/preset/biomepatch/PatchReplace.java`

**Действия**:
1. Создать `BiomeFeaturePatches` — простая обёртка
   `record BiomeFeaturePatches(List<PatchAdd> adds, List<PatchReplace> replaces)`.
2. Создать `PatchAdd` — `record PatchAdd(ResourceKey<BiomeModifier> id, Order order,
   GenerationStep.Decoration step, Optional<Filter> filter, HolderSet<PlacedFeature> features)`.
   ID нужен, чтобы знать имя для регистрации в нативных API.
3. Создать `PatchReplace` — `record PatchReplace(ResourceKey<BiomeModifier> id,
   GenerationStep.Decoration step, Optional<HolderSet<Biome>> biomes,
   Map<ResourceKey<PlacedFeature>, Holder<PlacedFeature>> replacements)`.
4. В `PresetBiomeModifierData.bootstrap` вместо вызова `ctx.register(KEY,
   BiomeModifiers.add(...))` — наполнять `BiomeFeaturePatches`. Возвращать его наружу
   (через поле `Preset` или helper, см. этап 2).
5. Старая регистрация в datapack registry `BIOME_MODIFIER` пока сохраняется параллельно
   (для этого этапа никаких удалений).

**Тест**: `./gradlew build` — должен пройти. Содержимое `BIOME_MODIFIER` registry не
изменилось, runtime поведение тоже.

---

### Этап 2: Перенаправить NeoForge на встроенные `AddFeaturesBiomeModifier`/`RemoveFeaturesBiomeModifier`

**Цель**: заменить кастомные `AddModifier`/`ReplaceModifier` на NeoForge стороне на
встроенные NeoForge типы.

**Файлы**:
- `neoforge/src/main/java/neoterra/data/worldgen/preset/biomepatch/neoforge/PatchesToNeoForgeBiomeModifiers.java` (новый)
- `neoforge/src/main/java/neoterra/neoforge/NTNeoForge.java` (правка bootstrap)

**Действия**:
1. Написать конвертер `BiomeFeaturePatches → List<net.neoforged.neoforge.common.world.BiomeModifier>`:
   - `PatchAdd` (filter.WHITELIST) → `AddFeaturesBiomeModifier(filter.biomes(), features, step)`.
   - `PatchAdd` (filter.BLACKLIST) → потребуется HolderSet с инверсией (через
     `BiomeManager.getKeys()` и filter, либо тагом `c:is_overworld` минус blacklist).
   - `PatchAdd` (filter.empty()) → нужен HolderSet всех overworld биомов
     (`BiomeTags.IS_OVERWORLD` с конвертацией `HolderGetter`).
   - `PatchReplace` → пара `(RemoveFeaturesBiomeModifier(biomes, step, removedHolderSet))`
     + `(AddFeaturesBiomeModifier(biomes, step, addedHolderSet))`.
2. Регистрировать получившиеся NeoForge `BiomeModifier`'ы в datapack registry
   `NeoForgeRegistries.Keys.BIOME_MODIFIERS` через `DatapackBuiltinEntriesProvider` в
   `GatherDataEvent` (это путь datagen-driven: JSON файлы попадают в
   `neoforge/src/generated/resources/data/neoterra/neoforge/biome_modifier/`).
3. Для случая `Order.PREPEND` (особенно `ADD_EROSION` в `RAW_GENERATION`): оставить
   кастомный NeoForge `BiomeModifier`-impl только для этого подмножества. Один файл
   `PrependFeaturesBiomeModifier` extends `net.neoforged.neoforge.common.world.BiomeModifier`,
   зарегистрировать его MapCodec через `DeferredRegister<MapCodec<? extends BiomeModifier>>`
   на `NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS`.
4. Сравнить runtime поведение: на этом этапе ОБА механизма (старый кастомный + новый
   нативный) активны параллельно. Важно: не должно быть дублирования. Простейший путь —
   на этом этапе на NeoForge старый кастомный механизм отключить (закомментировать
   регистрацию `addCodec(BIOME_MODIFIER, ...)` или фильтровать в `Preset.bootstrap`).

**Откуда копировать**:
- Структура `DatapackBuiltinEntriesProvider`:
  https://github.com/neoforged/NeoForge/blob/1.21.x/src/main/java/net/neoforged/neoforge/common/data/DatapackBuiltinEntriesProvider.java
- Использование `AddFeaturesBiomeModifier` через JSON: тестовый mod
  `examplemod/data/neoforge/biome_modifier/` в репо NeoForge.

**Тест**:
1. `./gradlew :neoforge:runData` — должно сгенериться 28 JSON файлов (по числу модификаторов).
2. `./gradlew :neoforge:runClient`, создать мир с пресетом NeoTerra — проверить:
   - в swamp есть кастомные деревья (`REPLACE_SWAMP_TREES`),
   - в taiga деревья NT'шные (`REPLACE_PINE_TREES`),
   - снег корректный (`ADD_SNOW_PROCESSING`),
   - эрозия видна — особое внимание, потому что `ADD_EROSION` использует кастомный
     `PrependFeaturesBiomeModifier`.

---

### Этап 3: Перенаправить Fabric на `BiomeModifications` API

**Цель**: убрать рефлексионный mixin `MixinBiomeModificationImpl`, переехать на
официальный Fabric API.

**Файлы**:
- `fabric/src/main/java/neoterra/fabric/biome/FabricBiomePatches.java` (новый)
- `fabric/src/main/java/neoterra/fabric/NTFabric.java` (правка `onInitialize`)
- `fabric/src/main/resources/neoterra-fabric.mixins.json` (убрать
  `mixin/MixinBiomeModificationImpl`)
- `fabric/src/main/java/neoterra/fabric/mixin/MixinBiomeModificationImpl.java` (удалить)

**Действия**:
1. Решить проблему «список зависит от пресета» — выбрать вариант (a) или (b) из секции
   «Сложности» выше. **Рекомендация**: (b) — регистрировать фиксированный набор
   модификаторов в `onInitialize`, а присетные boolean'ы превратить во включение/выключение
   placed feature'ов через тэги или через выбор соответствующего `Holder<PlacedFeature>` в
   `PresetPlacedFeatures` на основе `MiscellaneousSettings`. Так устраняется зависимость
   от пресета на runtime.

   Если (b) не получается — путь (a):
   ```java
   BiomeModifications.create(NTCommon.location("replace_swamp_trees"))
       .add(ModificationPhase.REPLACEMENTS,
            BiomeSelectors.includeByKey(Biomes.SWAMP).and(NeoTerraSelectors.isActivePreset()),
            (selectionCtx, modCtx) -> { ... });
   ```
   `NeoTerraSelectors.isActivePreset()` — кастомный predicate, который определяет, что
   текущий мир использует пресет NeoTerra (можно через сравнение `ChunkGenerator`'а с
   `NoiseBasedChunkGenerator` где settings равны нашим).

2. Реализовать `FabricBiomePatches.register()`:
   - Для каждого `PatchAdd` — `BiomeModifications.create(patch.id().location())`
     `.add(ModificationPhase.ADDITIONS, selector, (selCtx, modCtx) ->
     modCtx.getGenerationSettings().addFeature(patch.step(), patch.features().stream()
     .findFirst().get()))`. Если features больше одной — добавлять каждую отдельным
     вызовом или через цикл.
   - Для каждого `PatchReplace` — `removeFeature(step, oldKey)` + `addFeature(step, newHolder)`.
   - `Order.PREPEND` без чистого решения: либо принять что Fabric кладёт в конец фазы
     (для большинства модификаторов — приемлемо), либо оставить mixin ТОЛЬКО для PREPEND
     случаев (один-два).

3. Удалить `MixinBiomeModificationImpl.java` и убрать его из `neoterra-fabric.mixins.json`.

**Откуда копировать**:
- Примеры из Fabric API doc:
  https://github.com/FabricMC/fabric/tree/1.21/fabric-biome-api-v1
- Конкретно `BiomeModifications.create(...).add(phase, selector, modifier)` — основной
  паттерн в Fabric.

**Тест**:
1. `./gradlew :fabric:runClient`, создать мир с пресетом NeoTerra:
   - Все те же проверки что в этапе 2 (swamp/taiga/snow/erosion).
   - Особое внимание: на старте сервера НЕ должно быть `ClassNotFoundException` или
     `NoSuchMethodError` (значит mixin корректно удалён, рефлексия больше не нужна).
   - В логе НЕ должно быть упоминаний `BiomeModificationImpl$ModifierRecord` (это значит
     что мы не залезаем в приватные классы Fabric API).
2. Сравнить чанки seed-to-seed с предыдущим коммитом (тот же seed → визуально те же
   биомы и фичи).

---

### Этап 4: Удалить кастомную BiomeModifier инфраструктуру

**Цель**: окончательно вычистить код, который больше никем не используется.

**Файлы — удалить**:
- `common/src/main/java/neoterra/world/worldgen/biome/modifier/BiomeModifier.java`
- `common/src/main/java/neoterra/world/worldgen/biome/modifier/BiomeModifiers.java`
- `common/src/main/java/neoterra/world/worldgen/biome/modifier/Filter.java` (либо
  переехать в `data/worldgen/preset/biomepatch/` если ещё нужен)
- `common/src/main/java/neoterra/world/worldgen/biome/modifier/Order.java` (либо
  переехать в `data/worldgen/preset/biomepatch/` если ещё нужен)
- `fabric/src/main/java/neoterra/world/worldgen/biome/modifier/fabric/AddModifier.java`
- `fabric/src/main/java/neoterra/world/worldgen/biome/modifier/fabric/ReplaceModifier.java`
- `fabric/src/main/java/neoterra/world/worldgen/biome/modifier/fabric/FabricBiomeModifier.java`
- `fabric/src/main/java/neoterra/world/worldgen/biome/modifier/fabric/BiomeModifiersImpl.java`
- `neoforge/src/main/java/neoterra/world/worldgen/biome/modifier/neoforge/AddModifier.java`
- `neoforge/src/main/java/neoterra/world/worldgen/biome/modifier/neoforge/ReplaceModifier.java`
- `neoforge/src/main/java/neoterra/world/worldgen/biome/modifier/neoforge/ForgeBiomeModifier.java`
- `neoforge/src/main/java/neoterra/world/worldgen/biome/modifier/neoforge/BiomeModifiersImpl.java`

**Файлы — править**:
- `common/src/main/java/neoterra/registries/NTRegistries.java` (убрать `BIOME_MODIFIER` и
  `BIOME_MODIFIER_TYPE`).
- `common/src/main/java/neoterra/registries/NTBuiltInRegistries.java` (убрать
  `BIOME_MODIFIER_TYPE`).
- `common/src/main/java/neoterra/NTCommon.java` (убрать `BiomeModifiers.bootstrap()`).
- `common/src/main/java/neoterra/data/worldgen/Datapacks.java` (убрать
  `addCodec(NTRegistries.BIOME_MODIFIER, ...)`).
- `common/src/main/java/neoterra/data/worldgen/preset/settings/Preset.java` (убрать
  `addPatch(BIOME_MODIFIER)` и `addCodec(BIOME_MODIFIER)`).
- `fabric/src/main/java/neoterra/fabric/NTFabric.java` (убрать
  `createDataRegistry(BIOME_MODIFIER, ...)`).
- `neoforge/src/main/java/neoterra/neoforge/NTNeoForge.java` (убрать
  `createDataRegistry(BIOME_MODIFIER, ...)`).

**Действия**:
1. Удалить файлы.
2. Поправить регистрации (убрать). Сборка должна показать неиспользуемые
   импорты — почистить.
3. Если `Filter`/`Order` всё ещё используются для дескриптивных данных в
   `data/worldgen/preset/biomepatch/`, оставить их в новом пакете.

**Откуда копировать**: ничего нового, это чистка.

**Тест**:
1. `./gradlew clean build` — ноль warnings (про removal или unused).
2. Полный runtime-прогон: оба лоадера, оба пресета (`makeNTDefault`,
   `makeBetaDefault` если есть), визуальная проверка генерации.
3. **Breaking change для пользовательских датапаков**: если кто-то использовал
   `forge:biome_modifier` registry с типами `neoterra:add` или `neoterra:replace` — их
   датапак сломается. Упомянуть в release notes, что это удалено.

---

## Глобальная проверка после всех этапов

- `./gradlew clean build --no-daemon` без warnings.
- `./gradlew compileJava -Werror -Xlint:all` чистая сборка.
- Полный регрессионный прогон обоих лоадеров с одинаковым seed'ом и одним пресетом —
  визуально идентичные чанки до/после миграции.
- Нет упоминаний `MixinBiomeModificationImpl` или рефлексии в коде/логах.
