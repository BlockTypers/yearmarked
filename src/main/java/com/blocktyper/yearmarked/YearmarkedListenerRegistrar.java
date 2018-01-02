package com.blocktyper.yearmarked;

import com.blocktyper.v1_2_5.BlockTyperUtility;
import com.blocktyper.yearmarked.days.listeners.DataMigrationListener_2_0_0;
import com.blocktyper.yearmarked.days.listeners.SendDayInfoListener;
import com.blocktyper.yearmarked.days.listeners.diamonday.DiamondayListener;
import com.blocktyper.yearmarked.days.listeners.donnerstag.DonnerstagListener;
import com.blocktyper.yearmarked.days.listeners.earthday.EarthdayListener;
import com.blocktyper.yearmarked.days.listeners.feathersday.FeathersdayListener;
import com.blocktyper.yearmarked.days.listeners.fishfryday.FishfrydayListener;
import com.blocktyper.yearmarked.days.listeners.monsoonday.MonsoondayListener;
import com.blocktyper.yearmarked.days.listeners.special.SpecialDaysListenersRegistrar;
import com.blocktyper.yearmarked.days.listeners.wortag.WortagListener;
import com.blocktyper.yearmarked.items.SpecialItemsListenersRegistrar;

public class YearmarkedListenerRegistrar extends BlockTyperUtility{
	YearmarkedPlugin yearmarkedPlugin;

	public YearmarkedListenerRegistrar(YearmarkedPlugin yearmarkedPlugin) {
		super();
		this.yearmarkedPlugin = yearmarkedPlugin;
		init(yearmarkedPlugin);
	}

	public void registerListeners() {
			// days
			new MonsoondayListener(yearmarkedPlugin);
			new EarthdayListener(yearmarkedPlugin);
			new WortagListener(yearmarkedPlugin);
			new DonnerstagListener(yearmarkedPlugin);
			new FishfrydayListener(yearmarkedPlugin);
			new DiamondayListener(yearmarkedPlugin);
			new FeathersdayListener(yearmarkedPlugin);
	
			// send day info
			new SendDayInfoListener(yearmarkedPlugin);
	
			// migration
			new DataMigrationListener_2_0_0(yearmarkedPlugin);
	
			// special days
			new SpecialDaysListenersRegistrar(yearmarkedPlugin).registerSpecialDays();
			
			// special items
			new SpecialItemsListenersRegistrar(yearmarkedPlugin).registerSpecialDays();
		}
	
	

}
