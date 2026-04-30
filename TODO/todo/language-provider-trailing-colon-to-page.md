# Перенести trailing colon в PresetEditorPage

`common/src/main/java/neoterra/client/data/NTLanguageProvider.java:170`

```java
			this.add(NTTranslationKeys.GUI_SLIDER_MOUNTAIN_BIOME_USAGE, "Mountain Biome Usage");
			this.add(NTTranslationKeys.GUI_SLIDER_VOLCANO_BIOME_USAGE, "Volcano Biome Usage");

			//TODO move the trailing colon and space to PresetEditorPage
			this.add(NTTranslationKeys.GUI_LABEL_PREVIEW_AREA, "Area: ");
			this.add(NTTranslationKeys.GUI_LABEL_PREVIEW_TERRAIN, "Terrain: ");
			this.add(NTTranslationKeys.GUI_LABEL_PREVIEW_BIOME, "Biome: ");
			this.add(NTTranslationKeys.GUI_LABEL_CONTINENT, "Continent");
```
