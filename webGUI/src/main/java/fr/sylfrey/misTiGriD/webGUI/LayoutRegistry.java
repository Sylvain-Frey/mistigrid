package fr.sylfrey.misTiGriD.webGUI;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import fr.sylfrey.misTiGriD.alba.basic.roles.HouseLoadManager;
import fr.sylfrey.misTiGriD.layout.AtmosphereLayout;
import fr.sylfrey.misTiGriD.layout.HeaterLayout;
import fr.sylfrey.misTiGriD.layout.HeaterManagerLayout;
import fr.sylfrey.misTiGriD.layout.LampLayout;
import fr.sylfrey.misTiGriD.layout.Layout;
import fr.sylfrey.misTiGriD.layout.OpeningLayout;
import fr.sylfrey.misTiGriD.layout.ProsumerLayout;
import fr.sylfrey.misTiGriD.layout.ThermicObjectLayout;

@Component(name="LayoutRegistry",immediate=true)
//@Instantiate
public class LayoutRegistry {

	@Requires
	public HttpService httpService;

	@Bind(specification="fr.sylfrey.misTiGriD.layout.Layout",aggregate=true, optional=true)
	public void bind(Layout layout) {

		if (layout instanceof AtmosphereLayout) {
			atmosphereLayout = (AtmosphereLayout) layout;
			
		} else if (layout instanceof HeaterLayout) {
			heaterLayouts.put(((HeaterLayout) layout).getName(), (HeaterLayout) layout);

		} else if (layout instanceof LampLayout) {
			lampLayouts.put(((LampLayout) layout).getName(), (LampLayout) layout);

		} else if (layout instanceof ThermicObjectLayout) {
			thermicLayouts.put(((ThermicObjectLayout) layout).getName(), (ThermicObjectLayout) layout);

		} else if (layout instanceof ProsumerLayout) {
			prosumerLayouts.put(((ProsumerLayout) layout).getName(), (ProsumerLayout) layout);

		} else if (layout instanceof HeaterManagerLayout) {
			heaterManagerLayouts.put(HeaterManagerLayout + hmlCounter++, (HeaterManagerLayout) layout);

		} else if (layout instanceof OpeningLayout) {
			openingLayouts.put(OpeningLayout + olCounter++, (OpeningLayout) layout);

		} else {
			System.out.println("### warning: bad Layout " + layout.getClass() + " not bound to LayoutRegistry");
		}

	}

	@Unbind(specification="fr.sylfrey.misTiGriD.layout.Layout",aggregate=true)
	public void unbind(Layout layout) {
		
		if (layout instanceof AtmosphereLayout) {
			atmosphereLayout = null;
		} else if (layout instanceof ThermicObjectLayout) {
			for (Map.Entry<String, ThermicObjectLayout> entry : thermicLayouts.entrySet()) {
				if (entry.getValue() == layout) thermicLayouts.remove(entry.getKey());
			}				

		} else if (layout instanceof ProsumerLayout) {
			for (Map.Entry<String, ProsumerLayout> entry : prosumerLayouts.entrySet()) {
				if (entry.getValue() == layout) prosumerLayouts.remove(entry.getKey());
			}

		} else if (layout instanceof HeaterLayout) {
			for (Map.Entry<String, HeaterLayout> entry : heaterLayouts.entrySet()) {
				if (entry.getValue() == layout) heaterLayouts.remove(entry.getKey());
			}

		} else if (layout instanceof HeaterManagerLayout) {
			for (Map.Entry<String, HeaterManagerLayout> entry : heaterManagerLayouts.entrySet()) {
				if (entry.getValue() == layout) heaterManagerLayouts.remove(entry.getKey());
			}

		} else if (layout instanceof OpeningLayout) {
			for (Map.Entry<String, OpeningLayout> entry : openingLayouts.entrySet()) {
				if (entry.getValue() == layout) openingLayouts.remove(entry.getKey());
			}

		} else if (layout instanceof LampLayout) {
			for (Map.Entry<String, LampLayout> entry : lampLayouts.entrySet()) {
				if (entry.getValue() == layout) lampLayouts.remove(entry.getKey());
			}

		} else {
			System.out.println("### warning: bad Layout " + layout.getClass() + " not unbound from LayoutRegistry");
		}
	}
	
