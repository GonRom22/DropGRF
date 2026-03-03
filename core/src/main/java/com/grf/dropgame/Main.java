package com.grf.dropgame;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    //Declaración de recursos
    Texture dropTexture;
    Texture backgroundTexture;
    Texture bucketTexture;
    Sound dropSound;
    Music backgroundMusic;
    //Números que ayudan a mantener las dimensiones de la pantalla.
    FitViewport viewport;

    //Agrupa las imágenes en cada ciclo.
    SpriteBatch spriteBatch;

    //Sprites
    Sprite bucketSprite;

    //Capturar puntero del Ratón
    Vector2 touchPosition;

    //Gotas
    Array<Sprite> dropSprites;

    //Timer para la creación de gotas en la lógica
    float dropTimer;

    Rectangle bucketRectangle;
    Rectangle dropRectangle;




    //Este método es el que arranca todo
    @Override
    public void create() {
        // Prepare your application here.
        backgroundTexture = new Texture("background.png");
        dropTexture = new Texture("drop.png");
        bucketTexture = new Texture("bucket.png");

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));


        //8 unidades por 5
        viewport = new FitViewport(8, 5);
        spriteBatch = new SpriteBatch();

        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1, 1);

        touchPosition = new Vector2();

        dropSprites = new Array<>();

        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();

        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(.5f);
        backgroundMusic.play();


    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;


        // Resize your application here. The parameters represent the new window size.
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        // Draw your application here.
        //Ciclo de vida de videojuegos.
        input();//Entradas

        logic();//Calcula estados

        draw();//Dibuja
    }

    public void input(){
        //Posición y velocidad
        float speed = 4f;
        float deltaTime = Gdx.graphics.getDeltaTime();

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            bucketSprite.translateX(speed*deltaTime);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            bucketSprite.translateX(-speed*deltaTime);
        }
        //Transformamos los píxeles en la lógica de nuestro viewport
        if (Gdx.input.isTouched()){
            touchPosition.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPosition);
            bucketSprite.setCenterX(touchPosition.x);
        }


    }

    public void logic(){
        //Límites del mundo del juego y cubo
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();


        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidth-bucketWidth));


        //lógica para el movimiento de las gotas
        float delta = Gdx.graphics.getDeltaTime();

        //Aplly the bucket position and size to the bucketRectangle
        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);

        //loop for each drop
        //for(Sprite dropSprite: dropSprites){
          //  dropSprite.translateY(-2f*delta);
        //}

        dropTimer += delta;
        if(dropTimer > 1f){
            dropTimer = 0;
            createDroplet();
        }
        //Borramos desde el final del array, no por el principio
        for (int i = dropSprites.size -1; i >= 0; i--){
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getRegionHeight();

            dropSprite.translateY(-2f*delta);

            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            if (dropSprite.getY() < -dropHeight) {
                dropSprites.removeIndex(i);
            } else if (bucketRectangle.overlaps(dropRectangle)) {
                dropSprites.removeIndex(i);
                dropSound.play();
            }
        }

    }
    //Hay que borrar en cada ciclo.
    public void draw(){
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        //Convierte los píxeles en unidades para las imágenes que traemos.
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeigth = viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeigth);
        //.draw(bucketTexture, 0, 0, 1, 1);
        bucketSprite.draw(spriteBatch);

        //Recorremos el array de las gotas para que las pinte
        for(Sprite dropSprite: dropSprites){
            dropSprite.draw(spriteBatch);
        }

        spriteBatch.end();

    }

    public void createDroplet(){
        float dropWidth = 1;
        float dropHeight = 1;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        //dropSprite.setX(0);
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth));
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}
