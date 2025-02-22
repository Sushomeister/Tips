package net.darkhax.tipsmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.darkhax.tipsmod.api.TipsAPI;
import net.darkhax.tipsmod.impl.TipsModCommon;
import net.darkhax.tipsmod.impl.client.TipRenderHandler;
import net.darkhax.tipsmod.impl.gui.TipsListScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;

public class TipsModFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        TipsModCommon.init();

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {

            if (TipsAPI.canRenderOnScreen(screen)) {

                ScreenEvents.afterRender(screen).register((screen2, pose, mouseX, mouseY, delta) -> {

                    TipRenderHandler.drawTip(pose, screen2);
                });
            }
        });

        registerCommands(ClientCommandManager.DISPATCHER);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        final LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommandManager.literal("tipsmod");
        root.then(ClientCommandManager.literal("listscreen").executes(ctx -> {
            final Minecraft client = ctx.getSource().getClient();

            client.tell(() -> client.setScreen(TipsListScreen.factory(client, client.screen)));
            return 0;
        }));

        dispatcher.register(root);
    }
}