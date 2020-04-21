
import java.sql.*;

public class DBConnection {
	private Connection connection = null;
	
	public DBConnection(String pServer, String pPort, String pDatabase, String pUser, String pPass){
		try{
			Class.forName("org.postgresql.Driver");  
			connection = DriverManager.getConnection("jdbc:postgresql://" + pServer + ":" + pPort + "/" + pDatabase, pUser, pPass);
		}
		catch(Exception ex){
			System.err.println(ex.getMessage());
		}
	}
	
	public Connection getConnection(){
		return this.connection;
	}
}