	@Bind(id="houseLoadManager", optional=true)
	public void bindHierarch(HouseLoadManager houseLoadManager) {
		this.houseLoadManager = houseLoadManager;
	}

	@Unbind(id="houseLoadManager")
	public void unbindHierarch(HouseLoadManager houseLoadManager) {
		houseLoadManager = null;
	}
	
	
	private static final String LoadHierarch = "LoadHierarch";
	private static final String AtmosphereLayout = "AtmosphereLayout";
	private static final String ThermicObjectLayout = "ThermicObjectLayout";
	private static final String ProsumerLayout = "ProsumerLayout";
	private static final String HeaterLayout = "HeaterLayout";
	private static final String HeaterManagerLayout = "HeaterManagerLayout";
	private static final String OpeningLayout = "OpeningLayout";
	private static final String LampLayout = "LampLayout";
	private static final String AllLayouts = "AllLayouts";


	private AtmosphereLayout atmosphereLayout;
	private Map<String, ThermicObjectLayout> thermicLayouts = new ConcurrentHashMap<String, ThermicObjectLayout>();
	private Map<String, ProsumerLayout> prosumerLayouts = new ConcurrentHashMap<String, ProsumerLayout>();
	private Map<String, HeaterLayout> heaterLayouts = new ConcurrentHashMap<String, HeaterLayout>();
	private Map<String, HeaterManagerLayout> heaterManagerLayouts = new ConcurrentHashMap<String, HeaterManagerLayout>();
	private Map<String, OpeningLayout> openingLayouts = new ConcurrentHashMap<String, OpeningLayout>();
	private Map<String, LampLayout> lampLayouts = new ConcurrentHashMap<String, LampLayout>();
	
	private HouseLoadManager houseLoadManager;
	

	private static final String INDEX_PATH = "/layoutsIndex";
	private static final String LAYOUT_PREFIX = "/layouts";
	private static final String WEBGUI_PREFIX = "/webgui";

	private ObjectMapper mapper = new ObjectMapper();

	private int hmlCounter = 0;
	private int olCounter = 0;


	@Validate
	public void start() {

		try {
			httpService.registerResources(WEBGUI_PREFIX, "/WebGUI", null); 
		} catch (NamespaceException e) {
//			e.printStackTrace();
			System.out.println("# web resources already registered");
		}

		try {
			httpService.registerServlet(INDEX_PATH, new IndexServlet(), null, null); 
			httpService.registerServlet(LAYOUT_PREFIX, new SerialiserServlet(), null, null);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (NamespaceException e) {
//			e.printStackTrace();
			System.out.println("# web resource already registered");
		}

	}

	@Invalidate
	public void stop() {
		httpService.unregister(WEBGUI_PREFIX);
		httpService.unregister(INDEX_PATH);
		httpService.unregister(LAYOUT_PREFIX);
	}


	/**
	 * Bound to /INDEX_PATH
	 * On GET request, returns a JSON object mapping layout types 
	 * to arrays of layout names, example:
	 * {
	 * 		"ThermicObjectLayout" : ["atmosphere","room","kitchen"],
	 * 		"HeaterLayout" : ["heater_kitchen","heater_room"],
	 * 		etc.
	 * }
	 * Then, each layout object can be accessed at URL 
	 * /LAYOUT_PREFIX/type/name, example:
	 * 		/layouts/HeaterLayout/heater_kitchen
	 * @author syl
	 *
	 */
	class IndexServlet extends HttpServlet {

		private static final long serialVersionUID = 1L;

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.getWriter().write(index());		
		}	

		private String index() {

			ObjectNode index = mapper.createObjectNode();

			ArrayNode atmAN = mapper.createArrayNode();
			atmAN.add(AtmosphereLayout);
			index.put(AtmosphereLayout, atmAN);
			
			ArrayNode tols = mapper.createArrayNode();
			for (String tol : thermicLayouts.keySet()) {
				tols.add(tol);
			}
			index.put(ThermicObjectLayout, tols);

			ArrayNode pls = mapper.createArrayNode();
			for (String pl : prosumerLayouts.keySet()) {
				pls.add(pl);
			}
			index.put(ProsumerLayout, pls);
			
			ArrayNode hls = mapper.createArrayNode();
			for (String hl : heaterLayouts.keySet()) {
				hls.add(hl);
			}
			index.put(HeaterLayout, hls);

			ArrayNode hmls = mapper.createArrayNode();
			for (String hml : heaterManagerLayouts.keySet()) {
				hmls.add(hml);
			}
			index.put(HeaterManagerLayout, hmls);

			ArrayNode ols = mapper.createArrayNode();
			for (String ol : openingLayouts.keySet()) {
				ols.add(ol);
			}
			index.put(OpeningLayout, ols);

			ArrayNode lls = mapper.createArrayNode();
			for (String ll : lampLayouts.keySet()) {
				lls.add(ll);
			}
			index.put(LampLayout, lls);
			
			index.put(LoadHierarch, LoadHierarch);
			
			return index.toString();
		}

	}

