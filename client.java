import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class client extends JFrame{
    //private static final Component JScrollPane = null;

    Socket socket;

    BufferedReader br;
    PrintWriter out;

    private JLabel heading= new JLabel("Client Area");
    private JTextArea messageArea= new JTextArea();
    private JTextField messageInput=new JTextField();
    private Font font= new Font("Roboto",Font.PLAIN,20); 


    public client(){


        try {
             System.out.println("sending request to server");
             socket = new Socket("127.0.0.1", 7778);
             System.out.println("connection done");

             br=new BufferedReader(new InputStreamReader(socket.
             getInputStream()));
             out = new PrintWriter(socket.getOutputStream());             
            createGUI();
            handleevents();
            startReading();
            // startWriting();
            
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }
    private void handleevents() {
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
                //throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
            }

            @Override
            public void keyPressed(KeyEvent e) {
               // System.out.println("key pressed "+ e.getKeyCode());    
            }

            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println("key released "+ e.getKeyCode()); 
                if(e.getKeyCode()==10)
                {
                    String Contenttosend=messageInput.getText();
                    out.println(Contenttosend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                    messageArea.append("me:"+Contenttosend+"\n");
                }
            }
            
        } );
    }
    private void createGUI(){
        this.setTitle("Client Message[End]");
        this.setSize(600,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
       String imagePath = "/Users/mohitvats/Desktop/java program/chat application/user.png";
        System.out.println("Checking image at: " + imagePath);

    // Load and scale the image
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            int newWidth = 40;  // Set your desired width
            int newHeight = 40; // Set your desired height
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            heading.setIcon(scaledIcon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        this.setLayout(new BorderLayout());
        this.add(heading,BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        this.add(scrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);





        this.setVisible(true);
    }
     private void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started...");
            try {
                while (true) {
                    String msg = br.readLine(); // Declare and assign msg within the loop
                    if (msg == null) {
                        System.out.println("Server disconnected.");
                        JOptionPane.showMessageDialog(this, "Server disconnected.");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    if (msg.equals("exit")) {
                        System.out.println("Server has stopped");
                        JOptionPane.showMessageDialog(this, "Server terminated");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    // Use SwingUtilities.invokeLater for thread safety
                    SwingUtilities.invokeLater(() -> messageArea.append("Server: " + msg + "\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(r1).start();
    }
    
    private void startWriting() {
       
        //thread- data user lega and the send krega client tak
        Runnable r2=()->{
            System.out.println("writer started");
            try
            {
                while (true && !socket.isClosed()) 
                {
                    
                    BufferedReader br1= new BufferedReader
                    (new InputStreamReader(System.in));
                    String content = br1.readLine();
                    messageArea.append("me :" + content);
                    out.println(content);
                    out.flush();

                    if (content.equals("exit")) {
                        socket.close();
                        break;
                        
                    }
                
                }

                
            } 
            catch (Exception e) 
            {
                // TODO: handle exception
                e.printStackTrace();
            }
        };
        new Thread(r2).start();
    }
    public static void main(String[] args) {
        System.out.println("hello I am client");
        new client();
    }
}
