package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.t_cheque;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class chequesController implements Initializable {

    public TableView<t_cheque> chequesTable;
    public JFXButton back;
    public JFXButton add;
    public JFXButton deleteChequeBtn;
    public JFXTextField cheque_id;
    public JFXTextField amount1;
    public JFXDatePicker issued_date;
    public JFXDatePicker expire_date;
    public JFXTextField bank_name;
    public JFXTextField branch_name;
    public JFXComboBox type;
    public Text income;
    public Text profit;
    public TableColumn<t_cheque,String> cId;
    public TableColumn<t_cheque,String> cAmount;
    public TableColumn<t_cheque,String> cBank;
    public TableColumn<t_cheque,String> cBranch;
    public TableColumn<t_cheque,String> cIssueDate;
    public TableColumn<t_cheque,String> cExpireDate;
    public TableColumn<t_cheque,String> cType;
    private ArrayList<t_cheque> allRows;
    public Text dueBill;
    public Text issuedBill;

    public chequesController() throws SQLException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        cBank.setCellValueFactory(new PropertyValueFactory<>("bank"));
        cBranch.setCellValueFactory(new PropertyValueFactory<>("branch"));
        cIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        cExpireDate.setCellValueFactory(new PropertyValueFactory<>("expireDate"));
        cType.setCellValueFactory(new PropertyValueFactory<>("type"));
        cAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        chequesTable.setItems(chequesData);
        try {
            allRows = t_cheque.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.fillTypeCombo();
        this.setIssuedBill();
        // Add Delete Menu Item
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction((ActionEvent event)->{
            t_cheque currentClicked = chequesTable.getSelectionModel().getSelectedItem();
            try {
                currentClicked.delete();
                chequesTable.getItems().remove(currentClicked);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ContextMenu rightClick = new ContextMenu();
        rightClick.getItems().add(delete);
        chequesTable.setContextMenu(rightClick);
    }


    private ObservableList<t_cheque> chequesData = FXCollections.observableArrayList(
            t_cheque.getAll()
    );

    public void updateItem(MouseEvent mouseEvent) throws SQLException, IOException {
       try{
           String chequeNo = cheque_id.getText();
           String bank = bank_name.getText();
           String branch = branch_name.getText();
           String issue = issued_date.getValue().toString();
           String expire = expire_date.getValue().toString();
           double amount = Double.parseDouble(amount1.getText());
           String checkType = type.getSelectionModel().getSelectedItem().toString();
           t_cheque currentCheque = new t_cheque(chequeNo,bank,branch,issue,expire,checkType,amount);
           currentCheque.update();
           chequesTable.getItems().remove(chequesTable.getSelectionModel().getSelectedItem());
           chequesTable.getItems().add(currentCheque);
           clearInputs();

           //Refresh Table
           Stage thiswind = (Stage) chequesTable.getScene().getWindow();
           FXMLLoader chequesView = new FXMLLoader(getClass().getResource("/resources/views/cheques.fxml"));
           Parent root = (Parent) chequesView.load();
           thiswind.setTitle("Manage Cheques");
           thiswind.setScene(new Scene(root));
           thiswind.show();
       }catch(Exception e){
           System.out.println(e);
           warning.incomplete();
       }
    }

    public void deleteCheques(MouseEvent mouseEvent) throws SQLException, IOException {
        try{
            String chequeNo = cheque_id.getText();
            t_cheque currentCheque = new t_cheque(chequeNo,"","","","","",0);
            currentCheque.delete();
            chequesTable.getItems().remove(chequesTable.getSelectionModel().getSelectedItem());
            clearInputs();

            //Refresh Table
            Stage thiswind = (Stage) chequesTable.getScene().getWindow();
            FXMLLoader chequesView = new FXMLLoader(getClass().getResource("/resources/views/cheques.fxml"));
            Parent root = (Parent) chequesView.load();
            thiswind.setTitle("Manage Cheques");
            thiswind.setScene(new Scene(root));
            thiswind.show();
            warning.deleteSuccess();
        }catch(Exception e){
            System.out.println(e);
            warning.incomplete();
        }
    }

    private void clearInputs() {
        cheque_id.clear();
        bank_name.clear();
        branch_name.clear();
        type.getSelectionModel().clearSelection();
        amount1.clear();
        issued_date.setValue(null);
        expire_date.setValue(null);
    }

    public void backMenu(MouseEvent mouseEvent) throws IOException {
        Stage thisWindow = (Stage)chequesTable.getScene().getWindow();
        FXMLLoader backLoader = new FXMLLoader(getClass().getResource("/resources/views/mainMenu.fxml"));
        Parent root = backLoader.load();
        thisWindow.setTitle("Invoices");
        thisWindow.setScene(new Scene(root));
    }

    public void addNewCheque(MouseEvent mouseEvent) throws IOException {
        FXMLLoader load = new FXMLLoader(getClass().getResource("/resources/views/addCheque.fxml"));
        Stage model = new Stage();
        Parent root = load.load();
        model.setTitle("Add New Cheque");
        model.initModality(Modality.APPLICATION_MODAL);
        model.setScene(new Scene(root));
        model.show();
    }

    public void showDueCheques(MouseEvent mouseEvent) {
        chequesTable.getItems().remove(0,chequesTable.getItems().size());
        for(t_cheque currentItem:allRows){
            if(currentItem.getType().equals("Due")) chequesTable.getItems().add(currentItem);
        }
    }

    public void showIssuedCheques(MouseEvent mouseEvent) {
        chequesTable.getItems().remove(0,chequesTable.getItems().size());
        for(t_cheque currentItem:allRows){
            if(currentItem.getType().equals("Issued")) chequesTable.getItems().add(currentItem);
        }
    }

    public void showSettledCheques(MouseEvent mouseEvent) {
        chequesTable.getItems().remove(0,chequesTable.getItems().size());
        for(t_cheque currentItem:allRows){
            if(currentItem.getType().equals("Settled")) chequesTable.getItems().add(currentItem);
        }
    }

    public void fetchData(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount()>1){
            DateTimeFormatter fomatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            t_cheque currentClicked = chequesTable.getSelectionModel().getSelectedItem();
            cheque_id.setText(currentClicked.getId());
            bank_name.setText(currentClicked.getBank());
            branch_name.setText(currentClicked.getBranch());
            issued_date.setValue(LocalDate.parse(currentClicked.getIssueDate(),fomatter));
            expire_date.setValue(LocalDate.parse(currentClicked.getExpireDate(),fomatter));
            amount1.setText(Double.toString(currentClicked.getAmount()));
            type.getSelectionModel().select(currentClicked.getType());
        }
    }
    public void fillTypeCombo(){
        type.getItems().add("Due");
        type.getItems().add("Issued");
        type.getItems().add("Settled");
    }

    public void setIssuedBill(){
        double issuedBillc=0;
        double dueBillc=0;
        for(t_cheque current:allRows){
            if(current.getType().equals("Issued")) issuedBillc+=current.getAmount();
            else if(current.getType().equals("Due")) dueBillc+=current.getAmount();
        }
        issuedBill.setText("RS: "+Double.toString(issuedBillc)+" /=");
        dueBill.setText("RS: "+Double.toString(dueBillc)+" /=");
    }
}