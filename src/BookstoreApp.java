import java.io.*; // Importing classes for input and output operations
import java.util.Scanner; // Importing Scanner class for user input

// Represents a Book with a title, author, and quantity
class Book {
    String title; // Title of the book
    String author; // Author of the book
    int quantity; // Quantity of the book available

    // Constructor to initialize the book details
    public Book(String title, String author, int quantity) {
        this.title = title; // Set the book's title
        this.author = author; // Set the book's author
        this.quantity = quantity; // Set the book's quantity
    }

    // Formats book details as a string
    @Override
    public String toString() {
        return title + "," + author + "," + quantity; // Return book details as a comma-separated string
    }
}

// Represents a node containing a Book and a link to the next node
class BookNode {
    Book data; // Book data stored in the node
    BookNode next; // Reference to the next node in the list

    // Constructor to initialize BookNode with a Book object
    public BookNode(Book data) {
        this.data = data; // Set the book data
    }
}

// Linked list for managing a collection of books
class BookList {
    private BookNode head; // Head of the linked list

    // Adds a book to the list, updates quantity if it already exists
    public void addBook(Book book, boolean updateFile) {
        BookNode current = head; // Start from the head of the list
        while (current != null) { // Traverse the list
            if (current.data.title.equalsIgnoreCase(book.title)) { // Check if book already exists
                current.data.quantity += book.quantity; // Increase quantity if book exists
                if (updateFile) {
                    saveBooksToFile(); // Save updated list to file
                }
                System.out.println("Updated book quantity: " + current.data);
                return; // Exit the method
            }
            current = current.next; // Move to the next node
        }
        // If book does not exist, add it as a new node
        BookNode newNode = new BookNode(book);
        if (head == null) { // If list is empty, set new node as head
            head = newNode;
        } else {
            current = head;
            while (current.next != null) { // Traverse to the end of the list
                current = current.next;
            }
            current.next = newNode; // Add new node at the end
        }
        sortBooksByTitle(); // Sort books by title
        if (updateFile) {
            saveBooksToFile(); // Save updated list to file
        }
        System.out.println("Book added: " + book);
    }

    // Removes a book by title from the list
    public void removeBook(String title) {
        if (head == null) return; // If the list is empty, do nothing

        // Checks if the book to be removed is the head
        if (head.data.title.equalsIgnoreCase(title)) {
            head = head.next; // Remove the head node
            System.out.println("Removed book: " + title);
            saveBooksToFile(); // Save updated list to file
            return;
        }

        // Traverses the list to find and remove the book
        BookNode current = head;
        while (current.next != null && !current.next.data.title.equalsIgnoreCase(title)) {
            current = current.next; // Move to the next node
        }
        if (current.next != null) { // Book found
            System.out.println("Removed book: " + title);
            current.next = current.next.next; // Remove the node
            saveBooksToFile(); // Save updated list to file
        } else {
            System.out.println("Book not found: " + title);
        }
    }

    // Searches for a book by title
    public Book searchBook(String title) {
        BookNode current = head; // Start from the head of the list
        while (current != null) { // Traverse the list
            if (current.data.title.equalsIgnoreCase(title)) {
                return current.data; // Returns book if found
            }
            current = current.next; // Move to the next node
        }
        return null; // Returns null if book is not found
    }

    // Displays all books in the list
    public void viewBooks() {
        sortBooksByTitle(); // Sorts the list by title before displaying
        BookNode current = head; // Start from the head of the list
        while (current != null) { // Traverse the list
            System.out.println("Title: " + current.data.title + ", Author: " + current.data.author + ", Quantity: " + current.data.quantity);
            current = current.next; // Move to the next node
        }
    }

    // Loads books from a file and adds them to the list
    public void loadBooksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) { // Read each line from the file
                String[] parts = line.split(","); // Split line into parts
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
            BookNode current = head; // Start from the head of the list
            while (current != null) { // Traverse the list
                writer.println(current.data); // Writes each book as a line
                current = current.next; // Move to the next node
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
                    private BookNode current = head; // Start from the head of the list

                    @Override
                    public boolean hasNext() {
                        return current != null; // Check if there is a next node
                    }

                    @Override
                    public Book next() {
                        Book book = current.data; // Get the current book
                        current = current.next; // Move to the next node
                        return book; // Return the current book
                    }
                };
            }
        };
    }

    // Sorts the books in the list alphabetically by title
    public void sortBooksByTitle() {
        if (head == null || head.next == null) return; // If list is empty or has one element, do nothing

        BookNode sorted = null; // Start with an empty sorted list
        BookNode current = head; // Start from the head of the list

        // Insertion sort for linked list
        while (current != null) {
            BookNode next = current.next; // Store next node
            if (sorted == null || sorted.data.title.compareTo(current.data.title) >= 0) {
                current.next = sorted; // Insert at the beginning
                sorted = current;
            } else {
                BookNode temp = sorted;
                while (temp.next != null && temp.next.data.title.compareTo(current.data.title) < 0) {
                    temp = temp.next; // Find the correct position
                }
                current.next = temp.next; // Insert in the middle or end
                temp.next = current;
            }
            current = next; // Move to the next node
        }
        head = sorted; // Update head to the sorted list
    }
}

// Represents a customer's order
class Order {
    String customerName; // Customer's name
    BookList books; // List of books in the order

    public Order(String customerName) {
        this.customerName = customerName; // Set the customer's name
        this.books = new BookList(); // Initialize the book list
    }

