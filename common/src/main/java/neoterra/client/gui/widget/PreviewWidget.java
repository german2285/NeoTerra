package neoterra.client.gui.widget;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import neoterra.NTCommon;

public class PreviewWidget extends AbstractWidget {
	public static final int CANVAS_SIZE = 1024;
	private static final float MIN_ZOOM = 1.0F;
	private static final float MAX_ZOOM = 32.0F;
	private static final float ZOOM_STEP = 1.25F;

	private DynamicTexture texture;
	private ResourceLocation textureId;
	private boolean closed;

	private float zoom = 1.0F;
	private float panX;
	private float panY;

	public PreviewWidget(int x, int y, int size) {
		super(x, y, size, size, CommonComponents.EMPTY);
		this.allocate();
		this.clear();
	}

	private void allocate() {
		if (this.texture != null) {
			this.texture.close();
		}
		this.texture = new DynamicTexture(new NativeImage(CANVAS_SIZE, CANVAS_SIZE, false));
		this.textureId = Minecraft.getInstance().getTextureManager().register(NTCommon.MOD_ID + "-preview", this.texture);
	}

	public NativeImage getCanvas() {
		return this.texture.getPixels();
	}

	public void uploadCanvas() {
		this.texture.upload();
	}

	public void setImage(NativeImage source) {
		if (source.getWidth() != CANVAS_SIZE || source.getHeight() != CANVAS_SIZE) {
			throw new IllegalArgumentException("PreviewWidget expects " + CANVAS_SIZE + "×" + CANVAS_SIZE + " image, got " + source.getWidth() + "×" + source.getHeight());
		}
		this.getCanvas().copyFrom(source);
		this.uploadCanvas();
	}

	public void resetView() {
		this.zoom = 1.0F;
		this.panX = 0F;
		this.panY = 0F;
	}

	public void setView(float zoom, float panX, float panY) {
		this.zoom = Mth.clamp(zoom, MIN_ZOOM, MAX_ZOOM);
		this.panX = panX;
		this.panY = panY;
		this.clampPan();
	}

	public float getZoom() {
		return this.zoom;
	}

	public float getPanX() {
		return this.panX;
	}

	public float getPanY() {
		return this.panY;
	}

	public void clear() {
		NativeImage pixels = this.getCanvas();
		for (int z = 0; z < CANVAS_SIZE; z++) {
			for (int x = 0; x < CANVAS_SIZE; x++) {
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

	private float visibleSize() {
		return CANVAS_SIZE / this.zoom;
	}

	private void clampPan() {
		float visible = this.visibleSize();
		float maxPan = Math.max(0F, (CANVAS_SIZE - visible) / 2F);
		this.panX = Mth.clamp(this.panX, -maxPan, maxPan);
		this.panY = Mth.clamp(this.panY, -maxPan, maxPan);
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.height = this.getWidth();
		int x = this.getX();
		int y = this.getY();
		int w = this.getWidth();
		int h = this.getHeight();

		this.clampPan();
		float visible = this.visibleSize();
		float u = (CANVAS_SIZE - visible) / 2F + this.panX;
		float v = (CANVAS_SIZE - visible) / 2F + this.panY;

		guiGraphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF000000);
		guiGraphics.blit(this.textureId, x, y, w, h, u, v, (int) Math.max(1F, visible), (int) Math.max(1F, visible), CANVAS_SIZE, CANVAS_SIZE);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (!this.isMouseOver(mouseX, mouseY) || scrollY == 0) {
			return false;
		}

		float widgetSize = this.getWidth();
		float fracX = (float) ((mouseX - this.getX()) / widgetSize);
		float fracY = (float) ((mouseY - this.getY()) / widgetSize);

		float oldVisible = this.visibleSize();
		float worldX = (CANVAS_SIZE - oldVisible) / 2F + this.panX + fracX * oldVisible;
		float worldY = (CANVAS_SIZE - oldVisible) / 2F + this.panY + fracY * oldVisible;

		float factor = scrollY > 0 ? ZOOM_STEP : 1F / ZOOM_STEP;
		this.zoom = Mth.clamp(this.zoom * factor, MIN_ZOOM, MAX_ZOOM);

		float newVisible = this.visibleSize();
		this.panX = worldX - fracX * newVisible - (CANVAS_SIZE - newVisible) / 2F;
		this.panY = worldY - fracY * newVisible - (CANVAS_SIZE - newVisible) / 2F;
		this.clampPan();
		return true;
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
		float widgetSize = this.getWidth();
		if (widgetSize <= 0) {
			return;
		}
		float screenToTexture = this.visibleSize() / widgetSize;
		this.panX -= (float) dragX * screenToTexture;
		this.panY -= (float) dragY * screenToTexture;
		this.clampPan();
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
	}

	@Override
	public void playDownSound(net.minecraft.client.sounds.SoundManager soundManager) {
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
	}
}
