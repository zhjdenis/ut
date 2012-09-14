package com.amazon.SSOF.ut;

import java.io.File;
import java.util.List;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;

public class SSOFDaoCSVDataSet extends CachedDataSet {

	public SSOFDaoCSVDataSet(List<File> dataFile) throws DataSetException {
		super(new SSOFDaoCSVProducer(dataFile));
	}
}
