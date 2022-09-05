package com.myproject.appservice.controllers.calendarBusiness;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myproject.appservice.Common;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.calendarBusiness.bookingBusinessDetail.ActivityNewBooking;
import com.myproject.appservice.databinding.AdapterItemTimeBookingCalendarBusinessBinding;
import com.myproject.appservice.databinding.BreakViewBookingCalendarBinding;
import com.myproject.appservice.databinding.EventViewBookingCalendarBinding;
import com.myproject.appservice.models.Event;
import com.myproject.appservice.models.EventI;
import com.myproject.appservice.models.Schedule;

import java.util.ArrayList;

public class TimeLineView extends LinearLayout {

    public static int TOTAL = 24;
    public static int END_LABEL;
    final float SELECT_VIBRATION_TIME_MS = 60L;
    final int DEFAULT_START_LABEL = 7;
    final int DEFAULT_BG_COLOR = Color.BLACK;
    final int DEFAULT_LABEL_COLOR = Color.DKGRAY;
    final int DEFAULT_TEXT_COLOR = Color.BLACK;
    final String [] DAYS = getResources().getStringArray(R.array.array_days_week);
    final int NUMDAYSOFWEEK = CalendarUtils.selectedDate.getDayOfWeek().getValue()-1;

    private ArrayList<AdapterItemTimeBookingCalendarBusinessBinding> timelineViews;
    private ArrayList<View> timelineItemViews;
    private ArrayList<EventI> timeLineItems;
    private ArrayList<Rect> timelineItemRects;
    private GestureDetectorCompat mDetector;
    private ArrayList<Event> timelineEvents;
    private ArrayList<Rect> timeLineNoWorkRects;
    private ArrayList<View> timeLineNoWorkView;
    private ArrayList<EventI> timeLineNoWorkItems;
    private ArrayList<String> timeBreak;
    private Paint paint;

    private int openTime, closeTime;


    private Integer labelColor = DEFAULT_LABEL_COLOR;
    private Integer  eventBg =  Color.CYAN;
    private Integer  eventNameColor = DEFAULT_TEXT_COLOR;
    private Integer  eventTimeColor = DEFAULT_TEXT_COLOR;

    private AttributeSet attrs;

