package net.launchers.mod.entity_renderer.abstraction;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.launchers.mod.block.abstraction.AbstractLauncherBlock;
import net.launchers.mod.entity.abstraction.AbstractLauncherBlockEntity;
import net.launchers.mod.utils.MathUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LauncherBlockEntityRenderer<T extends AbstractLauncherBlockEntity> implements BlockEntityRenderer<T> {
    private final BlockEntityRendererProvider.Context context;

    public LauncherBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }


    @Override
    public void render(T blockEntity, float tickDelta, PoseStack matrices, @NotNull MultiBufferSource renderTypeBuffer, int light, int overlay) {
        BlockState entityState = blockEntity.getBlockState();
        matrices.pushPose();
        BlockRenderDispatcher dispatcher = context.getBlockRenderDispatcher();
        float extension = blockEntity.getDeltaProgress(tickDelta);
        //LLoader.LOGGER.info(extension);
        BakedModel model;
        if (extension < 0.35F) {
            model = dispatcher.getBlockModel(
                    entityState.setValue(AbstractLauncherBlock.MODELS, 2).setValue(AbstractLauncherBlock.FACING, entityState.getValue(AbstractLauncherBlock.FACING)));
        }
        else {
            model = dispatcher.getBlockModel(
                    entityState.setValue(AbstractLauncherBlock.MODELS, 1).setValue(AbstractLauncherBlock.FACING, entityState.getValue(AbstractLauncherBlock.FACING)));
        }
        Vec3 translation = MathUtils.fromDirection(entityState.getValue(AbstractLauncherBlock.FACING));
        matrices.translate(translation.x * extension, translation.y * extension, translation.z * extension);

        VertexConsumer vertexConsumer = renderTypeBuffer.getBuffer(RenderType.solid());
        dispatcher
                .getModelRenderer()
                .tesselateBlock(Objects.requireNonNull(blockEntity.getLevel()), model, entityState, blockEntity.getBlockPos(), matrices, vertexConsumer, true,
                                RandomSource.create(), 4, overlay, ModelData.EMPTY, RenderType.solid());

        matrices.popPose();
    }

}
