package com.grf.dropgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOverScreen implements Screen {

    final Drop game;
    final int finalScore;

    public GameOverScreen(final Drop game, int finalScore){
        this.game=game;
        this.finalScore = finalScore;
    }

    @Override
    public void show(){}

    @Override
    public void render(float delta){
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();
        game.font.draw(game.batch, "GAME OVER", 3, 3);
        game.font.draw(game.batch, "Puntuación: " + finalScore, 3, 2.5f);
        game.font.draw(game.batch, "Clica para volver al menú :D", 2, 2);
        game.batch.end();

        if(Gdx.input.isTouched()){
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height){
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause(){}
    @Override
    public void hide(){}
    @Override
    public void resume(){}
    @Override
    public void dispose(){}
}
