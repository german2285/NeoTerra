# Сомнение в nullability TileCache.get

`common/src/main/java/neoterra/world/worldgen/densityfunction/tile/TileCache.java:58`

```java
	@Override
	public void drop(int tileX, int tileZ) {
		long packedTilePos = PosUtil.pack(tileX, tileZ);
		//TODO i dont think get should be able to return null here
		CacheEntry<Entry> entry = this.cache.get(packedTilePos);
		if(entry != null && entry.get().drop()) {
			this.cache.remove(packedTilePos);
		}
```
