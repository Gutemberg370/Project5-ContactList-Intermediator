package application;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

//Implementação da interface de conexão RMI do intermediador
public class Intermediator extends UnicastRemoteObject implements IntermediatorInterface{
	
	private String port;
	private String IP;
	private List<String>users = new ArrayList<>();
	private List<String>servers = new ArrayList<>();
	

	protected Intermediator(String port, String IP) throws RemoteException {
		super();
		this.port = port;
		this.IP = IP;
	}

	// Registrar cliente na entidade intermediadora
	public void registerNewClient(String name) throws RemoteException {
		users.add(name);		
	}

	// Registrar servidor na entidade intermediadora
	public void registerNewServer(String name) throws RemoteException {
		
		// Servidor já registrado
		for(int i = 0; i < this.servers.size(); i++) {
			if(this.servers.get(i).equals(name)) {
				return;
			}
		}
		
		// Servidor não registrado
		servers.add(name);	
	}
	
	// Verificar se o servidor está online
	public Boolean isServerRunning(int serverNumber) throws RemoteException{
		try {
			ServerInterface serverConnection = (ServerInterface) Naming.lookup(String.format("rmi://%s:%s/%s", this.IP, this.port, this.servers.get(serverNumber)));
			serverConnection.returnContactList();
			return true;
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			return false;
		}
	}
	
	// Atualizar a lista de contatos de um servidor com outra lista de outro servidor
	public List<Contact> updateServerContactList(String serverName) throws RemoteException {
		
		for(int i = 0; i < this.servers.size(); i++) {
			try {
				if(!this.servers.get(i).equals(serverName)) {
					ServerInterface serverConnection = (ServerInterface) Naming.lookup(String.format("rmi://%s:%s/%s", this.IP, this.port, this.servers.get(i)));
					return serverConnection.returnContactList();
				}
			} catch (MalformedURLException | RemoteException | NotBoundException e) {}
		}
		
		return new ArrayList<>();
		
	}

	// Retornar a lista de contatos armazenados em um servidor
	public List<Contact> getContactList(int serverNumber) throws RemoteException {
		
		try {
			if(serverNumber < this.servers.size()) {
				ServerInterface serverConnection = (ServerInterface) Naming.lookup(String.format("rmi://%s:%s/%s", this.IP, this.port, this.servers.get(serverNumber)));
				return serverConnection.returnContactList();
			}
			return null;
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			return null;
		}
		
	}
	
	// Adicionar novo contato para todos os clientes e servidores conectados
	public Boolean addNewContact(Contact newContact) {
		
		Boolean Result = false;
		
		// Adicionar novo contato para todos os servidores
		for(int i = 0; i < this.servers.size(); i++) {
			try {
				ServerInterface serverConnection = (ServerInterface) Naming.lookup(String.format("rmi://%s:%s/%s", this.IP, this.port, this.servers.get(i)));
				Result = serverConnection.addNewContact(newContact);
			} catch (MalformedURLException | RemoteException | NotBoundException e) {}
			// Caso resultado seja positivo, significa que o contato já existe na
			// lista do servidor, então a adição finaliza
			if(Result) {
				return Result;
			}
		}
		
		// Adicionar novo contato para todos os clientes
		for(int i = 0; i < this.users.size(); i++) {
		
			try {
				UserInterface userConnection = (UserInterface) Naming.lookup(String.format("rmi://%s:%s/%s", this.IP, this.port, this.users.get(i)));
				Result = userConnection.addNewContact(newContact);
			} catch (MalformedURLException | RemoteException | NotBoundException e) {
				this.users.remove(i);
			}
			// Caso resultado seja positivo, significa que o contato já existe na
			// lista do cliente, então a adição finaliza
			if(Result) {
				return Result;
			}
		}
		
		return Result;
	}

	// Atualizar contato para todos os clientes e servidores conectados
	public Boolean updateContact(Contact oldContact, Contact newContact) throws RemoteException {
		
		Boolean Result = false;
		
		// Atualizar contato para todos os servidores
		for(int i = 0; i < this.servers.size(); i++) {
			try {
				ServerInterface serverConnection = (ServerInterface) Naming.lookup(String.format("rmi://%s:%s/%s", this.IP, this.port, this.servers.get(i)));
				Result = serverConnection.updateContact(oldContact, newContact);
			} catch (MalformedURLException | RemoteException | NotBoundException e) {}
			
			// Caso resultado seja positivo, significa que o nome do contato modificado
			// choca com o nome de um dos contatos da lista do servidor
			if(Result) {
				return Result;
			}
		}
		
		// Atualizar contato para todos os clientes
		for(int i = 0; i < this.users.size(); i++) {
			try {
				UserInterface userConnection = (UserInterface) Naming.lookup(String.format("rmi://%s:%s/%s", this.IP, this.port, this.users.get(i)));
				userConnection.updateContact(oldContact, newContact);
			} catch (MalformedURLException | RemoteException | NotBoundException e) {
				this.users.remove(i);
			}
			
			// Caso resultado seja positivo, significa que o nome do contato modificado
			// choca com o nome de um dos contatos da lista do cliente
			if(Result) {
				return Result;
			}
			
		}
		
		return Result;		
	}

	// Deletar contato para todos os clientes e servidores conectados
	public void deleteContact(Contact contact) throws RemoteException {
		
		// Deletar contato para todos os servidores
		for(int i = 0; i < this.servers.size(); i++) {
			try {
				ServerInterface serverConnection = (ServerInterface) Naming.lookup(String.format("rmi://%s:%s/%s", this.IP, this.port, this.servers.get(i)));
				serverConnection.deleteContact(contact);
			} catch (MalformedURLException | RemoteException | NotBoundException e) {}
		}
		
		// Deletar contato contato para todos os clientes
		for(int i = 0; i < this.users.size(); i++) {
				try {
				UserInterface userConnection = (UserInterface) Naming.lookup(String.format("rmi://%s:%s/%s", this.IP, this.port, this.users.get(i)));
				userConnection.deleteContact(contact);
			} catch (MalformedURLException | RemoteException | NotBoundException e) {
				this.users.remove(i);
			}
		}
		
	}

}
