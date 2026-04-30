# Обновить seed edit box при setSeed

`common/src/main/java/neoterra/client/gui/screen/presetconfig/PresetConfigScreen.java:86`

```java
			this.imageCache.clear();
		}
	}

	public void setSeed(long seed) {
		//TODO update the seed edit box
		this.parent.getUiState().setSettings(this.getSettings().withOptions((options) -> {
			return new WorldOptions(seed, options.generateStructures(), options.generateBonusChest());
		}));
	}
```
