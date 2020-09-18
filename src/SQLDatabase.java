
import com.mysql.jdbc.Connection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLDatabase extends JFrame {
    private JFrame jFrame;
    private SQLDatabase database;
    private SQLConnection connection;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JLabel nameLabel, emailLabel, websiteLabel, commentLabel;
    private JTextField nameField, emailField, websiteField, commentField, usernameField, passwordField;
    private JButton addCommentBtn, refreshBtn, connectBtn;
    private String name, email, website, comment;


    public SQLDatabase() {
        this.database = this;
        this.jFrame = this;
        this.setTitle("No connection");
        connectBtn = new JButton("Connect");
        addCommentBtn = new JButton("Add comment");
        refreshBtn = new JButton("Refresh");
        nameLabel = new JLabel("Name - ");
        emailLabel = new JLabel("Email - ");
        websiteLabel = new JLabel("Website - ");
        commentLabel = new JLabel("Comment - ");
        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300,400));
        nameField = new JTextField();
        emailField = new JTextField();
        websiteField = new JTextField();
        commentField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JTextField();
        nameLabel.setBounds(10, 0, 70, 20);
        nameField.setBounds(80, 0, 100, 20);
        nameField.setEnabled(false);
        emailLabel.setBounds(10, 20, 70, 20);
        emailField.setBounds(80, 20, 100, 20);
        emailField.setEnabled(false);
        websiteLabel.setBounds(10, 40, 70, 20);
        websiteField.setBounds(80, 40, 100, 20);
        websiteField.setEnabled(false);
        commentLabel.setBounds(10, 60, 70, 20);
        commentField.setBounds(80, 60, 100, 20);
        commentField.setEnabled(false);
        refreshBtn.setBounds(200, 10, 100, 20);
        refreshBtn.setEnabled(false);
        addCommentBtn.setBounds(200, 35, 100, 20);
        addCommentBtn.setEnabled(false);
        scrollPane.setBounds(20, 100, 340, 320);
        connectBtn.setBounds(400, 160, 100, 20);
        usernameField.setBounds(400, 100, 100, 20);
        passwordField.setBounds(400, 130, 100, 20);
        String select = "SELECT * FROM Guestbook";
        String entry = " INSERT INTO Guestbook (name, email, website, comment) " + "VALUES (?,?,?,?)";

        // -------------- BUTTONS ------------------------


        this.add(addCommentBtn);
        addCommentBtn.addActionListener(e -> {
            getTextField();
            if(!connection.checkText(name)){
                name = "Censur";
                connection.setCensored();
            } if(!connection.checkText(email)){
                email = "Censur";
                connection.setCensored();
            } if(!connection.checkText(website)){
                website = "Censur";
                connection.setCensored();
            } if(!connection.checkText(comment)){
                comment = "Censur";
                connection.setCensored();// set false;
            }

            connection.writeToDb(entry, name, email, website, comment);
        });



        this.add(refreshBtn);
        refreshBtn.addActionListener(e -> {

            connection.readFromDb(select);
            JScrollBar scroll = scrollPane.getVerticalScrollBar();
            scroll.setValue(scroll.getMaximum());


        });

        this.add(connectBtn);
        connectBtn.addActionListener(e -> {

            if(connectBtn.getText().equals("Connect")){
            String username = getUsername();
            String password = getPassword();


            connection = new SQLConnection(database, username, password);
            connection.start();
            jFrame.setTitle("Connected to server @" +connection.getUrl());
            toggleConnect();
            enableComment();
            refreshBtn.setEnabled(true);
            addCommentBtn.setEnabled(true);
            } else {
               connection.killConnection();
            }
        });
        // ------------ BUTTONS END -----------------------

        this.add(scrollPane);
        this.add(nameLabel);
        this.add(emailLabel);
        this.add(websiteLabel);
        this.add(commentLabel);
        this.add(nameField);
        this.add(emailField);
        this.add(websiteField);
        this.add(commentField);
        this.add(usernameField);
        this.add(passwordField);
        this.setSize(550, 500);//400 width and 500 height
        this.setLayout(null);//using no layout managers
        this.setVisible(true);//making the frame visible

    }

    public void toggleConnect(){
        if(connectBtn.getText().equals("Connect")){
            connectBtn.setText("Disconnect");
        } else if(connectBtn.getText().equals("Disconnect")){
            connectBtn.setText("Connect");
        }
    }

    public void getTextField() {
        name = nameField.getText();
        email = emailField.getText();
        website = websiteField.getText();
        comment = commentField.getText();
    }

    public void enableComment(){
        nameField.setEnabled(true);
        emailField.setEnabled(true);
        websiteField.setEnabled(true);
        commentField.setEnabled(true);
    }

    public void disableComment(){
        nameField.setEnabled(false);
        emailField.setEnabled(false);
        websiteField.setEnabled(false);
        commentField.setEnabled(false);
        addCommentBtn.setEnabled(false);
        refreshBtn.setEnabled(false);
    }

    public static void main(String[] args) {
        SQLDatabase f = new SQLDatabase();


    }

    public String getUsername(){
        return usernameField.getText();
    }

    public String getPassword(){
        return passwordField.getText();
    }

    public void print(String text){
        scrollPane.getViewport().revalidate();
        textArea.append(text + "\n");
    }

    public void removeText(){
        synchronized (textArea){
            textArea.setText("");
        }
    }

    public void logConnection(){
        print("------- Connection established -------");
    }

    public void disableConnectionInput(){
        usernameField.setEnabled(false);
        passwordField.setEnabled(false);
    }

    public void logNoConnection(){
     print("------- No connection -------");
     print("Wrong username/password");
    }

    public void logSuccess(String addText){
        print(addText+"\n \n \n");
    }

    public void logToTextarea(String name, String email, String website, String comment) {

        print("-----------------------------------");
        print("Name: "+name);
        print("Email: "+email);
        print("Website: " +website);
        print("Comment: " +comment);
        print("-----------------------------------");

    }

    public synchronized void diconnect(){
        usernameField.setEnabled(true);
        passwordField.setEnabled(true);
        disableComment();
        toggleConnect();
        removeText();
    }
}

