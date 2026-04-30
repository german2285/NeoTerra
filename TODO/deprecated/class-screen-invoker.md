# @Deprecated на интерфейсе ScreenInvoker

`common/src/main/java/neoterra/mixin/ScreenInvoker.java:12`

```java
//a bit unnecessary but whatever
//addRenderableWidget can be overridden so we don't use an access widener
@Deprecated
@Mixin(Screen.class)
public interface ScreenInvoker {

	@Invoker("addRenderableWidget")
    <T extends GuiEventListener & Renderable> T invokeAddRenderableWidget(T guiEventListener);
}
```
