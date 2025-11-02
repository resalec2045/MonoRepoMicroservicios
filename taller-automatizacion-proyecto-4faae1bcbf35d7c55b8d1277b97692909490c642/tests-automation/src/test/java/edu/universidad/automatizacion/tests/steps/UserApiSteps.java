package edu.universidad.automatizacion.tests.steps;

import com.github.javafaker.Faker;
import edu.universidad.automatizacion.users.UsersApiApplication;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@CucumberContextConfiguration
@SpringBootTest(classes = UsersApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserApiSteps {

    @LocalServerPort
    int port;

    private final Faker faker = new Faker();
    private Map<String, Object> user;
    private String createdId;
    private ValidatableResponse last;

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Given("un usuario aleatorio válido")
    public void un_usuario_aleatorio_valido() {
        user = new HashMap<>();
        user.put("name", faker.name().fullName());
        user.put("email", faker.internet().emailAddress());
    }

    @When("creo el usuario")
    public void creo_el_usuario() {
        last = given().port(port)
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/users")
                .then().statusCode(HttpStatus.CREATED.value())
                .and().contentType(ContentType.JSON)
                .and().body(io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/user.json"));

        createdId = last.extract().path("id");
        Assertions.assertNotNull(createdId);
    }

    @Then("puedo consultarlo")
    public void puedo_consultarlo() {
        last = given().port(port)
                .when().get("/users/{id}", createdId)
                .then().statusCode(HttpStatus.OK.value())
                .and().body(io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/user.json"));
    }

    @When("lo actualizo con un nuevo nombre")
    public void lo_actualizo_con_un_nuevo_nombre() {
        user.put("name", faker.name().fullName());
        last = given().port(port).contentType(ContentType.JSON)
                .body(user)
                .when().put("/users/{id}", createdId)
                .then().statusCode(org.hamcrest.Matchers.anyOf(org.hamcrest.Matchers.equalTo(HttpStatus.OK.value()), org.hamcrest.Matchers.equalTo(HttpStatus.CREATED.value())))
                .and().body(io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/user.json"));
    }

    @Then("lo puedo eliminar")
    public void lo_puedo_eliminar() {
        given().port(port)
                .when().delete("/users/{id}", createdId)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Given("un usuario con datos inválidos")
    public void un_usuario_con_datos_invalidos() {
        user = new HashMap<>();
        user.put("name", ""); // nombre vacío
        user.put("email", "correo-invalido"); // email inválido
    }

    @When("intento crear el usuario")
    public void intento_crear_el_usuario() {
        last = given().port(port)
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/users")
                .then();
    }

    @Then("recibo un error de validación")
    public void recibo_un_error_de_validacion() {
        last.statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Given("un identificador de usuario que no existe")
    public void un_identificador_de_usuario_que_no_existe() {
        createdId = "999999"; // id que no existe
    }

    @When("consulto el usuario")
    public void consulto_el_usuario() {
        last = given().port(port)
                .when().get("/users/{id}", createdId)
                .then();
    }

    @Then("recibo un error de no encontrado")
    public void recibo_un_error_de_no_encontrado() {
        last.statusCode(HttpStatus.NOT_FOUND.value());
    }

    @When("intento actualizar el usuario")
    public void intento_actualizar_el_usuario() {
        user = new HashMap<>();
        user.put("name", "Nuevo Nombre");
        user.put("email", "nuevo@email.com");
        last = given().port(port)
                .contentType(ContentType.JSON)
                .body(user)
                .when().put("/users/{id}", createdId)
                .then();
    }

    @When("intento eliminar el usuario")
    public void intento_eliminar_el_usuario() {
        last = given().port(port)
                .when().delete("/users/{id}", createdId)
                .then();
    }

    @Given("que existen usuarios en el sistema")
    public void que_existen_usuarios_en_el_sistema() {
        // Crear al menos un usuario para asegurar que la lista no esté vacía
        un_usuario_aleatorio_valido();
        creo_el_usuario();
    }

    @When("consulto la lista de usuarios")
    public void consulto_la_lista_de_usuarios() {
        last = given().port(port)
                .when().get("/users")
                .then();
    }

    @Then("recibo la lista completa de usuarios")
    public void recibo_la_lista_completa_de_usuarios() {
        last.statusCode(HttpStatus.OK.value());
        last.contentType(ContentType.JSON);
        last.body("size()", org.hamcrest.Matchers.greaterThan(0));
    }

    @Then("la respuesta cumple el esquema {string}")
    @SuppressWarnings("unused")
    public void validarEsquema(String schemaPath) {
        // Validamos que hubo una respuesta previa
        if (last == null) {
            Assertions.fail("No hay una respuesta previa para validar el esquema");
        }
        last.body(matchesJsonSchemaInClasspath(schemaPath));
    }

    // Static import placeholder to make schema validator available
    }
