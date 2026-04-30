package neoterra.client.gui.screen.presetconfig;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntConsumer;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import neoterra.client.gui.Tooltips;
import neoterra.client.gui.widget.Slider;
import neoterra.client.gui.widget.ValueButton;

final class EditorWidgets {

	private EditorWidgets() {
	}

	public static <T extends Enum<T>> CycleButton<T> createCycle(T[] values, T initial, String translationKey, CycleButton.OnValueChange<T> onChange) {
		CycleButton<T> button = CycleButton.<T>builder((value) -> Component.literal(value.name()))
			.withInitialValue(initial)
			.withValues(ImmutableList.copyOf(values))
			.create(-1, -1, -1, -1, Component.translatable(translationKey), onChange);
		button.setTooltip(Tooltips.create(Tooltips.translationKey(translationKey)));
		return button;
	}

	public static Slider createIntSlider(int initial, int min, int max, String translationKey, Slider.Callback callback) {
		Slider slider = new Slider(-1, -1, -1, -1, initial, min, max, Component.translatable(translationKey), Slider.Format.INT, callback);
		slider.setTooltip(Tooltips.create(Tooltips.translationKey(translationKey)));
		return slider;
	}

	public static ValueButton<Integer> createSeedButton(String translationKey, int initial, IntConsumer onChange) {
		ValueButton<Integer> button = new ValueButton<>(-1, -1, -1, -1, Component.translatable(translationKey), (b) -> {
			if (b instanceof ValueButton<?> vb) {
				@SuppressWarnings("unchecked")
				ValueButton<Integer> typed = (ValueButton<Integer>) vb;
				int next = ThreadLocalRandom.current().nextInt();
				typed.setValue(next);
				onChange.accept(next);
			}
		}, initial);
		button.setTooltip(Tooltips.create(Tooltips.translationKey(translationKey)));
		return button;
	}
}
