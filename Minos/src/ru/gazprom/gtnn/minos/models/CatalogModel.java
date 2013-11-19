package ru.gazprom.gtnn.minos.models;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import com.google.common.cache.LoadingCache;

import ru.gazprom.gtnn.minos.entity.CatalogNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;

public class CatalogModel  extends BasicModel {

	public CatalogModel(DatabaseConnectionKeeper kdb,
			LoadingCache<Integer, CatalogNode> cacheCatalog,			
			String sqlLoadSubCatalogIDs,			
			String pattern) {			
		super(kdb);		
		this.cacheCatalog = cacheCatalog;
		this.sqlLoadSubCatalogIDs = sqlLoadSubCatalogIDs;
		this.pattern = pattern;
	}		
		
	@Override
	public Object getRoot() {
		if(root == null) {
			root = new CatalogNode();
			root.catalogName = null;
			root.catalogID = 0;
			root.catalogParent = 0;
			checkAndLoadSubCatalogs(root);			
		}
		return root;
	}
	
	@Override
	public boolean isLeaf(Object arg) {
		if(arg == null) 
			return true;	
		
		assert (arg instanceof CatalogNode) : "CatalogModel.isLeaf() : arg have invlaid type";
		if(!(arg instanceof CatalogNode))
			return true;		
		
		CatalogNode cn = (CatalogNode)arg;
		checkAndLoadSubCatalogs(cn);
		return cn.subCatalogs.size() == 0;
	}
	
	@Override
	public int getChildCount(Object arg) {
		if(arg == null) 
			return 0;
		
		assert (arg instanceof CatalogNode) : "CatalogModel.getChildCount() : arg have invlaid type";
		if(!(arg instanceof CatalogNode))
			return 0;

		CatalogNode cn = (CatalogNode)arg;
		checkAndLoadSubCatalogs(cn);
		
		return cn.subCatalogs.size();
	}
	
	@Override
	public Object getChild(Object arg, int index) {		
		if(arg == null)
			return null;
		
		assert (arg instanceof CatalogNode) : "CatalogModel.getChild() : arg have incorrect type";
		if(!(arg instanceof CatalogNode))
			return null;		

		CatalogNode cn = (CatalogNode)arg;
		checkAndLoadSubCatalogs(cn);

		assert ((0 <= index) && (index < cn.subCatalogs.size())) : "CatalogModel.getChild() : index out of range";
		if((index < 0) || (index >= cn.subCatalogs.size()))
			return null;		

		Object obj = null;
		try {
			obj =  cacheCatalog.get(((CatalogNode)arg).subCatalogs.get(index));
		} catch (ExecutionException e) {
			e.printStackTrace();
			obj = null;
		}
		
		return obj;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent == null || child == null)
            return -1;
		
		assert (parent instanceof CatalogNode) : "CatalogTreeModel.getIndexOfChild() : parent have incorrect object type"; 
		assert  (child instanceof CatalogNode) : "CatalogTreeModel.getIndexOfChild() : child have incorrect object type";		
		if( !( (parent instanceof CatalogNode) && (child instanceof CatalogNode) ) )
			return -1;

		CatalogNode parentNode = (CatalogNode) parent;
		CatalogNode childNode = (CatalogNode) child; 

		assert parentNode.subCatalogs != null : "CatalogTreeModel.getIndexOfChild() : parents.catalogs is null";
		assert parentNode.subCatalogs.size() != 0 : "CatalogTreeModel.getIndexOfChild() : parents.catalogs is empty";

		int result = -1;
		for(int i = 0; i < parentNode.subCatalogs.size(); i++) {
			if (parentNode.subCatalogs.get(i) == childNode.catalogID) {
				result = i;
				break;
			}					
		}

		assert result != -1 : "CatalogTreeModel.getIndexOfChild() : childNode cann't find in parentNode.catalogs";

		return result;
	}
	
	@Override
	public void reload() {		
		cacheCatalog.invalidateAll();
	}
	
	@Override
	public void add(Object obj, TreePath path) throws Exception {
		if(obj instanceof CatalogNode) {
			if(path == null)
				JOptionPane.showMessageDialog(null, "�� ������� ������� ��� �������");

			Object []nodes = path.getPath();
			if( (nodes == null) || (nodes.length == 0) )
				JOptionPane.showMessageDialog(null, "�� ������� ������� ��� �������");

			for(int i = nodes.length; i > 0; i--) {				
				if(nodes[i - 1] instanceof CatalogNode) {
					CatalogNode dest = (CatalogNode)nodes[i - 1];					
					loadSubCatalogs(dest, false);					
					int max = 1;
					if(dest.subCatalogs.size() == 0) {
						dest.subCatalogs = new ArrayList<>();
					} else {						
						for(int j = 0; j < dest.subCatalogs.size(); j++) {
							int item = cacheCatalog.get(dest.subCatalogs.get(j)).catalogItem;
							if( max <  item)
								max = item;
						}							
					}
		
					CatalogNode source = (CatalogNode)obj;
					source.catalogParent = dest.catalogID;
					source.catalogItem = max + 1;
					CatalogNode.insert(kdb, 
							CatalogNode.CATALOG_ITEM | CatalogNode.CATALOG_NAME | CatalogNode.CATALOG_PARENT | 
							CatalogNode.CATALOG_CREATE | CatalogNode.CATALOG_REMOVE, 
							source);
					break;
				}
			}			
		}
	}


	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub

	}
	
	private void checkAndLoadSubCatalogs(CatalogNode cn) {
		if (cn == null)
			return;
		if(cn.subCatalogs == null)  {
			loadSubCatalogs(cn, true);
		}
	}
	
	private void loadSubCatalogs(CatalogNode cn, boolean flagPreload) {
		if (cn == null)
			return;
		cn.subCatalogs = loadChildIDs(sqlLoadSubCatalogIDs, pattern, cn.catalogID);
		if( flagPreload && (cn.subCatalogs.size() != 0) )
			try {
				cacheCatalog.getAll(cn.subCatalogs);
			} catch (ExecutionException e) {					
				e.printStackTrace(); 
			}
	}

	private CatalogNode root = null;
	private LoadingCache<Integer, CatalogNode> cacheCatalog;
	private String sqlLoadSubCatalogIDs;
	private String pattern;
}




	
	

	


	

	
		
	