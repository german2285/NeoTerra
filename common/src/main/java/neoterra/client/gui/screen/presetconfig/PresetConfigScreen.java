package neoterra.client.gui.screen.presetconfig;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.mojang.blaze3d.platform.NativeImage;

import org.apache.commons.io.file.PathUtils;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.levelgen.WorldOptions;
import neoterra.NTCommon;
import neoterra.client.data.NTTranslationKeys;
import neoterra.client.gui.screen.page.LinkedPageScreen;
import neoterra.client.gui.screen.presetconfig.PresetListPage.PresetEntry;
import neoterra.data.worldgen.Datapacks;
import neoterra.data.worldgen.preset.settings.Preset;

public class PresetConfigScreen extends LinkedPageScreen {
	private CreateWorldScreen parent;
	private volatile CompletableFuture<Void> applying;
	private final PreviewState previewState = new PreviewState();

	public PresetConfigScreen(CreateWorldScreen parent) {
		this.parent = parent;
		this.currentPage = new PresetListPage(this);
	}

	public PreviewState getPreviewState() {
		return this.previewState;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return !this.isApplying();
	}

	@Override
	public void onClose() {
		if(this.isApplying()) {
			return;
		}
		super.onClose();
		this.previewState.invalidate();
		this.minecraft.setScreen(this.parent);
	}

	public static final class PreviewState {
		public RenderMode renderMode = RenderMode.BIOME_TYPE;
		public float zoom = 1.0F;
		public float panX;
		public float panY;
		public Object lastPresetIdentity;
		public final EnumMap<RenderMode, NativeImage> imageCache = new EnumMap<>(RenderMode.class);

		public void invalidate() {
			for (NativeImage image : this.imageCache.values()) {
				image.close();
			}
			this.imageCache.clear();
		}
	}

	public void setSeed(long seed) {
		//TODO update the seed edit box
		this.parent.getUiState().setSettings(this.getSettings().withOptions((options) -> {
			return new WorldOptions(seed, options.generateStructures(), options.generateBonusChest());
		}));
	}

	public WorldCreationContext getSettings() {
		return this.parent.getUiState().getSettings();
	}

	public boolean isApplying() {
		CompletableFuture<Void> current = this.applying;
		return current != null && !current.isDone();
	}

	public CompletableFuture<Void> applyPreset(PresetEntry preset) {
		if(this.isApplying()) {
			NTCommon.debug("PresetConfigScreen.applyPreset: re-entry while applying, returning current future");
			return this.applying;
		}
		NTCommon.debug("PresetConfigScreen.applyPreset: applying preset {}", preset.getName().getString());

		Pair<Path, PackRepository> path = this.parent.getDataPackSelectionSettings(this.parent.getUiState().getSettings().dataConfiguration());
		Path exportPath = path.getFirst().resolve("neoterra-preset.zip");
		PackRepository repository = path.getSecond();
		RegistryAccess registries = this.getSettings().worldgenLoadContext();

		this.setNavigationButtonsActive(false);

		this.applying = CompletableFuture
			.runAsync(() -> {
				try {
					this.exportAsDatapack(exportPath, preset, registries);
				} catch(IOException e) {
					throw new CompletionException(e);
				}
			}, Util.backgroundExecutor())
			.thenAcceptAsync((unused) -> this.finishExportAndApplyPacks(exportPath, repository), this.minecraft::execute)
			.exceptionally((throwable) -> {
				this.minecraft.execute(() -> this.finishApply(false, throwable));
				return null;
			});
		return this.applying;
	}

	private void finishExportAndApplyPacks(Path exportPath, PackRepository repository) {
		String packId = "file/" + exportPath.getFileName();
		repository.reload();

		if(!repository.getAvailableIds().contains(packId)) {
			this.finishApply(false, new IllegalStateException("Pack not discovered after reload: " + packId));
			return;
		}

		List<String> selectedIds = new ArrayList<>(repository.getSelectedIds());
		if(!selectedIds.contains(packId)) {
			selectedIds.add(packId);
			repository.setSelected(selectedIds);
		}

		NTCommon.debug("PresetConfigScreen.applyPreset: pack {} ready, calling tryApplyNewDataPacks", packId);
		this.parent.tryApplyNewDataPacks(repository, false, (data) -> this.finishApply(true, null));
	}

	private void finishApply(boolean success, Throwable error) {
		this.applying = null;

		if(this.minecraft == null || this.minecraft.screen != this) {
			if(error != null) {
				NTCommon.LOGGER.error("PresetConfigScreen.applyPreset failed (screen no longer active)", error);
			} else {
				NTCommon.debug("PresetConfigScreen.applyPreset: completed but screen is no longer active");
			}
			return;
		}

		this.setNavigationButtonsActive(true);

		if(success) {
			NTCommon.debug("PresetConfigScreen.applyPreset: completed, returning to parent");
			this.minecraft.setScreen(this.parent);
		} else {
			NTCommon.LOGGER.error("PresetConfigScreen.applyPreset failed", error);
		}
	}

	private void setNavigationButtonsActive(boolean active) {
		if(this.doneButton != null) this.doneButton.active = active;
		if(this.cancelButton != null) this.cancelButton.active = active;
		if(this.previousButton != null) this.previousButton.active = active;
		if(this.nextButton != null) this.nextButton.active = active;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		if(this.isApplying()) {
			guiGraphics.fill(0, 0, this.width, this.height, 0xC0000000);
			Component label = Component.translatable(NTTranslationKeys.GUI_APPLYING_PRESET).copy().append(this.applyingDots());
			guiGraphics.drawCenteredString(this.font, label, this.width / 2, this.height / 2, 0xFFFFFF);
		}
	}

	private String applyingDots() {
		int n = (int) ((System.currentTimeMillis() / 400L) % 4L);
		return ".".repeat(n);
	}

	public void exportAsDatapack(Path outputPath, PresetEntry presetEntry) throws IOException {
		this.exportAsDatapack(outputPath, presetEntry, this.getSettings().worldgenLoadContext());
	}

	public void exportAsDatapack(Path outputPath, PresetEntry presetEntry, RegistryAccess registryAccess) throws IOException {
		NTCommon.debug("PresetConfigScreen.exportAsDatapack: starting export of preset '{}' to {}", presetEntry.getName().getString(), outputPath);
		long t0 = System.currentTimeMillis();
		Path datagenPath = Files.createTempDirectory("datagen-target-");
		Path datagenOutputPath = datagenPath.resolve("output");

		Preset preset = presetEntry.getPreset();
		Component presetName = presetEntry.getName();

		DataGenerator dataGenerator = Datapacks.makePreset(preset, registryAccess, datagenPath, datagenOutputPath, presetName.getString());
		dataGenerator.run();
		Files.deleteIfExists(outputPath);
		copyToZip(datagenOutputPath, outputPath);
		PathUtils.deleteDirectory(datagenPath);

		NTCommon.debug("Exported datapack to {} in {} ms", outputPath, System.currentTimeMillis() - t0);
	}

	private static void copyToZip(Path input, Path output) {
		Map<String, String> env = ImmutableMap.of("create", "true");
	    URI uri = URI.create("jar:" + output.toUri());
	    try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
	        PathUtils.copyDirectory(input, fs.getPath("/"), StandardCopyOption.REPLACE_EXISTING);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
