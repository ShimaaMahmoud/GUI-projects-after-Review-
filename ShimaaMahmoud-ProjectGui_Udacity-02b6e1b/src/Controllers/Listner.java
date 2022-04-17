/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Models.InvoiceHeader;
import Models.InvoiceHeaderTableModel;
import Models.InvoiceLine;
import Models.InvoiceLinesTableModel;
import Views.InvoiceFrame;
import Views.InvoiceHeaderDialog;
import Views.InvoiceLineDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author MaxaAB-Dell
 */
public class Listner implements ActionListener, ListSelectionListener  {
    private InvoiceFrame Myframe;
    private DateFormat dateformate = new SimpleDateFormat("dd-MM-yyyy");
    
    public Listner(InvoiceFrame frame) {
        this.Myframe = frame;
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "CreateNewInvoice":
                displayNewInvoiceDialog();
                break;
            case "DeleteInvoice":
                deleteInvoice();
                break;
            case "CreateNewLine":
                displayNewLineDialog();
                break;
            case "DeleteLine":
                deleteLine();
                break;
            case "LoadFile":
                loadFile();
                break;
            case "SaveFile":
                saveData();
                break;
            case "createInvCancel":
                createInvCancel();
                break;
            case "createInvOK":
                createInvOK();
                break;
            case "createLineCancel":
                createLineCancel();
                break;
            case "createLineOK":
                createLineOK();
                break;
        }
    }

    private void loadFile() {
        JOptionPane.showMessageDialog(Myframe, "Please, select header file!", "Attension", JOptionPane.WARNING_MESSAGE);
        JFileChooser openFile = new JFileChooser();
        int result = openFile.showOpenDialog(Myframe);
        if (result == JFileChooser.APPROVE_OPTION) {
            File headerFile = openFile.getSelectedFile();
            try {
                FileReader headerFr = new FileReader(headerFile);
                BufferedReader headerBr = new BufferedReader(headerFr);
                String headerLine = null;

                while ((headerLine = headerBr.readLine()) != null) {
                    String[] headerParts = headerLine.split(",");
                    String invNumStr = headerParts[0];
                    String invDateStr = headerParts[1];
                    String custName = headerParts[2];

                    int invNum = Integer.parseInt(invNumStr);
                    Date invDate = dateformate.parse(invDateStr);

                    InvoiceHeader inv = new InvoiceHeader(invNum, custName, invDate);
                    Myframe.getInvoicesList().add(inv);
                }

                JOptionPane.showMessageDialog(Myframe, "Please, select lines file!", "Attension", JOptionPane.WARNING_MESSAGE);
                result = openFile.showOpenDialog(Myframe);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File linesFile = openFile.getSelectedFile();
                    BufferedReader linesBr = new BufferedReader(new FileReader(linesFile));
                    String linesLine = null;
                    while ((linesLine = linesBr.readLine()) != null) {
                        String[] lineParts = linesLine.split(",");
                        String invNumStr = lineParts[0];
                        String itemName = lineParts[1];
                        String itemPriceStr = lineParts[2];
                        String itemCountStr = lineParts[3];

                        int invNum = Integer.parseInt(invNumStr);
                        double itemPrice = Double.parseDouble(itemPriceStr);
                        int itemCount = Integer.parseInt(itemCountStr);
                        InvoiceHeader header = findInvoiceByNum(invNum);
                        InvoiceLine invLine = new InvoiceLine(itemName, itemPrice, itemCount, header);
                        header.getLines().add(invLine);
                    }
                    Myframe.setInvoiceHeaderTableModel(new InvoiceHeaderTableModel(Myframe.getInvoicesList()));
                    Myframe.getInvoicesTable().setModel(Myframe.getInvoiceHeaderTableModel());
                    Myframe.getInvoicesTable().validate();
                }
                System.out.println("Check");
            } catch (ParseException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(Myframe, "Date Format Error\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(Myframe, "Number Format Error\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(Myframe, "File Error\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(Myframe, "Read Error\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        displayInvoices();
    }

    private void saveData() {
        String headers = "";
        String lines = "";
        for (InvoiceHeader header : Myframe.getInvoicesList()) {
            headers += header.getDataAsCSV();
            headers += "\n";
            for (InvoiceLine line : header.getLines()) {
                lines += line.getDataAsCSV();
                lines += "\n";
            }
        }
        JOptionPane.showMessageDialog(Myframe, "Please, select file to save header data!", "Attension", JOptionPane.WARNING_MESSAGE);
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(Myframe);
        if (result == JFileChooser.APPROVE_OPTION) {
            File headerFile = fileChooser.getSelectedFile();
            try {
                FileWriter hFW = new FileWriter(headerFile);
                hFW.write(headers);
                hFW.flush();
                hFW.close();

                JOptionPane.showMessageDialog(Myframe, "Please, select file to save lines data!", "Attension", JOptionPane.WARNING_MESSAGE);
                result = fileChooser.showSaveDialog(Myframe);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File linesFile = fileChooser.getSelectedFile();
                    FileWriter lFW = new FileWriter(linesFile);
                    lFW.write(lines);
                    lFW.flush();
                    lFW.close();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Myframe, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        JOptionPane.showMessageDialog(Myframe, "Data saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

    }

    private InvoiceHeader findInvoiceByNum(int invNum) {
        InvoiceHeader header = null;
        for (InvoiceHeader inv : Myframe.getInvoicesList()) {
            if (invNum == inv.getInvNum()) {
                header = inv;
                break;
            }
        }
        return header;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        System.out.println("Invoice Selected!");
        invoicesTableRowSelected();
    }

    private void invoicesTableRowSelected() {
        int selectedRowIndex = Myframe.getInvoicesTable().getSelectedRow();
        if (selectedRowIndex >= 0) {
            InvoiceHeader row = Myframe.getInvoiceHeaderTableModel().getInvoicesList().get(selectedRowIndex);
            Myframe.getCustNameTF().setText(row.getCustomerName());
            Myframe.getInvDateTF().setText(dateformate.format(row.getInvDate()));
            Myframe.getInvNumLbl().setText("" + row.getInvNum());
            Myframe.getInvTotalLbl().setText("" + row.getInvTotal());
            ArrayList<InvoiceLine> lines = row.getLines();
            Myframe.setInvoiceLinesTableModel(new InvoiceLinesTableModel(lines));
            Myframe.getInvLinesTable().setModel(Myframe.getInvoiceLinesTableModel());
            Myframe.getInvoiceLinesTableModel().fireTableDataChanged();
        }
    }

    private void displayNewInvoiceDialog() {
        Myframe.setHeaderDialog(new InvoiceHeaderDialog(Myframe));
        Myframe.getHeaderDialog().setVisible(true);
    }

    private void displayNewLineDialog() {
        Myframe.setLineDialog(new InvoiceLineDialog(Myframe));
        Myframe.getLineDialog().setVisible(true);
    }

    private void createInvCancel() {
        Myframe.getHeaderDialog().setVisible(false);
        Myframe.getHeaderDialog().dispose();
        Myframe.setHeaderDialog(null);
    }

    private void createInvOK() {
        String custName = Myframe.getHeaderDialog().getCustNameField().getText();
        String invDateStr = Myframe.getHeaderDialog().getInvDateField().getText();
        Myframe.getHeaderDialog().setVisible(false);
        Myframe.getHeaderDialog().dispose();
        Myframe.setHeaderDialog(null);
        try {
            Date invDate = dateformate.parse(invDateStr);
            int invNum = getNextInvoiceNum();
            InvoiceHeader invoiceHeader = new InvoiceHeader(invNum, custName, invDate);
            Myframe.getInvoicesList().add(invoiceHeader);
            Myframe.getInvoiceHeaderTableModel().fireTableDataChanged();
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(Myframe, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        displayInvoices();
    }

    private int getNextInvoiceNum() {
        int max = 0;
        for (InvoiceHeader header : Myframe.getInvoicesList()) {
            if (header.getInvNum() > max) {
                max = header.getInvNum();
            }
        }
        return max + 1;
    }

    private void createLineCancel() {
        Myframe.getLineDialog().setVisible(false);
        Myframe.getLineDialog().dispose();
        Myframe.setLineDialog(null);
    }

    private void createLineOK() {
        int x=Myframe.getInvoicesTable().getSelectedRow();
        String itemName = Myframe.getLineDialog().getItemNameField().getText();
        String itemCountStr = Myframe.getLineDialog().getItemCountField().getText();
        String itemPriceStr = Myframe.getLineDialog().getItemPriceField().getText();
        Myframe.getLineDialog().setVisible(false);
        Myframe.getLineDialog().dispose();
        Myframe.setLineDialog(null);
        int itemCount = Integer.parseInt(itemCountStr);
        double itemPrice = Double.parseDouble(itemPriceStr);
        int headerIndex = Myframe.getInvoicesTable().getSelectedRow();
        InvoiceHeader Headerinvoice = Myframe.getInvoiceHeaderTableModel().getInvoicesList().get(headerIndex);

        InvoiceLine invoiceLine = new InvoiceLine(itemName, itemPrice, itemCount, Headerinvoice);
        Headerinvoice.addInvLine(invoiceLine);
        Myframe.getInvoiceLinesTableModel().fireTableDataChanged();
        Myframe.getInvoiceHeaderTableModel().fireTableDataChanged();
        Myframe.getInvTotalLbl().setText("" + Headerinvoice.getInvTotal());
        displayInvoices();
        Myframe.getInvoicesTable().setRowSelectionInterval(x, x);
    }

    private void deleteInvoice() {
        int invIndex = Myframe.getInvoicesTable().getSelectedRow();
        InvoiceHeader header = Myframe.getInvoiceHeaderTableModel().getInvoicesList().get(invIndex);
        Myframe.getInvoiceHeaderTableModel().getInvoicesList().remove(invIndex);
        Myframe.getInvoiceHeaderTableModel().fireTableDataChanged();
        Myframe.setInvoiceLinesTableModel(new InvoiceLinesTableModel(new ArrayList<InvoiceLine>()));
        Myframe.getInvLinesTable().setModel(Myframe.getInvoiceLinesTableModel());
        Myframe.getInvoiceLinesTableModel().fireTableDataChanged();
        Myframe.getCustNameTF().setText("");
        Myframe.getInvDateTF().setText("");
        Myframe.getInvNumLbl().setText("");
        Myframe.getInvTotalLbl().setText("");
        displayInvoices();
    }

    private void deleteLine() {
        int lineIndex = Myframe.getInvLinesTable().getSelectedRow();
        InvoiceLine line = Myframe.getInvoiceLinesTableModel().getInvoiceLines().get(lineIndex);
        Myframe.getInvoiceLinesTableModel().getInvoiceLines().remove(lineIndex);
        Myframe.getInvoiceLinesTableModel().fireTableDataChanged();
        Myframe.getInvoiceHeaderTableModel().fireTableDataChanged();
        Myframe.getInvTotalLbl().setText("" + line.getHeader().getInvTotal());
        displayInvoices();
    }

    private void displayInvoices() {
       
        for (InvoiceHeader header : Myframe.getInvoicesList()) {
            System.out.println(header);
        }
        System.out.println("shimaa checker!!");
    }
    
}
