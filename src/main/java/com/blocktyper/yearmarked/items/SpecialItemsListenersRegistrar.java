package com.blocktyper.yearmarked.items;

import com.blocktyper.v1_2_1.BlockTyperUtility;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.items.listeners.LlamaSpitListener;

public class SpecialItemsListenersRegistrar extends BlockTyperUtility {

	YearmarkedPlugin yearmarkedPlugin;

	public SpecialItemsListenersRegistrar(YearmarkedPlugin yearmarkedPlugin) {
		super();
		init(yearmarkedPlugin);
		this.yearmarkedPlugin = yearmarkedPlugin;
	}

	public void registerSpecialDays() {
		new LlamaSpitListener(yearmarkedPlugin);
	}
}
