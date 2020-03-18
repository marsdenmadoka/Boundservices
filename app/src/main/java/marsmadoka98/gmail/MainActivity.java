package marsmadoka98.gmail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private OdometerService odometer;//We’ll use this for the OdometerService.
    private boolean bound = false; //Use this to store whether or not the activity’s bound to the service

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            watchMileage();//we call this method here so that it starts running when the activity is created
    }

         //Create a ServiceConnection;
    //we said that an activity binds to a service using a ServiceConnection object.
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            OdometerService.OdometerBinder odometerBinder =
                    (OdometerService.OdometerBinder) binder;
            odometer = odometerBinder.getOdometer();//Cast the Binder to an OdometerBinder, then use to get a reference to the OdometerService.
            bound = true; //When the service is connected, set bound to true.
        }

        public void onServiceDisconnected(ComponentName componentName) {
            bound = false; //When the service is disconnected,set bound to false.
        }
    };


    @Override
    //Bind to the service when the activity starts
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, OdometerService.class);//This is an intent directed to the OdometerService.
        bindService(intent, connection, Context.BIND_AUTO_CREATE);//This uses the intent and service connectionto bind the activity to the service.
        /** The code Context.BIND_AUTO_CREATE tells Android to
         create the service if it doesn’t already exist*/
    }

    //Unbind from the service when the activity stops
    @Override
    protected void onStop() {
        super.onStop();
        if (bound) { //This uses the service connection to unbind from the service.
            unbindService(connection);
            bound = false;
        }
    }

    //Display the distance traveled
    private void watchMileage() {
        final TextView distanceView = (TextView) findViewById(R.id.distance);
        final Handler handler = new Handler();//Post the code in the Runnable to be run again after a delay of 1,000 milliseconds, or 1 second. As this line of code is included in theRunnable run() method, it will run every second (with a slight lag).
        handler.post(new Runnable() { //Call the post() method, passing in a new Runnable.
            @Override
            public void run() {
                double distance = 0.0;
                if (odometer != null) {
                    distance = odometer.getMiles();//if we’ve got a reference to the OdometerService, use its getMiles() method.
                }
                String distanceStr = String.format("%1$,.2f miles", distance);//Format the miles.
                distanceView.setText(distanceStr);
                handler.postDelayed(this, 1000);//Post the code in the Runnable to be run again after a delay of 1,000 milliseconds

            }
        });
    }
}
