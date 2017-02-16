package com.castlewars.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.castlewars.game.CastleWars;
import com.castlewars.game.sprites.Bullet;
import com.castlewars.game.sprites.Castle;
import com.castlewars.game.sprites.Knight;
import com.castlewars.game.sprites.Lane;




public class PlayState extends State {


    private Array<Lane> lanes;
    private static final int LANE_WIDTH = 60;
    private  static final int KNIGHT_DEPLOYMENT_TIME_SCALE = 30;


    private int pressedCounter;
    private boolean touched;
    private boolean touchState;
    private int xTouch;
    private int yTouch;
    private Castle castle;

    private boolean gameover;

    public PlayState(GameStateManager gsm){
        super(gsm);
        castle = new Castle(CastleWars.HEIGHT/2);
        lanes = new Array<Lane>();
        int xAxis = 0;

        for(int i = 1; i < 5; i++){
            Lane lane = new Lane(i,xAxis,castle);
            lanes.add(lane);
            xAxis+= 60;
        }

        cam.setToOrtho(false,CastleWars.WIDTH/2, CastleWars.HEIGHT/2);
        touched = false;
        touchState = false;
        gameover = false;
        xTouch = 0;
        yTouch = 0;
    }
    @Override
    public void handleInput() {
        yTouch = Gdx.input.getY();

        if(CastleWars.HEIGHT/2 <= yTouch ){
            if(Gdx.input.isTouched()){
                touchState = true;
                touched = true;
                xTouch = Gdx.input.getX();
                yTouch = Gdx.input.getY();
                pressedCounter++;

            }else{
                touchState = false;
            }

            if(touched && !touchState){
                selectedKnightLane(xTouch/2,yTouch/2,pressedCounter);
                //Gdx.app.log("TOUCH", xTouch + "-" + yTouch);
                pressedCounter = 0;
                touchState = false;
                touched = false;
            }
        }else{
            if(Gdx.input.justTouched()){
                yTouch = Gdx.input.getY();
                xTouch = Gdx.input.getX();
                selectedBulletLane(xTouch/2, yTouch/2);
                //Gdx.app.log("TOUCH", xTouch + "-" + yTouch);
            }
        }



    }

    @Override
    public void update(float dt) {
        //Gdx.app.log("DT",dt+"");
        handleInput();
        for (Lane ln: lanes) {
            ln.update(dt);
        }

    }
    public void selectedBulletLane(int x, int y){
        for (Lane ln: lanes) {
            if (ln.getStartX() < x && x <= ln.getEndX()) {
                Bullet bl = new Bullet(ln.getStartX(), ln.getEndY()-13);
                ln.deployBullet(bl);
                //Gdx.app.log("BULLET X:", x+"");
                //Gdx.app.log("SELECTED  BULLET LANE", ln.getLaneId()+"");
            }
        }
    }
    public  void selectedKnightLane(int x , int y, int pressTime){
        for (Lane ln: lanes) {
            if (ln.getStartX() < x && x <= ln.getEndX()) {
                Knight kn = new Knight(ln.getStartX(), ln.getEndY()/10, pressTime/KNIGHT_DEPLOYMENT_TIME_SCALE);
                ln.deployKnight(kn);
                //Gdx.app.log("ID: ", ln.getLaneId()+"");
                //Gdx.app.log("KNIGHT X:", x+"");
                //Gdx.app.log("SELECTED KNIGHT LANE", ln.getLaneId()+"");

            }
        }
    }




    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();



        for (Lane ln:lanes) {
            sb.draw(ln.getLane(), ln.getStartX(), 0);
            sb.draw(castle.getCastle(),castle.getPosition().x,castle.getPosition().y);
        }
        for (Lane ln:lanes) {
            for (Knight kn:ln.getKnights()) {
                sb.draw(kn.getKnight(),(ln.getStartX() + LANE_WIDTH/2) - kn.getKnight().getRegionWidth()/2 ,kn.getPosition().y);
            }
            for (Knight kn:ln.getAttackingKnights()) {
                sb.draw(kn.getKnight(),(ln.getStartX() + LANE_WIDTH/2) - kn.getKnight().getRegionWidth()/2 ,kn.getPosition().y);
            }
            for (Bullet bl: ln.getBullets()) {
                sb.draw(bl.getBullet(),ln.getStartX() + ((LANE_WIDTH/2) - (bl.getBullet().getWidth()/2)), bl.getPosition().y);
            }

        }

        sb.end();
    }

    @Override
    public void dispose() {
        for (Lane ln: lanes) {
            ln.dispose();
        }
        castle.getCastle().dispose();

    }
}
