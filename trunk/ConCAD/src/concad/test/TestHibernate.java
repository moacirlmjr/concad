package concad.test;

import concad.core.smells.detectors.configuration.BrainClassDetectionConfiguration;
import concad.db.DataBaseManager;

public class TestHibernate {
	
	public static void main(String[] args) {
	
		
		BrainClassDetectionConfiguration brainClassConf = new BrainClassDetectionConfiguration();
		brainClassConf.setId((long) 10);
		brainClassConf.setLOC_GreaterEqual_2xVeryHigh(10.0);
		brainClassConf.setProjectName("teste");
		DataBaseManager.getInstance();
		
		

	}
	
	

}
