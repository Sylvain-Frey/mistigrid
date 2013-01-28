package fr.sylfrey.cirrus.touchpoint;

public interface Consumer<Data> {
	
	public void tell(Data data);

}
