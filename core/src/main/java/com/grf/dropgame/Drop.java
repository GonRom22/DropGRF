package com.grf.dropgame;



import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;




public class Drop extends Game{
    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;

    public void create(){
        batch = new SpriteBatch();
        //Usa la fuente por defecto de libGDX
        font = new BitmapFont();
        viewport = new FitViewport(8, 5);

        //La fuente tiene 15pt, pero necesitamos escalarlo en la vista
        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        this.setScreen(new MainMenuScreen(this));
    }

    public void render(){
        super.render(); //¡¡¡IMPORTANTE!!!
        /*A common mistake is to forget to call super.render() with a Game implementation.
        Without this call, the Screen that you set in the create() method
        will not be rendered if you override the render method in your Game class!
         */
    }

    public void dispose(){
        batch.dispose();
        font.dispose();
    }
}

