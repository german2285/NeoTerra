# @Deprecated на параметре exclusive record'а Range

`common/src/main/java/neoterra/world/worldgen/feature/util/Range.java:6`

```java
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Range(float from, float to, float max, float range, @Deprecated boolean exclusive) {
	public static final Codec<Range> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("from").forGetter(Range::from),
		Codec.FLOAT.fieldOf("to").forGetter(Range::to),
```
