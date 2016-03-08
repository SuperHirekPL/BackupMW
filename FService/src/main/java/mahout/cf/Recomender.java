package mahout.cf;

import java.util.List;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import dbContex.cassandra.DBContex;
import dbContex.cassandra.TestCassandraDataModel;

public class Recomender {

	public static void craeteItemSimilarityCFRecomender(SimilarityType type){
		//127.0.0.1:9042
		TestCassandraDataModel dm;
			
			int x=1;
			try {
				//dm =new CassandraDataModel("127.0.0.1",9042,"recomender");
				dm = (TestCassandraDataModel) DBContex.getDefaultDataModel();
				ItemSimilarity sim;
				
				switch(type) {
				case CITYBLOCK:
					sim = new CityBlockSimilarity(dm);
					 break;
				case EUCLIDEANDISTANCE:
					sim = new EuclideanDistanceSimilarity(dm);
					 break;
				case LOGLIKELIHOOD:
					sim = new LogLikelihoodSimilarity(dm);
					 break;
				case PEARSONCORRELATION:
					sim = new PearsonCorrelationSimilarity(dm);
					 break;
				case TANIMOTOCOEFFICIENT:
					sim = new TanimotoCoefficientSimilarity(dm);
					 break;
				case UNCENTEREDCOSINE:
					sim = new UncenteredCosineSimilarity(dm);
					break;
				default:
					sim = new LogLikelihoodSimilarity(dm);
					break;
				}
				GenericItemBasedRecommender recomender= new GenericItemBasedRecommender(dm, sim);
				
				for(LongPrimitiveIterator item=dm.getItemIDs();item.hasNext();){
					
					long itemId= item.nextLong();
					List<RecommendedItem>recommendedList=recomender.mostSimilarItems(itemId, 1);
					for(RecommendedItem recommended:recommendedList){
						System.out.println(itemId+","+recommended.getItemID()+","+recommended.getValue());
					}
					x++;
					if(x>3) break;
				}
			} catch (TasteException e) {
				System.out.println("B³ad przy iteracji");
				e.printStackTrace();
			}

	}

	public static void createCFCosineBasedSimilarity(){
		craeteItemSimilarityCFRecomender(SimilarityType.UNCENTEREDCOSINE);
	}
	
	//CityBlockSimilarity, EuclideanDistanceSimilarity, LogLikelihoodSimilarity, PearsonCorrelationSimilarity, TanimotoCoefficientSimilarity, UncenteredCosineSimilarity
	public enum SimilarityType{
		CITYBLOCK,EUCLIDEANDISTANCE,LOGLIKELIHOOD,PEARSONCORRELATION,TANIMOTOCOEFFICIENT,UNCENTEREDCOSINE
	}
}
