import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Cliente {

	public static boolean calling = false;

	// String IpServer;
	// ----------------------Clase principal
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MarcoCliente mimarco = new MarcoCliente();
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}// ----------------------------------**
}

//-----------------------------------Clase para dibujar la ventana
class MarcoCliente extends JFrame {

	public MarcoCliente() {

		setBounds(600, 300, 280, 350);
		LaminaMarcoCliente milamina = new LaminaMarcoCliente();
		add(milamina);
		setVisible(true);
		addWindowListener(new EnvioOnline());
	}
}// -------------------------------------------------------------**

// ----------------------------------Envio de señal onlain

// Clase para ejecutar una orden en cuanto se abre la ventana de ejecución
class EnvioOnline extends WindowAdapter {
	// Método que permitirá ejecutar una orden en cuando se abra la ventana

	public void windowOpened(WindowEvent e) {

		// Solicitud de la IP del servidor
		String IPE = JOptionPane.showInputDialog("iP servidor ");

		try {
			Socket miSocket = new Socket(IPE, 9999);
			Paquete datos = new Paquete();
			// Mensaje distintivo para indicarle al server que es un nuevo cliente
			datos.setMensaje(" online");
			ObjectOutputStream paquete_datos = new ObjectOutputStream(miSocket.getOutputStream());
			paquete_datos.writeObject(datos);
			miSocket.close();

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
		}

	}
}// ---------------------------------------------------------------------------**

//--------------------------------------------Creación de los elementos de la ventana
class LaminaMarcoCliente extends JPanel implements Runnable {

	// private String IpServer;
	private JTextField campo1;
	private JComboBox ip;
	private JLabel nick;
	private JTextArea campochat;
	private JButton miboton, btn_start, btn_stop, btn_escucha;
	EnvioOnline on;
	TargetDataLine audio_in;
	public SourceDataLine audio_out;
	public int port_server = 8888;
	// ----public String add_server = "127.0.0.1";

	// -------------------------------------Elementos de la ventana

	public LaminaMarcoCliente() {

		// ----------------
		// IpServer = JOptionPane.showInputDialog("iP servidor ");
		// on.recibir(JOptionPane.showInputDialog("iP servidor "));
		// ----------------
		// Solicitud del nombre de usuario
		String nick_usuario = JOptionPane.showInputDialog("Nombre de usuario: ");
		JLabel n_nick = new JLabel("Usuario: ");
		add(n_nick);
		nick = new JLabel();
		nick.setText(nick_usuario);
		add(nick);
		JLabel texto = new JLabel("Online: ");
		add(texto);
		ip = new JComboBox();
		add(ip);
		campochat = new JTextArea(12, 20);
		add(campochat);
		campo1 = new JTextField(20);
		add(campo1);
		miboton = new JButton("Enviar");
		EnviaTexto miEvento = new EnviaTexto();
		miboton.addActionListener(miEvento);
		add(miboton);
		// ----Botón de start
		btn_start = new JButton("Audio");
		EnviarAudio EnvioAudio = new EnviarAudio();
		btn_start.addActionListener(EnvioAudio);
		add(btn_start);
		// -----**
		// -------Botón Stop
		btn_stop = new JButton("Detener");
		StopAudio PararAudio = new StopAudio();
		btn_stop.addActionListener(PararAudio);
		add(btn_stop);
		// --------**
		// -------Botón escucha
		btn_escucha = new JButton("Escuchar");
		Escuchar esc = new Escuchar();
		btn_escucha.addActionListener(esc);
		add(btn_escucha);
		// --------**
		Thread mihilo = new Thread(this);
		mihilo.start();
	}// -------------------------------------------------------**

