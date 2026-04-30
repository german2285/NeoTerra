# Удалять старый pack перед сохранением

`common/src/main/java/neoterra/client/gui/screen/presetconfig/PresetListPage.java:286`

```java
		public Path getPath() {
			return PRESET_PATH.resolve(this.name.getString() + ".json");
		}
		
		//FIXME delete old pack before save
		public void save() throws IOException {
			if(!this.builtin) {
				try(
					Writer writer = Files.newBufferedWriter(this.getPath());
					JsonWriter jsonWriter = new JsonWriter(writer);
```
