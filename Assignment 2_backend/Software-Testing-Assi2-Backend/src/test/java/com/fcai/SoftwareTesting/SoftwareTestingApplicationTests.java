package com.fcai.SoftwareTesting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcai.SoftwareTesting.todo.Todo;
import com.fcai.SoftwareTesting.todo.TodoCreateRequest;
import com.fcai.SoftwareTesting.todo.TodoService;
import com.fcai.SoftwareTesting.todo.TodoServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SoftwareTestingApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TodoService todoService;
	@Autowired
	private ObjectMapper objectMapper;
	private TodoCreateRequest validTodoCreateRequest;
	private Todo createdTodo;

	@BeforeEach
	public void setUp() {
		validTodoCreateRequest = new TodoCreateRequest("Test Todo", "This is a test todo");
		createdTodo = new Todo("1", "Test Todo", "This is a test todo", false);
	}
	@Test
	public void create_ValidInput() throws Exception {
		when(todoService.create(any(TodoCreateRequest.class))).thenReturn(createdTodo);

		mockMvc.perform(post("/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validTodoCreateRequest)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(createdTodo.getId()))
				.andExpect(jsonPath("$.title").value(createdTodo.getTitle()))
				.andExpect(jsonPath("$.description").value(createdTodo.getDescription()))
				.andExpect(jsonPath("$.completed").value(createdTodo.isCompleted()));
	}
	@Test
	public void create_InvalidInput() throws Exception {
		when(todoService.create(any(TodoCreateRequest.class))).thenThrow(new IllegalArgumentException());

		mockMvc.perform(post("/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(new TodoCreateRequest("", ""))))
				.andExpect(status().isBadRequest());
	}
	@Test
	public void read_ValidInput() throws Exception {
		Todo todo = new Todo("1", "Test Title", "Test Description", false);
		when(todoService.read("1")).thenReturn(todo);

		mockMvc.perform(get("/read")
						.param("id", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(todo.getId()))
				.andExpect(jsonPath("$.title").value(todo.getTitle()))
				.andExpect(jsonPath("$.description").value(todo.getDescription()))
				.andExpect(jsonPath("$.completed").value(todo.isCompleted()));
	}
	@Test
	public void read_InvalidInput() throws Exception {
		when(todoService.read(anyString())).thenThrow(new IllegalArgumentException());

		mockMvc.perform(get("/read")
						.param("id", "1"))
				.andExpect(status().isBadRequest());
	}
	@Test
	public void update_ValidInput() throws Exception {
		Todo todo = new Todo("1", "Test Title", "Test Description", true);
		when(todoService.update("1", true)).thenReturn(todo);

		mockMvc.perform(put("/update")
						.param("id", "1")
						.param("completed", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(todo.getId()))
				.andExpect(jsonPath("$.title").value(todo.getTitle()))
				.andExpect(jsonPath("$.description").value(todo.getDescription()))
				.andExpect(jsonPath("$.completed").value(todo.isCompleted()));
	}
	@Test
	public void update_InvalidInput() throws Exception {
		when(todoService.update(anyString(), anyBoolean())).thenThrow(new IllegalArgumentException());

		mockMvc.perform(put("/update")
						.param("id", "1")
						.param("completed", "true"))
				.andExpect(status().isBadRequest());
	}
	@Test
	public void list_ShouldReturnOk_WhenTodosAreListed() throws Exception {
		List<Todo> todos = Arrays.asList(
				new Todo("1", "Task 1", "Description 1", false),
				new Todo("2", "Task 2", "Description 2", true)
		);

		when(todoService.list()).thenReturn(todos);

		mockMvc.perform(get("/list"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(todos.size()))
				.andExpect(jsonPath("$[0].id").value(todos.get(0).getId()))
				.andExpect(jsonPath("$[0].title").value(todos.get(0).getTitle()))
				.andExpect(jsonPath("$[0].description").value(todos.get(0).getDescription()))
				.andExpect(jsonPath("$[0].completed").value(todos.get(0).isCompleted()));
	}
	@Test
	public void list_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
		when(todoService.list()).thenThrow(new IllegalArgumentException());

		mockMvc.perform(get("/list"))
				.andExpect(status().isBadRequest());
	}
	@Test
	public void listCompleted_ShouldReturnOk_WhenCompletedTodosAreListed() throws Exception {
		List<Todo> todos = Arrays.asList(
				new Todo("2", "Task 2", "Description 2", true)
		);

		when(todoService.listCompleted()).thenReturn(todos);

		mockMvc.perform(get("/listCompleted"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(todos.size()))
				.andExpect(jsonPath("$[0].id").value(todos.get(0).getId()))
				.andExpect(jsonPath("$[0].title").value(todos.get(0).getTitle()))
				.andExpect(jsonPath("$[0].description").value(todos.get(0).getDescription()))
				.andExpect(jsonPath("$[0].completed").value(todos.get(0).isCompleted()));
	}
	@Test
	public void listCompleted_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
		when(todoService.listCompleted()).thenThrow(new IllegalArgumentException());

		mockMvc.perform(get("/listCompleted"))
				.andExpect(status().isBadRequest());
	}
	@Test
	public void delete_ValidInput() throws Exception {
		// Mock the service layer
		when(todoService.create(any(TodoCreateRequest.class))).thenReturn(createdTodo);
		doNothing().when(todoService).delete("1");

		// Firstly, create a Todo item
		mockMvc.perform(post("/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validTodoCreateRequest)))
				.andExpect(status().isOk());

		// Now, delete the Todo item
		mockMvc.perform(delete("/delete")
						.param("id", "1"))
				.andExpect(status().isOk());
	}
	@Test
	public void delete_InvalidInput() throws Exception {
		// Mock the service layer
		doThrow(new IllegalArgumentException()).when(todoService).delete("721");

		// Now, delete the non-existent Todo item
		mockMvc.perform(delete("/delete")
						.param("id", "721"))
				.andExpect(status().isBadRequest());
	}

	private TodoServiceImpl todoServiceImpl;

	@BeforeEach
	public void setUp1() {
		todoServiceImpl = new TodoServiceImpl();
	}
	@Test
	public void testCreateMethod_ValidRequest() {
		TodoCreateRequest validRequest = new TodoCreateRequest("Title", "Description");

		Todo createdTodo = todoServiceImpl.create(validRequest);

		assertNotNull(createdTodo);
		assertEquals("1", createdTodo.getId());
		assertEquals("Title", createdTodo.getTitle());
		assertEquals("Description", createdTodo.getDescription());
		assertFalse(createdTodo.isCompleted());
	}
	@Test
	public void testCreateMethod_NullRequest_ShouldThrowIllegalArgumentException() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			todoServiceImpl.create(null);
		});
		assertEquals("Todo cannot be null", thrown.getMessage());
	}
	@Test
	public void testCreateMethod_EmptyTitle_ShouldThrowIllegalArgumentException() {
		TodoCreateRequest requestWithEmptyTitle = new TodoCreateRequest("", "Description");

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			todoServiceImpl.create(requestWithEmptyTitle);
		});
		assertEquals("Todo title cannot be empty", thrown.getMessage());
	}
	@Test
	public void testCreateMethod_EmptyDescription_ShouldThrowIllegalArgumentException() {
		TodoCreateRequest requestWithEmptyDescription = new TodoCreateRequest("Title", "");

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			todoServiceImpl.create(requestWithEmptyDescription);
		});
		assertEquals("Todo description cannot be empty", thrown.getMessage());
	}
	@Test
	public void testRead_ShouldThrowException_WhenIdIsNull() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			todoServiceImpl.read(null);
		});

		assertEquals("Todo id cannot be null", exception.getMessage());
	}
	@Test
	public void testRead_ShouldThrowException_WhenIdIsEmpty() {
		todoService = new TodoServiceImpl();
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			todoService.read("");
		});
		assertEquals("Todo id cannot be empty", exception.getMessage());
	}
	@Test
	public void testRead_ShouldThrowException_WhenTodoNotFound() {
		todoService = new TodoServiceImpl();
		// Assuming the todos list is empty
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			todoService.read("1"); // Assuming there's no to-do with ID "1" in the list
		});
		assertEquals("Todo not found", exception.getMessage());
	}
	@Test
	public void testUpdate_ShouldUpdateTodo_WhenIdIsValid() {
		todoService = new TodoServiceImpl();
		// Given
		TodoCreateRequest request = new TodoCreateRequest("Test Title", "Test Description");
		Todo createdTodo = todoService.create(request);

		// When
		Todo updatedTodo = todoService.update(createdTodo.getId(), true);

		// Then
		assertNotNull(updatedTodo);
		assertTrue(updatedTodo.isCompleted());
	}

	@Test
	public void testUpdate_ShouldThrowException_WhenIdIsNull() {
		todoService = new TodoServiceImpl();
		// When
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			todoService.update(null, true);
		});

		// Then
		assertEquals("Todo id cannot be null", exception.getMessage());
	}

	@Test
	public void testUpdate_ShouldThrowException_WhenIdIsEmpty() {
		todoService = new TodoServiceImpl();
		// When
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			todoService.update("", true);
		});

		// Then
		assertEquals("Todo id cannot be empty", exception.getMessage());
	}
	@Test
	public void testUpdate_ShouldThrowException_WhenTodoNotFound() {
		todoService = new TodoServiceImpl();
		// When
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			todoService.update("2", true); // Assuming there's no todo with ID "2"
		});

		// Then
		assertEquals("Todo not found", exception.getMessage());
	}
}
