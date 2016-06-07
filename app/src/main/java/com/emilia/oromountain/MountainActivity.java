package com.emilia.oromountain;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.percent.PercentFrameLayout;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Random;

public class MountainActivity extends Activity implements Guy.Listener {

    private HorizontalScrollViewExt container;
    private RelativeLayout sub_container;
    private ImageView mountain;
    private RelativeLayout icons;

    Button play_btn;
    ImageView return_btn;

    private int currentX;
    private int currentY;

    private int current_pos;
    private int mountain_max;
    private int mountain_height;
    private int mountain_y;
    private int mountain_0;
    private float MOUNTAIN_SIZE = 1.5f;

    ArrayList<MiniGameIcon> miniGames;
    //ArrayList<Integer> miniPos;

    ArrayList<Point> pathPoints;

    int guy_pos = 0;

    Point displaySize;
    Point canvasSize;
    double A, B; //y=Ax+B for canvas mountain slope

    Context context;

    Guy guy;
    Flower flower;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mountain);

        context = this;

        container = (HorizontalScrollViewExt) findViewById(R.id.Container);
        sub_container = (RelativeLayout) findViewById(R.id.SubContainer);


        mountain = (ImageView) findViewById(R.id.Mountain);
        icons = (RelativeLayout) findViewById(R.id.Icons);

        play_btn = (Button) findViewById(R.id.play_btn);
        int btn_width = (int)(getResources().getDisplayMetrics().widthPixels*0.2);
        play_btn.setWidth(btn_width);
        play_btn.setHeight(btn_width/4);

        return_btn = (ImageView) findViewById(R.id.return_btn);
        return_btn.setMinimumWidth(btn_width/4);
        return_btn.setMinimumHeight(btn_width/4);

        // mountain.setImageResource(R.drawable.orobics);

        miniGames = new ArrayList<>();
        //miniPos = new ArrayList<>();
        pathPoints = new ArrayList<>();

        setDimensions();

        System.out.println("PATHHHHH");
        addPath(4); //up to 20 looks reasonable


        guy = new Guy(this,sub_container);
        guy.setPosition(relativePoint( pathPoints.get(0) ));
        guy.setPath(prepareGuyPath());

        for(MiniGameIcon g : miniGames)
            g.setGuy(guy);
        flower.setGuy(guy);


        guy.registerListener((Guy.Listener) this);




        container.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(HorizontalScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
                Log.d("POSA", Float.toString(x) + " " + Float.toString(y));

                // y=Ax+B
                mountain_y = (int)(A*x + B);
                mountain.scrollTo(0, mountain_y);

                int move = mountain_max -mountain_y;
                guy.ChangePosBy(move);
                for (MiniGameIcon b : miniGames)
                    b.ChangePosBy(move);
                flower.ChangePosBy(move);
            }
        });
        container.setOnTouchListener( new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(guy.moving) return true;
                else return false;
            }
        });


    }


    private void setDimensions() {

        //get screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displaySize = new Point(size.x, size.y);

        //get hill image dimensions
        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = true;
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.orobics1, dimensions);
        int img_height = dimensions.outHeight;
        int img_width = dimensions.outWidth;

        double ratio = (double) img_width / (double) img_height;
        //set canvas dimensions
        ViewGroup.LayoutParams params = mountain.getLayoutParams();
        params.width =(int)(MOUNTAIN_SIZE * displaySize.x);
        params.height = (int) (params.width / ratio);
        mountain.setLayoutParams(params);

        //set canvas container dimensions
        FrameLayout.LayoutParams subParams = (FrameLayout.LayoutParams) sub_container.getLayoutParams();
        subParams.width = params.width;
        subParams.height = params.height;
        subParams.leftMargin = 0;
        subParams.topMargin = 0;
        sub_container.setLayoutParams(subParams);

        RelativeLayout.LayoutParams icoParams = (RelativeLayout.LayoutParams) icons.getLayoutParams();
        icoParams.width = params.width;
        icoParams.height = params.height;
        icoParams.leftMargin = 0;
        icoParams.topMargin = 0;
        icons.setLayoutParams(icoParams);



        //max Y position for canvas (bottom of the hill)
        mountain_max = params.height-(int)(displaySize.y*1.2);
        //min Y position for canvas (top of the hill)
        mountain_0 = (int)(params.height *0.1);
        //actual height of the mountain
        mountain_height = (int) (mountain_max -mountain_0);

        //initialize positions
        mountain.scrollTo(0, mountain_max);
        sub_container.scrollTo(0, 0);


        //initialize slope
        canvasSize = new Point(params.width, params.height);
        B = mountain_max;
        A = (mountain_0-B)/(canvasSize.x-displaySize.x);

    }


    private void addPath(int games) {

        //setting path thickness
        int pathThinckess = 15;
        if(games<11) pathThinckess=25;
        final int finalPathThinckess = pathThinckess;






        //---prepare path to attach it to the hill
        //Bitmap tempBitmap = ((BitmapDrawable)mountain.getDrawable()).getBitmap();
        Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.orobics1);

        //Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.orobics0);
        Bitmap mutableBitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas tempCanvas = new Canvas(mutableBitmap);
        tempCanvas.drawBitmap(mutableBitmap, 0, 0, null);

        System.out.println("path Mounta drawing:"+mountain.getWidth()+" "+mountain.getHeight());
        System.out.println("path Canvas drawing:"+tempCanvas.getWidth()+" "+tempCanvas.getHeight());


        preparePathPoints(games, tempCanvas);

        Point start = pathPoints.get(0);
        Point end = pathPoints.get(pathPoints.size()-1);
        System.out.println("PATH pointsss: "+start+", "+end);

        // y=Ax+B
        double A = (double)(end.y-start.y)/(end.x-start.x);
        double B;




        //preparing paints
        Paint paint_path = new Paint() {
            {
                setStyle(Style.STROKE);
                setStrokeCap(Cap.ROUND);
                setStrokeWidth(finalPathThinckess);
                setAntiAlias(true);
                setColor(Color.rgb(219,220,91));
                setDither(true);
                //mPaint.setColor(0xFFFFFF00);
                setStyle(Paint.Style.STROKE);
                setStrokeJoin(Paint.Join.ROUND);
                setStrokeCap(Paint.Cap.ROUND);
                //mPaint.setStrokeWidth(3);
            }
        };
        Paint paint2 = new Paint() {
            {
             //   setStyle(Style.STROKE);
                setStrokeCap(Cap.ROUND);
                setStrokeWidth(10.0f);
                setAntiAlias(true);
                setColor(Color.BLUE);
            }
        };
        Paint paint3 = new Paint() {
            {
                setStyle(Style.STROKE);
                setStrokeCap(Cap.ROUND);
                setStrokeWidth(10.0f);
                setAntiAlias(true);
                setColor(Color.RED);
            }
        };




        //create path from points
        Path path = new Path();
        path.moveTo(start.x, start.y);

        for(int i=0;i<pathPoints.size()-1;i++){
            Point _start = pathPoints.get(i);
            Point _end   = pathPoints.get(i+1);
            float mid_x,mid_y, mid_x2, mid_y2;

            //first curve point
            B = _start.y - A*_start.x;
            mid_x = _start.x + (_end.x - _start.x) * 2 / 5;
            mid_y = (int)(A*mid_x + B);
            //second curve point
            B = _end.y - A*_end.x;
            mid_x2 = _end.x - (_end.x - _start.x) * 2 / 5;
            mid_y2 = (int)(A*mid_x2 + B);

            RectF tmpRect = new RectF();
            tmpRect.top = _start.y-20;
            tmpRect.bottom = _start.y+20;
            tmpRect.left = start.x-200;
            tmpRect.right = _start.x+10;

//This call will draw nothing
        //    canvas.drawCircle(tmpRect.centerX(), tmpRect.centerY(), tmpRect.width() / 2, selectionPaint);
//This call will draw a circle
           // tempCanvas.drawOval(tmpRect, paint2);
            System.out.println("PAINT "+tmpRect+", "+_start);

            //tempCanvas.drawCircle(_start.x+5, _start.y+5, 10, paint3);
            //tempCanvas.drawCircle(mid_x, mid_y, 2, paint3);
            //tempCanvas.drawCircle(mid_x2, mid_y2, 5, paint3);

            //draw Bezier curve
            //path.quadTo(mid_x, mid_y, _end.x, _end.y);
            path.cubicTo(mid_x, mid_y, mid_x2, mid_y2, _end.x, _end.y);
        }




        //add path to canvas
        tempCanvas.drawPath(path, paint_path);

        //draw start and end
       // tempCanvas.drawCircle(start.x, start.y, 10, paint2);
       // tempCanvas.drawCircle(end.x, end.y, 10, paint2);

