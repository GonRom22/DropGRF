package com.grf.dropgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    final Drop game;
    Texture titleTexture;
    Texture titleLetters;
    //Constructor
    public MainMenuScreen(final Drop game){
        this.game = game;
        titleTexture = new Texture("title.png");
        titleLetters = new Texture("titleLetters.png");
    }

    @Override
    public void render(float delta){
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        float titleWidth = worldWidth - 5f;
        float titleHeight = worldHeight - 1f;

        float x = (worldWidth/1.5f)-(titleWidth/2);
        float y = (worldHeight/2)-(titleHeight/2);


        game.batch.draw(titleTexture, x, y, titleWidth, titleHeight);
        game.batch.draw(titleLetters,0.1f, 3.5f, 3f, 1f);
        //Dibuja texto. Hay que recordar que x e y están en metros
        game.font.draw(game.batch, "Bienvenidx a Drops Inferno", 0.5f, 1.5f);
        game.font.draw(game.batch, "Clica para empezar :)", 0.5f, 1);

        game.batch.end();

        if(Gdx.input.isTouched()){
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height){
        game.viewport.update(width, height, true);
    }

    @Override
    public void show(){}
    @Override
    public void pause(){}
    @Override
    public void resume(){}
    @Override
    public void hide(){}
    @Override
    public void dispose(){
        titleTexture.dispose();
    }
}

