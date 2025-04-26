import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class FXUniversity extends Application {
	private static final String URL = "jdbc:mysql://localhost:3306/universitysystem?useSSL=false&allowPublicKeyRetrieval=true";
	private static final String USER = "root";
	private static final String PASSWORD = "NewSQLMuhammad";

	@Override
	public void start(Stage uniStage) {
		// Create TabPane for Tabs
		TabPane uniTabPane = new TabPane();
		uniTabPane.getTabs().addAll(createDepartmentsTab(), createProfessorsTab(), createStudentsTab(),
				createCoursesTab(), createClassroomsTab(), createSchedulesTab(), createEnrollmentsTab());

		// Top Header
		Label headerLabel = new Label("University Management System");
		headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
		headerLabel.setAlignment(Pos.CENTER);
		headerLabel.setMaxWidth(Double.MAX_VALUE);
		headerLabel.setPadding(new Insets(15));
		VBox header = new VBox(headerLabel);
		header.setStyle("-fx-background-color: #2c3e50;");

		// Sidebar Text
		Label welcomeText = new Label("Welcome to the University System");
		welcomeText.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
		welcomeText.setWrapText(true); // Enable text wrapping
		welcomeText.setMaxWidth(180); // Set a maximum width to avoid overflow

		Label descriptionText = new Label("Use the tabs above to navigate through the system.\n\n"
				+ "Manage students, professors, departments, and more.");
		descriptionText.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
		descriptionText.setWrapText(true); // Enable text wrapping
		descriptionText.setMaxWidth(180); // Set a maximum width to avoid overflow

		// VBox for Sidebar Layout
		VBox sidebar = new VBox(20, welcomeText, descriptionText); // Add spacing between elements
		sidebar.setPadding(new Insets(15));
		sidebar.setStyle("-fx-background-color: #34495e;");
		sidebar.setPrefWidth(200); // Set an appropriate sidebar width

		// Main Layout with Sidebar, Header, and Tabs
		BorderPane mainLayout = new BorderPane();
		mainLayout.setTop(header);
		mainLayout.setLeft(sidebar);
		mainLayout.setCenter(uniTabPane);

		// Scene and Stage Setup
		Scene scene = new Scene(mainLayout, 1000, 700);
		CheckBox darkModeCheckBox = new CheckBox("Enable Dark Mode");
		sidebar.getChildren().add(darkModeCheckBox);

		darkModeCheckBox.setOnAction(e -> {
			if (darkModeCheckBox.isSelected()) {
				// Apply dark mode styles

				scene.getStylesheets().add(getClass().getResource("darkMode.css").toExternalForm());

			} else {
				// Apply light mode styles
				scene.getStylesheets().remove(getClass().getResource("darkMode.css").toExternalForm());
				// uniTabPane.setStyle("-fx-background-color: #121212;");

			}
		});

		scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		uniStage.setTitle("University System");
		uniStage.setScene(scene);
		uniStage.show();

	}

	private Tab createDepartmentsTab() {
		Tab departmentsTab = new Tab("Departments");
		TableView<ObservableList<String>> tableView = createTableView("SELECT dept_id, dept_name FROM department");
		ObservableList<ObservableList<String>> data = tableView.getItems();

		TextField deptIdField = new TextField();
		deptIdField.setPromptText("Department ID");
		deptIdField.setPrefWidth(125);
		deptIdField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

		TextField deptNameField = new TextField();
		deptNameField.setPromptText("Department Name");
		deptNameField.setPrefWidth(125);
		deptNameField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

		Button addButton = new Button("Add");
		addButton.setPrefWidth(125);
		addButton.setFont(new Font("New Times Roman", 18));
		addButton.setOnAction(event -> {
			String deptId = deptIdField.getText();
			String deptName = deptNameField.getText();

			if (validateInputs(deptId, deptName)) {
				try {
					int deptIdInt = Integer.parseInt(deptId);

					String query = "INSERT INTO department (dept_id, dept_name) VALUES (?, ?)";
					try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
							PreparedStatement pst = con.prepareStatement(query)) {
						pst.setInt(1, deptIdInt);
						pst.setString(2, deptName);
						pst.executeUpdate();
						data.add(FXCollections.observableArrayList(deptId, deptName));
					}
				} catch (NumberFormatException e) {
					showAlert("Validation Error", "Department ID must be a valid integer.");
				} catch (SQLException e) {
					showAlert("Error", "Failed to add department: " + e.getMessage());
				}
			}
		});

		Button deleteButton = new Button("Delete");
		deleteButton.setPrefWidth(125);
		deleteButton.setFont(new Font("New Times Roman", 18));
		deleteButton.setOnAction(event -> {
			ObservableList<String> selectedRow = tableView.getSelectionModel().getSelectedItem();
			if (selectedRow != null) {
				String deptId = selectedRow.get(0);
				String query = "DELETE FROM department WHERE dept_id = ?";
				try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
						PreparedStatement pst = con.prepareStatement(query)) {
					pst.setInt(1, Integer.parseInt(deptId));
					pst.executeUpdate();
					data.remove(selectedRow);
				} catch (SQLException e) {
					showAlert("Error", "Failed to delete department: " + e.getMessage());
				}
			} else {
				showAlert("Error", "Please select a row to delete.");
			}
		});

		HBox inputBox = new HBox(10, deptIdField, deptNameField);
		inputBox.setAlignment(Pos.CENTER);

		HBox buttonsBox = new HBox(10, addButton, deleteButton);
		buttonsBox.setAlignment(Pos.CENTER);

		VBox controlsBox = new VBox(10, inputBox, buttonsBox);
		controlsBox.setAlignment(Pos.CENTER);

		VBox layout = new VBox(10, tableView, controlsBox);
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.CENTER);

		departmentsTab.setContent(layout);
		departmentsTab.setClosable(false);

		return departmentsTab;
	}

	private Tab createProfessorsTab() {
		Tab professorsTab = new Tab("Professors");
		TableView<ObservableList<String>> tableView = createTableView(
				"SELECT prof_id, name, email, dept_id FROM professor");
		ObservableList<ObservableList<String>> data = tableView.getItems();

		TextField profIdField = new TextField();
		profIdField.setPromptText("Professor ID");
		profIdField.setPrefWidth(125);
		profIdField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

		TextField nameField = new TextField();
		nameField.setPromptText("Name");
		nameField.setPrefWidth(125);
		nameField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

		TextField emailField = new TextField();
		emailField.setPromptText("Email");
		emailField.setPrefWidth(125);
		emailField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

		ComboBox<String> deptIdComboBox = new ComboBox<>();
		deptIdComboBox.setPromptText("Department ID");
		deptIdComboBox.setPrefWidth(155);
		deptIdComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		ComboBox(deptIdComboBox, "SELECT dept_id FROM department");

		Button addButton = new Button("Add");
		addButton.setPrefWidth(125);
		addButton.setFont(new Font("New Times Roman", 18));
		addButton.setOnAction(event -> {
			String profId = profIdField.getText();
			String name = nameField.getText();
			String email = emailField.getText();
			String deptId = deptIdComboBox.getValue();

			if (validateInputs(profId, name, email, deptId)) {
				try {
					int profIdInt = Integer.parseInt(profId);
					int deptIdInt = Integer.parseInt(deptId);

					String query = "INSERT INTO professor (prof_id, name, email, dept_id) VALUES (?, ?, ?, ?)";
					try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
							PreparedStatement pst = con.prepareStatement(query)) {
						pst.setInt(1, profIdInt);
						pst.setString(2, name);
						pst.setString(3, email);
						pst.setInt(4, deptIdInt);
						pst.executeUpdate();
						data.add(FXCollections.observableArrayList(profId, name, email, deptId));
					}
				} catch (NumberFormatException e) {
					showAlert("Validation Error", "Professor ID and Department ID must be valid integers.");
				} catch (SQLException e) {
					showAlert("Error", "Failed to add professor: " + e.getMessage());
				}
			}
		});

		Button deleteButton = new Button("Delete");
		deleteButton.setPrefWidth(125);
		deleteButton.setFont(new Font("New Times Roman", 18));
		deleteButton.setOnAction(event -> {
			ObservableList<String> selectedRow = tableView.getSelectionModel().getSelectedItem();
			if (selectedRow != null) {
				String profId = selectedRow.get(0);
				String query = "DELETE FROM professor WHERE prof_id = ?";
				try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
						PreparedStatement pst = con.prepareStatement(query)) {
					pst.setInt(1, Integer.parseInt(profId));
					pst.executeUpdate();
					data.remove(selectedRow);
				} catch (SQLException e) {
					showAlert("Error", "Failed to delete professor: " + e.getMessage());
				}
			} else {
				showAlert("Error", "Please select a row to delete.");
			}
		});

		HBox inputBox = new HBox(10, profIdField, nameField, emailField, deptIdComboBox);
		inputBox.setAlignment(Pos.CENTER);

		HBox buttonsBox = new HBox(10, addButton, deleteButton);
		buttonsBox.setAlignment(Pos.CENTER);

		VBox controlsBox = new VBox(10, inputBox, buttonsBox);
		controlsBox.setAlignment(Pos.CENTER);

		VBox layout = new VBox(10, tableView, controlsBox);
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.CENTER);

		professorsTab.setContent(layout);
		professorsTab.setClosable(false);

		return professorsTab;
	}

	private Tab createStudentsTab() {
		Tab studentsTab = new Tab("Students");
		TableView<ObservableList<String>> tableView = createTableView(
				"SELECT student_id, name, email, dob, dept_id FROM student");
		ObservableList<ObservableList<String>> data = tableView.getItems();

		TextField studentIdField = new TextField();
		studentIdField.setPrefWidth(125);
		studentIdField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		studentIdField.setPromptText("Student ID");

		TextField nameField = new TextField();
		nameField.setPrefWidth(125);
		nameField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		nameField.setPromptText("Name");

		TextField emailField = new TextField();
		emailField.setPrefWidth(125);
		emailField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		emailField.setPromptText("Email");

		DatePicker dobPicker = new DatePicker();
		dobPicker.setPrefWidth(135);
		dobPicker.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		dobPicker.setPromptText("Date of Birth");

		ComboBox<String> deptIdComboBox = new ComboBox<>();
		deptIdComboBox.setPromptText("Department ID");
		deptIdComboBox.setPrefWidth(155);
		deptIdComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		ComboBox(deptIdComboBox, "SELECT dept_id FROM department");

		Button addButton = new Button("Add");
		addButton.setPrefWidth(125);
		addButton.setFont(new Font("New Times Roman", 18));
		addButton.setOnAction(event -> {
			String studentId = studentIdField.getText();
			String name = nameField.getText();
			String email = emailField.getText();
			LocalDate dob = dobPicker.getValue();
			String deptId = deptIdComboBox.getValue();

			if (validateInputs(studentId, name, email, dob != null ? dob.toString() : null, deptId)) {
				try {
					int studentIdInt = Integer.parseInt(studentId);
					int deptIdInt = Integer.parseInt(deptId);

					String query = "INSERT INTO student (student_id, name, email, dob, dept_id) VALUES (?, ?, ?, ?, ?)";
					try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
							PreparedStatement pst = con.prepareStatement(query)) {
						pst.setInt(1, studentIdInt);
						pst.setString(2, name);
						pst.setString(3, email);
						pst.setDate(4, Date.valueOf(dob));
						pst.setInt(5, deptIdInt);
						pst.executeUpdate();
						data.add(FXCollections.observableArrayList(studentId, name, email, dob.toString(), deptId));
					}
				} catch (NumberFormatException e) {
					showAlert("Validation Error", "Student ID and Department ID must be valid integers.");
				} catch (SQLException e) {
					showAlert("Error", "Failed to add student: " + e.getMessage());
				}
			}
		});

		Button deleteButton = new Button("Delete");
		deleteButton.setPrefWidth(125);
		deleteButton.setFont(new Font("New Times Roman", 18));

		deleteButton.setOnAction(event -> {
			ObservableList<String> selectedRow = tableView.getSelectionModel().getSelectedItem();
			if (selectedRow != null) {
				String studentId = selectedRow.get(0);
				String deleteEnrollmentQuery = "DELETE FROM enrollments WHERE student_id = ?";
				String deleteStudentQuery = "DELETE FROM student WHERE student_id = ?";

				try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
						PreparedStatement pstDeleteEnrollment = con.prepareStatement(deleteEnrollmentQuery);
						PreparedStatement pstDeleteStudent = con.prepareStatement(deleteStudentQuery)) {

					// Delete dependent records first
					pstDeleteEnrollment.setInt(1, Integer.parseInt(studentId));
					pstDeleteEnrollment.executeUpdate();

					// Now delete the student record
					pstDeleteStudent.setInt(1, Integer.parseInt(studentId));
					pstDeleteStudent.executeUpdate();

					data.remove(selectedRow);
				} catch (SQLException e) {
					showAlert("Error", "Failed to delete student: " + e.getMessage());
				}
			} else {
				showAlert("Error", "Please select a row to delete.");
			}
		});

		HBox inputBox = new HBox(10, studentIdField, nameField, emailField, dobPicker, deptIdComboBox);
		inputBox.setAlignment(Pos.CENTER);

		HBox buttonsBox = new HBox(10, addButton, deleteButton);
		buttonsBox.setAlignment(Pos.CENTER);

		VBox controlsBox = new VBox(10, inputBox, buttonsBox);
		controlsBox.setAlignment(Pos.CENTER);

		VBox layout = new VBox(10, tableView, controlsBox);
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.CENTER);

		studentsTab.setContent(layout);
		studentsTab.setClosable(false);

		return studentsTab;
	}

	private Tab createCoursesTab() {
		Tab coursesTab = new Tab("Courses");
		TableView<ObservableList<String>> tableView = createTableView(
				"SELECT course_id, name, credits, dept_id FROM course");
		ObservableList<ObservableList<String>> data = tableView.getItems();

		TextField courseIdField = new TextField();
		courseIdField.setPrefWidth(125);
		courseIdField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		courseIdField.setPromptText("Course ID");

		TextField nameField = new TextField();
		nameField.setPrefWidth(125);
		nameField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		nameField.setPromptText("Name");

		TextField creditsField = new TextField();
		creditsField.setPrefWidth(125);
		creditsField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		creditsField.setPromptText("Credits");

		ComboBox<String> deptIdComboBox = new ComboBox<>();
		deptIdComboBox.setPromptText("Department ID");
		deptIdComboBox.setPrefWidth(155);
		deptIdComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		ComboBox(deptIdComboBox, "SELECT dept_id FROM department");

		Button addButton = new Button("Add");
		addButton.setPrefWidth(125);
		addButton.setFont(new Font("New Times Roman", 18));
		addButton.setOnAction(event -> {
			String courseId = courseIdField.getText();
			String name = nameField.getText();
			String credits = creditsField.getText();
			String deptId = deptIdComboBox.getValue();

			if (validateInputs(courseId, name, credits, deptId)) {
				try {
					int courseIdInt = Integer.parseInt(courseId);
					int creditsInt = Integer.parseInt(credits);
					int deptIdInt = Integer.parseInt(deptId);

					String query = "INSERT INTO course (course_id, name, credits, dept_id) VALUES (?, ?, ?, ?)";
					try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
							PreparedStatement pst = con.prepareStatement(query)) {
						pst.setInt(1, courseIdInt);
						pst.setString(2, name);
						pst.setInt(3, creditsInt);
						pst.setInt(4, deptIdInt);
						pst.executeUpdate();
						data.add(FXCollections.observableArrayList(courseId, name, credits, deptId));
					}
				} catch (NumberFormatException e) {
					showAlert("Validation Error", "Course ID, Credits, and Department ID must be valid integers.");
				} catch (SQLException e) {
					showAlert("Error", "Failed to add course: " + e.getMessage());
				}
			}
		});

		Button deleteButton = new Button("Delete");
		deleteButton.setPrefWidth(125);
		deleteButton.setFont(new Font("New Times Roman", 18));
		deleteButton.setOnAction(event -> {
			ObservableList<String> selectedRow = tableView.getSelectionModel().getSelectedItem();
			if (selectedRow != null) {
				String courseId = selectedRow.get(0);
				String query = "DELETE FROM course WHERE course_id = ?";
				try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
						PreparedStatement pst = con.prepareStatement(query)) {
					pst.setInt(1, Integer.parseInt(courseId));
					pst.executeUpdate();
					data.remove(selectedRow);
				} catch (SQLException e) {
					showAlert("Error", "Failed to delete course: " + e.getMessage());
				}
			} else {
				showAlert("Error", "Please select a row to delete.");
			}
		});

		HBox inputBox = new HBox(10, courseIdField, nameField, creditsField, deptIdComboBox);
		inputBox.setAlignment(Pos.CENTER);

		HBox buttonsBox = new HBox(10, addButton, deleteButton);
		buttonsBox.setAlignment(Pos.CENTER);

		VBox controlsBox = new VBox(10, inputBox, buttonsBox);
		controlsBox.setAlignment(Pos.CENTER);

		VBox layout = new VBox(10, tableView, controlsBox);
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.CENTER);

		coursesTab.setContent(layout);
		coursesTab.setClosable(false);

		return coursesTab;
	}

	private Tab createClassroomsTab() {
		Tab classroomsTab = new Tab("Classrooms");
		TableView<ObservableList<String>> tableView = createTableView(
				"SELECT room_id, location, capacity FROM classroom");
		ObservableList<ObservableList<String>> data = tableView.getItems();

		TextField roomIdField = new TextField();
		roomIdField.setPrefWidth(125);
		roomIdField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		roomIdField.setPromptText("Room ID");

		TextField locationField = new TextField();
		locationField.setPrefWidth(125);
		locationField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		locationField.setPromptText("Location");

		TextField capacityField = new TextField();
		capacityField.setPrefWidth(125);
		capacityField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		capacityField.setPromptText("Capacity");

		Button addButton = new Button("Add");
		addButton.setPrefWidth(125);
		addButton.setFont(new Font("New Times Roman", 18));
		addButton.setOnAction(event -> {
			String roomId = roomIdField.getText();
			String location = locationField.getText();
			String capacity = capacityField.getText();

			if (validateInputs(roomId, location, capacity)) {
				try {
					int roomIdInt = Integer.parseInt(roomId);
					int capacityInt = Integer.parseInt(capacity);

					String query = "INSERT INTO classroom (room_id, location, capacity) VALUES (?, ?, ?)";
					try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
							PreparedStatement pst = con.prepareStatement(query)) {
						pst.setInt(1, roomIdInt);
						pst.setString(2, location);
						pst.setInt(3, capacityInt);
						pst.executeUpdate();
						data.add(FXCollections.observableArrayList(roomId, location, capacity));
					}
				} catch (NumberFormatException e) {
					showAlert("Validation Error", "Room ID and Capacity must be valid integers.");
				} catch (SQLException e) {
					showAlert("Error", "Failed to add classroom: " + e.getMessage());
				}
			}
		});

		Button deleteButton = new Button("Delete");
		deleteButton.setPrefWidth(125);
		deleteButton.setFont(new Font("New Times Roman", 18));
		deleteButton.setOnAction(event -> {
			ObservableList<String> selectedRow = tableView.getSelectionModel().getSelectedItem();
			if (selectedRow != null) {
				String roomId = selectedRow.get(0);
				String query = "DELETE FROM classroom WHERE room_id = ?";
				try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
						PreparedStatement pst = con.prepareStatement(query)) {
					pst.setInt(1, Integer.parseInt(roomId));
					pst.executeUpdate();
					data.remove(selectedRow);
				} catch (SQLException e) {
					showAlert("Error", "Failed to delete classroom: " + e.getMessage());
				}
			} else {
				showAlert("Error", "Please select a row to delete.");
			}
		});

		HBox inputBox = new HBox(10, roomIdField, locationField, capacityField);
		inputBox.setAlignment(Pos.CENTER);

		HBox buttonsBox = new HBox(10, addButton, deleteButton);
		buttonsBox.setAlignment(Pos.CENTER);

		VBox controlsBox = new VBox(10, inputBox, buttonsBox);
		controlsBox.setAlignment(Pos.CENTER);

		VBox layout = new VBox(10, tableView, controlsBox);
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.CENTER);

		classroomsTab.setContent(layout);
		classroomsTab.setClosable(false);

		return classroomsTab;
	}

	private Tab createSchedulesTab() {
		Tab schedulesTab = new Tab("Schedules");
		TableView<ObservableList<String>> tableView = createTableView(
				"SELECT schedule_id, course_id, prof_id, room_id, time_slot FROM schedules");
		ObservableList<ObservableList<String>> data = tableView.getItems();

		TextField scheduleIdField = new TextField();
		scheduleIdField.setPrefWidth(125);
		scheduleIdField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		scheduleIdField.setPromptText("Schedule ID");

		ComboBox<String> courseIdComboBox = new ComboBox<>();
		courseIdComboBox.setPromptText("Course ID");
		courseIdComboBox.setPrefWidth(155);
		courseIdComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		ComboBox(courseIdComboBox, "SELECT course_id FROM course");

		ComboBox<String> profIdComboBox = new ComboBox<>();
		profIdComboBox.setPromptText("Professor ID");
		profIdComboBox.setPrefWidth(155);
		profIdComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		ComboBox(profIdComboBox, "SELECT prof_id FROM professor");

		ComboBox<String> roomIdComboBox = new ComboBox<>();
		roomIdComboBox.setPromptText("Room ID");
		roomIdComboBox.setPrefWidth(155);
		roomIdComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		ComboBox(roomIdComboBox, "SELECT room_id FROM classroom");

		TextField timeSlotField = new TextField();
		timeSlotField.setPrefWidth(125);
		timeSlotField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		timeSlotField.setPromptText("Time Slot");

		Button addButton = new Button("Add");
		addButton.setPrefWidth(125);
		addButton.setFont(new Font("New Times Roman", 18));
		addButton.setOnAction(event -> {
			String scheduleId = scheduleIdField.getText();
			String courseId = courseIdComboBox.getValue();
			String profId = profIdComboBox.getValue();
			String roomId = roomIdComboBox.getValue();
			String timeSlot = timeSlotField.getText();

			if (validateInputs(scheduleId, courseId, profId, roomId, timeSlot)) {
				try {
					int scheduleIdInt = Integer.parseInt(scheduleId);
					int profIdInt = Integer.parseInt(profId);
					int roomIdInt = Integer.parseInt(roomId);

					String query = "INSERT INTO schedules (schedule_id, course_id, prof_id, room_id, time_slot) VALUES (?, ?, ?, ?, ?)";
					try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
							PreparedStatement pst = con.prepareStatement(query)) {
						pst.setInt(1, scheduleIdInt);
						pst.setString(2, courseId);
						pst.setInt(3, profIdInt);
						pst.setInt(4, roomIdInt);
						pst.setString(5, timeSlot);
						pst.executeUpdate();
						data.add(FXCollections.observableArrayList(scheduleId, courseId, profId, roomId, timeSlot));
					}
				} catch (NumberFormatException e) {
					showAlert("Validation Error", "Schedule ID, Professor ID, and Room ID must be valid integers.");
				} catch (SQLException e) {
					showAlert("Error", "Failed to add schedule: " + e.getMessage());
				}
			}
		});

		Button deleteButton = new Button("Delete");
		deleteButton.setPrefWidth(125);
		deleteButton.setFont(new Font("New Times Roman", 18));
		deleteButton.setOnAction(event -> {
			ObservableList<String> selectedRow = tableView.getSelectionModel().getSelectedItem();
			if (selectedRow != null) {
				String scheduleId = selectedRow.get(0);
				String query = "DELETE FROM schedules WHERE schedule_id = ?";
				try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
						PreparedStatement pst = con.prepareStatement(query)) {
					pst.setInt(1, Integer.parseInt(scheduleId));
					pst.executeUpdate();
					data.remove(selectedRow);
				} catch (SQLException e) {
					showAlert("Error", "Failed to delete schedule: " + e.getMessage());
				}
			} else {
				showAlert("Error", "Please select a row to delete.");
			}
		});

		HBox inputBox = new HBox(10, scheduleIdField, courseIdComboBox, profIdComboBox, roomIdComboBox, timeSlotField);
		inputBox.setAlignment(Pos.CENTER);

		HBox buttonsBox = new HBox(10, addButton, deleteButton);
		buttonsBox.setAlignment(Pos.CENTER);

		VBox controlsBox = new VBox(10, inputBox, buttonsBox);
		controlsBox.setAlignment(Pos.CENTER);

		VBox layout = new VBox(10, tableView, controlsBox);
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.CENTER);

		schedulesTab.setContent(layout);
		schedulesTab.setClosable(false);

		return schedulesTab;
	}

	private Tab createEnrollmentsTab() {
		Tab enrollmentsTab = new Tab("Enrollments");
		TableView<ObservableList<String>> tableView = createTableView(
				"SELECT enrollment_id, student_id, course_id, semester FROM enrollments");
		ObservableList<ObservableList<String>> data = tableView.getItems();

		TextField enrollmentIdField = new TextField();
		enrollmentIdField.setPrefWidth(125);
		enrollmentIdField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		enrollmentIdField.setPromptText("Enrollment ID");

		ComboBox<String> studentIdComboBox = new ComboBox<>();
		studentIdComboBox.setPromptText("Student ID");
		studentIdComboBox.setPrefWidth(155);
		studentIdComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		ComboBox(studentIdComboBox, "SELECT student_id FROM student");

		ComboBox<String> courseIdComboBox = new ComboBox<>();
		courseIdComboBox.setPromptText("Course ID");
		courseIdComboBox.setPrefWidth(155);
		courseIdComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		ComboBox(courseIdComboBox, "SELECT course_id FROM course");

		TextField semesterField = new TextField();
		semesterField.setPrefWidth(125);
		semesterField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
		semesterField.setPromptText("Semester");

		Button addButton = new Button("Add");
		addButton.setPrefWidth(125);
		addButton.setFont(new Font("New Times Roman", 18));
		addButton.setOnAction(event -> {
			String enrollmentId = enrollmentIdField.getText();
			String studentId = studentIdComboBox.getValue();
			String courseId = courseIdComboBox.getValue();
			String semester = semesterField.getText();

			if (validateInputs(enrollmentId, studentId, courseId, semester)) {
				try {
					int enrollmentIdInt = Integer.parseInt(enrollmentId);

					String query = "INSERT INTO enrollments (enrollment_id, student_id, course_id, semester) VALUES (?, ?, ?, ?)";
					try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
							PreparedStatement pst = con.prepareStatement(query)) {
						pst.setInt(1, enrollmentIdInt);
						pst.setString(2, studentId);
						pst.setString(3, courseId);
						pst.setString(4, semester);
						pst.executeUpdate();
						data.add(FXCollections.observableArrayList(enrollmentId, studentId, courseId, semester));
					}
				} catch (NumberFormatException e) {
					showAlert("Validation Error", "Enrollment ID must be a valid integer.");
				} catch (SQLException e) {
					showAlert("Error", "Failed to add enrollment: " + e.getMessage());
				}
			}
		});

		Button deleteButton = new Button("Delete");
		deleteButton.setPrefWidth(125);
		deleteButton.setFont(new Font("New Times Roman", 18));
		deleteButton.setOnAction(event -> {
			ObservableList<String> selectedRow = tableView.getSelectionModel().getSelectedItem();
			if (selectedRow != null) {
				String enrollmentId = selectedRow.get(0);
				String query = "DELETE FROM enrollments WHERE enrollment_id = ?";
				try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
						PreparedStatement pst = con.prepareStatement(query)) {
					pst.setInt(1, Integer.parseInt(enrollmentId));
					pst.executeUpdate();
					data.remove(selectedRow);
				} catch (SQLException e) {
					showAlert("Error", "Failed to delete enrollment: " + e.getMessage());
				}
			} else {
				showAlert("Error", "Please select a row to delete.");
			}
		});

		HBox inputBox = new HBox(10, enrollmentIdField, studentIdComboBox, courseIdComboBox, semesterField);
		inputBox.setAlignment(Pos.CENTER);

		HBox buttonsBox = new HBox(10, addButton, deleteButton);
		buttonsBox.setAlignment(Pos.CENTER);

		VBox controlsBox = new VBox(10, inputBox, buttonsBox);
		controlsBox.setAlignment(Pos.CENTER);

		VBox layout = new VBox(10, tableView, controlsBox);
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.CENTER);

		enrollmentsTab.setContent(layout);
		enrollmentsTab.setClosable(false);

		return enrollmentsTab;
	}

	private void ComboBox(ComboBox<String> comboBox, String query) {
		try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement pst = con.prepareStatement(query);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				comboBox.getItems().add(rs.getString(1));
			}
		} catch (SQLException e) {
			showAlert("Error", "Failed to populate ComboBox: " + e.getMessage());
		}
	}

	private TableView<ObservableList<String>> createTableView(String query) {
		TableView<ObservableList<String>> tableView = new TableView<>();
		ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

		try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(query)) {

			ResultSetMetaData metaData = rs.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				TableColumn<ObservableList<String>, String> column = new TableColumn<>(metaData.getColumnName(i));
				final int columnIndex = i - 1;
				column.setCellValueFactory(cellData -> {
					if (cellData.getValue().size() > columnIndex) {
						return new javafx.beans.property.SimpleStringProperty(cellData.getValue().get(columnIndex));
					}
					return null;
				});
				tableView.getColumns().add(column);
			}

			while (rs.next()) {
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					row.add(rs.getString(i));
				}
				data.add(row);
			}
			tableView.setItems(data);
		} catch (SQLException e) {
			showAlert("Error", "Failed to load data: " + e.getMessage());
		}

		return tableView;
	}

	private boolean validateInputs(String... inputs) {
		for (String input : inputs) {
			if (input == null || input.isEmpty()) {
				showAlert("Validation Error", "All fields must be filled.");
				return false;
			}
		}
		return true;
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}