package net.timelesswastaken.justputtyexplosive.blockentity;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.timelesswastaken.justputtyexplosive.init.JustPuttyExplosiveModBlockEntities;

public class ExplosivePuttyBlockEntity extends BlockEntity {
	private static final String TAG_DETONATOR_ID = "DetonatorId";
	private static final String TAG_PRIMED = "Primed";

	private UUID detonatorId;
	private boolean primed;

	public ExplosivePuttyBlockEntity(BlockPos pos, BlockState state) {
		super(JustPuttyExplosiveModBlockEntities.EXPLOSIVE_PUTTY_RIGGED.get(), pos, state);
	}

	public UUID getDetonatorId() {
		return this.detonatorId;
	}

	public void setDetonatorId(UUID detonatorId) {
		this.detonatorId = detonatorId;
		this.setChanged();
	}

	public void clearDetonatorId() {
		this.setDetonatorId(null);
	}

	public boolean isPrimed() {
		return this.primed;
	}

	public void setPrimed(boolean primed) {
		this.primed = primed;
		this.setChanged();
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (this.detonatorId != null) {
			tag.putUUID(TAG_DETONATOR_ID, this.detonatorId);
		}
		tag.putBoolean(TAG_PRIMED, this.primed);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.detonatorId = tag.hasUUID(TAG_DETONATOR_ID) ? tag.getUUID(TAG_DETONATOR_ID) : null;
		this.primed = tag.getBoolean(TAG_PRIMED);
	}
}
