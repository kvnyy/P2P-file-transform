package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    private int sign=0;
    @FXML
    private TextField userName;
    @FXML
    public void loginClick(ActionEvent actionEvent) throws IOException {
        if(userName.getText().isEmpty())
        {
            Alert alert=new Alert(Alert.AlertType.WARNING,"请输入用户名");
            alert.showAndWait();
        }
//        else if()
//        {
//            Alert alert=new Alert(Alert.AlertType.WARNING,"服务器未启动");
//            alert.showAndWait();
//        }
        else
        {
            //隐藏本stage
            Stage stage=(Stage) userName.getScene().getWindow();
            stage.hide();
            //更新stage的sence
            FXMLLoader loader=new FXMLLoader(getClass().getResource("client-view.fxml"));
            Parent parent =loader.load();
            Scene mainScene=new Scene(parent);
            stage.setScene(mainScene);
            stage.show();
            //传名字
            ClientController clientController=loader.getController();
            clientController.setName(userName.getText());

        }

    }

    @FXML
    public void endClick(ActionEvent actionEvent) {
        Stage stage=(Stage) userName.getScene().getWindow();
        stage.close();
        System.exit(0);
    }
}
