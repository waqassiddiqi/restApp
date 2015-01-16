package net.waqassiddiqi.app.crew.model;

public class QuickComment {
	private int id;
	private String quickComment;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getQuickComment() {
		return quickComment;
	}
	public void setQuickComment(String quickComment) {
		this.quickComment = quickComment;
	}
	
	@Override
	public String toString() {
		return quickComment;
	}
}
