package net.timelesswastaken.justputtyexplosive.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.Mob;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.timelesswastaken.justputtyexplosive.blockentity.ExplosivePuttyBlockEntity;
import net.timelesswastaken.justputtyexplosive.util.ExplosivePuttySupport;

public class IncendiaryExplosivePuttyRiggedBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final EnumProperty<AttachFace> FACE = FaceAttachedHorizontalDirectionalBlock.FACE;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public IncendiaryExplosivePuttyRiggedBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.FROGLIGHT).strength(0f, 1f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false).instrument(NoteBlockInstrument.DIDGERIDOO));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL).setValue(WATERLOGGED, false));
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return state.getFluidState().isEmpty();
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 0;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			default -> switch (state.getValue(FACE)) {
				case FLOOR -> box(6, 0, 5, 10, 2, 11);
				case WALL -> box(6, 5, 0, 10, 11, 2);
				case CEILING -> box(6, 14, 5, 10, 16, 11);
			};
			case NORTH -> switch (state.getValue(FACE)) {
				case FLOOR -> box(6, 0, 5, 10, 2, 11);
				case WALL -> box(6, 5, 14, 10, 11, 16);
				case CEILING -> box(6, 14, 5, 10, 16, 11);
			};
			case EAST -> switch (state.getValue(FACE)) {
				case FLOOR -> box(5, 0, 6, 11, 2, 10);
				case WALL -> box(0, 5, 6, 2, 11, 10);
				case CEILING -> box(5, 14, 6, 11, 16, 10);
			};
			case WEST -> switch (state.getValue(FACE)) {
				case FLOOR -> box(5, 0, 6, 11, 2, 10);
				case WALL -> box(14, 5, 6, 16, 11, 10);
				case CEILING -> box(5, 14, 6, 11, 16, 10);
			};
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, FACE, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return ExplosivePuttySupport.getStateForPlacement(this, context);
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		boolean unsupported = !world.isClientSide() && ExplosivePuttySupport.isSupportUpdate(state, facing) && !ExplosivePuttySupport.hasSupport(world, currentPos, state);
		if (unsupported) {
			return Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		return BlockPathTypes.WALKABLE;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ExplosivePuttyBlockEntity(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}