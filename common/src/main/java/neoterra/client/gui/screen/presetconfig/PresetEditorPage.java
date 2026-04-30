package neoterra.client.gui.screen.presetconfig;

import java.awt.Color;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import neoterra.NTCommon;
import neoterra.client.data.NTTranslationKeys;
import neoterra.client.gui.screen.page.BisectedPage;
import neoterra.client.gui.screen.presetconfig.PresetConfigScreen.PreviewState;
import neoterra.client.gui.screen.presetconfig.PresetListPage.PresetEntry;
import neoterra.client.gui.widget.PreviewWidget;
import neoterra.client.gui.widget.ValueButton;
import neoterra.concurrent.cache.CacheManager;
import neoterra.config.PerformanceConfig;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.data.worldgen.preset.settings.SpawnType;
import neoterra.data.worldgen.preset.settings.WorldSettings;
import neoterra.mixin.ScreenInvoker;
import neoterra.registries.NTRegistries;
import neoterra.world.worldgen.GeneratorContext;
import neoterra.world.worldgen.cell.heightmap.Levels;
import neoterra.world.worldgen.densityfunction.tile.Tile;
import neoterra.world.worldgen.noise.module.Noise;
import neoterra.world.worldgen.util.PosUtil;

public abstract class PresetEditorPage extends BisectedPage<PresetConfigScreen, AbstractWidget, AbstractWidget> {
	private static final float DEFAULT_ZOOM = 49.5F;
	private static final int PREVIEW_MAX_SIZE = 200;
	private static final int PREVIEW_GAP = 8;
	private static final long REGENERATE_DEBOUNCE_MS = 400L;
	private static final int TILE_FACTOR = 6;
	private static final ScheduledExecutorService DEBOUNCE_SCHEDULER = Executors.newSingleThreadScheduledExecutor(runnable -> {
		Thread thread = new Thread(runnable, "NeoTerra-PreviewDebounce");
		thread.setDaemon(true);
		return thread;
	});

	protected PresetEntry preset;
	protected PreviewWidget preview;
	private CycleButton<RenderMode> renderModeButton;
	private ValueButton<Integer> seedButton;
	private ScheduledFuture<?> pendingRegenerate;

