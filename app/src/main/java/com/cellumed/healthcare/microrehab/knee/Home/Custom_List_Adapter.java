package com.cellumed.healthcare.microrehab.knee.Home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cellumed.healthcare.microrehab.knee.R;

import java.util.ArrayList;

/**
 * Created by test on 2016-10-23.
 */
public class Custom_List_Adapter extends BaseAdapter { // implements View.OnTouchListener {

    private static Typeface typeface;



    private ArrayList<Custom_List_View_Item> listViewItemList = new ArrayList<Custom_List_View_Item>();
    private OnItemValueChangedListener listener;

/*
    private void setGlobalFont(View view) {
        if(view != null) {
            if(view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup)view;
                int vgCnt = viewGroup.getChildCount();
                for(int i = 0; i<vgCnt; i++) {
                    View v = viewGroup.getChildAt(i);
                    if(v instanceof TextView) {
                        ((TextView) v).setTypeface(typeface);
                    }
                    setGlobalFont(v);
                }
            }
        }
    }
*/
    public Custom_List_Adapter(Activity activity, OnItemValueChangedListener listener){
        this.listener = listener;
        if(typeface == null) {
            typeface = Typeface.createFromAsset(activity.getAssets(), "NotoSansKR-Regular-Hestia.otf");
        }
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        final ViewGroup par = parent;

        TextView name = null;
        Button decrease = null;
        TextView level = null;
        Button increase = null;
        CustomHolder holder = null;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview_item, parent, false);

            //setGlobalFont(convertView);

