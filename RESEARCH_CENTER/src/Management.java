

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.Vector;


public class Management {
	private DBConnection myConnection = null;
	
	public Management(DBConnection pConnection){
		myConnection = pConnection;
	}
	
	
	public Vector<String> getAllCompetitionsThat(int pMonth){
		Vector<String> result = new Vector<String>(); 
		String query = "SELECT DISTINCT call.id AS id_call, title AS title_call " + 
				       "FROM call INNER JOIN proposal ON call.id = proposal.callid " + 
		               "INNER JOIN collaborator ON collaborator.proposalid  = proposal.id " +
		               "WHERE call.status = 'open' AND EXTRACT(MONTH FROM deadline) > ? " + 
		               "AND ((amount > ?) OR collaborator.proposalid IN (SELECT proposalid FROM collaborator GROUP BY proposalid HAVING COUNT(researcherid) > ?))";

		
		try {
			PreparedStatement psCompetittions = myConnection.getConnection().prepareStatement(query);
			//Month given as parameter
			psCompetittions.setInt(1, pMonth);
			
			//conditions given to find only large proposals
			psCompetittions.setFloat(2,10); //minimum of money requested
			psCompetittions.setInt(3,1);      //maximum of participants 
			ResultSet rsCompetitions = psCompetittions.executeQuery();

			while (rsCompetitions.next()) {
				result.add("ID_CALL: " + rsCompetitions.getInt("id_call") + " -- | -- TITLE_CALL: " + rsCompetitions.getString("title_call"));
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	
		return result;
	}
	
	
	
	
	
	public Vector<String> getAllCompetitionsThatOnly(int pMonth, String area, int principalInvest){
		Vector<String> result = new Vector<String>(); 
		String query = "SELECT DISTINCT call.id AS id_call, title AS title_call " + 
				       "FROM call INNER JOIN proposal ON call.id = proposal.callid " + 
		               "INNER JOIN collaborator ON collaborator.proposalid  = proposal.id " +
		               "WHERE call.status = 'open' AND EXTRACT(MONTH FROM deadline) > ? " + 
		               "AND call.area = ? AND proposal.pi = ? " +
		               "AND ((amount > ?) OR collaborator.proposalid IN (SELECT proposalid FROM collaborator GROUP BY proposalid HAVING COUNT(researcherid) > ?))";

		
		try {
			PreparedStatement psCompetittions = myConnection.getConnection().prepareStatement(query);
			//Month given as parameter
			psCompetittions.setInt(1, pMonth);
			
			psCompetittions.setString(2, area);
			psCompetittions.setInt(3, principalInvest);
			
			//conditions given to find only large proposals
			psCompetittions.setFloat(4,20000); //minimum of money requested
			psCompetittions.setInt(5,10);      //maximum of participants 
			ResultSet rsCompetitions = psCompetittions.executeQuery();

			while (rsCompetitions.next()) {
				result.add("ID_CALL: " + rsCompetitions.getInt("id_call") + " -- | -- TITLE_CALL: " + rsCompetitions.getString("title_call"));
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	
		return result;
	}
	
	
	public Vector<String> getProposalOfMoreMoney(String pArea){
		Vector<String> result = new Vector<String>(); 
		String query = "SELECT proposal.id, amount " +
				"FROM call INNER JOIN proposal ON call.id = proposal.callid " +
				"WHERE call.area = ? AND amount IS NOT null " +
				"ORDER BY amount DESC LIMIT 1";

		try {
			PreparedStatement psProposals = myConnection.getConnection().prepareStatement(query);
			//Area given as parameter
			psProposals.setString(1, pArea);
			ResultSet rsProposals = psProposals.executeQuery();

			while (rsProposals.next()) {
				result.add("PROPOSAL_ID: " + rsProposals.getInt("id") + " -- | -- PROPOSAL_AMOUNT: " + rsProposals.getFloat("amount"));
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	
		return result;
	}

	
	public Vector<String> getProposalBeforeDate(Date pDate){
		Vector<String> result = new Vector<String>(); 
		String query = "SELECT proposal.id, deadline, amount " +
						"FROM call INNER JOIN proposal ON call.id = proposal.callid " +
						"WHERE proposal.status = 'awarded' AND amount IS NOT null AND deadline < ? " +
						"ORDER BY amount DESC LIMIT 1";

		try {
			PreparedStatement psProposals = myConnection.getConnection().prepareStatement(query);
			//Area given as parameter
			psProposals.setDate(1, pDate);
			ResultSet rsProposals = psProposals.executeQuery();

			while (rsProposals.next()) {
				result.add("PROPOSAL_ID: " + rsProposals.getInt("id") +
						" -- | -- PROPOSAL_DATE: " + rsProposals.getDate("deadline") + 
						" -- | -- PROPOSAL_AMOUNT: " + rsProposals.getFloat("amount"));
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	
		return result;
	}


	
	
	public Vector<String[]> getAllAreas(){
		Vector<String[]> result = new Vector<String[]>(); 
		String query = "SELECT DISTINCT area FROM call ORDER BY area";
		
		try {
			Statement stAreas = myConnection.getConnection().createStatement();
			ResultSet rsAreas = stAreas.executeQuery(query);
			
			int i = 1;
			while (rsAreas.next()) {
				String[] area = new String[2];
				area[0] = String.valueOf(i) + "- " + rsAreas.getString("area");
				area[1] = rsAreas.getString("area");
				result.add(area);
				i++;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	
		return result;
	}
	
	
	public int getAmountAreas(){
		int result = 0;
		String query = "SELECT DISTINCT COUNT(*) FROM call";
		try {
			PreparedStatement psAreas = myConnection.getConnection().prepareStatement(query);
			ResultSet rsAreas = psAreas.executeQuery();
			
			while (rsAreas.next ()){
				result = rsAreas.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	public Vector<Object[]> getAllInvestigators(){
		Vector<Object[]> result = new Vector<Object[]>(); 
		String query = "SELECT concat_ws(' ', firstname, lastname) AS invest, id FROM researcher ORDER BY concat_ws(' ', firstname, lastname)";
		
		try {
			Statement stInvest = myConnection.getConnection().createStatement();
			ResultSet rsInvest = stInvest.executeQuery(query);
			
			int i = 1;
			while (rsInvest.next()) {
				Object[] investig = new Object[2];
				investig[0] = rsInvest.getInt("id");
				investig[1] = String.valueOf(i) + "- " + rsInvest.getString("invest");
				result.add(investig);
				i++;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	
		return result;
	}
	
	
	public int getAmountInvestigators(){
		int result = 0;
		String query = "SELECT COUNT(*) FROM researcher";
		try {
			PreparedStatement psInvest = myConnection.getConnection().prepareStatement(query);
			ResultSet rsInvest = psInvest.executeQuery();
			
			while (rsInvest.next ()){
				result = rsInvest.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
	public float getAverageDiscrepancyForArea(String pArea){
		float result = 0;
		String query = "SELECT ABS(SUM(requests) - SUM(awards)) AS discrep " +
						"FROM (SELECT 0 as requests, amount AS awards " +
									"FROM proposal INNER JOIN call ON proposal.callid = call.id " +
							   		"WHERE call.area = ? AND proposal.status = 'awarded' " +
							   "UNION " +
							   "SELECT amount as requests, 0 AS awards " +
							   		"FROM proposal INNER JOIN call ON proposal.callid = call.id " +
							   		"WHERE call.area = ? AND proposal.status = 'submitted') AS two_crit ";
		
		
		try {
			PreparedStatement psAreaDiscrep = myConnection.getConnection().prepareStatement(query);
			psAreaDiscrep.setString(1, pArea);
			psAreaDiscrep.setString(2, pArea);
			ResultSet rsAreaDiscrep = psAreaDiscrep.executeQuery();
			
			while (rsAreaDiscrep.next ()){
				result = rsAreaDiscrep.getFloat("discrep");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	
	
	
	

}


