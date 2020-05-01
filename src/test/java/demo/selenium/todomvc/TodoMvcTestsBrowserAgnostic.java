package demo.selenium.todomvc;

import io.github.bonigarcia.seljup.SeleniumExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pages.TodoMvc;
import pages.TodoMvcPage;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Running Tests being agnostic of the browser type
 */

@ExtendWith(SeleniumExtension.class)
@DisplayName("Managing ToDo's")
public class TodoMvcTestsBrowserAgnostic {

    private TodoMvc todoMvc;
    private final WebDriver driver;

    private final String buyTheMilk = "Buy the milk";
    private final String cleanupTheRoom = "Clean up the room";
    private final String readTheBook = "Read the book";

    public TodoMvcTestsBrowserAgnostic(WebDriver driver) {
        this.driver = driver;
        this.todoMvc = PageFactory.initElements(driver, TodoMvcPage.class);
        this.todoMvc.navigateTo();
    }

    @AfterEach
    void storageCleanup() {
        ((JavascriptExecutor)driver).executeScript("window.localStorage.clear()");
    }

    @Test
    @DisplayName("Creates Todo with given name")
    void createsTodo() {
        todoMvc.createTodo(buyTheMilk);

        assertAll(
                () -> assertEquals(1, todoMvc.getTodosLeft()),
                () -> assertTrue(todoMvc.todoExists(buyTheMilk))
        );
    }

    @Test
    @DisplayName("Creates Todos all with the same name")
    void createsTodosWithSameName() {

        todoMvc.createTodos(buyTheMilk, buyTheMilk, buyTheMilk);

        assertEquals(3, todoMvc.getTodosLeft());


        todoMvc.showActive();

        assertEquals(3, todoMvc.getTodoCount());
    }

    @Test
    @DisplayName("Edits inline double-clicked Todo")
    void editsTodo() {

        todoMvc.createTodos(buyTheMilk, cleanupTheRoom);

        todoMvc.renameTodo(buyTheMilk, readTheBook);

        assertAll(
                () -> assertFalse(todoMvc.todoExists(buyTheMilk)),
                () -> assertTrue(todoMvc.todoExists(readTheBook)),
                () -> assertTrue(todoMvc.todoExists(cleanupTheRoom))
        );

    }

    @Test
    @DisplayName("Removes selected Todo")
    void removesTodo() {

        todoMvc.createTodos(buyTheMilk, cleanupTheRoom, readTheBook);

        todoMvc.removeTodo(buyTheMilk);

        assertAll(
                () -> assertFalse(todoMvc.todoExists(buyTheMilk)),
                () -> assertTrue(todoMvc.todoExists(cleanupTheRoom)),
                () -> assertTrue(todoMvc.todoExists(readTheBook))
        );
    }

    @Test
    @DisplayName("Toggles selected Todo as completed")
    void togglesTodoCompleted() {
        todoMvc.createTodos(buyTheMilk, cleanupTheRoom, readTheBook);

        todoMvc.completeTodo(buyTheMilk);
        assertEquals(2, todoMvc.getTodosLeft());

        todoMvc.showCompleted();
        assertEquals(1, todoMvc.getTodoCount());

        todoMvc.showActive();
        assertEquals(2, todoMvc.getTodoCount());
    }

    @Test
    @DisplayName("Toggles all Todos as completed")
    void togglesAllTodosCompleted() {
        todoMvc.createTodos(buyTheMilk, cleanupTheRoom, readTheBook);

        todoMvc.completeAllTodos();
        assertEquals(0, todoMvc.getTodosLeft());

        todoMvc.showCompleted();
        assertEquals(3, todoMvc.getTodoCount());

        todoMvc.showActive();
        assertEquals(0, todoMvc.getTodoCount());
    }

    @Test
    @DisplayName("Clears all completed Todos")
    void clearsCompletedTodos() {
        todoMvc.createTodos(buyTheMilk, cleanupTheRoom);
        todoMvc.completeAllTodos();
        todoMvc.createTodo(readTheBook);

        todoMvc.clearCompleted();
        assertEquals(1, todoMvc.getTodosLeft());

        todoMvc.showCompleted();
        assertEquals(0, todoMvc.getTodoCount());

        todoMvc.showActive();
        assertEquals(1, todoMvc.getTodoCount());
    }
}
