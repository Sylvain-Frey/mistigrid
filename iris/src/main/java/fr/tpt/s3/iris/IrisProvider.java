package fr.tpt.s3.iris;

import org.osgi.service.http.NamespaceException;

public interface IrisProvider {
	
	public <S> void publish(String path, S service, Class<S> clazz) throws NamespaceException;
	public void unpublish(String path);
	
	public <S> S get(String url, Class<S> clazz) throws NotFoundException;
	public <S> void release(S proxy);

}
