# Голый TODO в else-ветке oreCompatibleStoneOnly

`common/src/main/java/neoterra/data/worldgen/tags/NTBlockTagsProvider.java:38`

```java
		this.tag(NTBlockTags.SEDIMENT).add(Blocks.SAND, Blocks.GRAVEL);
		this.tag(NTBlockTags.ERODIBLE).add(Blocks.SNOW_BLOCK).add(Blocks.POWDER_SNOW).add(Blocks.GRAVEL).addOptionalTag(BlockTags.DIRT.location());
		
//		if(!miscellaneousSettings.oreCompatibleStoneOnly) {
			this.tag(NTBlockTags.ROCK).add(Blocks.GRANITE, Blocks.ANDESITE, Blocks.STONE, Blocks.DIORITE);
//		} else{
			//TODO
//		}
	}
}
```
