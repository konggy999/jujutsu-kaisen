package radon.jujutsu_kaisen.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.client.gui.screen.MissionsScreen;
import radon.jujutsu_kaisen.client.gui.screen.ShadowInventoryScreen;
import radon.jujutsu_kaisen.client.gui.screen.VeilRodScreen;

public class ClientWrapper {
    public static @Nullable Level getLevel() {
        return Minecraft.getInstance().level;
    }

    public static @Nullable Player getPlayer() {
        return Minecraft.getInstance().player;
    }

    public static void setOverlayMessage(Component component, boolean animate) {
        Minecraft.getInstance().gui.setOverlayMessage(component, animate);
    }

    public static void openShadowInventory() {
        Minecraft.getInstance().setScreen(new ShadowInventoryScreen());
    }

    public static void openMissions() {
        Minecraft.getInstance().setScreen(new MissionsScreen());
    }

    public static void refreshMissions() {
        if (!(Minecraft.getInstance().screen instanceof MissionsScreen missions)) return;

        missions.refresh();
    }
}
