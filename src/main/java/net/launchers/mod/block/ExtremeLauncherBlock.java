package net.launchers.mod.block;

import net.launchers.mod.block.abstraction.AbstractLauncherBlock;
import net.launchers.mod.entity.ExtremeLauncherBlockEntity;
import net.launchers.mod.initializer.LSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ExtremeLauncherBlock extends AbstractLauncherBlock {
    public static final String ID = "extreme_launcher_block";

    public ExtremeLauncherBlock() {
        super();
        baseMultiplier = 2.95F;
        stackPowerPercentage = 0.275F;
        stackMultiplier = baseMultiplier * stackPowerPercentage;
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExtremeLauncherBlockEntity(pos, state);
    }

    @Override
    protected void playLaunchSound(Level world, BlockPos pos) {
        world.playSound(null, pos, LSounds.LAUNCHER_SOUND_EVENT.get(), SoundSource.BLOCKS, 1.15F, 0.775F);
    }

}
