package testrun;

import mahout.cf.Recomender;
import mahout.cf.Recomender.SimilarityType;
import service.connector.*;

import java.util.LinkedList;
import java.util.List;

import com.restfb.Version;

import dbContex.cassandra.DBContex;


public class RunTest {
	private static final String groupID=/*Testowa*/"1604918533098087"; //Dragons"1432118370353397";
	private static final String appToken ="EAAGJtcEBEQsBAAQ7yDZAEnaO3LpxT784KJuo6V7aImYb5jby8FEh2S5vjsl8On4HKZCX09DK7mZBTEY2FWPFCni7a5tN0ZBlh09rhsjG3ZAW1ttshpiDuZCpJ6ldJYIgRyOZARNMt7AVWOZAZAZBUZB8ZC8UTSNaiWAbkggnRugCBcGDdgZDZD";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Example.connectWithFacebookByToken();
//		DBContex dbInstance = DBContex.getInstance();
//		dbInstance.deleteDefaultKeysSpace();
//		dbInstance.createSchemaForTwoVer();
//		Example.connectWithFacebookByToken();
//		Recomender.craeteItemSimilarityCFRecomender(SimilarityType.EUCLIDEANDISTANCE);
		 DBContex dbContext = DBContex.getInstance();
		 dbContext.deleteDefaultKeysSpace();
		 dbContext.createSchemaForTwoVer();
		 
		
	

//		FbConnector fbconnector = new  FbConnector(appToken);
//		List<String> listOfMemebers=  fbconnector.getGroupMembersId(groupID);
//		if(listOfMemebers!= null && listOfMemebers.size()>0){
//			System.out.println(listOfMemebers.size());
//		} 
		
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
			
//			Recomender.craeteItemSimilarityCFRecomender(Recomender.SimilarityType.CITYBLOCK);
			
			//rec.craeteCFRecomender();
			//System.out.print("Recomendacja zosta³a pomyslnie wykonana");
			//dbContex.testSelectUsers();
			
			//dbContex.createSchemaForTwoVer();
			
		//}catch(Exception e){
			
		//	throw e;
		//}
	}

}
