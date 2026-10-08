package com.ii.technology.cpu;

import com.ii.technology.IITechnology;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Network payloads for the 2i CPU storage editor. */
public final class IITechnologyCpuPayloads {
    private static final double MAX_EDIT_DISTANCE_SQR = 8.0 * 8.0;

    private IITechnologyCpuPayloads() {
    }

    public static void registerServerbound(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(
                UpdateCpuPayload.TYPE,
                UpdateCpuPayload.STREAM_CODEC,
                IITechnologyCpuPayloads::handleUpdate);
    }

    private static void handleUpdate(UpdateCpuPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            if (player.distanceToSqr(Vec3.atCenterOf(payload.pos())) > MAX_EDIT_DISTANCE_SQR) return;
            if (!(player.level().getBlockEntity(payload.pos()) instanceof TwoICpuBlockEntity cpu)) return;

            cpu.setStorageBytes(payload.storageBytes());
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("2i CPU 存储容量已更新"),
                    true);
        });
    }

    public record OpenCpuEditorPayload(BlockPos pos, long storageBytes)
            implements CustomPacketPayload {
        public static final Type<OpenCpuEditorPayload> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(IITechnology.ID, "open_cpu_editor"));
        public static final StreamCodec<ByteBuf, OpenCpuEditorPayload> STREAM_CODEC =
                StreamCodec.composite(
                        BlockPos.STREAM_CODEC, OpenCpuEditorPayload::pos,
                        ByteBufCodecs.VAR_LONG, OpenCpuEditorPayload::storageBytes,
                        OpenCpuEditorPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record UpdateCpuPayload(BlockPos pos, long storageBytes)
            implements CustomPacketPayload {
        public static final Type<UpdateCpuPayload> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(IITechnology.ID, "update_cpu"));
        public static final StreamCodec<ByteBuf, UpdateCpuPayload> STREAM_CODEC =
                StreamCodec.composite(
                        BlockPos.STREAM_CODEC, UpdateCpuPayload::pos,
                        ByteBufCodecs.VAR_LONG, UpdateCpuPayload::storageBytes,
                        UpdateCpuPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