	/**
	 * Returns a JSON representation of a Layout object, or "null" if not found.
	 * The URL of the GET request sent to this servlet identifies the object, example:
	 * 		GET /layouts/HeaterLayout/heater_kitchen
	 * returns the serialisation of an HeaterLayout named heater_kitchen.
	 * @author syl
	 *
	 */
	class SerialiserServlet extends HttpServlet {

		private static final long serialVersionUID = 1L;

		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{

			String[] path = req.getPathInfo().split("/");	
			if (path.length < 3) {

				resp.getWriter().write("### error, invalid path: " + req.getPathInfo());
				return;

			}

			String layoutType = path[1];
			String layoutName = path[2];
			String DATA = req.getParameter("info");

			if(DATA != null){

				if(layoutType.equals(HeaterManagerLayout)){

					HeaterManagerLayout heaterMan = heaterManagerLayouts.get(layoutName);
					float temperature = Float.parseFloat(DATA);
					heaterMan.setRequiredTemperature(temperature);

				} else if(layoutType.equals(HeaterLayout)){

					HeaterLayout heaterMan = heaterLayouts.get(layoutName);
					float power = Float.parseFloat(DATA);
					heaterMan.setEmissionPower(power);

				} else if(layoutType.equals(OpeningLayout)){
					
					OpeningLayout wallLayoutObj = openingLayouts.get(layoutName);
					boolean status = Boolean.parseBoolean(DATA);
					if(status){
						wallLayoutObj.open();
					}else{
						wallLayoutObj.close();
					}
					
				} else if(layoutType.equals(LampLayout)){

					LampLayout arduinoLamp = lampLayouts.get(layoutName);
					boolean power = Boolean.parseBoolean(DATA);
					if (power) { // turn on
						arduinoLamp.setEmissionPower( 1023f/17.05f );
						arduinoLamp.turnOn(); // wtf? intern!
					} else { // turn off
						arduinoLamp.setEmissionPower(0);
						arduinoLamp.turnOff(); // again
					}

				} else if (layoutType.equals(AtmosphereLayout)) {
					
					boolean increase = Boolean.parseBoolean(DATA);
					if (increase) { // raise temperature
						atmosphereLayout.setBaseTemperature(atmosphereLayout.getBaseTemperature() + 0.5f);
					} else { // decrease temperature
						atmosphereLayout.setBaseTemperature(atmosphereLayout.getBaseTemperature() - 0.5f);
					}
					
				}

			}
		}

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

