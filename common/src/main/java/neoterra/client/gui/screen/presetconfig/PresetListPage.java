package neoterra.client.gui.screen.presetconfig;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.commons.compress.utils.FileNameUtils;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.JsonOps;

import io.netty.util.internal.StringUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId;
import neoterra.client.gui.widget.AdaptiveEditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.GsonHelper;
import neoterra.NTCommon;
import neoterra.client.data.NTTranslationKeys;
import neoterra.client.gui.Toasts;
import neoterra.client.gui.screen.page.BisectedPage;
import neoterra.client.gui.screen.page.LinkedPageScreen.Page;
import neoterra.client.gui.screen.presetconfig.PresetListPage.PresetEntry;
import neoterra.client.gui.widget.Label;
import neoterra.client.gui.widget.WidgetList;
import neoterra.client.gui.widget.WidgetList.Entry;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.data.worldgen.preset.settings.Presets;
import neoterra.platform.ConfigUtil;

class PresetListPage extends BisectedPage<PresetConfigScreen, PresetEntry, AbstractWidget> {
	private static final Path PRESET_PATH = ConfigUtil.rtf("presets");
	private static final Path EXPORT_PATH = ConfigUtil.rtf("exports");


	private static final Predicate<String> IS_VALID = Pattern.compile("^[A-Za-z0-9\\-_ ]+$").asPredicate();

	private EditBox input;
	private Button createPreset;
	private Button deletePreset;
	private Button exportAsDatapack;
	private Button copyPreset;
	private Button openPresetFolder;
	private Button openExportFolder;
	
