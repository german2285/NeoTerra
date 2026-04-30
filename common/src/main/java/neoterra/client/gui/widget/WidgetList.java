package neoterra.client.gui.widget;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import neoterra.NTCommon;

public class WidgetList<T extends AbstractWidget> extends ContainerObjectSelectionList<WidgetList.Entry<T>> {
	private boolean renderSelected;
	
    public WidgetList(Minecraft minecraft, int i, int j, int k, int l) {
        super(minecraft, i, j, k, l);
    }

    public void select(T widget) {
    	for(Entry<T> entry : this.children()) {
    		if(entry.widget.equals(widget)) {
    			this.setSelected(entry);
    			return;
    		}
    	}
    }
    
    public <W extends T> W addWidget(W widget) {
        super.addEntry(new Entry<>(widget));
        return widget;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        for (Entry<T> entry : this.children()) {
            T widget = entry.getWidget();
            if (widget.visible && widget.isMouseOver(mx, my)) {
                if (entry.mouseClicked(mx, my, button)) {
                    this.setFocused(entry);
                    this.setDragging(true);
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        for (Entry<T> entry : this.children()) {
            T widget = entry.getWidget();
            if (widget.visible && widget.isMouseOver(mx, my)) {
                if (widget.mouseScrolled(mx, my, scrollX, scrollY)) {
                    return true;
                }
            }
        }
        return super.mouseScrolled(mx, my, scrollX, scrollY);
    }

    public void setRenderSelected(boolean renderSelected) {
    	this.renderSelected = renderSelected;
    }

    @Override
    protected boolean isSelectedItem(int i) {
        return this.renderSelected && Objects.equals(this.getSelected(), this.children().get(i));
    }

    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_GAP = 4;
    private static final int LEFT_MARGIN = 10;
    private static final int RIGHT_MARGIN = 4;

    @Override
    public int getRowWidth() {
        return Math.max(0, this.width - LEFT_MARGIN - SCROLLBAR_GAP - SCROLLBAR_WIDTH - RIGHT_MARGIN);
    }

    @Override
    public int getRowLeft() {
        return this.getX() + LEFT_MARGIN;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getX() + this.width - RIGHT_MARGIN - SCROLLBAR_WIDTH;
    }

    public static class Entry<T extends AbstractWidget> extends ContainerObjectSelectionList.Entry<Entry<T>> {
        private T widget;

        public Entry(T widget) {
            this.widget = widget;
        }

        public T getWidget() {
        	return this.widget;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.singletonList(this.widget);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            int optionWidth = Math.min(396, width);
            int padding = (width - optionWidth) / 2;
            widget.setX(left + padding);
            widget.setY(top);
            widget.visible = true;
            widget.setWidth(optionWidth);
            widget.setHeight(height - 1);
            widget.render(guiGraphics, mouseX, mouseY, partialTicks);
        }

		@Override
		public List<T> narratables() {
			return Collections.singletonList(this.widget);
		}
    }
}
