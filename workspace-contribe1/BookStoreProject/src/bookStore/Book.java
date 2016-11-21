package bookStore;
import java.sql.*;
import java.util.Enumeration;
import java.util.InputMismatchException;
import java.util.Scanner;
public class Book implements BookList{
	   private String title;
	   private String author;
	   private float prices;
	   private int quantity;
	   Connection con=null;
	   Statement stmt = null;
	   PreparedStatement pstmt = null;
       ResultSet rs = null;
       Scanner sc;
       Book[] bookDetails;
       public enum Status {
    	   OK(0),
    	   NOT_IN_STOCK(1),
    	   DOES_NOT_EXIST(2) ; 
			private int statusCode;

		    private Status(int statusCode) {
		        this.statusCode = statusCode;
		    }
		}
	public Book() {
		super();
		
		getDBConnection();
		userOperations();
	}
	public void getDBConnection()
    {
		System.out.println(".........!!Welcome to Book store!!........");
		try{
            String url="jdbc:oracle:thin:@localhost:1521:XE";
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con=DriverManager.getConnection(url,"system","1234");
            stmt= con.createStatement();
            String sql="select * from books";
             rs= stmt.executeQuery(sql);
             System.out.println("Title\t\tAuthor\t\tprice\tQuantity");
             while(rs.next())
             {
            	 
             System.out.println(rs.getString(1)+"\t"+rs.getString(2)+"\t\t"+rs.getInt(3)+"\t\t"+rs.getInt(4));
             }
            
            
		}catch(Exception e)
        {    
            System.out.println(e);
        }
    }
	public static void main(String args[])
	{
		new Book();
		
	}
	 void userOperations(){
		int choice=0;
		do{
			System.out.println("\n ----------------Please select one of the following operations!------------");
			System.out.println("\n 1. Buy a book");
			System.out.println("\n 2. Add Book to sore");
			System.out.println("\n 3. Remove Book from store");
			System.out.println("\n 4. Search a Book");
			System.out.println("\n 5. View BookStore");
			System.out.println("\n 6. Update prices/quanity in BookStore");
			System.out.println("\n 7. EXIT");
			System.out.println("\n --------------------------------------------------------------------------");
			System.out.println(" Enter the number of one of the above options(1 to 7).");
			try{
				sc=new Scanner(System.in);
				choice=sc.nextInt();
			}
			catch(InputMismatchException e)
			{
				System.out.println("--->Please type only the numbers assigned to above options.");
			}
			if(choice==1){
				buy();
				}
			else if(choice==2){
				add(null, 0);
			}
			
			else if(choice==3){
				System.out.println("-----------------Enter the following details to remove a book from store--------------------");
				System.out.println("enter the book title");
				sc=new Scanner(System.in);
				String bookName= sc.nextLine();
				int rValue= checkingIfBookexists(bookName);
				int cQuantity= checkingNoOfBooks(bookName);
				if(rValue==0){
					System.out.println("--->Please check book name and try again.");
					userOperations();
				}
				else{
					if((rValue!=0)&&(cQuantity>0)){
						int remove;
						System.out.println("How many "+bookName+"books you want to remove from store?");
						try{
							remove=sc.nextInt();
						if(cQuantity>remove){
							try {
								String sql = "UPDATE books SET quantity = ? WHERE title = ?";
								pstmt = con.prepareStatement(sql);
								pstmt.setInt(1, (cQuantity-remove));
								pstmt.setString(2, bookName);
								int x=pstmt.executeUpdate();
								if(x>0){
									System.out.println("--------------Book Deleted and quanity updated!-----------");
									userOperations();
								}
									else{
									System.out.println("-------------Book not deleted!-----------");
									userOperations();
									}
								}
							catch(SQLException e){
							System.out.println(e);
							}
						}
						
						}
						catch(InputMismatchException e){
							System.out.println("Invalid input");
							userOperations();
							}
					}
					else
					System.out.println("--->The Book is"+Status.values()[1]+"Sorry. Only "+cQuantity+" books are available------------");
				}	
				}//elseif
			
			else if(choice==4){ 
				System.out.println("Enter the book title or author name to search");
				sc=new Scanner(System.in);
				String searchBook=sc.nextLine().trim(); 
				System.out.println("->"+searchBook);
				list(searchBook);
				    
				
			}
			else if(choice==5){
				try{
					stmt= con.createStatement();
		            String sql="select * from books";
		             rs= stmt.executeQuery(sql);
		             System.out.println("---------------View BookStore-----------------");
		             System.out.println("Title\t\tAuthor\t\tprice\tQuantity");
		             	while(rs.next()){
		             		System.out.println(rs.getString(1)+"\t"+rs.getString(2)+"\t\t"+rs.getInt(3)+"\t\t"+rs.getInt(4));
		             	}
				}
				catch (SQLException e){
					System.out.println(e);
				}
				userOperations();
			}
				
			else if(choice==6){
				System.out.println("------------Bookstore updation in progress---------------");
				System.out.println("--->Enter the book name to update.");
				sc=new Scanner(System.in);
				
				String bookName= sc.nextLine();
				int rValue=checkingIfBookexists(bookName);
				if(rValue==0){
					//System.out.println("--->The Book "+Status.values()[2]+". Please try again.");
					userOperations();
				}
					else{
					System.out.println("--->What do you want to update?"+ "\n a. Price"+ "\n b. Quantity");
					System.out.println("-----------------------------------------------");
					int updateChoice=sc.next().charAt(0);
							if(updateChoice=='a'){
								float price=0;
								System.out.println("--->Enter the value of price.");	
								sc=new Scanner(System.in);
								try{price=sc.nextFloat();}
								catch(InputMismatchException e){System.out.println("Please enter a valid input."); userOperations();}
								try{
								String sql="update books set price=? where title=?";
								pstmt= con.prepareStatement(sql);
								pstmt.setFloat(1, price);
								pstmt.setString(2, bookName);
								int x = pstmt.executeUpdate();
								if(x>0){
									System.out.println("--->The price is updated.");
									userOperations();
								}
									else
									System.out.println("--->The price is not updated.");
								}
								catch(SQLException e){
									System.out.println(e);
								}
						
					}
					else if(updateChoice=='b'){
						System.out.println("--->Enter the value for quantity.");	
						int quantity=0;
						try{
						quantity=sc.nextInt();}
						catch(InputMismatchException e){System.out.println("Please enter a valid input."); userOperations();}
						try{
						String sql="update books set quantity=? where title=?";
						pstmt= con.prepareStatement(sql);
						pstmt.setInt(1, quantity);
						pstmt.setString(2, bookName);
						int x = pstmt.executeUpdate();
						if(x>0){
							System.out.println("--->The Quantity is updated.");
							userOperations();
						}
						else
							System.out.println("--->The Quantity is not updated.");
						}
						catch(SQLException e){
							System.out.println(e);
						}
					}
					else{
						System.out.println("Invalid choice."); 
						userOperations();
					}
				}
				
				}
			else if(choice==7){
				System.out.println("The bookstore app is stopped/shut down.");
				System.exit(0);
				}
			else{
				System.out.println("--->Enter a valid choice!!");
				userOperations();
				}
			}while(choice==0);
		}
	 
