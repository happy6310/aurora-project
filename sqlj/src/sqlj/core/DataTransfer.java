package sqlj.core;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sqlj.exception.NoDataFoundException;
import sqlj.exception.TooManyRowsException;
import sqlj.parser.SqljParser;

public class DataTransfer {
	/**
	 * transfer one row into given type object
	 * 
	 * @param clazz
	 * @param rs
	 * @return
	 * @throws Exception
	 *             if <b>no_data_found</b> or <b>too_many_rows</b> or any others
	 *             exception
	 */
	public static <T> T transfer1(Class<T> clazz, ResultSet rs)
			throws Exception {
		if (!rs.next())
			throw new NoDataFoundException();
		List<String> column_names = getColumnNames(rs);
		T res = null;
		if (Map.class.isAssignableFrom(clazz)) {
			Map map = null;
			if (!clazz.isInterface()) {
				map = (Map) clazz.newInstance();
			} else
				map = new HashMap();
			fillMap(map, rs, column_names);
			res = (T) map;
		} else if (clazz == Long.class) {

		} else {
			Object bean = clazz.newInstance();
			fillBean(bean, rs, column_names);
			res = (T) bean;
		}
		if (rs.next())
			throw new TooManyRowsException();
		return res;
	}

	public static Map transfer1(Map map, ResultSet rs, List<String> column_names)
			throws Exception {
		if (!rs.next())
			throw new NoDataFoundException();
		fillMap(map, rs, column_names);
		if (rs.next())
			throw new TooManyRowsException();
		return map;
	}

	public static List<String> getColumnNames(ResultSet rs) throws SQLException {
		List<String> column_names = new ArrayList<String>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int c = rsmd.getColumnCount();
		for (int i = 1; i <= c; i++)
			column_names.add(rsmd.getColumnName(i));
		return column_names;
	}

	public static void fillMap(Map map, ResultSet rs, List<String> column_names)
			throws Exception {
		for (String name : column_names) {
			map.put(name.toLowerCase(), rs.getObject(name));
		}
	}

	public static void fillBean(Object bean, ResultSet rs,
			List<String> column_names) throws IllegalArgumentException,
			IllegalAccessException, SQLException {
		Field[] flds = bean.getClass().getFields();
		List<Class<?>> supported_type_list = Arrays
				.asList(SqljParser.supported_types);
		ArrayList<String> acceptedColumns = new ArrayList<String>();
		for (Field f : flds) {
			if (supported_type_list.contains(f.getType())) {
				String upperName = f.getName().toUpperCase();
				if (column_names.contains(upperName)) {
					f.set(bean, verboseGet(rs, upperName, f.getType()));
					acceptedColumns.add(upperName);
				}
			}
		}
		if (bean instanceof Dynamic
				&& acceptedColumns.size() < column_names.size()) {
			for (String e : column_names) {
				if (!acceptedColumns.contains(e)) {
					((Dynamic) bean).set(e, rs.getObject(e));
				}
			}
		}
		if (bean instanceof Initable) {
			((Initable) bean).init();
		}
	}

	public static Object verboseGet(ResultSet rs, String name, Class<?> type)
			throws SQLException {
		if (type == String.class)
			return getString(rs, name);
		if (type == double.class)
			return getDouble(rs, name);
		if (type == java.sql.Date.class)
			return getDate(rs, name);
		if (type == int.class)
			return getInt(rs, name);
		if (type == long.class)
			return getLong(rs, name);
		if (type == Integer.class)
			return getInt_(rs, name);
		if (type == Long.class)
			return getLong_(rs, name);
		if (type == Double.class)
			return getDouble_(rs, name);
		if (type == java.util.Date.class)
			return getUtilDate(rs, name);
		return rs.getObject(name);
	}

	public static BigDecimal getBigDecimal(ResultSet rs, String name)
			throws SQLException {
		return rs.getBigDecimal(name);
	}

	public static int getInt(ResultSet rs, String name) throws SQLException {
		return rs.getInt(name);
	}

	public static Integer getInt_(ResultSet rs, String name)
			throws SQLException {
		BigDecimal bd = rs.getBigDecimal(name);
		if (bd == null)
			return null;
		return bd.intValue();
	}

	public static long getLong(ResultSet rs, String name) throws SQLException {
		return rs.getLong(name);
	}

	public static Long getLong_(ResultSet rs, String name) throws SQLException {
		BigDecimal bd = rs.getBigDecimal(name);
		if (bd == null)
			return null;
		return bd.longValue();
	}

	public static double getDouble(ResultSet rs, String name)
			throws SQLException {
		return rs.getDouble(name);
	}

	public static Double getDouble_(ResultSet rs, String name)
			throws SQLException {
		BigDecimal bd = rs.getBigDecimal(name);
		if (bd == null)
			return null;
		return bd.doubleValue();
	}

	public static String getString(ResultSet rs, String name)
			throws SQLException {
		return rs.getString(name);
	}

	public static java.util.Date getUtilDate(ResultSet rs, String name)
			throws SQLException {
		java.sql.Date d = rs.getDate(name);
		if (d == null)
			return null;
		return new java.util.Date(d.getTime());
	}

	public static java.sql.Date getDate(ResultSet rs, String name)
			throws SQLException {
		return rs.getDate(name);
	}

	public static Timestamp getTimestamp(ResultSet rs, String name)
			throws SQLException {
		return rs.getTimestamp(name);
	}

	/**
	 * 
	 * @param obj
	 *            <table border='0'>
	 *            <tr><td>Number</td><td>Number.longValue()</td></tr>
	 *            <tr><td>String</td><td>Long.parseLong(String)</td></tr>
	 *            <tr><td>null</td><td>0</td></tr>
	 *            <tr><td><b>others</b></td><td>NumberFormatException</td></tr>
	 *            </table>
	 * @return
	 */
	public static long castLong(Object obj) {
		if (obj instanceof Number)
			return ((Number) obj).longValue();
		if (obj instanceof String)
			return Long.parseLong((String) obj);
		if (obj == null)
			return 0L;
		throw new NumberFormatException(obj + " is not a number.");
	}

	/**
	 * {@link #castLong(Object)}
	 * 
	 * @param obj
	 * @return
	 */
	public static int castInt(Object obj) {
		return (int) castLong(obj);
	}

}
