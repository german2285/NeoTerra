package neoterra.client.gui.widget;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public class Label extends Button {
    
	public Label(int x, int y, int width, int height, Component component) {
    	this(x, y, width, height, (b) -> {}, component);
	}
	
	public Label(int x, int y, int width, int height, OnPress onPress, Component component) {
    	super(x, y, width, height, component, onPress, Supplier::get);
	}

	@Override
    public void playDownSound(SoundManager soundManager) {
    }

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		Minecraft minecraft = Minecraft.getInstance();
		int textWidth = minecraft.font.width(this.getMessage());
		int textX = textWidth <= this.getWidth()
			? this.getX() + (this.getWidth() - textWidth) / 2
			: this.getX();
		int textY = this.getY() + (this.height - 8) / 2;
		graphics.drawString(minecraft.font, this.getMessage(), textX, textY, 0xFFFFFF);
	}
}
