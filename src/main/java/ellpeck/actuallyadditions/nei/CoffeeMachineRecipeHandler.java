package ellpeck.actuallyadditions.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ellpeck.actuallyadditions.inventory.gui.GuiCoffeeMachine;
import ellpeck.actuallyadditions.items.InitItems;
import ellpeck.actuallyadditions.items.ItemCoffee;
import ellpeck.actuallyadditions.items.metalists.TheMiscItems;
import ellpeck.actuallyadditions.util.ModUtil;
import ellpeck.actuallyadditions.util.StringUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoffeeMachineRecipeHandler extends TemplateRecipeHandler{

    public static final String NAME = "actuallyadditions.coffee";

    public CoffeeMachineRecipeHandler(){
        super();
        RecipeInfo.setGuiOffset(this.getGuiClass(), 32, 3);
    }

    public class CachedCoffee extends CachedRecipe{

        public PositionedStack cup;
        public PositionedStack coffeeBeans;
        public PositionedStack result;
        public PositionedStack ingredientStack;
        public String extraText;

        public CachedCoffee(ItemCoffee.Ingredient ingredient){
            this.cup = new PositionedStack(new ItemStack(InitItems.itemMisc, 1, TheMiscItems.CUP.ordinal()), 45, 39);
            this.coffeeBeans = new PositionedStack(new ItemStack(InitItems.itemCoffeeBean), 2, 39);
            this.ingredientStack = new PositionedStack(ingredient.ingredient, 90, 21);
            this.setupResult(ingredient);
            this.extraText = ingredient.getExtraText();
        }

        public void setupResult(ItemCoffee.Ingredient ingredient){
            ItemStack result = new ItemStack(InitItems.itemCoffee);
            ItemCoffee.addEffectToStack(result, ingredient);
            this.result = new PositionedStack(result.copy(), 45, 70);
        }

        @Override
        public List<PositionedStack> getIngredients(){
            ArrayList<PositionedStack> list = new ArrayList<PositionedStack>();
            list.add(this.ingredientStack);
            list.add(this.cup);
            list.add(this.coffeeBeans);
            return list;
        }

        @Override
        public PositionedStack getResult(){
            return result;
        }
    }

    @Override
    public int recipiesPerPage(){
        return 1;
    }

    @Override
    public void loadTransferRects(){
        transferRects.add(new RecipeTransferRect(new Rectangle(21, 39, 22, 16), NAME));
        transferRects.add(new RecipeTransferRect(new Rectangle(67, 42, 22, 10), NAME));
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass(){
        return GuiCoffeeMachine.class;
    }

    @Override
    public String getRecipeName(){
        return StatCollector.translateToLocal("container.nei." + NAME + ".name");
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results){
        if(outputId.equals(NAME) && getClass() == CoffeeMachineRecipeHandler.class){
            ArrayList<ItemCoffee.Ingredient> ingredients = ItemCoffee.ingredients;
            for(ItemCoffee.Ingredient ingredient : ingredients){
                arecipes.add(new CachedCoffee(ingredient));
            }
        }
        else super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result){
        ArrayList<ItemCoffee.Ingredient> ingredients = ItemCoffee.ingredients;
        for(ItemCoffee.Ingredient ingredient : ingredients){
            if(result.getItem() instanceof ItemCoffee) arecipes.add(new CachedCoffee(ingredient));
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient){

        ArrayList<ItemCoffee.Ingredient> ingredients = ItemCoffee.ingredients;
        for(ItemCoffee.Ingredient ingr : ingredients){
            if(NEIServerUtils.areStacksSameTypeCrafting(new ItemStack(InitItems.itemMisc, 1, TheMiscItems.CUP.ordinal()), ingredient) || NEIServerUtils.areStacksSameTypeCrafting(new ItemStack(InitItems.itemCoffeeBean), ingredient) || NEIServerUtils.areStacksSameTypeCrafting(ingr.ingredient, ingredient)){
                CachedCoffee theRecipe = new CachedCoffee(ingr);
                theRecipe.setIngredientPermutation(Collections.singletonList(theRecipe.ingredientStack), ingredient);
                arecipes.add(theRecipe);
            }
        }
    }

    @Override
    public String getGuiTexture(){
        return ModUtil.MOD_ID_LOWER + ":textures/gui/guiNEICoffeeMachine.png";
    }

    @Override
    public void drawBackground(int recipeIndex){
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 126, 88);
    }

    @Override
    public void drawExtras(int recipe){
        drawProgressBar(21, 39, 126, 0, 21, 16, 48, 0);
        drawProgressBar(63, 42, 125, 16, 24, 12, 48, 2);

        CachedCoffee cache = (CachedCoffee)this.arecipes.get(recipe);
        if(cache.extraText != null){
            GuiDraw.drawString(StatCollector.translateToLocal("container.nei." + ModUtil.MOD_ID_LOWER + ".coffee.special"), 2, 6, StringUtil.DECIMAL_COLOR_GRAY_TEXT, false);
            GuiDraw.drawString(cache.extraText, 2, 18, StringUtil.DECIMAL_COLOR_GRAY_TEXT, false);
        }
        GuiDraw.drawString("[SHIFT]!", 1, 75, StringUtil.DECIMAL_COLOR_GRAY_TEXT, false);
    }

    @Override
    public String getOverlayIdentifier(){
        return NAME;
    }
}