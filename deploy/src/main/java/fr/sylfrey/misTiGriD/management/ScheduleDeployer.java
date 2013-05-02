package fr.sylfrey.misTiGriD.management;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.BundleContext;

import akka.actor.TypedActor;
import akka.actor.TypedActorExtension;
import akka.actor.TypedProps;
import akka.japi.Creator;
import fr.sylfrey.akka.ActorSystemProvider;
import fr.sylfrey.misTiGriD.alba.basic.model.Schedule;
import fr.sylfrey.misTiGriD.alba.basic.model.ScheduleImpl;
import fr.sylfrey.misTiGriD.environment.Time;

@Component
@Instantiate
public class ScheduleDeployer {

	public ScheduleDeployer(@Requires BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	@Requires Time time;
	@Requires ActorSystemProvider actorSystemProvider;
	BundleContext bundleContext;

	@Validate void start() {
		
		final TypedActorExtension tas = TypedActor.get(actorSystemProvider.getSystem());

		final long size = time.dayLength()/1000;

		Schedule schedule = tas.typedActorOf(
				new TypedProps<Schedule>(
						Schedule.class, 
						new Creator<Schedule>() {
							public Schedule create() { return new ScheduleImpl(time, (int) size); }
						}),
						"schedule");
		System.out.println("# remote schedule deployed: " + tas.getActorRefFor(schedule));
		bundleContext.registerService(Schedule.class.getName(), schedule, null);
		
	}
}
