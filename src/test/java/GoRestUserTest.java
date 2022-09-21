import Pojo.GoRestPost;
import Pojo.GoRestUser;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;



public class GoRestUserTest {

    private RequestSpecification reqSpec;
    private GoRestUser user;

    private GoRestPost postUser;


    @BeforeClass
    public void setup() {


        RestAssured.baseURI = "https://gorest.co.in";

        reqSpec = given()
                .log().body()
                .header("Authorization", "Bearer 3552b21c78056c29958ec50112cc2afacfe0d9da2826aa3865018da59883d9f6")
                .contentType(ContentType.JSON);

        user = new GoRestUser();
        user.setName("Chris Webber");
        user.setEmail("chris11@gmail.com");
        user.setGender("male");
        user.setStatus("active");

        postUser = new GoRestPost();
        postUser.setBody("I'm so sleepy");
        postUser.setTitle("Wednesday");

    }

    @Test
    public void createNewUser() {
        user.setId(given()
                .spec(reqSpec)
                .body(user)
                .when()
                .post("/public/v2/users")
                .then()
                .log().body()
                .statusCode(201)
                .body("name", equalTo(user.getName()))
                .extract().jsonPath().getString("id"));
    }
    @Test(dependsOnMethods = "createNewUser")
    public void createNegativeTest() {
        given()
                .spec(reqSpec)
                .body(user)
                .when()
                .post("/public/v2/users")
                .then()
                .log().body()
                .statusCode(422);

    }
    @Test(dependsOnMethods = "createNegativeTest")
    public void createPost() {

        postUser.setId(given()
                .spec(reqSpec)
                .body(postUser)
                .when()
                .post("/public/v2/users/" + user.getId() + "/posts")
                .then()
                .log().body()
                .statusCode(201)
                .body("title", equalTo(postUser.getTitle()))
                .extract().jsonPath().getString("id"));
    }
    @Test(dependsOnMethods = "createPost")
    public void editPost() {

        HashMap<String, String> title = new HashMap<>();
        title.put("title", "I love Burger and fries");

        given()
                .spec(reqSpec)
                .body(postUser)
                .when()
                .put("/public/v2/posts/" + postUser.getId())
                .then()
                .log().body()
                .statusCode(200)
                .body("title", equalTo(postUser.getTitle()));
    }

    @Test(dependsOnMethods = "editPost")
    public void deletePost(){
        given()
                .spec(reqSpec)
                .when()
                .delete("/public/v2/users/" + postUser.getId())
                .then()
                .log().body()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "deletePost")
    public void deleteUser() {
        given()
                .spec(reqSpec)
                .when()
                .delete("/public/v2/users/" + user.getId())
                .then()
                .log().body()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "deleteUser")
    public void deleteUserNegativeTest() {

        given()
                .spec(reqSpec)
                .when()
                .delete("/public/v2/users/" +user.getId())
                .then()
                .log().body()
                .statusCode(404);

    }




}
