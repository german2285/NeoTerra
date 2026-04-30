## @Deprecated на поле hasContext в MixinRandomState

`common/src/main/java/neoterra/mixin/MixinRandomState.java:53`

```java
	private DensityFunction.Visitor densityFunctionWrapper;
	@Shadow
	@Final
    private SurfaceSystem surfaceSystem;
	
	@Deprecated
	private boolean hasContext;
	@Nullable
	private GeneratorContext generatorContext;
	@Nullable
	private Preset preset;
```

Используется в самом миксине: выставляется в `true` при первой переписи `CellSampler.Marker` в density-wrapper'е (строки 88, 91), читается в `NTRandomState.initialize` (строка 107) и в двух блоках инициализации (строки 121, 130). Логически дублирует `generatorContext != null` / `preset != null` — флаг можно убрать, проверив сами `@Nullable`-поля.
