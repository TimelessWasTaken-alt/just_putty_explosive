package net.timelesswastaken.justputtyexplosive.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.timelesswastaken.justputtyexplosive.JustPuttyExplosiveMod;
import net.timelesswastaken.justputtyexplosive.init.JustPuttyExplosiveModSounds;
import net.timelesswastaken.justputtyexplosive.blockentity.ExplosivePuttyBlockEntity;
import net.timelesswastaken.justputtyexplosive.init.JustPuttyExplosiveModBlocks;

public final class ExplosivePuttyLinkManager {
	public static final int MAX_LINKS = 5;
	public static final float EXPLOSION_POWER = 3.0F;
	public static final float HIGH_EXPLOSION_POWER = 8.0F;
	private static final int ACTIVATE_SOUND_DELAY_TICKS = 26;
	private static final String COOLDOWN_TAG = "CooldownUntil";
	private static final long DETONATION_COOLDOWN_TICKS = 100;

	private static final String TAG_DETONATOR_ID = "DetonatorId";
	private static final String TAG_LINKS = "Links";
	private static final String TAG_X = "X";
	private static final String TAG_Y = "Y";
	private static final String TAG_Z = "Z";

	private ExplosivePuttyLinkManager() {
	}

	public enum LinkResult {
		LINKED,
		UNLINKED,
		FULL,
		OTHER_DETONATOR,
		PRIMED,
		INVALID
	}

	public enum PuttyType {
		NONE,
		REGULAR,
		INCENDIARY,
		HIGH
	}

	public record PuttyExplosion(PuttyType type, boolean underwater) {
	}

