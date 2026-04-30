# @Deprecated на интерфейсе MixinPresetEditor

`fabric/src/main/java/neoterra/fabric/mixin/MixinPresetEditor.java:20`

```java
// PresetEditor.EDITORS — это Map.of(...) в clinit. Mojang не даёт public API
// зарегистрировать editor для своего worldgen preset, поэтому Redirect перехватывает
// вызов Map.of и возвращает modifiable HashMap, в который PUT'аем editor для нашего
// neoterra:overworld.
@Deprecated
@Mixin(PresetEditor.class)
interface MixinPresetEditor {

	@Redirect(
```
