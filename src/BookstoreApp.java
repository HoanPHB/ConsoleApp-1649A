import java.io.*;
import java.util.Scanner;

// Represents a Book with a title, author, and quantity
class Book {
    String title;
    String author;
    int quantity;

    // Constructor to initialize the book details
    public Book(String title, String author, int quantity) {
        this.title = title;
        this.author = author;
        this.quantity = quantity;
    }

    // Formats book details as a string
    @Override
    public String toString() {
        return title + "," + author + "," + quantity;
    }
}

// Represents a node containing a Book and a link to the next node
class BookNode {
    Book data;
    BookNode next;

    // Constructor to initialize BookNode with a Book object
    public BookNode(Book data) {
        this.data = data;
    }
}

// Linked list for managing a collection of books
class BookList {
    private BookNode head;

 // Adds a book to the list, updates quantity if it already exists
public void addBook(Book book, boolean updateFile) {
    BookNode current = head;
    while (current != null) {
        if (current.data.title.equalsIgnoreCase(book.title)) {
            current.data.quantity += book.quantity; // Increase quantity if book exists
            if (updateFile) {
                saveBooksToFile();
            }
            System.out.println("Updated book quantity: " + current.data);
            return;
        }
        current = current.next;
    }
    // If book does not exist, add it as a new node
    BookNode newNode = new BookNode(book);
    if (head == null) {
        head = newNode;
    } else {
        current = head;
        while (current.next != null) {
            current = current.next;
        }
        current.next = newNode;
    }
    sortBooksByTitle();
    if (updateFile) {
        saveBooksToFile();
    }
    System.out.println("Book added: " + book);
}

    // Removes a book by title from the list
    public void removeBook(String title) {
        if (head == null) return; // If the list is empty, do nothing

        // Checks if the book to be removed is the head
        if (head.data.title.equalsIgnoreCase(title)) {
            head = head.next;
            System.out.println("Removed book: " + title);
            saveBooksToFile(); // Saves updated list to file
            return;
        }

        // Traverses the list to find and remove the book
        BookNode current = head;
        while (current.next != null && !current.next.data.title.equalsIgnoreCase(title)) {
            current = current.next;
        }
        if (current.next != null) { // Book found
            System.out.println("Removed book: " + title);
            current.next = current.next.next;
            saveBooksToFile();
        } else {
            System.out.println("Book not found: " + title);
        }
    }

    // Searches for a book by title
    public Book searchBook(String title) {
        BookNode current = head;
        while (current != null) {
            if (current.data.title.equalsIgnoreCase(title)) {
                return current.data; // Returns book if found
            }
            current = current.next;
        }
        return null; // Returns null if book is not found
    }

    // Displays all books in the list
    public void viewBooks() {
        sortBooksByTitle(); // Sorts the list by title before displaying
        BookNode current = head;
        while (current != null) {
            System.out.println("Title: " + current.data.title + ", Author: " + current.data.author + ", Quantity: " + current.data.quantity);
            current = current.next;
        }
    }

    // Loads books from a file and adds them to the list
    public void loadBooksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String title = parts[0];
                    String author = parts[1];
                    int quantity = Integer.parseInt(parts[2]);
                    addBook(new Book(title, author, quantity), false); // Adds each book to the list without saving
                }
            }
        } catch (IOException e) {
            System.out.println("No previous book data found.");
        }
    }

    // Saves all books in the list to a file
    public void saveBooksToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("books.txt"))) {
            BookNode current = head;
            while (current != null) {
                writer.println(current.data); // Writes each book as a line
                current = current.next;
            }
        } catch (IOException e) {
            System.out.println("Error saving book data.");
        }
    }
        // Provides an iterable for the books in the list
        public Iterable<Book> getBooks() {
            return new Iterable<Book>() {
                @Override
                public java.util.Iterator<Book> iterator() {
                    return new java.util.Iterator<Book>() {
                        private BookNode current = head;

                        @Override
                        public boolean hasNext() {
                            return current != null;
                        }

                        @Override
                        public Book next() {
                            Book book = current.data;
                            current = current.next;
                            return book;
                        }
                    };
                }
            };
        }

    // Sorts the books in the list alphabetically by title
    public void sortBooksByTitle() {
        if (head == null || head.next == null) return;

        BookNode sorted = null;
        BookNode current = head;

        // Insertion sort for linked list
        while (current != null) {
            BookNode next = current.next;
            if (sorted == null || sorted.data.title.compareTo(current.data.title) >= 0) {
                current.next = sorted;
                sorted = current;
            } else {
                BookNode temp = sorted;
                while (temp.next != null && temp.next.data.title.compareTo(current.data.title) < 0) {
                    temp = temp.next;
                }
                current.next = temp.next;
                temp.next = current;
            }
            current = next;
        }
        head = sorted;
    }
}

// Represents a customer's order
class Order {
    String customerName;
    BookList books;

    public Order(String customerName) {
        this.customerName = customerName;
        this.books = new BookList();
    }

    // Returns a string representation of the order including customer name and books
    public String toOrderString() {
        StringBuilder orderDetails = new StringBuilder(customerName);
        for (Book book : books.getBooks()) {
            orderDetails.append(" | ").append(book.toString());
        }
        return orderDetails.toString();
    }

