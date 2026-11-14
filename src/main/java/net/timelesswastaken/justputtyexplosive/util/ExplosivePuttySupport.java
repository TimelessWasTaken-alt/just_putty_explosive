package net.timelesswastaken.justputtyexplosive.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.timelesswastaken.justputtyexplosive.block.ExplosivePuttyBlock;
import net.timelesswastaken.justputtyexplosive.block.ExplosivePuttyRiggedBlock;
import net.timelesswastaken.justputtyexplosive.block.HighExplosivePuttyBlock;
import net.timelesswastaken.justputtyexplosive.block.HighExplosivePuttyRiggedBlock;
import net.timelesswastaken.justputtyexplosive.block.IncendiaryExplosivePuttyBlock;
import net.timelesswastaken.justputtyexplosive.block.IncendiaryExplosivePuttyRiggedBlock;

public final class ExplosivePuttySupport {
	private static final double TOUCH_EPSILON = 0.001;

	private ExplosivePuttySupport() {
	}

	public static Direction getSupportDirection(BlockState state) {
		return switch (state.getValue(FaceAttachedHorizontalDirectionalBlock.FACE)) {
			case FLOOR -> Direction.DOWN;
			case CEILING -> Direction.UP;
			case WALL -> state.getValue(HorizontalDirectionalBlock.FACING).getOpposite();
		};
	}

	public static boolean isSupportUpdate(BlockState state, Direction facing) {
		return facing == getSupportDirection(state);
	}

	public static boolean hasSupport(LevelReader level, BlockPos pos, BlockState state) {
		Direction supportDirection = getSupportDirection(state);
		BlockPos supportPos = pos.relative(supportDirection);
		if (!level.hasChunkAt(supportPos)) {
			return true;
		}
		return touchesSupport(level, pos, state, supportPos, supportDirection);
	}

	public static boolean isHiddenInPot(BlockGetter level, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		boolean putty = block instanceof ExplosivePuttyBlock || block instanceof ExplosivePuttyRiggedBlock
				|| block instanceof IncendiaryExplosivePuttyBlock || block instanceof IncendiaryExplosivePuttyRiggedBlock
				|| block instanceof HighExplosivePuttyBlock || block instanceof HighExplosivePuttyRiggedBlock;
		if (!putty) {
			return false;
		}
		return state.getValue(FaceAttachedHorizontalDirectionalBlock.FACE) == AttachFace.FLOOR
				&& level.getBlockState(pos.below()).is(Blocks.DECORATED_POT);
	}

	public static BlockState getStateForPlacement(Block block, BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		boolean waterlogged = level.getFluidState(pos).getType() == Fluids.WATER;

		List<Direction> supportDirections = new ArrayList<>();
		supportDirections.add(context.getClickedFace().getOpposite());
		for (Direction direction : new Direction[] { Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP }) {
			if (!supportDirections.contains(direction)) {
				supportDirections.add(direction);
			}
		}

		for (Direction supportDirection : supportDirections) {
			BlockPos supportPos = pos.relative(supportDirection);
			BlockState supportState = level.getBlockState(supportPos);
			VoxelShape supportShape = supportState.getCollisionShape(level, supportPos, CollisionContext.empty());
			if (supportShape.isEmpty()) {
				continue;
			}
			supportShape = supportShape.move(supportDirection.getStepX(), supportDirection.getStepY(), supportDirection.getStepZ());
			for (BlockState candidate : candidates(block, supportDirection, context)) {
				VoxelShape puttyShape = candidate.getShape(level, pos, CollisionContext.empty());
				if (shapesTouch(puttyShape, supportShape)) {
					return candidate.setValue(BlockStateProperties.WATERLOGGED, waterlogged);
				}
			}
		}
		return null;
	}

	private static List<BlockState> candidates(Block block, Direction supportDirection, BlockPlaceContext context) {
		BlockState base = block.defaultBlockState();
		Direction horizontal = context.getHorizontalDirection();
		return switch (supportDirection) {
			case DOWN -> List.of(base.setValue(FaceAttachedHorizontalDirectionalBlock.FACE, AttachFace.FLOOR).setValue(HorizontalDirectionalBlock.FACING, horizontal));
			case UP -> List.of(base.setValue(FaceAttachedHorizontalDirectionalBlock.FACE, AttachFace.CEILING).setValue(HorizontalDirectionalBlock.FACING, horizontal));
			default -> List.of(base.setValue(FaceAttachedHorizontalDirectionalBlock.FACE, AttachFace.WALL).setValue(HorizontalDirectionalBlock.FACING, supportDirection.getOpposite()));
		};
	}

	private static boolean touchesSupport(LevelReader level, BlockPos pos, BlockState state, BlockPos supportPos, Direction supportDirection) {
		BlockState supportState = level.getBlockState(supportPos);
		VoxelShape supportShape = supportState.getCollisionShape(level, supportPos, CollisionContext.empty());
		if (supportShape.isEmpty()) {
			return false;
		}
		supportShape = supportShape.move(supportDirection.getStepX(), supportDirection.getStepY(), supportDirection.getStepZ());
		VoxelShape puttyShape = state.getShape(level, pos, CollisionContext.empty());
		return shapesTouch(puttyShape, supportShape);
	}

	private static boolean shapesTouch(VoxelShape puttyShape, VoxelShape supportShape) {
		for (AABB puttyBox : puttyShape.toAabbs()) {
			for (AABB supportBox : supportShape.toAabbs()) {
				if (puttyBox.inflate(TOUCH_EPSILON).intersects(supportBox)) {
					return true;
				}
			}
		}
		return false;
	}
}
