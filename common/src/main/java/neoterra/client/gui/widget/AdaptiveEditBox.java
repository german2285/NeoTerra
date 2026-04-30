package neoterra.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.CommonComponents;

public class AdaptiveEditBox extends EditBox {
	private static final int HINT_HORIZONTAL_PADDING = 8;

	private final Font font;
	private final Component fullHint;
	private final Component shortHint;

	public AdaptiveEditBox(Font font, Component fullHint, Component shortHint) {
		super(font, -1, -1, -1, -1, CommonComponents.EMPTY);
		this.font = font;
		this.fullHint = fullHint;
		this.shortHint = shortHint;
		this.setHint(fullHint);
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int available = this.getWidth() - HINT_HORIZONTAL_PADDING;
		Component hint = this.font.width(this.fullHint) <= available ? this.fullHint : this.shortHint;
		this.setHint(hint);
		super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
	}
}
