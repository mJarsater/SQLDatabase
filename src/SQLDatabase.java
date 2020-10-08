
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
    private JLabel nameLabel, emailLabel, websiteLabel, commentLabel, usernameLabel, passwordLabel;
    private JTextField nameField, emailField, websiteField, commentField, usernameField, passwordField;
    private JButton addCommentBtn, refreshBtn, connectBtn;
    private String name, email, website, comment;

    //Konstruktor för SQLdatabase - GUI
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
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300,400));
        nameField = new JTextField();
        emailField = new JTextField();
        websiteField = new JTextField();
        commentField = new JTextField();
        usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        passwordLabel = new JLabel("Password:");
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
        refreshBtn.setBounds(200, 10, 150, 20);
        refreshBtn.setEnabled(false);
        addCommentBtn.setBounds(200, 35, 150, 20);
        addCommentBtn.setEnabled(false);
        scrollPane.setBounds(20, 100, 340, 320);
        usernameLabel.setBounds(400,80,100,20);
        usernameField.setBounds(400, 100, 100, 20);
        passwordLabel.setBounds(400,130,100,20);
        passwordField.setBounds(400, 150, 100, 20);
        connectBtn.setBounds(400, 180, 100, 20);

        String select = "SELECT * FROM Guestbook";
        String entry = " INSERT INTO Guestbook (name, email, website, comment) " + "VALUES (?,?,?,?)";

        // -------------- BUTTONS ------------------------
        /*Knapp för att lämna kommentar, som kollar all
         text innan den skickar till databasen. */
        this.add(addCommentBtn);
        addCommentBtn.addActionListener(e -> {
            getTextField();
            if(connection.checkText(name)){
                name = "Censur";
                connection.setCensored();
            } if(connection.checkText(email)){
                email = "Censur";
                connection.setCensored();
            } if(connection.checkText(website)){
                website = "Censur";
                connection.setCensored();
            } if(connection.checkText(comment)){
                comment = "Censur";
                connection.setCensored();// set false;
            }

            connection.writeToDb(entry, name, email, website, comment);
        });



        this.add(refreshBtn);
        /* Knapp för att läsa från databasen*/
        refreshBtn.addActionListener(e -> {
            /* Kallar på metoden readFromDb */
            connection.readFromDb(select);

            /*  GUI - Skapar en scrollbar och sätter
             autoscrollen till max (nyaste inläggen) */
            JScrollBar scroll = scrollPane.getVerticalScrollBar();
            scroll.setValue(scroll.getMaximum());


        });

        this.add(connectBtn);
        connectBtn.addActionListener(e -> {

            if(connectBtn.getText().equals("Connect")){
            String username = getUsername();
            String password = getPassword();
            /* Skapar en ny SQLConnection och skickar med database (this),
            avändarnamn och lösenord. */
            connection = new SQLConnection(database, username, password);

            //"Startar" tråden connection.
            connection.connectToDb();
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
        this.add(usernameLabel);
        this.add(usernameField);
        this.add(passwordLabel);
        this.add(passwordField);
        this.setSize(550, 500);//400 width and 500 height
        this.setLayout(null);//using no layout managers
        this.setVisible(true);//making the frame visible
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }
    /* Metod som togglar innehållet på knappen
    för connect / disconnect */
    public void toggleConnect(){
        if(connectBtn.getText().equals("Connect")){
            connectBtn.setText("Disconnect");
        } else if(connectBtn.getText().equals("Disconnect")){
            connectBtn.setText("Connect");
        }
    }

    /* Metod som häntar innehåller i alla
    fälten för en kommentar*/
    public synchronized void getTextField() {
        name = nameField.getText();
        email = emailField.getText();
        website = websiteField.getText();
        comment = commentField.getText();
    }

    /*Metod som sätter fälten för kommentar
    som "enabled"*/
    public void enableComment(){
        nameField.setEnabled(true);
        emailField.setEnabled(true);
        websiteField.setEnabled(true);
        commentField.setEnabled(true);
    }


    /*Metod som sätter fälten för kommentar
    som "disabled"*/
    public void disableComment(){
        nameField.setEnabled(false);
        emailField.setEnabled(false);
        websiteField.setEnabled(false);
        commentField.setEnabled(false);
        addCommentBtn.setEnabled(false);
        refreshBtn.setEnabled(false);
    }
// ----------------- MAIN -------------------
    public static void main(String[] args) {
        SQLDatabase f = new SQLDatabase();
// ----------------- MAIN END ---------------

    }

    //Metod som returnerar användarnamn
    public String getUsername(){
        return usernameField.getText();
    }
    //Metod som returnerar lösenord
    public String getPassword(){
        return passwordField.getText();
    }
    //Metod som skriver på scrollpanen
    public void print(String text){
        scrollPane.getViewport().revalidate();
        textArea.append(text + "\n");
    }
    //Metod som rensar scrollpanen på text
    public void removeText(){
        synchronized (textArea){
            textArea.setText("");
        }
    }

    /*Metod som skriver ut :
    ----------- Connection established --------
    på scrollpanen
    */
    public void logConnection(){
        print("------- Connection established -------");
    }

    /*Metod som sätter användarnamn- och
    lösenordsfältet som "disabled" */
    public void disableConnectionInput(){
        usernameField.setEnabled(false);
        passwordField.setEnabled(false);
    }

    /*Metod som skriver ut :
    ------- No connection -------
    Wrong username/password
    på scrollpanen
    */
    public void logNoConnection(){
     print("------- No connection -------");
     print("Wrong username/password");
    }


    /* Skriver ett successmeddelande till scrollpanen */
    public void logSuccess(String addText){
        print(addText+"\n \n \n");
    }

    /* Skriver text + parametrarna till scrollpanen */
    public void logToTextarea(String name, String email, String website, String comment) {

        print("Name: "+name);
        print("Email: "+email);
        print("Website: " +website);
        print("Comment: " +comment);
        print("-----------------------------------");

    }

    /*Metod för att avbryta en connection*/
    public synchronized void diconnect(){
        usernameField.setEnabled(true);
        passwordField.setEnabled(true);
        disableComment();
        toggleConnect();
        removeText();
    }
}

class SQLConnection {
    private SQLDatabase sqlDatabase;
    private boolean connected = false;
    private boolean censorship = false;
    private String url = "jdbc:mysql://atlas.dsv.su.se:3306/db_20947219";
    private String username;
    private String password;
    private PreparedStatement prepStatement;
    private Statement statement;
    private Connection dbConnection;
    private Pattern htmlPattern = Pattern.compile("<*>");

    // Konstruktor för klasse SQLConnection
    public SQLConnection(SQLDatabase sqlDatabase, String username, String password){
        this.sqlDatabase = sqlDatabase;
        this.username = username;
        this.password = password;
    }
    // Metod som returnerar strängen "url"
    public String getUrl(){
        return url;
    }
    // Metod som sätter censorship till sant
    public void setCensored(){
        censorship = true;
    }
    /* Metod som returnerar textens om ska skrivas
    till scrollpanen när det görs ett nytt entry
    i databasen */
    public String getAddText(){
        if(censorship = true){
            return "Added to guestbook - But some got censored. No html plsss...";
        } else{
            return "Added to guestbook";
        }
    }

    /* Metod som skriver paramtrarna till databasen
     med ett prepared statement, samt kallar på readFromDb */
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


    // Metod som kollar så att ingen html finns i texten
    public synchronized boolean checkText(String string){
        Matcher matcher = htmlPattern.matcher(string);
        return matcher.find();
    }

    // Metod som läser från databasen
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

    /* Metod som loggar anslutningen till scrollpanen
    samt disablear input för användarnamn och lösenord.*/
    public void setConnection(){
        sqlDatabase.logConnection();
        sqlDatabase.disableConnectionInput();
    }

    // Metod som ansluter till databasen
    public void connectToDb(){
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
    // Metoden som stänger anslutningen till databasen
    public void killConnection()  {
        try{
        dbConnection.close();
        sqlDatabase.diconnect();
        sqlDatabase.print("Disconnected");
        sqlDatabase.setTitle("No connection");
    } catch (SQLException sqlE){
            sqlDatabase.print("ERROR: Could not disconnect");
        }
    }
}
