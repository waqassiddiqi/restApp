package net.waqassiddiqi.app.crew.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.waqassiddiqi.app.crew.model.QuickComment;

import org.apache.log4j.Logger;

public class QuickCommentDAO {
	private Logger log = Logger.getLogger(getClass().getName());
	protected ConnectionManager db;
	
	public QuickCommentDAO() {
		db = ConnectionManager.getInstance();
	}
	
	public int addQuickComment(String comment) {
		int generatedId = db.executeInsert("INSERT INTO quick_comments(quick_comment) " +
				"VALUES(?);", 
				
				new String[] { comment });
		
		if(log.isDebugEnabled()) {
			log.debug("Generated ID: " + generatedId);
		}
		
		return generatedId;
	}
	
	public List<QuickComment> getAll() {
		List<QuickComment> list = new ArrayList<QuickComment>();
		QuickComment qComment = null;
		
		final ResultSet rs = this.db.executeQuery("SELECT * FROM quick_comments order by id");
		try {
			while(rs.next()) {
				qComment = new QuickComment();
				qComment.setId(rs.getInt("id"));
				qComment.setQuickComment(rs.getString("quick_comment"));
				
				list.add(qComment);
			}
		} catch (Exception e) {
			log.error("Error executing EntryTimeDAO.getAll(): " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return list;
		
	}
}
