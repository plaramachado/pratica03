package videoConference;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class MultiActionListener implements ActionListener{
	
	ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();

	/*
	 * Redirects to every registered actionListener
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).actionPerformed(e);
		}
	}

	public boolean add(ActionListener e) {
		return listeners.add(e);
	}

	public void remove(ActionListener e) {
		listeners.remove(e);
	}

	public boolean isEmpty() {
		return listeners.isEmpty();
	}
	
	
	

}
