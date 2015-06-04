package com.plux.cordova.bioplux;


public enum BPErrorTypes {
	OK (0,"OK"),
	BT_ADDRESS (1,"BT_ADDRESS"),
	BT_ADAPTER_NOT_FOUND (2,"BT_ADAPTER_NOT_FOUND"),
	BT_DEVICE_NOT_FOUND (3,"BT_DEVICE_NOT_FOUND"),
	CONTACTING_DEVICE (4,"CONTACTING_DEVICE"),
	PORT_COULD_NOT_BE_OPENED (5,"PORT_COULD_NOT_BE_OPENED"),
	PORT_INITIALIZATION (6,"PORT_INITIALIZATION"),
	DEVICE_NOT_IDLE (7,"DEVICE_NOT_IDLE"),
	DEVICE_NOT_IN_ACQUISITION_MODE (8,"DEVICE_NOT_IN_ACQUISITION_MODE"),
	PORT_COULD_NOT_BE_CLOSED (9,"PORT_COULD_NOT_BE_CLOSED"),
	BT_DEVICE_NOT_PAIRED (10,"BT_DEVICE_NOT_PAIRED"),
	INVALID_PARAMETER (11,"INVALID_PARAMETER"),
    INVALID_FUNCTION (12,"INVALID_FUNCTION"),
	UNDEFINED (13,"UNDEFINED ERROR");

	private final int value;
	private final String name;
	BPErrorTypes (int value, String name)
	{
	    this.value = value;
	    this.name = name;
	}
	public int getValue()
	{
	    return value;
	}
	public String getName()
	{
	    return name; 
	}
	
	public static final BPErrorTypes getType(int val)
	{
	    for (BPErrorTypes t : BPErrorTypes.values())
	    {
	        if (t.getValue()==val)
	            return t;
	    }
	    return UNDEFINED;
	}
	
	public static final BPErrorTypes getType(String val)
	{
	    for (BPErrorTypes t : BPErrorTypes.values())
	    {
	        if (t.getName().equals(val))
	            return t;
	    }
	    return UNDEFINED;
	}
	
//	public int getValuebyName(String name)
//	{
//	    if (SOURCE.getName().equals(name))
//	        return SOURCE
//	}

 
}
