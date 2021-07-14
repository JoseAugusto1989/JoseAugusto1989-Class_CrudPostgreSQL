package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import dao.DB;
import dbException.DbException;
import dbException.DbIntegrityException;

public class StartApp {
	
	static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	static Scanner scan = new Scanner(System.in);
	static Connection conn = null;
	static PreparedStatement st = null;
	static Statement st2 = null;
	
	public static void main(String[] args) {
		
		/*
		 * Insert Data
		 */
		System.out.print("Digite o nome do Funcionario: ");
			String name = scan.nextLine();
		System.out.print("Digite o email: ");
			String email = scan.nextLine();
		//System.out.print("Digite a data de nascimento (dd/MM/yyyy): ");
			//Date date = (Date) sdf.parse(scan.next());
		System.out.print("Digite o salario base: ");
			Double salary = readDouble();
		System.out.print("Digite o Id do Departamento: ");
			Integer depart = readInteger();
			
		insertData(name, email, salary, depart);
		
		/*
		 * Update Data
		 */
		System.out.print("Digite um valor para acrescentar no salario: ");
			Double salary2 = readDouble();
		System.out.print("Qual departamento deseja acrescentar o salario dos funcionarios: ");
			int depart2 = readInteger();
	
		updateData(salary2, depart2);
		
		/*
		 * Delete Data
		 */
		
		System.out.print("Digite o Id do departemaneto para deletar: ");
			int id = readInteger();
		
		deleteData(id);
		
		/*
		 * Update More Data
		 */
		
		System.out.print("Digite o valor para alterar: ");
			double salary3 = readDouble();
		System.out.print("Digite o Id do departamento: ");
			int id3 = readInteger();
		System.out.print("Digite o valor para alterar: ");
			double salary4 = readDouble();
		System.out.print("Digite o Id do departamento: ");
			int id4 = readInteger();
			
		updateMoreData(salary3, id3, salary4, id4);
			
		/*
		 * print data
		 */
		//printData();	
		
		scan.close();
	}
	
	public static void printData() {
		
		Connection conn = null;		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			conn = DB.getConnection();
			
			st = conn.createStatement();
			rs = st.executeQuery("select * from department");
			
			while(rs.next()) {
				System.out.println(rs.getInt("Id") + ", " + rs.getString("Name"));
				
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
			DB.closeConnection();
		}		
	
	}
	
	public static void deleteData(int id) {
		
		try {
			conn = DB.getConnection();
			
			st = conn.prepareStatement(
					"DELETE FROM department "
					+ "WHERE "
					+ "Id = ?");
			
			st.setInt(1, id);
			
			int rowsAffected = st.executeUpdate();
			
			System.out.println("Done! Rows Affected: " + rowsAffected);
			
		}
		catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
			
		}
		finally {
			DB.closeStatement(st);
			DB.closeConnection();
			
		}
	
	}

	public static void updateMoreData(double value1, int id1, double value2, int id2) {
		
		try {
			conn = DB.getConnection();
			conn.setAutoCommit(false); //o operador q comanda a operação. 
			st2 = conn.createStatement();
			
			
			int row1 = st.executeUpdate(
					"UPDATE seller SET BaseSalary = " + value1 + " WHERE "
					+ "departmentid = " + id1);
			
			
			int row2 = st.executeUpdate(
					"UPDATE seller SET BaseSalary = " + value2 + " WHERE "
					+ "departmentid = " + id2);
			
			conn.commit();
			
			System.out.println("Rows 1: " + row1);
			System.out.println("Rows 2: " + row2);
			
		}
		catch (SQLException e) {
			try {
				conn.rollback();
				throw new DbException("Transaction rolled back! Caused by: " + e.getMessage());
				
			}
			catch (SQLException e1) {
				throw new DbException("Error trying to rollback! Caused by:  " + e.getMessage());
				
			}
			
		}
		finally {
			DB.closeStatement(st);
			DB.closeConnection();
			
		}
	
	}
	
	public static void updateData(Double salary, Integer depart) {
		try {
			conn = DB.getConnection();
			st = conn.prepareStatement(
					"UPDATE seller "
					+ "SET BaseSalary = BaseSalary + ? "
					+ "WHERE "
					+ "(DepartmentId = ?)");
			
			st.setDouble(1, salary);
			st.setInt(2, depart);
			
			int rowsAffected = st.executeUpdate();
			
			System.out.println("Done! Rows Affected: " + rowsAffected);
			
		}
		catch (SQLException e) {
			e.printStackTrace();
			
		}
		finally {
			DB.closeStatement(st);
			DB.closeConnection();
		}
	}
	
	public static void insertData(String name, String email, Double salary, Integer depart) {
		
		try {
			conn = DB.getConnection();
			
			st = conn.prepareStatement(
					"INSERT INTO seller " +
					"(Name, Email, BirthDate, BaseSalary, DepartmentId) " +
					"VALUES " +
					"(?, ?, ?, ?, ?)", 
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, name);
			st.setString(2, email);
			st.setDate(3, new java.sql.Date(sdf.parse("22/04/1985").getTime()));
			st.setDouble(4, salary);
			st.setInt(5, depart);
			
			/*
			st = conn.prepareStatement(
					"insert into department (Name) values ('D1'), ('D2')", 
					Statement.RETURN_GENERATED_KEYS);
			*/
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				while (rs.next()) {
					int id = rs.getInt(1);
					System.out.println("Done! Id = " + id);
				}
				
			} else {
				System.out.println("Row not Affected!");
				
			}
			
		}
		catch (SQLException e) {
			e.printStackTrace();
			
		}
		catch (ParseException e) {
			e.printStackTrace();
			
		}
		finally {
			DB.closeStatement(st);
			DB.closeConnection();
			
		}
	}
	
	private static int readInteger() {
		int value = scan.nextInt();   scan.nextLine();
		return value;
	}

	private static double readDouble() {
		double value = scan.nextDouble();   scan.nextLine();
		return value;
	}
}