	public PresetEditorPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen);

		this.preset = preset;
	}

	private PreviewState state() {
		return this.screen.getPreviewState();
	}

	protected void regenerate() {
		this.state().invalidate();
		if (this.pendingRegenerate != null) {
			this.pendingRegenerate.cancel(false);
		}
		this.pendingRegenerate = DEBOUNCE_SCHEDULER.schedule(
			() -> Minecraft.getInstance().execute(this::regenerateNow),
			REGENERATE_DEBOUNCE_MS,
			TimeUnit.MILLISECONDS
		);
	}

	protected void regenerateNow() {
		NTCommon.debug("PresetEditorPage.regenerateNow: rebuilding preview for preset '{}'", this.preset.getName().getString());
		if (this.preview == null) {
			return;
		}

		PreviewState state = this.state();
		NativeImage cached = state.imageCache.get(state.renderMode);
		if (cached != null) {
			this.preview.setImage(cached);
			return;
		}

		WorldCreationContext settings = this.screen.getSettings();
		RegistryAccess.Frozen registries = settings.worldgenLoadContext();
		HolderLookup.Provider provider = this.preset.getPreset().buildPatch(registries);
		HolderGetter<Preset> presets = provider.lookupOrThrow(NTRegistries.PRESET);
		HolderGetter<Noise> noises = provider.lookupOrThrow(NTRegistries.NOISE);
		Preset preset = presets.getOrThrow(Preset.KEY).value();
		WorldSettings world = preset.world();
		WorldSettings.Properties properties = world.properties;

		try {
			CacheManager.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		PerformanceConfig config = PerformanceConfig.read(PerformanceConfig.DEFAULT_FILE_PATH)
			.resultOrPartial(NTCommon.LOGGER::error)
			.orElseGet(PerformanceConfig::makeDefault);
		GeneratorContext generatorContext = GeneratorContext.makeUncached(preset, noises, (int) settings.options().seed(), TILE_FACTOR, 0, config.batchCount());

		int centerX = 0;
		int centerZ = 0;
		if (preset.world().properties.spawnType == SpawnType.CONTINENT_CENTER) {
			long nearestContinentCenter = generatorContext.lookup.getHeightmap().continent().getNearestCenter(0, 0);
			centerX = PosUtil.unpackLeft(nearestContinentCenter);
			centerZ = PosUtil.unpackRight(nearestContinentCenter);
		}

		Tile tile = generatorContext.generator.generateZoomed(centerX, centerZ, DEFAULT_ZOOM, false).join();
		Levels levels = new Levels(properties.worldHeight, properties.seaLevel);

		int stroke = 2;
		int width = tile.getBlockSize().size();
		NativeImage image = new NativeImage(width, width, false);
		RenderMode mode = state.renderMode;

		tile.iterate((cell, x, z) -> {
			if (x < stroke || z < stroke || x >= width - stroke || z >= width - stroke) {
				image.setPixelRGBA(x, z, Color.BLACK.getRGB());
			} else {
				image.setPixelRGBA(x, z, mode.getColor(cell, levels));
			}
		});

		state.imageCache.put(state.renderMode, image);
		this.preview.setImage(image);
	}

	@Override
	public void init() {
		super.init();

		if (this.preview != null) {
			this.preview.close();
			this.preview = null;
		}

		PreviewState state = this.state();
		if (state.lastPresetIdentity != this.preset) {
			state.invalidate();
			state.lastPresetIdentity = this.preset;
		}

		this.renderModeButton = EditorWidgets.createCycle(RenderMode.values(), state.renderMode, NTTranslationKeys.GUI_BUTTON_RENDER_MODE, (button, value) -> {
			state.renderMode = value;
			this.regenerateNow();
		});
		this.seedButton = EditorWidgets.createSeedButton(NTTranslationKeys.GUI_BUTTON_SEED, (int) this.screen.getSettings().options().seed(), (seed) -> {
			this.screen.setSeed(seed);
			this.state().invalidate();
			this.regenerateNow();
		});

		this.right.addWidget(this.renderModeButton);
		this.right.addWidget(this.seedButton);

		int columnWidth = this.right.getWidth();
		int previewSize = Math.max(0, Math.min(PREVIEW_MAX_SIZE, columnWidth));
		int previewX = this.right.getX() + (columnWidth - previewSize) / 2;
		int previewY = this.right.getY();
		int reservedTop = previewSize + PREVIEW_GAP;
		this.right.setY(previewY + reservedTop);
		this.right.setHeight(Math.max(0, this.right.getHeight() - reservedTop));

		this.preview = new PreviewWidget(previewX, previewY, previewSize);
		this.preview.setView(state.zoom, state.panX, state.panY);
		((ScreenInvoker) this.screen).invokeAddRenderableWidget(this.preview);

		this.regenerateNow();
	}

	@Override
	public void onClose() {
		super.onClose();

		if (this.pendingRegenerate != null) {
			this.pendingRegenerate.cancel(false);
			this.pendingRegenerate = null;
		}

		try {
			NTCommon.debug("PresetEditorPage.onClose: saving preset '{}' and closing preview", this.preset.getName().getString());
			this.preset.save();
			if (this.preview != null) {
				PreviewState state = this.state();
				state.zoom = this.preview.getZoom();
				state.panX = this.preview.getPanX();
				state.panY = this.preview.getPanY();
				this.preview.close();
				this.preview = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDone() {
		super.onDone();

		NTCommon.debug("PresetEditorPage.onDone: applying preset '{}'", this.preset.getName().getString());
		this.screen.applyPreset(this.preset);
	}
}
