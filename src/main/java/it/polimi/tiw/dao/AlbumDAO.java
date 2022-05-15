package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Album;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

public class AlbumDAO {
	
	private Connection connection;
	
	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Album> getAlbumsByUser(int userId) throws SQLException {
		List<Album> albums = new ArrayList<>();
		
		String prepared_query = "SELECT * FROM ALBUM WHERE idUser = ? ORDER BY date DESC";
		
		PreparedStatement preparedStatement = this.connection.prepareStatement(prepared_query);
		preparedStatement.setInt(1, userId);
		
		ResultSet result = preparedStatement.executeQuery();
		
		while (result.next()) {
			Album album_to_add = new Album();
			album_to_add.setUserId(userId);
			album_to_add.setId(result.getInt("idUser"));
			album_to_add.setTitle(result.getString("title"));
			album_to_add.setDateOfCreation(result.getDate("date"));
			
			albums.add(album_to_add);
		}
		
		return albums;
	}
}