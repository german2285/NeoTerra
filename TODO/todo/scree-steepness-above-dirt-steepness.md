# screeSteepness должна быть выше dirtSteepness

`common/src/main/java/neoterra/client/gui/screen/presetconfig/SurfaceSettingsPage.java:20`

```java
	private Slider dirtVariance;
	private Slider dirtMin;
	private Slider rockSteepness;
	private Slider dirtSteepness; //TODO ensure is above rockSteepness
	private Slider screeSteepness; //TODO ensure is above dirtSteepness
	
	public SurfaceSettingsPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen, preset);
```
