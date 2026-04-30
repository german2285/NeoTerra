# Граничный if (value >= 1.0F) в Levels.scale

`common/src/main/java/neoterra/world/worldgen/cell/heightmap/Levels.java:29-31`

```java
        this.elevationRange = 1.0F - this.water;
    }
    
    public int scale(float value) {
//        if (value >= 1.0F) {
//            return this.worldHeight - 1;
//        }
        return (int) (value * this.worldHeight);
    }
    
    public float elevation(float value) {
```
