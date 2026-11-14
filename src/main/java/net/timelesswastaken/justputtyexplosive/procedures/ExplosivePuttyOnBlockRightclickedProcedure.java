package net.timelesswastaken.justputtyexplosive.procedures;

import net.timelesswastaken.justputtyexplosive.init.JustPuttyExplosiveModItems;
import net.timelesswastaken.justputtyexplosive.init.JustPuttyExplosiveModBlocks;

import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.timelesswastaken.justputtyexplosive.init.JustPuttyExplosiveModSounds;

import java.util.Map;

public class ExplosivePuttyOnBlockRightclickedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == JustPuttyExplosiveModItems.BLASTING_CAP.get()) {
			{
				BlockPos _bp = BlockPos.containing(x, y, z);
				BlockState _bso = world.getBlockState(_bp);
				boolean incendiary = _bso.getBlock() == JustPuttyExplosiveModBlocks.INCENDIARY_EXPLOSIVE_PUTTY.get();
				boolean high = _bso.getBlock() == JustPuttyExplosiveModBlocks.HIGH_EXPLOSIVE_PUTTY.get();
				if (_bso.getBlock() != JustPuttyExplosiveModBlocks.EXPLOSIVE_PUTTY.get() && !incendiary && !high) {
					return;
				}
				Block riggedBlock = high
						? JustPuttyExplosiveModBlocks.HIGH_EXPLOSIVE_PUTTY_RIGGED.get()
						: incendiary ? JustPuttyExplosiveModBlocks.INCENDIARY_EXPLOSIVE_PUTTY_RIGGED.get() : JustPuttyExplosiveModBlocks.EXPLOSIVE_PUTTY_RIGGED.get();
				BlockState _bs = riggedBlock.defaultBlockState();
				for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
					Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
					if (_property != null && _bs.getValue(_property) != null)
						try {
							_bs = _bs.setValue(_property, (Comparable) entry.getValue());
						} catch (Exception e) {
						}
				}
				world.setBlock(_bp, _bs, 3);
				if (world instanceof Level level) {
					level.playSound(null, _bp.getX() + 0.5, _bp.getY() + 0.5, _bp.getZ() + 0.5,
							JustPuttyExplosiveModSounds.BLASTING_CAP_PLACE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
				}
			}
			if (entity instanceof Player _player && !_player.getAbilities().instabuild) {
				ItemStack _stktoremove = new ItemStack(JustPuttyExplosiveModItems.BLASTING_CAP.get());
				_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
			}
		}
	}
}