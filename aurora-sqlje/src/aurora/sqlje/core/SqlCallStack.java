package aurora.sqlje.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import aurora.sqlje.core.database.DatabaseDescriptor;
import aurora.sqlje.core.database.IDatabaseDescriptor;
import uncertain.composite.CompositeMap;

public class SqlCallStack implements ISqlCallStack {
	private CompositeMap contextData;
	private FastStack<Connection> connectionStack = new FastStack<Connection>();
	private Map<Connection, ArrayList<Object>> resourceMap = new HashMap<Connection, ArrayList<Object>>();

	private DataSource dataSource;
	private DatabaseDescriptor dbDesriptor;

	public SqlCallStack(DataSource ds, Connection initConnection)
			throws SQLException {
		super();
		if (initConnection == null)
			throw new NullPointerException("initConnection can't be null.");
		this.dataSource = ds;
		dbDesriptor = new DatabaseDescriptor();
		dbDesriptor.init(initConnection.getMetaData());
		connectionStack.push(initConnection);
	}

	@Override
	public Connection getCurrentConnection() throws SQLException {
		return connectionStack.peek();
	}

	@Override
	public Connection createConnection() throws SQLException {
		return createConnection(true);
	}

	@Override
	public Connection createConnection(boolean man) throws SQLException {
		Connection conn = dataSource.getConnection();
		conn.setAutoCommit(false);
		if (man)
			connectionStack.push(conn);
		return conn;
	}

	@Override
	public void push(ResultSet rs) {
		pushInternal(rs);
	}

	@Override
	public void push(Statement stmt) {
		pushInternal(stmt);
	}

	private void pushInternal(Object res) {
		Connection currentConnection = connectionStack.peek();
		ArrayList<Object> list = resourceMap.get(currentConnection);
		if (list == null) {
			list = new ArrayList<Object>();
			resourceMap.put(currentConnection, list);
		}
		list.add(res);
	}

	@Override
	public void free(Connection conn) throws SQLException {
		free(conn, true);
	}

	public void free(Connection conn, boolean close) throws SQLException {
		if (close && getCurrentConnection() == conn)
			connectionStack.pop();
		ArrayList<Object> list = resourceMap.get(conn);
		if (list != null) {
			for (Object o : list) {
				try {
					if (o instanceof Statement)
						((Statement) o).close();
					else if (o instanceof ResultSet)
						((ResultSet) o).close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			list.clear();
		}

		if (close) {
			resourceMap.remove(conn);
			conn.close();
		}
	}

	@Override
	public CompositeMap getContextData() {
		return contextData;
	}

	@Override
	public void setContextData(CompositeMap data) {
		this.contextData = data;
	}

	@Override
	public void cleanUp() throws SQLException {
		Connection conn = getCurrentConnection();
		if (conn != null)
			free(conn);
	}

	@Override
	public void commit() throws SQLException {
		Connection conn = getCurrentConnection();
		if (conn != null) {
			conn.commit();
		}
	}

	@Override
	public void rollback() throws SQLException {
		Connection conn = getCurrentConnection();
		if (conn != null) {
			conn.rollback();
		}
	}

	@Override
	public IDatabaseDescriptor getDatabaseDescriptor() {
		return dbDesriptor;
	}

}
