package com.sinosoft.one.bpm.test.data;

import java.util.List;

import com.sinosoft.one.bpm.test.domain.Combo;

public interface DataStore {

	public  void store(Combo c) throws Exception;

	public List<Combo> getCombos();
	
	public Combo getCombo(String comboCode);
}
