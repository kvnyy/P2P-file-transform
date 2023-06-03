package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ServerController {

    Center center;
    @FXML
    private Label welcomeText;

    @FXML
    private Button startButton;
    @FXML
    private Button endButton;
    @FXML
    private Button showFile;
    @FXML
    private TextArea fileList;
    @FXML
    protected void onstartButtonClick() {
        if(center==null) {
            center = new Center();
            center.receiveThread();
            welcomeText.setText("启动服务器！");
        }
        else
        {
            welcomeText.setText("服务器已启动！");
        }

    }

    @FXML
    protected void onendButtonCilck(){
        Stage stage=(Stage) fileList.getScene().getWindow();
        stage.close();
        System.exit(0);

    }
    @FXML
    protected void onshowfileButtonCilck(){
        if(center==null){
            fileList.setText("服务器未启动");
        }
        else
        {

            fileList.setText(center.getFileList());
        }
    }
}