            name = (TextView) convertView.findViewById(R.id.nameView);
            decrease = (Button) convertView.findViewById(R.id.decreaseLevel);
            level = (TextView) convertView.findViewById(R.id.levelValue);
            increase = (Button) convertView.findViewById(R.id.increaseLevel);


/*
            Rect delegateArea = new Rect();
            increase.getHitRect(delegateArea);
            convertView.getHitRect(delegateArea);
            delegateArea.left -= 50;
            delegateArea.right += 50;
            delegateArea.top -= 10;
            delegateArea.bottom += 10;
            parent.setTouchDelegate(new TouchDelegate(delegateArea,increase));
*/
            holder = new CustomHolder();
            holder.nameView = name;
            holder.decrease = decrease;
            holder.valueView = level;
            holder.increase = increase;

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
            name = holder.nameView;
            decrease = holder.decrease;
            level = holder.valueView;
            increase = holder.increase;
        }

        name.setText(listViewItemList.get(position).getNameString());
        level.setText(listViewItemList.get(position).getLevelValueString());

        listViewItemList.get(position).setLevel(level);

        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TT","de onClick  "+this);
                int powerValue = Integer.parseInt(listViewItemList.get(position).getLevelValueString());
                powerValue--;

                if (powerValue < 0) {
                    return;
                }
                listViewItemList.get(position).setLevelValueString(String.format("%02d", powerValue));
                listener.onItemValueChanged();
                notifyDataSetChanged();
                //listViewItemList.get(position).getLevel().invalidate();

            }
        });


        decrease.setOnTouchListener(new View.OnTouchListener() {


            private int initialInterval =500;
            private final int normalInterval = 250;

         //   Log.d("TT","Touch NEW ");

            private Runnable handlerRunnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("TT","Touch DN run " + listViewItemList.get(position).getTouched() + " a=" + this);

                        if (listViewItemList.get(position).getTouched()) {
                            listViewItemList.get(position).getHandler().postDelayed(this, normalInterval);

                            //onclick
                            int powerValue = Integer.parseInt(listViewItemList.get(position).getLevelValueString());
                            powerValue--;

                            if (powerValue < 0) {
                                return;
                            }
                            listViewItemList.get(position).setLevelValueString(String.format("%02d", powerValue));
                            listener.onItemValueChanged();
                            listViewItemList.get(position).getLevel().setText(String.format("%02d", powerValue));
                            listViewItemList.get(position).getLevel().invalidate();
                        }


                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewParent vv;
               // dumpEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vv=v.getParent();
                        vv.requestDisallowInterceptTouchEvent(true);
                        vv=vv.getParent();
                        vv.requestDisallowInterceptTouchEvent(true);

                        Log.d("TT","Touch DN + "+ listViewItemList.get(position).getHandler());

                        listViewItemList.get(position).setTouched(true);
                        listViewItemList.get(position).getHandler().removeCallbacks(null);
                        listViewItemList.get(position).getHandler().postDelayed(handlerRunnable, initialInterval);

                        break;


                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_CANCEL:

                        listViewItemList.get(position).getHandler().removeCallbacks(null);
                        listViewItemList.get(position).setTouched(false);

                        vv=v.getParent();
                        vv.requestDisallowInterceptTouchEvent(false);
                        vv=vv.getParent();
                        vv.requestDisallowInterceptTouchEvent(false);

                        Log.d("TT","Touch UP "+listViewItemList.get(position).getHandler()+event.getAction()) ;
                        notifyDataSetChanged();

                        break;

                }

                // touch상태면 onclick등 수행X
                return  false;
            }

        });

        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TT","onClick  "+this);
                int powerValue = Integer.parseInt(listViewItemList.get(position).getLevelValueString());
                powerValue++;

                if (powerValue > 99) {
                    return;
                }
                listViewItemList.get(position).setLevelValueString(String.format("%02d", powerValue));
                listener.onItemValueChanged();
                notifyDataSetChanged();

            }
        });


        increase.setOnTouchListener(new View.OnTouchListener() {


            private int initialInterval =500;
            private final int normalInterval = 250;


            //   Log.d("TT","Touch NEW ");

            private Runnable handlerRunnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("TT","Touch DN run " + listViewItemList.get(position).getTouched() + " a=" + this);

                    if (listViewItemList.get(position).getTouched()) {
                        listViewItemList.get(position).getHandler().postDelayed(this, normalInterval);


                        //onclick
                        int powerValue = Integer.parseInt(listViewItemList.get(position).getLevelValueString());
                        powerValue++;

                        if (powerValue > 99) {
                            return;
                        }
                        listViewItemList.get(position).setLevelValueString(String.format("%02d", powerValue));

                        listener.onItemValueChanged();
                        listViewItemList.get(position).getLevel().setText(String.format("%02d", powerValue));
                        listViewItemList.get(position).getLevel().invalidate();
                    }

                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewParent vv;
               //dumpEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vv=v.getParent();
                        vv.requestDisallowInterceptTouchEvent(true);
                        vv=vv.getParent();
                        vv.requestDisallowInterceptTouchEvent(true);

                        Log.d("TT","Touch DN + "+ listViewItemList.get(position).getHandler());

                        listViewItemList.get(position).setTouched(true);
                        listViewItemList.get(position).getHandler().removeCallbacks(null);
                        listViewItemList.get(position).getHandler().postDelayed(handlerRunnable, initialInterval);


                        break;
                    case MotionEvent.ACTION_MOVE:
                        vv = v.getParent();

                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_CANCEL:
                        listViewItemList.get(position).getHandler().removeCallbacks(null);
                        listViewItemList.get(position).setTouched(false);

                        vv=v.getParent();
                        vv.requestDisallowInterceptTouchEvent(false);
                        vv=vv.getParent();
                        vv.requestDisallowInterceptTouchEvent(false);

                        Log.d("TT","Touch UP "+listViewItemList.get(position).getHandler()+event.getAction()) ;
                        notifyDataSetChanged();




                        break;

                }

                // touch상태면 onclick등 수행X
                return false;
            }

        });

        return convertView;
    }

    public void addItem(String name, String value) {
        Custom_List_View_Item item = new Custom_List_View_Item();
        item.setNameString(name);
        item.setLevelValueString(value);
        listViewItemList.add(item);
    }

    private class CustomHolder {
        TextView nameView;
        Button decrease;
        TextView valueView;
        Button increase;
    }


    public interface OnItemValueChangedListener{
        void onItemValueChanged();
    }


    private void dumpEvent(MotionEvent event) {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
        sb.append("]");
        Log.d("TT", sb.toString());
    }

}
