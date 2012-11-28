package fr.tpt.s3.microSmartGridSimulation.temperature.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.tpt.s3.microSmartGridSimulation.temperature.ThermicObject;
import fr.tpt.s3.microSmartGridSimulation.temperature.impl.ThermicObjectImpl;
import fr.tpt.s3.microSmartGridSimulation.temperature.impl.WallImpl;

public class ThermicObjectTest {
	
	@Test
	public void testUpdate() throws InterruptedException {
				
		ThermicObjectImpl room1 = new ThermicObjectImpl();
		room1.heatCapacity = 1;
		room1.temperature = 10;
		room1.period = 1;
		
		ThermicObjectImpl room2 = new ThermicObjectImpl();
		room2.heatCapacity = 1;
		room2.temperature = 20;
		room1.period = 1;		
		
		List<ThermicObject> rooms = new ArrayList<ThermicObject>();
		
		WallImpl wall = new WallImpl();
		
		rooms.add(room1);
		rooms.add(room2);
		wall.thermicNeighbours = rooms;
		wall.size = 1;
		wall.openHeatConductance = 1;
		wall.surfacicHeatConductance = 0.1f;
		wall.isOpen = false;
		
		room1.bindWall(wall);
		room2.bindWall(wall);
		
		while (Math.abs(room2.temperature - room1.temperature) > 0.5) {
			room1.update();
			room2.update();
			System.out.println("# room1 : " + room1.temperature + "   room2 : " + room2.temperature);
			Thread.sleep(1);
		}
		
	}

}