	private void emptyCart() {
		// TODO Auto-generated method stub
		int rows=0;
		try{
			//System.out.println("emptying cart");
			String sql = "select count(*) from bookscart";
			pstmt= con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()){rows=rs.getInt(1);}
			//System.out.println(rows);
				if(rows!=0){
					pstmt = con.prepareStatement("delete from bookscart");
					int x=pstmt.executeUpdate();
					//System.out.println(x);
						if(x>0){con.commit();
							//System.out.println("The cart is cleared after the purchase.");
						}
						else{
							//System.out.println("The cart is not cleared after the purchase.");
							}
						}
				else
					System.out.println("no rows in cart table");
		}
		catch(SQLException e){
			System.out.println(e);
		}
	}
	private void rollBackMainTable() {
		// TODO Auto-generated method stub
		String bookTitles[]; int [] bookQuantity;int numRows=0;
		bookTitles = new String [20];
		bookQuantity=new int[20];
		try{
			String sql1="Select count(*) from bookscart";
			pstmt = con.prepareStatement(sql1);
			rs = pstmt.executeQuery(sql1);
					while(rs.next()) {
						numRows=rs.getInt(1);
					}
			}
			catch(SQLException e){System.out.println(e);}
		try{
		String sql1="Select * from bookscart";
		pstmt = con.prepareStatement(sql1);
		rs = pstmt.executeQuery(sql1);
				for (int i=0;rs.next();i++) {
					bookTitles[i] = rs.getString(1);
					bookQuantity[i] = rs.getInt(3);
				}
		}
		catch(SQLException e){System.out.println(e);}
		/*for (int i=0;i<numRows;i++){
			System.out.println(bookQuantity[i]+bookTitles[i]);}*/
		for (int j=0;j<numRows;j++){
			int current=checkingNoOfBooks(bookTitles[j]);
		try{
			String uSql = "UPDATE books SET quantity = ? WHERE title = ?";
			pstmt = con.prepareStatement(uSql);
			pstmt.setInt(1,bookQuantity[j]+current);
			pstmt.setString(2,bookTitles[j]);
			int x=pstmt.executeUpdate();
			/*if(x>0)
				System.out.println("Main table is rolledback.");
			else
				System.out.println("Main table is not rolledback.");*/
			}
			catch(SQLException e){
			System.out.println(e);
			}
		}
	}
	private void updateMainTable(String bookName, int cQuantity, int buyQuantity) {
		// TODO Auto-generated method stub
		try{
		String uSql = "UPDATE books SET quantity = ? WHERE title = ?";
		pstmt = con.prepareStatement(uSql);
		pstmt.setInt(1,(cQuantity-buyQuantity));
		pstmt.setString(2,bookName);
		int x=pstmt .executeUpdate();
		if(x>0)
			System.out.println("Main table updated after purchase.");
		else
			System.out.println("Main table is not updated after purchase.");
		}
		catch(SQLException e){
		System.out.println(e);
		}
	}
	private void showOrder() {
		// TODO Auto-generated method stub
		try{
			String sql = "select * from bookscart";
			pstmt= con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				System.out.println(rs.getString(1)+" \t\t"+rs.getFloat(2)+"\t\t"+rs.getInt(3));
				}
			}
		catch(SQLException e){
			System.out.println(e);
			}
	}
	private float totalPriceofPurchase() {
		// TODO Auto-generated method stub
		float tPrice=0;
			try{
				String sql = "select * from bookscart";
				pstmt= con.prepareStatement(sql);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					tPrice = tPrice+rs.getInt(2)*rs.getInt(3);
					}
				}
			catch(SQLException e){
				System.out.println(e);
				}
			return tPrice;
	}
	private void updateCart(String bookName, float price2, int buyQuantity) {
		// TODO Auto-generated method stub
		
		int rows=findBookInCart(bookName);
		if(rows==0){
		try{
			String sql = "INSERT INTO bookscart (title, price, quantity) VALUES (?,?,?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, bookName);
			pstmt.setFloat(2, price2);
			pstmt.setInt(3, buyQuantity);
			pstmt.executeUpdate();
				/*if(x>0)
				System.out.println("--->Book added to your cart.");
				else
				System.out.println("--->Book is not added to your cart.");*/
			}
		catch(SQLException e){
		System.out.println(e);	
			}
		}
		else{

			int a=checkingNoOfBooksInCart(bookName);
			try{
				String sql = "update bookscart set quantity=? where title=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, (a+buyQuantity));
				pstmt.setString(2, bookName);
				pstmt.executeUpdate();
				}
			catch(SQLException e){
			System.out.println(e);	
				}
		}
	}
	private int findBookInCart(String bookName) {
		// TODO Auto-generated method stub
		int rows=0;
		try{
		String sql = "select count(*) from bookscart where title=?";
		pstmt= con.prepareStatement(sql);
		pstmt.setString(1, bookName);
		rs = pstmt.executeQuery();
		while(rs.next()){rows=rs.getInt(1);}
		//System.out.println(rows);
		}
		catch(SQLException e){
		System.out.println(e);
		}
		return rows;
	}
	private float getPriceOfBook(String bookName) {
		// TODO Auto-generated method stub
		float price=0;
		try{
		String sql = "select * from books where title=?";
		pstmt= con.prepareStatement(sql);
		pstmt.setString(1, bookName);
		rs = pstmt.executeQuery();
			while (rs.next()) {
				price = rs.getFloat(3);
				}
			}
		catch(SQLException e){
			System.out.println(e);
			}
		return price;
	}
	private int checkingNoOfBooksInCart(String a) {
		// TODO Auto-generated method stub
		int quantity=0;
		try{
		String sql = "select * from bookscart where title=?";
		pstmt= con.prepareStatement(sql);
		pstmt.setString(1, a);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			quantity = rs.getInt(3);
			}
		//System.out.println("--->Quantity in cart: "+quantity);
		//System.out.println("--->"+newBook+" Books in cart are: "+quantity);
		}
		catch(SQLException e){
			System.out.println(e);
			}
		return quantity;
	}
	private int checkingNoOfBooks(String bookName) {
		// TODO Auto-generated method stub
			int quantity=0;
			try{
			String sql = "select * from books where title=?";
			pstmt= con.prepareStatement(sql);
			pstmt.setString(1, bookName);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				quantity = rs.getInt(4);
				}
			//System.out.println("--->No. of '"+bookName+"' Books available:"+quantity);
			}
			catch(SQLException e){
				System.out.println(e);
				}
			return quantity;
	}
		private int checkingIfBookexists(String bookName) {
		// TODO Auto-generated method stub
			int count=0;
			try{
			String sql = "select count(*) from books where title=?";
			pstmt= con.prepareStatement(sql);
			pstmt.setString(1, bookName);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
				}
			if(count!=0)
			System.out.println("--->"+Status.values()[0]+". Book is registered in store.");
			else
				System.out.println("--->Book "+Status.values()[2]+" in store.");	
			}
			catch(SQLException e){
				System.out.println(e);
				}
			return count;
	}
		//useroperation
	
		private void viewOrModifyOrder(String bookName, int cQuantity, int buyQuantity) {
		// TODO Auto-generated method stub
			//System.out.println("--->The Book name is registered in the store.");
			 int newQuantity=0,a=0,b=0; float price1,totalPrice=0;String addBook="";
			float price=getPriceOfBook(bookName);
			//System.out.println("pricemethod"+price);
			if((cQuantity>=buyQuantity)&&(cQuantity>0)){
			//updateCart(bookName,price,buyQuantity);
			System.out.println("Select one of the following options?");
			System.out.println("\n a). Confirm order");
			System.out.println("\n b). Modify order");
			System.out.println("\n c). Cancel order");
			char ch=sc.next().charAt(0);
				if(ch=='a'){
					System.out.println("------------YOUR ORDER-------------");
					showOrder();
					totalPrice=totalPriceofPurchase();
					System.out.println("------------------------------------------------");
					System.out.println("---> The total order costs: "+totalPrice+" $");
					System.out.println("---> Order placed successfully.!");
					updateMainTable(bookName,cQuantity,buyQuantity);
					emptyCart();
					userOperations();
				}
				else if(ch=='b'){
					System.out.println("---------------- Modify your order----------------");
				System.out.println("---> You have currently ordered "+buyQuantity+" "+bookName+" books.");
				System.out.println("---> How do you want to modify order?");
				System.out.println("\n a). Add another book.");
				System.out.println("\n b). Delete book.");
				System.out.println("------------------------------------------------");
				System.out.println("--->Type a/b to continue...");
				try{
				char ch1=sc.next().charAt(0);
					 if(ch1=='a'){
						
						System.out.println("---> Name the book you want to add to your order?");
						sc=new Scanner(System.in);
						addBook=sc.nextLine();
						a=checkingIfBookexists(addBook);
						b= checkingNoOfBooks(addBook);
						price1=getPriceOfBook(addBook);
						System.out.println("Book price:"+price1+" $");
						if((a!=0)){
							if(b<=0){
								System.out.println("--->The Book"+Status.values()[1]);
								emptyCart();
								rollBackMainTable();
								userOperations();
							}
							else{
							System.out.println("---> How many '"+addBook+"' books you want to add to your order?");
							newQuantity=sc.nextInt();
								if(b>=newQuantity&&b>0){
									updateCart(addBook,price1,newQuantity);
									System.out.println("------------YOUR ORDER-------------");
									showOrder();
									updateMainTable(addBook,b,newQuantity);
									viewOrModifyOrder(addBook,b,newQuantity);
									float tPrice=totalPriceofPurchase();
									System.out.println("------------------------------------");
									System.out.println("---> The total order costs: "+tPrice+" $");
									System.out.println("---> Order placed successfully.!");
									emptyCart();
									userOperations();
									}
								else{
									System.out.println("--->The Book "+Status.values()[1]+"\n "+"-->No. of '"+addBook+"' books available: "+b);
									System.out.println("--->Try again.");
									emptyCart();
									rollBackMainTable();
									userOperations();
								}
							}
							}//if(a!=0)
						else{
							System.out.println("--->The Book"+Status.values()[2]);
							rollBackMainTable();
				
							emptyCart();
							userOperations();			
							}	
					}
					else if(ch1=='b'){
					deleteBookFromOrder();
					userOperations();
					}
					else{
					System.out.println("--->Enter valid choice.");
					rollBackMainTable();
					//rollBackMainTable(addBook,b,newQuantity);
					emptyCart();
					userOperations();	
					}
				}
				catch(InputMismatchException e){
					System.out.println("--->Enter only the label given to the above choices and try again.");
					rollBackMainTable();
					emptyCart();
					userOperations();
				}
				}
			else if(ch=='c'){
				System.out.println("Order is cancelled!");
				rollBackMainTable();
				emptyCart();
				userOperations();
			}
			else{
				System.out.println("Order is cancelled! Please enter valid choice.");
				rollBackMainTable();
				emptyCart();
				userOperations();
			}
		}
		else{
		System.out.println("--->The_BOOK"+Status.values()[1]);
		rollBackMainTable();
		emptyCart();
		userOperations();
		}
	}

	
		private void deleteBookFromOrder() {
			// TODO Auto-generated method stub
			 int newQuantity=0,a=0,b=0; float price1,totalPrice=0;String addBook="";
			System.out.println("--->Enter book name to remove form order");
			sc=new Scanner(System.in);
			String bookName= sc.nextLine();
			int rValue= findBookInCart(bookName);
			int cQuantity= checkingNoOfBooksInCart(bookName);
			if(rValue==0){
				System.out.println("Book is not in your order. Please check book name and try again.");
				deleteBookFromOrder();
			}
			else{
				if((rValue!=0)&&(cQuantity>0)){
					try{
					System.out.println("How many "+bookName+"books you want to remove from cart?");
					int remove=sc.nextInt();
					if(cQuantity>=remove){
						try {
							String sql = "UPDATE bookscart SET quantity = ? WHERE title = ?";
							pstmt = con.prepareStatement(sql);
							pstmt.setInt(1, (cQuantity-remove));
							pstmt.setString(2, bookName);
							int x=pstmt.executeUpdate();
							if(x>0){
								System.out.println("--->Book Deleted and cart updated!");
								System.out.println("Select one of the following options?");
								System.out.println("\n a). Confirm order");
								System.out.println("\n b). Modify order");
								System.out.println("\n c). Cancel order");
								char ch=sc.next().charAt(0);
								if(ch=='a'){
									System.out.println("------------YOUR ORDER-------------");
									showOrder();
									totalPrice=totalPriceofPurchase();
									System.out.println("------------------------------------------------");
									System.out.println("---> The total order costs: "+totalPrice+" $");
									System.out.println("---> Order placed successfully.!");
									//updateMainTable(bookName,cQuantity,buyQuantity);
									emptyCart();
									userOperations();
								}
								else if(ch=='b'){
									System.out.println("---------------- Modify your order----------------");
								//System.out.println("---> You have currently ordered "+buyQuantity+" "+bookName+" books.");
								System.out.println("---> How do you want to modify order?");
								System.out.println("\n a). Add another book.");
								System.out.println("\n b). Delete book.");
								System.out.println("------------------------------------------------");
								System.out.println("--->Type a/b to continue...");
								try{
								char ch1=sc.next().charAt(0);
									 if(ch1=='a'){
										
										System.out.println("---> Name the book you want to add to your order?");
										sc=new Scanner(System.in);
										addBook=sc.nextLine();
										a=checkingIfBookexists(addBook);
										b= checkingNoOfBooks(addBook);
										price1=getPriceOfBook(addBook);
										System.out.println("Book price:"+price1+" $");
										if((a!=0)){
											if(b<=0){
												System.out.println("--->The Book"+Status.values()[1]);
												emptyCart();
												rollBackMainTable();
												userOperations();
											}
											else{
											System.out.println("---> How many '"+addBook+"' books you want to add to your order?");
											newQuantity=sc.nextInt();
												if(b>=newQuantity&&b>0){
													updateCart(addBook,price1,newQuantity);
													System.out.println("------------YOUR ORDER-------------");
													showOrder();
													updateMainTable(addBook,b,newQuantity);
													viewOrModifyOrder(addBook,b,newQuantity);
													float tPrice=totalPriceofPurchase();
													System.out.println("------------------------------------");
													System.out.println("---> The total order costs: "+tPrice+" $");
													System.out.println("---> Order placed successfully.!");
													emptyCart();
													userOperations();
													}
												else{
													System.out.println("--->The Book "+Status.values()[1]+"\n "+"-->No. of '"+addBook+"' books available: "+b);
													System.out.println("--->Try again.");
													emptyCart();
													rollBackMainTable();
													userOperations();
												}
											}
											}//if(a!=0)
										else{
											System.out.println("--->The Book"+Status.values()[2]);
											rollBackMainTable();
								
											emptyCart();
											userOperations();			
											}	
									}
									else if(ch1=='b'){
									deleteBookFromOrder();
									userOperations();
									}
									else{
									System.out.println("--->Enter valid choice.");
									rollBackMainTable();
									//rollBackMainTable(addBook,b,newQuantity);
									emptyCart();
									userOperations();	
									}
								}
								catch(InputMismatchException e){
									System.out.println("--->Enter only the label given to the above choices and try again.");
									rollBackMainTable();
									emptyCart();
									userOperations();
								}
								}//elseif(ch=b)
							else if(ch=='c'){
								System.out.println("Order is cancelled!");
								rollBackMainTable();
								emptyCart();
								userOperations();
							}
							else{
								System.out.println("Order is cancelled! Please enter valid choice.");
								rollBackMainTable();
								emptyCart();
								userOperations();
							}
						}
						else{
						System.out.println("--->The_BOOK"+Status.values()[1]);
						rollBackMainTable();
						emptyCart();
						userOperations();
						}
						
					}
					catch(SQLException e){
						System.out.println("Enter valid input");
						}
					}
					else{
					System.out.println("--->Book not deleted from cart!");
					rollBackMainTable();
					emptyCart();
					userOperations();
							}
						}
						catch(InputMismatchException e){
						System.out.println(e);
						rollBackMainTable();
						emptyCart();
						userOperations();
						}
					}
					else{
						System.out.println("---> Sorry only "+cQuantity+" books are available in the cart");
						rollBackMainTable();
						emptyCart();
						userOperations();
					}
				}
					
					
		}
		@Override
		public Book[] list(String searchString) {
			// TODO Auto-generated method stub
			//System.out.println("list--"+searchString);
			if(!(searchString instanceof String)) throw new IllegalArgumentException();
			try {
				String sql = "select * FROM books where title=? or author=?";
				pstmt = con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
				pstmt.setString(1, searchString);
				pstmt.setString(2, searchString);
				rs=pstmt.executeQuery();
					if (!rs.isBeforeFirst() ) {    
						System.out.println("--->The Book "+Status.values()[2]);
						System.out.println("Please check and use the book/author name as shown above");
						userOperations();
						} 
					else{
						System.out.println("Title\t\tAuthor\t\tprice\tQuantity");
						while(rs.next()){
						System.out.println(rs.getString(1)+"\t"+rs.getString(2)+"\t\t"+rs.getInt(3)+"\t\t"+rs.getInt(4));
						}rs.beforeFirst();userOperations();
						}
					
		      		}
		     
		      catch(NullPointerException |SQLException e){
		    	  System.out.println("Please check and use the book/author name as shown above"); 
		      }

			return null;
		}
		@Override
		public boolean add(Book book, int quantity) {
			// TODO Auto-generated method stub
			System.out.println("-----------------Enter the following details to ADD the book----------------------------");
			System.out.println("Enter the book title");
			sc=new Scanner(System.in);
			String addBookTitle=sc.nextLine();
			int rValue= checkingIfBookexists(addBookTitle);
				if(rValue==0){
					System.out.println("--->So, the book will be added to store as a new entry.");
					System.out.println("--->enter the name of book author");
					String addBookAuthor=sc.nextLine();
					float addBookPrice; int addBookQuantity;
					try{
					System.out.println("enter the book price");
					addBookPrice=sc.nextFloat();
					System.out.println("enter the quantity of book you want to add");
					addBookQuantity=sc.nextInt();
							try{
								pstmt=con.prepareStatement("insert into books(title,author, price,quantity)values(?,?,?,?)");
								pstmt.setString(1, addBookTitle);
								pstmt.setString(2, addBookAuthor);
								pstmt.setFloat(3, addBookPrice);
								pstmt.setInt(4, addBookQuantity);
								int x=pstmt.executeUpdate();
								if(x>0){
									System.out.println("--->The book is added to store! check the book in 6.viewstore-------------------");
									userOperations();
								}
									else
									System.out.println("The book is not added!");
								}
							catch(SQLException e){
								System.out.println(e);
								}
					}
				catch(InputMismatchException e){
					System.out.println("Invalid Input. Please try again.");
					userOperations();
					}
				}
				else{
					System.out.println(Status.values()[0]);
					System.out.println("how many books you want to add?");
					try{
					int addQuantity=sc.nextInt();
						try{
							int x= checkingNoOfBooks(addBookTitle);
							x+=addQuantity;
							String sql = "UPDATE books SET quantity = ? WHERE title = ?";
							pstmt = con.prepareStatement(sql);
							pstmt.setInt(1, x);
							pstmt.setString(2, addBookTitle);
							pstmt.executeUpdate();
							System.out.println("-----------The book is added to the store--------------");
							userOperations();
						}
						catch(SQLException e){
						System.out.println(e);
						}
					}
					catch(InputMismatchException e){
						System.out.println("________Enter valid input and try again!!!______");
						userOperations();
					}
				}
			return false;
		}
		@Override
		public int[] buy(Book... books) {
			// TODO Auto-generated method stub
			System.out.println("--->Enter the name/title of the book you want to buy.");
			sc=new Scanner(System.in);
			String bookName= sc.nextLine();
			int rValue= checkingIfBookexists(bookName);
				if(rValue==0){
					//System.out.println(Status.values()[2]+" Please check book name and try again.");
					userOperations();
					}
				else{
						int numBooks=checkingNoOfBooks(bookName);
						if(numBooks<=0){
						System.out.println("--->"+Status.values()[1]);
						userOperations();
						}
						else{
							try{
								System.out.println("--->How many"+" '"+bookName+"' "+" books you want to buy.");
								int buyQuantity= sc.nextInt();
								System.out.println("--->Buying Quanity: "+buyQuantity);
								float price=getPriceOfBook(bookName);
								System.out.println("--->Book Price: "+price+" $");
								int cQuantity=checkingNoOfBooks(bookName);
								updateCart(bookName,price,buyQuantity);
								updateMainTable(bookName,cQuantity,buyQuantity);
								viewOrModifyOrder(bookName,cQuantity,buyQuantity);
							}
							catch(InputMismatchException e){
								System.out.println("enter valid input and try again.");
								userOperations();
							}
						}
					}
			return null;
		}
		

}//class

