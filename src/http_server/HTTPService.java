/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http_server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rnavarro
 */
public class HTTPService implements Runnable {

    private static final Logger LOG = Logger.getLogger(HTTPService.class.getName());
    
    private Socket clientSocket;
    
    PrintStream out;
    public HTTPService(Socket c) throws IOException {
        clientSocket = c;
        this.out = new PrintStream(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        BufferedReader in
                = null;
        try {       
            //para obtner los datos que enviara el cliente y los datos que enviaremos
            //enviamos nuestro datos
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            
            String[] direccion;
            String direc = null;
            
            // leer la solicitud del cliente
            while ((inputLine = in.readLine()) != null) {   
                if(inputLine.startsWith("GET")){
                    //separar en tokens las lineas, la url, es para obtener el nombre de otro archivo
                    //para obtener el archivo
                    direccion = inputLine.split("\\s+");
                    direc = direccion[1].substring(direccion[1].lastIndexOf("/")+ 1);
                }
                //imprime las lineas del encabezado
                System.out.println(inputLine);
                
                //Si recibimos linea en blanco, es fin del la solicitud
                if( inputLine.isEmpty() ) { //se lee el encabezado del http
                    break;
                    //cuando sea en blanco(termine) se saldra en while
                }
            }
            if(direc.endsWith(".jpg")||direc.endsWith(".png") ||direc.endsWith(".ico") || direc.endsWith(".gif")) {
                leerImagen(direc);
            }else if(direc.endsWith(".html")){
                leerDocHtml(direc);
            }else if(direc.endsWith("")){
                LOG.info("FORM");
                doGet(inputLine);
            }
            //para obtener el archivo y sacar el tamaño de los bytes
            
           
          
            System.out.println("El archivo es: ");
            System.out.println(direc);
            
            System.out.println("done");
            clientSocket.close();//cierra conexion con el navegador
            
        } catch (IOException ex) {
            System.out.println("Error en la conexion");
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void leerDocHtml(String archivo){
        File fi = new File(archivo);
        try (FileReader file = new FileReader(fi)){
            
            //encabezado de respuesta
            out.println("HTTP/1.1 200 OK"); //protocolo HTTP ejecutar
            out.println("Content-Type: text/html; charset=utf-8");//tipo de datos que enviara el servidor al navegador
            //out.println("Content-Length: " + f.length); el f.length regresa la cantidad de bytes del archivo
            out.println("Contenr-Length: " + fi.length()); //contenido de caracteres del archivo a enviar o la
            out.println();
            
            int data;
                //se envia byte por  byte el archivo nuestro
            while( (data = file.read()) != -1 ) {
                out.write(data);
                
                    //System.out.println(".");
            }
            out.flush();
            file.close();
        } catch(FileNotFoundException e){
            System.err.println("Error " + e.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void leerImagen(String archivo){
                    //enviar nuestros datos
            //encabezado de respuesta
            File fi = new File(archivo);
            out.println("HTTP/1.1 200 OK");//protocolo HTTP  ejecutar
            if (archivo.endsWith(".jpg")) {
                out.println("Content-Type: image/jpeg");//tipo de datos que enviara el servidor al navegador
            } else if (archivo.endsWith(".ico")) {
                out.println("Content-Type: image/x-icon");
            } else if (archivo.endsWith(".png")) {
                out.println("Content-Type: image/png");
            } else if (archivo.endsWith(".gif")) {
                out.println("Content-Type: image/gif");
            }
            //out.println("Content-Length: " + f.length); el f.length regresa la cantidad de bytes del archivo
            out.println("Content-Length: " + fi.length());//contenido de caracteres del archivo a enviar o la cantidad de bytes
            out.println();
           
            System.out.println("Tamaño del archivo: {0} " + fi.length());
            
            FileInputStream file;
            try{
                file = new FileInputStream(fi);
                int data;
                    //se envia byte por byte el archivo nuestro
                while( (data = file.read()) != -1 ) {
                    out.write(data);
                    //System.out.println(".");
                }
                out.flush();
                file.close();
            } catch(FileNotFoundException e){
                System.err.println("Error:" + e.getMessage());
            } catch (IOException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        }
     
    }
    public void notEncontrado(String archivo){
    
        File f = new File(archivo);
        if(archivo.contains("404.html")){
            out.println("HTTP/1.1 404 Not Found");
            out.println("Content-Type: text/html; charset=utf-8");
            out.println("Content-Length : " + f.length());
            out.println();
            FileReader file = null;
            try {
                file = new FileReader(f);

                int data;

                while ((data = file.read()) != -1) {
                    out.write(data);
                    out.flush();
                }
                file.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    public void notImplementado(String archivo){
    
        File f = new File(archivo);
        if(archivo.contains("501.html")){
            out.println("HTTP/1.1 501 Not Implemented");
            out.println("Content-Type: text/html; charset=utf-8");
            out.println("Content-Length : " + f.length());
            out.println();
            FileReader file = null;
            try {
                file = new FileReader(f);

                int data;

                while ((data = file.read()) != -1) {
                    out.write(data);
                    out.flush();
                }
                file.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }      
    }
    private void doGet(String commandLine) {
        StringBuilder response = new StringBuilder();
        System.out.println(commandLine);

        String query = commandLine.substring(commandLine.lastIndexOf('?') + 1,
                commandLine.lastIndexOf(' '));

        System.out.println(query);

        String[] tokens = query.split("\\&+");

        for (int i = 0; i < tokens.length; i++) {
            System.out.println(tokens[i]);
        }
    }    
}