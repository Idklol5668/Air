package net.minecraft.world.gen.feature;

import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenVines extends WorldGenerator {
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        for (; position.getY() < 128; position = position.up()) {
            if (worldIn.isAirBlock(position)) {
                for (Direction enumfacing : Direction.Plane.HORIZONTAL.facings()) {
                    if (Blocks.vine.canPlaceBlockOnSide(worldIn, position, enumfacing)) {
                        IBlockState iblockstate = Blocks.vine.getDefaultState().withProperty(BlockVine.NORTH, Boolean.valueOf(enumfacing == Direction.NORTH)).withProperty(BlockVine.EAST, Boolean.valueOf(enumfacing == Direction.EAST)).withProperty(BlockVine.SOUTH, Boolean.valueOf(enumfacing == Direction.SOUTH)).withProperty(BlockVine.WEST, Boolean.valueOf(enumfacing == Direction.WEST));
                        worldIn.setBlockState(position, iblockstate, 2);
                        break;
                    }
                }
            } else {
                position = position.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));
            }
        }

        return true;
    }
}
