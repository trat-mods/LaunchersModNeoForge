package net.launchers.mod.block;

import net.launchers.mod.block.abstraction.AbstractLauncherBlock;
import net.launchers.mod.entity.PoweredLauncherBlockEntity;
import net.launchers.mod.initializer.LSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PoweredLauncherBlock extends AbstractLauncherBlock
{
    public static final String ID = "powered_launcher_block";
    public PoweredLauncherBlock()
    {
        super();
        baseMultiplier = 2.125F;
        stackPowerPercentage = 0.2975F;
        stackMultiplier = baseMultiplier * stackPowerPercentage;
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PoweredLauncherBlockEntity(pos,state);
    }

    @Override
    protected void playLaunchSound(Level world, BlockPos pos)
    {
        world.playSound(null,pos, LSounds.LAUNCHER_SOUND_EVENT.get(), SoundSource.BLOCKS, 0.9F, 0.875F);
    }
}
