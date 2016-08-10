package com.example.kush.scrolltabs;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


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
        /*if (id == R.id.receiver) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        TextView packetstatus;
        TextView receivedstatus,stats;
        private int packetcount = 0;
        EditText mIpAddress, mPort;
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                //View rootView = inflater.inflate(R.layout.receiver, container, false);
                final RelativeLayout rootView = (RelativeLayout)inflater.inflate(R.layout.receiver, null);
                Button submit;

                packetstatus= (TextView) rootView.findViewById(R.id.packetreceive);
                receivedstatus= (TextView) rootView.findViewById(R.id.textView3);
                submit= (Button) rootView.findViewById(R.id.button);

                submit.setOnClickListener(new View.OnClickListener() {


                                              @Override
                                              public void onClick(View view) {
                                                    Context c= rootView.getContext();
                                                    takeWifi(c,true);
                                                      mIpAddress = (EditText) rootView.findViewById(R.id.editText);
                                                      mPort = (EditText) rootView.findViewById(R.id.editText2);
                                                      String mip = mIpAddress.getText().toString();
                                                      String port = mPort.getText().toString();
                                                  InetAddress group = null;
                                                  MulticastSocket socket = null;

                                                  try {
                                                      group= InetAddress.getByName(mip);
                                                      socket= new MulticastSocket(Integer.parseInt(port));
                                                      socket.joinGroup(group);
                                                      while(true) {
                                                          byte[] buf = new byte[1000];
                                                          DatagramPacket recv = new DatagramPacket(buf, buf.length);
                                                          Log.d("VIVZ", "Datagram packet created");
                                                          socket.receive(recv);
                                                          Log.d("VIVZ", "It was recvd");
                                                          String received = new String(recv.getData(), 0, recv.getLength());
                                                          if (received == "STOP") {
                                                              updatePC();
                                                              updateUI(received);
                                                              break;

                                                          }

                                                          //Initially just for simplicity
                                                          updatePC();
                                                          updateUI(received);
                                                      }
                                                      takeWifi(c,false);
                                                  }
                                                  catch (Exception e) {
                                                      e.printStackTrace();
                                                      Log.d("VIVZ", String.valueOf(e));
                                                  }


                                              }
                                          }
                );
                return rootView;
            }
            else{
                final RelativeLayout rootView = (RelativeLayout)inflater.inflate(R.layout.sender, null);
                Button packet50, packet100,packet500;
                packet50= (Button) rootView.findViewById(R.id.packet50);
                packet100= (Button) rootView.findViewById(R.id.packet100);
                packet500= (Button) rootView.findViewById(R.id.packet500);
                stats = (TextView) rootView.findViewById(R.id.status);

                packet50.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(rootView.getContext(),"packet50 has been clicked", LENGTH_SHORT).show();
                        Context c= rootView.getContext();
                        takeWifi(c,true);
                        try {
                            InetAddress group = InetAddress.getByName("228.5.6.7");
                            MulticastSocket s = new MulticastSocket(6789);
                            s.joinGroup(group);
                            int count = 0;
                            String stem = "BITS Test Packet- ";
                            String msg;
                            Log.d("Kush","Before while loop");
                            while (true) {
                                //Scanner st= new Scanner(System.in);
                                //msg= st.nextLine();
                                count += 1;
                                msg = stem + count;
                                if (count == 50)
                                    msg = "STOP";
                                DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, 6789);
                                s.send(hi);

                                //Toast.makeText(rootView.getContext(),"Sent: "+ msg, LENGTH_SHORT).show();

                                stats.setText("Sending: "+ msg);
                                Log.d("Kush","Sending" + msg);
                                if (count == 50) {
                                    s.leaveGroup(group);
                                    break;
                                }
                                Thread.sleep(1000);
                            }
                        }
                        catch(Exception e)
                        {
                            System.out.println(e);
                        }

                        takeWifi(c,false);
                    }
                });
                packet100.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(rootView.getContext(),"packet50 has been clicked", LENGTH_SHORT).show();
                        Context c= rootView.getContext();
                        takeWifi(c,true);
                        try {
                            InetAddress group = InetAddress.getByName("228.5.6.7");
                            MulticastSocket s = new MulticastSocket(6789);
                            s.joinGroup(group);
                            int count = 0;
                            String stem = "BITS Test Packet- ";
                            String msg;
                            while (true) {
                                //Scanner st= new Scanner(System.in);
                                //msg= st.nextLine();
                                count += 1;
                                msg = stem + count;
                                if (count == 100)
                                    msg = "STOP";
                                DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, 6789);
                                s.send(hi);
                                Log.d("Kush","Sending" + msg);
                                if (count == 100) {
                                    s.leaveGroup(group);
                                    break;
                                }
                                Thread.sleep(500);
                            }
                        }
                        catch(Exception e)
                        {
                            System.out.println(e);
                        }

                        takeWifi(c,false);
                    }
                });
                packet500.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(rootView.getContext(),"packet50 has been clicked", LENGTH_SHORT).show();
                        Context c= rootView.getContext();
                        takeWifi(c,true);
                        try {
                            InetAddress group = InetAddress.getByName("228.5.6.7");
                            MulticastSocket s = new MulticastSocket(6789);
                            s.joinGroup(group);
                            int count = 0;
                            String stem = "BITS Test Packet- ";
                            String msg;
                            while (true) {
                                //Scanner st= new Scanner(System.in);
                                //msg= st.nextLine();
                                count += 1;
                                msg = stem + count;
                                if (count == 500)
                                    msg = "STOP";
                                DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, 6789);
                                s.send(hi);
                                Log.d("Kush","Sending" + msg);
                                if (count == 500) {
                                    s.leaveGroup(group);
                                    break;
                                }
                                Thread.sleep(100);
                            }
                        }
                        catch(Exception e)
                        {
                            System.out.println(e);
                        }

                        takeWifi(c,false);
                    }
                });

                return rootView;
            }
        }
        public void updateUI(String recvd) {

            packetstatus.setText("Packet Received: " + packetcount);
            receivedstatus.setText("Received "+ recvd);
            if(recvd=="STOP")
                packetstatus.setText("Tune out. Final Packet Received: " + packetcount);
        }

        public void updatePC() {
            packetcount += 1;
        }

        public void takeWifi(Context c,Boolean what)
        {
            WifiManager wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
            WifiManager.MulticastLock lock = null;
            if (wifi != null) {
                if(what)
                { lock = wifi.createMulticastLock("Log_Tag");
                lock.acquire();}
                else
                {
                    lock.release();
                }
            }
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "receiver";
                case 1:
                    return "sender";

            }
            return null;
        }
    }
}
