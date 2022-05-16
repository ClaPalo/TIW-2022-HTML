package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Image;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

public class ImageDAO {
	
	private Connection connection;
	
	public ImageDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Image> getImagesByAlbum(int albumId) throws SQLException {
		List<Image> images = new ArrayList<>();
		
		String prepared_query = 
					"SELECT * "
				+ 	"FROM Image I join AlbumImages AI on (I.idImage = AI.idImage)"
				+ 	"WHERE AI.idAlbum = ? "
				+ 	"ORDER BY date DESC";
		
		PreparedStatement preparedStatement = this.connection.prepareStatement(prepared_query);
		preparedStatement.setInt(1, albumId);
		
		ResultSet result = preparedStatement.executeQuery();
		//TODO try
		while (result.next()) {
			Image image_to_add = new Image();
			image_to_add.setId(result.getInt("idImage"));
			image_to_add.setTitle(result.getString("title"));
			image_to_add.setText(result.getString("text"));
			image_to_add.setDateOfCreation(result.getDate("date"));
			image_to_add.setImgPath(result.getString("img_path"));
			image_to_add.setUserId(result.getInt("idUser"));
			
			images.add(image_to_add);
		}
		
		return images;
	}
	
	public Image getImageById(int imageId) throws SQLException {
		Image image = null;
		
		String prepared_query = "SELECT * FROM Image WHERE idImage = ?";
		
		PreparedStatement preparedStatement = this.connection.prepareStatement(prepared_query);
		preparedStatement.setInt(1, imageId);
		
		ResultSet result = preparedStatement.executeQuery();
		
		if (result.next()) {
			image = new Image();
			image.setId(result.getInt("idAlbum"));
			image.setTitle(result.getString("title"));
			image.setText(result.getString("text"));
			image.setDateOfCreation(result.getDate("date"));
			image.setUserId(result.getInt("idUser"));
			image.setImgPath(result.getString("img_path"));
		}
		
		return image;
	}
}