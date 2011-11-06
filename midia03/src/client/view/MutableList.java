package client.view;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class MutableList extends JList {
    public MutableList() {
    	super(new DefaultListModel());
    }
    
    public DefaultListModel getContents() {
    	return (DefaultListModel)getModel();
    }
}   
