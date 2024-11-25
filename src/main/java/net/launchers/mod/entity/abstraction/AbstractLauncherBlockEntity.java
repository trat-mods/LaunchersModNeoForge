package net.launchers.mod.entity.abstraction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AbstractLauncherBlockEntity extends BlockEntity {
    //    private final VoxelShape RETRACTED_BASE_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    //    private final VoxelShape EXTENDED_BASE_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    //    private final VoxelShape SHORT_EXTENDER_SHAPE = Block.box(6.0D, 2.0D, 6.0D, 10.0D, 10.0D, 10.0D);
    //    private final VoxelShape LONG_EXTENDER_SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    //    private final VoxelShape HEAD_SHAPE = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public LauncherState[] states;
    public LauncherState launcherState;
    protected int currentTick = 0;
    private float progress;
    private float lastProgress;
    private boolean extending = true; // true if its extending, false if retracting

    public AbstractLauncherBlockEntity(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        states = LauncherState.values();
        launcherState = LauncherState.RETRACTED;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        ((AbstractLauncherBlockEntity) t).tickExecute();
    }

    public void tickExecute() {
        int retractingDelay = 2;
        // 1/stride ticks per move
        float extensionStride = 1F;
        switch (launcherState) {
            case EXTENDED:
                currentTick++;
                if (currentTick >= retractingDelay) {
                    currentTick = 0;
                    startRetracting();
                }
                break;
            case RETRACTED:
                break;
            case MOVING:
                this.lastProgress = this.progress;
                if (extending) {
                    if (this.lastProgress >= 1.0F) {
                        launcherState = LauncherState.EXTENDED;
                        this.lastProgress = 1F;
                    }
                    else {
                        this.progress += extensionStride;
                        if (this.progress >= 1.0F) {
                            this.progress = 1.0F;
                        }
                    }
                }
                else {
                    if (this.lastProgress <= 0F) {
                        launcherState = LauncherState.RETRACTED;
                        lastProgress = 0F;
                    }
                    else {
                        float retractingStride = extensionStride / 4;
                        this.progress -= retractingStride;
                        if (this.progress <= 0F) {
                            this.progress = 0F;
                        }
                    }
                }
                break;
        }
        assert level != null;
        if (!level.isClientSide && lastProgress != progress) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
        }
        //LLoader.LOGGER.info("state: "+launcherState+", progress: "+progress);
    }

    public void startExtending() {
        extending = true;
        launcherState = LauncherState.MOVING;
        progress = 0;
    }

    public void startRetracting() {
        extending = false;
        launcherState = LauncherState.MOVING;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag nbt = super.getUpdateTag(provider);
        nbt.putInt("currentTick", currentTick);
        nbt.putFloat("progress", progress);
        nbt.putBoolean("extending", extending);
        nbt.putInt("launcherState", launcherState.ordinal());
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookup) {
        CompoundTag tag = pkt.getTag();
        assert tag != null;
        handleUpdateTag(tag, lookup);
    }


    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider holders) {
        currentTick = tag.getInt("currentTick");
        progress = tag.getFloat("progress");
        extending = tag.getBoolean("extending");
        launcherState = states[tag.getInt("launcherState")];
        super.handleUpdateTag(tag, holders);
    }


    public float getDeltaProgress(float tickDelta) {
        if (tickDelta > 1.0F) {
            tickDelta = 1.0F;
        }

        return Mth.lerp(tickDelta, this.lastProgress, this.progress);
    }


    public enum LauncherState {EXTENDED, RETRACTED, MOVING}
}
