package com.example.myfirstapp.model;

import static java.util.stream.Collectors.toCollection;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myfirstapp.communication.CartStatusCode;
import com.example.myfirstapp.communication.FragmentLabel;
import com.example.myfirstapp.communication.UIEventCode;
import com.example.myfirstapp.data.BookDatasource;
import com.example.myfirstapp.data.StudentDatasource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class RRViewModel extends ViewModel {

    private FragmentLabel currentScreen;
    private String currentStudent;
    private HashMap<String, ArrayList<UIBook>> studentCarts;
    private MutableLiveData<ArrayList<UIBook>> shoppingCart;
    private ArrayList<UIStudent> classroomStudents;

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
        cart.add(new UIBook(b, this::removeFromCart));
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

    public void navigatedToFragment(FragmentLabel fl) {
        if (currentScreen == FragmentLabel.SHOPPING_CART) {
            saveCartToStudent(currentStudent);
        }
        if (fl == FragmentLabel.SHOPPING_CART) {
            loadCartFromStudent(currentStudent);
        }
        currentScreen = fl;
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
    }

}
