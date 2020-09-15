
import com.mysql.jdbc.Connection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SQLDatabase extends JFrame {
    private JFrame jFrame;
    private SQLDatabase database;
    private SQLConnection connection;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JLabel nameLabel, emailLabel, websiteLabel, commentLabel;
    private JTextField nameField, emailField, websiteField, commentField;
    private JButton addCommentBtn, refreshBtn, connectBtn;
    private String name, email, website, comment;
    private String toggleConnect = "Connect";

    public SQLDatabase() {
        this.database = this;
        this.jFrame = this;
        this.setTitle("No connection");
        connectBtn = new JButton("Connect");
        addCommentBtn = new JButton("Add comment");
        refreshBtn = new JButton("Refresh");
        nameLabel = new JLabel("Name: ");
        emailLabel = new JLabel("Email: ");
        websiteLabel = new JLabel("Website: ");
        commentLabel = new JLabel("Comment: ");
        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        nameField = new JTextField();
        emailField = new JTextField();
        websiteField = new JTextField();
        commentField = new JTextField();
        nameLabel.setBounds(10, 0, 70, 20);
        nameField.setBounds(80, 0, 100, 20);
        emailLabel.setBounds(10, 20, 70, 20);
        emailField.setBounds(80, 20, 100, 20);
        websiteLabel.setBounds(10, 40, 70, 20);
        websiteField.setBounds(80, 40, 100, 20);
        commentLabel.setBounds(10, 60, 70, 20);
        commentField.setBounds(80, 60, 100, 20);
        refreshBtn.setBounds(200, 10, 100, 20);
        refreshBtn.setEnabled(false);
        addCommentBtn.setBounds(200, 35, 100, 20);
        addCommentBtn.setEnabled(false);
        connectBtn.setBounds(200, 60, 100, 20);
        textArea.setBounds(20, 100, 340, 320);

        String select = "SELECT * FROM Guestbook";
        String entry = " INSERT INTO Guestbook (name, email, website, comment) " + "VALUES (?,?,?,?)";

        // -------------- BUTTONS ------------------------


        this.add(addCommentBtn);
        addCommentBtn.addActionListener(e -> {
            textArea.setText("Clicked add comment");
            getTextField();
            connection.writeToDb(entry, name, email, website, comment);
        });


        this.add(refreshBtn);
        refreshBtn.addActionListener(e -> connection.readFromDb(select));

        this.add(connectBtn);
        connectBtn.addActionListener(e -> {
            connection = new SQLConnection(database);
            connection.start();
            jFrame.setTitle("Connected to server @" +connection.getUrl());
            connectBtn.setText("Disconnect");
            refreshBtn.setEnabled(true);
            addCommentBtn.setEnabled(true);
        });
        // ------------ BUTTONS END -----------------------

        this.add(textArea);
        this.add(nameLabel);
        this.add(emailLabel);
        this.add(websiteLabel);
        this.add(commentLabel);
        this.add(nameField);
        this.add(emailField);
        this.add(websiteField);
        this.add(commentField);


        this.setSize(400, 500);//400 width and 500 height
        this.setLayout(null);//using no layout managers
        this.setVisible(true);//making the frame visible

    }

    public void getTextField() {
        name = nameField.getText();
        email = emailField.getText();
        website = websiteField.getText();
        comment = commentField.getText();
    }

    public static void main(String[] args) {
        SQLDatabase f = new SQLDatabase();


    }


    public void print(String name, String email, String website, String comment) {
        textArea.setText(
                "Name: "+name+ "\n"+ "Email: "+email+ "\n"+ "Website: "+website+ "\n"+ "Comment: "+comment
        );
    }
}

class SQLConnection extends Thread{
    private SQLDatabase sqlDatabase;
    private boolean connected = false;
    private String url = "jdbc:mysql://atlas.dsv.su.se:3306/db_20947219";
    private String username = "usr_20947219";
    private String password = "947219";
    private PreparedStatement prepStatement;
    private Statement statement;
    private Connection dbConnection;


    public SQLConnection(SQLDatabase sqlDatabase){
        this.sqlDatabase = sqlDatabase;


    }

    public String getUrl(){
        return url;
    }

    public boolean isConnected(){
        return connected;
    }

    public void writeToDb(String entry,String name,String email,String website,String comment) {
        try {
            prepStatement = dbConnection.prepareStatement(entry);
            prepStatement.setString(1,name);
            prepStatement.setString(2,email);
            prepStatement.setString(3,website);
            prepStatement.setString(4,comment);
            prepStatement.execute();
        } catch (SQLException sqlException){
            System.out.println(sqlException);
        }
    }

    public synchronized void readFromDb(String select){
        String name, email, website, comment;

        try{
        statement = dbConnection.createStatement();
        ResultSet result = statement.executeQuery(select);

        while(result.next()){
            //Retrieve
            name = result.getString("name");
            email = result.getString("email");
            website = result.getString("website");
            comment = result.getString("comment");

            sqlDatabase.print(name, email, website, comment);

            System.out.println("Name: "+name);
            System.out.println("Email:" +email);
            System.out.println("Website: "+website);
            System.out.println("Comment: "+comment);
        }
    } catch (SQLException sqle){
            System.out.println(sqle);
        }
    }



    public void run(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            dbConnection = (Connection) DriverManager.getConnection(
                    url,username,password);
            connected = true;
            System.out.println("Connection successfull!");
        } catch(SQLException | ClassNotFoundException e){
            System.out.println(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

}
