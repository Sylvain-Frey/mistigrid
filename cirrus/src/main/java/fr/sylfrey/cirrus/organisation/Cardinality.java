package fr.sylfrey.cirrus.organisation;

public class Cardinality {
	
	public final int minMembers;
	public final int maxMembers;


	private Cardinality(int minMembers, int maxMembers) {
		super();
		this.minMembers = minMembers;
		this.maxMembers = maxMembers;
	}

	
	public static Cardinality _(int minMembers, int maxMembers) {
		return new Cardinality(minMembers, maxMembers);
	}
	
	
}
