
import com.mysql.jdbc.Connection;

import javax.swing.*;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLDatabase {
    public static void main(String[] args) {
        JFrame f = new JFrame();//creating instance of JFrame

        JButton button = new JButton("Add comment");//creating instance of JButton
        JLabel nameLabel = new JLabel("Name: ");
        JLabel emailLabel = new JLabel("Email: ");
        JLabel websiteLabel = new JLabel("Website: ");
        JLabel commentLabel = new JLabel("Comment: ");
        JTextArea textArea = new JTextArea();
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField website = new JTextField();
        JTextField comment = new JTextField();
        nameLabel.setBounds(10, 0,70,20 );
        name.setBounds(80,0,100,20);
        emailLabel.setBounds(10, 20,70,20 );
        email.setBounds(80,20,100,20);
        websiteLabel.setBounds(10, 40,70,20 );
        website.setBounds(80,40,100,20);
        commentLabel.setBounds(10, 60,70,20 );
        comment.setBounds(80,60,100,20);
        button.setBounds(200,30,150, 30);//x axis, y axis, width, height
        textArea.setBounds(20,100, 340, 320);

        f.add(textArea);
        f.add(nameLabel);
        f.add(emailLabel);
        f.add(websiteLabel);
        f.add(commentLabel);
        f.add(name);
        f.add(email);
        f.add(website);
        f.add(comment);
        f.add(button);//adding button in JFrame

        f.setSize(400,500);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible

        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection dbConnection = (Connection) DriverManager.getConnection(
                "jdbc:mysql://atlas.dsv.su.se:3306/db_20947219","usr_20947219","947219");

    } catch(SQLException | ClassNotFoundException e){
            System.out.println(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }}
