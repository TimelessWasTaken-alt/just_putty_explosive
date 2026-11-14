package net.timelesswastaken.justputtyexplosive.item;

import net.timelesswastaken.justputtyexplosive.procedures.BlastingCapRightclickedOnBlockProcedure;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;

public class BlastingCapItem extends Item {
	public BlastingCapItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		BlastingCapRightclickedOnBlockProcedure.execute(context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(), context.getPlayer());
		return InteractionResult.SUCCESS;
	}
}