	// ----------------------------------Acciones del botón START
	private class EnviarAudio implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			System.out.println("iniciando botón start...");
			iniciar_audio();
		}
	}// ----------------------------------------------------**

	// -----------------------------Acciones del boton STOP
	private class StopAudio implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			System.out.println("iniciando botón de detener...");
			Cliente.calling = false;
			btn_start.setEnabled(true);
			btn_stop.setEnabled(false);
		}
	}// --------------------------------------------------**

	// ---------------------------------Acciones boton ESCUCHA
	private class Escuchar implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			System.out.println("Iniciado botón de escuchar...");
			iniciar_sonido();
		}
	}// ----------------------------------------------------**

	// ----------------------------------------------------Accion de boton enviar
	private class EnviaTexto implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// System.out.println(campo1.getText());

			campochat.append("\n" + campo1.getText());
			try {
				// ****************Instrucciones de socket que estaban en localhost, ahora
				// selecciona una ip concreta
				Socket miSocket = new Socket(ip.getSelectedItem().toString(), 9999);

				Paquete datos = new Paquete();
				// SE envia el nombre al atributo nick capturado del TextArea
				datos.setNick(nick.getText());
				// Se envia el atributoo ip
				datos.setIp(ip.getSelectedItem().toString());
				// Se envia el atributo mensaje
				datos.setMensaje(campo1.getText());
				// Se crea un flujo de salida de datos (se enviaran objetos)
				ObjectOutputStream paquete_datos = new ObjectOutputStream(miSocket.getOutputStream());
				// Se envia el objeto
				paquete_datos.writeObject(datos);
				// Se cierra el soquet we
				miSocket.close();

				// Se debe serializar la clase: Convertir el objeto a bytes para ser enviado por
				// la red

				// DataOutputStream flujo_salida = new
				// DataOutputStream(miSocket.getOutputStream());

				// flujo_salida.writeUTF(campo1.getText());

				// flujo_salida.close();

			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			}
		}
	}// ---------------------------------------------------------------**

	// ------------------------------------------Clase para dar formato al audio
	public static AudioFormat getaudioformat() {

		float sampleRate = 8000.0F;
		int sampleSizeInbits = 16;
		int channel = 2;
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed, bigEndian);
	}// -------------------------------------------------------**

	// ---------------------------------------------------Clase que ejecuta el
	// encender micro
	public void iniciar_audio() {
		try {
			AudioFormat format = getaudioformat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("not suport");
				System.exit(0);
			}
			audio_in = (TargetDataLine) AudioSystem.getLine(info);
			audio_in.open(format);
			audio_in.start();
			Audio_hilo r = new Audio_hilo();
			InetAddress inet = InetAddress.getByName(ip.getSelectedItem().toString());
			r.audio_in = audio_in;
			r.data_out = new DatagramSocket();
			r.server_ip = inet;
			r.server_puerto = port_server;
			Cliente.calling = true;
			r.start();
			btn_start.setEnabled(false);
			btn_stop.setEnabled(true);

		} catch (LineUnavailableException | UnknownHostException | SocketException ex) {
			System.out.println("Exception de audio");
		}
	}// ------------------------------------------------------------------**
	
		// -----------------------------------------Método para encender sonido
	public void iniciar_sonido() {

		try {
			AudioFormat format = getaudioformat();
			DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);

			if (!AudioSystem.isLineSupported(info_out)) {

				System.out.println("not suport");
				System.exit(0);
			}
			audio_out = (SourceDataLine) AudioSystem.getLine(info_out);
			audio_out.open(format);
			audio_out.start();
			Escucha_hilo p = new Escucha_hilo();
			p.data_in = new DatagramSocket(port_server);
			p.audio_out = audio_out;
			Cliente.calling = true;
			p.start();
			btn_escucha.setEnabled(false);

		} catch (LineUnavailableException | SocketException ex) {
			System.out.println("Error al ejecutar sonido");
		}
	}// --------------------------------------------------------------**

	// --------------------------------------------Hilo nativo para ejecutar las
	// conexiones
	@Override
	public void run() {

		try {

			ServerSocket servidor_cliente = new ServerSocket(9090);
			// Es posible usar el mismo socket pa no joder tanto
			Socket cliente;
			// Instancia de la clase de paquete para desglozar sus artibutos
			Paquete paqueteRecibido;
			// Permanece a la escucha indefinidamente
			while (true) {
				// Acepta las conexiones
				cliente = servidor_cliente.accept();
				// Se crea un flujo de entrada de datos
				ObjectInputStream flujoentrada = new ObjectInputStream(cliente.getInputStream());
				// Se iguala la instancia de clase al objeto que recibe de parte del servidor
				paqueteRecibido = (Paquete) flujoentrada.readObject();

				// System.out.println("Soy el cliente y tengo esto:
				// "+paqueteRecibido.getMensaje());

				if (!paqueteRecibido.getMensaje().equals(" online")) {

					// Se imprime la información obtenida por atributos
					campochat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());

				} else {

					// campochat.append("\n" + paqueteRecibido.getIps());
					// Array para guardar las direcciones ip
					ArrayList<String> IpsMenu = new ArrayList<String>();
					// se guardan las direcciones acumuladas
					IpsMenu = paqueteRecibido.getIps();
					// Limpia el ComboBox de direcciones antes de agregar
					ip.removeAllItems();
					// Ciclo para agregar las direcciones de conectados al comboBox
					for (String z : IpsMenu) {

						ip.addItem(z);
					}
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}// -------------------------------------------------------**
}// --------------------------------------------------------------***
