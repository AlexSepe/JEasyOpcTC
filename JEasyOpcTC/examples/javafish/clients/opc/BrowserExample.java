package javafish.clients.opc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafish.clients.opc.browser.JOpcBrowser;
import javafish.clients.opc.exception.CoInitializeException;
import javafish.clients.opc.exception.CoUninitializeException;
import javafish.clients.opc.exception.ConnectivityException;
import javafish.clients.opc.exception.HostException;
import javafish.clients.opc.exception.NotFoundServersException;
import javafish.clients.opc.exception.UnableAddGroupException;
import javafish.clients.opc.exception.UnableAddItemException;
import javafish.clients.opc.exception.UnableBrowseBranchException;
import javafish.clients.opc.exception.UnableBrowseLeafException;
import javafish.clients.opc.exception.UnableIBrowseException;

class OpcNode {
    int level;
    String parent;
    boolean isLeaf;

    // campos gerais
    String rawName;

    // campos decodificados (apenas se leaf)
    String fullItemName;
    String itemType;
    String itemName;

    OpcNode(String rawName, String parent, int level, boolean isLeaf) {
        this.rawName = rawName;
        this.parent = parent;
        this.level = level;
        this.isLeaf = isLeaf;

        if (isLeaf) {
            decodeLeafName(rawName);
        }
    }

    private void decodeLeafName(String rawName) {
        String[] parts = rawName.split(";");
        if (parts.length == 3) {
            this.fullItemName = parts[2].trim();
            this.itemType = parts[1].trim();
            this.itemName = parts[0].trim();
        } else {
            // fallback: não está no formato esperado
            this.fullItemName = rawName;
            this.itemType = "";
            this.itemName = "";
        }
    }

    @Override
    public String toString() {
        if (isLeaf) {
            return String.format("Level=%d, Parent=%s, Leaf=true, fullItemName=%s, itemType=%s, itemName=%s",
                    level, parent, fullItemName, itemType, itemName);
        } else {
            return String.format("Level=%d, Parent=%s, Leaf=false, Branch=%s",
                    level, parent, rawName);
        }
    }
}


public class BrowserExample {


    public static void main(String[] args) {
        try {
            JOpcBrowser.coInitialize();
        } catch (CoInitializeException e1) {
            e1.printStackTrace();
        }

        JOpcBrowser jbrowser = new JOpcBrowser("localhost", "Matrikon.OPC.Simulation", "FlatBrowser");

        try {
            jbrowser.connect();

            List<OpcNode> flatList = new ArrayList<>();
            browseRecursive(jbrowser, "", null, 0, flatList);

            // imprime lista flat
            flatList.forEach(System.out::println);

            JOpcBrowser.coUninitialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Função recursiva que popula uma lista flat com branches e leaves.
     */
    private static void browseRecursive(JOpcBrowser jbrowser,
                                        String branchPath,
                                        String parentName,
                                        int level,
                                        List<OpcNode> flatList) {
    	try {
            String[] items = jbrowser.getOpcItems(branchPath, false);
            if (items != null) {
                for (String item : items) {
                    flatList.add(new OpcNode(item, parentName, level, true));
                }
            }
        } catch (UnableBrowseLeafException | UnableIBrowseException |
                 UnableAddGroupException | UnableAddItemException ex) {
            ex.printStackTrace();
        }
    	
        try {
            // tenta buscar sub-branches
            String[] branches = jbrowser.getOpcBranch(branchPath);

            if (branches != null) {
                for (String branch : branches) {
                    String fullPath = branchPath.isEmpty() ? branch : branchPath + "." + branch;

                    flatList.add(new OpcNode(branch, parentName, level, false));

                    // chamada recursiva para explorar sub-branches
                    browseRecursive(jbrowser, fullPath, branch, level + 1, flatList);
                }
            }
        } catch (UnableBrowseBranchException | UnableIBrowseException e) {
            // se não conseguiu navegar, significa que não há mais branches
            // então tentamos buscar os items (leaves)
            
        }
    }



	
  public static void main2(String[] args) {
    try {
      JOpcBrowser.coInitialize();
    }
    catch (CoInitializeException e1) {
      e1.printStackTrace();
    }
    
    // find opc-servers (OpcEnum interface)
    try {
      String[] opcServers = JOpcBrowser.getOpcServers("localhost");
      System.out.println(Arrays.asList(opcServers));
    }
    catch (HostException e1) {
      e1.printStackTrace();
    }
    catch (NotFoundServersException e1) {
      e1.printStackTrace();
    }
    
    JOpcBrowser jbrowser = new JOpcBrowser("localhost", "Matrikon.OPC.Simulation", "JOPCBrowser1");    
    try {
      jbrowser.connect();
      
      String[] branches = jbrowser.getOpcBranch("");
      System.out.println(String.join(",", branches));
      
      String[] branches2 = jbrowser.getOpcBranch("Simulation Items");
      System.out.println(String.join(",", branches2));      
    }
    catch (ConnectivityException e) {
      e.printStackTrace();
    }
    catch (UnableBrowseBranchException e) {
      e.printStackTrace();
    }
    catch (UnableIBrowseException e) {
      e.printStackTrace();
    }
    
    try {
      String[] items = jbrowser.getOpcItems("Simulation Items.Random", false);
      if (items != null) {
        for (int i = 0; i < items.length; i++) {
          System.out.println(items[i]);
        }
      }
      // disconnect server
      JOpcBrowser.coUninitialize();
    }
    catch (UnableBrowseLeafException e) {
      e.printStackTrace();
    }
    catch (UnableIBrowseException e) {
      e.printStackTrace();
    }
    catch (UnableAddGroupException e) {
      e.printStackTrace();
    }
    catch (UnableAddItemException e) {
      e.printStackTrace();
    }
    catch (CoUninitializeException e) {
      e.printStackTrace();
    }
  }

}
