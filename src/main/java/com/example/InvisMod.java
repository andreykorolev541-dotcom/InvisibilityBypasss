package com.example;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.matrix.MatrixStack;

@Mod("seethrough")
public class InvisMod {
    private static boolean isEnabled = true;
    private static int toggleKey = GLFW.GLFW_KEY_V;

    public InvisMod() {
        // Регистрация в шине событий Forge
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        if (!isEnabled || !(event.getEntity() instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) event.getEntity();
        // Принудительно отображаем невидимых игроков
        if (player.isInvisible()) {
            player.setInvisible(false);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        if (!isEnabled || !(event.getEntity() instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) event.getEntity();
        
        // Возвращаем статус невидимости после рендера, чтобы не ломать логику игры
        // (Опционально, если нужно только визуальное отображение)
        
        renderArmorInfo(event, player);
    }

    private void renderArmorInfo(RenderLivingEvent.Post<?, ?> event, PlayerEntity player) {
        float armor = player.getArmorValue();
        if (armor <= 0) return;

        MatrixStack matrixStack = event.getMatrixStack();
        matrixStack.pushPose();
        
        // Поднимаем текст чуть выше головы или сбоку
        matrixStack.translate(0, player.getBbHeight() + 0.5F, 0);
        // Разворачиваем текст к камере
        matrixStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        matrixStack.scale(-0.025F, -0.025F, 0.025F);

        String text = "🛡 " + (int)armor;
        int color = 0xFFFFFF; // Белый
        
        Minecraft.getInstance().font.draw(matrixStack, text, 
            -Minecraft.getInstance().font.width(text) / 2f, 0, color);
            
        matrixStack.popPose();
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getInstance().screen == null && 
            event.getKey() == toggleKey && 
            event.getAction() == GLFW.GLFW_PRESS) {
            
            isEnabled = !isEnabled;
            statusMessage("Invis Render: " + (isEnabled ? "§aON" : "§cOFF"));
        }
    }

    private void statusMessage(String text) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(new StringTextComponent(text), true);
        }
    }
}
