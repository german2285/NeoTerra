# Сделать RegistryUtilImpl непубличным на NeoForge

`neoforge/src/main/java/neoterra/platform/neoforge/RegistryUtilImpl.java:24`

```java
import net.neoforged.neoforge.registries.RegistryBuilder;
import neoterra.NTCommon;

//this is only public so the initializer class can call register
//TODO make this non public
public final class RegistryUtilImpl {
	private static final List<Registry<?>> BUILTIN_REGISTRIES = Collections.synchronizedList(new ArrayList<>());
	private static final List<DataRegistry<?>> DATA_REGISTRIES = Collections.synchronizedList(new ArrayList<>());
	private static final Map<ResourceKey<?>, DeferredRegister<?>> REGISTERS = new ConcurrentHashMap<>();
```
