package net.launchers.mod.block.abstraction;

import com.mojang.serialization.MapCodec;
import net.launchers.mod.loader.LLoader;
import net.launchers.mod.entity.abstraction.AbstractLauncherBlockEntity;
import net.launchers.mod.initializer.LEntities;
import net.launchers.mod.network.packet.UnboundedEntityVelocityS2CPacket;
import net.launchers.mod.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.ScheduledTick;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

public abstract class AbstractLauncherBlock extends BaseEntityBlock {
    public static final ResourceLocation LAUNCH_SOUND = ResourceLocation.fromNamespaceAndPath(LLoader.MOD_ID, "launcher_block_launch");
    public static final IntegerProperty MODELS = IntegerProperty.create("models", 0, 2);
    public static final EnumProperty<Direction> FACING;

    static {
        FACING = DirectionalBlock.FACING;
    }

    public float stackMultiplier;
    public float baseMultiplier;
    protected float stackPowerPercentage;
    private float launchForce = 1F;
    private int maxStackable = 4;

    public AbstractLauncherBlock() {
        super(BlockBehaviour.Properties.of().strength(0.7F, 0.6F).sound(SoundType.METAL).dynamicShape().noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(MODELS, 0));
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return Block.box(0F, 0F, 0F, 16F, 16F, 16F);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
        //return super.getRenderShape(p_49232_);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public void fallOn(Level level, @NotNull BlockState p_152427_, @NotNull BlockPos p_152428_, Entity entity, float distance) {
        entity.causeFallDamage(distance, 0.0F, level.damageSources().fall());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODELS);
        super.createBlockStateDefinition(builder);
    }


    public void launchEntities(Level world, BlockPos pos, List<? extends Entity> entities) {
        if (!world.isClientSide) {
            if (entities.size() < 1) {
                return;
            }
            float force = launchForce * baseMultiplier;

            BlockState parentState = world.getBlockState(pos);
            Direction stackDirection = parentState.getValue(FACING).getOpposite();
            BlockPos currentPos = pos.relative(stackDirection);
            int currentIndex = 1;
            double multiplier = 1F;
            if (!stackDirection.equals(Direction.UP) && !stackDirection.equals(Direction.DOWN)) {
                multiplier *= 1.75F;
            }
            Block current;
            while (currentIndex < maxStackable && ((current = world.getBlockState(currentPos).getBlock()) instanceof AbstractLauncherBlock && world
                    .getBlockState(currentPos)
                    .getValue(FACING)
                    .equals(parentState.getValue(FACING)))) {
                AbstractLauncherBlock launcherBlock = (AbstractLauncherBlock) current;
                multiplier += launcherBlock.stackMultiplier;
                currentPos = currentPos.relative(stackDirection);
                currentIndex++;
            }
            force *= multiplier;
            for (Entity entity : entities) {
                Vec3 initialVelocity = entity.getDeltaMovement();
                Vec3 vectorForce = MathUtils.fromDirection(world.getBlockState(pos).getValue(AbstractLauncherBlock.FACING));
                Vec3 velocity = vectorForce.scale(force).add(initialVelocity);
                entity.setDeltaMovement(velocity);
                UnboundedEntityVelocityS2CPacket packet = new UnboundedEntityVelocityS2CPacket(new Vector3f((float) velocity.x, (float) velocity.y, (float) velocity.z),
                                                                                               entity.getId());
                PacketDistributor.sendToServer(packet);
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        AbstractLauncherBlockEntity launcherBlockEntity = (AbstractLauncherBlockEntity) world.getBlockEntity(pos);
        boolean isRecevingRedstonePower = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
        assert launcherBlockEntity != null;
        boolean isRetracted = launcherBlockEntity.launcherState == AbstractLauncherBlockEntity.LauncherState.RETRACTED;
        if (!isRetracted) return;
        if (isRecevingRedstonePower) {
            world.getBlockTicks().schedule(ScheduledTick.probe(this, pos));
        }
    }


    protected abstract void playLaunchSound(Level world, BlockPos pos);

    public boolean canLaunch(Level world, BlockPos pos) {
        AbstractLauncherBlockEntity launcherBlockEntity = (AbstractLauncherBlockEntity) world.getBlockEntity(pos);
        BlockPos offset = pos.relative(world.getBlockState(pos).getValue(FACING));
        return (world.getBlockState(offset).isAir() || world
                .getBlockState(offset)
                .getBlock()
                .equals(Blocks.TRIPWIRE)) && launcherBlockEntity.launcherState == AbstractLauncherBlockEntity.LauncherState.RETRACTED;
    }

    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        LLoader.LOGGER.debug("launching");
        if (canLaunch(world, pos)) {
            List<Entity> entities = world.getEntitiesOfClass(Entity.class, new AABB(pos.relative(state.getValue(FACING))));
            launchEntities(world, pos, entities);
            playLaunchSound(world, pos);
            ((AbstractLauncherBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).startExtending();
        }
    }

    public int getTickRate(Level worldView) {
        return 1;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> type) {
        if (type == LEntities.LAUNCHER_BLOCK_TILE_ENTITY.get()) {
            return AbstractLauncherBlockEntity::tick;
        }
        if (type == LEntities.POWERED_LAUNCHER_BLOCK_TILE_ENTITY.get()) {
            return AbstractLauncherBlockEntity::tick;
        }
        if (type == LEntities.EXTREME_LAUNCHER_BLOCK_TILE_ENTITY.get()) {
            return AbstractLauncherBlockEntity::tick;
        }
        return null;
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState) this.getStateDefinition().any().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation direction) {
        return (BlockState) state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }


    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }
}
