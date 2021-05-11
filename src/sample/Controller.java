package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Controller {
    Socket socket;
    DataOutputStream out;
    Thread thread;
    @FXML
    TextArea textArea;
    @FXML
    TextField textField;
    @FXML
    TextArea textAreaUserList;

    @FXML
    private void onSubmit(){
        String text = textField.getText();
        textArea.appendText(text+"\n");
        textField.clear();
        try {
            out.writeUTF(text);
        } catch (IOException exception) {
            textArea.appendText("Произошла ошибка");
            exception.printStackTrace();
        }
    }
    @FXML
    private void connect(){
        try {
            socket = new Socket("localhost",8188);
            DataInputStream in =new DataInputStream(socket.getInputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            out=new DataOutputStream(socket.getOutputStream()); // Инициализация out
            try {
                String response = ois.readObject().toString(); // Ждём сообщение от сервера
                textArea.appendText(response+"\n"); // Введите имя
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        try {
                            Object object = ois.readObject();
                            if(String.class == object.getClass()){ // Если нам прислали текстовое сообщение
                                textArea.appendText(object.toString()+"\n"); // То печатаем его на TextArea
                            }else if(ArrayList.class ==  object.getClass()){ //  Если нам прислали объект класса ArrayList
                                ArrayList<String> usersName = new ArrayList<String>(); // то это список пользователей
                                usersName = (ArrayList<String>) object; // Преобразуем объект в ArrayList
                                textAreaUserList.clear(); // Очищаем поле для списка имён
                                for (String userName: usersName) { // Перебираем список
                                    textAreaUserList.appendText(userName+"\n"); // И ишем их на экран
                                }
                            }else{
                                System.out.println("Класс не определен");
                            }

                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}