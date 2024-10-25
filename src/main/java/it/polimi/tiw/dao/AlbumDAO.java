package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Album;

import java.sql.Connection;
import java.sql.Date;
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
	
	public List<Album> getAlbumsByUser(int userId, boolean thisUser) throws SQLException {
		List<Album> albums = new ArrayList<>();
		
		String operator = thisUser ? "=" : "<>"; 
		
		String prepared_query = "SELECT * FROM ALBUM WHERE idUser" + operator + "? ORDER BY date DESC";
		
		PreparedStatement preparedStatement = this.connection.prepareStatement(prepared_query);
		preparedStatement.setInt(1, userId);
		
		ResultSet result = preparedStatement.executeQuery();
		//TODO try
		while (result.next()) {
			Album album_to_add = new Album();
			album_to_add.setUserId(userId);
			album_to_add.setId(result.getInt("idAlbum"));
			album_to_add.setTitle(result.getString("title"));
			album_to_add.setDateOfCreation(result.getDate("date"));
			
			albums.add(album_to_add);
		}
		
		return albums;
	}
	
	public Album getAlbumById(int albumId) throws SQLException {
		Album album = null;
		
		String prepared_query = "SELECT * FROM ALBUM WHERE idAlbum = ?";
		
		PreparedStatement preparedStatement = this.connection.prepareStatement(prepared_query);
		preparedStatement.setInt(1, albumId);
		
		ResultSet result = preparedStatement.executeQuery();
		
		if (result.next()) {
			album = new Album();
			album.setId(result.getInt("idAlbum"));
			album.setTitle(result.getString("title"));
			album.setDateOfCreation(result.getDate("date"));
			album.setUserId(result.getInt("idUser"));
		}
		
		return album;
	}
	
	public void createEmptyAlbum(String name, int userId) throws SQLException {
		String prepared_query = "INSERT INTO Album (title, date, idUser) VALUES (?, CURRENT_TIMESTAMP(),?)";
		PreparedStatement preparedStatement = this.connection.prepareStatement(prepared_query);
		preparedStatement.setString(1, name);
		preparedStatement.setInt(2, userId);
		
		preparedStatement.executeUpdate();
	}
	
	/**
	 * Get the ID of the user that owns that album
	 * @param albumId ID of the album
	 * @return ID of the owner. -1 if the album doesn't exist
	 * @throws SQLException 
	 */
	public int getIdOwnerOfAlbum(int albumId) throws SQLException {
		String prepared_query = "SELECT idUser FROM Album WHERE idAlbum = ?";
		PreparedStatement preparedStatement = this.connection.prepareStatement(prepared_query);
		preparedStatement.setInt(1, albumId);
		
		ResultSet result = preparedStatement.executeQuery();
		if (result.next()) {
			return result.getInt("idUser");
		}
		return -1;
	}
}