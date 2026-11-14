package net.timelesswastaken.justputtyexplosive.procedures;

import net.timelesswastaken.justputtyexplosive.init.JustPuttyExplosiveModItems;
import net.timelesswastaken.justputtyexplosive.init.JustPuttyExplosiveModBlocks;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

public class BlastingCapRightclickedOnBlockProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == JustPuttyExplosiveModItems.BLASTING_CAP.get()) {
			BlockState _bs = world.getBlockState(BlockPos.containing(x, y, z));
			if (_bs.getBlock() == JustPuttyExplosiveModBlocks.EXPLOSIVE_PUTTY.get() || _bs.getBlock() == JustPuttyExplosiveModBlocks.INCENDIARY_EXPLOSIVE_PUTTY.get() || _bs.getBlock() == JustPuttyExplosiveModBlocks.HIGH_EXPLOSIVE_PUTTY.get()) {
				if (entity instanceof Player _player && !_player.getAbilities().instabuild) {
					ItemStack _stktoremove = new ItemStack(JustPuttyExplosiveModItems.BLASTING_CAP.get());
					_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
				}
			}
		}
	}
}