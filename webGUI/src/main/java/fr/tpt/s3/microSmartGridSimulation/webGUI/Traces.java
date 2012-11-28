package fr.tpt.s3.microSmartGridSimulation.webGUI;

public interface Traces {
	
	String[] listTraces();
	String[][] data(String traceName);

}
