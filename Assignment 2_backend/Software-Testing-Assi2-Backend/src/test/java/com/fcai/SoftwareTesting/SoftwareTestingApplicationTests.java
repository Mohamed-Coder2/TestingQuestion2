package com.fcai.SoftwareTesting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcai.SoftwareTesting.todo.Todo;
import com.fcai.SoftwareTesting.todo.TodoCreateRequest;
import com.fcai.SoftwareTesting.todo.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
	public void create_ShouldReturnOk_WhenTodoIsCreatedSuccessfully() throws Exception {
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
	public void create_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
		when(todoService.create(any(TodoCreateRequest.class))).thenThrow(new IllegalArgumentException());

		mockMvc.perform(post("/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(new TodoCreateRequest("", ""))))
				.andExpect(status().isBadRequest());
	}
}
