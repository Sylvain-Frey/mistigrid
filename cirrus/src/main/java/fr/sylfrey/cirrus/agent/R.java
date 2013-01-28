package fr.sylfrey.cirrus.agent;

import java.io.Serializable;
import java.net.URI;

public interface R<Type> extends Serializable, Comparable<R<?>> {
	
	public URI uri();
	public Type _();

}
