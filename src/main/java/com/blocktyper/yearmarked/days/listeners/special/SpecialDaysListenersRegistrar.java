package com.blocktyper.yearmarked.days.listeners.special;

import com.blocktyper.v1_2_6.BlockTyperUtility;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.listeners.special.marketday.MarketDayListener;

public class SpecialDaysListenersRegistrar extends BlockTyperUtility{
	
	YearmarkedPlugin yearmarkedPlugin;
	
	public SpecialDaysListenersRegistrar(YearmarkedPlugin yearmarkedPlugin) {
		super();
		init(yearmarkedPlugin);
		this.yearmarkedPlugin = yearmarkedPlugin;
	}

	public void registerSpecialDays(){
		new MarketDayListener(yearmarkedPlugin);
	}
}
