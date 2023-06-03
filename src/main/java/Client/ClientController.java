package Client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

public class ClientController {


    @FXML
    private Label Client;

    //GUI右边部分
//    @FXML
//    private Button exit;
//    @FXML
//    private Button start;
//    @FXML
//    private Button upload;
//    @FXML
//    private Button getList;
//    @FXML
//    private Button download;
    @FXML
    private TextField wantSeq;
    @FXML
    private TextArea showFileList;
    @FXML
    private TextArea LocalFile;
    @FXML
    private Label warn;

    /**
     * -----------------------------------
     * 非界面部分
     */
    private Peer peer;

    //左上脚的登录用户名
    private String bufName="";

    private String fileList="";

    private String filepath;


    public void setName(String name){
        this.bufName=name;
    }
    /**
     * --------------------------------------
     * 事件部分
     */


    @FXML
    public void initialize(){
        this.Client.setText(bufName);
        this.Client.setOpacity(1);
    }
    /**
     * 启动，和中央服务器连接
     */
    @FXML
    public void startClick() throws Exception {
        peer=new Peer(bufName);
        System.out.println(bufName);
        peer.run();
        /*和客户段的传输文件线程*/
        peer.startServer();


    }
    /**
     * 向中央服务器发送本地文件列表
     */
    @FXML
    public void uploadClick(){

        if(LocalFile.getText().isEmpty()){
            Alert alert=new Alert(Alert.AlertType.WARNING,"请先选择需要上传的文件！");
            alert.showAndWait();
        }
        else {
            //sendFile(DataInputStream dis, DataOutputStream dos, File file)
            //向中央服务器发送本地的文件列表
            System.out.println(filepath);
            peer.sendFileList(filepath);
        }
    }

    /**
     * 选择要上传的文件目录信息
     */
    @FXML
    public void chooseFileClick(){
        FileChooser fileChooser=new FileChooser();
        File file=fileChooser.showOpenDialog((Stage)this.Client.getScene().getWindow());
        filepath=file.getAbsolutePath();
        LocalFile.setText(filepath);
        System.out.println(filepath);
    }
    /**
     * 从中央服务器请求文件信息
     */
    @FXML
    public void getListClick(){
        //reqFiles()
        if(peer==null)
        {
              Alert alert=new Alert(Alert.AlertType.WARNING,"请先启动");
              alert.showAndWait();
        }
        else {
            fileList= peer.reqFiles();
            showFileList.setText(fileList);
        }

    }

    /**
     * 从其他client下载文件
     */
    @FXML
    public void downloadClick(){
        // downloadFile(String host, int port, String url) 调用下面的函数
        // recvFile(DataInputStream dis, DataOutputStream dos,
        //String savePath)
        if(peer==null)
        {
            Alert alert=new Alert(Alert.AlertType.WARNING,"请先启动");
            alert.showAndWait();
        }
        else if(wantSeq.getText().isEmpty())
        {
            Alert alert=new Alert(Alert.AlertType.WARNING,"请输入需要下载的序号");
            alert.showAndWait();
        }
        else
        {

            String info[]=peer.action(wantSeq.getText());
            //思路，把downloadFile从action中剥离出来
            //参数放到string=host+port+url里
            //在用一个函数分这个string,传参到
            //downloadFile(string)
            if(info==null){
                Alert alert=new Alert(Alert.AlertType.WARNING,"文件不存在");
                alert.showAndWait();
            }

            else{
                DirectoryChooser directoryChooser=new DirectoryChooser();
                File result=directoryChooser.showDialog(null);
                String path=result.getAbsolutePath();
                int sign=peer.downloadFile(info,path);
                if(sign==-1){
                    Alert alert=new Alert(Alert.AlertType.WARNING,"该用户不在线，向其他用户请求文件吧！");
                    alert.showAndWait();
                }
                else {
                    Alert alert=new Alert(Alert.AlertType.INFORMATION,"请求成功");
                    alert.showAndWait();
                }
            }

        }
    }
    /**
     * 关闭client
     */
    @FXML
    public void exitClick(){
        Stage stage=(Stage) Client.getScene().getWindow();
        stage.close();
        System.exit(0);
    }
}
