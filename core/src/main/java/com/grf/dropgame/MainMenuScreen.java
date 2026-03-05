package com.grf.dropgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    final Drop game;
    //Constructor
    public MainMenuScreen(final Drop game){
        this.game = game;
    }

    @Override
    public void render(float delta){
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();
        //Dibuja texto. Hay que recordar que x e y están en metros
        game.font.draw(game.batch, "Bienvenidx a Drops Inferno", 1, 1.5f);
        game.font.draw(game.batch, "Clica para empezar :)", 1, 1);
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
    public void dispose(){}
}

