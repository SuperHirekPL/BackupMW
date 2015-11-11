/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dbContex.cassandra;

//import com.google.common.base.Preconditions;
//import me.prettyprint.cassandra.model.HColumnImpl;
//import me.prettyprint.cassandra.serializers.BytesArraySerializer;
//import me.prettyprint.cassandra.serializers.FloatSerializer;
//import me.prettyprint.cassandra.serializers.LongSerializer;
//import me.prettyprint.cassandra.service.OperationType;
//import me.prettyprint.hector.api.Cluster;
//import me.prettyprint.hector.api.ConsistencyLevelPolicy;
//import me.prettyprint.hector.api.HConsistencyLevel;
//import me.prettyprint.hector.api.Keyspace;
//import me.prettyprint.hector.api.beans.ColumnSlice;
//import me.prettyprint.hector.api.beans.HColumn;
//import me.prettyprint.hector.api.factory.HFactory;
//import me.prettyprint.hector.api.mutation.Mutator;
//import me.prettyprint.hector.api.query.ColumnQuery;
//import me.prettyprint.hector.api.query.CountQuery;
//import me.prettyprint.hector.api.query.SliceQuery;
import org.apache.cassandra.thrift.ColumnSlice;
import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.hadoop.item.PrefAndSimilarityColumnWritable;
import org.apache.mahout.cf.taste.impl.common.Cache;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.common.Retriever;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.common.base.Preconditions;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * A {@link DataModel} based on a Cassandra keyspace. By default it uses
 * keyspace "recommender" but this can be configured. Create the keyspace before
 * using this class; this can be done on the Cassandra command line with a
 * command linke {@code create keyspace recommender;}.
 * </p>
 *
 * <p>
 * Within the keyspace, this model uses four column families:
 * </p>
 *
 * <p>
 * First, it uses a column family called "users". This is keyed by the user ID
 * as an 8-byte long. It contains a column for every preference the user
 * expresses. The column name is item ID, again as an 8-byte long, and value is
 * a floating point value represnted as an IEEE 32-bit floating poitn value.
 * </p>
 *
 * <p>
 * It uses an analogous column family called "items" for the same data, but
 * keyed by item ID rather than user ID. In this column family, column names are
 * user IDs instead.
 * </p>
 *
 * <p>
 * It uses a column family called "userIDs" as well, with an identical schema.
 * It has one row under key 0. IT contains a column for every user ID in th
 * emodel. It has no values.
 * </p>
 *
 * <p>
 * Finally it also uses an analogous column family "itemIDs" containing item
 * IDs.
 * </p>
 *
 * <p>
 * Each of these four column families needs to be created ahead of time. Again
 * the Cassandra CLI can be used to do so, with commands like
 * {@code create column family users;}.
 * </p>
 *
 * <p>
 * Note that this thread uses a long-lived Cassandra client which will run until
 * terminated. You must {@link #close()} this implementation when done or the
 * JVM will not terminate.
 * </p>
 *
 * <p>
 * This implementation still relies heavily on reading data into memory and
 * caching, as it remains too data-intensive to be effective even against
 * Cassandra. It will take some time to "warm up" as the first few requests will
 * block loading user and item data into caches. This is still going to send a
 * great deal of query traffic to Cassandra. It would be advisable to employ
 * caching wrapper classes in your implementation, like
 * {@link org.apache.mahout.cf.taste.impl.recommender.CachingRecommender} or
 * {@link org.apache.mahout.cf.taste.impl.similarity.CachingItemSimilarity}.
 * </p>
 */
public final class TestCassandraDataModel implements DataModel, Closeable {

	/** Default Cassandra host. Default: localhost */
	private static final String DEFAULT_HOST = "localhost";

	/** Default Cassandra port. Default: 9160 */
	private static final int DEFAULT_PORT = 9160;

	/** Default Cassandra keyspace. Default: recommender */
	private static final String DEFAULT_KEYSPACE = "recommenderTest";

	static final String USERS_CF = "users";
	static final String ITEMS_CF = "items";
	static final String USER_IDS_CF = "userIDs";
	static final String ITEM_IDS_CF = "itemIDs";
	private static final long ID_ROW_KEY = 0L;
	private static final byte[] EMPTY = new byte[0];

	private static Session session;
	private final Cluster cluster;
	private final String keyspace;
	private Cache<Long, PreferenceArray> userCache;
	private Cache<Long, PreferenceArray> itemCache;
	private Cache<Long, FastIDSet> itemIDsFromUserCache;
	private Cache<Long, FastIDSet> userIDsFromItemCache;
	private AtomicReference<Integer> userCountCache;
	private AtomicReference<Integer> itemCountCache;
	private HashMap<Long, Integer> userNumPrefCache;
	private FastIDSet userItemsCache;
	private HashMap<Long,PreferenceArray> prefMapCache;

	/**
	 * Uses the standard Cassandra host and port (localhost:9160), and keyspace
	 * name ("recommender").
	 */
	public TestCassandraDataModel() {
		this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_KEYSPACE);
	}

	/**
	 * @param host
	 *            Cassandra server host name
	 * @param port
	 *            Cassandra server port
	 * @param keyspaceName
	 *            name of Cassandra keyspace to use
	 */
	public TestCassandraDataModel(String host, int port, String keyspaceName) {

		Preconditions.checkNotNull(host);
		keyspace = keyspaceName;
		Preconditions.checkArgument(port > 0, "port must be greater then 0!");
		Preconditions.checkNotNull(keyspace);

		cluster = Cluster.builder().addContactPoint(host).build();
		session = cluster.connect(keyspace);

		// keyspace = HFactory.createKeyspace(keyspaceName, cluster);
		// keyspace.setConsistencyLevelPolicy(new OneConsistencyLevelPolicy());

		userCache = new Cache<Long, PreferenceArray>(
				new UserPrefArrayRetriever(), 1 << 20);
		itemCache = new Cache<Long, PreferenceArray>(
				new ItemPrefArrayRetriever(), 1 << 20);
		itemIDsFromUserCache = new Cache<Long, FastIDSet>(
				new ItemIDsFromUserRetriever(), 1 << 20);
		userIDsFromItemCache = new Cache<Long, FastIDSet>(
				new UserIDsFromItemRetriever(), 1 << 20);
		userCountCache = new AtomicReference<Integer>(null);
		itemCountCache = new AtomicReference<Integer>(null);
		userNumPrefCache = new HashMap<Long, Integer>();
		userItemsCache = new FastIDSet();
		prefMapCache = new HashMap<Long, PreferenceArray>();

	}

	@Override
	public LongPrimitiveIterator getUserIDs() {
		ResultSet result = session
				.execute("SELECT * FROM " + USER_IDS_CF + ";");
		FastIDSet userIDs = new FastIDSet();
		Iterator<Row> iter = result.iterator();
		while (iter.hasNext()) {
			userIDs.add(iter.next().getLong(0));
		}
		result.iterator();
		return userIDs.iterator();
	}

	@Override
	public PreferenceArray getPreferencesFromUser(long userID)
			throws TasteException {
		return userCache.get(userID);
	}

	@Override
	public FastIDSet getItemIDsFromUser(long userID) throws TasteException {

		ResultSet result = session.execute("SELECT itemId FROM " + USERS_CF
				+ " where userid= " + userID + " ALLOW FILTERING;");
		FastIDSet fastIDSet = new FastIDSet();

		Iterator<Row> iter = result.iterator();
		Row row = null;
		while (iter.hasNext()) {
			row = iter.next();
			fastIDSet.add(row.getLong(0));
		}
		return fastIDSet;
	}

	@Override
	public LongPrimitiveIterator getItemIDs() {
		ResultSet result = null;
		if (userItemsCache == null || userItemsCache.size() == 0) {
			result = session.execute("SELECT itemId FROM " + ITEM_IDS_CF + ";");
			Iterator<Row> iter = result.iterator();
			while (iter.hasNext()) {
				userItemsCache.add(iter.next().getLong(0));
			}
		}
		return userItemsCache.iterator();
	}

	@Override
	public PreferenceArray getPreferencesForItem(long itemID)
			throws TasteException {
			if(prefMapCache.containsKey(itemID) && prefMapCache.get(itemID).hasPrefWithItemID(itemID)){ 
				return prefMapCache.get(itemID);
			}				
			List<Preference> listPref = new ArrayList<Preference>();
			ResultSet result = session.execute("SELECT * FROM " + USERS_CF
					+ " WHERE itemid = " + itemID + " ALLOW FILTERING;");
			
			Iterator<Row> iter = result.iterator();
			while (iter.hasNext()) {
				Row row = iter.next();
				listPref.add(new GenericPreference(row.getLong(0),itemID,row.getFloat(2)));
			}
			PreferenceArray prefArray = new GenericItemPreferenceArray(listPref);
			prefMapCache.put(itemID, prefArray);
			return prefArray;
		
	}

	@Override
	public Float getPreferenceValue(long userID, long itemID) {
		ResultSet result = session.execute("SELECT * FROM " + USERS_CF
				+ " WHERE userId= " + userID + " && " + "itemId= " + itemID
				+ ";");
		return result.one().getFloat(2);
	}

	@Override
	public Long getPreferenceTime(long userID, long itemID) {
		// ColumnQuery<Long,Long,?> query =
		// HFactory.createColumnQuery(keyspace, LongSerializer.get(),
		// LongSerializer.get(), BytesArraySerializer.get());
		// query.setColumnFamily(USERS_CF);
		// query.setKey(userID);
		// query.setName(itemID);
		// HColumn<Long,?> result = query.execute().get();
		// return result == null ? null : result.getClock();
		return null;
	}

	@Override
	public int getNumItems() {
		Integer itemCount = itemCountCache.get();
		if (itemCount == null) {
			ResultSet result = session.execute("SELECT id FROM " + ITEM_IDS_CF
					+ ";");
			itemCount = result.all().size();
			itemCountCache.set(itemCount);
		}
		return itemCount;
	}

	@Override
	public int getNumUsers() {
		Integer userCount = itemCountCache.get();
		if (userCount == null) {
			ResultSet result = session.execute("SELECT id FROM " + USER_IDS_CF
					+ ";");
			userCount = result.all().size();
			userCountCache.set(userCount);
		}
		return userCount;
	}

	@Override
	public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
		// /do poprawy ?

		if (!userNumPrefCache.containsKey(itemID)) {
			ResultSet result = session.execute("SELECT * FROM " + USERS_CF
					+ " WHERE itemid= " + itemID + " ALLOW FILTERING");
			userNumPrefCache.put(itemID, result.all().size());
		}
		return userNumPrefCache.get(itemID);
	}

	@Override
	public int getNumUsersWithPreferenceFor(long itemID1, long itemID2)
			throws TasteException {
		FastIDSet userIDs1 = userIDsFromItemCache.get(itemID1);
		FastIDSet userIDs2 = userIDsFromItemCache.get(itemID2);
		return userIDs1.size() < userIDs2.size()?
				userIDs2.intersectionSize(userIDs1) 
				: userIDs1.intersectionSize(userIDs2);
	}

	@Override
	public void setPreference(long userID, long itemID, float value) {

		if (Float.isNaN(value)) {
			value = 1.0f;
		}

		session.execute("INSERT INTO items (itemid,userid,value) VALUES ("
				+ userID + "," + itemID + "," + value + ");");

		session.execute("INSERT INTO users (userid,itemid,value) VALUES ("
				+ userID + "," + itemID + "," + value + ");");

		session.execute("INSERT INTO userids (id, userid) VALUES ("
				+ ID_ROW_KEY + "," + userID + ");");

		session.execute("INSERT INTO itemids (id, itemID) VALUES ("
				+ ID_ROW_KEY + "," + itemID + ");");

		// mutator.execute();
	}

	@Override
	public void removePreference(long userID, long itemID) {
		session.execute("Delete From " + USERS_CF + " where userids =" + userID
				+ " && itemID=" + itemID);

		session.execute("Delete From " + ITEMS_CF + " where userids =" + userID
				+ " && itemID=" + itemID + ";");
		// Not deleting from userIDs, itemIDs though
	}

	/**
	 * @return true
	 */
	@Override
	public boolean hasPreferenceValues() {
		return true;
	}

	/**
	 * @return Float#NaN
	 */
	@Override
	public float getMaxPreference() {
		return Float.NaN;
	}

	/**
	 * @return Float#NaN
	 */
	@Override
	public float getMinPreference() {
		return Float.NaN;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
		userCache.clear();
		itemCache.clear();
		userIDsFromItemCache.clear();
		itemIDsFromUserCache.clear();
		userCountCache.set(null);
		itemCountCache.set(null);
	}

	@Override
	public String toString() {
		return "CassandraDataModel[" + keyspace + ']';
	}

	@Override
	public void close() {
		// HFactory.shutdownCluster(cluster);
		session.close();
		cluster.close();
	}

	private final class UserPrefArrayRetriever implements
			Retriever<Long, PreferenceArray> {
		@Override
		public PreferenceArray get(Long userID) throws TasteException {
			ResultSet result = session.execute("SELECT * FROM " + USERS_CF
					+ " WHERE userids = " + userID + ";");

			Iterator<Row> iter = result.iterator();

			int size = result.all().size();

			PreferenceArray prefs = new GenericItemPreferenceArray(size);
			int ind = 0;
			Row row = null;
			while (iter.hasNext()) {
				ind = ind++;
				row = iter.next();
				prefs.setItemID(ind, row.getLong(1));
				prefs.setValue(ind, row.getFloat(2));
			}

			return prefs;
		}
	}

	private final class ItemPrefArrayRetriever implements
			Retriever<Long, PreferenceArray> {
		@Override
		public PreferenceArray get(Long itemID) throws TasteException {
			ResultSet result = session.execute("SELECT * FROM " + ITEMS_CF
					+ " WHERE itemId=" + itemID + ";");
			if (result == null) {
				throw new NoSuchItemException(itemID);
			}
			Iterator<Row> iter = result.iterator();

			// int size =result.all().size();

			PreferenceArray prefs = new GenericItemPreferenceArray(result
					.getColumnDefinitions().size());
			int ind = 0;
			while (iter.hasNext()) {
				ind = ind++;
				Row row = iter.next();
				prefs.setUserID(ind, row.getLong(1));
				prefs.setValue(ind, row.getFloat(2));

			}
			// int size = userIDColumns.size();
			//
			// prefs.setItemID(0, itemID);
			// for (int i = 0; i < size; i++) {
			// HColumn<Long,Float> userIDColumn = userIDColumns.get(i);
			// prefs.setUserID(i, userIDColumn.getName());
			// prefs.setValue(i, userIDColumn.getValue());
			// }
			return prefs;
		}
	}

	// /moteoda do sprawdzenia. Sprzawdz czy ma byc USER_CF, bo
	private final class UserIDsFromItemRetriever implements
			Retriever<Long, FastIDSet> {
		@Override
		public FastIDSet get(Long itemID) throws TasteException {

			ResultSet result = session.execute("Select userid FROM " + ITEMS_CF
					+ " where itemId=" + itemID + " ALLOW FILTERING;");
			if (result == null) {
				throw new NoSuchItemException(itemID);
			}
			Iterator<Row> iter = result.iterator();
			// int size =result.all().size();
			// do poprawy
			FastIDSet userIDs = new FastIDSet();
			Row row = null;
			while (iter.hasNext()) {
				row = iter.next();
				userIDs.add(row.getLong(0));
			}
			return userIDs;
		}
	}

	// /moteoda do sprawdzenia.
	private final class ItemIDsFromUserRetriever implements
			Retriever<Long, FastIDSet> {
		@Override
		public FastIDSet get(Long userID) throws TasteException {
			// /session.execute("use recomender");
			ResultSet result = session.execute("Select itemiD FROM " + USERS_CF
					+ " where userId=" + userID + " ALLOW FILTERING;");
			if (result == null) {
				throw new NoSuchUserException(userID);
			}
			Iterator<Row> iter = result.iterator();

			// int size =result.all().size();

			FastIDSet itemIDs = new FastIDSet();
			// while(iter.hasNext()){
			// itemIDs.add(iter.next().getLong(0));
			// }
			while (iter.hasNext()) {
				itemIDs.add(iter.next().getLong(0));
			}
			return itemIDs;
		}
	}

}
