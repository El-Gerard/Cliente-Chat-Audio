import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.TargetDataLine;

//Hilo que se mantiene abierto para enviar y recibir datdatagramas de audio
public class Audio_hilo extends Thread {
	
	public TargetDataLine audio_in = null; //Dato que se enviara tipo audio
    public DatagramSocket data_out; //Se crea el datagrama 
    byte byte_buffer[] = new byte[512]; // Array que contendrá la información del audio
    public InetAddress server_ip; //modo de obtener la direccion del server
    public int server_puerto; //puerto del servidor (8888)
    
    @Override
    public void run(){
        
        int i = 0;
        while(Cliente.calling){
            
            try {
                audio_in.read(byte_buffer, 0, byte_buffer.length);
                DatagramPacket data = new DatagramPacket(byte_buffer, byte_buffer.length, server_ip, server_puerto);
                System.out.println("Datagrama Nº: "+i++);
                data_out.send(data);
            } catch (IOException ex) {
                Logger.getLogger(Audio_hilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        audio_in.close();
        audio_in.drain();
        System.out.println("Hilo de audio detenido!");
    }

}
