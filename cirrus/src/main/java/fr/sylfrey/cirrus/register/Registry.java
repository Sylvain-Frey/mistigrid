package fr.sylfrey.cirrus.register;

public interface Registry<Registree> {
	
	public void register(Registree registree);
	public void unregister(Registree registree);

}
