package com.grf.dropgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {

    final Drop game;
    //Texturas
    Texture backgroundTexture;
    Texture potTexture;
    Texture dropTexture;
    Texture flameTexture;
    Texture leafTexture;
    Texture leafLostTexture;
    //Sonidos
    Sound dropSound;
    Sound flameSound;
    Music music;

    //Sprites
    Sprite potSprite;
    Vector2 touchPos;
    Array<Sprite> dropSprites;
    Array<Sprite> flameSprites;
    //Miscelánea
    float dropTimer;
    float flameTimer;
    Rectangle potRectangle;
    Rectangle dropRectangle;
    Rectangle flameRectangle;
    int dropsGathered;
    //Variables para la derrota GameOver
    int hits;
    int maxHits = 5;



    public GameScreen(final Drop game){
        this.game = game;

        //Cargar imágenes para el fondo, el cubo y la gota
        backgroundTexture = new Texture("background2.jpeg");
        potTexture = new Texture("potty.png");
        dropTexture = new Texture("drops.png");
        flameTexture = new Texture("flames.png");
        leafTexture = new Texture("leaves.png");
        leafLostTexture = new Texture("leavesLost.png");

        //Cargamos los sonidos y musica
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        flameSound = Gdx.audio.newSound(Gdx.files.internal("flame.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(0.5f);

        potSprite = new Sprite(potTexture);
        potSprite.setSize(0.75f, 1);

        touchPos = new Vector2();

        potRectangle = new Rectangle();
        dropRectangle = new Rectangle();
        flameRectangle = new Rectangle();

        dropSprites = new Array<>();
        flameSprites = new Array<>();


    }

    @Override
    public void show(){
        //Empieza la música de fondo
        //Cuando la pantalla se muestra
        music.play();
    }

    @Override
    public void render (float delta){
        input();
        logic();
        draw();
    }

    private void input(){
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            potSprite.translateX(speed*delta);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
           potSprite.translateX(-speed*delta);
        }

        if(Gdx.input.isTouched()){
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            game.viewport.unproject(touchPos);
            potSprite.setCenterX(touchPos.x);
        }
    }

    private void logic(){
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        float bucketWidth = potSprite.getWidth();
        float bucketHeight = potSprite.getHeight();
        float delta = Gdx.graphics.getDeltaTime();

        potSprite.setX(MathUtils.clamp(potSprite.getX(), 0, worldWidth - bucketWidth));
        potRectangle.set(potSprite.getX(), potSprite.getY(), bucketWidth, bucketHeight);

        for(int i = dropSprites.size - 1; i >= 0 ; i--){
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-2f*delta);
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            if(potRectangle.overlaps(dropRectangle)) {
                dropsGathered++;
                dropSprites.removeIndex(i);
                dropSound.play();

                if(hits > 0){
                    hits--;
                }
            }
            else if(dropSprite.getY() < -dropHeight) {
                dropSprites.removeIndex(i);
            }
        }

        for(int i = flameSprites.size - 1; i >= 0 ; i--) {
            Sprite flameSprite = flameSprites.get(i);
            float flameWidth = flameSprite.getWidth();
            float flameHeight = flameSprite.getHeight();

            flameSprite.translateY(-2.5f * delta);
            flameRectangle.set(flameSprite.getX(), flameSprite.getY(), flameWidth, flameHeight);

            if(potRectangle.overlaps(flameRectangle)) {
                hits++;
                flameSprites.removeIndex(i);
                flameSound.play();

                if(hits >= maxHits){
                    game.setScreen(new GameOverScreen(game, dropsGathered));
                    dispose();
                    return;
                }
            }

            else if(flameSprite.getY() < -flameHeight) {
                flameSprites.removeIndex(i);
            }
        }

        dropTimer += delta;
        if (dropTimer > 1f){
            dropTimer = 0;
            createDroplet();
        }

        flameTimer += delta;
        if (flameTimer > 1.25f){
            flameTimer = 0;
            createFlame();
        }
    }



    private void draw(){
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        game.batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        potSprite.draw(game.batch);

        game.font.draw(game.batch, "Gotas recogidas: " + dropsGathered, 0.25f, worldHeight - 0.25f);
        //game.font.draw(game.batch, "Daño: "+ hits+ " de " + maxHits, 0.25f, worldHeight - 0.75f);
        float lifeWidth = 0.20f;
        float lifeHeight = 0.25f;
        float startX = 0.25f;
        float y = worldHeight - 0.75f;

        int lives = maxHits - hits;

        for(int i = 0; i < maxHits; i++){
            Texture texture;

            if(i < lives){
                texture = leafTexture;
            }else{
                texture = leafLostTexture;
            }
            game.batch.draw(texture, startX + i * (lifeWidth + 0.1f), y, lifeWidth, lifeHeight);
        }

        for (Sprite dropSprite : dropSprites){
            dropSprite.draw(game.batch);
        }

        for (Sprite flameSprite : flameSprites){
            flameSprite.draw(game.batch);
        }

        game.batch.end();
    }

    private void createDroplet() {
        float dropWidth = 0.5f;
        float dropHeight = 0.75f;
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth));
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite);
    }

    private void createFlame() {
        float flameWidth = 0.35f;
        float flameHeight = 0.75f;
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        Sprite flameSprite = new Sprite(flameTexture);
        flameSprite.setSize(flameWidth, flameHeight);
        flameSprite.setX(MathUtils.random(0f, worldWidth - flameWidth));
        flameSprite.setY(worldHeight);
        flameSprites.add(flameSprite);
    }

    @Override
    public void resize(int width, int height){
        game.viewport.update(width, height, true);
    }

    @Override
    public void hide(){
    }

    @Override
    public void pause(){
    }

    @Override
    public void resume(){
    }

    @Override
    public void dispose(){
        backgroundTexture.dispose();
        dropSound.dispose();
        flameSound.dispose();
        music.dispose();
        dropTexture.dispose();
        flameTexture.dispose();
        leafTexture.dispose();
        leafLostTexture.dispose();
        potTexture.dispose();
    }
}
