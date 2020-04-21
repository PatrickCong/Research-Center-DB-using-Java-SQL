

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.Vector;

public class Run {
	
	private static DBConnection myConnection = null;
	
	private static Management dataManagement = null;
	
	private static String HOST_NAME = "localhost";
	private static String PORT_NUMBER = "5432";
	private static String DATABASE_NAME = "420new";
	
	private static Scanner keyboardInput = null;
	
	public static void main(String[] args) {
		String user_name = "";
		String password = "";
				
		System.out.println("Please, enter your database conection user name");
		keyboardInput = new Scanner(System.in);
		user_name = keyboardInput.nextLine();
		
		System.out.println("Please, enter the database connection password:");
		keyboardInput = new Scanner(System.in);
		password = keyboardInput.nextLine();
		
		
		System.out.println("Stablishing database connection");
		myConnection = new DBConnection(Run.HOST_NAME, Run.PORT_NUMBER, Run.DATABASE_NAME, user_name, password);
		
		if (myConnection.getConnection() == null) {
			System.err.println("Failed to make connection!");
		} else {
			System.out.println("The connection was successfully stablished.");
			dataManagement = new Management(myConnection);
			
			displayMenu();
		}
	}
		
	public static void displayMenu(){
		boolean alreadyIn = false;
		boolean month_ok = false;
    	boolean area_ok = false;
    	boolean invest_ok = false;
    	
    	int num_month = 0;
		String month = "";
		
		int num_area = 0;
		String area = "";
		Vector<String[]> areas = new Vector<String[]>();
		
		int num_invest = 0;
		String investigator = "";
		
		String date = "";
		Date formatted_date = null;
    	
		//Scanner object to register keyboard input
		keyboardInput = new Scanner(System.in); 

		 //Holding the main menu selection
		 String mainMenuSelection = ""; 
		 
		 //System line separator to use in console writing
		 String lineSeparator = System.lineSeparator();
		 
		// Will hold user selections
		String allSelections = "";
		
		// Outer loop for the main menu. This will continue to loop until e or E is pressed.
		while (!mainMenuSelection.toLowerCase().equals("f")) {
			//Holds the main option selected
			String mainOperation = "";
			
			//Holds the parameters for the main option 
			String opPatameters = "";
			
			//Display the main menu...
			
			
			if (!alreadyIn) {
			    System.out.print("Please, select one of the following operations:" + lineSeparator
			    		+ "A:\tDisplay all competitions open at a given month, which at least one submitted large proposal." + lineSeparator 
			    		+ "B:\tDisplay large competitions given a month, area principle investigator." + lineSeparator
				        + "C:\tGiven an area, display the proposals requesting large amount of money." + lineSeparator 
				        + "D:\tDisplay proposals submitted before a given date, that awared the large amount of money." + lineSeparator
				        + "E:\tGiven an area, display the average the absolute value of the difference between the amounts" + lineSeparator
				        + "F:\tExit Order System" + lineSeparator + "Choice --> ");
	
				//Get menu selection input from the user
				mainMenuSelection = keyboardInput.nextLine().toLowerCase();
			}
			
			//Get the option selected based on the letter entered.
			switch (mainMenuSelection) {
				case "a":
					num_month = 0;
					month = "";
					if (!alreadyIn) {
						System.out.println("Please, enter the month. Use a number between 1 and 12:");
					}
					month = keyboardInput.nextLine().toLowerCase();
					try {
						num_month = Integer.parseInt(month);
						
						if(num_month > 0 && num_month < 13){
							Vector<String> competitions = dataManagement.getAllCompetitionsThat(num_month);
							if (competitions.size()>0) {
								for (int i = 0; i < competitions.size(); i++) {
									System.out.println(competitions.get(i));
								}
							}else{
								System.out.println("No competitions were found for those conditions.");
							}
							
							System.out.println("Do you whish to try other operations? (No --> n/ Any other key --> yes)");
							if(keyboardInput.nextLine().toLowerCase().equals("n")){
								System.out.println("Bye bye");
								System.exit(0);
							}
							
							alreadyIn = false;
						}else{
							System.err.println("Invalid month! Try again...");
							alreadyIn = true;
						}
						
					} catch (NumberFormatException  e) {
						System.err.println("The month must be a number between 1 and 12.");
						alreadyIn = true;
					}
					break;
			    case "b":
					if (!alreadyIn) {
						System.out.println("Please, enter the month, area and investigator:");
					}
					
					if (!month_ok) {
						System.out.println("Enter a month. Use a number between 1 and 12:");
						month = keyboardInput.nextLine().toLowerCase();
					}
					
					try {
						num_month = Integer.parseInt(month);
						
						if(num_month > 0 && num_month < 13){
							//areas.clear();
							month_ok = true;
							if (!area_ok) {
								System.out.println("Please, select one area:");
								areas = dataManagement.getAllAreas();
								for (int i = 0; i < areas.size(); i++) {
									System.out.println(areas.get(i)[0]);
								}
								area = keyboardInput.nextLine().toLowerCase();
							}
							
							try {
								num_area = Integer.parseInt(area);
								
								if(num_area > 0 && num_area < dataManagement.getAmountAreas()+1){
									area_ok = true;
									Vector<Object[]> investigators = new Vector<Object[]>();						
									if (!invest_ok) {
										System.out.println("Please, select the principle investigator:");
										investigators = dataManagement.getAllInvestigators();
										for (int i = 0; i < investigators.size(); i++) {
											System.out.println(String.valueOf(investigators.get(i)[1]));
										}
										investigator = keyboardInput.nextLine().toLowerCase();
									}
									
									try {
										num_invest = Integer.parseInt(investigator);
										
										if(num_invest > 0 && num_invest < dataManagement.getAmountInvestigators()+1){
											invest_ok = true;
											Vector<String> competitions = dataManagement.getAllCompetitionsThatOnly(num_month, areas.get(num_area-1)[1], (int)investigators.get(num_invest-1)[0]);
											if (competitions.size()>0) {
												for (int i = 0; i < competitions.size(); i++) {
													System.out.println(competitions.get(i));
												}
											}else{
												System.out.println("No competitions were found for those conditions.");
											}
											
											System.out.println("Do you whish to try other operations? (No --> n/ Any other key --> yes)");
											if(keyboardInput.nextLine().toLowerCase().equals("n")){
												System.out.println("Bye bye");
												System.exit(0);
											}
											
											alreadyIn = false;
										}else{
											System.err.println("Invalid investigator! Try again...");
											alreadyIn = true;
										}
									} catch (NumberFormatException  e) {
										System.err.println("The investigator must be a number between 1 and " + dataManagement.getAmountInvestigators());
										alreadyIn = true;
									}
								}else{
									System.err.println("Invalid area! Try again...");
									alreadyIn = true;
								}
							} catch (NumberFormatException  e) {
								System.err.println("The area must be a number between 1 and " + dataManagement.getAmountAreas());
								alreadyIn = true;
							}
						}else{
							System.err.println("Invalid month! Try again...");
							alreadyIn = true;
						}
						
					} catch (NumberFormatException  e) {
						System.err.println("The month must be a number between 1 and 12.");
						alreadyIn = true;
					}
			    	break;
			    case "c":
					num_area = 0;
					area = "";
					//areas.clear();
					if (!alreadyIn) {
						System.out.println("Please, select one area:");
					}
					areas = dataManagement.getAllAreas();
					for (int i = 0; i < areas.size(); i++) {
						System.out.println(areas.get(i)[0]);
					}
							
					area = keyboardInput.nextLine().toLowerCase();
					try {
						num_area = Integer.parseInt(area);
								
						if(num_area > 0 && num_area < dataManagement.getAmountAreas()+1){
						
							Vector<String> competitions = dataManagement.getProposalOfMoreMoney(areas.get(num_area-1)[1]);
							if (competitions.size()>0) {
								for (int i = 0; i < competitions.size(); i++) {
									System.out.println(competitions.get(i));
								}
							}else{
								System.out.println("No proposals were found for the given condition.");
							}
											
							System.out.println("Do you whish to try other operations? (No --> n/ Any other key --> yes)");
							if(keyboardInput.nextLine().toLowerCase().equals("n")){
								System.out.println("Bye bye");
								System.exit(0);
							}
							
							alreadyIn = false;
						}else{
							System.err.println("Invalid area! Try again...");
							alreadyIn = true;
						}
					} catch (NumberFormatException  e) {
						System.err.println("The area must be a number between 1 and " + dataManagement.getAmountAreas());
						alreadyIn = true;
					}
			    	break;
			    case "d":
			    	System.out.println("Please, enter a date with format yyyy-mm-dd:");
							
					date = keyboardInput.nextLine().toLowerCase();
					try {
						formatted_date = new SimpleDateFormat("yyyy-MM-dd").parse(date);  
						
						int monthStr = Integer.valueOf(date.substring(date.indexOf("-") + 1,date.indexOf("-") + 3));
						
						if(monthStr > 0 && monthStr < 13){
							
							int dayStr = Integer.valueOf(date.substring(date.indexOf("-") + 4,date.indexOf("-") + 6));
							
							if(dayStr > 0 && dayStr < 32){
								Vector<String> competitions = dataManagement.getProposalBeforeDate(new java.sql.Date(formatted_date.getTime()));
								if (competitions.size()>0) {
									for (int i = 0; i < competitions.size(); i++) {
										System.out.println(competitions.get(i));
									}
								}else{
									System.out.println("No proposals were found for the given condition.");
								}
												
								System.out.println("Do you whish to try other operations? (No --> n/ Any other key --> yes)");
								if(keyboardInput.nextLine().toLowerCase().equals("n")){
									System.out.println("Bye bye");
									System.exit(0);
								}
								alreadyIn = false;
							}
							else{
								System.err.println("The day must be between 1 and 31\n");
								alreadyIn = true;
							}
						}
						else{
							System.err.println("Month must be between 1 and 12\n");
							alreadyIn = true;
						}
					} catch (ParseException  e) {
						System.err.println("The date format is wrong. Please, use format yyyy-mm-dd\n");
						alreadyIn = true;
					}
			    	break;
			    case "e":
			    	num_area = 0;
					area = "";
					//areas.clear();
					if (!alreadyIn) {
						System.out.println("Please, select one area:");
					}
					areas = dataManagement.getAllAreas();
					for (int i = 0; i < areas.size(); i++) {
						System.out.println(areas.get(i)[0]);
					}

							
					area = keyboardInput.nextLine().toLowerCase();
					try {
						num_area = Integer.parseInt(area);
								
						if(num_area > 0 && num_area < dataManagement.getAmountAreas()+1){
							
							float discrep = dataManagement.getAverageDiscrepancyForArea(areas.get(num_area-1)[1]);
							
							System.out.println("The average of requested/awarded discrepancy for the selected area is: " + discrep);
							
							System.out.println("Do you whish to try other operations? (No --> n/ Any other key --> yes)");
							if(keyboardInput.nextLine().toLowerCase().equals("n")){
								System.out.println("Bye bye");
								System.exit(0);
							}
							alreadyIn = false;
						}else{
							System.err.println("Invalid area! Try again...");
							alreadyIn = true;
						}
					} catch (NumberFormatException  e) {
						System.err.println("The area must be a number between 1 and " + dataManagement.getAmountAreas());
						alreadyIn = true;
					}
			    	break;
			    	
			    	
			    	
			    	
			    case "f":
	            // Go to the beginning of loop and allow the loop condition to exit it.
			    	continue;
			    default:
			    	// If none of the above menu letters were selected, inform of invalid entry 
			    	// and allow the user to make a proper selection.
			        System.out.flush();  
			        
			    	System.err.println("Invalid menu entry! Try again..." + lineSeparator + lineSeparator);
			    	
			    	continue;
			}
		}

		// User selected e or E to exit the system.
		System.out.println(lineSeparator + "===========================================");
		System.out.println("The application will close now.");

		// Exit the application
		System.exit(0);
	}
	
	
	

}
