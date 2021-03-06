package umbc.edu.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import umbc.edu.services.GuideBoxService;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Bharath on 4/25/2017.
 */

public class browseListAdapter extends ArrayAdapter<GuideBoxService.Result> {

    private ArrayList<SharedPreferences> prefs;

    private static class ViewHolder {
        TextView title;
        TextView description;
        Spinner statusSpinner;
        ImageView artWork;
    }

    private List<GuideBoxService.Result> dataSet;
    private List<Bitmap> imageList;
    private List<String> descriptionList;
   // List<Integer> prevPosition = new ArrayList<>();
    int[] prevPosition = new int[25];

    Context mContext;

    public browseListAdapter(List<GuideBoxService.Result> data, List<Bitmap> browseImages, List<String> browseDescription, Context context, ArrayList<SharedPreferences> prefs) {
        super(context, R.layout.list_item, data);
        this.dataSet = data;
        this.mContext = context;
        this.imageList = browseImages;
        this.descriptionList = browseDescription;
        this.prefs = prefs;
    }
    public View getView(int position, View convertView, ViewGroup parent){

        final GuideBoxService.Result tempResult  = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.titleView);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.artWork = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.statusSpinner = (Spinner) convertView.findViewById(R.id.spinner);
            result = convertView;
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Log.d("in adapter", String.valueOf(position));
        viewHolder.title.setText(tempResult.getTitle());
        viewHolder.description.setText(descriptionList.get(position));
        //viewHolder.artWork.setImageResource(R.drawable.reasons_why_2);
        Bitmap mbitmap = imageList.get(position);
        Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        final float densityMultiplier = getContext().getResources().getDisplayMetrics().density;
        final float roundPx = 40*densityMultiplier;
        Paint mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight()-roundPx)), mpaint);
        canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), roundPx, roundPx, mpaint);// Round Image Corner 100 100 100 100
        viewHolder.artWork.setImageBitmap(imageRounded);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.spinner_list, R.layout.spinner_list);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down);
        viewHolder.statusSpinner.setAdapter(adapter);

        viewHolder.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //prevPosition[position] = pos;

                //TODO: pos gives you the button value
                //0 -> ADD_TO main button
                //1 -> WATCHING
                //2 -> COMPLETED

                SharedPreferences.Editor editor,editor1;

                if(pos==1){
                    editor = prefs.get(0).edit();
                    editor.putString(tempResult.getImdbID(), tempResult.getImdbID());
                    editor.commit();
                    editor1 = prefs.get(1).edit();
                    editor1.remove(tempResult.getImdbID());
                    editor1.commit();
                    editor1 = prefs.get(2).edit();
                    editor1.remove(tempResult.getImdbID());
                    editor1.commit();
                    editor1 = prefs.get(3).edit();
                    editor1.remove(tempResult.getImdbID());
                    editor1.commit();
                }else if(pos==2){
                    editor = prefs.get(1).edit();
                    editor.putString(tempResult.getImdbID(), tempResult.getImdbID());
                    editor.commit();
                    editor1 = prefs.get(0).edit();
                    editor1.remove(tempResult.getImdbID());
                    editor1.commit();
                    editor1 = prefs.get(2).edit();
                    editor1.remove(tempResult.getImdbID());
                    editor1.commit();
                    editor1 = prefs.get(3).edit();
                    editor1.remove(tempResult.getImdbID());
                    editor1.commit();
                }else if(pos==3){
                    editor = prefs.get(2).edit();
                    editor.putString(tempResult.getImdbID(), tempResult.getImdbID());
                    editor.commit();
                    editor1 = prefs.get(1).edit();
                    editor1.remove(tempResult.getImdbID());
                    editor1.commit();
                    editor1 = prefs.get(0).edit();
                    editor1.remove(tempResult.getImdbID());
                    editor1.commit();
                    editor1 = prefs.get(3).edit();
                    editor1.remove(tempResult.getImdbID());
                    editor1.commit();
                }else if(pos==4){
                    editor = prefs.get(3).edit();
                    editor.putString(tempResult.getImdbID(), tempResult.getImdbID());
                    editor.commit();
                    editor1 = prefs.get(1).edit();
                    editor1.remove(tempResult.getImdbID());
                    editor1.commit();
                    editor1 = prefs.get(0).edit();
                    editor1.remove(tempResult.getImdbID());
                    editor1.commit();
                    editor1 = prefs.get(2).edit();
                    editor1.remove(tempResult.getImdbID());
                    editor1.commit();

                }

                //TODO:
                // access to ImdbID - tempResult.getImdbID()
                //editor.putString("IMDB_ID", "IMDB_ID");
                //editor.commit();

                //prefs.get(0).getAll();

               // Toast.makeText(getApplicationContext(),tempResult.getImdbID(),Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        String testImdb = prefs.get(0).getString(tempResult.getImdbID(),"0");
        if(!testImdb.equals("0")){
            prevPosition[position] = 1;
        }
        testImdb = prefs.get(1).getString(tempResult.getImdbID(),"0");
        if(!testImdb.equals("0")){
            prevPosition[position] = 2;
        }
        testImdb = prefs.get(2).getString(tempResult.getImdbID(),"0");
        if(!testImdb.equals("0")){
            prevPosition[position] = 3;
        }
        testImdb = prefs.get(3).getString(tempResult.getImdbID(),"0");
        if(!testImdb.equals("0")){
            prevPosition[position] = 4;
        }

            viewHolder.statusSpinner.setSelection(prevPosition[position]);

            //viewHolder.artWork.setImageBitmap(imageList.get(position));
        return convertView;
    }
}