			String[] path = req.getPathInfo().split("/");
			String layoutType = null;
			String layoutName = null;

			if (path.length < 3) {
				if(!path[1].equals(AllLayouts)){
					resp.getWriter().write("Error 404: " + req.getPathInfo() + " not found.");
					return;			
				}
				layoutType = path[1];
			}else{
				layoutType = path[1];
				layoutName = path[2];
			}

			ObjectNode node = mapper.createObjectNode();
			String response = "null";

			if (layoutType.equals(AtmosphereLayout)) {
				if (atmosphereLayout != null) response = serialise(atmosphereLayout, node).toString();
				
			} else if (layoutType.equals(ThermicObjectLayout)) {
				ThermicObjectLayout layout = thermicLayouts.get(layoutName);
				if (layout!= null) response = serialise(layout, node).toString(); 

			} else if (layoutType.equals(HeaterLayout)) {
				HeaterLayout layout = heaterLayouts.get(layoutName);
				if (layout!= null) response = serialise(layout, node).toString(); 

			} else if (layoutType.equals(ProsumerLayout)) {
				ProsumerLayout layout = prosumerLayouts.get(layoutName);
				if (layout!= null) response = serialise(layout, node).toString(); 

			} else if (layoutType.equals(HeaterManagerLayout)) {
				HeaterManagerLayout layout = heaterManagerLayouts.get(layoutName);
				if (layout!= null) response = serialise(layout, node).toString(); 

			} else if (layoutType.equals(OpeningLayout)) {
				OpeningLayout layout = openingLayouts.get(layoutName);
				if (layout!= null) response = serialise(layout, node).toString(); 

			} else if (layoutType.equals(LampLayout)) {
				LampLayout layout = lampLayouts.get(layoutName);
				if (layout!= null) response = serialise(layout, node).toString(); 

			} else if (layoutType.equals(AllLayouts)){
				response = serialiseUpdate(node).toString();
				
			} else if (layoutType.equals(LoadHierarch)){
				if (houseLoadManager!= null) response = serialise(houseLoadManager, node).toString();
			}

