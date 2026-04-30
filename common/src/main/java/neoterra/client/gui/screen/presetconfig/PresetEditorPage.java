package neoterra.client.gui.screen.presetconfig;

import net.minecraft.client.gui.components.AbstractWidget;
import neoterra.NTCommon;
import neoterra.client.gui.screen.page.BisectedPage;
import neoterra.client.gui.screen.presetconfig.PresetListPage.PresetEntry;

public abstract class PresetEditorPage extends BisectedPage<PresetConfigScreen, AbstractWidget, AbstractWidget> {
	protected PresetEntry preset;

	public PresetEditorPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen);

		this.preset = preset;
	}

	protected void regenerate() {
		NTCommon.debug("PresetEditorPage.regenerate: '{}'", this.preset.getName().getString());
		// TODO Сюда вставить перерисовку миникарты после изменения настроек пресета.
		// Подклассы (WorldSettingsPage и т.п.) дёргают this.regenerate() при каждом
		// изменении значения слайдера/кнопки. Здесь нужно либо запросить новую
		// картинку у генератора, либо перерисовать пиксели в существующей текстуре.
	}

	@Override
	public void init() {
		super.init();

		// TODO Сюда добавить виджет миникарты в this.right (правая колонка).
		// Когда будет готовый рендерер — создать виджет, добавить через this.right.addWidget(...)
		// и при необходимости вызывать regenerate() на init.
	}

	@Override
	public void onClose() {
		super.onClose();

		try {
			NTCommon.debug("PresetEditorPage.onClose: saving preset '{}'", this.preset.getName().getString());
			this.preset.save();
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