    // Returns a string representation of the order including customer name and books
    public String toOrderString() {
        StringBuilder orderDetails = new StringBuilder(customerName); // Start with customer name
        for (Book book : books.getBooks()) {
            orderDetails.append(" | ").append(book.toString()); // Append each book's details
        }
        return orderDetails.toString(); // Return the order details
    }

    @Override
    public String toString() {
        return customerName; // Return the customer's name
    }
}

// Node to store an Order in a queue
class OrderNode {
    Order data; // Order data stored in the node
    OrderNode next; // Reference to the next node in the queue

    public OrderNode(Order data) {
        this.data = data; // Set the order data
    }
}

// Queue for managing a list of orders
class OrderQueue {
    private OrderNode head, tail; // Head and tail of the queue
    private BookList bookList; // Reference to the BookList

    public OrderQueue(BookList bookList) {
        this.bookList = bookList; // Set the reference to the BookList
    }

    // Adds a new order to the end of the queue
    public void enqueue(Order order) {
        OrderNode newNode = new OrderNode(order); // Create a new node for the order
        if (tail == null) { // If queue is empty
            head = tail = newNode; // Set head and tail to the new node
        } else {
            tail.next = newNode; // Add new node at the end
            tail = newNode; // Update tail
        }
        saveOrdersToFile(); // Save updated queue to file
        System.out.println("Order added: " + order);
    }

    // Removes and returns the order at the front of the queue
    public Order dequeue() {
        if (head == null) return null; // If queue is empty, return null
        OrderNode temp = head; // Store the head node
        head = head.next; // Move head to the next node
        if (head == null) tail = null; // If queue is now empty, reset tail

        // Reduce the quantity of each book in the order
        for (Book book : temp.data.books.getBooks()) {
            Book inventoryBook = bookList.searchBook(book.title); // Find the book in inventory
            if (inventoryBook != null) {
                inventoryBook.quantity -= book.quantity; // Reduce quantity in inventory
                // Remove the book if its quantity reaches zero
                if (inventoryBook.quantity <= 0) {
                    bookList.removeBook(inventoryBook.title); // Remove book from inventory
                }
            }
        }
        bookList.saveBooksToFile(); // Save updated inventory
        saveOrdersToFile(); // Save updated queue to file
        System.out.println("Order completed: " + temp.data);
        return temp.data; // Return the completed order
    }

    // Displays all orders in the queue
    public void viewOrders() {
        OrderNode current = head; // Start from the head of the queue
        while (current != null) { // Traverse the queue
            System.out.println("Order for customer: " + current.data.customerName);
            for (Book book : current.data.books.getBooks()) {
                System.out.println("  - " + book); // Display each book in the order
            }
            current = current.next; // Move to the next node
        }
    }

    // Loads orders from a file and adds them to the queue
    public void loadOrdersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("orders.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) { // Read each line from the file
                enqueue(new Order(line.trim())); // Adds each order to the queue
            }
        } catch (IOException e) {
            System.out.println("No previous order data found.");
        }
    }

    // Saves all orders in the queue to a file
    private void saveOrdersToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("orders.txt"))) {
            OrderNode current = head; // Start from the head of the queue
            while (current != null) { // Traverse the queue
                writer.println(current.data.toOrderString()); // Writes each order with books
                current = current.next; // Move to the next node
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
    private static Scanner scanner = new Scanner(System.in); // Scanner for user input

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

            choice = scanner.nextInt(); // Get user choice
            scanner.nextLine(); // clear newline

            // Perform action based on user choice
            switch (choice) {
                case 1 -> addBook(); // Add a book
                case 2 -> removeBook(); // Remove a book
                case 3 -> bookList.viewBooks(); // View all books
                case 4 -> searchBook(); // Search for a book
                case 5 -> addOrder(); // Add an order
                case 6 -> orderQueue.dequeue(); // Complete next order
                case 7 -> orderQueue.viewOrders(); // View all orders
                case 8 -> System.out.println("Exiting system."); // Exit the system
                default -> System.out.println("Invalid option."); // Invalid choice
            }
        } while (choice != 8); // Exit the loop if user chooses option 8
    }

    // Prompts user to add a book to the list
    private static void addBook() {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine(); // Get book title
        System.out.print("Enter author: ");
        String author = scanner.nextLine(); // Get book author
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt(); // Get book quantity
        scanner.nextLine(); // clear newline
        bookList.addBook(new Book(title, author, quantity), true); // Save to file after adding
    }

    // Prompts user to remove a book from the list
    private static void removeBook() {
        System.out.print("Enter title of book to remove: ");
        String title = scanner.nextLine(); // Get book title to remove
        bookList.removeBook(title); // Remove the book
    }

    // Prompts user to search for a book by title
    private static void searchBook() {
        System.out.print("Enter book title to search: ");
        String title = scanner.nextLine(); // Get book title to search
        Book foundBook = bookList.searchBook(title); // Search for the book
        System.out.println((foundBook != null) ? "Found: " + foundBook : "Book not found."); // Display result
    }

    // Prompts user to add an order for a customer
    private static void addOrder() {
        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine(); // Get customer name
        Order order = new Order(customerName); // Create a new order

        // Prompts user to add books to the order
        while (true) {
            System.out.print("Enter book title to add to order (or 'done' to finish): ");
            String title = scanner.nextLine(); // Get book title to add
            if (title.equalsIgnoreCase("done")) break; // Exit loop if done

            Book book = bookList.searchBook(title); // Search for the book
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
