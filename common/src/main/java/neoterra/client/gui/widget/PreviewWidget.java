package neoterra.client.gui.widget;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import neoterra.NTCommon;

public class PreviewWidget extends AbstractWidget {
	public static final int DEFAULT_TILE_FACTOR = 6;
	public static final int DEFAULT_CANVAS_SIZE = 16 << DEFAULT_TILE_FACTOR;

	private DynamicTexture texture;
	private ResourceLocation textureId;
	private int canvasSize;
	private boolean closed;

	public PreviewWidget(int x, int y, int size) {
		super(x, y, size, size, CommonComponents.EMPTY);
		this.allocate(DEFAULT_CANVAS_SIZE);
		this.clear();
	}

	private void allocate(int newCanvasSize) {
		if (this.texture != null) {
			this.texture.close();
		}
		this.canvasSize = newCanvasSize;
		this.texture = new DynamicTexture(new NativeImage(newCanvasSize, newCanvasSize, false));
		this.textureId = Minecraft.getInstance().getTextureManager().register(NTCommon.MOD_ID + "-preview", this.texture);
	}

	public int getCanvasSize() {
		return this.canvasSize;
	}

	public NativeImage getCanvas() {
		return this.texture.getPixels();
	}

	public void uploadCanvas() {
		this.texture.upload();
	}

	public void setImage(NativeImage source) {
		int sourceSize = source.getWidth();
		if (this.canvasSize != sourceSize) {
			this.allocate(sourceSize);
		}
		this.getCanvas().copyFrom(source);
		this.uploadCanvas();
	}

	public void clear() {
		NativeImage pixels = this.getCanvas();
		for (int z = 0; z < this.canvasSize; z++) {
			for (int x = 0; x < this.canvasSize; x++) {
				pixels.setPixelRGBA(x, z, 0xFF202020);
			}
		}
		this.uploadCanvas();
	}

	public void close() {
		if (this.closed) {
			return;
		}
		this.texture.close();
		this.closed = true;
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.height = this.getWidth();
		int x = this.getX();
		int y = this.getY();
		int w = this.getWidth();
		int h = this.getHeight();
		guiGraphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF000000);
		guiGraphics.blit(this.textureId, x, y, 0, 0, w, h, w, h);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
	}
}
