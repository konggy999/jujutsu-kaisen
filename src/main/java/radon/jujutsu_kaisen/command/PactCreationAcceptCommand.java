package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.command.argument.PactArgument;
import radon.jujutsu_kaisen.data.contract.IContractData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncContractDataS2CPacket;
import radon.jujutsu_kaisen.pact.Pact;

public class PactCreationAcceptCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("jjkpactcreationaccept")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("pact", PactArgument.pact())
                                .executes(ctx -> accept(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), PactArgument.getPact(ctx, "pact"))))));

        dispatcher.register(Commands.literal("jjkpactcreationaccept").redirect(node));
    }

    public static int accept(CommandSourceStack stack, ServerPlayer dst, Pact pact) {
        ServerPlayer src = stack.getPlayer();

        if (src == null) return 0;

        IJujutsuCapability srcCap = src.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (srcCap == null) return 0;

        IContractData srcData = srcCap.getContractData();

        IJujutsuCapability dstCap = dst.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (dstCap == null) return 0;

        IContractData dstData = dstCap.getContractData();

        if (srcData == null || dstData == null) return 0;

        if (dstData.hasRequestedPactCreation(src.getUUID(), pact)) {
            dstData.createPact(src.getUUID(), pact);
            srcData.createPact(dst.getUUID(), pact);

            dstData.removePactCreationRequest(src.getUUID(), pact);

            PacketHandler.sendToClient(new SyncContractDataS2CPacket(dstData.serializeNBT()), dst);
            PacketHandler.sendToClient(new SyncContractDataS2CPacket(srcData.serializeNBT()), src);

            src.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_creation_accept", JujutsuKaisen.MOD_ID), pact.getName().getString().toLowerCase(), dst.getName()));
            dst.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_creation_accept", JujutsuKaisen.MOD_ID), pact.getName().getString().toLowerCase(), src.getName()));
        } else {
            src.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_failure_create", JujutsuKaisen.MOD_ID), dst.getName(), pact.getName().getString().toLowerCase()));
            return 0;
        }
        return 1;
    }
}