	public static UUID getOrCreateDetonatorId(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.hasUUID(TAG_DETONATOR_ID)) {
			tag.putUUID(TAG_DETONATOR_ID, UUID.randomUUID());
		}
		return tag.getUUID(TAG_DETONATOR_ID);
	}

	public static boolean hasStoredLinks(ItemStack stack) {
		return getLinksTag(stack, false).size() > 0;
	}

	public static boolean isRiggedPutty(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.is(JustPuttyExplosiveModBlocks.EXPLOSIVE_PUTTY_RIGGED.get())
				|| state.is(JustPuttyExplosiveModBlocks.INCENDIARY_EXPLOSIVE_PUTTY_RIGGED.get())
				|| state.is(JustPuttyExplosiveModBlocks.HIGH_EXPLOSIVE_PUTTY_RIGGED.get());
	}

	public static boolean isIncendiaryRiggedPutty(Level level, BlockPos pos) {
		return level.getBlockState(pos).is(JustPuttyExplosiveModBlocks.INCENDIARY_EXPLOSIVE_PUTTY_RIGGED.get());
	}

	public static boolean isHighRiggedPutty(Level level, BlockPos pos) {
		return level.getBlockState(pos).is(JustPuttyExplosiveModBlocks.HIGH_EXPLOSIVE_PUTTY_RIGGED.get());
	}

	public static boolean isUnderwater(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.getFluidState().getType() == Fluids.WATER || level.isWaterAt(pos);
	}

	public static List<BlockPos> getValidLinkedPositions(Level level, ItemStack stack) {
		UUID detonatorId = getOrCreateDetonatorId(stack);
		ListTag storedLinks = getLinksTag(stack, true);
		List<BlockPos> positions = new ArrayList<>();
		ListTag validLinks = new ListTag();

		for (int i = 0; i < storedLinks.size(); i++) {
			Tag tag = storedLinks.get(i);
			if (!(tag instanceof CompoundTag compound)) {
				continue;
			}
			BlockPos pos = new BlockPos(compound.getInt(TAG_X), compound.getInt(TAG_Y), compound.getInt(TAG_Z));
			if (isValidLink(level, pos, detonatorId)) {
				positions.add(pos);
				validLinks.add(compound.copy());
			}
		}

		stack.getOrCreateTag().put(TAG_LINKS, validLinks);
		return positions;
	}

	public static int getRemainingSpaces(Level level, ItemStack stack) {
		return MAX_LINKS - getValidLinkedPositions(level, stack).size();
	}

	public static LinkResult toggleLink(Level level, BlockPos pos, ItemStack stack) {
		if (!isRiggedPutty(level, pos) || !(level.getBlockEntity(pos) instanceof ExplosivePuttyBlockEntity putty)) {
			return LinkResult.INVALID;
		}

		if (putty.isPrimed()) {
			return LinkResult.PRIMED;
		}

		UUID detonatorId = getOrCreateDetonatorId(stack);
		List<BlockPos> validPositions = getValidLinkedPositions(level, stack);
		UUID ownerId = putty.getDetonatorId();

		if (ownerId == null) {
			if (validPositions.size() >= MAX_LINKS) {
				return LinkResult.FULL;
			}
			addLinkedPosition(stack, pos);
			putty.setDetonatorId(detonatorId);
			return LinkResult.LINKED;
		}

		if (ownerId.equals(detonatorId)) {
			removeLinkedPosition(stack, pos);
			putty.clearDetonatorId();
			return LinkResult.UNLINKED;
		}

		return LinkResult.OTHER_DETONATOR;
	}

	public static boolean detonate(Level level, ItemStack stack, Player player, InteractionHand hand) {
		List<BlockPos> positions = getValidLinkedPositions(level, stack);
		if (positions.isEmpty()) {
			return false;
		}

		if (stack.getTag() != null && level.getGameTime() < stack.getTag().getLong(COOLDOWN_TAG)) {
			return false;
		}
		stack.getOrCreateTag().putLong(COOLDOWN_TAG, level.getGameTime() + DETONATION_COOLDOWN_TICKS);

		clearLinkedPositions(stack);
		Set<BlockPos> firePositions = new HashSet<>();
		Set<BlockPos> highPositions = new HashSet<>();
		for (BlockPos pos : positions) {
			if (level.getBlockEntity(pos) instanceof ExplosivePuttyBlockEntity putty) {
				putty.setPrimed(true);
			}
			if (isIncendiaryRiggedPutty(level, pos) && !isUnderwater(level, pos)) {
				firePositions.add(pos);
			}
			if (isHighRiggedPutty(level, pos)) {
				highPositions.add(pos);
			}
		}
		if (player != null) {
			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					JustPuttyExplosiveModSounds.DETONATOR_ACTIVATE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		}
		List<ChunkPos> forcedChunks = new ArrayList<>();
		if (level instanceof ServerLevel serverLevel) {
			for (BlockPos pos : positions) {
				ChunkPos chunkPos = new ChunkPos(pos);
				if (!forcedChunks.contains(chunkPos)) {
					forcedChunks.add(chunkPos);
					serverLevel.setChunkForced(chunkPos.x, chunkPos.z, true);
				}
			}
		}
		JustPuttyExplosiveMod.queueServerWork(ACTIVATE_SOUND_DELAY_TICKS, () -> {
			for (BlockPos pos : positions) {
				if (!isRiggedPutty(level, pos)) {
					continue;
				}
				level.destroyBlock(pos, false);
				float power = highPositions.contains(pos) ? HIGH_EXPLOSION_POWER : EXPLOSION_POWER;
				explodeAt(level, pos, null, firePositions.contains(pos), power);
			}
			if (level instanceof ServerLevel serverLevel) {
				for (ChunkPos chunkPos : forcedChunks) {
					serverLevel.setChunkForced(chunkPos.x, chunkPos.z, false);
				}
			}
		});

		if (player != null) {
			stack.hurtAndBreak(positions.size(), player, p -> p.broadcastBreakEvent(hand));
		}
		return true;
	}

	public static PuttyExplosion consumePutty(Level level, BlockPos pos) {
		if (level.isClientSide) {
			return new PuttyExplosion(PuttyType.NONE, false);
		}

		BlockState state = level.getBlockState(pos);
		boolean underwater = isUnderwater(level, pos);
		if (state.is(JustPuttyExplosiveModBlocks.EXPLOSIVE_PUTTY.get())) {
			level.destroyBlock(pos, false);
			return new PuttyExplosion(PuttyType.REGULAR, underwater);
		}
		if (state.is(JustPuttyExplosiveModBlocks.EXPLOSIVE_PUTTY_RIGGED.get())) {
			clearLinkAt(level, pos);
			level.destroyBlock(pos, false);
			return new PuttyExplosion(PuttyType.REGULAR, underwater);
		}
		if (state.is(JustPuttyExplosiveModBlocks.INCENDIARY_EXPLOSIVE_PUTTY.get())) {
			level.destroyBlock(pos, false);
			return new PuttyExplosion(PuttyType.INCENDIARY, underwater);
		}
		if (state.is(JustPuttyExplosiveModBlocks.INCENDIARY_EXPLOSIVE_PUTTY_RIGGED.get())) {
			clearLinkAt(level, pos);
			level.destroyBlock(pos, false);
			return new PuttyExplosion(PuttyType.INCENDIARY, underwater);
		}
		if (state.is(JustPuttyExplosiveModBlocks.HIGH_EXPLOSIVE_PUTTY.get())) {
			level.destroyBlock(pos, false);
			return new PuttyExplosion(PuttyType.HIGH, underwater);
		}
		if (state.is(JustPuttyExplosiveModBlocks.HIGH_EXPLOSIVE_PUTTY_RIGGED.get())) {
			clearLinkAt(level, pos);
			level.destroyBlock(pos, false);
			return new PuttyExplosion(PuttyType.HIGH, underwater);
		}
		return new PuttyExplosion(PuttyType.NONE, false);
	}

	public static void explodeAt(Level level, BlockPos pos, Entity source, boolean fire, float power) {
		if (level.isClientSide || !level.isLoaded(pos)) {
			return;
		}
		level.explode(source, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, power, fire, Level.ExplosionInteraction.BLOCK);
	}

	private static boolean isValidLink(Level level, BlockPos pos, UUID detonatorId) {
		if (!isRiggedPutty(level, pos) || !(level.getBlockEntity(pos) instanceof ExplosivePuttyBlockEntity putty)) {
			return false;
		}
		return detonatorId.equals(putty.getDetonatorId());
	}

	private static void clearLinkAt(Level level, BlockPos pos) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof ExplosivePuttyBlockEntity putty) {
			putty.clearDetonatorId();
		}
	}

	private static void clearLinkedPositions(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			tag.remove(TAG_LINKS);
		}
	}

	private static ListTag getLinksTag(ItemStack stack, boolean create) {
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			if (!create) {
				return new ListTag();
			}
			tag = stack.getOrCreateTag();
		}

		ListTag links = tag.getList(TAG_LINKS, Tag.TAG_COMPOUND);
		if (create && !tag.contains(TAG_LINKS)) {
			tag.put(TAG_LINKS, links);
		}
		return links;
	}

	private static void addLinkedPosition(ItemStack stack, BlockPos pos) {
		ListTag links = getLinksTag(stack, true);
		if (!containsPosition(links, pos)) {
			CompoundTag tag = new CompoundTag();
			tag.putInt(TAG_X, pos.getX());
			tag.putInt(TAG_Y, pos.getY());
			tag.putInt(TAG_Z, pos.getZ());
			links.add(tag);
		}
	}

	private static void removeLinkedPosition(ItemStack stack, BlockPos pos) {
		ListTag links = getLinksTag(stack, true);
		ListTag keptLinks = new ListTag();
		for (int i = 0; i < links.size(); i++) {
			Tag tag = links.get(i);
			if (tag instanceof CompoundTag compound && !positionEquals(compound, pos)) {
				keptLinks.add(compound.copy());
			}
		}
		stack.getOrCreateTag().put(TAG_LINKS, keptLinks);
	}

	private static boolean containsPosition(ListTag links, BlockPos pos) {
		for (int i = 0; i < links.size(); i++) {
			Tag tag = links.get(i);
			if (tag instanceof CompoundTag compound && positionEquals(compound, pos)) {
				return true;
			}
		}
		return false;
	}

	private static boolean positionEquals(CompoundTag tag, BlockPos pos) {
		return tag.getInt(TAG_X) == pos.getX() && tag.getInt(TAG_Y) == pos.getY() && tag.getInt(TAG_Z) == pos.getZ();
	}
}
