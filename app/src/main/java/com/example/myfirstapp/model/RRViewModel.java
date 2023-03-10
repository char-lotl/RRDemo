package com.example.myfirstapp.model;

import static java.util.stream.Collectors.toCollection;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.example.myfirstapp.communication.CartStatusCode;
import com.example.myfirstapp.communication.FragmentLabel;
import com.example.myfirstapp.communication.UIEventCode;
import com.example.myfirstapp.data.BookDatasource;
import com.example.myfirstapp.data.StudentDatasource;
import com.example.myfirstapp.database.AppDatabase;
import com.example.myfirstapp.database.Customer;
import com.example.myfirstapp.database.CustomerBook;
import com.example.myfirstapp.database.CustomerBookDao;
import com.example.myfirstapp.database.CustomerDao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RRViewModel extends ViewModel {

    private FragmentLabel currentScreen;
    private String currentStudent;
    private HashMap<String, ArrayList<UIBook>> studentCarts;
    private MutableLiveData<ArrayList<UIBook>> shoppingCart;
    private ArrayList<UIStudent> classroomStudents;
    private AppDatabase db;

    public RRViewModel() {
        super();
        studentCarts = new HashMap<>();
        currentScreen = FragmentLabel.CLASSROOM;
        shoppingCart = new MutableLiveData<>(new ArrayList<>());
        classroomStudents = new ArrayList<>();
    }

    public LiveData<ArrayList<UIBook>> getCart() {
        return shoppingCart;
    }

    public UIEventCode receivedBarcode(String barcode, Context c) {
        if (currentScreen == FragmentLabel.SHOPPING_CART) {
            return shoppingCartReceivedBarcode(barcode, c);
        } else {
            return UIEventCode.NONE;
            // Currently the shopping cart is the only screen that takes barcodes.
        }
    }

    public UIEventCode shoppingCartReceivedBarcode(String barcode, Context c) {
        Book b = BookDatasource.getAllBooks(c).get(barcode);
        if (b == null) {
            return UIEventCode.ISBN_NOT_FOUND;
        } else if (cartIsFull()) {
            return UIEventCode.CART_FULL;
        } else {
            addToCart(b);
            return UIEventCode.NONE;
        }
    }

    private boolean cartIsFull() {
        return (shoppingCart.getValue().size() >= 8);
    }

    private void addToCart(Book b) {
        ArrayList<UIBook> cart = shoppingCart.getValue();
        cart.add(new UIBook(b, currentStudent, this::removeFromCart));
        // Each book is bundled with a method reference for removing that book from the cart.
        // This means we don't have to provide the object responsible for deleting them a reference
        // to this ViewModel.
        shoppingCart.setValue(cart);
    }

    private void removeFromCart(int index) {
        ArrayList<UIBook> cart = shoppingCart.getValue();
        cart.remove(index);
        shoppingCart.setValue(cart);
    }

    public ArrayList<UIStudent> getClassroom(Context c) {
        if (classroomStudents.size() == 0) {
            classroomStudents = StudentDatasource.getAllStudents(c)
                    .values().stream().map(UIStudent::new)
                    .sorted(Comparator.comparing(s -> s.student.studentID))
                    .collect(toCollection(ArrayList::new));
        }

        return classroomStudents;
        // In the future, this will send them a list of only the students
        // in the current classroom!
    }

    // Notify the ViewModel that we've arrived at some fragment. Knowing the current
    // fragment is important to handling incoming barcodes correctly. This code also handles
    // the business logic for events occurring on fragment transition.
    public void navigatedToFragment(FragmentLabel fl) {
        if (currentScreen == FragmentLabel.SHOPPING_CART) { // Coming *from* the cart screen.
            saveCartToStudent(currentStudent);
        }
        if (fl == FragmentLabel.SHOPPING_CART) { // Going *to* the cart screen.
            loadCartFromStudent(currentStudent);
        }
        currentScreen = fl;
        // Importantly, none of the information we need to carry out these fragment-change events
        // is localized to the fragments themselves (it's all stored in this ViewModel as it comes
        // in via user inputs), so we don't need to scramble to extract the information before
        // the fragment is destroyed. In fact, these all actually take place shortly after the
        // *new* fragment is *created*.
    }

    private void saveCartToStudent(String studentID) {
        studentCarts.put(studentID, shoppingCart.getValue());
    }

    private void loadCartFromStudent(String studentID) {
        ArrayList<UIBook> cart = studentCarts.get(studentID);
        if (cart != null) {
            shoppingCart.setValue(cart);
        } else {
            shoppingCart.setValue(new ArrayList<>());
        }
    }

    public void clickedStudent(String studentID) {
        currentStudent = studentID;
    }

    public CartStatusCode getCartStatus(String studentID) {
        ArrayList<UIBook> cart = studentCarts.get(studentID);
        if (cart == null) {
            return CartStatusCode.NOT_STARTED;
        } else if (cart.size() == 0) {
            return CartStatusCode.EMPTY;
        } else if (cart.size() < 8) {
            return CartStatusCode.PARTIAL;
        } else return CartStatusCode.FULL;
    }

    public void resetStudentCarts() {
        studentCarts = new HashMap<>();
    }

    public Long getClassTotal() {
        return studentCarts.values().stream().map(UIBook::sum_prices)
                .reduce(0L, Long::sum);
        // I like using the Streams API for list operations.
    }

    public FragmentLabel getFragment() {
        return currentScreen;
    }

    public void storeCustomerSession(Customer cu, Context co) {
        ArrayList<UIBook> allBooks = new ArrayList<>();
        for (ArrayList<UIBook> bl : studentCarts.values()) {
            allBooks.addAll(bl);
        } // This completes in linear time, so it's algorithmically fine.

        List<String> allISBNs = allBooks.stream().map(UIBook::get_isbn).collect(Collectors.toList());
        // List of all the ISBNs of the books selected by all student profiles.
        List<String> allStudentIDs = allBooks.stream().map(UIBook::get_student).collect(Collectors.toList());
        // List of the student IDs associated with each of those picks, so that later when the
        // selections are extracted, they can be split up by student profile, in case the
        // teacher had some students in mind when they picked them.

        List<CustomerBook> allCBs = new ArrayList<>();

        for (int i = 0; i < allISBNs.size(); i++) {
            allCBs.add(new CustomerBook(cu, allISBNs.get(i), allStudentIDs.get(i)));
        }

        db = AppDatabase.getInstance(co);
        CustomerDao cd = db.customerDao();
        CustomerBookDao cbd = db.customerBookDao();

        // The actual Room database transactions.
        // These are made non-blocking with RxJava2 Completables.
        cd.insertCustomer(cu).subscribe(
                () -> Log.i("ViewModelLog", "Saved user successfully."),
                error -> Log.i("ViewModelLog", "Failed to save user."));
        cbd.insertList(allCBs).subscribe(
                () -> Log.i("ViewModelLog", "Saved books successfully."),
                error -> Log.i("ViewModelLog", "Failed to save books.")
        );

    }

}
