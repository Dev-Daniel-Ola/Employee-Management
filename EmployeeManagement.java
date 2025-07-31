import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class EmployeeManagement extends JFrame{
	JTextField idField, nameField, departmentField, salaryField;
	JButton addButton, deleteButton, viewButton, updateButton, searchButton;
	JTextArea displayArea;
	Connection connection;
	Statement statement;

	public EmployeeManagement (){
		setTitle("Employee Management System");

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		setSize(screenSize);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(0, 0);
		
		idField = new JTextField(45);
		nameField = new JTextField(45);
		departmentField = new JTextField(45);
		salaryField = new JTextField(45);

		addButton = new JButton("Add Employee");
		deleteButton = new JButton("Delete Employee");
		viewButton  = new JButton("View Employee");
		updateButton = new JButton("Update Employee");
		searchButton = new JButton ("Search Employee");
		
		displayArea = new JTextArea(10, 40);
		displayArea.setEditable(false);

		JPanel p = new JPanel();
		p.setLayout(new GridLayout(7,2,10,10));

		p.add(new JLabel("ID"));
		p.add(idField);

		p.add(new JLabel("Name"));
		p.add(nameField);

		p.add(new JLabel("Department"));
		p.add(departmentField);

		p.add(new JLabel("Salary"));
		p.add(salaryField);

		p.add(addButton);
		p.add(deleteButton);
		p.add(viewButton);
		p.add(updateButton);
		p.add(searchButton);
		p.add(new JScrollPane(displayArea));
		
		add(p);

		connectToDataBase();

		addButton.addActionListener(this::addEmployee);		
		deleteButton.addActionListener(this::deleteEmployee);
		viewButton.addActionListener(this::viewEmployee);
		updateButton.addActionListener(this::updateEmployee);
		searchButton.addActionListener(this::searchEmployee);

	}
		
	public void connectToDataBase(){
		try{
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/EmployeeManagement", "Daniel", "$Daniel365$");
			statement =connection.createStatement();
			
		}catch (SQLException e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Failed to connect to database!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
		
	public void addEmployee(ActionEvent event){
		String name = nameField.getText();
		String department = departmentField.getText();
		String salaryStr = salaryField.getText();
			
		if(name.isEmpty() || department.isEmpty() || salaryStr.isEmpty()){
			JOptionPane.showMessageDialog(this, "Please, provide information for all fields.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try{
			double salary = Double.parseDouble(salaryStr);
			String sql = "INSERT INTO EmpMgt (name, department, salary) VALUES ('"+name+"', '"+department+"', "+salary+")";
			statement.executeUpdate(sql);
			JOptionPane.showMessageDialog(this, "Employee added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
		} catch(SQLException e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error adding Employee.", "Error", JOptionPane.ERROR_MESSAGE);
				
		}
	}
	public void deleteEmployee(ActionEvent event){
		String idStr = idField.getText();
		if(idStr.isEmpty()){
			JOptionPane.showMessageDialog(this, "Please, enter ID to delete", "Error", JOptionPane.ERROR_MESSAGE);					
			return;	
		}
		try{
			int id = Integer.parseInt(idStr);
			String sql = "DELETE FROM EmpMgt WHERE id = "+id;
			int rowsDeleted = statement.executeUpdate(sql);
			if(rowsDeleted > 0){
				JOptionPane.showMessageDialog(this, "Employee Deleted Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
			}else{
				JOptionPane.showMessageDialog(this, "Employee not found", "Error", JOptionPane.ERROR_MESSAGE);				
			}
		}catch (SQLException e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error deleting Employee", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void viewEmployee(ActionEvent event){
		try{
			String sql = "SELECT * FROM EmpMgt";
			ResultSet resultSet = statement.executeQuery(sql);
			displayArea.setText("");

			while(resultSet.next()){
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				String department = resultSet.getString("department");
				Double salary = resultSet.getDouble("salary");
			
				displayArea.append("ID: "+id+" - Name: "+name+" - Department:"+department+" - Salary: "+salary+"\n");
			}

		}catch (SQLException e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error fetching Employees.", "Error", JOptionPane.ERROR_MESSAGE);
		}
			
	}
	public void updateEmployee(ActionEvent event){
		String idStr = idField.getText();
		String name = nameField.getText();
		String department = departmentField.getText();
		String salaryStr = salaryField.getText();

		if(idStr.isEmpty() || name.isEmpty() || department.isEmpty() || salaryStr.isEmpty()){
			JOptionPane.showMessageDialog(this, "Please, provide information for all fields.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try{
			int id = Integer.parseInt(idStr);
			double salary = Double.parseDouble(salaryStr);
				
			String sql = "UPDATE EmpMgt SET name = '"+name+"', department = '"+department+"', salary = "+salary+" WHERE id = "+id;
			int rowsUpdated = statement.executeUpdate(sql);
			if(rowsUpdated > 0){
				JOptionPane.showMessageDialog(this, "Employee Updated Successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);	
			}else{
				JOptionPane.showMessageDialog(this, "Employee not found!", "Error", JOptionPane.ERROR_MESSAGE);
			}

				
			} catch (SQLException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error updating Employee", "Error", JOptionPane.ERROR_MESSAGE);	

		}

	}
	public void searchEmployee(ActionEvent event){
		String idStr = idField.getText();
		if(idStr.isEmpty()){
			JOptionPane.showMessageDialog(this, "Please, enter ID to search for.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try{
			int id = Integer.parseInt(idStr);
			String sql = "SELECT * FROM EmpMgt WHERE id = "+id;
			ResultSet resultSet = statement.executeQuery(sql);
			displayArea.setText("");
				
			if(resultSet.next()){
				String name = resultSet.getString("name");
				String department = resultSet.getString("department");
				Double salary = resultSet.getDouble("salary");
				displayArea.append("ID: "+id+", Name: "+name+", Department: "+department+", Salary: "+salary+"\n");
			}else{
				JOptionPane.showMessageDialog(this, "Employee not found!.", "Error", JOptionPane.ERROR_MESSAGE);	
			}
								
		}catch (SQLException e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error searching for Employee!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String args[]){
		SwingUtilities.invokeLater(() -> {
			new EmployeeManagement().setVisible(true);
		});
	}
}