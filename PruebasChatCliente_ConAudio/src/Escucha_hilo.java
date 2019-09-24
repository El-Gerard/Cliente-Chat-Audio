import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.SourceDataLine;

//Hilo que se mantendrá abierto para enviar audio
public class Escucha_hilo extends Thread {
	
	public DatagramSocket data_in;
    public SourceDataLine audio_out;
    byte[] buffer = new byte[512];
    
    @Override
    public void run(){
        
        int i = 0;
        DatagramPacket entrada = new DatagramPacket(buffer, buffer.length);
        
        while(Cliente.calling){
            
            try {
                
                data_in.receive(entrada);
                buffer = entrada.getData();
                audio_out.write(buffer, 0, buffer.length);
                System.out.println("Datagrama enviado #"+i++);
                
            } catch (IOException ex) {
                Logger.getLogger(Escucha_hilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        audio_out.close();
        audio_out.drain();
        System.out.println("Hilo de escucha detenido!");
    } 

}
