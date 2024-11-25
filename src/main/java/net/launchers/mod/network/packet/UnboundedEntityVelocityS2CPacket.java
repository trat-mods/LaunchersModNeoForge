package net.launchers.mod.network.packet;

import io.netty.buffer.ByteBuf;
import net.launchers.mod.loader.LLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Vector3f;

public record UnboundedEntityVelocityS2CPacket(Vector3f velocity, int entityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UnboundedEntityVelocityS2CPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(LLoader.MOD_ID, "unbounded_entity_velocity_packet"));

    public static final StreamCodec<ByteBuf, UnboundedEntityVelocityS2CPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VECTOR3F,
                                                                                                                    UnboundedEntityVelocityS2CPacket::velocity,
                                                                                                                    ByteBufCodecs.VAR_INT,
                                                                                                                    UnboundedEntityVelocityS2CPacket::entityId,
                                                                                                                    UnboundedEntityVelocityS2CPacket::new);

    public static void handleDataOnMain(final UnboundedEntityVelocityS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            assert player != null;
            LLoader.LOGGER.info("Entity: " + data.entityId + ", player: " + player.getId());
            Entity targetEntity = player.level().getEntity(data.entityId);
            assert targetEntity != null;
            targetEntity.setDeltaMovement(new Vec3(data.velocity));
        });


    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