	public PresetListPage(PresetConfigScreen screen) {
		super(screen);
		
		try {
			if(!Files.exists(PRESET_PATH)) Files.createDirectory(PRESET_PATH);
			if(!Files.exists(EXPORT_PATH)) Files.createDirectory(EXPORT_PATH);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Component title() {
		return Component.translatable(NTTranslationKeys.GUI_SELECT_PRESET_TITLE);
	}

	@Override
	public void init() {
		super.init();
		
		AdaptiveEditBox adaptive = new AdaptiveEditBox(
			this.screen.font,
			Component.translatable(NTTranslationKeys.GUI_INPUT_PROMPT).withStyle(ChatFormatting.DARK_GRAY),
			Component.translatable(NTTranslationKeys.GUI_INPUT_PROMPT_SHORT).withStyle(ChatFormatting.DARK_GRAY)
		);
		adaptive.setResponder((text) -> {
			boolean isValid = this.isValidPresetName(text);
			final int white = 14737632;
			final int red = 0xFFFF3F30;
			this.createPreset.active = isValid;
			this.input.setTextColor(isValid ? white : red);
		});
		this.input = adaptive;
		this.createPreset = PresetWidgets.createThrowingButton(NTTranslationKeys.GUI_BUTTON_CREATE, () -> {
			new PresetEntry(Component.literal(this.input.getValue()), Presets.makeLegacyDefault(), false, this).save();
			this.rebuildPresets();
			this.input.setValue(StringUtil.EMPTY_STRING);
		});
		this.createPreset.active = this.isValidPresetName(this.input.getValue());
		this.copyPreset = PresetWidgets.createThrowingButton(NTTranslationKeys.GUI_BUTTON_COPY, () -> {
			PresetEntry preset = this.left.getSelected().getWidget();
			String name = preset.getName().getString();
			int counter = 1;
			String uniqueName;
			while(Files.exists(PRESET_PATH.resolve((uniqueName = name + " (" + counter + ")") + ".json"))) { 
				counter++;
			}
			new PresetEntry(Component.literal(uniqueName), preset.getPreset().copy(), false, this).save();
			this.rebuildPresets();
		});
		this.deletePreset = PresetWidgets.createThrowingButton(NTTranslationKeys.GUI_BUTTON_DELETE, () -> {
			PresetEntry preset = this.left.getSelected().getWidget();
			Files.delete(preset.getPath());
			this.rebuildPresets();
		});
		this.openPresetFolder = PresetWidgets.createThrowingButton(NTTranslationKeys.GUI_BUTTON_OPEN_PRESET_FOLDER, () -> {
			Util.getPlatform().openUri(PRESET_PATH.toUri());
			this.rebuildPresets();
		});
		this.openExportFolder = PresetWidgets.createThrowingButton(NTTranslationKeys.GUI_BUTTON_OPEN_EXPORT_FOLDER, () -> {
			Util.getPlatform().openUri(EXPORT_PATH.toUri());
			this.rebuildPresets();
		});
		this.exportAsDatapack = PresetWidgets.createThrowingButton(NTTranslationKeys.GUI_BUTTON_EXPORT_AS_DATAPACK, () -> {
			PresetEntry preset = this.left.getSelected().getWidget();
			Path path = EXPORT_PATH.resolve(preset.getName().getString() + ".zip");
			this.screen.exportAsDatapack(path, preset);
			this.rebuildPresets();
			
			Toasts.notify(NTTranslationKeys.GUI_BUTTON_EXPORT_SUCCESS, Component.literal(path.toString()), SystemToastId.WORLD_BACKUP);
		});

		this.right.addWidget(this.input);
		this.right.addWidget(this.createPreset);
		this.right.addWidget(this.copyPreset);
		this.right.addWidget(this.deletePreset);
		this.right.addWidget(this.openPresetFolder);
		this.right.addWidget(this.openExportFolder);
		this.right.addWidget(this.exportAsDatapack);
		
		this.left.setRenderSelected(true);
		
		try {
			this.rebuildPresets();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Optional<Page> previous() {
		return Optional.empty();
	}

	@Override
	public Optional<Page> next() {
		return Optional.ofNullable(this.left).map(WidgetList::getSelected).map((e) -> {
			PresetEntry entry = e.getWidget();
			if(entry.isBuiltin()) {
				entry = new PresetEntry(entry.name, entry.preset.copy(), true, (b) -> {});
			}
			return new WorldSettingsPage(this.screen, entry);
		});
	}
	
	@Override
	public void onDone() {
		super.onDone();

		Entry<PresetEntry> selected = this.left.getSelected();
		if(selected != null) {
			this.screen.applyPreset(selected.getWidget());
		}
	}
	
	private void selectPreset(@Nullable PresetEntry entry) {
		boolean active = entry != null;
		
		this.screen.doneButton.active = active;
		this.copyPreset.active = active;
		this.deletePreset.active = active && !entry.isBuiltin();
		this.exportAsDatapack.active = active;
		this.screen.nextButton.active = active;
	}
	
	private void rebuildPresets() throws IOException {
		this.selectPreset(null);
		
		List<PresetEntry> entries = new ArrayList<>();
		entries.addAll(this.listPresets(PRESET_PATH));

		entries.add(new PresetEntry(Component.translatable(NTTranslationKeys.GUI_DEFAULT_PRESET_NAME).withStyle(ChatFormatting.GRAY), Presets.makeNTDefault(), true, this));
		entries.add(new PresetEntry(Component.translatable(NTTranslationKeys.GUI_DEFAULT_LEGACY_PRESET_NAME).withStyle(ChatFormatting.GRAY), Presets.makeLegacyDefault(), true, this));
		entries.add(new PresetEntry(Component.translatable(NTTranslationKeys.GUI_BEAUTIFUL_PRESET_NAME).withStyle(ChatFormatting.GRAY), Presets.makeLegacyBeautiful(), true, this));
		entries.add(new PresetEntry(Component.translatable(NTTranslationKeys.GUI_HUGE_BIOMES_PRESET_NAME).withStyle(ChatFormatting.GRAY), Presets.makeLegacyHugeBiomes(), true, this));
		entries.add(new PresetEntry(Component.translatable(NTTranslationKeys.GUI_LITE_PRESET_NAME).withStyle(ChatFormatting.GRAY), Presets.makeLegacyLite(), true, this));
		entries.add(new PresetEntry(Component.translatable(NTTranslationKeys.GUI_VANILLAISH_PRESET_NAME).withStyle(ChatFormatting.GRAY), Presets.makeLegacyVanillaish(), true, this));
		this.left.replaceEntries(entries.stream().map(WidgetList.Entry::new).toList());
	}
	
	private boolean isValidPresetName(String text) {
		return IS_VALID.test(text) && !this.hasPresetWithName(text);
	}
	
	private boolean hasPresetWithName(String name) {
		return this.left.children().stream().filter((entry) -> {
			return entry.getWidget().getName().getString().equals(name);
		}).findAny().isPresent();
	}
	
	private List<PresetEntry> listPresets(Path path) throws IOException	{
		List<PresetEntry> presets = new ArrayList<>();
		if(Files.exists(path)) {
			for(Path presetPath : Files.list(path)
				.filter(Files::isRegularFile)
				.toList()
			) {
				try(Reader reader = Files.newBufferedReader(presetPath)) {
					String base = FileNameUtils.getBaseName(presetPath.toString());
					DataResult<Preset> result = Preset.DIRECT_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader));
					Optional<Error<Preset>> error = result.error();
					if(error.isPresent()) {
						NTCommon.LOGGER.error(error.get().message());
						continue;
					}
					Preset preset = result.result().get();
					presets.add(new PresetEntry(Component.literal(base), preset, false, this));
				}
			}
		}
		return presets;
	}
	
	public static class PresetEntry extends Label {
		private static final int TEXT_PADDING_X = 4;

		private Component name;
		private Preset preset;
		private boolean builtin;

		public PresetEntry(Component name, Preset preset, boolean builtin, OnPress onPress) {
			super(-1, -1, -1, -1, onPress, name);

			this.name = name;
			this.preset = preset;
			this.builtin = builtin;
		}

		@Override
		public void renderWidget(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
			net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
			int textWidth = mc.font.width(this.getMessage());
			int textX = textWidth + TEXT_PADDING_X * 2 <= this.getWidth()
				? this.getX() + (this.getWidth() - textWidth) / 2
				: this.getX() + TEXT_PADDING_X;
			int textY = this.getY() + (this.height - 8) / 2;
			graphics.drawString(mc.font, this.getMessage(), textX, textY, 0xFFFFFF);
		}
		
		public PresetEntry(Component name, Preset preset, boolean builtin, PresetListPage page) {
			this(name, preset, builtin, (b) -> {
				if(b instanceof PresetEntry entry) {
					page.selectPreset(entry);
				}
			});
		}

		public Component getName() {
			return this.name;
		}
		
		public Preset getPreset() {
			return this.preset;
		}
		
		public boolean isBuiltin() {
			return this.builtin;
		}
		
		public Path getPath() {
			return PRESET_PATH.resolve(this.name.getString() + ".json");
		}
		
		//FIXME delete old pack before save
		public void save() throws IOException {
			if(!this.builtin) {
				try(
					Writer writer = Files.newBufferedWriter(this.getPath());
					JsonWriter jsonWriter = new JsonWriter(writer);
				) {
					JsonElement element = Preset.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, this.preset).result().orElseThrow();
					jsonWriter.setSerializeNulls(false);
					jsonWriter.setIndent("  ");
					GsonHelper.writeValue(jsonWriter, element, null);
				}
			}
		}
	}
}
