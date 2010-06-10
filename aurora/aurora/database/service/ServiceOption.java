/*
 * Created on 2008-6-18
 */
package aurora.database.service;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class ServiceOption extends DynamicObject {
    
    public static final String KEY_AUTO_COUNT = "autocount";

    public static final String MODE_FREE_QUERY = "freequery";
    
    public static final String MODE_BY_PK = "pkquery"; 
    
    public static final String KEY_DEFAULT_WHERE_CLAUSE = "defaultwhereclause";
    
    public static final String KEY_QUERY_ORDER_BY = "queryorderby";
    
    public static final String KEY_CONNECTION_NAME = "connectionname";
    
    public static final String KEY_FIELD_CASE = "fieldcase";    
    
    public String getConnectionName() {
		return getString(KEY_CONNECTION_NAME);
	}

	public void setConnectionName(String connectionName) {
		putString(KEY_CONNECTION_NAME, connectionName);
	}

	public static ServiceOption createInstance(){
        CompositeMap map = new CompositeMap(20);
        map.setName("query-option");
        ServiceOption option = new ServiceOption();
        option.initialize(map);
        return option;
    }
    
    public ServiceOption(){
        super();
    }
    
    public String getQueryMode(){
        return getString("querymode");
    }
    
    public void setQueryMode( String mode ){
        putString("querymode", mode);
    }
    
    public boolean isFreeQuery(){
        return MODE_FREE_QUERY.equalsIgnoreCase(getQueryMode());
    }
    
    public boolean isPkQuery(){
        return MODE_BY_PK.equalsIgnoreCase(getQueryMode());
    }
    
    public boolean isAutoCount(){
        return getBoolean(KEY_AUTO_COUNT, false);
    }
    
    public void setAutoCount( boolean b){
        putBoolean(KEY_AUTO_COUNT, b);
    }   
    
    public String getQueryOrderBy(){
        return getString(KEY_QUERY_ORDER_BY);
    }
    
    public void setQueryOrderBy( String order_by ){
        putString(KEY_QUERY_ORDER_BY, order_by);
    }
    
    public String getDefaultWhereClause(){
        return getString(KEY_DEFAULT_WHERE_CLAUSE);
    }
    
    public void setDefaultWhereClause( String where ){
        putString(KEY_DEFAULT_WHERE_CLAUSE, where);
    }
    
    public byte getFieldCase(){
        return (byte)getInt(KEY_FIELD_CASE, Character.UNASSIGNED);
    }
    
    public void setFieldCase( byte field_case ){
        putInt(KEY_FIELD_CASE, field_case);
    }
}
