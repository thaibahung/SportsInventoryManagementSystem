package com.sportsinventory.DAO;

import com.sportsinventory.DTO.ItemDTO;
import com.sportsinventory.Database.ConnectionFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Locale;
import java.util.Vector;

public class ItemDAO {

    Connection conn = null;
    PreparedStatement prepStatement = null;
    PreparedStatement prepStatement2 = null;
    Statement statement = null;
    Statement statement2 = null;
    ResultSet resultSet = null;

    public ItemDAO() {
        try {
            conn = new ConnectionFactory().getConn();
            statement = conn.createStatement();
            statement2 = conn.createStatement();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ResultSet getUserInfo() {
        try {
            String query = "SELECT * FROM users";
            resultSet = statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getItemStock() {
        try {
            String query = "SELECT * FROM currentstock";
            resultSet = statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getItemInfo() {
        try {
            String query = "SELECT * FROM items";
            resultSet = statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public Double getItemCost(String prodCode) {
        Double costPrice = null;
        try {
            String query = "SELECT costprice FROM items WHERE itemcode='" +prodCode+ "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next())
                costPrice = resultSet.getDouble("costprice");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return costPrice;
    }

    String suppCode;
    public String getSuppCode(String suppName) {
        try {
            String query = "SELECT suppliercode FROM suppliers WHERE fullname='" +suppName+ "'";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                suppCode = resultSet.getString("suppliercode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppCode;
    }

    String prodCode;
    public String getItemCode(String prodName) {
        try {
            String query = "SELECT itemcode FROM items WHERE itemname='" +prodName+ "'";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                suppCode = resultSet.getString("itemcode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prodCode;
    }

    // Method to check for availability of stock in Inventory
    boolean flag = false;
    public boolean checkStock(String prodCode) {
        try {
            String query = "SELECT * FROM currentstock WHERE itemid='" +prodCode+ "'";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    // Methods to add a new item
    public void addItemDAO(ItemDTO itemDTO) {
        try {
            String query = "SELECT * FROM items WHERE itemname='"
                    + itemDTO.getItemName()
                    + "' AND costprice='"
                    + itemDTO.getCostPrice()
                    + "' AND description='"
                    + itemDTO.getDescription()
                    + "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next())
                JOptionPane.showMessageDialog(null, "Itemuct has already been added.");
            else
                addFunction(itemDTO);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addFunction(ItemDTO itemDTO) {
        try {
            String query = "INSERT INTO items VALUES(null,?,?,?,?,?)";
            prepStatement = (PreparedStatement) conn.prepareStatement(query);
            prepStatement.setString(1, itemDTO.getItemCode());
            prepStatement.setString(2, itemDTO.getItemName());
            prepStatement.setDouble(3, itemDTO.getCostPrice());
            prepStatement.setDouble(4, itemDTO.getSellPrice());
            prepStatement.setString(5, itemDTO.getDescription());

            String query2 = "INSERT INTO currentstock VALUES(?,?)";
            prepStatement2 = conn.prepareStatement(query2);
            prepStatement2.setString(1, itemDTO.getItemCode());
            prepStatement2.setInt(2, itemDTO.getQuantity());

            prepStatement.executeUpdate();
            prepStatement2.executeUpdate();
            JOptionPane.showMessageDialog(null, "Item added and ready for sale.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // Method to add a new purchase transaction
    public void addBookingDAO(ItemDTO itemDTO) {
        try {
            String query = "INSERT INTO booking VALUES(?,?,?,?,?,?,?)";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, itemDTO.getSuppCode());
            prepStatement.setString(2, itemDTO.getItemCode());
            prepStatement.setString(3, itemDTO.getDate());
            prepStatement.setInt(4, itemDTO.getQuantity());
            prepStatement.setDouble(5, itemDTO.getTotalCost());

            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Purchase log added.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        String prodCode = itemDTO.getItemCode();
        if(checkStock(prodCode)) {
            try {
                String query = "UPDATE currentstock SET quantity=quantity+? WHERE itemid=?";
                prepStatement = conn.prepareStatement(query);
                prepStatement.setInt(1, itemDTO.getQuantity());
                prepStatement.setString(2, prodCode);

                prepStatement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else if (!checkStock(prodCode)) {
            try {
                String query = "INSERT INTO currentstock VALUES(?,?)";
                prepStatement = (PreparedStatement) conn.prepareStatement(query);
                prepStatement.setString(1, itemDTO.getItemCode());
                prepStatement.setInt(2, itemDTO.getQuantity());

                prepStatement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        deleteStock();
    }

    // Method to update existing item details
    public void editItemDAO(ItemDTO itemDTO) {
        try {
            String query = "UPDATE items SET itemname=?,costprice=?,description=? WHERE itemcode=?";
            prepStatement = (PreparedStatement) conn.prepareStatement(query);
            prepStatement.setString(1, itemDTO.getItemName());
            prepStatement.setDouble(2, itemDTO.getCostPrice());
            prepStatement.setDouble(3, itemDTO.getSellPrice());
            prepStatement.setString(4, itemDTO.getDescription());
            prepStatement.setString(5, itemDTO.getItemCode());

            String query2 = "UPDATE currentstock SET quantity=? WHERE itemid=?";
            prepStatement2 = conn.prepareStatement(query2);
            prepStatement2.setInt(1, itemDTO.getQuantity());
            prepStatement2.setString(2, itemDTO.getItemCode());

            prepStatement.executeUpdate();
            prepStatement2.executeUpdate();
            JOptionPane.showMessageDialog(null, "Item details updated.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // Methods to handle updating of stocks in Inventory upon any transaction made
    public void editStock(String code, int quantity) {
        try {
            String query = "SELECT * FROM currentstock WHERE itemid='" +code+ "'";
            resultSet = statement.executeQuery(query);
            if(resultSet.next()) {
                String query2 = "UPDATE currentstock SET quantity=quantity-? WHERE itemid=?";
                prepStatement = conn.prepareStatement(query2);
                prepStatement.setInt(1, quantity);
                prepStatement.setString(2, code);
                prepStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void editBookedStock(String code, int quantity) {
        try {
            String query = "SELECT * FROM currentstock WHERE itemid='" +code+ "'";
            resultSet = statement.executeQuery(query);
            if(resultSet.next()) {
                String query2 = "UPDATE currentstock SET quantity=quantity+? WHERE itemid=?";
                prepStatement = conn.prepareStatement(query2);
                prepStatement.setInt(1, quantity);
                prepStatement.setString(2, code);
                prepStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void deleteStock() {
        try {
            String query = "DELETE FROM currentstock WHERE itemid NOT IN(SELECT itemid FROM booking)";
            String query2 = "DELETE FROM booking WHERE itemid NOT IN(SELECT items.itemid FROM items)";
            statement.executeUpdate(query);
            statement.executeUpdate(query2);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // Method to permanently delete an item from sportsinventory
    public void deleteItemDAO(String code) {
        try {
            String query = "DELETE FROM items WHERE itemcode=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, code);

            String query2 = "DELETE FROM currentstock WHERE itemid=?";
            prepStatement2 = conn.prepareStatement(query2);
            prepStatement2.setString(1, code);

            prepStatement.executeUpdate();
            prepStatement2.executeUpdate();

            JOptionPane.showMessageDialog(null, "Item has been removed.");
        } catch (SQLException e){
            e.printStackTrace();
        }
        deleteStock();
    }

    public void deleteBookingDAO(int ID){
        try {
            String query = "DELETE FROM booking WHERE bookingid=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setInt(1, ID);
            prepStatement.executeUpdate();

            JOptionPane.showMessageDialog(null, "Transaction has been removed.");
        } catch (SQLException e){
            e.printStackTrace();
        }
        deleteStock();
    }

    public void deleteSaleDAO(int ID) {
        try {
            String query = "DELETE FROM booking WHERE bookingID=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setInt(1, ID);
            prepStatement.executeUpdate();

            JOptionPane.showMessageDialog(null, "Transaction has been removed.");
        } catch (SQLException e){
            e.printStackTrace();
        }
        deleteStock();
    }

    // Sales transaction handling
    public void sellItemuctDAO(ItemDTO itemDTO, String username) {
        int quantity = 0;
        String prodCode = null;
        try {
            String query = "SELECT * FROM currentstock WHERE itemid='" + itemDTO.getItemCode()+ "'";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                prodCode = resultSet.getString("itemcode");
                quantity = resultSet.getInt("quantity");
            }
            if (itemDTO.getQuantity()>quantity)
                JOptionPane.showMessageDialog(null, "Insufficient stock for this item.");
            else if (itemDTO.getQuantity()<=0)
                JOptionPane.showMessageDialog(null, "Please enter a valid quantity");
            else {
                String stockQuery = "UPDATE currentstock SET quantity=quantity-'"
                        + itemDTO.getQuantity()
                        +"' WHERE itemid='"
                        + itemDTO.getItemCode()
                        +"'";
                String bookingsQuery = "INSERT INTO booking(itemid, userid, quantity, date, expirationdate, penaltyperday)" +
                        "VALUES('"+ itemDTO.getDate()+"','"+ itemDTO.getItemCode()+"','"+ itemDTO.getCustCode()+
                        "','"+ itemDTO.getQuantity()+"','"+"','"+username+"')";
                statement.executeUpdate(stockQuery);
                statement.executeUpdate(bookingsQuery);
                JOptionPane.showMessageDialog(null, "Item sold.");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // Item data set retrieval for display
    public ResultSet getQueryResult() {
        try {
            String query = "SELECT itemcode,itemname,costprice,description FROM items ORDER BY itemname";
            resultSet = statement.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultSet;
    }

    // Purchase table data set retrieval
    public ResultSet getBooking() {
        try {
            String query = "SELECT bookingid, booking.itemid,ItemName,Quantity,penaltyperday " +
                    "FROM booking INNER JOIN items " +
                    "ON items.itemid=booking.itemid ORDER BY date;";
            resultSet = statement.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultSet;
    }

    // Stock table data set retrieval
    public ResultSet getCurrentStockInfo() {
        try {
            String query = """
                    SELECT currentstock.itemid,items.itemname,
                    currentstock.Quantity,items.costprice
                    FROM currentstock INNER JOIN items
                    ON currentstock.itemid=items.itemid;
                    """;
            resultSet = statement.executeQuery(query);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return resultSet;
    }

    // Sales table data set retrieval
    public ResultSet getBookingsInfo() {
        try {
            String query = """
                    SELECT bookingid,booking.itemid,itemname,
                    booking.quantity,users.name AS Sold_by
                    FROM booking
                    INNER JOIN items
                        ON booking.itemid=items.itemid
                    INNER JOIN users
                        ON booking.userid=users.id;
                    """;
            resultSet = statement.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultSet;
    }

    // Search method for items
    public ResultSet getItemSearch(String text) {
        try {
            String query = "SELECT itemcode,itemname,costprice,description FROM items " +
                            "WHERE itemcode LIKE '%"+text+"%' OR itemname LIKE '%"+text+
                            "%' OR description LIKE '%"+text+"%'";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getItemFromCode(String text) {
        try {
            String query = "SELECT itemcode,itemname,costprice,costprice,description FROM items " +
                    "WHERE itemcode='" +text+ "' LIMIT 1";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getSalesSearch(String text) {
        try {
            String query = "SELECT bookingid,booking.itemid,itemname,\n" +
                    "                    booking.quantity,date,users.name AS Sold_by\n" +
                    "                    FROM booking INNER JOIN items\n" +
                    "                    ON booking.itemid=items.itemid\n" +
                    "                    INNER JOIN users\n" +
                    "                    ON booking.userid=users.id\n" +
                    "                    INNER JOIN users\n" +
                    "                    ON users.id=booking.userid\n" +
                    "WHERE booking.itemid LIKE '%"+text+"%' OR itemname LIKE '%"+text+"%' " +
                    "OR users.name LIKE '%"+text+"%' OR users.name LIKE '%"+text+"%' ORDER BY bookingid;";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    // Search method for purchase logs
    public ResultSet getPurchaseSearch(String text) {
        try {
            String query = "SELECT bookingid,booking.itemid,items.itemname,quantity " +
                    "FROM booking INNER JOIN items ON booking.itemid=items.itemid " +
                    "WHERE bookingid LIKE '%"+text+"%' OR itemcode LIKE '%"+text+"%' OR itemname LIKE '%"+text+"%' " +
                    "OR date LIKE '%"+text+"%' ORDER BY date";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getItemName(String code) {
        try {
            String query = "SELECT itemname FROM items WHERE itemcode='" +code+ "'";
            resultSet = statement.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultSet;
    }

    public String getUserName(int ID) {
        String name = null;
        try {
            String query = "SELECT name FROM users " +
                    "INNER JOIN booking ON users.id=booking.userid " +
                    "WHERE bookingid='" +ID+ "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next())
                name = resultSet.getString("fullname");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return name;
    }

    public String getBookedDate(int ID) {
        String date = null;
        try {
            String query = "SELECT date FROM booking WHERE bookingid='" +ID+ "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next())
                date = resultSet.getString("date");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return date;
    }
    public String getSaleDate(int ID) {
        String date = null;
        try {
            String query = "SELECT date FROM booking WHERE bookingid='" +ID+ "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next())
                date = resultSet.getString("date");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return date;
    }

    public DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Vector<String> columnNames = new Vector<>();
        int colCount = metaData.getColumnCount();

        for (int col=1; col <= colCount; col++)
            columnNames.add(metaData.getColumnName(col).toUpperCase(Locale.ROOT));

        Vector<Vector<Object>> data = new Vector<>();
        while (resultSet.next()) {
            Vector<Object> vector = new Vector<>();
            for (int col=1; col<=colCount; col++) vector.add(resultSet.getObject(col));
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }
}
