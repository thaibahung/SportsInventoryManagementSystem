package com.sportsinventory.DAO;

import com.sportsinventory.DTO.CustomerDTO;
import com.sportsinventory.Database.ConnectionFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Locale;
import java.util.Vector;

public class CustomerDAO {
    Connection conn = null;
    PreparedStatement prepStatement= null;
    Statement statement = null;
    ResultSet resultSet = null;

    public CustomerDAO() {
        try {
            conn = new ConnectionFactory().getConn();
            statement = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Methods to add new custoemr
    public void addCustomerDAO(CustomerDTO customerDTO) {
        try {
            String query = "SELECT * FROM customers WHERE fullname='"
                    +customerDTO.getFullName()
                    + "' AND location='"
                    +customerDTO.getLocation()
                    + "' AND phone='"
                    +customerDTO.getPhone()
                    + "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next())
                JOptionPane.showMessageDialog(null, "Customer already exists.");
            else
                addFunction(customerDTO);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addFunction(CustomerDTO customerDTO) {
        try {
            String query = "INSERT INTO customers VALUES(null,?,?,?,?)";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, customerDTO.getCustCode());
            prepStatement.setString(2, customerDTO.getFullName());
            prepStatement.setString(3, customerDTO.getLocation());
            prepStatement.setString(4, customerDTO.getPhone());
            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "New customer has been added.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Method to edit existing customer details
    public  void editCustomerDAO(CustomerDTO customerDTO) {
        try {
            String query = "UPDATE customers SET fullname=?,location=?,phone=? WHERE customercode=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, customerDTO.getFullName());
            prepStatement.setString(2, customerDTO.getLocation());
            prepStatement.setString(3, customerDTO.getPhone());
            prepStatement.setString(4, customerDTO.getCustCode());
            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Customer details have been updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete existing customer
    public void deleteCustomerDAO(String custCode) {
        try {
            String query = "DELETE FROM customers WHERE customercode='" +custCode+ "'";
            statement.executeUpdate(query);
            JOptionPane.showMessageDialog(null, "Customer removed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve data set to be displayed
    public ResultSet getQueryResult() {
        try {
            String query = "SELECT custcode,fullname,location,phone FROM customers";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    // Method to retrieve search data
    public ResultSet getCustomerSearch(String text) {
        try {
            String query = "SELECT custcode,fullname,location,phone FROM customers " +
                    "WHERE customercode LIKE '%"+text+"%' OR fullname LIKE '%"+text+"%' OR " +
                    "location LIKE '%"+text+"%' OR phone LIKE '%"+text+"%'";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getCustName(String custCode) {
        try {
            String query = "SELECT * FROM customers WHERE customercode='" +custCode+ "'";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Vector<String> columnNames = new Vector<>();
        int colCount = metaData.getColumnCount();

        for (int col=1; col <= colCount; col++){
            columnNames.add(metaData.getColumnName(col).toUpperCase(Locale.ROOT));
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (resultSet.next()) {
            Vector<Object> vector = new Vector<>();
            for (int col=1; col<=colCount; col++) vector.add(resultSet.getObject(col));
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }

}
