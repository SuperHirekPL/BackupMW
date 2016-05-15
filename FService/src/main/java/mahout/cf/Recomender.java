package mahout.cf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.common.Weighting;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import dbContex.cassandra.DBContex;
import dbContex.cassandra.TestCassandraDataModel;
import service.model.Recomendation;

public class Recomender {
	private static TestCassandraDataModel dmDefault = (TestCassandraDataModel) DBContex.getDefaultDataModel();

	public static List<String> craeteItemSimilarityCFRecomender(SimilarityType type){
		//127.0.0.1:9042
		List<String> recomendationIdList = new ArrayList<String>();
			
			int x=1;
			try {
				//dm =new CassandraDataModel("127.0.0.1",9042,"recomender");

				ItemSimilarity sim;

				switch(type) {
				case CITYBLOCK:
					sim = new CityBlockSimilarity(dmDefault);
					 break;
				case EUCLIDEANDISTANCE:
					sim = new EuclideanDistanceSimilarity(dmDefault);
					 break;
				case LOGLIKELIHOOD:
					sim = new LogLikelihoodSimilarity(dmDefault);
					 break;
				case PEARSONCORRELATION:
					sim = new PearsonCorrelationSimilarity(dmDefault);
					 break;
				case TANIMOTOCOEFFICIENT:
					sim = new TanimotoCoefficientSimilarity(dmDefault);
					 break;
				case UNCENTEREDCOSINE:
					sim = new UncenteredCosineSimilarity(dmDefault);
					
					break;
				default:
					sim = new LogLikelihoodSimilarity(dmDefault);
					break;
				}
				
				
				GenericItemBasedRecommender recomender= new GenericItemBasedRecommender(dmDefault, sim);

				
				
				for(LongPrimitiveIterator item=dmDefault.getItemIDs();item.hasNext();){
					
					long itemId= item.nextLong();
					List<RecommendedItem>recommendedList=recomender.mostSimilarItems(itemId, 1);
					Collections.sort(recommendedList,getCompByValue());
					
					
					for(RecommendedItem recommended:recommendedList){
						
//						System.out.println(itemId+","+recommended.getItemID()+","+recommended.getValue());
						recomendationIdList.add(Long.toString(recommended.getItemID()));
					}
					x++;
					if(x>20) break;
				}
			} catch (TasteException e) {
				System.out.println("B³ad przy iteracji");
				e.printStackTrace();
			}
			return recomendationIdList;

	}
	
	public static Comparator<RecommendedItem> getCompByValue() {
		Comparator<RecommendedItem> comp = new Comparator<RecommendedItem>() {


			@Override
			public int compare(RecommendedItem o1, RecommendedItem o2) {
				float change1 = o1.getValue();
				float change2 = o2.getValue();
				if (change1 < change2) return -1;
				if (change1 > change2) return 1;
				return 0;
				
			}
		};
		return comp;
	}
	

	public static void createCFCosineBasedSimilarity(){
		craeteItemSimilarityCFRecomender(SimilarityType.UNCENTEREDCOSINE);
	}
	
		
	//CityBlockSimilarity, EuclideanDistanceSimilarity, LogLikelihoodSimilarity, PearsonCorrelationSimilarity, TanimotoCoefficientSimilarity, UncenteredCosineSimilarity
	public enum SimilarityType{
		CITYBLOCK,EUCLIDEANDISTANCE,LOGLIKELIHOOD,PEARSONCORRELATION,TANIMOTOCOEFFICIENT,UNCENTEREDCOSINE
	}
	
}