class SQLConnection extends Thread{
    private SQLDatabase sqlDatabase;
    private boolean connected = false;
    private boolean censorship = false;
    private String url = "jdbc:mysql://atlas.dsv.su.se:3306/db_20947219";
    private String username;
    private String password;
    private PreparedStatement prepStatement;
    private Statement statement;
    private Connection dbConnection;
    private Pattern htmlPattern = Pattern.compile("<(\"[^\"]*\"|'[^']*'|[^'\">])*>"+"<*>");


    public SQLConnection(SQLDatabase sqlDatabase, String username, String password){
        this.sqlDatabase = sqlDatabase;
        this.username = username;
        this.password = password;
    }

    public String getUrl(){
        return url;
    }

    public void setCensored(){
        censorship = true;
    }

    public String getAddText(){
        if(censorship = true){
            return "Added to guestbook - But some got censored. No html plsss...";
        } else{
            return "Added to guestbook";
        }
    }

    public void writeToDb(String entry,String name,String email,String website,String comment) {
        String select = "SELECT * FROM Guestbook";
        try {
            prepStatement = dbConnection.prepareStatement(entry);
            prepStatement.setString(1,name);
            prepStatement.setString(2,email);
            prepStatement.setString(3,website);
            prepStatement.setString(4,comment);
            prepStatement.execute();
            sqlDatabase.logSuccess(getAddText());
            readFromDb(select);
        } catch (SQLException sqlException){
            System.out.println(sqlException);
        }
    }

    public synchronized boolean checkText(String string){
        Matcher matcher = htmlPattern.matcher(string);
        return matcher.find();
    }


    public synchronized void readFromDb(String select){
        String name, email, website, comment;
        sqlDatabase.removeText();
        try {
            statement = dbConnection.createStatement();
            ResultSet result = statement.executeQuery(select);

        while(result.next()){
            //Retrieve
            name = result.getString("name");
            email = result.getString("email");
            website = result.getString("website");
            comment = result.getString("comment");

            sqlDatabase.logToTextarea(name, email, website, comment);

        }
    } catch (SQLException sqle){
            System.out.println(sqle);
        }
    }

    public void setConnection(){
        sqlDatabase.logConnection();
        sqlDatabase.disableConnectionInput();
    }


    public void run(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            dbConnection = (Connection) DriverManager.getConnection(
                    url,username,password);
            connected = true;
            setConnection();
        } catch(SQLException | ClassNotFoundException e){
            System.out.println(e);
            sqlDatabase.logNoConnection();

        } catch (IllegalAccessException e) {
            e.printStackTrace();

        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void killConnection()  {
        try{
        dbConnection.close();
        sqlDatabase.diconnect();
        sqlDatabase.print("Disconnected");
    } catch (SQLException sqlE){
            sqlDatabase.print("ERROR: Could not disconnect");
        }
    }
}
