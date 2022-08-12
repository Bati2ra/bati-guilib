package net.bati.guilib.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bati.guilib.gui.screen.ScreenUtils;
import net.bati.guilib.utils.DrawHelper;
import net.bati.guilib.utils.DrawUtils;
import net.bati.guilib.utils.Pivot;
import net.bati.guilib.utils.font.TextUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

/**
 * Accordion con funcionamiento similar al equivalente de Bootstrap, usado en conjunto con {@link AlignedContainer} y
 * Align Vertical se consigue el efecto correcto.
 * @author Bautista Picca
 * @version 1.0 7 Aug 2022
 * @since 1.0 7 Aug 2022
 * @see AlignedContainer
 */
public class Accordion extends Container {
    private boolean show = false;

    // minHeight determina el tamaño del acordeón cerrado
    private int minHeight;
    private String displayName;

    private int contentHeight;
    private double animProgress;
    private int prevPosX;
    private int prevPosY;

    public Accordion(String identifier) {
        super(identifier);
        setBoxWidth(100);
        setMinHeight(10);
        setDisplayName(identifier);

        prevPosX = getOffsetX();
        prevPosY = getOffsetY();
    }

    /**
     * Al cambiar el tamaño se cerrará el acordeón y se le asignará el mismo tamaño a boxHeight, esto para evitar errores visuales.
     * @param minHeight altura del acordeón cerrado
     */
    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
        setBoxHeight(minHeight);
        show = false;
    }

    /**
     * @param displayName Nombre a mostrar en el tope del acordeón, inicialmente es el identificador del Widget.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    protected void draw(MatrixStack matrices, float mouseX, float mouseY, float delta) {
        matrices.push();
        matrices.translate(0.0, 0.0, this.getZ());
        float opacity = getOpacity();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, getRecursiveOpacity());
        animProgress = MathHelper.clamp(MathHelper.lerp(delta * 0.5, animProgress, (show) ? 1 : 0), 0F, 1F);

        prevPosX = (int)MathHelper.lerp(delta * 0.5F, prevPosX, this.getOffsetX());
        prevPosY = (int)MathHelper.lerp(delta * 0.5F, prevPosY, this.getOffsetY());
        DrawHelper.drawWithPivot(matrices, prevPosX, prevPosY, (float)this.getBoxWidth(), (float)this.getBoxHeight(), this.getSize(), delta, this.getPivot(), () -> {
            boolean hovered = isHoveringAccordion(mouseX, mouseY);
            DrawUtils.drawVerticalGradient(matrices, 0, 0, getBoxWidth(), minHeight, 0, hovered ? 16777215 : 1, hovered ? 16777215 : 1, 0.5f, 0.5f);

            TextUtils.drawText(displayName, 2, 3, 0.5f, 16777215, false, false, matrices);
            TextUtils.drawText((show) ? "▲" : "▼", getBoxWidth() - 6, 3, 0.6f, 16777215, false, false, matrices);

            if(animProgress > 0.1) {
                DrawHelper.drawWithPivot(matrices, getBoxWidth()/2, minHeight, (float) this.getBoxWidth(), (float) this.getBoxHeight() - minHeight, this.getSize(), delta, Pivot.MIDDLE_TOP, () -> {
                    setOpacity((float) (getOpacity() * animProgress));
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, getRecursiveOpacity());
                    DrawUtils.drawVerticalGradient(matrices, 0, 0, getBoxWidth(), (float)(contentHeight*animProgress), 0, 1, 1, 0.25f, 0.25f);

                    ScreenUtils.renderWidgets(this.getWidgets(), matrices, mouseX, mouseY - minHeight, delta);
                }, () -> matrices.scale(1, (float)animProgress, 1));
                setOpacity(opacity);
            }
        });


        matrices.translate(0.0, 0.0, -this.getZ());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }

    /**
     * Determina el tamaño del acordeón cada vez que es activado o desactivado.
     * En caso de activarse, calcula el contentHeight con base en los Widgets hijos
     */
    public void fit() {
        contentHeight = 0;
        if(show) {
            getWidgets().forEach((key, value) -> {
                if ((value.getY()) + value.getBoxHeight() * value.getSize() > contentHeight) {
                    contentHeight = (int) ((value.getY()) + value.getBoxHeight() * value.getSize());
                }
            });
        }
        setBoxHeight(minHeight + contentHeight);
    }


    /**
     * Añade {@link Accordion#minHeight} a la detección
     */
    public boolean isHoveringAccordion(double mouseX, double mouseY) {

        return isIgnoreBox() || (mouseX >= getRecursiveXLastTick() && mouseX <= getRecursiveXLastTick()+getBoxWidth()*getRecursiveSizeLastTick() && mouseY >= getRecursiveYLastTick() && mouseY <= getRecursiveYLastTick()+minHeight*getRecursiveSizeLastTick());
    }

    @Override
    public void onMouseClick(double mouseX, double mouseY, int mouseButton) {
        super.onMouseClick(mouseX, mouseY - minHeight, mouseButton);
        if(isHoveringAccordion(mouseX, mouseY)) {
            show = !show;
            fit();
            if(hasParent() && getParent() instanceof AlignedContainer alignedContainer) {
                prevPosX = getOffsetX();
                prevPosY = getOffsetY();
                alignedContainer.fit();
            }
        }
    }
    @Override
    public void onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        super.onMouseDrag(mouseX, mouseY - minHeight, button, deltaX, deltaY);
    }

    @Override
    public void onMouseRelease(double mouseX, double mouseY, int state) {
        super.onMouseRelease(mouseX, mouseY - minHeight, state);
    }

    @Override
    public void onMouseScroll(double mouseX, double mouseY, double amount) {
        super.onMouseScroll(mouseX, mouseY - minHeight, amount);
    }

}
