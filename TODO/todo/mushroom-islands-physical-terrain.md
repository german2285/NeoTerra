# Грибные острова: рельеф не появляется в мире

`common/src/main/java/neoterra/world/worldgen/cell/terrain/populator/IslandPopulator.java:97-119`

`IslandPopulator` подключён в `Heightmap.make` и работает: `cell.terrain = MUSHROOM_FIELDS` ставится в ~0.6% клеток в открытом океане (counter подтверждал ~7M из 8M total на превью). `cell.height` ставится в `levels.water(25)` ≈ 0.272 норм. ед.; `cell.erosionMask = true` защищает от `Erosion` и `Smoothing` фильтров; `cell.erosion = 0.0F`, `cell.weirdness = 0.0F` фиксят попадание в multinoise ванильного `Biomes.MUSHROOM_FIELDS`.

В результате `/locate biome minecraft:mushroom_fields` биом **находит**. F3 на координате tp подтверждает `Biome: minecraft:mushroom_fields`. Surface rules в `data/neoterra/worldgen/noise_settings/overworld.json:1648-1664` ожидают этот биом и постилают `mycelium`. Но **физически на координате `Y=64`, под ногами вода**, никакого рельефа не возникает.

Counter в `CellSampler.Field.HEIGHT.read()` показывал `avgMushroomHeight ≈ 0.225` (норм. ед., выше воды `0.194` на ~10 блоков). То есть density function видит грибную клетку с высотой выше уровня воды, но финальная блочная генерация острова не выдаёт.

Подозрения:
- DEPTH/OFFSET function в `PresetNoiseRouterData.java:48-55` через `clampToNearestUnit(height, worldHeight)` и `yClampedGradient` — может терять амплитуду cell.height для одиночных «выпуклых» клеток между океаническими.
- 4-block interpolation между cells (cell.height=0.272 в одной cell, 0.10 в соседних) сглаживает кластеры в 1-2 cell под уровень воды.
- `ContinentLerper2` в `Heightmap.java:135` лерпит cell.height между oceans (наш populator) и land на лерп-зоне `[shallowOcean, inland]` — только cells полностью в `cell.continentEdge < shallowOcean` сохраняют наш height чисто.

Что было перепробовано (не помогло):
- `cell.erosionMask = true` — фильтры пропускают клетку, но рельеф всё равно не виден.
- Поднятие `archipelagoMin` с `water(5)` до `water(25)` — counter показал что финальная средняя height ≈ 0.225, не 0.272 (значит lerp-ветка в самом `IslandPopulator` опускает половину cells), но всё равно выше воды.
- Подмена `cell.erosion`/`cell.weirdness` на 0.0 — помогло биому маппиться, рельеф не починила.

Что не пробовали (следующие шаги):
- Заставить `IslandPopulator` срабатывать только в **глубоком** океане (`cell.continentEdge < controlPoints.deepOcean` явно), чтобы избежать interpolation с прибрежными low-height клетками.
- Кластеризация: вместо ~0.6% случайных клеток сделать большие связные пятна (например, островной populator должен заполнять regions размером 4×4+ cells подряд), чтобы interpolation между cells не размывала их к воде.
- Глубокий разбор `PresetNoiseRouterData.overworld()` (`initial_density`/`final_density`/`slopedCheese`) — как именно cell.height трансформируется в финальную блочную высоту, и где «теряется» амплитуда.

Семантика родителей:
- В **TerraForged** mushroom-острова никогда не были реализованы (единственная ссылка закомментирована в `ModClimates.java:74`).
- В **ReTerraForged** `IslandPopulator` помечен `@Deprecated(forRemoval = true)` и не подключён к pipeline.
- Это новая фича NeoTerra, в которой мы дошли дальше всех родителей, но финальный физический рельеф не получен.