			resp.getWriter().write(response);

		}	

	}

	private ObjectNode serialise(Layout layout, ObjectNode node) {
		node.put("x", layout.x());
		node.put("y", layout.y());
		node.put("width", layout.width());
		node.put("height", layout.height());
		node.put("layer", layout.layer());
		return node;
	}

	private ObjectNode serialise(OpeningLayout layout, ObjectNode node) {
		serialise((Layout) layout, node);
		node.put("isOpen", layout.isOpen());
		node.put("isClosed", layout.isClosed());
		return node;
	}

	private ObjectNode serialise(HeaterManagerLayout layout, ObjectNode node) {
		serialise((Layout) layout, node);
		node.put("requiredTemperature", layout.getRequiredTemperature());	
		node.put("isEconomizing", layout.isEconomizing());		
		return node;
	}
	
	private ObjectNode serialise(AtmosphereLayout layout, ObjectNode node) {
		serialise((ThermicObjectLayout) layout, node);
		return node;
	}

	private ObjectNode serialise(ThermicObjectLayout layout, ObjectNode node) {
		serialise((Layout) layout, node);
		node.put("name", layout.getName());		
		node.put("currentTemperature", layout.getCurrentTemperature());		
		return node;
	}

	private ObjectNode serialise(ProsumerLayout layout, ObjectNode node) {
		serialise((Layout) layout, node);
		node.put("name", layout.getName());		
		node.put("prosumedPower", layout.getProsumedPower());		
		return node;
	}

	private ObjectNode serialise(HeaterLayout layout, ObjectNode node) {
		serialise((Layout) layout, node);
		node.put("emissionPower", layout.getEmissionPower());
		node.put("maxEmissionPower", layout.getMaxEmissionPower());
		return node;
	}

	private ObjectNode serialise(LampLayout layout, ObjectNode node) {
		serialise((Layout) layout, node);
		node.put("prosumedPower", layout.getProsumedPower());
		node.put("emissionPower", layout.getEmissionPower());
		node.put("maxEmissionPower", layout.getMaxEmissionPower());
		return node;
	}

	private ObjectNode serialise(HouseLoadManager houseLoadManager, ObjectNode node) {
		node.put("maxConsumptionThreshold", houseLoadManager.maxConsumptionThreshold());
		return node;
	}
	
	private ObjectNode serialiseUpdate(ObjectNode node) {

		for (Map.Entry<String, ThermicObjectLayout> tol : thermicLayouts.entrySet()) {
			ObjectNode object = mapper.createObjectNode();
			ThermicObjectLayout aux = tol.getValue();
			if(aux!=null){
				String id = tol.getKey();
				object.put("type",ThermicObjectLayout);
				object.put("name",aux.getName());
				object.put("currentTemperature",aux.getCurrentTemperature());
				node.put(id,object);
			}
		}

		for (Map.Entry<String, ProsumerLayout> pl : prosumerLayouts.entrySet()) {
			ObjectNode object = mapper.createObjectNode();
			ProsumerLayout aux = pl.getValue();
			if(aux!=null){
				String id = pl.getKey();
				object.put("type",ProsumerLayout);
				object.put("name",aux.getName());
				object.put("prosumedPower", aux.getProsumedPower());
				node.put(id,object);
			}
		}
		
		for (Map.Entry<String, HeaterManagerLayout> tol : heaterManagerLayouts.entrySet()) {
			ObjectNode object = mapper.createObjectNode();
			HeaterManagerLayout aux = tol.getValue();
			if(aux!=null){
				String id = tol.getKey();
				object.put("type",HeaterManagerLayout);
				object.put("requiredTemperature",aux.getRequiredTemperature());
				object.put("isEconomizing", aux.isEconomizing());		
				node.put(id,object);
			}
		}

		for (Map.Entry<String, HeaterLayout> tol : heaterLayouts.entrySet()) {
			ObjectNode object = mapper.createObjectNode();
			HeaterLayout aux = tol.getValue();
			if(aux!=null){
				String id = tol.getKey();
				object.put("type",HeaterLayout);
				object.put("emissionPower",aux.getEmissionPower());
				object.put("maxEmissionPower",aux.getMaxEmissionPower());
				node.put(id,object);
			}
		}

		for (Map.Entry<String, OpeningLayout> tol : openingLayouts.entrySet()) {
			ObjectNode object = mapper.createObjectNode();
			OpeningLayout aux = tol.getValue();
			if(aux!=null){
				String id = tol.getKey();
				object.put("type",OpeningLayout);
				object.put("height",aux.height());
				object.put("width",aux.width());
				object.put("isOpen",aux.isOpen());
				object.put("isClosed",aux.isClosed());
				node.put(id,object);
			}
		}

		for (Map.Entry<String, LampLayout> all : lampLayouts.entrySet()) {
			ObjectNode object = mapper.createObjectNode();
			LampLayout aux = all.getValue();
			if(aux!=null){
				String id = all.getKey();
				object.put("type",LampLayout);
				object.put("prosumedPower", aux.getProsumedPower());
				object.put("emissionPower",aux.getEmissionPower());
				object.put("maxEmissionPower",aux.getMaxEmissionPower());
				node.put(id,object);
			}
		}
		
		if (houseLoadManager != null) {
			ObjectNode object = mapper.createObjectNode();
			object.put("maxConsumptionThreshold", houseLoadManager.maxConsumptionThreshold());
			node.put(LoadHierarch, object);
		}
		
		if (atmosphereLayout != null) {
			ObjectNode object = mapper.createObjectNode();
			object.put("type",AtmosphereLayout);
			object.put("name",atmosphereLayout.getName());
			object.put("currentTemperature",atmosphereLayout.getCurrentTemperature());
			node.put(AtmosphereLayout,object);
		}
		
		return node;
	}
}
