# Прибрать Scaling

`common/src/main/java/neoterra/world/worldgen/util/Scaling.java:5`

```java
package neoterra.world.worldgen.util;

import neoterra.world.worldgen.noise.NoiseUtil;

// TODO clean this up some more
public record Scaling(int worldHeight, float unit, int waterY, int groundY, int groundLevel, int waterLevel, float ground, float water, float elevationRange) {
    
    public int scale(float value) {
        return (int) (value * this.worldHeight);
```
