package fr.tpt.s3.microSmartGridSimulation.management.resources.loadAnalyser;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;
import fr.tpt.s3.akka.ActorSystemProvider;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Prosumer;
import fr.tpt.s3.microSmartGridSimulation.environment.Updatable;
import fr.tpt.s3.microSmartGridSimulation.framework.ActorContainer;
import fr.tpt.s3.microSmartGridSimulation.framework.topic.Topic;
import fr.tpt.s3.microSmartGridSimulation.management.data.LoadMessage;

@Component(name="LoadAnalyser",immediate=true)
@Provides(specifications={Updatable.class})
public class LoadAnalyserContainer implements Updatable {

	@Validate
	public void start() {
		loadAnalyser = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<LoadAnalyserImpl>(
						LoadAnalyser.class,
						new Creator<LoadAnalyserImpl>() {
							public LoadAnalyserImpl create() {
								return new LoadAnalyserImpl(loadTopic, prosumer, lowThreshold, highThreshold);
							}}),
							actorPath);
	}
	
	@Override
	public int getPeriod() {
		return period;
	}

	@Override
	public void update() {
		loadAnalyser.update();
	}	
	
	@Invalidate
	public void stop() {
		TypedActor.get(actorSystem).stop(loadAnalyser);		
	}

	@Bind
	public void bindActorSystem(ActorSystemProvider provider) {
		actorSystem = provider.getSystem();
	}

	public ActorSystem actorSystem;	

	@Property(mandatory=true)
	public String actorPath;
	
	@Bind(id="loadTopic")
	public void bindTopic(ActorContainer<Topic<LoadMessage>> container) {
		loadTopic = container.actor();
	}
	
	private Topic<LoadMessage> loadTopic;

	@Requires
	public Prosumer prosumer;
	
	@Property(mandatory=true)
	public int period;
	
	@Property
	public float lowThreshold; // !! probably negative !!

	@Property
	public float highThreshold; // !! probably negative !!
	
	private LoadAnalyser loadAnalyser;
	
}
