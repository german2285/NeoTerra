package neoterra.client.gui.widget;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import neoterra.NTCommon;
import neoterra.client.data.NTTranslationKeys;
import neoterra.world.worldgen.cell.Cell;
import neoterra.world.worldgen.densityfunction.tile.Tile;

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

	private Tile tile;
	private int worldCenterX;
	private int worldCenterZ;
	private float blocksPerPixel = 1.0F;

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

	public void setTile(Tile tile, int worldCenterX, int worldCenterZ, float blocksPerPixel) {
		this.tile = tile;
		this.worldCenterX = worldCenterX;
		this.worldCenterZ = worldCenterZ;
		this.blocksPerPixel = blocksPerPixel;
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

		if (this.tile != null && this.isMouseOver(mouseX, mouseY)) {
			this.drawHoverOverlay(guiGraphics, mouseX, mouseY);
		}
	}

	private void drawHoverOverlay(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		float widgetSize = this.getWidth();
		float fracX = (mouseX - this.getX()) / widgetSize;
		float fracY = (mouseY - this.getY()) / widgetSize;

		float visible = this.visibleSize();
		float canvasX = (CANVAS_SIZE - visible) / 2F + this.panX + fracX * visible;
		float canvasY = (CANVAS_SIZE - visible) / 2F + this.panY + fracY * visible;

		int pixelX = Mth.clamp((int) canvasX, 0, CANVAS_SIZE - 1);
		int pixelZ = Mth.clamp((int) canvasY, 0, CANVAS_SIZE - 1);

		int border = this.tile.getBlockSize().border();
		Cell cell = this.tile.getCellRaw(border + pixelX, border + pixelZ);
		if (cell == null || cell.biome == null) {
			return;
		}

		int worldX = this.worldCenterX + Math.round((pixelX - CANVAS_SIZE / 2F) * this.blocksPerPixel);
		int worldZ = this.worldCenterZ + Math.round((pixelZ - CANVAS_SIZE / 2F) * this.blocksPerPixel);

		Component biomeName = Component.translatable(NTTranslationKeys.biome(cell.biome.name()));
		String terrainId = cell.terrain != null ? cell.terrain.getName() : "?";
		Component terrainName = Component.literal(terrainId);
		Component coords = Component.translatable(NTTranslationKeys.GUI_LABEL_PREVIEW_COORDS, worldX, worldZ);

		Font font = Minecraft.getInstance().font;
		int padding = 3;
		int lineHeight = font.lineHeight + 1;
		int textWidth = Math.max(Math.max(font.width(biomeName), font.width(terrainName)), font.width(coords));
		int boxWidth = textWidth + padding * 2;
		int boxHeight = lineHeight * 3 + padding * 2;

		int boxX = this.getX() + padding;
		int boxY = this.getY() + this.getHeight() - padding - boxHeight;

		guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xC0000000);
		guiGraphics.drawString(font, biomeName, boxX + padding, boxY + padding, 0xFFFFFFFF);
		guiGraphics.drawString(font, terrainName, boxX + padding, boxY + padding + lineHeight, 0xFFCCAA66);
		guiGraphics.drawString(font, coords, boxX + padding, boxY + padding + lineHeight * 2, 0xFFAAAAAA);
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
