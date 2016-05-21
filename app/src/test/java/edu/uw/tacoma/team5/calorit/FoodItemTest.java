package edu.uw.tacoma.team5.calorit;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import edu.uw.tacoma.team5.calorit.model.FoodItem;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by Levi on 5/21/2016.
 */
public class FoodItemTest {

    private FoodItem foodItem;

    @Before
    public void setUp(){
        foodItem = new FoodItem("Banana", "Fruits and Veggies", 89);
    }

    @Test
    public void testSetNullFoodName(){
        try{
            foodItem.setmFoodName(null);
            fail("Failed! FoodName was set to null");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testSetNullFoodCategory(){
        try{
            foodItem.setmCategory(null);
            fail("Failed! Food category was set to null");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testSetNegativeFoodCalories(){
        try{
            foodItem.setmCalories(-5);
            fail("Failed! Food calories was set to a negative number");
        } catch (IllegalArgumentException e){}
    }

    @Test
    public void testGetFoodName(){
        String name = foodItem.getmFoodName();
        assertEquals(name, "Banana");
    }

    @Test
    public void testGetFoodCategory(){
        String category = foodItem.getmCategory();
        assertEquals(category, "Fruits and Veggies");
    }

    @Test
    public void testGetFoodCalories(){
        int cals = foodItem.getmCalories();
        assertTrue(cals == 89);
    }

    @Test
    public void testParseFoodItemJSON(){
        String foodItemJSON = "[{\"foodName\":\"Banana\",\"Category\":\"Fruits and Veggies\",\"Calories\":\"89\"}]";
        String message = foodItem.parseFoodItemJSON(foodItemJSON, new ArrayList<FoodItem>());
        //null = success, parseFoodItemJSON returns an error message in the string if something went wrong.
        assertTrue(message == null);
    }

}