    @Override
    public String toString() {
        return customerName;
    }
}

// Node to store an Order in a queue
class OrderNode {
    Order data;
    OrderNode next;

    public OrderNode(Order data) {
        this.data = data;
    }
}

// Queue for managing a list of orders
class OrderQueue {
    private OrderNode head, tail;
    private BookList bookList; // Reference to the BookList

    public OrderQueue(BookList bookList) {
        this.bookList = bookList;
    }

    // Adds a new order to the end of the queue
    public void enqueue(Order order) {
        OrderNode newNode = new OrderNode(order);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        saveOrdersToFile();
        System.out.println("Order added: " + order);
    }

// Removes and returns the order at the front of the queue
public Order dequeue() {
    if (head == null) return null; // If queue is empty, return null
    OrderNode temp = head;
    head = head.next;
    if (head == null) tail = null; // If queue is now empty, reset tail

    // Reduce the quantity of each book in the order
    for (Book book : temp.data.books.getBooks()) {
        Book inventoryBook = bookList.searchBook(book.title);
        if (inventoryBook != null) {
            inventoryBook.quantity -= book.quantity;
            // Remove the book if its quantity reaches zero
            if (inventoryBook.quantity <= 0) {
                bookList.removeBook(inventoryBook.title);
            }
        }
    }
    bookList.saveBooksToFile(); // Save updated inventory
    saveOrdersToFile();
    System.out.println("Order completed: " + temp.data);
    return temp.data;
}

    // Displays all orders in the queue
    public void viewOrders() {
        OrderNode current = head;
        while (current != null) {
            System.out.println("Order for customer: " + current.data.customerName);
            for (Book book : current.data.books.getBooks()) {
                System.out.println("  - " + book);
            }
            current = current.next;
        }
    }

    // Loads orders from a file and adds them to the queue
    public void loadOrdersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("orders.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                enqueue(new Order(line.trim())); // Adds each order to the queue
            }
        } catch (IOException e) {
            System.out.println("No previous order data found.");
        }
    }

    // Saves all orders in the queue to a file
    private void saveOrdersToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("orders.txt"))) {
            OrderNode current = head;
            while (current != null) {
                writer.println(current.data.toOrderString()); // Writes each order with books
                current = current.next;
            }
        } catch (IOException e) {
            System.out.println("Error saving order data.");
        }
    }
}

// Main application class for the bookstore management system
public class BookstoreApp {
    private static BookList bookList = new BookList(); // Manages books
    private static OrderQueue orderQueue = new OrderQueue(bookList); // Pass bookList to OrderQueue
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Load existing books and orders from files
        bookList.loadBooksFromFile();
        orderQueue.loadOrdersFromFile();

        int choice;
        do {
            // Display the main menu
            System.out.println("\n--- Bookstore Management System ---");
            System.out.println("1. Add a Book");
            System.out.println("2. Remove a Book");
            System.out.println("3. View All Books");
            System.out.println("4. Search for a Book");
            System.out.println("5. Add an Order");
            System.out.println("6. Complete Next Order");
            System.out.println("7. View All Orders");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // clear newline

            // Perform action based on user choice
            switch (choice) {
                case 1 -> addBook();
                case 2 -> removeBook();
                case 3 -> bookList.viewBooks();
                case 4 -> searchBook();
                case 5 -> addOrder();
                case 6 -> orderQueue.dequeue();
                case 7 -> orderQueue.viewOrders();
                case 8 -> System.out.println("Exiting system.");
                default -> System.out.println("Invalid option.");
            }
        } while (choice != 8); // Exit the loop if user chooses option 8
    }

    // Prompts user to add a book to the list
    private static void addBook() {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // clear newline
        bookList.addBook(new Book(title, author, quantity), true); // Save to file after adding
    }
    // Prompts user to remove a book from the list
    private static void removeBook() {
        System.out.print("Enter title of book to remove: ");
        String title = scanner.nextLine();
        bookList.removeBook(title);
    }

    // Prompts user to search for a book by title
    private static void searchBook() {
        System.out.print("Enter book title to search: ");
        String title = scanner.nextLine();
        Book foundBook = bookList.searchBook(title);
        System.out.println((foundBook != null) ? "Found: " + foundBook : "Book not found.");
    }

// Prompts user to add an order for a customer
private static void addOrder() {
    System.out.print("Enter customer name: ");
    String customerName = scanner.nextLine();
    Order order = new Order(customerName);

    // Prompts user to add books to the order
    while (true) {
        System.out.print("Enter book title to add to order (or 'done' to finish): ");
        String title = scanner.nextLine();
        if (title.equalsIgnoreCase("done")) break;

        Book book = bookList.searchBook(title);
        if (book != null && book.quantity > 0) {
            order.books.addBook(new Book(title, book.author, 1), false); // Add one copy to the order
            book.quantity--; // Reduce quantity in inventory
            if (book.quantity <= 0) {
                bookList.removeBook(book.title); // Remove book if quantity is zero
            }
            System.out.println("Added book to order: " + book);
        } else {
            System.out.println("Book not found or out of stock.");
        }
    }

    orderQueue.enqueue(order); // Adds order to the queue
    bookList.saveBooksToFile(); // Save updated inventory
}
}
