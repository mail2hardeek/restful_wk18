package com.restfulbooker.wk18.crudtest;

import com.restfulbooker.wk18.restfulinfo.CreateSteps;
import com.restfulbooker.wk18.restfulinfo.DeleteSteps;
import com.restfulbooker.wk18.restfulinfo.ReadSteps;
import com.restfulbooker.wk18.restfulinfo.UpdateSteps;
import com.restfulbooker.wk18.testbase.TestBase;
import com.restfulbooker.wk18.utils.TestUtils;
import io.restassured.response.ValidatableResponse;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Title;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.hasValue;

@RunWith(SerenityRunner.class)
public class CRUDTest extends TestBase {
    static String username = "admin";
    static String password = "password123";
    static String firstname = "Harry" + TestUtils.getRandomValue();
    static String lastname = "VD";
    static int totalprice = 999;
    static boolean depositpaid = true;
    static String additionalneeds = "Breakfast";
    static String checkin = "2025-04-03";
    static String checkout = "2025-04-19";
    static int bookingid;
    static String token;

    @Steps
    CreateSteps create;

    @Title("Creating token.")
    @Test
    public void test001() {
        ValidatableResponse response = create.createToken(username, password);
        response.log().all().statusCode(200);
        token = response.extract().path("token");
        System.out.println(token);
    }

    @Title("Creating booking and verifying booking created.")
    @Test
    public void test002() {
        ValidatableResponse response = create.createBooking(firstname, lastname, totalprice, depositpaid, checkin, checkout, additionalneeds);
        response.log().all().statusCode(200);
        bookingid = response.extract().path("bookingid");
        HashMap<String, Object> value = response.extract().path("");
        Assert.assertThat(value, hasValue(bookingid));
    }

    @Steps
    ReadSteps readSteps;

    @Title("Getting All IDs.")
    @Test
    public void test003() {
        ValidatableResponse response = readSteps.getAllId();
        response.log().all().statusCode(200);
        List<String> booking = response.extract().path("bookingid");
        Assert.assertTrue(booking.contains(bookingid));
    }

    @Title("Getting Single ID.")
    @Test
    public void test004() {
        ValidatableResponse response = readSteps.getSingleId(bookingid);
        response.log().all().statusCode(200);
        HashMap<String, Object> value = response.extract().path("");
        Assert.assertThat(value, hasValue(firstname));
    }

    @Steps
    UpdateSteps updateSteps;

    @Title("Updating Single ID.")
    @Test
    public void test005() {
        firstname = firstname + "-updated";
        updateSteps.updateBooking(token, bookingid, firstname, lastname, totalprice, depositpaid, checkin, checkout, additionalneeds);
        ValidatableResponse response = updateSteps.getUserbyId(token, bookingid);
        response.log().all().statusCode(200);
        HashMap<String, Object> value = response.extract().path("");
        Assert.assertThat(value, hasValue(firstname));
    }

    @Steps
    DeleteSteps deleteSteps;

    @Title("Deleting the ID and verifying deletion.")
    @Test
    public void test006() {
        deleteSteps.deleteUser(token, bookingid).statusCode(201);
        deleteSteps.getUserbyId(token, bookingid).statusCode(404);
    }
}
