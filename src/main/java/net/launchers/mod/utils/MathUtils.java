package net.launchers.mod.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class MathUtils
{
    public static Vec3 fromDirection(Direction direction)
    {
        return switch (direction) {
            case UP -> new Vec3(0, 1, 0);
            case DOWN -> new Vec3(0, -1, 0);
            case EAST -> new Vec3(1, 0, 0);
            case WEST -> new Vec3(-1, 0, 0);
            case NORTH -> new Vec3(0, 0, -1);
            case SOUTH -> new Vec3(0, 0, 1);
        };
    }

    public static AABB getExpansionBlock(BlockPos pos, Direction facing)
    {
        AABB res = new AABB(pos);
        switch(facing)
        {
            case UP:
                res.expandTowards(0.2D, 1.2D, 0.2D);
            case DOWN:
                res.expandTowards(0.2D, 1.2D, 0.2D);
            case EAST:
                res.expandTowards(1.2D, 0.2D, 0.2D);
            case WEST:
                res.expandTowards(1.2D, 0.2D, 0.2D);
            case NORTH:
                res.expandTowards(0.2D, 0.2D, 1.2D);
            case SOUTH:
                res.expandTowards(0.2D, 0.2D, 1.2D);
            default:
                res.expandTowards(0, 0, 0);
        }
        return res;
    }
}

