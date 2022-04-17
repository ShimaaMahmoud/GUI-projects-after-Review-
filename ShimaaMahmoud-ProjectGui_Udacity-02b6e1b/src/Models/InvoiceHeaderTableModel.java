
package Models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.AbstractTableModel;


public class InvoiceHeaderTableModel extends AbstractTableModel {

    private List<InvoiceHeader> invoices;
    private DateFormat dateformate = new SimpleDateFormat("dd-MM-yyyy");
    
    public InvoiceHeaderTableModel(List<InvoiceHeader> invoicesList) {
        this.invoices = invoicesList;
    }

    public List<InvoiceHeader> getInvoicesList() {
        return invoices;
    }
    
    
    @Override
    public int getRowCount() {
        return invoices.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Invoice Num";
            case 1:
                return "Customer Name";
            case 2:
                return "Invoice Date";
            case 3:
                return "Invoice Total";
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Integer.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        InvoiceHeader row = invoices.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                return row.getInvNum();
            case 1:
                return row.getCustomerName();
            case 2:
                return dateformate.format(row.getInvDate());
            case 3:
                return row.getInvTotal();
            default:
                return "";
        }
        
    }
    
}
