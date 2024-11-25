package net.launchers.mod.block;

import net.launchers.mod.block.abstraction.AbstractLauncherBlock;
import net.launchers.mod.entity.ExtremeLauncherBlockEntity;
import net.launchers.mod.entity.LauncherBlockEntity;
import net.launchers.mod.entity.PoweredLauncherBlockEntity;
import net.launchers.mod.entity.abstraction.AbstractLauncherBlockEntity;
import net.launchers.mod.initializer.LEntities;
import net.launchers.mod.initializer.LSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LauncherBlock extends AbstractLauncherBlock
{
    public static final String ID = "launcher_block";
    public LauncherBlock()
    {
        super();
        baseMultiplier = 1.25F;
        stackPowerPercentage = 0.335F;
        stackMultiplier = baseMultiplier * stackPowerPercentage;
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LauncherBlockEntity(pos,state);
    }

    @Override
    protected void playLaunchSound(Level world, BlockPos pos) {
        world.playSound(null,pos, LSounds.LAUNCHER_SOUND_EVENT.get(), SoundSource.BLOCKS, 0.75F, 1F);
    }

}
