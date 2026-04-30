# addEnchantment(...) и блок addBiome(...)

`common/src/main/java/neoterra/client/data/LanguageProvider.java:84-100`

```java
    public void add(ItemStack key, String name) {
        add(key.getDescriptionId(), name);
    }

    //public void addEnchantment(Supplier<? extends Enchantment> key, String name) {
    //    add(key.get(), name);
    //}
//
    //public void add(Enchantment key, String name) {
    //    add(key.getDescriptionId(), name);
    //}

    /*
    public void addBiome(Supplier<? extends Biome> key, String name) {
        add(key.get(), name);
    }

    public void add(Biome key, String name) {
        add(key.getTranslationKey(), name);
    }
    */

    public void addEffect(Supplier<? extends MobEffect> key, String name) {
        add(key.get(), name);
    }
```
