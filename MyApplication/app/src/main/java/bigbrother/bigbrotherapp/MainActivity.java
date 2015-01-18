package bigbrother.bigbrotherapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private Button button;
    private EditText name;
    private EditText frequency;
    private EditText pin;
    private EditText pin_check;
    private Button submit_button;

    private Person person;

    private Pinger pinger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        button = (Button) findViewById(R.id.button_test);
        submit_button = (Button) findViewById(R.id.btnLogin);
        pin = (EditText) findViewById(R.id.pin_text);
        pin_check = (EditText) findViewById(R.id.pin_check_text);
        name = (EditText) findViewById(R.id.name_text);
        frequency = (EditText) findViewById(R.id.frequency_text);

        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               new Relax().execute(pinger.getPing());

            }
        });

        final Activity mactivity = this;

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pin.length() != 4) {
                    pin.setError("Pin should be 4 numbers");
                    pin.requestFocus();
                }
                else if (!pin.getText().toString().equals(pin_check.getText().toString())){
                        pin_check.setError("Pin does not match");
                        pin.requestFocus();
                        pin_check.requestFocus();
                }else{
                    String[] flname = name.getText().toString().split(" ", 2);
                    String fname, lname;

                    if (flname.length == 2) {
                        fname = flname[0];
                        lname = flname[1];
                    } else {
                        fname = flname[0];
                        lname = "";
                    }

                    person = new Person(fname,
                            lname,
                            Integer.parseInt(frequency.getText().toString()) * 60,
                            Integer.parseInt(pin.getText().toString()));
                    // create pinger
                    pinger = new Pinger(person);

                    // check-in with server
                    person.sendToServer();

                    // start new thread
                    Thread timer = new Thread() {
                        public void run() {
                            for (;;) {
                                try {
                                    Thread.sleep(person.getCheck_freq() * 1000);
                                    Intent intent = new Intent(mactivity, EnterPinActivity.class);
                                    System.out.println("Intent?");
                                    startActivity(intent);
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                    };
                    timer.start();

                 }


        }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
