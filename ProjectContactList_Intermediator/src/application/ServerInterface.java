package application;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

// Interface do servidor
public interface ServerInterface extends Remote{
	
	public List<Contact> returnContactList() throws RemoteException;
	
	public Boolean addNewContact(Contact contact) throws RemoteException;
	
	public Boolean updateContact(Contact oldContact, Contact newContact) throws RemoteException;
	
	public void deleteContact(Contact contact) throws RemoteException;

}