    public TimeLineView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        constructor(attrs);

    }

    public TimeLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        constructor(attrs);
    }

    private void constructor(AttributeSet attrs){
        this.attrs = attrs;
        loadData();

        initListeners();

    }

    public void loadData(){
        paint = new Paint();
        timelineViews = new ArrayList<>();
        timelineItemViews = new ArrayList<>();
        timeLineItems = new ArrayList<>();
        timelineItemRects = new ArrayList<>();
        timelineEvents = new ArrayList<>();
        timeLineNoWorkRects = new ArrayList<>();
        timeLineNoWorkView = new ArrayList<>();
        timeLineNoWorkItems = new ArrayList<>();
        timeBreak = new ArrayList<>();
     //   openTime = DEFAULT_START_LABEL;
        closeTime = TOTAL;
        END_LABEL = TOTAL;
        int dayOfWeek = CalendarUtils.selectedDate.getDayOfWeek().getValue()-1;
        String [] days = getResources().getStringArray(R.array.array_days_week);

        Task<QuerySnapshot> schedulesRef = FirebaseFirestore.getInstance()
                .collection("Businesses")
                .document(Common.idBusiness)
                .collection("Schedules")
                .whereEqualTo("opened", true)
                .whereEqualTo("day", days[dayOfWeek])
                .get();
        schedulesRef.addOnCompleteListener(task -> {
            if (task.isSuccessful() ){
                ArrayList<Schedule> schedulesBooking = new ArrayList<Schedule>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("TAG", document.getId() + " => " + document.getData());
                    Schedule schedule = document.toObject(Schedule.class);
                    schedulesBooking.add(schedule);
                }
                TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TimelineView,
                        0, 0);
                labelColor = typedArray.getColor(R.styleable.TimelineView_labelColor, DEFAULT_LABEL_COLOR);
                eventNameColor = typedArray.getColor(R.styleable.TimelineView_eventNameColor, DEFAULT_TEXT_COLOR);
                eventTimeColor = typedArray.getColor(R.styleable.TimelineView_eventTimeColor, DEFAULT_TEXT_COLOR);
                eventBg = typedArray.getColor(R.styleable.TimelineView_eventBackground, getColor(R.attr.colorPrimary));
                setOrientation(VERTICAL);
//                    if(schedulesBooking.size() == 0){
//                        closeTime = 24;
//                        for (int i = 0; i < closeTime; i++) {
//                            BreakViewBookingCalendarBinding binding = BreakViewBookingCalendarBinding.inflate(LayoutInflater.from(getContext()));
//                            binding.setEvent(new EventI(new Event("", "", i*3600, 3600*(i+1)), openTime));
//                            binding.cardView.setCardBackgroundColor(Color.LTGRAY);
//                            binding.setConstrained(true);
//                            timeLineNoWorkView.add(binding.getRoot());
//                            binding.executePendingBindings();
//                        }
//                    } else {
                    loadOpenAndClose(schedulesBooking);
//                    }
                //initLabels();

                getTimelineEvents();
            } else {
                //Tratar error
            }
        });
    }

    private void loadOpenAndClose(ArrayList<Schedule> schedulesBooking){
        String[] range = null;
        Schedule schedule = null;
        if(schedulesBooking.size()> 0) {
            schedule = schedulesBooking.get(0);
            range = schedule.getSchedulesDay().get(0).split(" - ");
            openTime = Integer.parseInt(range[0].split(":")[0]);
            range = schedule.getSchedulesDay().get(schedulesBooking.get(0).getSchedulesDay().size()-1).split(" - ");

            closeTime = Integer.parseInt(range[1].split(":")[0]);
            if(closeTime >= 1 && closeTime <= 23){
                closeTime++;
            } else if(closeTime == 0){
                closeTime = 24;
            }
//        if (closeTime < 23 && range != null && !range[1].split(":")[1].contains("00")) {
//            closeTime++;
//        }
        }

        if(closeTime > openTime){
            closeTime = closeTime - openTime;
        } else {
            closeTime = openTime - closeTime;
        }

//        timelineEvents.add(new Event("Orgy", "Cortar el pelo", 0, 3600*6+30*60));
//        setTimeLineEvents();
        setStartTime();
        if(schedule != null && schedule.getSchedulesDay().size()>=1) {
            String iniH = "";
            String finH = "";
            int i=0;
            for (String h : schedule.getSchedulesDay()) {
                range = h.split(" - ");
                if(i != 0){
                    iniH = range[0];
                    timeBreak.add(finH + " - " + iniH);
                }
                finH = range[1];
                i++;
            }
        } else {
            timeBreak.add(openTime + ":00 - " + closeTime + ":00");
        }
        setTimeLineBreaks();
    }

    public void setTimeLineBreaks(){
        timeLineNoWorkItems.clear();
        timeBreak.forEach((it)-> {
            int hInit = Integer.parseInt((it.split(" - ")[0]).split(":")[0]);
            int mInit = Integer.parseInt((it.split(" - ")[0]).split(":")[1]);
            int hFin = Integer.parseInt((it.split(" - ")[1]).split(":")[0]);
            int mFin = Integer.parseInt((it.split(" - ")[1]).split(":")[1]);
            int x = 0;
            for(int i = hInit; i< hFin; i++){
                x = i + 1;
                int calIn, calFin;
                if(mInit > 0){
                    calIn = (3600 * i)+(mInit) * 60;
                } else{
                    calIn = (3600 * i);
                }

                if(mFin > 0){
                    calFin = (3600 * i)+(mFin) * 60;
                } else{
                    calFin = (3600 * x);
                }
                mInit = 0;
                mFin = 0;
                System.out.println("*******");
                timeLineNoWorkItems.add(new EventI(new Event("", "", calIn, calFin), openTime));
                timeLineNoWorkRects.add(new Rect());
            }
        });
        addTimelineBreakItemViews();
        requestLayout();
    }

    private void setStartTime(){
        if(closeTime > openTime){
            closeTime = closeTime - openTime;
        } else {
            closeTime = openTime - closeTime;
        }

        if(openTime < 0 || openTime > TOTAL){
            throw new IllegalArgumentException("Start time has to be fom 0 to 23");
        }
        initLabels();
    }

    public void setTimeLineEvents(){
        timeLineItems.clear();
        timelineEvents.forEach((it)-> {
            timelineItemRects.add(new Rect());
            timeLineItems.add(new EventI(it, openTime));
        });
        addTimelineItemViews();
        requestLayout();
    }

    private void addTimelineItemViews(){
        timelineItemViews.clear();
        for(EventI item : timeLineItems){
            EventViewBookingCalendarBinding binding = EventViewBookingCalendarBinding.inflate(LayoutInflater.from(getContext()));
            binding.setEvent(item);
            binding.cardView.setCardBackgroundColor(eventBg);
            binding.setConstrained(true);
            binding.eventName.setTextColor(eventNameColor);
            binding.eventTime.setTextColor(eventTimeColor);
            timelineItemViews.add(binding.getRoot());

            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ActivityNewBooking.class);
                getContext().startActivity(intent);
            });

            binding.executePendingBindings();
        }
    }

    private void addTimelineBreakItemViews(){
        timeLineNoWorkView.clear();
        for(EventI item : timeLineNoWorkItems){
            BreakViewBookingCalendarBinding binding = BreakViewBookingCalendarBinding.inflate(LayoutInflater.from(getContext()));
            binding.setEvent(item);
            binding.cardView.setCardBackgroundColor(Color.LTGRAY);
            binding.setConstrained(true);
            timeLineNoWorkView.add(binding.getRoot());

            binding.executePendingBindings();
        }
    }

    private void initLabels() {
        timelineViews.clear();
        removeAllViewsInLayout();
        for (int i = 0; i < closeTime; i++) {
            AdapterItemTimeBookingCalendarBusinessBinding binding = AdapterItemTimeBookingCalendarBusinessBinding.inflate(LayoutInflater.from(getContext()), this, false);
            binding.timelineLabel.setText(getTime(i, openTime));
            binding.setLabelColor(labelColor);
            timelineViews.add(binding);
            addView(binding.root);
        }
    }

    @Override
    protected void onLayout(boolean changed, int  l, int  t, int  r, int b){
        super.onLayout(changed, l, t, r, b);
        if(timelineViews.size() > 0) {
            int divWidth = timelineViews.get(0).divider.getWidth();
            for (int index = 0; index < timelineItemViews.size(); index++) {
                int left = (int) (getRight() - divWidth);
                int top = getPosFromIndex(timeLineItems.get(index).getStartIndex());
                int bottom = getPosFromIndex(timeLineItems.get(index).getEndIndex());
                int right = (int) (getRight());

                int widthSpec = MeasureSpec.makeMeasureSpec(right - left, MeasureSpec.EXACTLY);
                int heightSpec = MeasureSpec.makeMeasureSpec(bottom - top, MeasureSpec.EXACTLY);
                timelineItemViews.get(index).measure(widthSpec, heightSpec);
                timelineItemViews.get(index).layout(0, 0, right - left, bottom - top);
            }

            for (int index = 0; index < timeLineNoWorkView.size(); index++) {
                int left = (int) (getRight() - divWidth);
                int top = getPosFromIndex(timeLineNoWorkItems.get(index).getStartIndex());
                int bottom = getPosFromIndex(timeLineNoWorkItems.get(index).getEndIndex());
                int right = (int) (getRight());

                int widthSpec = MeasureSpec.makeMeasureSpec(right - left, MeasureSpec.EXACTLY);
                int heightSpec = MeasureSpec.makeMeasureSpec(bottom - top, MeasureSpec.EXACTLY);
                timeLineNoWorkView.get(index).measure(widthSpec, heightSpec);
                timeLineNoWorkView.get(index).layout(0, 0, right - left, bottom - top);
            }
        }

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(timelineViews.size() > 0) {
            int divWidth = timelineViews.get(0).divider.getWidth();
            for (int index = 0; index < timelineItemViews.size(); index++) {
                int left = (int) (getRight() - divWidth);
                int top = getPosFromIndex(timeLineItems.get(index).getStartIndex());
                int bottom = getPosFromIndex(timeLineItems.get(index).getEndIndex());
                int right = (int) (getRight());

                timelineItemRects.get(index).set(left, top, right, bottom);

                canvas.save();
                canvas.translate((int) (left), (int) (top));
                timelineItemViews.get(index).draw(canvas);
                canvas.restore();
            }

            for (int index = 0; index < timeLineNoWorkView.size(); index++) {
                int left = (int) (getRight() - divWidth);
                int top = getPosFromIndex(timeLineNoWorkItems.get(index).getStartIndex());
                int bottom = getPosFromIndex(timeLineNoWorkItems.get(index).getEndIndex());
                int right = (int) (getRight());

                timeLineNoWorkRects.get(index).set(left, top, right, bottom);

                canvas.save();
                canvas.translate((int) (left), (int) (top));
                timeLineNoWorkView.get(index).draw(canvas);
                canvas.restore();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDetector.onTouchEvent(event)) {
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    private Integer getColor(Integer colorAttr) {
//        TypedValue typedValue = new TypedValue();
//        Resources.Theme theme = getContext().getTheme();
//        theme.resolveAttribute(colorAttr, typedValue, true);
//        Integer color = typedValue.data;
//        return color;

        TypedValue typedValue = new TypedValue();
        TypedArray a = getContext().obtainStyledAttributes(typedValue.data, new int[]{colorAttr});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    private String getTime(Integer raw, Integer start) {
        String state = "AM";
        int time = (raw + start) % TOTAL;

//        if (time >= 12) state = "PM";
//        if (time == 0) time = 12;
//        if (time > 12) time -= 12;

        return String.format("%2d", time);
    }

    private float dpToPx(Integer dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.floatValue(), getResources().getDisplayMetrics());
    }

    private Integer getPosFromIndex(Float index)  {
        int l = (int) Math.floor(index);
        int h = (int) Math.ceil(index);

        int lVal = timelineViews.get(l).getRoot().getTop() + timelineViews.get(l).divider.getTop();
        int hVal = timelineViews.get(h).getRoot().getTop() + timelineViews.get(l).divider.getTop();

        return (int) (lVal + (index - l) * (hVal - lVal));
    }

    public ArrayList<Event> getTimelineEvents() {
        return timelineEvents;
    }

    private void initListeners(){
        mDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent event) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (e != null) {
                    int i = -1;
                    for(int x = 0; x < timelineItemRects.size(); x++ ){
                        if(timelineItemRects.get(x).contains((int) e.getX(), (int)e.getY())){
                            i = x;
                            break;
                        }
                    }
                    if (i != -1) {
                        timelineItemViews.get(i).callOnClick();
                    }
                }
            }
        });
    }
}
