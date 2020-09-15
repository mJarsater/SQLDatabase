
import com.mysql.jdbc.Connection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLDatabase extends JFrame{
    private SQLConnection connection;
    private JTextArea textArea;
    private JLabel nameLabel, emailLabel , websiteLabel, commentLabel;
    private JTextField nameField, emailField, websiteField, commentField;
    private JButton addCommentBtn, refreshBtn;
    private String name, email, website, comment;

    public SQLDatabase(){
        this.connection = new SQLConnection();
        connection.start();

        addCommentBtn = new JButton("Add comment");
        refreshBtn = new JButton("Refresh");
        nameLabel = new JLabel("Name: ");
        emailLabel = new JLabel("Email: ");
        websiteLabel = new JLabel("Website: ");
        commentLabel = new JLabel("Comment: ");
        textArea = new JTextArea();
        nameField = new JTextField();
        emailField = new JTextField();
        websiteField = new JTextField();
        commentField = new JTextField();
        nameLabel.setBounds(10, 0,70,20 );
        nameField.setBounds(80,0,100,20);
        emailLabel.setBounds(10, 20,70,20 );
        emailField.setBounds(80,20,100,20);
        websiteLabel.setBounds(10, 40,70,20 );
        websiteField.setBounds(80,40,100,20);
        commentLabel.setBounds(10, 60,70,20 );
        commentField.setBounds(80,60,100,20);
        addCommentBtn.setBounds(200,40,150, 30);//x axis, y axis, width, height
        refreshBtn.setBounds(200, 10,150,30);
        textArea.setBounds(20,100, 340, 320);

        // -------------- BUTTONS ------------------------
        this.add(addCommentBtn);
        addCommentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("Clicked add comment");
                getTextField();
                String entry = " INSERT INTO Guestbook (name, email, website, comment) "+"VALUES (?,?,?,?)";
               /* String entry = "CREATE TABLE Guestbook "+
                        "(name VARCHAR(225), "+
                        "email VARCHAR(225), "+
                        "website VARCHAR(225), "+
                        "comment VARCHAR(225))";*/
                connection.writeToDb(entry, name, email, website, comment);
            }
        });


        this.add(refreshBtn);
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
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




        this.setSize(400,500);//400 width and 500 height
        this.setLayout(null);//using no layout managers
        this.setVisible(true);//making the frame visible

    }

    public void getTextField(){
        name = nameField.getText();
        email = emailField.getText();
        website = websiteField.getText();
        comment = commentField.getText();
    }



    public static void main(String[] args) {
        SQLDatabase f = new SQLDatabase();


    }



}

class SQLConnection extends Thread{
    private boolean connected = false;
    private String url = "jdbc:mysql://atlas.dsv.su.se:3306/db_20947219";
    private String username = "usr_20947219";
    private String password = "947219";
    private PreparedStatement statement;
    private Connection dbConnection;


    public boolean isConnected(){
        return connected;
    }

    public void writeToDb(String entry,String name,String email,String website,String comment) {
        try {
            statement = dbConnection.prepareStatement(entry);
            statement.setString(1,name);
            statement.setString(2,email);
            statement.setString(3,website);
            statement.setString(4,comment);
            statement.execute();
        } catch (SQLException sqlException){
            System.out.println(sqlException);
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
