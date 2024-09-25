package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class IntermediatorRegister extends Application{
	
	private int port;
	private String name;

	// Criar página de login do intermediador
    private Parent createLogin() throws AlreadyBoundException {
    	
    	Pane root = new Pane();
    	
    	BackgroundFill backgroundFill = new BackgroundFill(Color.valueOf("#6194F5"), new CornerRadii(10), new Insets(10));

    	Background background = new Background(backgroundFill);
    	
    	root.setBackground(background);
    	
    	root.setPrefSize(504, 322);
    	
    	Label intermediatorTitle = new Label("LOGIN - INTERMEDIADOR");
    	intermediatorTitle.setFont(new Font("Monaco",36));
    	intermediatorTitle.setLayoutX(45);
    	intermediatorTitle.setLayoutY(15);
    	
    	Label title = new Label("Insira o nome do intermediador e a porta em que \n ele irá operar. Logo após clique \n no botão para iniciá-lo.");
    	title.setFont(new Font("Arial",18));
    	title.setLayoutX(60);
    	title.setLayoutY(80);
    	title.setTextAlignment(TextAlignment.CENTER);
    	
    	Label name = new Label("Nome :");
    	name.setFont(new Font("Arial",13));
    	name.setLayoutX(95);
    	name.setLayoutY(175);
    	
    	TextField nameInput = new TextField("Intermediator");
    	nameInput.setLayoutX(145);
    	nameInput.setLayoutY(170);
    	nameInput.setMinWidth(220);
    	
    	Label port = new Label("Porta  :");
    	port.setFont(new Font("Arial",13));
    	port.setLayoutX(95);
    	port.setLayoutY(225);
    	
    	TextField portInput = new TextField("6000");
    	portInput.setLayoutX(145);
    	portInput.setLayoutY(220);
    	portInput.setMinWidth(220);
    	
    	Button loginButton = new Button("Iniciar Intermediador");
    	loginButton.setLayoutX(180);
    	loginButton.setLayoutY(270);
    	loginButton.setMinWidth(150);
    	loginButton.setOnAction(event -> {
    		this.name = nameInput.getText();
    		this.port = Integer.valueOf(portInput.getText());
        	Stage window = (Stage)loginButton.getScene().getWindow();
        	Scene scene = new Scene(createScene());
        	window.setScene(scene);
        	window.setResizable(false);
        	
			try {
				// Registrar o intermediador no servidor de nomes
				Registry rmiRegistry = LocateRegistry.createRegistry(this.port); // Número da porta
				String IP = null;
				
				try {
					IP = InetAddress.getLocalHost().getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				rmiRegistry.bind(this.name, new Intermediator(portInput.getText(),IP));
			} catch (RemoteException | AlreadyBoundException  e) {
				e.printStackTrace();
			}
			System.out.println("Intermediador está funcionando...");
        });
    	
    	root.getChildren().addAll(intermediatorTitle, title, name, nameInput, port, portInput, loginButton);
    	
    	return root;
    }
	
    // Criar página "Intermediador Online"
	private Parent createScene() {
		
    	Pane root = new Pane();
    	
    	BackgroundFill backgroundFill = new BackgroundFill(Color.valueOf("#5CDB95"), new CornerRadii(10), new Insets(10));

    	Background background = new Background(backgroundFill);
    	
    	root.setBackground(background);
    	
    	root.setPrefSize(504, 322);
    	              	
    	Label intermediator = new Label("INTERMEDIADOR ONLINE");
    	intermediator.setFont(new Font("Monaco",36));
    	intermediator.setLayoutX(50);
    	intermediator.setLayoutY(55);
    	intermediator.setTextAlignment(TextAlignment.CENTER);
    	
    	String intermediatorName = String.format("NOME: %s", this.name.toUpperCase());
    	Label intermediatorNameLabel = new Label(intermediatorName);
    	intermediatorNameLabel.setFont(new Font("Monaco",20));
    	intermediatorNameLabel.setLayoutX(100);
    	intermediatorNameLabel.setLayoutY(145);
    	
    	String intermediatorPort = String.format("PORTA: %d", this.port);
    	Label intermediatorPortLabel = new Label(intermediatorPort);
    	intermediatorPortLabel.setFont(new Font("Monaco",20));
    	intermediatorPortLabel.setLayoutX(100);
    	intermediatorPortLabel.setLayoutY(205);
    	intermediatorPortLabel.setTextAlignment(TextAlignment.CENTER);
  	
    	root.getChildren().addAll(intermediator, intermediatorNameLabel, intermediatorPortLabel);
    	
    	return root;
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Scene loginScene = new Scene(createLogin());
			primaryStage.setTitle("Intermediador");;
			primaryStage.setScene(loginScene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static void main(String[] args) throws Exception{
		launch(args);
	}

}
