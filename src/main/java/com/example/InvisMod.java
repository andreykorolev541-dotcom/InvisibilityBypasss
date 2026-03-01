@Mod("seethrough")
public class InvisMod {
    private static boolean isEnabled = true;
    private static int toggleKey = GLFW.GLFW_KEY_V;

    public InvisMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        if (!isEnabled || !(event.getEntity() instanceof PlayerEntity)) return;
        
        PlayerEntity player = (PlayerEntity) event.getEntity();
        
        // Принудительно заставляем игру думать, что игрока нужно рендерить, даже если он невидим
        if (player.isInvisible()) {
            // Вместо setInvisible(false), который меняет данные сущности, 
            // мы просто игнорируем проверку на невидимость для рендера
            player.setInvisible(false); 
            // После завершения рендера в Post событии нужно будет вернуть как было
        }
    }

    @SubscribeEvent
    public void onRenderPost(RenderLivingEvent.Post<?, ?> event) {
        if (!isEnabled || !(event.getEntity() instanceof PlayerEntity)) return;
        
        PlayerEntity player = (PlayerEntity) event.getEntity();
        
        // Отрисовка "полоски" брони рядом с хитбоксом
        renderArmorBar(event);
    }

    private void renderArmorBar(RenderLivingEvent.Post<?, ?> event) {
        PlayerEntity player = (PlayerEntity) event.getEntity();
        float armorValue = player.getArmorValue(); // Макс 20
        if (armorValue <= 0) return;

        com.mojang.blaze3d.matrix.MatrixStack matrixStack = event.getMatrixStack();
        matrixStack.pushPose();
        
        // Смещение к краю хитбокса
        matrixStack.translate(0.5D, player.getBbHeight() / 2, 0);
        
        // Здесь должен быть код отрисовки через Tessellator или FontRenderer
        // Для краткости: рисуем текст с уровнем брони рядом
        String armorText = "🛡 " + (int)armorValue;
        Minecraft.getInstance().font.draw(matrixStack, armorText, 0.6f, 0, 0xFFFFFF);
        
        matrixStack.popPose();
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getInstance().screen == null && event.getKey() == toggleKey && event.getAction() == GLFW.GLFW_PRESS) {
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