//        tempCanvas.drawCircle(100, mountain_max+100, 10, paint2);
//        tempCanvas.drawCircle(100, 2401-620, 10, paint2);
//        tempCanvas.drawCircle(100, 2401-520, 10, paint2);
//        tempCanvas.drawCircle(100, 2401-420, 10, paint2);
//        tempCanvas.drawCircle(100, 2401-320, 10, paint2);
//        tempCanvas.drawCircle(100, 2401-220, 10, paint2);


        //attach canvas to view
        mountain.setImageDrawable(new BitmapDrawable(getResources(), mutableBitmap));


        //add MiniGames Icons
        addMinigames();

//        miniGames.add(new OrodanceIcon(0, "g_", new Point((int)(icons.getLayoutParams().width*0.1),(int)(icons.getLayoutParams().height*0.1)), context, icons, 2, false));
//        miniGames.add(new OrodanceIcon(0, "g_", new Point((int)(icons.getLayoutParams().width*0.3),(int)(icons.getLayoutParams().height*0.3)), context, icons, 2, false));
//        miniGames.add(new FruitfiestaIcon(0, "g_", new Point((int)(icons.getLayoutParams().width*0.5),(int)(icons.getLayoutParams().height*0.5)), context, icons, 2, false));


    }

    private void preparePathPoints(int games, Canvas canvas){


        System.out.println("path Canvas:"+canvas.getWidth());
        //imageSize == (2701, 1689)

        //(50, 1350)
        Point start = new Point((int)(canvas.getWidth()*0.01851), (int)(canvas.getHeight()*0.79929));
        //(2200, 450)
        Point end   = new Point((int)(canvas.getWidth()*0.81451), (int)(canvas.getHeight()*0.26643));


        pathPoints.add(start);


        int dist_x = (int)(end.x-start.x)/(games+1);
        int dist_y = (int)(start.y - end.y)/(games+1);
        Random rand = new Random();
        int max_noise_top = (int)(start.y - end.y)/(7);
        int min_noise_bottom = (int)(start.y - end.y)/(6);

        //prepare points for path
        for(int i=1;i<=games;i++){
            int noise_x = rand.nextInt((int)(dist_x*0.2));
            int noise_y = rand.nextInt((int)(dist_y*1.0))+ (int)(dist_x*0.2);
            if(noise_y>max_noise_top) noise_y = max_noise_top;
            if(i%2==0)  noise_y = -noise_y;
            else {
                noise_x *=4;
                noise_y *=1.5;
                if(noise_y<min_noise_bottom) noise_y = min_noise_bottom;
            }

            Point tmp_p = new Point( start.x + dist_x*i + noise_x,
                    start.y - dist_y*i + noise_y);
            pathPoints.add(tmp_p);
        }

        pathPoints.add(end);

    }


    private void addMinigames(){

        //FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sub_container.getLayoutParams();

        //a.x == 720, a.y = 1280
        Point a = new Point();
        getWindowManager().getDefaultDisplay().getSize(a);
        Log.d("XY", Integer.toString(a.x)+" "+Integer.toString(a.y));



        generateMiniGames();

        addFlower();

    }

    private void generateMiniGames(){

        Random rand = new Random();
        int enable = rand.nextInt(pathPoints.size()-2)+2;

        for(int i=1; i<pathPoints.size()-1; i++){
            Point p = relativePoint(pathPoints.get(i));

            int stars = rand.nextInt(4);
            boolean enabled = i<enable;
            enabled=true;
            boolean fruit = rand.nextBoolean();

            if(!fruit)
                miniGames.add(new OrodanceIcon(i, "g_"+i, p, context, icons, stars, enabled));
            else
                miniGames.add(new FruitfiestaIcon(i, "g_"+i, p, context, icons, stars, enabled));

        }

    }

    public Point relativePoint(Point p){

//        float pxs = x;
//        float dps = pxs/getResources().getDisplayMetrics().density;
        System.out.println("DENSITY:"+getResources().getDisplayMetrics().density);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        System.out.println(dm);

        //--good for MOUNTAIN_SIZE==1.5
//        float scale = 1 * getResources().getDisplayMetrics().density /( dm.xdpi/dm.densityDpi) ;
//        float y_move = mountain_max;
//
//        System.out.println("POOOINT: "+p+" => "+new Point((int)(p.x*scale/MOUNTAIN_SIZE), (int)((p.y)*scale/MOUNTAIN_SIZE-y_move)));
//        return new Point((int)(p.x*scale/MOUNTAIN_SIZE), (int)((p.y)*scale/MOUNTAIN_SIZE-y_move));


        //a bit too big when increasing mountine_size
//        float scale = 1 /( dm.xdpi/dm.densityDpi) ;
//        float y_move = mountain_max;
//
//        Point ret = new Point((int)(p.x*MOUNTAIN_SIZE/scale), (int)((p.y)*MOUNTAIN_SIZE/scale-y_move));
//        System.out.println("POOOINT: "+p+" => "+ret);
//        System.out.println("POOOINT: "+icons.getLayoutParams().width+", "+ icons.getLayoutParams().height);

        float scale = 1 /( dm.xdpi/dm.densityDpi) ;
        float y_move = mountain_max;



        Point ret = new Point((int)(p.x*MOUNTAIN_SIZE/scale/mountain.getLayoutParams().width*icons.getLayoutParams().width),
                (int)((p.y)*MOUNTAIN_SIZE/scale/mountain.getLayoutParams().height*icons.getLayoutParams().height)-mountain_max);
        //Point ret = new Point((int)((float)p.x/(float)mountain.getLayoutParams().width*icons.getLayoutParams().width),
        //        (int)((float)p.y/(float)mountain.getLayoutParams().height*icons.getLayoutParams().height)-(int)(mountain_max));
        System.out.println("POOOINT: "+p+" => "+ret);
        System.out.println("POOOINT: "+(float)p.x/(float)mountain.getLayoutParams().width+", "+ (float)p.y/(float)mountain.getLayoutParams().height);


        return ret;


    }


    private ArrayList<Point> prepareGuyPath(){
        ArrayList<Point> arr = new ArrayList<>();

        for (Point p:pathPoints)
            arr.add(relativePoint(p));

        return arr;
    }


    private void addFlower(){
        flower = new Flower(pathPoints.size()-1,relativePoint(pathPoints.get(pathPoints.size()-1)),context,icons,true);

    }


    @Override
    public void onMoveStart(){
        play_btn.setVisibility(View.INVISIBLE);
        VideoModeOn();
    }

    @Override
    public void onMoveEnded(int pos) {
        setPlayBtn();
        VideoModeOff();
    }

    private void setPlayBtn(){
        System.out.println("BTN");
        if(miniGames.get(current_pos).enabled) {
            play_btn.setVisibility(View.VISIBLE);

            play_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO onclick settings
                    System.out.println("Current index of a game:"+current_pos);
                }
            });
        }
        else
            play_btn.setVisibility(View.INVISIBLE);
    }


    private void VideoModeOn(){
        final ImageView top =  (ImageView) findViewById(R.id.black_rect_top);
        final ImageView bottom =  (ImageView) findViewById(R.id.black_rect_bottom);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.1);


        ValueAnimator anim = ValueAnimator.ofInt(top.getHeight(), height);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                //top rectangle
                ViewGroup.LayoutParams layoutParams = top.getLayoutParams();
                layoutParams.height = val;
                top.setLayoutParams(layoutParams);
                //bottom rectangle
                ViewGroup.LayoutParams layoutParams2 = bottom.getLayoutParams();
                layoutParams2.height = val;
                bottom.setLayoutParams(layoutParams2);
            }
        });
        anim.setDuration(200);
        anim.start();


    }

    private void VideoModeOff(){
        final ImageView top =  (ImageView) findViewById(R.id.black_rect_top);
        final ImageView bottom =  (ImageView) findViewById(R.id.black_rect_bottom);


        ValueAnimator anim = ValueAnimator.ofInt(top.getHeight(), 0);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                //top rectangle
                ViewGroup.LayoutParams layoutParams = top.getLayoutParams();
                layoutParams.height = val;
                top.setLayoutParams(layoutParams);
                //bottom rectangle
                ViewGroup.LayoutParams layoutParams2 = bottom.getLayoutParams();
                layoutParams2.height = val;
                bottom.setLayoutParams(layoutParams2);
            }
        });
        anim.setDuration(200);
        anim.start();


    }
}
