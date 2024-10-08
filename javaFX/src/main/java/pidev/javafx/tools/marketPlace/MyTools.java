package pidev.javafx.tools.marketPlace;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import pidev.javafx.crud.marketplace.CrudFavorite;
import pidev.javafx.model.Contrat.Contract;
import pidev.javafx.model.MarketPlace.Bien;
import pidev.javafx.model.MarketPlace.Favorite;
import pidev.javafx.model.MarketPlace.Product;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

public class MyTools {

    private static MyTools instance;

    private Label textNotif;
    private HBox notifHbox;
    private Label imageNotif;

    public Label getTextNotif() {
        return textNotif;
    }


    private MyTools() {
    }

    public static MyTools getInstance() {
        if (instance == null)
            instance = new MyTools();
        return instance;
    }


    public void generatePDF(Contract contract) {
        try {
            Document document = new Document();
            PdfWriter.getInstance( document, new FileOutputStream( getFileOfSave() ) );
            document.open();
            document.add( new Paragraph( "Title: Sale contrat" ) );
            document.add( new Paragraph( "Party A ID: " + 1 ) );
            document.add( new Paragraph( "Party B ID: " + 2 ) );
            document.add( new Paragraph( "Item Name: " ) );
            document.add( new Paragraph( "Effective Date: " + contract.getEffectiveDate() ) );
            document.add( new Paragraph( "Termination Date: " + contract.getTerminationDate() ) );
            document.add( new Paragraph( "Purpose: buyintg oéjrhgniuoh't" ) );
            document.add( new Paragraph( "Terms and Conditions: " + contract.getTermsAndConditions() ) );
            document.add( new Paragraph( "Payment Method: " + contract.getPaymentMethod() ) );
            document.close();
            System.out.println( "PDF generated successfully!" );
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generatePDFWithApi() {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage( page );

            PDPageContentStream contentStream = new PDPageContentStream( document, page );
            PDImageXObject image = PDImageXObject.createFromFile( "src/main/resources/appLogo/logo.png", document );
            float imageWidth = image.getWidth();
            float imageHeight = image.getHeight();

            // Position the image at the end of the page
            float imageX = 100; // Adjust X-coordinate as needed
            float imageY = 100; // Adjust Y-coordinate as needed
            contentStream.drawImage( image, imageX, imageY, 100, 100 );
//            contentStream.beginText();
//            contentStream.setFont( PDType1Font.HELVETICA_BOLD, 18);
//            contentStream.newLineAtOffset(100, 700);
//            contentStream.showText("Hello, PDFBox!");
//            contentStream.endText();

            contentStream.close();
            document.save( new FileOutputStream( getFileOfSave() ) );
            System.out.println( "PDF created successfully at: " );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private File getFileOfSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( "Pdf Image" );
        fileChooser.setInitialDirectory( new File( "src/main/resources/Cnotrat" ) );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter( "PDF", "*.pdf" )
        );
        return fileChooser.showSaveDialog( Stage.getWindows().get( 0 ) );
    }


    public String getPathAndSaveIMG(String chosenFilePath) {

        var byteArray=imageToByteArray(chosenFilePath);
        return sendImage(byteArray,"http://localhost:8000/api/upload-image").replace( '"',' ').trim();
    }

    public String getPathAndSaveIMGUser(String chosenFilePath) {

        String path = "usersImg/" + UUID.randomUUID() + ".png";

        Path src = Paths.get( chosenFilePath );
        Path dest = Paths.get( "C:\\Users\\omar salhi\\Desktop\\integratedPorjectJavaSymfony\\citiezenHub_webapp\\public\\" + path );

        try {
            Files.copy( src, dest );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }

        return path;
    }


    public static byte[] imageToByteArray(String imagePath) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read( new File( imagePath ) );
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write( bufferedImage, "png", byteArrayOutputStream );
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    public static String sendImage(byte[] imageData, String targetUrl) {
        URL url = null;
        try {
            url = new URL( targetUrl );
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput( true );
            httpURLConnection.setRequestMethod( "POST" );
            httpURLConnection.setRequestProperty( "Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW" );

            try (DataOutputStream dataOutputStream = new DataOutputStream( httpURLConnection.getOutputStream() )) {
                // Write the multipart form data content
                String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
                dataOutputStream.writeBytes( "--" + boundary + "\r\n" );
                dataOutputStream.writeBytes( "Content-Disposition: form-data; name=\"image\"; filename=\"image.png\"\r\n" );
                dataOutputStream.writeBytes( "Content-Type: image/png\r\n\r\n" );
                dataOutputStream.write( imageData );
                dataOutputStream.writeBytes( "\r\n--" + boundary + "--\r\n" );
                dataOutputStream.flush();
            }

            try (InputStreamReader reader = new InputStreamReader( httpURLConnection.getInputStream() )) {
                StringBuilder response = new StringBuilder();
                int c;
                while ((c = reader.read()) != -1) {
                    response.append( (char) c );
                }
                return handleJsonResponse( response.toString() );
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException( e );
        } catch (ProtocolException e) {
            throw new RuntimeException( e );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    public static String handleJsonResponse(String jsonResponse) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson( jsonResponse, JsonObject.class );
        if (jsonObject.has( "path" )) {
            return jsonObject.get( "path" ).toString();
        }
        return null;
    }

//    public void notifyUser4NewAddedProduct(Product product) {
//        ObservableList<Favorite> favoriteObservableList = CrudFavorite.getInstance().selectItems();
//        boolean checkIfProductIsValid = true;
//        if (product instanceof Bien prod) {
//            for (Favorite favorite : favoriteObservableList) {
//                String[] parts = favorite.getSpecifications().split( "__" );
//                if (LocalDate.now().isAfter( LocalDate.parse( parts[0] ) ) || LocalDate.now().isEqual( LocalDate.parse( parts[0] ) )) {
//                    if (!parts[1].isEmpty() && (LocalDate.now().isAfter( LocalDate.parse( parts[1] ) )))
//                        checkIfProductIsValid = false;
//                    else if (!parts[2].equals( "-1" ) && Integer.parseInt( parts[2] ) > prod.getPrice())
//                        checkIfProductIsValid = false;
//                    else if (!parts[3].equals( "-1" ) && Integer.parseInt( parts[3] ) < prod.getPrice())
//                        checkIfProductIsValid = false;
//                    else if (!parts[4].equals( "-1" ) && Integer.parseInt( parts[4] ) != prod.getQuantity())
//                        checkIfProductIsValid = false;
//                    else if (!parts[5].equals( prod.getCategorie().toString() ) && !parts[5].equals( "ALL" ))
//                        checkIfProductIsValid = false;
//                } else
//                    checkIfProductIsValid = false;
//                if (checkIfProductIsValid)
//                    PhoneSMS.getInstance().sendSMS( "+21629624921", "A New Product Was Added" );
//                checkIfProductIsValid = true;
//            }
//        }
//    }

    public Popup createPopUp() {
        Popup popup = new Popup();
        Label popupContent = new Label();
        popupContent.setStyle( "-fx-border-color: #000000; -fx-border-width: 1px; -fx-padding: 5px; -fx-text-fill: white;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" );
        popupContent.setWrapText( true );
        popupContent.setAlignment( Pos.CENTER );
        popupContent.setFont( Font.font( "System", FontWeight.MEDIUM, FontPosture.REGULAR, 16 ) );
        popup.getContent().add( popupContent );
        return popup;
    }

    public void deleteAnimation(Node child, Node parent) {
        ScaleTransition scaleTransition = new ScaleTransition( Duration.seconds( 0.5 ), child );
        scaleTransition.setToX( 0 );
        scaleTransition.setToY( 0 );
        scaleTransition.setCycleCount( 1 );
        scaleTransition.setAutoReverse( false );
        scaleTransition.play();
        if (parent instanceof HBox hBox)
            scaleTransition.setOnFinished( event1 -> hBox.getChildren().remove( child ) );
        else if (parent instanceof VBox vBox)
            scaleTransition.setOnFinished( event1 -> vBox.getChildren().remove( child ) );
        if (parent instanceof GridPane gridPane)
            scaleTransition.setOnFinished( event1 -> gridPane.getChildren().remove( child ) );
    }


    public void showAnimation(Node child) {
        child.setVisible( false );
        ScaleTransition scaleTransition = new ScaleTransition( Duration.seconds( 0.1 ), child );
        scaleTransition.setToX( 0 );
        scaleTransition.setToY( 0 );
        scaleTransition.setCycleCount( 1 );
        scaleTransition.setAutoReverse( false );
        scaleTransition.play();
        scaleTransition.setOnFinished( event -> {
            child.setVisible( true );
            scaleTransition.setDuration( Duration.seconds( 0.5 ) );
            scaleTransition.setToX( 1 );
            scaleTransition.setToY( 1 );
            scaleTransition.play();
        } );
    }


    public void showAndHideAnimation(Node child, int ttoWhat, double delay) {

        ScaleTransition scaleTransition = new ScaleTransition( Duration.seconds( 0.5 ), child );
        scaleTransition.setToX( ttoWhat );
        scaleTransition.setToY( ttoWhat );
        scaleTransition.setCycleCount( 1 );
        scaleTransition.setAutoReverse( false );
        scaleTransition.setDelay( Duration.millis( delay ) );
        if (ttoWhat == 1)
            child.setVisible( true );
        else {
            scaleTransition.setOnFinished( event -> child.setVisible( false ) );
        }
        scaleTransition.play();
    }


    public void setTextNotif(Label textNotif) {
        this.textNotif = textNotif;
    }

    public HBox getNotifHbox() {
        return notifHbox;
    }

    public void setNotifHbox(HBox notifHbox) {
        this.notifHbox = notifHbox;
    }

    public Label getImageNotif() {
        return imageNotif;
    }

    public void setImageNotif(Label imageNotif) {
        this.imageNotif = imageNotif;
    }


    public void showNotif() {
        textNotif.setStyle( "-fx-background-color: #fdc847" );
        imageNotif.setStyle( "-fx-background-color: #fdc847" );
        showAndHideAnimation( notifHbox, 1, 500 );
        Timeline timeline = new Timeline( new KeyFrame( Duration.millis( 5000 ), event1 -> {
            showAndHideAnimation( notifHbox, 0, 0 );
        } ) );
        timeline.setCycleCount( Animation.INDEFINITE );
        timeline.play();
    }

    public void showErrorNotif() {
        textNotif.setStyle( "-fx-background-color: rgba(224,55,55,0.48)" );
        imageNotif.setStyle( "-fx-background-color: rgba(224,55,55,0.48)" );
        imageNotif.setGraphic( new ImageView( new Image( String.valueOf( getClass().getResource( "/icons/marketPlace/cancel.png" ) ), 16, 16, false, false ) ) );
        showAndHideAnimation( notifHbox, 1, 500 );
        Timeline timeline = new Timeline( new KeyFrame( Duration.millis( 5000 ), event1 -> {
            showAndHideAnimation( notifHbox, 0, 0 );
        } ) );
        timeline.setCycleCount( Animation.INDEFINITE );
        timeline.play();
    }


    public void showErrorNotif2(int time) {
        textNotif.setStyle( "-fx-background-color: rgba(224,55,55,0.48)" );
        imageNotif.setStyle( "-fx-background-color: rgba(224,55,55,0.48)" );
        imageNotif.setGraphic( new ImageView( new Image( String.valueOf( getClass().getResource( "/icons/marketPlace/cancel.png" ) ), 16, 16, false, false ) ) );
        showAndHideAnimation( notifHbox, 1, 500 );
        Timeline timeline = new Timeline( new KeyFrame( Duration.millis( time ), event1 -> {
            showAndHideAnimation( notifHbox, 0, 0 );
        } ) );
        timeline.setCycleCount( Animation.INDEFINITE );
        timeline.play();
    }

}
