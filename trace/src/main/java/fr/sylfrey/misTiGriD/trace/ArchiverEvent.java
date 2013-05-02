package fr.sylfrey.misTiGriD.trace;

public class ArchiverEvent<Content> {
	public final String type;
	public final Content content;
	public ArchiverEvent(String type, Content content) {
		this.type = type;
		this.content = content;
	}
	@Override public String toString() {
		return "{type : " + type + ", content : " + content + "}";
	}
}