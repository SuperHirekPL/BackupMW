package testrun;

import mahout.cf.Recomender;
import service.connector.*;

import com.restfb.Version;

import dbContex.cassandra.DBContex;


public class RunTest {
	private static final String groupID=/*Testowa*/"1604917516431522"; //Dragons"1432118370353397";
	private static final String appToken ="CAACEdEose0cBAFKXsobLqUdVaX70Izz1k5iNdEc8T9rrPophq0o6ZCJvgeEiZBZACnPYnSwAC1tterPpadFMtIZBeCfH5ATy8tLtyA9HabaKrzPvU1fVsT6fGbyHvJ4GD4p86OnlIycTLFv43yA49NS3ZADqbhhG3LcMRcxrKLiCuO3lBScybcfN4O0qcfgRAoz0NNhpRZBgZDZD";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Example.connectWithFacebookByToken();

		
		//fbConnector.getItemRecomendationByUser(groupID);
		//fbConnector.getGroupMembersId(groupID);
		//fbConnector.getGroupPostId(groupID);
		//fbConnector.getItemRecomendationByUser(groupID);
		DBContex dbContex= new DBContex();

//		
//		//try{
//			dbContex.startDBCassandra();
//			dbContex.deleteDefaultKeysSpace();
		
			//dbContex.createSchemaForTwoVer();
//			FbConnector fbConnector = new FbConnector(appToken,Version.VERSION_2_0);
			//System.out.println("Wynik publiczeg wyszukiwania "+ fbConnector.getResultFbSearch());
			
//			System.out.println("Test"+ fbConnector.testSmapleFql());
			
			
			//System.out.print(fbConnector.getCurrentUser().getFirstName());
			
			//fbConnector.getItemRecomendationByUser(groupID);
			//dbContex.insertSampleDate();
			//System.out.print("insert ok");
			
			Recomender.craeteItemSimilarityCFRecomender(Recomender.SimilarityType.CITYBLOCK);
			
			//rec.craeteCFRecomender();
			//System.out.print("Recomendacja zosta³a pomyslnie wykonana");
			//dbContex.testSelectUsers();
			
			//dbContex.createSchemaForTwoVer();
			
		//}catch(Exception e){
			
		//	throw e;
		//}
	}

}
