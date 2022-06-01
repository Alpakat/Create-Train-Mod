package com.simibubi.create.content.curiosities.deco;

import java.util.List;

import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SlidingDoorTileEntity extends SmartTileEntity {

	LerpedFloat animation;
	int bridgeTicks;

	public SlidingDoorTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		animation = LerpedFloat.linear()
			.startWithValue(isOpen(state) ? 1 : 0);
	}

	@Override
	public void tick() {
		super.tick();
		boolean open = isOpen(getBlockState());
		boolean wasSettled = animation.settled();
		animation.chase(open ? 1 : 0, .15f, Chaser.LINEAR);
		animation.tickChaser();

		if (level.isClientSide()) {
			if (bridgeTicks < 2 && open)
				bridgeTicks++;
			else if (bridgeTicks > 0 && !open && isVisible(getBlockState()))
				bridgeTicks--;
			return;
		}

		if (!open && !wasSettled && animation.settled() && !isVisible(getBlockState()))
			showBlockModel();
	}

	protected boolean isVisible(BlockState state) {
		return state.getOptionalValue(TrainDoorBlock.VISIBLE)
			.orElse(true);
	}

	protected boolean shouldRenderSpecial(BlockState state) {
		return !isVisible(state) || bridgeTicks != 0;
	}

	protected void showBlockModel() {
		level.setBlock(worldPosition, getBlockState().setValue(TrainDoorBlock.VISIBLE, true), 3);
		level.playSound(null, worldPosition, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, .5f, 1);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {}

	public static boolean isOpen(BlockState state) {
		return state.getOptionalValue(DoorBlock.OPEN)
			.orElse(false);
	}

}