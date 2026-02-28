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
    private static int toggleKey = GLFW.GLFW_KEY_V; // По умолчанию клавиша V

    public InvisMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Логика отображения невидимок
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (!isEnabled) return;
        PlayerEntity player = event.getPlayer();
        if (player.isInvisible()) {
            player.setInvisible(false);
        }
    }

    // Обработка нажатия клавиши
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getInstance().screen == null && event.getKey() == toggleKey && event.getAction() == GLFW.GLFW_PRESS) {
            isEnabled = !isEnabled;
            statusMessage("Invis Render: " + (isEnabled ? "§aON" : "§cOFF"));
        }
    }

    // Команда для бинда клавиши в чате
    @SubscribeEvent
    public void onClientChat(ClientChatEvent event) {
        String msg = event.getMessage();
        if (msg.startsWith(".bind inviz ")) {
            event.setCanceled(true); // Сообщение не уйдет на сервер (скрытность)
            String keyName = msg.replace(".bind inviz ", "").toUpperCase();
            
            // Простая логика: берем первую букву (например, 'X' -> GLFW_KEY_X)
            if (keyName.length() == 1) {
                toggleKey = keyName.charAt(0); 
                statusMessage("§6Bind updated! §fNew key: §e" + keyName);
            } else {
                statusMessage("§cError! Use single letter (e.g. .bind inviz X)");
            }
        }
    }

    private void statusMessage(String text) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(new StringTextComponent(text), true);
        }
    }
}
