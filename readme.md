#### 使用方式

##### 1idea打开这个项目（可能需要qiye version）

![image-20230604131747923](C:\Users\29864\AppData\Roaming\Typora\typora-user-images\image-20230604131747923.png)

##### 2运行P2P的server

![image-20230604131954973](C:\Users\29864\AppData\Roaming\Typora\typora-user-images\image-20230604131954973.png)

###### 运行后结果

可以看到启动按钮，界面中间显示：server中保存的文件元数据（url ip port）

![image-20230604132143331](C:\Users\29864\AppData\Roaming\Typora\typora-user-images\image-20230604132143331.png)

#### 3运行P2P的client

###### 这里只展示运行一个client,client和client传文件需要另创一个javafx项目，把client的部分复制过去就可以传了

##### 3.1登录界面（senceBuilder 搭建）

![image-20230604133027888](C:\Users\29864\AppData\Roaming\Typora\typora-user-images\image-20230604133027888.png)

##### 3.2操作界面（senceBuilder 搭建）

先start,然后可选：1.choose-upload(上传本地文件元数据到server)；2.getlist-填序号-dowmload（显示并下载在线上的其他client的文件到本地）

![image-20230604132732558](C:\Users\29864\AppData\Roaming\Typora\typora-user-images\image-20230604132732558.png)