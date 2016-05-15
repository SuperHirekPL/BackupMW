package dbContex.cassandra;

import java.util.Iterator;
import java.util.List;

import org.apache.mahout.cf.taste.model.DataModel;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class DBContex {

	private static Cluster cluster;
	private static Session session;
	private static Metadata metadata;
	private static DBContex dbContex; 
	
	private static DataModel cassndraDbModel;
	
	private static final String LOCAL_CONNECTION = "127.0.0.1";
	
	private static final String LOCAL_PORT_TO_CASSANDRA = "9042";

	public final static String KEYSPACE = "recomender";

	public final static String USERS_TABLE = "users";
	public final static String ITEMS_TABLE = "items";
	public final static String ITEMSIDS_TABLE = "itemids";
	public final static String USERSIDS_TABLE = "userids";

	public final static String USERS_COLUMN_USERID = "userid";
	public final static String USERS_COLUMN_ITEMID = "itemid";
	public final static String USERS_COLUMN_VALUE = "value";

	public final static String ITEMS_COLUMN_ITEMID = "itemid";
	public final static String ITEMS_COLUMN_USERID = "userid";
	public final static String ITEMS_COLUMN_VALUE = "value";

	public final static String USERIDS_COLUMN_ID = "id";
	public final static String USERIDS_COLUMN_USERID = "userid";

	public final static String ITEMSIDS_COLUMN_ID = "id";
	public final static String ITEMSIDS_COLUMN_ITEMID = "itemid";

	//powinnien byc pywatny konsturkotro singletona
	private DBContex() {
	}
	
	
	public static DBContex getInstance(){
		if(dbContex == null){ 
			dbContex = new DBContex();
			startDBCassandra();
		} 
		return dbContex; 
	}

	public static  void connect(String node) {
		cluster = Cluster.builder().addContactPoint(node).build();
		 metadata = cluster.getMetadata();
//		System.out.printf("Connected to cluster: %s\n",
//				metadata.getClusterName());
//		for (Host host : metadata.getAllHosts()) {
//			System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n",
//					host.getDatacenter(), host.getAddress(), host.getRack());
//		}
		if (metadata.getKeyspace(KEYSPACE) == null) {
			session = cluster.connect();
		} else {
			session = cluster.connect(KEYSPACE);
		}
	}

	public void close() {
		getSession().close();
		cluster.close();
	}

	public void createSchema() {
		try {
			session.execute("CREATE KEYSPACE IF NOT EXISTS recomender WITH replication "
					+ "= {'class':'SimpleStrategy', 'replication_factor':3};");
			// wstawiamy kolumne uzytkownika
			session.execute("CREATE COLUMN FAMILY users"
					+ " WITH comparator = LongType"
					+ " AND key_validation_class=LongType"
					+ " AND default_validation_class=FloatType");
			// wstawiamy testowe dane do uzytkownika
//			session.execute("set users[0][0]='1.0'; "
//					+ "set users[1][0]='3.0';" + "set users[2][2]='1.0';");
			// wstwawiamy kolumne przedmity
			session.execute("CREATE COLUMN FAMILY items "
					+ "WITH comparator = LongType"
					+ "AND key_validation_class=LongType "
					+ "AND default_validation_class=FloatType;");
			// wstawiamy testowe przedmioty
//			session.execute("set items[0][0]='1.0';" + "set items[0][1]='3.0';"
//					+ "set items[2][2]='1.0';");
			// /wstwaimy kolumne userIds
			session.execute("CREATE COLUMN FAMILY userIDs"
					+ "WITH comparator = LongType "
					+ "AND key_validation_class=LongType;");
			// wstawiamy testowe userIDs
//			session.execute("set userIDs[0][0]=''; " + "set userIDs[0][1]=''; "
//					+ "set userIDs[0][2]='';");
			// wstawwamy kolumne itemIds
			session.execute("CREATE COLUMN FAMILY itemIDs"
					+ " WITH comparator = LongType  "
					+ "AND key_validation_class=LongType;");
			// wstwaimay testowe itemids
//			session.execute("set itemIDs[0][0]=''; "
//					+ " set itemIDs[0][1]=''; " + "set itemIDs[0][2]='';");
		} catch (Exception e) {
			// throw e;
		}
	}

	public void deleteDefaultKeysSpace(){
		if (cluster.getMetadata().getKeyspace(KEYSPACE)!=null){ 
			getSession().execute("DROP KEYSPACE "+ KEYSPACE);
		}
	}
	
	public void  deleteKeySpace(String _keySpace){
		if (cluster.getMetadata().getKeyspace(_keySpace)!=null){ 
			getSession().execute("DROP KEYSPACE"+ _keySpace);
		}
	}
	
	// /Sprawdziæ literówki;
	public void createSchemaForTwoVer() {
		try {
			getSession()
					.execute(
							"CREATE KEYSPACE IF NOT EXISTS "
									+ KEYSPACE
									+ " WITH replication "
									+ "= {'class':'SimpleStrategy', 'replication_factor':3};");
			getSession().execute("use " + KEYSPACE + ";");
			getSession()
					.execute(
							"CREATE TABLE users (userID bigint, itemID bigint, value float, PRIMARY KEY(userID, itemID));");

			getSession()
					.execute(
							"CREATE TABLE items (itemID bigint, userID bigint, value float, PRIMARY KEY(itemID, userID));");
			// getSession().execute("INSERT INTO users (userID, itemID, value) VALUES (1, 1, 10);");
			getSession()
					.execute(
							"CREATE TABLE userIDs (id bigint, userID bigint, PRIMARY KEY(id, userID));");
			getSession()
					.execute(
							"CREATE TABLE itemIDs (id bigint, itemID bigint, PRIMARY KEY(id, itemID));");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertSampleDate() {
		// sample data for users
		getSession().execute(
				"INSERT INTO users (userid,itemid,value) VALUES (1, 1, 1.0);");
		getSession().execute(
				"INSERT INTO users (userid,itemid,value) VALUES (2, 2, 2.0);");
		getSession().execute(
				"INSERT INTO users (userid,itemid,value) VALUES (3, 3, 3.0);");
		// sample data for items
		getSession().execute(
				"INSERT INTO items (itemid,userid,value) VALUES (1, 1, 1.0);");
		getSession().execute(
				"INSERT INTO items (itemid,userid,value) VALUES (2, 2, 2.0);");
		getSession().execute(
				"INSERT INTO items (itemid,userid,value) VALUES (3, 3, 3.0);");
		// sample data for userIDs
		getSession().execute("INSERT INTO userids (id, userid) VALUES (1, 1);");
		getSession().execute("INSERT INTO userids (id, userid) VALUES (2, 2);");
		getSession().execute("INSERT INTO userids (id, userid) VALUES (3, 3);");
		// sample data for itemIDs
		getSession().execute("INSERT INTO itemids (id, itemID) VALUES (1, 1);");
		getSession().execute("INSERT INTO itemids (id, itemID) VALUES (2, 2);");
		getSession().execute("INSERT INTO itemids (id, itemID) VALUES (3, 3);");

	}

	public void executeInDb(String steatment) {
		getSession().execute(steatment);
	}

	public static void startDBCassandra() {
		connect(LOCAL_CONNECTION);
	}

	// Region to getter and setter

	public Session getSession() {
		if (session == null) {
			if (cluster == null) {
				startDBCassandra();
			}
		} else if (session.isClosed()) {
			session = cluster.connect(KEYSPACE);
		}
		return session;
	}

	public long getLengthFromTable(String table) {
		long tempSize = 0;
		ResultSet rs = null;

		switch (table) {
		case USERS_TABLE:
			rs = getSession()
					.execute(
							"SELECT DISTINCT " + USERS_COLUMN_USERID + " FROM "
									+ table);
			break;
		case ITEMS_TABLE:
			rs = getSession().execute(
					"SELECT DISTINCT " + ITEMSIDS_COLUMN_ITEMID + " FROM "
							+ table);
			break;
		case USERSIDS_TABLE:
			rs = getSession().execute(
					"SELECT DISTINCT " + USERIDS_COLUMN_ID + " FROM " + table);
			break;
		case ITEMSIDS_TABLE:
			rs = getSession().execute(
					"SELECT DISTINCT " + ITEMSIDS_COLUMN_ID + " FROM " + table);
		default:
			break;
		}
		tempSize = rs.all().size();
		return tempSize;
	}

	public void insertIntoUsers(String _userid, String _itemid, String _value) {
		getSession().execute(
				"INSERT INTO " + USERS_TABLE
						+ " (userid, itemid, value) VALUES (" + _userid + ","
						+ _itemid + "," + _value + ")");
	}

	public void insertIntoItems(String _itemid, String _userid, String _value) {
		getSession().execute(
				"INSERT INTO " + ITEMS_TABLE + " " + "(" + ITEMS_COLUMN_ITEMID
						+ "," + ITEMS_COLUMN_USERID + "," + ITEMS_COLUMN_VALUE
						+ ") " + "VALUES(" + _itemid + "," + _userid + ","
						+ _value + ")");
	}
	

	public void insertIntoUserIDs(String _id, String _userid) {
		// getSession().execute("use "+KEYSPACE +";");
		getSession().execute(
				"INSERT INTO " + USERSIDS_TABLE + " (" + USERIDS_COLUMN_ID
						+ "," + USERIDS_COLUMN_USERID + ") VALUES (" + _id
						+ "," + _userid + ");");
	}

	public void insertIntoItemsIDs(String _id, String _itemid) {
		getSession().execute(
				"INSERT INTO " + ITEMSIDS_TABLE + " (" + ITEMSIDS_COLUMN_ID
						+ "," + ITEMSIDS_COLUMN_ITEMID + ") VALUES (" + _id
						+ "," + _itemid + ");");
	}
	
	public void insertBatchStatement(String statement, int size){
		getSession().execute(
				"BEGIN UNLOGGED BATCH "+
				insertStatement(statement,size)+				
				"APPLY BATCH;");
		
	}
	private String insertStatement(String statmenet, int size){
		String result="";
		for(int i=0; i<size;i++){
			result=result+statmenet;
		}
		return result;
	}
	
	public static DataModel getDefaultDataModel(){
		if(cassndraDbModel==null){
			cassndraDbModel= new TestCassandraDataModel(LOCAL_CONNECTION, Integer.parseInt(LOCAL_PORT_TO_CASSANDRA), KEYSPACE);
		} 
	 return cassndraDbModel;
	} 
	
	
	

}
