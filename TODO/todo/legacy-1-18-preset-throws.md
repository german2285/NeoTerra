# makeLegacy1_18 бросает UnsupportedOperationException

`common/src/main/java/neoterra/data/worldgen/preset/settings/Presets.java:281`

```java
			new StructureSettings(),
			new MiscellaneousSettings(true, 721, true, true, true, false, true, true, true, true, false, 0.902F, 0.945F)
		);
	}
	
	public static Preset makeLegacy1_18() {
		throw new UnsupportedOperationException("TODO");
	}
}
```
