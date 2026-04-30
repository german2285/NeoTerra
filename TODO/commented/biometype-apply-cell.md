# Закомментированные apply/applyLinear/applyCurve(Cell)

`common/src/main/java/neoterra/world/worldgen/cell/biome/type/BiomeType.java:99-109`

```java
    public static BiomeType getCurve(float temperature, float moisture) {
        int x = NoiseUtil.round(255.0f * temperature);
        int y = getYCurve(x, temperature, moisture);
        return getType(x, y);
    }
    
//    public static void apply(Cell cell) {
//        applyCurve(cell);
//    }
    
//    public static void applyLinear(Cell cell) {
//        cell.biome = get(cell.regionTemperature, cell.regionMoisture);
//    }
//    
//    public static void applyCurve(Cell cell) {
//        cell.biome = get(cell.regionTemperature, cell.regionMoisture);
//    }
    
    private static BiomeType getType(int x, int y) {
        return BiomeTypeLoader.getInstance().getTypeMap()[y][x];
    }
```
