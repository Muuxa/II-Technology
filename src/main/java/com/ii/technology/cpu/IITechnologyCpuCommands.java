package com.ii.technology.cpu;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** Storage-only command fallback for the 2i CPU pool. */
public final class IITechnologyCpuCommands {
    private static final SimpleCommandExceptionType NOT_CPU =
            new SimpleCommandExceptionType(Component.literal("目标不是 2i CPU"));
    private static final SimpleCommandExceptionType TOO_FAR =
            new SimpleCommandExceptionType(Component.literal("距离目标方块太远"));
    private static final double MAX_EDIT_DISTANCE_SQR = 8.0 * 8.0;

    private IITechnologyCpuCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("iitechnology")
                .then(Commands.literal("cpu")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.literal("storage")
                                        .then(Commands.argument("bytes", LongArgumentType.longArg(
                                                        TwoICpuBlockEntity.MIN_STORAGE_BYTES,
                                                        TwoICpuBlockEntity.MAX_STORAGE_BYTES))
                                                .executes(IITechnologyCpuCommands::setStorage))))));
    }

    private static int setStorage(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        BlockPos pos = BlockPosArgument.getBlockPos(context, "pos");
        if (player.distanceToSqr(Vec3.atCenterOf(pos)) > MAX_EDIT_DISTANCE_SQR) {
            throw TOO_FAR.create();
        }
        if (!(context.getSource().getLevel().getBlockEntity(pos) instanceof TwoICpuBlockEntity cpu)) {
            throw NOT_CPU.create();
        }

        long bytes = LongArgumentType.getLong(context, "bytes");
        cpu.setStorageBytes(bytes);
        context.getSource().sendSuccess(() -> Component.literal("存储容量已设为 " + cpu.getConfiguredStorageBytes() + " B"), true);
        return (int) Math.min(Integer.MAX_VALUE, cpu.getConfiguredStorageBytes());
    }
}
