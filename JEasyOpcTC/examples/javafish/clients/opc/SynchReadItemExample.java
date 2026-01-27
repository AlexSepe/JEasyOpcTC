package javafish.clients.opc;

import java.util.GregorianCalendar;
import java.util.stream.Collectors;

import javafish.clients.opc.component.OpcGroup;
import javafish.clients.opc.component.OpcItem;
import javafish.clients.opc.exception.ComponentNotFoundException;
import javafish.clients.opc.exception.ConnectivityException;
import javafish.clients.opc.exception.SynchReadException;
import javafish.clients.opc.exception.UnableAddGroupException;
import javafish.clients.opc.exception.UnableAddItemException;
import javafish.clients.opc.variant.Variant;
import javafish.clients.opc.variant.VariantList;

public class SynchReadItemExample {
	
  public static void main(String[] args) throws InterruptedException, ComponentNotFoundException, SynchReadException {
    SynchReadItemExample test = new SynchReadItemExample();
    
    JOpc.coInitialize();
    
    JOpc jopc = new JOpc("localhost", "Matrikon.OPC.Simulation", "JOPC1");

    OpcItem item1 = new OpcItem("Random.Int4", true, "");
    OpcItem item2 = new OpcItem("Random.ArrayOfReal8", true, "");
    OpcItem item3 = new OpcItem("Random.ArrayOfString", true, "");
    
    //OpcItem item1 = new OpcItem("Random.Real8", true, "");
    //OpcItem item1 = new OpcItem("Random.String", true, "");
    
    OpcGroup group = new OpcGroup("group1", true, 500, 0.0f);
    
    group.addItem(item1);
    group.addItem(item2);
    group.addItem(item3);
    jopc.addGroup(group);
        
    try {
      jopc.connect();
      System.out.println("JOPC client is connected...");
    }
    catch (ConnectivityException e2) {
      e2.printStackTrace();
    }
    
    try {
      jopc.registerGroups();
      System.out.println("OPCGroup are registered...");
    }
    catch (UnableAddGroupException e2) {
      e2.printStackTrace();
    }
    catch (UnableAddItemException e2) {
      e2.printStackTrace();
    }
    
       
    
//    synchronized(test) {
//      test.wait(50);
//    }
    
    // Synchronous reading of item
    int cycles = 7;
    int acycle = 0;
//    while (acycle++ < cycles) {
//      synchronized(test) {
//        test.wait(1000);
//      }
      
      try {
    	 // GregorianCalendar c = GregorianCalendar.init;
    	  
        OpcItem responseItem = jopc.synchReadItem(group, item1);
        System.out.println(responseItem);
        System.out.println(Variant.getVariantName(responseItem.getDataType()) + ": " + responseItem.getValue());
        
        responseItem = jopc.synchReadItem(group, item2);
        System.out.println(responseItem);
        System.out.println(Variant.getVariantName(responseItem.getDataType()) + ": " + responseItem.getValue());
        
        responseItem = jopc.synchReadItem(group, item3);
        System.out.println(responseItem);
        System.out.println(Variant.getVariantName(responseItem.getDataType()) + ": " + responseItem.getValue());
        Variant varout = responseItem.getValue();
        if ((varout.getVariantType() & Variant.VT_ARRAY) == Variant.VT_ARRAY) {
        	VariantList varlist = varout.getArray();  
//        	String result = varlist.stream()
//        			.map(Object::toString)
//        			.collect(Collectors.joining("','"));
        	System.out.println(varlist);

        }
      }
      catch (ComponentNotFoundException e1) {
        e1.printStackTrace();
      }
      catch (SynchReadException e) {
        e.printStackTrace();
      }
    //}    
      
    JOpc.coUninitialize();
  }
}
