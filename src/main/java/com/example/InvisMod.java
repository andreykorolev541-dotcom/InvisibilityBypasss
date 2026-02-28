package com.example;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod("seethrough")
public class InvisMod {
    private static boolean isEnabled = true;
    private static int toggleKey = GLFW.GLFW_KEY_V;

    public InvisMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (!isEnabled) return;
        PlayerEntity player = event.getPlayer();
        // В 1.16.5 для надежности лучше временно отключать флаг невидимости 
        // и разрешать рендер имени
        if (player.isInvisible()) {
            player.setInvisible(false);
            event.getRenderer().getFont().drawInBatch(" ", 0, 0, 0, false, event.getMatrixStack().last().pose(), event.getBuffers(), false, 0, event.getLight());
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getInstance().screen == null && event.getKey() == toggleKey && event.getAction() == GLFW.GLFW_PRESS) {
            isEnabled = !isEnabled;
            statusMessage("Invis Render: " + (isEnabled ? "§aON" : "§cOFF"));
        }
    }

    @SubscribeEvent
    public void onClientChat(ClientChatEvent event) {
        String msg = event.getMessage();
        if (msg.startsWith(".bind inviz ")) {
            event.setCanceled(true);
            try {
                String keyName = msg.replace(".bind inviz ", "").toUpperCase();
                // Для клавиш-букв (A-Z)
                if (keyName.length() == 1) {
                    toggleKey = (int) keyName.charAt(0);
                    statusMessage("§6Bind updated! §fNew key: §e" + keyName);
                }
            } catch (Exception e) {
                statusMessage("§cError! Use: .bind inviz X");
            }
        }
    }

    private void statusMessage(String text) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(new StringTextComponent(text), true);
        }
    }
}
