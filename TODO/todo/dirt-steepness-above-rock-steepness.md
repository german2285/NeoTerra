# dirtSteepness должна быть выше rockSteepness

`common/src/main/java/neoterra/client/gui/screen/presetconfig/SurfaceSettingsPage.java:19`

```java
	private Slider rockMin;
	private Slider dirtVariance;
	private Slider dirtMin;
	private Slider rockSteepness;
	private Slider dirtSteepness; //TODO ensure is above rockSteepness
	private Slider screeSteepness; //TODO ensure is above dirtSteepness
	
	public SurfaceSettingsPage(PresetConfigScreen screen, PresetEntry preset) {
```
