package fr.tpt.s3.iris.akka;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.osgi.service.http.NamespaceException;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;
import fr.tpt.s3.akka.ActorSystemProvider;
import fr.tpt.s3.iris.NotFoundException;

@Component
@Provides
@Instantiate
public class AkkaProviderImpl implements AkkaProvider {

	@Bind
	public void bindActorSystem(ActorSystemProvider provider) {
		actorSystem = provider.getSystem();
	}
	public ActorSystem actorSystem;


	@Override
	public <S> void publish(String path, final S service, Class<S> clazz)	throws NamespaceException {
		TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<S>(clazz, 
						new Creator<S>() {
							public S create() {
								return service;
							}}),
						path);
	}

	@Override
	public void unpublish(String path) {
		TypedActor.get(actorSystem).poisonPill(actorSystem.actorFor(path));		
	}
	

	@Override
	public <S> S get(String url, Class<S> clazz) throws NotFoundException {
		S proxy = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<S>(clazz),
				actorSystem.actorFor(url));
		if (proxy==null) throw new NotFoundException();
		return proxy;
	}

	@Override
	public <S> void release(S proxy) {
		if (TypedActor.get(actorSystem).isTypedActor(proxy)) TypedActor.get(actorSystem).stop(proxy); 
	}

}
